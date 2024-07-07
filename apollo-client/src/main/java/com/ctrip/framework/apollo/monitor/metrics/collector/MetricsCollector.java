package com.ctrip.framework.apollo.monitor.metrics.collector;

import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.monitor.metrics.model.MetricsSample;
import java.util.List;

/**
 * @author Rawven
 */
public interface MetricsCollector {


  String name();

  /**
   * is support the event
   */
  boolean isSupport(MetricsEvent event);

  /**
   * collect metrics from event
   */
  void collect(MetricsEvent event);

  /**
   * is samples updated
   */
  boolean isSamplesUpdated();

  /**
   *
   * export to a format recognized by the monitoring system
   */
  List<MetricsSample> export();


}
