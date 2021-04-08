import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.pack_builder.PackBuilder
import org.junit.Test
import java.util.*

class Test {

    @Test
    fun buildPack() {
        PackBuilder().build(
            UUID.randomUUID(),
            ModelWrapper(1, ModelType.HAT, "Viking", "SenseiJu"),
            ModelWrapper(2, ModelType.HAT, "Scuba", "Rito"),
        )
    }
}