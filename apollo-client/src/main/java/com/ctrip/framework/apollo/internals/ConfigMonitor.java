package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.monitor.exposer.NamespaceExposer;
import com.ctrip.framework.apollo.monitor.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.monitor.exposer.ThreadPoolExposer;
import com.ctrip.framework.apollo.monitor.exposer.ExceptionExposer;

public interface ConfigMonitor {

  ThreadPoolExposer getMemoryStatusExposer();

  ExceptionExposer getTracerEventExposer();

  NamespaceExposer getClientEventExposer();

  StartupParamsExposer getStartupParamsExposer();

  String getDataWithCurrentMonitoringSystemFormat();
}
