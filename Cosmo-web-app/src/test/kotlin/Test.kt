import kotlinx.coroutines.runBlocking
import me.senseiju.cosmo_web_app.data_storage.selectLastModels
import org.junit.Test

class Test {

    @Test
    fun selectNModels() {
        runBlocking {
            selectLastModels().forEach {
                println(it)
            }
        }
    }
}