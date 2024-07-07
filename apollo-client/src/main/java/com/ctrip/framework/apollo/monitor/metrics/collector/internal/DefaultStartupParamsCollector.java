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
package com.ctrip.framework.apollo.monitor.metrics.collector.internal;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.monitor.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.monitor.metrics.Metrics;
import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.monitor.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Rawven
 */
public class DefaultStartupParamsCollector extends AbstractMetricsCollector implements
    StartupParamsExposer {

  private final Map<String, Object> map = Maps.newHashMap();

  public DefaultStartupParamsCollector(ConfigUtil configUtil) {
    super("Nop");
    map.put("apollo.access-key.secret", configUtil.getAccessKeySecret());
    map.put("apollo.autoUpdateInjectedSpringProperties",
        configUtil.isAutoUpdateInjectedSpringPropertiesEnabled());
    map.put("apollo.bootstrap.enabled", configUtil.isAutoUpdateInjectedSpringPropertiesEnabled());
    map.put("apollo.bootstrap.namespaces",
        System.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES));
    map.put("apollo.bootstrap.eagerLoad.enabled",
        configUtil.isAutoUpdateInjectedSpringPropertiesEnabled());
    map.put("apollo.override-system-properties", configUtil.isOverrideSystemProperties());
    map.put("apollo.cache-dir", configUtil.getDefaultLocalCacheDir());
    map.put("apollo.cluster", configUtil.getCluster());
    map.put("apollo.config-service",
        System.getProperty(ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE));
    map.put("apollo.client.monitor.form", configUtil.getMonitorForm());
    map.put("apollo.client.monitor.enabled", Metrics.isClientMonitorEnabled());
    map.put("apollo.client.monitor.export-period", configUtil.getMonitorExportPeriod());
    map.put("apollo.meta", configUtil.getMetaServerDomainName());
    map.put("apollo.property.names.cache.enable", configUtil.isPropertyNamesCacheEnabled());
    map.put("apollo.property.order.enable", configUtil.isPropertiesOrderEnabled());
    map.put("app.id", configUtil.getAppId());
    map.put("env", configUtil.getApolloEnv());
  }

  @Override
  public void collect0(MetricsEvent event) {

  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
    return Collections.emptyList();
  }


  @Override
  public String getStartupParams(String key) {
    Object value = map.get(key);
    if (value == null) {
      return "No value";
    }
    return value.toString();
  }


  @Override
  public String getApolloAccessKeySecret() {
    Object value = map.get("apollo.access-key.secret");
    if (value == null) {
      return "No secret";
    }
    return value.toString();
  }

  @Override
  public Boolean getApolloAutoUpdateInjectedSpringProperties() {
    return (Boolean) map.get("apollo.autoUpdateInjectedSpringProperties");
  }

  @Override
  public Boolean getApolloBootstrapEnabled() {
    return (Boolean) map.get("apollo.bootstrap.enabled");
  }

  @Override
  public String getApolloBootstrapNamespaces() {
    return (String) map.get("apollo.bootstrap.namespaces");
  }

  @Override
  public Boolean getApolloBootstrapEagerLoadEnabled() {
    return (Boolean) map.get("apollo.bootstrap.eagerLoad.enabled");
  }

  @Override
  public Boolean getApolloOverrideSystemProperties() {
    return (Boolean) map.get("apollo.override-system-properties");
  }

  @Override
  public String getApolloCacheDir() {
    return map.get("apollo.cache-dir").toString();
  }

  @Override
  public String getApolloCluster() {
    return map.get("apollo.cluster").toString();
  }

  @Override
  public String getApolloConfigService() {
    return map.get("apollo.config-service").toString();
  }

  @Override
  public String getApolloClientMonitorForm() {
    return map.get("apollo.client.monitor.form").toString();
  }

  @Override
  public Boolean getApolloClientMonitorEnabled() {
    return (Boolean) map.get("apollo.client.monitor.enabled");
  }

  @Override
  public long getApolloClientMonitorExportPeriod() {
    return (Long) map.get("apollo.client.monitor.export-period");
  }

  @Override
  public String getApolloMeta() {
    return map.get("apollo.meta").toString();
  }

  @Override
  public Boolean getApolloPropertyNamesCacheEnable() {
    return (Boolean) map.get("apollo.property.names.cache.enable");
  }

  @Override
  public Boolean getApolloPropertyOrderEnable() {
    return (Boolean) map.get("apollo.property.order.enable");
  }

  @Override
  public String getEnv() {
    return map.get("env").toString();
  }

  @Override
  public String getAppId() {
    return map.get("app.id").toString();
  }

  @Override
  public String name() {
    return "StartUpParams";
  }
}
