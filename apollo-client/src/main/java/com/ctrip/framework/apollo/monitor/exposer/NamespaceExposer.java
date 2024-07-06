package com.ctrip.framework.apollo.monitor.exposer;

import java.util.List;
import javax.management.MXBean;

/**
 * @author Rawven
 */
@MXBean
public interface NamespaceExposer {

  /**
   * get namespace release key
   */
  String getNamespaceReleaseKey(String namespace);

  /**
   * get namespace usage count
   */
  long getNamespaceUsageCount(String namespace);

  /**
   * get namespace latest update time
   */
  String getNamespaceLatestUpdateTime(String namespace);

  /**
   * get time which namespace first load spend
   */
  long getNamespaceFirstLoadSpend(String namespace);

  List<String> getNamespaceItemName(String namespace);

  List<String> getAllNamespaceReleaseKey();

  List<String> getAllNamespaceUsageCount();

  List<String> getAllNamespacesLatestUpdateTime();

  List<String> getAllUsedNamespaceName();

  List<String> getAllNamespaceFirstLoadSpend();

  List<String> getAllNamespaceItemName();
}
