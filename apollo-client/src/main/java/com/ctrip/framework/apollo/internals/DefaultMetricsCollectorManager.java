package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.monitor.DefaultNamespaceMetricsExposer;
import com.ctrip.framework.apollo.monitor.DefaultStartupParamsExposer;
import com.ctrip.framework.apollo.monitor.DefaultThreadPoolMetricsExposer;
import com.ctrip.framework.apollo.monitor.DefaultExceptionMetricsExposer;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Lists;
import java.util.List;

/**
 * @author Rawven
 */
public class DefaultMetricsCollectorManager implements MetricsCollectorManager {

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
    DefaultExceptionMetricsExposer traceCollector = new DefaultExceptionMetricsExposer();
    DefaultThreadPoolMetricsExposer configMemoryStatusCollector = new DefaultThreadPoolMetricsExposer(
        RemoteConfigRepository.m_executorService, AbstractConfig.m_executorService,
        AbstractConfigFile.m_executorService);
    DefaultNamespaceMetricsExposer clientEventCollector = new DefaultNamespaceMetricsExposer(
        configManager.m_configs,
        configManager.m_configLocks, configManager.m_configFiles, configManager.m_configFileLocks);
    DefaultStartupParamsExposer startupCollector = new DefaultStartupParamsExposer(configUtil);

    //only save collector which will push event
    collectors = Lists.newArrayList(traceCollector, clientEventCollector);

    //init monitor
    DefaultConfigMonitor defaultConfigMonitor = (DefaultConfigMonitor) ApolloInjector.getInstance(
        ConfigMonitor.class);
    defaultConfigMonitor.init(clientEventCollector, configMemoryStatusCollector, traceCollector,
        startupCollector,
        configUtil);
  }

  @Override
  public List<MetricsCollector> getCollectors() {
    return collectors;
  }

}
