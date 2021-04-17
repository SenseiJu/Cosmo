package me.senseiju.cosmo_web_app.data_storage.wrappers

import java.util.*
import javax.sql.rowset.CachedRowSet

data class PackWrapper(
    val packId: UUID,
    val userId: String,
    val name: String
)

/**
 * Turn query results into [PackWrapper]
 *
 * @param results the result set
 *
 * @return a collection of [PackWrapper]
 */
fun wrapPacksFromResults(results: CachedRowSet): Collection<PackWrapper> {
    val packs = arrayListOf<PackWrapper>()
    while (results.next()) {
        packs.add(
            PackWrapper(
                packId = UUID.fromString(results.getString("pack_id")),
                userId = results.getString("user_id"),
                name = results.getString("name")
            )
        )
    }

    return packs
}