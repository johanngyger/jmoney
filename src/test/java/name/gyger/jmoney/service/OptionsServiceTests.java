package name.gyger.jmoney.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OptionsServiceTests {

    @Autowired
    OptionsService optionsService;

    @Test
    public void testImport() throws IOException {
        ClassPathResource cpr = new ClassPathResource("options-service-tests.xml.gz");
        InputStream is = cpr.getInputStream();
        optionsService.importFile(is);
    }

}
