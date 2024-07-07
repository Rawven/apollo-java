package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
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

/**
 * @author Rawven
 */
public class DefaultMetricsCollectorManager implements MetricsCollectorManager {
  private List<MetricsCollector> collectors;

  public DefaultMetricsCollectorManager() {
    initializeMonitorChain();
  }

  private void initializeMonitorChain() {
    ConfigUtil configUtil = ApolloInjector.getInstance(ConfigUtil.class);
    DefaultConfigManager configManager = (DefaultConfigManager) ApolloInjector.getInstance(
        ConfigManager.class);
    MetricsReporterFactory reporterFactory = ApolloInjector.getInstance(MetricsReporterFactory.class);

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
