package com.ctrip.framework.apollo.metrics.collector;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Rawven
 */
public class MemoryStatusCollector extends AbstractMetricsCollector implements
    MemoryStatusCollectorMBean {

  public static final String APPID = "appId";
  public static final String CLUSTER = "cluster";
  public static final String ENV = "env";
  public static final String STARTUP_PARAMETERS = "startup_parameters";

  private final ConfigUtil m_configUtil;
  private final Map<String, Config> m_configs;
  private final Map<String, Object> m_configLocks;
  private final Map<String, ConfigFile> m_configFiles;
  private final Map<String, Object> m_configFileLocks;
  private final ScheduledThreadPoolExecutor remoteConfigRepositoryExecutorService;
  private final ThreadPoolExecutor abstractConfigExecutorService;
  private final ThreadPoolExecutor abstractConfigFileExecutorService;

  public MemoryStatusCollector(Map<String, Config> m_configs,
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
  public String getAbstractConfigExecutorServiceInfo() {
    return getThreadPoolInfo(abstractConfigExecutorService);
  }

  @Override
  public String getAbstractConfigFileExecutorServiceInfo() {
    return getThreadPoolInfo(abstractConfigFileExecutorService);
  }

  @Override
  public String getRemoteConfigRepositoryExecutorServiceInfo() {
    return getThreadPoolInfo(remoteConfigRepositoryExecutorService);
  }


  private String getThreadPoolInfo(ThreadPoolExecutor executor) {
    // 获取线程池中正在执行任务的线程数
    int activeCount = executor.getActiveCount();
    // 获取等待执行的任务数
    int queueSize = executor.getQueue().size();
    // 获取线程池已完成的任务总数
    long completedTaskCount = executor.getCompletedTaskCount();
    // 获取当前线程池中线程的数量
    int poolSize = executor.getPoolSize();
    // 获取曾经提交给线程池的任务总数，包括已完成和正在执行的任务
    long taskCount = executor.getTaskCount();
    // 获取核心线程数
    int corePoolSize = executor.getCorePoolSize();
    // 获取线程池中允许的最大线程数
    int maximumPoolSize = executor.getMaximumPoolSize();
    // 获取线程池运行期间同时存在的最大线程数
    int largestPoolSize = executor.getLargestPoolSize();
    // 获取工作队列
    BlockingQueue<Runnable> queue = executor.getQueue();
    // 获取队列的总容量（已使用容量 + 剩余容量）
    int queueCapacity = queue.remainingCapacity() + queue.size();
    // 获取队列的剩余容量
    int remainingCapacity = queue.remainingCapacity();
    // 计算线程池当前负载
    double currentLoad = (double) poolSize / maximumPoolSize;
    // 返回包含线程池状态信息的字符串
    return String.format(
        "Active task count: %d, "
            + "Queue size: %d, "
            + "Completed task count: %d, "
            + "Pool size: %d, "
            + "Total task count: %d, "
            + "Core pool size: %d, "
            + "Maximum pool size: %d, "
            + "Largest pool size: %d, "
            + "Queue capacity: %d, "
            + "Queue remaining capacity: %d, "
            + "Current load: %.2f",
        activeCount, queueSize, completedTaskCount, poolSize, taskCount,
        corePoolSize, maximumPoolSize, largestPoolSize, queueCapacity, remainingCapacity,
        currentLoad
    );
  }

  @Override
  public List<String> getAllUsedNamespaceName() {
    ArrayList<String> namespaces = Lists.newArrayList();
    m_configs.forEach((k, v) -> namespaces.add(k));
    return namespaces;
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
    exportThreadPoolMetrics(samples, abstractConfigExecutorService, "abstractConfig_");
    exportThreadPoolMetrics(samples, abstractConfigFileExecutorService, "abstractConfigFile_");
    exportThreadPoolMetrics(samples, remoteConfigRepositoryExecutorService, "remoteConfigRepository_");
    return samples;
  }
  public void exportThreadPoolMetrics(List<MetricsSample> samples,ThreadPoolExecutor executor,String name){
    samples.add(GaugeMetricsSample.builder().name(name+"active_task_count").value(executor.getActiveCount()).apply(value -> (int) value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"queue_size").value(executor.getQueue().size()).apply(value -> (int) value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"completed_task_count").value(executor.getCompletedTaskCount()).apply(value -> (long) value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"pool_size").value(executor.getPoolSize()).apply(value -> (int)value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"total_task_count").value(executor.getTaskCount()).apply(value -> (long)value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"core_pool_size").value(executor.getCorePoolSize()).apply(value -> (int)value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"maximum_pool_size").value(executor.getMaximumPoolSize()).apply(value -> (int)value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"largest_pool_size").value(executor.getLargestPoolSize()).apply(value -> (int)value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"queue_capacity").value(executor.getQueue().remainingCapacity() + executor.getQueue().size()).apply(value -> (int)value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"queue_remaining_capacity").value(executor.getQueue().remainingCapacity()).apply(value -> (int)value).build());
    samples.add(GaugeMetricsSample.builder().name(name+"current_load").value((double) executor.getPoolSize() / executor.getMaximumPoolSize()).apply(value -> (double)value).build());
  }
}
