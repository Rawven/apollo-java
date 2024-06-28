package com.ctrip.framework.apollo.metrics;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.metrics.collector.internal.NopMetricsCollectorManager;
import com.ctrip.framework.foundation.Foundation;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import java.util.List;

/**
 * @author Rawven
 */
public abstract class Metrics {

  private static MetricsCollectorManager collectorManager;

  private static void init() {
    collectorManager = new NopMetricsCollectorManager();
    if (isMetricsEnabled()) {
      List<MetricsCollectorManager> managers = ServiceBootstrap.loadAllOrdered(
          MetricsCollectorManager.class);
      if (!managers.isEmpty()) {
        collectorManager = managers.get(0);
      }
    }
  }

  public static void push(MetricsEvent event) {
    if (collectorManager == null) {
      //Lazy loading
      init();
    }
    for (MetricsCollector collector : collectorManager.getCollectors()) {
      if (collector.isSupport(event.getTag())) {
        collector.collect(event);
        return;
      }
    }
  }

  public static boolean isMetricsEnabled() {
    String enabled = System.getProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED);
    if (enabled == null) {
      enabled = Foundation.app()
          .getProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED, "false");
    }
    return Boolean.parseBoolean(enabled);
  }

}
