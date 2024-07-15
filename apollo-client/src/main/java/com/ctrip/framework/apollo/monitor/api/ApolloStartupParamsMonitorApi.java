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
package com.ctrip.framework.apollo.monitor.api;

import javax.management.MXBean;

/**
 * @author Rawven
 */
@MXBean
public interface ApolloStartupParamsMonitorApi {

  String getStartupParams(String key);

  String getApolloAccessKeySecret();

  Boolean getApolloAutoUpdateInjectedSpringProperties();

  Boolean getApolloBootstrapEnabled();

  String getApolloBootstrapNamespaces();

  Boolean getApolloBootstrapEagerLoadEnabled();

  Boolean getApolloOverrideSystemProperties();

  String getApolloCacheDir();

  String getApolloCluster();

  String getApolloConfigService();

  String getApolloClientMonitorForm();

  Boolean getApolloClientMonitorEnabled();

  long getApolloClientMonitorExportPeriod();

  String getApolloMeta();

  Boolean getApolloPropertyNamesCacheEnable();

  Boolean getApolloPropertyOrderEnable();

  String getEnv();

  String getAppId();
}
