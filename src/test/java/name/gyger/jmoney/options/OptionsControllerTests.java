package name.gyger.jmoney.options;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class OptionsControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testImport() throws Exception {
        ClassPathResource cpr = new ClassPathResource("options-service-tests.xml.gz");
        InputStream is = cpr.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("file", is);
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/rest/options/import")
                .file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/options.html#/options/import?success"));
    }

    @Test
    public void testImportWithInvalidInput() throws Exception {
        ClassPathResource cpr = new ClassPathResource("lorem_ipsum.txt");
        InputStream is = cpr.getInputStream();
        MockMultipartFile multipartFile = new MockMultipartFile("file", is);
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/rest/options/import")
                .file(multipartFile))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/options.html#/options/import?error"));
    }

}
