package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.metrics.util.JMXUtil;
import com.ctrip.framework.apollo.monitor.DefaultExceptionCollector;
import com.ctrip.framework.apollo.monitor.DefaultNamespaceCollector;
import com.ctrip.framework.apollo.monitor.DefaultStartupParamsCollector;
import com.ctrip.framework.apollo.monitor.DefaultThreadPoolCollector;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import com.google.common.collect.Lists;
import java.util.List;
import org.slf4j.Logger;

/**
 * @author Rawven
 */
public class DefaultMetricsCollectorManager implements MetricsCollectorManager {

  private static final Logger logger = DeferredLoggerFactory.getLogger(
      DefaultMetricsCollectorManager.class);
  private List<MetricsCollector> collectors;

  public DefaultMetricsCollectorManager() {
    ConfigUtil configUtil = ApolloInjector.getInstance(ConfigUtil.class);
    DefaultConfigManager configManager = (DefaultConfigManager) ApolloInjector.getInstance(
        ConfigManager.class);
    initialize(configUtil, configManager);
  }

  private void initialize(ConfigUtil configUtil,
      DefaultConfigManager configManager) {
    //init collector
    DefaultExceptionCollector exceptionCollector = new DefaultExceptionCollector();
    DefaultThreadPoolCollector threadPoolCollector = new DefaultThreadPoolCollector(
        RemoteConfigRepository.m_executorService, AbstractConfig.m_executorService,
        AbstractConfigFile.m_executorService);
    DefaultNamespaceCollector namespaceCollector = new DefaultNamespaceCollector(
        configManager.m_configs,
        configManager.m_configLocks, configManager.m_configFiles, configManager.m_configFileLocks);
    DefaultStartupParamsCollector startupCollector = new DefaultStartupParamsCollector(configUtil);
    collectors = Lists.newArrayList(exceptionCollector, namespaceCollector,
        threadPoolCollector,
        startupCollector);

    //init reporter
    String form = configUtil.getMonitorForm();
    MetricsReporter reporter = null;
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
          logger.error("Error initializing MetricsReporter for protocol: {} ."
              + "Please make sure you include the necessary dependencies,such as apollo-plugin-client-prometheus", form, e);
          ApolloConfigException exception = new ApolloConfigException(
              "Error initializing MetricsReporter for form: " + form, e);
          Tracer.logError(exception);
          throw exception;
        }
      }
    }
    //init monitor
    DefaultConfigMonitor defaultConfigMonitor = (DefaultConfigMonitor) ApolloInjector.getInstance(
        ConfigMonitor.class);
    defaultConfigMonitor.init(namespaceCollector, threadPoolCollector, exceptionCollector,
        startupCollector, reporter
    );
  }

  @Override
  public List<MetricsCollector> getCollectors() {
    return collectors;
  }

}
