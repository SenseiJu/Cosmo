package me.senseiju.cosmo_web_app.data_storage

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.senseiju.cosmo_web_app.data_storage.wrappers.Replacement
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import javax.sql.rowset.CachedRowSet
import javax.sql.rowset.RowSetProvider

const val HOST = "cosmo.senseiju.me"
const val PORT = 3306
const val DATABASE = "cosmo"
const val USERNAME = "senseiju"
const val PASSWORD = "senseiju"

class Database {
    private var source: HikariDataSource

    init {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl =
                "jdbc:mysql://$HOST:$PORT/$DATABASE" +
                        "?autoReconnect=true&allowMultiQueries=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false"
        hikariConfig.username = USERNAME
        hikariConfig.password = PASSWORD
        hikariConfig.connectionTimeout = 8000
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")

        source = HikariDataSource(hikariConfig)
    }

    suspend fun asyncQuery(q: String, vararg replacements: Any = emptyArray()): CachedRowSet {
        return withContext(Dispatchers.IO) {
            query(q, *replacements)
        }
    }

    private fun query(q: String, vararg replacements: Any = emptyArray()): CachedRowSet {
        source.connection.use { conn ->
            val s = conn.prepareStatement(q)

            replaceQueryParams(s, *replacements)

            val set = s.executeQuery()

            val cachedSet = RowSetProvider.newFactory().createCachedRowSet()
            cachedSet.populate(set)

            return cachedSet
        }
    }

    suspend fun asyncUpdateQuery(q: String, vararg replacements: Any = emptyArray()) {
        withContext(Dispatchers.IO) {
            updateQuery(q, *replacements)
        }
    }

    private fun updateQuery(q: String, vararg replacements: Any = emptyArray()) {
        source.connection.use { conn ->
            val s = conn.prepareStatement(q)

            replaceQueryParams(s, *replacements)

            try {
                s.executeUpdate()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun updateBatchQuery(q: String, vararg replacements: Replacement = emptyArray()) {
        source.connection.use { conn ->
            conn.autoCommit = false

            val s = conn.prepareStatement(q)

            var i = 1

            replacements.forEach { set ->
                set.replacements.forEach { replacement ->
                    s.setObject(i++, replacement)
                }

                s.addBatch()

                i = 1
            }

            try {
                s.executeBatch()
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }

            conn.commit()
            conn.autoCommit = true
        }
    }

    fun getConnection(): Connection {
        return source.connection
    }

    fun replaceQueryParams(s: PreparedStatement, vararg replacements: Any = emptyArray()) {
        var i = 1

        replacements.forEach { replacement -> s.setObject(i++, replacement) }
    }
}

