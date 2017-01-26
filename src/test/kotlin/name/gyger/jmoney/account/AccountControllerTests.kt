package name.gyger.jmoney.account

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
@WebAppConfiguration
open class AccountControllerTests {

    @Autowired
    lateinit private var context: WebApplicationContext

    lateinit private var mockMvc: MockMvc

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        mockMvc.perform(put("/api/options/init"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetEmptyAccounts() {
        mockMvc.perform(get("/api/accounts"))
                .andExpect(content().json("[]"))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testBasics() {
        val result = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1\"}"))
                .andExpect(status().isOk)
                .andReturn()
        val accountId = result.response.contentAsString

        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk)
                .andExpect(content().json("[{'name':'Account 1'}]"))

        mockMvc.perform(put("/api/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1b\"}"))
                .andExpect(status().isOk)

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk)
                .andExpect(content().json("{'name':'Account 1b'}"))

        mockMvc.perform(delete("/api/accounts/" + accountId))
                .andExpect(status().isOk)

        testGetEmptyAccounts()
    }


    @Test
    @Throws(Exception::class)
    fun testAccountWithEntries() {
        val accountId = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"name\": \"Account 1\",\n" +
                        "  \"entries\": [\n" +
                        "    {\n" +
                        "      \"date\": \"2001-17-19\",\n" +
                        "      \"description\": \"Entry 1\",\n" +
                        "      \"expense\": 10\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        mockMvc.perform(get("/api/accounts/" + accountId))
                .andExpect(status().isOk)
                .andExpect(content().json("{'name':'Account 1'}"))

        val entry = mockMvc.perform(get("/api/accounts/$accountId/entries"))
                .andExpect(status().isOk)
                .andExpect(content().json("[]"))
                .andReturn().response.contentAsString
        println(entry)
    }
}
