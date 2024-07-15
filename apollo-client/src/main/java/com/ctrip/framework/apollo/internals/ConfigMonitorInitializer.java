package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.monitor.api.ConfigMonitor;
import com.ctrip.framework.apollo.monitor.internal.DefaultConfigMonitor;
import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloExceptionCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloNamespaceCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloRunningParamsCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloThreadPoolCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultMetricsCollectorManager;
import com.ctrip.framework.apollo.monitor.internal.exporter.MetricsExporter;
import com.ctrip.framework.apollo.monitor.internal.exporter.MetricsExporterFactory;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * @author Rawven
 */
public class ConfigMonitorInitializer {

  public static void init() {
    DefaultMetricsCollectorManager manager = (DefaultMetricsCollectorManager) ApolloInjector.getInstance(
        MetricsCollectorManager.class);
    ConfigUtil configUtil = ApolloInjector.getInstance(ConfigUtil.class);
    DefaultConfigManager configManager = (DefaultConfigManager) ApolloInjector.getInstance(
        ConfigManager.class);
    MetricsExporterFactory reporterFactory = ApolloInjector.getInstance(
        MetricsExporterFactory.class);
    //init collector
    DefaultApolloExceptionCollector exceptionCollector = new DefaultApolloExceptionCollector();
    DefaultApolloThreadPoolCollector threadPoolCollector = new DefaultApolloThreadPoolCollector(
        RemoteConfigRepository.m_executorService, AbstractConfig.m_executorService,
        AbstractConfigFile.m_executorService);
    DefaultApolloNamespaceCollector namespaceCollector = new DefaultApolloNamespaceCollector(
        configManager.m_configs,
        configManager.m_configLocks, configManager.m_configFiles, configManager.m_configFileLocks);
    DefaultApolloRunningParamsCollector startupCollector = new DefaultApolloRunningParamsCollector(
        configUtil);
    List<MetricsCollector> collectors = Lists.newArrayList(exceptionCollector, namespaceCollector,
        threadPoolCollector,
        startupCollector);
    manager.setCollectors(collectors);
    //init reporter and monitor
    MetricsExporter metricsExporter = reporterFactory.getMetricsReporter(collectors);
    DefaultConfigMonitor defaultConfigMonitor = (DefaultConfigMonitor) ApolloInjector.getInstance(
        ConfigMonitor.class);
    defaultConfigMonitor.init(namespaceCollector, threadPoolCollector, exceptionCollector,
        startupCollector, metricsExporter
    );
  }
}
