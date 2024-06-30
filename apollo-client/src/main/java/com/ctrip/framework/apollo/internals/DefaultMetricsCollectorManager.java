package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.metrics.collector.ClientEventCollector;
import com.ctrip.framework.apollo.metrics.collector.ConfigMemoryStatusCollector;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.metrics.collector.TracerEventCollector;
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
    TracerEventCollector traceCollector = new TracerEventCollector();
    ConfigMemoryStatusCollector configMemoryStatusCollector = new ConfigMemoryStatusCollector(
        configManager.m_configs,
        configManager.m_configLocks, configManager.m_configFiles, configManager.m_configFileLocks,
        configUtil, RemoteConfigRepository.m_executorService, AbstractConfig.m_executorService,
        AbstractConfigFile.m_executorService);
    ClientEventCollector clientEventCollector = new ClientEventCollector();
    collectors = Lists.newArrayList(traceCollector, clientEventCollector,
        configMemoryStatusCollector);

    //init monitor
    DefaultConfigMonitorImpl defaultConfigMonitorImpl = (DefaultConfigMonitorImpl) ApolloInjector.getInstance(
        ConfigMonitor.class);
    defaultConfigMonitorImpl.init(clientEventCollector, configMemoryStatusCollector, traceCollector,
        collectors,
        configUtil);
  }

  @Override
  public List<MetricsCollector> getCollectors() {
    return collectors;
  }

}
