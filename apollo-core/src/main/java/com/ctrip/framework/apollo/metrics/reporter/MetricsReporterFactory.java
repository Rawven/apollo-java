package com.ctrip.framework.apollo.metrics.reporter;

import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import java.util.List;

public interface MetricsReporterFactory {

  MetricsReporter getMetricsReporter(List<MetricsCollector> collectors);
}
