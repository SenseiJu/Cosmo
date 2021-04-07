package me.senseiju.cosmo_web_app.data_storage

import kotlinx.coroutines.launch
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelDataType
import me.senseiju.cosmo_web_app.data_storage.wrappers.Replacement
import me.senseiju.cosmo_web_app.defaultScope
import java.util.*
import javax.sql.rowset.CachedRowSet

private val db = Database()

/**
 * Adds a new model to the resource pack
 *
 * @param packId the pack id
 * @param modelDataType the model data and type
 */
fun insertModelToPack(packId: UUID, modelDataType: ModelDataType) {
    defaultScope.launch {
        val query = "INSERT INTO `${Table.RESOURCE_PACK_MODELS}`(`pack_id`, `model_data`, `model_type`) VALUES(?,?,?);"

        db.asyncUpdateQuery(query, packId.toString(), modelDataType.modelData, modelDataType.modelType.toString())
    }
}

/**
 * Remove models from the resource pack
 *
 * @param packId the pack id
 * @param modelDataTypes the model data and type
 */
fun deleteModelsFromPack(packId: UUID, vararg modelDataTypes: ModelDataType) {
    defaultScope.launch {
        val query = "DELETE FROM `${Table.RESOURCE_PACK_MODELS}` WHERE `pack_id`=? AND `model_data`=? AND `model_type`=?;"
        val replacements = modelDataTypes.map {
            Replacement(packId.toString(), it.modelData, it.modelType.toString())
        }.toTypedArray()

        db.updateBatchQuery(query, *replacements)
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

        db.asyncUpdateQuery(query, packId.toString())
    }
}

/**
 * Create a new model
 *
 * @param modelDataType the model data and type
 * @param name the model name
 * @param userId the user id
 */
fun insertModel(modelDataType: ModelDataType, name: String, userId: String) {
    defaultScope.launch {
        val query = "INSERT INTO `${Table.MODELS}`(`model_data`, `model_type`, `name`, `user_id`) VALUES(?,?,?,?);"

        db.asyncUpdateQuery(query, modelDataType.modelData, modelDataType.modelType.toString(), name, userId)
    }
}

/**
 * Selects all models from the resource pack
 *
 * @param packId the pack id
 *
 * @return the models results set [`model_data` `model_type` `name` `user_id`]
 */
suspend fun selectModelsFromPackJoinedWithModels(packId: UUID): CachedRowSet {
    val query = "SELECT `${Table.RESOURCE_PACK_MODELS}`.`model_data`, `${Table.RESOURCE_PACK_MODELS}`.`model_type`, `${Table.MODELS}`.`name`, `${Table.MODELS}`.`user_id` " +
            "FROM `${Table.RESOURCE_PACK_MODELS}` " +
            "LEFT JOIN `${Table.MODELS}` ON `${Table.MODELS}`.`model_data` = `${Table.RESOURCE_PACK_MODELS}`.`model_data` " +
            "AND `${Table.MODELS}`.`model_type` = `${Table.RESOURCE_PACK_MODELS}`.`model_type` " +
            "WHERE `${Table.RESOURCE_PACK_MODELS}`.`pack_id`=?;"
    return db.asyncQuery(query, packId.toString())
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