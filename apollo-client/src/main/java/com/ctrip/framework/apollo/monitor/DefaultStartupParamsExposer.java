package com.ctrip.framework.apollo.monitor;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.metrics.Metrics;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.monitor.exposer.StartupParamsExposer;
import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.ctrip.framework.apollo.util.ConfigUtil;
import java.util.Collections;
import java.util.List;

/**
 * @author Rawven
 */
public class DefaultStartupParamsExposer extends AbstractMetricsCollector implements
    StartupParamsExposer {

  public static final String APPID = "appId";
  public static final String CLUSTER = "cluster";
  public static final String ENV = "env";
  public static final String STARTUP_PARAMETERS = "startup_parameters";
  private final ConfigUtil configUtil;

  public DefaultStartupParamsExposer(ConfigUtil configUtil) {
    super("Nop");
    this.configUtil = configUtil;
  }

  @Override
  public void collect0(MetricsEvent event) {

  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
//    samples.add(GaugeMetricsSample.builder().name(STARTUP_PARAMETERS).value(1)
//        .apply(value -> 1)
//        .putTag(APPID, getAppId())
//        .putTag(CLUSTER, getApolloCluster())
//        .putTag(ENV, getEnv()).build());
    return Collections.emptyList();
  }

  @Override
  public String getApolloAccessKeySecret() {
    return configUtil.getAccessKeySecret();
  }

  @Override
  public Boolean getApolloAutoUpdateInjectedSpringProperties() {
    return configUtil.isAutoUpdateInjectedSpringPropertiesEnabled();
  }

  @Override
  public Boolean getApolloBootstrapEnabled() {
    return configUtil.isAutoUpdateInjectedSpringPropertiesEnabled();
  }

  @Override
  public String getApolloBootstrapNamespaces() {
    return System.getProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_NAMESPACES);
  }

  @Override
  public Boolean getApolloBootstrapEagerLoadEnabled() {
    return configUtil.isAutoUpdateInjectedSpringPropertiesEnabled();
  }

  @Override
  public Boolean getApolloOverrideSystemProperties() {
    return configUtil.isOverrideSystemProperties();
  }

  @Override
  public String getApolloCacheDir() {
    return configUtil.getDefaultLocalCacheDir();
  }

  @Override
  public String getApolloCluster() {
    return configUtil.getCluster();
  }

  @Override
  public String getApolloConfigService() {
    return System.getProperty(ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE);
  }

  @Override
  public String getApolloClientMonitorForm() {
    return configUtil.getMonitorForm();
  }

  @Override
  public Boolean getApolloClientMonitorEnabled() {
    return Metrics.isClientMonitorEnabled();
  }

  @Override
  public long getApolloClientMonitorExportPeriod() {
    return configUtil.getMonitorExportPeriod();
  }

  @Override
  public String getApolloMeta() {
    return configUtil.getMetaServerDomainName();
  }

  @Override
  public Boolean getApolloPropertyNamesCacheEnable() {
    return configUtil.isPropertyNamesCacheEnabled();
  }

  @Override
  public Boolean getApolloPropertyOrderEnable() {
    return configUtil.isPropertiesOrderEnabled();
  }

  @Override
  public String getEnv() {
    return configUtil.getApolloEnv().toString();
  }

  @Override
  public String getAppId() {
    return configUtil.getAppId();
  }

  @Override
  public String name() {
    return "StartUpParams";
  }
}
