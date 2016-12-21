package name.gyger.jmoney.account;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class EntryControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(put("/rest/options/init"));
    }

    @Test
    public void testEntries() throws Exception {
        String accountId = mockMvc.perform(post("/rest/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(post("/rest/accounts/{accountId}/entries", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2001-17-19\",\"description\":\"My entry\",\"expense\":10}"));

        String entriesStr = mockMvc.perform(get("/rest/accounts/{accountId}/entries", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].description", equalTo("My entry")))
                .andReturn().getResponse().getContentAsString();
        JSONArray entries = new JSONArray(entriesStr);
        JSONObject entry = (JSONObject) entries.get(0);

        mockMvc.perform(get("/rest/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", equalTo("My entry")));

        mockMvc.perform(get("/rest/accounts/{accountId}/entries/count", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        entry.put("description", "My fancy entry");
        mockMvc.perform(put("/rest/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", equalTo("My fancy entry")));

        mockMvc.perform(delete("/rest/accounts/{accountId}/entries/{entryId}", accountId, entry.get("id")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/accounts/{accountId}/entries/count", accountId))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }


}
