package name.gyger.jmoney.report

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@RunWith(SpringRunner::class)
@SpringBootTest
@WebAppConfiguration
open class ReportControllerTests {

    @Autowired
    lateinit private var context: WebApplicationContext

    lateinit private var mockMvc: MockMvc

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
        mockMvc.perform(put("/rest/options/init"))
    }

    @Test
    @Throws(Exception::class)
    fun testGetBalances() {
        mockMvc.perform(get("/rest/reports/balances"))
                .andExpect(content().json("[{'accountName':'Total','balance':0,'total':true}]"))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testGetCashFlows() {
        mockMvc.perform(get("/rest/reports/cash-flows")
                .param("fromDate", "2016-12-01")
                .param("toDate", "2016-12-31"))
                .andExpect(content().json("[{'categoryId':null,'categoryName':'Total','income':0,'expense':0,'difference':0,'total':true}]"))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testGetInconsistentSplitEntries() {
        mockMvc.perform(get("/rest/reports/consitency/inconsistent-split-entries"))
                .andExpect(content().json("[]"))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testGetEntriesWithoutCategory() {
        mockMvc.perform(get("/rest/reports/consitency/entries-without-category"))
                .andExpect(content().json("[]"))
                .andExpect(status().isOk)
    }

    @Test
    @Throws(Exception::class)
    fun testGetEntriesWithCategory() {
        mockMvc.perform(get("/rest/reports/entries-with-category")
                .param("categoryId", "0")
                .param("fromDate", "2016-12-01")
                .param("toDate", "2016-12-31"))
                .andExpect(content().json("[]"))
                .andExpect(status().isOk)
    }

}
