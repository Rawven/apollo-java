package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.metrics.DefaultNamespaceMetricsExposer;
import com.ctrip.framework.apollo.metrics.DefaultStartupParamsExposer;
import com.ctrip.framework.apollo.metrics.DefaultThreadPoolMetricsExposer;
import com.ctrip.framework.apollo.metrics.DefaultExceptionMetricsExposer;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.exposer.NamespaceMetricsExposer;
import com.ctrip.framework.apollo.metrics.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.metrics.exposer.ThreadPoolMetricsExposer;
import com.ctrip.framework.apollo.metrics.exposer.ExceptionMetricsExposer;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.metrics.util.JMXUtil;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import com.google.common.collect.Lists;
import java.util.List;
import org.slf4j.Logger;

/**
 * exposes all collected data through ConfigService
 *
 * @author Rawven
 */
public class DefaultConfigMonitor implements ConfigMonitor {

  private static final Logger logger = DeferredLoggerFactory.getLogger(DefaultConfigMonitor.class);
  private MetricsReporter reporter;
  private ThreadPoolMetricsExposer memoryStatusExposer;
  private ExceptionMetricsExposer tracerEventExposer;
  private NamespaceMetricsExposer namespaceMetricsExposer;
  private StartupParamsExposer startupParamsExposer;

  @Override
  public ThreadPoolMetricsExposer getMemoryStatusExposer() {
    return memoryStatusExposer;
  }

  @Override
  public ExceptionMetricsExposer getTracerEventExposer() {
    return tracerEventExposer;
  }

  @Override
  public NamespaceMetricsExposer getClientEventExposer() {
    return namespaceMetricsExposer;
  }

  @Override
  public StartupParamsExposer getStartupParamsExposer() {
    return startupParamsExposer;
  }

  @Override
  public String getDataWithCurrentMonitoringSystemFormat() {
    if (reporter == null) {
      return "No MonitoringSystem Use";
    }
    return reporter.response();
  }

  public void init(DefaultNamespaceMetricsExposer ClientEventCollector,
      DefaultThreadPoolMetricsExposer threadPoolCollector,
      DefaultExceptionMetricsExposer defaultTracerEventCollector,
      DefaultStartupParamsExposer startupCollector,
      ConfigUtil configUtil) {
    this.namespaceMetricsExposer = ClientEventCollector;
    this.memoryStatusExposer = threadPoolCollector;
    this.tracerEventExposer = defaultTracerEventCollector;
    this.startupParamsExposer = startupCollector;
    List<MetricsCollector> collectors = Lists.newArrayList(
        ClientEventCollector,
        threadPoolCollector,
        defaultTracerEventCollector,
        startupCollector);

    //init reporter
    String form = configUtil.getMonitorForm();
    //TODO 有无优化实现
    if (form != null) {
      if (JMXUtil.JMX.equals(form)) {
        collectors.forEach(metricsCollector ->
            JMXUtil.register(JMXUtil.MBEAN_NAME + metricsCollector.name(),
                metricsCollector));
      } else {
        try {
          reporter = ServiceBootstrap.loadPrimary(MetricsReporter.class);
          reporter.init(collectors, configUtil.getMonitorExportPeriod());
        } catch (Exception e) {
          logger.error("Error initializing MetricsReporter for protocol: {}", form, e);
          ApolloConfigException exception = new ApolloConfigException(
              "Error initializing MetricsReporter for form: " + form, e);
          Tracer.logError(exception);
          throw exception;
        }
      }
    }
  }
}
