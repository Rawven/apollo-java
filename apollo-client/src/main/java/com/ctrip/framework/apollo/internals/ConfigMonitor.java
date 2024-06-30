package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.metrics.exposer.NamespaceMetricsExposer;
import com.ctrip.framework.apollo.metrics.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.metrics.exposer.ThreadPoolMetricsExposer;
import com.ctrip.framework.apollo.metrics.exposer.ExceptionMetricsExposer;

public interface ConfigMonitor {

  ThreadPoolMetricsExposer getMemoryStatusExposer();

  ExceptionMetricsExposer getTracerEventExposer();

  NamespaceMetricsExposer getClientEventExposer();

  StartupParamsExposer getStartupParamsExposer();

  String getDataWithCurrentMonitoringSystemFormat();
}
