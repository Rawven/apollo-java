package com.ctrip.framework.apollo.monitor.metrics.reporter;

import com.ctrip.framework.apollo.core.spi.Ordered;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample;
import java.util.List;

/**
 * @author Rawven
 */
public interface MetricsReporter extends Ordered {

  /**
   * init method
   */
  void init(List<MetricsCollector> collectors, long collectPeriod);

  /**
   * is support
   *
   * @param form form
   * @return
   */
  boolean isSupport(String form);
  /**
   * used to register Counter type metrics
   */
  void registerCounterSample(CounterMetricsSample sample);

  /**
   * used to register Gauge type metrics
   */
  void registerGaugeSample(GaugeMetricsSample<?> sample);

  /**
   * result of the collect metrics
   */
  String response();

  @Override
  default int getOrder() {
    return 0;
  }
}
