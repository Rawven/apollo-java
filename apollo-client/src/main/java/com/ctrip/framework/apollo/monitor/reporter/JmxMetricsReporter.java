package com.ctrip.framework.apollo.monitor.reporter;

import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.metrics.util.JMXUtil;
import java.util.List;

/**
 * jmx reporter
 */
public class JmxMetricsReporter implements MetricsReporter {

  @Override
  public void init(List<MetricsCollector> collectors, long collectPeriod) {
     collectors.forEach(metricsCollector ->
             JMXUtil.register(JMXUtil.MBEAN_NAME + metricsCollector.name(),
                 metricsCollector));
  }

  @Override
  public boolean isSupport(String form) {
       return JMXUtil.JMX.equals(form);
  }

  @Override
  public void registerCounterSample(CounterMetricsSample sample) {
  }

  @Override
  public void registerGaugeSample(GaugeMetricsSample<?> sample) {
  }

  @Override
  public String response() {
    return "JMX has no format data";
  }

  @Override
  public int getOrder() {
    return 2;
  }
}
