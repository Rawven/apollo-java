package com.ctrip.framework.apollo.metrics.exposer;

import java.util.List;
import javax.management.MXBean;

@MXBean
public interface NamespaceMetricsExposer {

  String getAllNamespaceUsedTimes();

  String getNamespaceLatestUpdateTime();

  List<String> getAllUsedNamespaceName();
}
