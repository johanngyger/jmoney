package name.gyger.jmoney

import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.springframework.boot.SpringApplication

@RunWith(PowerMockRunner::class)
@PrepareForTest(SpringApplication::class)
class JMoneyApplicationTests {

    @Test
    fun testAppClass() {
        JMoneyApplication()
    }

    @Test
    fun testMain() {
        PowerMockito.mockStatic(SpringApplication::class.java)
        main(arrayOf<String>())
    }

}
