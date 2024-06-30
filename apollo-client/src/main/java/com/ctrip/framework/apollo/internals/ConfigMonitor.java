package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.metrics.collector.ClientEventCollectorMBean;
import com.ctrip.framework.apollo.metrics.collector.ConfigMemoryStatusCollectorMBean;
import com.ctrip.framework.apollo.metrics.collector.TracerEventCollectorMBean;

public interface ConfigMonitor {

  ConfigMemoryStatusCollectorMBean getMemoryStatusMetrics();

  TracerEventCollectorMBean getTracerEventMetrics();

  ClientEventCollectorMBean getClientEventMetrics();

  String getDataWithCurrentMonitoringSystemFormat();
}
