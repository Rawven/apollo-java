package com.ctrip.framework.apollo.metrics.reporter;

import com.ctrip.framework.apollo.core.spi.Ordered;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
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
