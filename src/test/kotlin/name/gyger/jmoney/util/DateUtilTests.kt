package name.gyger.jmoney.util

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

class DateUtilTests {

    @Test
    fun testConstructor() {
        DateUtil()
    }

    @Test
    fun testParsing() {
        assertThat(parse("2005-05-14")).isNotNull()
    }

    @Test
    fun testParseNull() {
        assertThat(parse(null)).isNull()
    }

    @Test
    fun testInvalidString() {
        assertThatThrownBy { parse("INVALID STRING") }.isInstanceOf(IllegalArgumentException::class.java)
    }

}
