package name.gyger.jmoney.session

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest
@Transactional
open class SessionServiceTests {

    @Autowired
    lateinit var sessionService: SessionService

    @Test
    fun testBasics() {
        sessionService.initSession()
        assertThat(sessionService.isSessionAvailable()).isTrue()

        sessionService.handleContextRefresh(null)
        assertThat(sessionService.isSessionAvailable()).isTrue()

        val s = sessionService.getSession()
        assertThat(s).isNotNull()
        assertThat(s.rootCategory).isNotNull()
        assertThat(s.splitCategory).isNotNull()
        assertThat(s.transferCategory).isNotNull()
        assertThat(s.id).isNotNull()

        sessionService.removeOldSession()
        assertThat(sessionService.isSessionAvailable()).isFalse()
    }

}
