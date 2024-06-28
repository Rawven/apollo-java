package com.ctrip.framework.apollo.metrics.collector;

public interface ClientEventCollectorMBean {

  String getAllNamespaceUsedTimes();

  String getNamespaceLatestUpdateTime();
}
