package com.ctrip.framework.apollo.monitor.metrics.collector;

import com.ctrip.framework.apollo.core.spi.Ordered;
import java.util.List;

/**
 * @author Rawven
 */
public interface MetricsCollectorManager extends Ordered {

  /**
   * get collectors
   */
  List<MetricsCollector> getCollectors();

  @Override
  default int getOrder() {
    return 0;
  }
}
