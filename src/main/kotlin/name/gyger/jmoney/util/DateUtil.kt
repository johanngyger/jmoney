package name.gyger.jmoney.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil

private val sdf = SimpleDateFormat("yyyy-MM-dd")

fun parse(dateString: String?): Date? {
    if (dateString == null) return null

    val result: Date?
    try {
        result = sdf.parse(dateString)
    } catch (e: ParseException) {
        throw IllegalArgumentException(dateString, e)
    }

    return result
}

