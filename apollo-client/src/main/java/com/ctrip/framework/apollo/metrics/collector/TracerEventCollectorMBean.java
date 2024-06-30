package com.ctrip.framework.apollo.metrics.collector;

import java.util.List;

public interface TracerEventCollectorMBean {

  String getNamespace404();

  String getNamespaceTimeout();

  Integer getExceptionNum();
  List<String>  getExceptionDetails();
}
