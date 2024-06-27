package com.ctrip.framework.apollo.core.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.
      ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

}
