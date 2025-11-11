package com.loltft.rudefriend.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ConvertDateToDateTime {
    fun convertMap(dateFrom: LocalDate?, dateTo: LocalDate?): MutableMap<String?, LocalDateTime?> {
        var from: LocalDateTime? = null
        var to: LocalDateTime? = null

        if (dateFrom != null) {
            from = dateFrom.atStartOfDay()
        }
        if (dateTo != null) {
            to = dateTo.atTime(LocalTime.MAX)
        }

        val result = HashMap<String?, LocalDateTime?>()
        result.put("from", from)
        result.put("to", to)
        return result
    }
}
