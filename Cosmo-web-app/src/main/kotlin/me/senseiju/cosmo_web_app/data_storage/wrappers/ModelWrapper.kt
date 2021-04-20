package me.senseiju.cosmo_web_app.data_storage.wrappers

import kotlinx.serialization.Serializable
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.MODELS_PATH
import me.senseiju.cosmo_web_app.discord_api.requests.getDiscordUserById
import java.io.File
import javax.sql.rowset.CachedRowSet

@Serializable
data class ModelWrapper(
    val modelData: Int,
    val modelType: ModelType,
    val name: String? = null,
    val author: String? = null
)

/**
 * Turn query results into [ModelWrapper]
 *
 * @param results the result set
 *
 * @return a collection of [ModelWrapper]
 */
fun wrapModelsFromResults(results: CachedRowSet): Collection<ModelWrapper> {
    val models = arrayListOf<ModelWrapper>()
    while (results.next()) {
        models.add(
            ModelWrapper(
                modelData = results.getInt("model_data"),
                modelType = ModelType.parse(results.getString("model_type")) ?: continue,
                name = results.getString("name"),
                author = getDiscordUserById(results.getString("user_id")).username
            )
        )
    }

    return models
}

fun getImageURLForModel(model: ModelWrapper): String {
    val imgPath = "${model.modelType.toString().toLowerCase()}/${model.modelData}/${model.modelData}.png"
    val imgFile = File(MODELS_PATH, imgPath)
    if (imgFile.exists()) {
        return "/models/$imgPath"
    }

    return "/img/no-image-available.png"
}