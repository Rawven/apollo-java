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
package com.ctrip.framework.apollo.monitor.metrics;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.util.ConfigUtil;

/**
 * @author Rawven
 */
public abstract class Metrics {

  private static final ConfigUtil m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
  private static volatile MetricsCollectorManager collectorManager;

  static {
    if (m_configUtil.isClientMonitorEnabled()) {
      collectorManager = ApolloInjector.getInstance(MetricsCollectorManager.class);
    }
  }


  public static void push(MetricsEvent event) {
    if (collectorManager != null) {
      for (MetricsCollector collector : collectorManager.getCollectors()) {
        if (collector.isSupport(event)) {
          collector.collect(event);
          return;
        }
      }
    }
  }

}


