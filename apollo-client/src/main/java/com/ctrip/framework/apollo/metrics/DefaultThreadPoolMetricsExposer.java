package com.ctrip.framework.apollo.metrics;

import com.ctrip.framework.apollo.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.metrics.exposer.ThreadPoolMetricsExposer;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Rawven
 */
public class DefaultThreadPoolMetricsExposer extends AbstractMetricsCollector implements
    ThreadPoolMetricsExposer {


  public static final String[] THREAD_POOL_PARAMS = new String[]{"ThreadPoolName", "activeTaskCount", "queueSize",
      "completedTaskCount",
      "poolSize", "totalTaskCount", "corePoolSize", "maximumPoolSize", "largestPoolSize",
      "queueCapacity", "queueRemainingCapacity", "currentLoad"};

  //TODO
  private final ScheduledThreadPoolExecutor remoteConfigRepositoryExecutorService;
  private final ThreadPoolExecutor abstractConfigExecutorService;
  private final ThreadPoolExecutor abstractConfigFileExecutorService;

  public DefaultThreadPoolMetricsExposer(
      ScheduledExecutorService remoteConfigRepositoryExecutorService,
      ExecutorService abstractConfigExecutorService,
      ExecutorService abstractConfigFileExecutorService) {
    super("Nop");
    this.remoteConfigRepositoryExecutorService = (ScheduledThreadPoolExecutor) remoteConfigRepositoryExecutorService;
    this.abstractConfigExecutorService = (ThreadPoolExecutor) abstractConfigExecutorService;
    this.abstractConfigFileExecutorService = (ThreadPoolExecutor) abstractConfigFileExecutorService;
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
  public String name() {
    return "ThreadPoolMetrics";
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