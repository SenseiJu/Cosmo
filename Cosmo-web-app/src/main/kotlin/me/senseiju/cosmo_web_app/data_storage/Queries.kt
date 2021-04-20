package me.senseiju.cosmo_web_app.data_storage

import kotlinx.coroutines.launch
import me.senseiju.cosmo_web_app.data_storage.wrappers.*
import me.senseiju.cosmo_web_app.utils.defaultScope
import me.senseiju.cosmo_web_app.pack_builder.buildPack
import me.senseiju.cosmo_web_app.pack_builder.deletePackFiles
import java.util.*

private val db = Database()

/**
 * Adds a new model to the resource pack
 *
 * @param packId the pack id
 * @param modelWrapper the model data and type
 */
fun insertModelToPack(packId: UUID, modelWrapper: ModelWrapper) {
    defaultScope.launch {
        val query = "INSERT IGNORE INTO `${Table.RESOURCE_PACK_MODELS}`(`pack_id`, `model_data`, `model_type`) VALUES(?,?,?);"

        db.asyncUpdateQuery(query, packId.toString(), modelWrapper.modelData, modelWrapper.modelType.toString())

        buildPack(packId)
    }
}

/**
 * Remove models from the resource pack
 *
 * @param packId the pack id
 * @param modelWrappers the model data and type
 */
fun deleteModelsFromPack(packId: UUID, vararg modelWrappers: ModelWrapper) {
    defaultScope.launch {
        val query = "DELETE FROM `${Table.RESOURCE_PACK_MODELS}` WHERE `pack_id`=? AND `model_data`=? AND `model_type`=?;"
        val replacements = modelWrappers.map {
            Replacement(packId.toString(), it.modelData, it.modelType.toString())
        }.toTypedArray()

        db.updateBatchQuery(query, *replacements)

        buildPack(packId)
    }
}

/**
 * Delete a resource pack
 *
 * @param packId the pack id
 */
fun deletePack(packId: UUID) {
    defaultScope.launch {
        val query = "DELETE FROM `${Table.RESOURCE_PACKS}` WHERE `pack_id`=?;"
        val query2 = "DELETE FROM `${Table.RESOURCE_PACK_MODELS}` WHERE `pack_id`=?;"

        db.asyncUpdateQuery(query, packId.toString())
        db.asyncUpdateQuery(query2, packId.toString())

        deletePackFiles(packId)
    }
}

/**
 * Create a new model
 *
 * @param modelWrapper the model data and type
 * @param name the model name
 * @param userId the user id
 */
fun insertModel(modelWrapper: ModelWrapper, name: String, userId: String) {
    defaultScope.launch {
        val query = "INSERT INTO `${Table.MODELS}`(`model_data`, `model_type`, `name`, `user_id`) VALUES(?,?,?,?);"

        db.asyncUpdateQuery(query, modelWrapper.modelData, modelWrapper.modelType.toString(), name, userId)
    }
}

/**
 * Selects all models from the resource pack
 *
 * @param packId the pack id
 *
 * @return the models in the pack
 */
suspend fun selectModelsFromPackJoinedWithModels(packId: UUID): Collection<ModelWrapper> {
    val query = "SELECT `${Table.RESOURCE_PACK_MODELS}`.`model_data`, `${Table.RESOURCE_PACK_MODELS}`.`model_type`, `${Table.MODELS}`.`name`, `${Table.MODELS}`.`user_id` " +
            "FROM `${Table.RESOURCE_PACK_MODELS}` " +
            "LEFT JOIN `${Table.MODELS}` ON `${Table.MODELS}`.`model_data` = `${Table.RESOURCE_PACK_MODELS}`.`model_data` " +
            "AND `${Table.MODELS}`.`model_type` = `${Table.RESOURCE_PACK_MODELS}`.`model_type` " +
            "WHERE `${Table.RESOURCE_PACK_MODELS}`.`pack_id`=?;"
    val results = db.asyncQuery(query, packId.toString())

    return wrapModelsFromResults(results)
}

/**
 * Selects the name of a model
 *
 * @param packId the pack id
 *
 * @return the name or invalid string
 */
suspend fun selectPackName(packId: UUID): String {
    val query = "SELECT `name` FROM `${Table.RESOURCE_PACKS}` WHERE `pack_id`=?;"
    val results = db.asyncQuery(query, packId.toString())
    if (results.next()) {
        val name = results.getString("name")
        if (name.isBlank()) {
            return "{{ INVALID PACK NAME }}"
        }
        return results.getString("name")
    }
    return "{{ INVALID PACK NAME }}"
}

/**
 * Checks if user owns a pack
 *
 * @param packId the pack id
 * @param userId the user id
 *
 * @return if owned
 */
suspend fun isUserPackOwner(packId: UUID, userId: String): Boolean {
    val query = "SELECT `user_id` FROM `${Table.RESOURCE_PACKS}` WHERE `pack_id`=?;"
    val results = db.asyncQuery(query, packId.toString())

    if (!results.next()) {
        return false
    }

    return results.getString("user_id").equals(userId)
}

/**
 * Create a new pack
 *
 * @param name the pack name
 * @param userId the user id
 *
 * @return the pack id
 */
fun insertPack(name: String, userId: String): UUID {
    val packId = UUID.randomUUID()

    defaultScope.launch {
        val query = "INSERT INTO `${Table.RESOURCE_PACKS}`(`pack_id`, `user_id`, `name`) VALUES(?,?,?);"

        db.asyncUpdateQuery(query, packId.toString(), userId, name)
    }

    return packId
}

/**
 * Used if supplied [ModelWrapper] only contains the `model_data` and `model_type`
 *
 * @param modelWrapper the model
 *
 * @return a new [ModelWrapper] with model's name and author
 */
suspend fun selectModels(vararg modelWrapper: ModelWrapper): Collection<ModelWrapper> {
    val predicate = List(modelWrapper.size) {
        "(`model_data`=? AND `model_type`=?)"
    }.joinToString(" OR ")

    val query = "SELECT * FROM `${Table.MODELS}` WHERE $predicate;"
    val results = db.asyncQuery(
        query,
        *(modelWrapper.flatMap {
            listOf(it.modelData, it.modelType.toString())
        }.toTypedArray())
    )

    return wrapModelsFromResults(results)
}

/**
 * Select last models sorted by `model_data`
 *
 * @param n the number of rows
 *
 * @return a collection of [ModelWrapper]
 */
suspend fun selectLastModels(n: Int = 20): Collection<ModelWrapper> {
    val query = "SELECT * FROM `${Table.MODELS}` ORDER BY `model_data` DESC LIMIT ?;"
    val results = db.asyncQuery(query, n)

    return wrapModelsFromResults(results)
}

/**
 * Select all pack ids for a user
 *
 * @param userId the user id
 *
 * @return a collection of pack ids
 */
suspend fun selectPacks(userId: String): Collection<PackWrapper> {
    val query = "SELECT * FROM `${Table.RESOURCE_PACKS}` WHERE `user_id`=?;"
    val results = db.asyncQuery(query, userId)

    return wrapPacksFromResults(results)
}