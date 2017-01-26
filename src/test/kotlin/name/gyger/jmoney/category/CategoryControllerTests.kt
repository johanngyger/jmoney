package name.gyger.jmoney.category

import org.hamcrest.Matchers.*
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
@WebAppConfiguration
open class CategoryControllerTests {

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
    fun testGetCategories() {
        mockMvc.perform(get("/api/categories"))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testGetSplitCategory() {
        mockMvc.perform(get("/api/split-category"))
                .andExpect(content().json("{'name':'[SPLIT]'}"))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testGetRootCategory() {
        mockMvc.perform(get("/api/root-category"))
                .andExpect(content().json("{'name':'[ROOT]'}"))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testCreateAndDeleteCategory() {
        val rootCat = mockMvc.perform(get("/api/root-category"))
                .andReturn().response.contentAsString
        val rootCatJson = JSONObject(rootCat)
        val rootCatId = "" + rootCatJson.getInt("id")

        val categoryId = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"My fancy new category\",\"parentId\":\"$rootCatId\"}"))
                .andExpect(status().isOk)
                .andReturn().response.contentAsString

        mockMvc.perform(get("/api/categories"))
                .andExpect(content().string(containsString("My fancy new category")))
                .andExpect(status().isOk)

        mockMvc.perform(delete("/api/categories/" + categoryId))
                .andExpect(status().isOk)

        mockMvc.perform(get("/api/categories"))
                .andExpect(content().string(not(containsString("My fancy new category"))))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testCategoryTree() {
        val categoryTree = mockMvc.perform(get("/api/category-tree"))
                .andExpect(content().json("{'name':'[ROOT]'}"))
                .andExpect(status().isOk)
                .andExpect(content().string(not(containsString("SPLITTBUCHUNG"))))
                .andExpect(content().string(not(containsString("UMBUCHUNG"))))
                .andReturn().response.contentAsString

        mockMvc.perform(put("/api/category-tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryTree))
                .andExpect(status().isOk)

        mockMvc.perform(get("/api/category-tree"))
                .andExpect(content().string(equalTo(categoryTree)))
    }

}
