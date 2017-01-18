package name.gyger.jmoney.options

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
@WebAppConfiguration
open class OptionsControllerTests {

    @Autowired
    lateinit private var context: WebApplicationContext

    lateinit private var mockMvc: MockMvc

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    @Throws(Exception::class)
    fun testImport() {
        val cpr = ClassPathResource("options-service-tests.xml.gz")
        val `is` = cpr.inputStream
        val multipartFile = MockMultipartFile("file", `is`)
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/rest/options/import")
                .file(multipartFile))
                .andExpect(status().isOk)
    }

}
