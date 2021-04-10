import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.data_storage.selectLastModels
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.defaultScope
import me.senseiju.cosmo_web_app.pack_builder.PackBuilder
import org.junit.Test
import java.util.*

class Test {

    @Test
    fun buildNewPack() {
        PackBuilder().build(
            UUID.randomUUID(),
            ModelWrapper(1, ModelType.HAT, "Viking", "SenseiJu"),
            ModelWrapper(2, ModelType.HAT, "Scuba", "Rito"),
        )
    }

    @Test
    fun buildPack() {
        PackBuilder().build(
            UUID.fromString("ac7b5923-86c5-4db7-887f-417a6c0120e5"),
            ModelWrapper(1, ModelType.HAT, "snowny", "SenseiJu"),
            ModelWrapper(2, ModelType.HAT, "Viking", "SenseiJu")
        )
    }

    @Test
    fun selectNModels() {
        runBlocking {
            selectLastModels().forEach {
                println(it)
            }
        }
    }
}