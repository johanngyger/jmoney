package name.gyger.jmoney.account

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.not
import org.json.JSONArray
import org.json.JSONObject
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
@WebAppConfiguration
class EntryControllerTests {

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
    fun testEntries() {
        val accountId = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1\"}"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        mockMvc.perform(post("/api/accounts/{accountId}/entries", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2001-17-19\",\"description\":\"My entry\",\"expense\":10}"))

        val entriesStr = mockMvc.perform(get("/api/accounts/{accountId}/entries", accountId))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.[0].description", equalTo("My entry")))
                .andReturn().response.contentAsString
        val entries = JSONArray(entriesStr)
        val entry = entries.get(0) as JSONObject

        mockMvc.perform(get("/api/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id")))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.description", equalTo("My entry")))

        mockMvc.perform(get("/api/accounts/{accountId}/entries/count", accountId))
                .andExpect(status().isOk)
                .andExpect(content().string("1"))

        entry.put("description", "My fancy entry")
        mockMvc.perform(put("/api/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk)

        mockMvc.perform(get("/api/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id")))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.description", equalTo("My fancy entry")))

        mockMvc.perform(delete("/api/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id")))
                .andExpect(status().isOk)

        mockMvc.perform(get("/api/accounts/{accountId}/entries/count", accountId))
                .andExpect(status().isOk)
                .andExpect(content().string("0"))
    }

    @Test
    @Throws(Exception::class)
    fun testSplitEntry() {
        val accountId = mockMvc.perform(post("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1\"}"))
                .andReturn().response.contentAsString

        val splitCatJson = mockMvc.perform(get("/api/split-category"))
                .andReturn().response.contentAsString
        val splitCat = JSONObject(splitCatJson)

        val entry = JSONObject()
        entry.put("categoryId", splitCat.get("id"))
        val entryId = mockMvc.perform(post("/api/accounts/{accountId}/entries", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andReturn().response.contentAsString

        entry.put("id", entryId)
        val splitEntries = JSONArray()
        splitEntries.put(JSONObject("{\"description\": \"s1\"}"))
        entry.put("subEntries", splitEntries)
        mockMvc.perform(put("/api/accounts/{accountId}/entries/{entryId}", accountId, entryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk)

        mockMvc.perform(get("/api/accounts/{accountId}/entries/{entryId}", accountId, entryId))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("s1")))

        splitEntries.put(JSONObject("{\"description\": \"s2\"}"))
        mockMvc.perform(put("/api/accounts/{accountId}/entries/{entryId}", accountId, entryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk)
        mockMvc.perform(get("/api/accounts/{accountId}/entries/{entryId}", accountId, entryId))
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("s1")))
                .andExpect(content().string(containsString("s2")))

        splitEntries.remove(0)
        mockMvc.perform(put("/api/accounts/{accountId}/entries/{entryId}", accountId, entryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk)
        mockMvc.perform(get("/api/accounts/{accountId}/entries/{entryId}", accountId, entryId))
                .andExpect(status().isOk)
                .andExpect(content().string(not(containsString("s1"))))
                .andExpect(content().string(containsString("s2")))
    }
}