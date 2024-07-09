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

import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_ACCESS_KEY_SECRET;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_CACHE_DIR;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_ENABLED;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_EXPORT_PERIOD;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_FORM;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_CLUSTER;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_META;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_OVERRIDE_SYSTEM_PROPERTIES;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_PROPERTY_NAMES_CACHE_ENABLE;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APOLLO_PROPERTY_ORDER_ENABLE;
import static com.ctrip.framework.apollo.core.ApolloClientSystemConsts.APP_ID;
import static com.ctrip.framework.apollo.core.ConfigConsts.APOLLO_AUTO_UPDATE_INJECTED_SPRING_PROPERTIES;
import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED;
import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED;
import static com.ctrip.framework.apollo.spring.config.PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES;

import com.ctrip.framework.apollo.monitor.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.monitor.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Maps;
import java.util.Map;

/**
 * @author Rawven
 */
public class DefaultStartupParamsCollector extends AbstractMetricsCollector implements
    StartupParamsExposer {

  public static final String ENV = "env";
  public static final String STARTUP_PARAMS = "StartUpParams";
  private final Map<String, Object> map = Maps.newHashMap();

  public DefaultStartupParamsCollector(ConfigUtil configUtil) {
    super(STARTUP_PARAMS, "Nop");
    map.put(APOLLO_ACCESS_KEY_SECRET, configUtil.getAccessKeySecret());
    map.put(APOLLO_AUTO_UPDATE_INJECTED_SPRING_PROPERTIES,
        configUtil.isAutoUpdateInjectedSpringPropertiesEnabled());
    map.put(APOLLO_BOOTSTRAP_ENABLED,
        Boolean.parseBoolean(System.getProperty(APOLLO_BOOTSTRAP_ENABLED)));
    map.put(APOLLO_BOOTSTRAP_NAMESPACES, System.getProperty(APOLLO_BOOTSTRAP_NAMESPACES));
    map.put(APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED,
        Boolean.parseBoolean(System.getProperty(APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED)));
    map.put(APOLLO_OVERRIDE_SYSTEM_PROPERTIES, configUtil.isOverrideSystemProperties());
    map.put(APOLLO_CACHE_DIR, configUtil.getDefaultLocalCacheDir());
    map.put(APOLLO_CLUSTER, configUtil.getCluster());
    map.put(APOLLO_CONFIG_SERVICE, System.getProperty(APOLLO_CONFIG_SERVICE));
    map.put(APOLLO_CLIENT_MONITOR_FORM, configUtil.getMonitorForm());
    map.put(APOLLO_CLIENT_MONITOR_ENABLED, configUtil.isClientMonitorEnabled());
    map.put(APOLLO_CLIENT_MONITOR_EXPORT_PERIOD, configUtil.getMonitorExportPeriod());
    map.put(APOLLO_META, configUtil.getMetaServerDomainName());
    map.put(APOLLO_PROPERTY_NAMES_CACHE_ENABLE, configUtil.isPropertyNamesCacheEnabled());
    map.put(APOLLO_PROPERTY_ORDER_ENABLE, configUtil.isPropertiesOrderEnabled());
    map.put(APP_ID, configUtil.getAppId());
    map.put(ENV, configUtil.getApolloEnv());
  }

  @Override
  public void collect0(MetricsEvent event) {

  }

  @Override
  public void export0() {
  }

  @Override
  public boolean isSamplesUpdated() {
    return false;
  }

  @Override
  public String getStartupParams(String key) {
    return map.getOrDefault(key, "").toString();
  }


  @Override
  public String getApolloAccessKeySecret() {
    return map.getOrDefault(APOLLO_ACCESS_KEY_SECRET, "").toString();
  }

  @Override
  public Boolean getApolloAutoUpdateInjectedSpringProperties() {
    return (Boolean) map.get(APOLLO_AUTO_UPDATE_INJECTED_SPRING_PROPERTIES);
  }

  @Override
  public Boolean getApolloBootstrapEnabled() {
    return (Boolean) map.get(APOLLO_BOOTSTRAP_ENABLED);
  }

  @Override
  public String getApolloBootstrapNamespaces() {
    return (String) map.get(APOLLO_BOOTSTRAP_NAMESPACES);
  }

  @Override
  public Boolean getApolloBootstrapEagerLoadEnabled() {
    return (Boolean) map.get(APOLLO_BOOTSTRAP_EAGER_LOAD_ENABLED);
  }

  @Override
  public Boolean getApolloOverrideSystemProperties() {
    return (Boolean) map.get(APOLLO_OVERRIDE_SYSTEM_PROPERTIES);
  }

  @Override
  public String getApolloCacheDir() {
    return map.get(APOLLO_CACHE_DIR).toString();
  }

  @Override
  public String getApolloCluster() {
    return map.get(APOLLO_CLUSTER).toString();
  }

  @Override
  public String getApolloConfigService() {
    return map.get(APOLLO_CONFIG_SERVICE).toString();
  }

  @Override
  public String getApolloClientMonitorForm() {
    return map.get(APOLLO_CLIENT_MONITOR_FORM).toString();
  }

  @Override
  public Boolean getApolloClientMonitorEnabled() {
    return (Boolean) map.get(APOLLO_CLIENT_MONITOR_ENABLED);
  }

  @Override
  public long getApolloClientMonitorExportPeriod() {
    return (Long) map.get(APOLLO_CLIENT_MONITOR_EXPORT_PERIOD);
  }

  @Override
  public String getApolloMeta() {
    return map.get(APOLLO_META).toString();
  }

  @Override
  public Boolean getApolloPropertyNamesCacheEnable() {
    return (Boolean) map.get(APOLLO_PROPERTY_NAMES_CACHE_ENABLE);
  }

  @Override
  public Boolean getApolloPropertyOrderEnable() {
    return (Boolean) map.get(APOLLO_PROPERTY_ORDER_ENABLE);
  }

  @Override
  public String getEnv() {
    return map.get(ENV).toString();
  }

  @Override
  public String getAppId() {
    return map.get(APP_ID).toString();
  }

}
