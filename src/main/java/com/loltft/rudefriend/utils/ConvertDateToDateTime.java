package com.loltft.rudefriend.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class ConvertDateToDateTime {

  public Map<String, LocalDateTime> convertMap(LocalDate dateFrom, LocalDate dateTo) {
    LocalDateTime from = null;
    LocalDateTime to = null;

    if (dateFrom != null) {
      from = dateFrom.atStartOfDay();
    }
    if (dateTo != null) {
      to = dateTo.atTime(LocalTime.MAX);
    }

    Map<String, LocalDateTime> result = new HashMap<>();
    result.put("from", from);
    result.put("to", to);
    return result;
  }
}
