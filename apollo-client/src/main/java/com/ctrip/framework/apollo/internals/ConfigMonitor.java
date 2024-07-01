package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.monitor.exposer.NamespaceMetricsExposer;
import com.ctrip.framework.apollo.monitor.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.monitor.exposer.ThreadPoolMetricsExposer;
import com.ctrip.framework.apollo.monitor.exposer.ExceptionMetricsExposer;

public interface ConfigMonitor {

  ThreadPoolMetricsExposer getMemoryStatusExposer();

  ExceptionMetricsExposer getTracerEventExposer();

  NamespaceMetricsExposer getClientEventExposer();

  StartupParamsExposer getStartupParamsExposer();

  String getDataWithCurrentMonitoringSystemFormat();
}
