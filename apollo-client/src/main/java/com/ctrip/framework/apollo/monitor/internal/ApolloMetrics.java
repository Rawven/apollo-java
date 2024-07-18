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
package com.ctrip.framework.apollo.monitor.internal;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.internals.ConfigMonitorInitializer;
import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.monitor.internal.model.MetricsEvent;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.ctrip.framework.apollo.util.ConfigUtil;
import org.slf4j.Logger;

/**
 * @author Rawven
 */
public abstract class ApolloMetrics {

  protected static final Logger log = DeferredLoggerFactory.getLogger(
      ApolloMetrics.class);

  private static final ConfigUtil m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
  private static final MetricsCollectorManager COLLECTOR_MANAGER = ApolloInjector.getInstance(
      MetricsCollectorManager.class);

  static {
    if (m_configUtil.isClientMonitorEnabled()) {
      try {
        ConfigMonitorInitializer.initializeMonitorSystem();
      } catch (Throwable ex) {
        Throwable realCause = ex.getCause().getCause().getCause();
        Tracer.logError(realCause);
        throw new ApolloConfigException(realCause.getMessage(), realCause);
      }
    }
  }


  public static void push(MetricsEvent event) {
    if (COLLECTOR_MANAGER != null) {
      for (MetricsCollector collector : COLLECTOR_MANAGER.getCollectors()) {
        if (collector.isSupport(event)) {
          collector.collect(event);
          return;
        }
      }
    }
  }

}


