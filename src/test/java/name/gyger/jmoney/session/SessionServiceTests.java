package name.gyger.jmoney.session;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class SessionServiceTests {

    @Autowired
    SessionService sessionService;

    @Test
    public void testBasics() {
        sessionService.initSession();
        assertThat(sessionService.isSessionAvailable()).isTrue();

        sessionService.handleContextRefresh(null);
        assertThat(sessionService.isSessionAvailable()).isTrue();

        Session s = sessionService.getSession();
        assertThat(s).isNotNull();
        assertThat(s.getRootCategory()).isNotNull();
        assertThat(s.getSplitCategory()).isNotNull();
        assertThat(s.getTransferCategory()).isNotNull();
        assertThat(s.getId()).isNotNull();

        sessionService.removeOldSession();
        assertThat(sessionService.isSessionAvailable()).isFalse();
    }

}
