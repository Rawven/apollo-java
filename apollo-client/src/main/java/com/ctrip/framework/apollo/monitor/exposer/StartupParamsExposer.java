package com.ctrip.framework.apollo.monitor.exposer;

import javax.management.MXBean;

/**
 * @author Rawven
 */
@MXBean
public interface StartupParamsExposer {

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
