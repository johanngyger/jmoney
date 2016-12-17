package name.gyger.jmoney.options;

import name.gyger.jmoney.session.SessionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OptionsController.class)
public class OptionsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OptionsService optionsService;

    @MockBean
    private SessionService sessionService;

    @Test
    public void testInit() throws Exception {
        mockMvc.perform(put("/rest/options/init"))
                .andExpect(status().isOk());
    }


}
