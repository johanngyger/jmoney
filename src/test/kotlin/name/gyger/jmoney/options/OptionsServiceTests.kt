package name.gyger.jmoney.options

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit4.SpringRunner
import java.io.IOException
import javax.transaction.Transactional


@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class OptionsServiceTests {

    @Autowired
    lateinit var optionsService: OptionsService

    @Test
    @Throws(IOException::class)
    fun testImport() {
        val cpr = ClassPathResource("options-service-tests.xml.gz")
        optionsService.importFile(cpr.inputStream)
    }

}
