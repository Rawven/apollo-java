package com.ctrip.framework.apollo.monitor.metrics.reporter;

import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollector;
import java.util.List;

public interface MetricsReporterFactory {

  MetricsReporter getMetricsReporter(List<MetricsCollector> collectors);
}
