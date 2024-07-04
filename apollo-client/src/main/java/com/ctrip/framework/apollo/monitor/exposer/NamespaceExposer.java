package com.ctrip.framework.apollo.monitor.exposer;

import java.util.List;
import javax.management.MXBean;

/**
 * @author Rawven
 */
@MXBean
public interface NamespaceExposer {

  String getNamespaceReleaseKey(String namespace);

  long getNamespaceUsageCount(String namespace);

  String getNamespaceLatestUpdateTime(String namespace);

  long getNamespaceFirstLoadSpend(String namespace);

  List<String> getNamespaceItemName(String namespace);

  List<String> getAllNamespaceReleaseKey();

  List<String> getAllNamespaceUsageCount();

  List<String> getAllNamespacesLatestUpdateTime();

  List<String> getAllUsedNamespaceName();

  List<String> getAllNamespaceFirstLoadSpend();

  List<String> getAllNamespaceItemName();
}
