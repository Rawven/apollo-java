package com.ctrip.framework.apollo.metrics.exposer;

import java.util.List;
import javax.management.MXBean;

@MXBean
public interface ExceptionMetricsExposer {

  String getNamespace404();

  String getNamespaceTimeout();

  Integer getExceptionNum();
  List<String>  getExceptionDetails();
}
