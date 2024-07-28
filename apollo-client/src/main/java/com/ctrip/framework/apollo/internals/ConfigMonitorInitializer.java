package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.utils.ClassLoaderUtil;
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
import com.ctrip.framework.apollo.monitor.internal.tracer.MessageProducerComposite;
import com.ctrip.framework.apollo.monitor.internal.tracer.MonitorMessageProducer;
import com.ctrip.framework.apollo.tracer.internals.NullMessageProducer;
import com.ctrip.framework.apollo.tracer.internals.cat.CatMessageProducer;
import com.ctrip.framework.apollo.tracer.internals.cat.CatNames;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * @author Rawven
 */

public class ConfigMonitorInitializer {

  private static  ConfigUtil m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
  private static  Boolean hasInitialized = false;

  public static void initializeMonitorSystem() {
    if (m_configUtil.isClientMonitorEnabled() && !hasInitialized) {
      DefaultMetricsCollectorManager manager = initializeMetricsCollectorManager();
      List<MetricsCollector> collectors = initializeCollectors(manager);
      MetricsExporter metricsExporter = initializeMetricsExporter(collectors);
      initializeConfigMonitor(collectors, metricsExporter);
      hasInitialized = true;
    }
  }

  private static DefaultMetricsCollectorManager initializeMetricsCollectorManager() {
    return (DefaultMetricsCollectorManager) ApolloInjector.getInstance(MetricsCollectorManager.class);
  }

  private static List<MetricsCollector> initializeCollectors(DefaultMetricsCollectorManager manager) {
    DefaultConfigManager configManager = (DefaultConfigManager) ApolloInjector.getInstance(ConfigManager.class);
    DefaultApolloExceptionCollector exceptionCollector = new DefaultApolloExceptionCollector();
    DefaultApolloThreadPoolCollector threadPoolCollector = new DefaultApolloThreadPoolCollector(
        RemoteConfigRepository.m_executorService, AbstractConfig.m_executorService, AbstractConfigFile.m_executorService);
    DefaultApolloNamespaceCollector namespaceCollector = new DefaultApolloNamespaceCollector(
        configManager.m_configs, configManager.m_configLocks, configManager.m_configFiles, configManager.m_configFileLocks);
    DefaultApolloRunningParamsCollector startupCollector = new DefaultApolloRunningParamsCollector(m_configUtil);

    List<MetricsCollector> collectors = Lists.newArrayList(exceptionCollector, namespaceCollector, threadPoolCollector, startupCollector);
    manager.setCollectors(collectors);
    return collectors;
  }

  private static MetricsExporter initializeMetricsExporter(List<MetricsCollector> collectors) {
    MetricsExporterFactory reporterFactory = ApolloInjector.getInstance(MetricsExporterFactory.class);
    return reporterFactory.getMetricsReporter(collectors);
  }

  private static void initializeConfigMonitor(List<MetricsCollector> collectors, MetricsExporter metricsExporter) {
    DefaultConfigMonitor defaultConfigMonitor = (DefaultConfigMonitor) ApolloInjector.getInstance(ConfigMonitor.class);
    DefaultApolloExceptionCollector exceptionCollector = (DefaultApolloExceptionCollector) collectors.get(0);
    DefaultApolloNamespaceCollector namespaceCollector = (DefaultApolloNamespaceCollector) collectors.get(1);
    DefaultApolloThreadPoolCollector threadPoolCollector = (DefaultApolloThreadPoolCollector) collectors.get(2);
    DefaultApolloRunningParamsCollector startupCollector = (DefaultApolloRunningParamsCollector) collectors.get(3);
    defaultConfigMonitor.init(namespaceCollector, threadPoolCollector, exceptionCollector, startupCollector, metricsExporter);
  }

  public static MessageProducerComposite initializeMessageProducerComposite() {

    // Prioritize loading user-defined producers from SPI
    List<MessageProducer> producers = ServiceBootstrap.loadAllOrdered(MessageProducer.class);

    // The producer that comes with the client
    if (m_configUtil.isClientMonitorEnabled()) {
      producers.add(new MonitorMessageProducer());
    }

    if (ClassLoaderUtil.isClassPresent(CatNames.CAT_CLASS)) {
      producers.add(new CatMessageProducer());
    }

    // default logic
    if (producers.isEmpty()) {
      producers.add(new NullMessageProducer());
    }
    return new MessageProducerComposite(producers);
  }
}