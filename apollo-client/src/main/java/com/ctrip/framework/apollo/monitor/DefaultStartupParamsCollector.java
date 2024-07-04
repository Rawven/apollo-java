package com.ctrip.framework.apollo.monitor;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.metrics.Metrics;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.monitor.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Rawven
 */
public class DefaultStartupParamsCollector extends AbstractMetricsCollector implements
    StartupParamsExposer {

  private final Map<String, Supplier<Object>> map = Maps.newHashMap();

  public DefaultStartupParamsCollector(ConfigUtil configUtil) {
    super("Nop");
    map.put("apollo.access-key.secret", configUtil::getAccessKeySecret);
    map.put("apollo.autoUpdateInjectedSpringProperties",
        configUtil::isAutoUpdateInjectedSpringPropertiesEnabled);
    map.put("apollo.bootstrap.enabled", configUtil::isAutoUpdateInjectedSpringPropertiesEnabled);
    map.put("apollo.bootstrap.namespaces",
        () -> System.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES));
    map.put("apollo.bootstrap.eagerLoad.enabled",
        configUtil::isAutoUpdateInjectedSpringPropertiesEnabled);
    map.put("apollo.override-system-properties", configUtil::isOverrideSystemProperties);
    map.put("apollo.cache-dir", configUtil::getDefaultLocalCacheDir);
    map.put("apollo.cluster", configUtil::getCluster);
    map.put("apollo.config-service",
        () -> System.getProperty(ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE));
    map.put("apollo.client.monitor.form", configUtil::getMonitorForm);
    map.put("apollo.client.monitor.enabled", Metrics::isClientMonitorEnabled);
    map.put("apollo.client.monitor.export-period", configUtil::getMonitorExportPeriod);
    map.put("apollo.meta", configUtil::getMetaServerDomainName);
    map.put("apollo.property.names.cache.enable", configUtil::isPropertyNamesCacheEnabled);
    map.put("apollo.property.order.enable", configUtil::isPropertiesOrderEnabled);
    map.put("app.id", configUtil::getAppId);
    map.put("env", configUtil::getApolloEnv);
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
    Object value = map.get(key).get();
    if (value == null) {
      return "No value";
    }
    return value.toString();
  }


  @Override
  public String getApolloAccessKeySecret() {
    Object value = map.get("apollo.access-key.secret").get();
    if (value == null) {
      return "No secret";
    }
    return value.toString();
  }

  @Override
  public Boolean getApolloAutoUpdateInjectedSpringProperties() {
    return (Boolean) map.get("apollo.autoUpdateInjectedSpringProperties").get();
  }

  @Override
  public Boolean getApolloBootstrapEnabled() {
    return (Boolean) map.get("apollo.bootstrap.enabled").get();
  }

  @Override
  public String getApolloBootstrapNamespaces() {
    return (String) map.get("apollo.bootstrap.namespaces").get();
  }

  @Override
  public Boolean getApolloBootstrapEagerLoadEnabled() {
    return (Boolean) map.get("apollo.bootstrap.eagerLoad.enabled").get();
  }

  @Override
  public Boolean getApolloOverrideSystemProperties() {
    return (Boolean) map.get("apollo.override-system-properties").get();
  }

  @Override
  public String getApolloCacheDir() {
    return map.get("apollo.cache-dir").get().toString();
  }

  @Override
  public String getApolloCluster() {
    return map.get("apollo.cluster").get().toString();
  }

  @Override
  public String getApolloConfigService() {
    return map.get("apollo.config-service").get().toString();
  }

  @Override
  public String getApolloClientMonitorForm() {
    return map.get("apollo.client.monitor.form").get().toString();
  }

  @Override
  public Boolean getApolloClientMonitorEnabled() {
    return (Boolean) map.get("apollo.client.monitor.enabled").get();
  }

  @Override
  public long getApolloClientMonitorExportPeriod() {
    return (Long) map.get("apollo.client.monitor.export-period").get();
  }

  @Override
  public String getApolloMeta() {
    return map.get("apollo.meta").get().toString();
  }

  @Override
  public Boolean getApolloPropertyNamesCacheEnable() {
    return (Boolean) map.get("apollo.property.names.cache.enable").get();
  }

  @Override
  public Boolean getApolloPropertyOrderEnable() {
    return (Boolean) map.get("apollo.property.order.enable").get();
  }

  @Override
  public String getEnv() {
    return map.get("env").get().toString();
  }

  @Override
  public String getAppId() {
    return map.get("app.id").get().toString();
  }

  @Override
  public String name() {
    return "StartUpParams";
  }
}
