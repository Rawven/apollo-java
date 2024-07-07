/*
 * Copyright 2022 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
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
