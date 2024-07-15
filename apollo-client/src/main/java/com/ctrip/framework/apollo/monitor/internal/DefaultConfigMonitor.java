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

import com.ctrip.framework.apollo.monitor.api.ApolloExceptionMonitorApi;
import com.ctrip.framework.apollo.monitor.api.ApolloNamespaceMonitorApi;
import com.ctrip.framework.apollo.monitor.api.ApolloRunningParamsMonitorApi;
import com.ctrip.framework.apollo.monitor.api.ApolloThreadPoolMonitorApi;
import com.ctrip.framework.apollo.monitor.api.ConfigMonitor;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloExceptionCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloNamespaceCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloRunningParamsCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.internal.DefaultApolloThreadPoolCollector;
import com.ctrip.framework.apollo.monitor.internal.exporter.MetricsExporter;

/**
 * exposes all collected data through ConfigService
 *
 * @author Rawven
 */
public class DefaultConfigMonitor implements ConfigMonitor {

  private MetricsExporter reporter;
  private ApolloThreadPoolMonitorApi memoryStatusExposer;
  private ApolloExceptionMonitorApi tracerEventExposer;
  private ApolloNamespaceMonitorApi apolloNamespaceMonitorApi;
  private ApolloRunningParamsMonitorApi apolloRunningParamsMonitorApi;

  @Override
  public ApolloThreadPoolMonitorApi getMemoryStatusExposer() {
    return memoryStatusExposer;
  }

  @Override
  public ApolloExceptionMonitorApi getTracerEventExposer() {
    return tracerEventExposer;
  }

  @Override
  public ApolloNamespaceMonitorApi getClientEventExposer() {
    return apolloNamespaceMonitorApi;
  }

  @Override
  public ApolloRunningParamsMonitorApi getStartupParamsExposer() {
    return apolloRunningParamsMonitorApi;
  }

  @Override
  public String getDataWithCurrentMonitoringSystemFormat() {
    if (reporter == null) {
      return "No MonitoringSystem Use";
    }
    return reporter.response();
  }

  public void init(DefaultApolloNamespaceCollector ClientEventCollector,
      DefaultApolloThreadPoolCollector threadPoolCollector,
      DefaultApolloExceptionCollector defaultTracerEventCollector,
      DefaultApolloRunningParamsCollector startupCollector,
      MetricsExporter reporter) {
    this.apolloNamespaceMonitorApi = ClientEventCollector;
    this.memoryStatusExposer = threadPoolCollector;
    this.tracerEventExposer = defaultTracerEventCollector;
    this.apolloRunningParamsMonitorApi = startupCollector;
    this.reporter = reporter;
  }
}
