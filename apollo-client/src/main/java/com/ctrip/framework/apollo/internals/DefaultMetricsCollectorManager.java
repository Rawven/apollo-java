package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporterFactory;
import com.ctrip.framework.apollo.monitor.DefaultExceptionCollector;
import com.ctrip.framework.apollo.monitor.DefaultNamespaceCollector;
import com.ctrip.framework.apollo.monitor.DefaultStartupParamsCollector;
import com.ctrip.framework.apollo.monitor.DefaultThreadPoolCollector;
import com.ctrip.framework.apollo.util.ConfigUtil;
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

    //init reporter and monitor
    MetricsReporterFactory reporterFactory = ApolloInjector.getInstance(MetricsReporterFactory.class);
    MetricsReporter metricsReporter = reporterFactory.getMetricsReporter(collectors);

    DefaultConfigMonitor defaultConfigMonitor = (DefaultConfigMonitor) ApolloInjector.getInstance(
        ConfigMonitor.class);
    defaultConfigMonitor.init(namespaceCollector, threadPoolCollector, exceptionCollector,
        startupCollector, metricsReporter
    );

  }

  @Override
  public List<MetricsCollector> getCollectors() {
    return collectors;
  }

}
