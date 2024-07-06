package com.ctrip.framework.apollo.monitor.exposer;

import javax.management.MXBean;

/**
 * @author Rawven
 */
@MXBean
public interface ThreadPoolExposer {


  int getRemoteConfigRepositoryThreadPoolActiveCount();

  int getRemoteConfigRepositoryThreadPoolQueueSize();

  int getRemoteConfigRepositoryThreadPoolCorePoolSize();

  int getRemoteConfigRepositoryThreadPoolMaximumPoolSize();

  int getRemoteConfigRepositoryThreadPoolPoolSize();

  long getRemoteConfigRepositoryThreadPoolTaskCount();

  long getRemoteConfigRepositoryThreadPoolCompletedTaskCount();

  int getRemoteConfigRepositoryThreadPoolLargestPoolSize();

  int getRemoteConfigRepositoryThreadPoolRemainingCapacity();

  double getRemoteConfigRepositoryThreadPoolCurrentLoad();


  int getAbstractConfigThreadPoolActiveCount();

  int getAbstractConfigThreadPoolQueueSize();

  int getAbstractConfigThreadPoolCorePoolSize();

  int getAbstractConfigThreadPoolMaximumPoolSize();

  int getAbstractConfigThreadPoolPoolSize();

  long getAbstractConfigThreadPoolTaskCount();

  long getAbstractConfigThreadPoolCompletedTaskCount();

  int getAbstractConfigThreadPoolLargestPoolSize();

  int getAbstractConfigThreadPoolRemainingCapacity();

  double getAbstractConfigThreadPoolCurrentLoad();


  int getAbstractConfigFileThreadPoolActiveCount();

  int getAbstractConfigFileThreadPoolQueueSize();

  int getAbstractConfigFileThreadPoolCorePoolSize();

  int getAbstractConfigFileThreadPoolMaximumPoolSize();

  int getAbstractConfigFileThreadPoolPoolSize();

  long getAbstractConfigFileThreadPoolTaskCount();

  long getAbstractConfigFileThreadPoolCompletedTaskCount();

  int getAbstractConfigFileThreadPoolLargestPoolSize();

  int getAbstractConfigFileThreadPoolRemainingCapacity();

  double getAbstractConfigFileThreadPoolCurrentLoad();

}
