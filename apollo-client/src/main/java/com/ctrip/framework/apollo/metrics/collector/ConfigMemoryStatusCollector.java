package com.ctrip.framework.apollo.metrics.collector;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Rawven
 */
public class ConfigMemoryStatusCollector extends AbstractMetricsCollector implements
    ConfigMemoryStatusCollectorMBean {

  public static final String APPID = "appId";
  public static final String CLUSTER = "cluster";
  public static final String ENV = "env";
  public static final String STARTUP_PARAMETERS = "startup_parameters";
  public static final String[] THREAD_POOL_PARAMS = new String[]{"ThreadPoolName", "activeTaskCount", "queueSize",
      "completedTaskCount",
      "poolSize", "totalTaskCount", "corePoolSize", "maximumPoolSize", "largestPoolSize",
      "queueCapacity", "queueRemainingCapacity", "currentLoad"};
  private final ConfigUtil m_configUtil;
  private final Map<String, Config> m_configs;
  //TODO
  private final Map<String, Object> m_configLocks;
  private final Map<String, ConfigFile> m_configFiles;
  private final Map<String, Object> m_configFileLocks;
  //TODO
  private final ScheduledThreadPoolExecutor remoteConfigRepositoryExecutorService;
  private final ThreadPoolExecutor abstractConfigExecutorService;
  private final ThreadPoolExecutor abstractConfigFileExecutorService;

  public ConfigMemoryStatusCollector(Map<String, Config> m_configs,
      Map<String, Object> m_configLocks,
      Map<String, ConfigFile> m_configFiles,
      Map<String, Object> m_configFileLocks, ConfigUtil m_configUtil,
      ScheduledExecutorService remoteConfigRepositoryExecutorService,
      ExecutorService abstractConfigExecutorService,
      ExecutorService abstractConfigFileExecutorService) {
    super("Nop");
    this.m_configs = m_configs;
    this.m_configLocks = m_configLocks;
    this.m_configFiles = m_configFiles;
    this.m_configFileLocks = m_configFileLocks;
    this.m_configUtil = m_configUtil;
    this.remoteConfigRepositoryExecutorService = (ScheduledThreadPoolExecutor) remoteConfigRepositoryExecutorService;
    this.abstractConfigExecutorService = (ThreadPoolExecutor) abstractConfigExecutorService;
    this.abstractConfigFileExecutorService = (ThreadPoolExecutor) abstractConfigFileExecutorService;
  }


  @Override
  public List<String> getAllUsedNamespaceName() {
    ArrayList<String> namespaces = Lists.newArrayList();
    m_configs.forEach((k, v) -> namespaces.add(k));
    return namespaces;
  }

  @Override
  public int getRemotePollThreadPoolActiveCount() {
    return remoteConfigRepositoryExecutorService.getActiveCount();
  }

  @Override
  public int getRemotePollThreadPoolQueueSize() {
    return remoteConfigRepositoryExecutorService.getQueue().size();
  }

  @Override
  public int getRemotePollThreadPoolCorePoolSize() {
    return remoteConfigRepositoryExecutorService.getCorePoolSize();
  }

  @Override
  public int getRemotePollThreadPoolMaximumPoolSize() {
    return remoteConfigRepositoryExecutorService.getMaximumPoolSize();
  }

  @Override
  public int getRemotePollThreadPoolPoolSize() {
    return remoteConfigRepositoryExecutorService.getPoolSize();
  }

  @Override
  public long getRemotePollThreadPoolTaskCount() {
    return remoteConfigRepositoryExecutorService.getTaskCount();
  }

  @Override
  public long getRemotePollThreadPoolCompletedTaskCount() {
    return remoteConfigRepositoryExecutorService.getCompletedTaskCount();
  }

  @Override
  public int getRemotePollThreadPoolLargestPoolSize() {
    return remoteConfigRepositoryExecutorService.getLargestPoolSize();
  }

  @Override
  public int getRemotePollThreadPoolRemainingCapacity() {
    return remoteConfigRepositoryExecutorService.getQueue().remainingCapacity();
  }

  @Override
  public double getRemotePollThreadPoolCurrentLoad() {
    return (double) remoteConfigRepositoryExecutorService.getPoolSize()
        / remoteConfigRepositoryExecutorService.getMaximumPoolSize();
  }

  @Override
  public String getAppId() {
    return m_configUtil.getAppId();
  }

  @Override
  public String getCluster() {
    return m_configUtil.getCluster();
  }

  @Override
  public String getApolloEnv() {
    return m_configUtil.getApolloEnv().name();
  }

  @Override
  public boolean isSupport(String tag) {
    return false;
  }

  @Override
  public void collect0(MetricsEvent event) {
    return;
  }

  @Override
  public boolean isSamplesUpdated() {
    // memory status special
    return true;
  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
    samples.add(GaugeMetricsSample.builder().name(STARTUP_PARAMETERS).value(1)
        .apply(value -> 1)
        .putTag(APPID, getAppId())
        .putTag(CLUSTER, getCluster())
        .putTag(ENV, getApolloEnv()).build());
    //TODO
    //exportThreadPoolMetrics(samples, abstractConfigExecutorService, "abstractConfig_");
    //exportThreadPoolMetrics(samples, abstractConfigFileExecutorService, "abstractConfigFile_");
    exportThreadPoolMetrics(samples, remoteConfigRepositoryExecutorService,
        "remoteConfigRepository");
    return samples;
  }

  public void exportThreadPoolMetrics(List<MetricsSample> samples, ThreadPoolExecutor executor,
      String name) {
    List<Double> list = Arrays.asList((double)executor.getActiveCount(),
        (double)executor.getQueue().size(),
        (double)executor.getCompletedTaskCount(), (double) executor.getPoolSize(),
        (double) executor.getTaskCount(), (double) executor.getCorePoolSize(),
        (double) executor.getMaximumPoolSize(), (double) executor.getLargestPoolSize(),
        (double) (executor.getQueue().remainingCapacity() + executor.getQueue().size()),
        (double) executor.getQueue().remainingCapacity(),
        (double) executor.getPoolSize() / executor.getMaximumPoolSize());
    for (int i = 0; i<list.size(); i++) {
      samples.add(GaugeMetricsSample.builder().putTag(THREAD_POOL_PARAMS[0], name).name(THREAD_POOL_PARAMS[i+1])
          .value(list.get(i)).apply(value -> (double)value).build());
    }
  }
}