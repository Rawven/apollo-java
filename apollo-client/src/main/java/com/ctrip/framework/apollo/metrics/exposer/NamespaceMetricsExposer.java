package com.ctrip.framework.apollo.metrics.exposer;

import java.util.List;
import javax.management.MXBean;

/**
 * @author Rawven
 */
@MXBean
public interface NamespaceMetricsExposer {

  List<String> getAllNamespaceUsageCount();

  List<String> getAllNamespacesLatestUpdateTime();

  List<String> getAllUsedNamespaceName();

  List<String> getAllNamespaceFirstLoadSpend();

  List<String> getAllNamespaceItemName();
}
