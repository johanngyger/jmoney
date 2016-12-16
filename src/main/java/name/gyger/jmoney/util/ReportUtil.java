package name.gyger.jmoney.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportUtil {

    public static Map<Long, Long> mapResult(List queryResultList) {
        Map<Long, Long> result = new HashMap<Long, Long>();

        for (Object resultItem : queryResultList) {
            Object[] resultItemArray = (Object[]) resultItem;
            Long accountId = (Long) resultItemArray[0];
            Long sum = (Long) resultItemArray[1];
            result.put(accountId, sum);
        }

        return result;
    }
}
