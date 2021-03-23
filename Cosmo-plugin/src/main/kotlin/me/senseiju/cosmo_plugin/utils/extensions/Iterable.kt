package me.senseiju.sennetmc.utils.extensions

inline fun <T> Iterable<T>.sumByDouble(selector: (T) -> Double): Double {
    return map { selector(it) }.sum()
}