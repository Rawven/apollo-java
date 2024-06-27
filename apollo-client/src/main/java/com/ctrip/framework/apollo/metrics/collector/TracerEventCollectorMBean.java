package com.ctrip.framework.apollo.metrics.collector;

public interface TracerEventCollectorMBean {

  String getNamespace404();

  String getNamespaceTimeout();

  Integer getExceptionNum();
}
