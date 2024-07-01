package com.ctrip.framework.apollo.monitor.exposer;

import javax.management.MXBean;

/**
 * @author Rawven
 */
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
