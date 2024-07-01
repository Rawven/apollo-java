package com.ctrip.framework.apollo.metrics.exposer;

import java.util.List;
import javax.management.MXBean;

/**
 * @author Rawven
 */
@MXBean
public interface ExceptionMetricsExposer {

  /**
   *  get the namespaces which 404
   */
  String getNamespace404();

  /**
   * get the namespace which timeout
   */
  String getNamespaceTimeout();

  /**
   * get the number of exceptions
   */
  Integer getExceptionNum();

  /**
   * get exception details
   */
  List<String> getExceptionDetails();
}
