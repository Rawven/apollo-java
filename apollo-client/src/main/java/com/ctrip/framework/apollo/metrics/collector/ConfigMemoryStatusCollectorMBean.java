package com.ctrip.framework.apollo.metrics.collector;

import java.util.List;

public interface ConfigMemoryStatusCollectorMBean {


  List<String> getAllUsedNamespaceName();

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

  String getAppId();

  String getCluster();

  String getApolloEnv();
}
