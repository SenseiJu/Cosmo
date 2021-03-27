import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.cosmo_pack_builder.json_templates.ItemJsonTemplate
import org.junit.Test
import java.io.File

class Tests {

    @Test
    fun testItemJsonTemplate() {
        val item = Json.decodeFromString<ItemJsonTemplate>(
            File("D:\\Intellij Projects\\Cosmo\\Cosmo-pack-builder\\src\\test\\resources\\models\\hat\\1\\1.json").readText()
        )
    }
}