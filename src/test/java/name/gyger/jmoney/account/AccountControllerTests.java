package name.gyger.jmoney.account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(put("/rest/options/init"));
    }

    @Test
    public void testGetEmptyAccounts() throws Exception {
        mockMvc.perform(get("/rest/accounts"))
                .andExpect(content().json("[]"))
                .andExpect(status().isOk());
    }

    @Test
    public void testBasics() throws Exception {
        MvcResult result = mockMvc.perform(post("/rest/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String accountId = result.getResponse().getContentAsString();

        mockMvc.perform(get("/rest/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'name':'Account 1'}]"));

        mockMvc.perform(put("/rest/accounts/" + accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1b\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(content().json("{'name':'Account 1b'}"));

        mockMvc.perform(delete("/rest/accounts/" + accountId))
                .andExpect(status().isOk());

        testGetEmptyAccounts();
    }


    @Test
    public void testAccountWithEntries() throws Exception {
        String accountId = mockMvc.perform(post("/rest/accounts")
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
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/rest/accounts/" + accountId))
                .andExpect(status().isOk())
                .andExpect(content().json("{'name':'Account 1','entries':null}"));

        String entry = mockMvc.perform(get("/rest/accounts/" + accountId + "/entries"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(entry);
    }
}
