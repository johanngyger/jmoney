package name.gyger.jmoney.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parse(String dateString) {
        if (dateString == null) return null;

        Date result = null;
        try {
            result = sdf.parse(dateString);
        } catch (ParseException e) {
            throw new IllegalArgumentException(dateString, e);
        }
        return result;
    }

}
