package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporterFactory;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import java.util.List;
import org.slf4j.Logger;

public class DefaultMetricsReporterFactory implements MetricsReporterFactory {
  private static final Logger logger = DeferredLoggerFactory.getLogger(
      DefaultMetricsCollectorManager.class);
  private final ConfigUtil m_configUtil;

  public DefaultMetricsReporterFactory(){
    m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
  }

  @Override
  public MetricsReporter getMetricsReporter(List<MetricsCollector> collectors) {
    //init reporter
    String form = m_configUtil.getMonitorForm();
    MetricsReporter reporter = null;
    if (form != null) {
      List<MetricsReporter> metricsReporters = ServiceBootstrap.loadAllOrdered(
          MetricsReporter.class);
      for (MetricsReporter metricsReporter : metricsReporters) {
        if (metricsReporter.isSupport(form)) {
          reporter = metricsReporter;
          reporter.init(collectors, m_configUtil.getMonitorExportPeriod());
          break;
        }
      }
      if (reporter == null) {
        ApolloConfigException exception = new ApolloConfigException(
            "Error initializing MetricsReporter for form: " + form);
        logger.error(
            "Error initializing MetricsReporter for protocol: {},Please check whether necessary dependencies are imported, such as apollo-plugin-client-prometheus",
            form, exception);
        Tracer.logError(exception);
        throw exception;
      }
    }
    return reporter;
  }
}
