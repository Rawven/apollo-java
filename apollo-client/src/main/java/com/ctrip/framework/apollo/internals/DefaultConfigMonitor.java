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
package com.ctrip.framework.apollo.internals;

import com.ctrip.framework.apollo.monitor.metrics.collector.internal.DefaultExceptionCollector;
import com.ctrip.framework.apollo.monitor.metrics.collector.internal.DefaultNamespaceCollector;
import com.ctrip.framework.apollo.monitor.metrics.collector.internal.DefaultStartupParamsCollector;
import com.ctrip.framework.apollo.monitor.metrics.collector.internal.DefaultThreadPoolCollector;
import com.ctrip.framework.apollo.monitor.exposer.ExceptionExposer;
import com.ctrip.framework.apollo.monitor.exposer.NamespaceExposer;
import com.ctrip.framework.apollo.monitor.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.monitor.exposer.ThreadPoolExposer;
import com.ctrip.framework.apollo.monitor.metrics.reporter.MetricsReporter;

/**
 * exposes all collected data through ConfigService
 *
 * @author Rawven
 */
public class DefaultConfigMonitor implements ConfigMonitor {

  private MetricsReporter reporter;
  private ThreadPoolExposer memoryStatusExposer;
  private ExceptionExposer tracerEventExposer;
  private NamespaceExposer namespaceExposer;
  private StartupParamsExposer startupParamsExposer;

  @Override
  public ThreadPoolExposer getMemoryStatusExposer() {
    return memoryStatusExposer;
  }

  @Override
  public ExceptionExposer getTracerEventExposer() {
    return tracerEventExposer;
  }

  @Override
  public NamespaceExposer getClientEventExposer() {
    return namespaceExposer;
  }

  @Override
  public StartupParamsExposer getStartupParamsExposer() {
    return startupParamsExposer;
  }

  @Override
  public String getDataWithCurrentMonitoringSystemFormat() {
    if (reporter == null) {
      return "No MonitoringSystem Use";
    }
    return reporter.response();
  }

  public void init(DefaultNamespaceCollector ClientEventCollector,
      DefaultThreadPoolCollector threadPoolCollector,
      DefaultExceptionCollector defaultTracerEventCollector,
      DefaultStartupParamsCollector startupCollector,
      MetricsReporter reporter) {
    this.namespaceExposer = ClientEventCollector;
    this.memoryStatusExposer = threadPoolCollector;
    this.tracerEventExposer = defaultTracerEventCollector;
    this.startupParamsExposer = startupCollector;
    this.reporter = reporter;
  }
}
