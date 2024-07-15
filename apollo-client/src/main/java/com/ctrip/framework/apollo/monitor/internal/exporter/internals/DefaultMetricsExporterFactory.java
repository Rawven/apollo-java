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
package com.ctrip.framework.apollo.monitor.internal.exporter.internals;

import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultMetricsCollectorManager;
import com.ctrip.framework.apollo.monitor.internal.exporter.MetricsExporter;
import com.ctrip.framework.apollo.monitor.internal.exporter.MetricsExporterFactory;
import com.ctrip.framework.apollo.tracer.Tracer;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.ctrip.framework.foundation.internals.ServiceBootstrap;
import java.util.List;
import org.slf4j.Logger;

public class DefaultMetricsExporterFactory implements MetricsExporterFactory {

  private static final Logger logger = DeferredLoggerFactory.getLogger(
      DefaultMetricsCollectorManager.class);
  private final ConfigUtil m_configUtil;

  public DefaultMetricsExporterFactory() {
    m_configUtil = ApolloInjector.getInstance(ConfigUtil.class);
  }

  @Override
  public MetricsExporter getMetricsReporter(List<MetricsCollector> collectors) {
    //init reporter
    String form = m_configUtil.getMonitorForm();
    MetricsExporter reporter = null;
    if (form != null) {
      List<MetricsExporter> metricsExporters = ServiceBootstrap.loadAllOrdered(
          MetricsExporter.class);
      for (MetricsExporter metricsExporter : metricsExporters) {
        if (metricsExporter.isSupport(form)) {
          reporter = metricsExporter;
          reporter.init(collectors, m_configUtil.getMonitorExportPeriod());
          break;
        }
      }
      if (reporter == null) {
        ApolloConfigException exception = new ApolloConfigException(
            "Error initializing MetricsReporter for form: " + form);
        logger.error(
            "Error initializing MetricsReporter for protocol: {},Please check whether necessary dependencies are imported, such as apollo-plugin-client-prometheus",
            form, exception);
        Tracer.logError(exception);
        throw exception;
      }
    }
    return reporter;
  }
}
