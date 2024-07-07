package com.ctrip.framework.apollo.monitor.metrics;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.foundation.Foundation;

/**
 * @author Rawven
 */
public abstract class Metrics {

  private static MetricsCollectorManager collectorManager;

  private static void init() {
    collectorManager = ApolloInjector.getInstance(MetricsCollectorManager.class);
  }


  public static void push(MetricsEvent event) {
    if (isClientMonitorEnabled()) {
      if (collectorManager == null) {
        //Lazy loading
        init();
      }
      for (MetricsCollector collector : collectorManager.getCollectors()) {
        if (collector.isSupport(event)) {
          collector.collect(event);
          return;
        }
      }
    }
  }

  public static boolean isClientMonitorEnabled() {
    String enabled = System.getProperty(ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_ENABLED);
    if (enabled == null) {
      enabled = Foundation.app()
          .getProperty(ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_ENABLED, "false");
    }
    return Boolean.parseBoolean(enabled);
  }


}
