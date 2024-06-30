package com.ctrip.framework.apollo.metrics.exposer;

import javax.management.MXBean;

@MXBean
public interface ThreadPoolMetricsExposer {


  int getRemotePollThreadPoolActiveCount();

  int getRemotePollThreadPoolQueueSize();

  int getRemotePollThreadPoolCorePoolSize();

  int getRemotePollThreadPoolMaximumPoolSize();

  int getRemotePollThreadPoolPoolSize();

  long getRemotePollThreadPoolTaskCount();

  long getRemotePollThreadPoolCompletedTaskCount();

  int getRemotePollThreadPoolLargestPoolSize();

  int getRemotePollThreadPoolRemainingCapacity();

  double getRemotePollThreadPoolCurrentLoad();

}
