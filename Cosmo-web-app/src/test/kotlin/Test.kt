import kotlinx.coroutines.runBlocking
import me.senseiju.cosmo_commons.ModelType
import me.senseiju.cosmo_web_app.data_storage.selectLastModels
import me.senseiju.cosmo_web_app.data_storage.wrappers.ModelWrapper
import me.senseiju.cosmo_web_app.data_storage.wrappers.getImageURLForModel
import me.senseiju.cosmo_web_app.utils.FileType
import me.senseiju.cosmo_web_app.utils.isFileOfType
import org.junit.Test
import java.io.File
import java.net.URLConnection

class Test {

    @Test
    fun selectNModels() {
        runBlocking {
            selectLastModels().forEach {
                println(it)
            }
        }
    }

    @Test
    fun testing() {
        val file = File("/cosmo/models/hat/1/textures/test.jpg")

        println(isFileOfType(file, FileType.PNG))
    }

    @Test
    fun getImage() {
        println(getImageURLForModel(ModelWrapper(1, ModelType.HAT)))
    }
}