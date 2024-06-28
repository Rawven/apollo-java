package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.metrics.collector.ClientEventCollector;
import com.ctrip.framework.apollo.metrics.collector.ClientEventCollectorMBean;
import com.ctrip.framework.apollo.metrics.collector.ConfigMemoryStatusCollector;
import com.ctrip.framework.apollo.metrics.collector.ConfigMemoryStatusCollectorMBean;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.collector.TracerEventCollector;
import com.ctrip.framework.apollo.metrics.collector.TracerEventCollectorMBean;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.metrics.util.JMXUtil;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import java.util.List;
import org.slf4j.Logger;

/**
 * exposes all collected data through ConfigService
 *
 * @author Rawven
 */
public class ConfigMonitor {

  private static final Logger logger = DeferredLoggerFactory.getLogger(ConfigMonitor.class);
  private MetricsReporter reporter;
  private ConfigMemoryStatusCollectorMBean memoryStatusCollector;
  private TracerEventCollectorMBean tracerEventCollector;
  private ClientEventCollectorMBean clientEventCollector;

  public ConfigMemoryStatusCollectorMBean getMemoryStatusMetrics() {
    return memoryStatusCollector;
  }

  public TracerEventCollectorMBean getTracerEventMetrics() {
    return tracerEventCollector;
  }

  public ClientEventCollectorMBean getClientEventMetrics() {
    return clientEventCollector;
  }

  public String getDataWithCurrentMonitoringSystemFormat() {
    if (reporter == null) {
      return "No MonitoringSystem Use";
    }
    return reporter.response();
  }

  public void init(ClientEventCollector clientEventCollector,
      ConfigMemoryStatusCollector configMemoryStatusCollector, TracerEventCollector tracerEventCollector,
      List<MetricsCollector> metricsCollectors,
      ConfigUtil configUtil) {
    this.clientEventCollector = clientEventCollector;
    this.memoryStatusCollector = configMemoryStatusCollector;
    this.tracerEventCollector = tracerEventCollector;

    //init reporter
    String protocol = configUtil.getMonitorProtocol();
     if (protocol != null && !JMXUtil.JMX.equals(protocol)) {
      try {
        reporter = ServiceBootstrap.loadPrimary(MetricsReporter.class);
        if (reporter != null) {
          reporter.init(metricsCollectors,configUtil.getMonitorCollectPeriod());
        } else {
          logger.warn("No MetricsReporter found for protocol: {}", protocol);
        }
      } catch (Exception e) {
        logger.error("Error initializing MetricsReporter for protocol: {}", protocol, e);
        ApolloConfigException exception = new ApolloConfigException(
            "Error initializing MetricsReporter for protocol: " + protocol, e);
        Tracer.logError(exception);
      }
    }
  }

}
