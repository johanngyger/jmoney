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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.*;
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

    @Test
    public void testSplitEntry() throws Exception {
        String accountId = mockMvc.perform(post("/rest/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Account 1\"}"))
                .andReturn().getResponse().getContentAsString();

        String splitCatJson = mockMvc.perform(get("/rest/split-category"))
                .andReturn().getResponse().getContentAsString();
        JSONObject splitCat = new JSONObject(splitCatJson);

        JSONObject entry = new JSONObject();
        entry.put("categoryId", splitCat.get("id"));
        String entryId = mockMvc.perform(post("/rest/accounts/{accountId}/entries", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andReturn().getResponse().getContentAsString();

        entry.put("id", entryId);
        JSONArray splitEntries = new JSONArray();
        splitEntries.put(new JSONObject("{\"description\": \"s1\"}"));
        entry.put("subEntries", splitEntries);
        mockMvc.perform(put("/rest/accounts/{accountId}/entries/{entryId}", accountId, entryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/accounts/{accountId}/entries/{entryId}", accountId, entryId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("s1")));

        splitEntries.put(new JSONObject("{\"description\": \"s2\"}"));
        mockMvc.perform(put("/rest/accounts/{accountId}/entries/{entryId}", accountId, entryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rest/accounts/{accountId}/entries/{entryId}", accountId, entryId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("s1")))
                .andExpect(content().string(containsString("s2")));

        splitEntries.remove(0);
        mockMvc.perform(put("/rest/accounts/{accountId}/entries/{entryId}", accountId, entryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(entry.toString()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/rest/accounts/{accountId}/entries/{entryId}", accountId, entryId))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("s1"))))
                .andExpect(content().string(containsString("s2")));
    }
}