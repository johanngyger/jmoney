package name.gyger.jmoney.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DateUtilTests {

    @Test
    public void testConstructor() {
        new DateUtil();
    }

    @Test
    public void testParsing() {
        assertThat(DateUtilKt.parse("2005-05-14")).isNotNull();
    }

    @Test
    public void testParseNull() {
        assertThat(DateUtilKt.parse(null)).isNull();
    }

    @Test
    public void testInvalidString() {
        assertThatThrownBy(() -> DateUtilKt.parse("INVALID STRING"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
