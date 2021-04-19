package me.senseiju.cosmo_web_app.utils

import kotlin.concurrent.thread

class CachedHashMap<K, V>(
    private val timeToLive: Long,
    private val cleanupPeriod: Long = 5000
) {
    private val map = hashMapOf<K, ValueWrapper>()

    init {
        startCleanUpTask()
    }

    @Synchronized
    fun set(key: K, value: V) {
        map[key] = ValueWrapper(value, System.currentTimeMillis() + timeToLive)
    }

    @Synchronized
    fun get(key: K): V? {
        return map[key]?.value
    }

    @Synchronized
    fun remove(key: K): V? {
        return map.remove(key)?.value
    }

    @Synchronized
    fun contains(key: K): Boolean {
        return map.contains(key)
    }

    private fun startCleanUpTask() {
        thread {
            while (true) {
                Thread.sleep(cleanupPeriod)

                cleanUp()
            }
        }

        Thread {

        }
    }

    private fun cleanUp() {
        map.forEach { (key, value) ->
            if (System.currentTimeMillis() >= value.expiresAt) {
                remove(key)
            }
        }
    }

    private inner class ValueWrapper(val value: V, var expiresAt: Long)
}