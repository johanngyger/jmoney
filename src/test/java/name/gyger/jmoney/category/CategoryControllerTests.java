package name.gyger.jmoney.category;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class CategoryControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc.perform(put("/rest/options/init"));
    }

    @Test
    public void testGetCategories() throws Exception {
        mockMvc.perform(get("/rest/categories"))
                .andExpect(content().string(not(isEmptyString())))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSplitCategory() throws Exception {
        mockMvc.perform(get("/rest/split-category"))
                .andExpect(content().json("{'name':'[SPLITTBUCHUNG]'}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRootCategory() throws Exception {
        mockMvc.perform(get("/rest/root-category"))
                .andExpect(content().json("{'name':'[ROOT]'}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateAndDeleteCategory() throws Exception {
        String rootCat = mockMvc.perform(get("/rest/root-category"))
                .andReturn().getResponse().getContentAsString();
        JSONObject rootCatJson = new JSONObject(rootCat);
        String rootCatId = "" + rootCatJson.getInt("id");

        String categoryId = mockMvc.perform(post("/rest/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"My fancy new category\",\"parentId\":\"" + rootCatId + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/rest/categories"))
                .andExpect(content().string(containsString("My fancy new category")))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/rest/categories/" + categoryId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rest/categories"))
                .andExpect(content().string(not(containsString("My fancy new category"))))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCategoryTree() throws Exception {
        String categoryTree = mockMvc.perform(get("/rest/category-tree"))
                .andExpect(content().json("{'name':'[ROOT]'}"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("SPLITTBUCHUNG"))))
                .andExpect(content().string(not(containsString("UMBUCHUNG"))))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(put("/rest/category-tree")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryTree))
                .andExpect(status().isOk());
    }

}
