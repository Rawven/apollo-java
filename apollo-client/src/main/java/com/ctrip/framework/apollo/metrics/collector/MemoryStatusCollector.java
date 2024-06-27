package com.ctrip.framework.apollo.metrics.collector;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.util.ConfigUtil;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rawven
 */
public class MemoryStatusCollector implements MetricsCollector {

  private final ConfigUtil m_configUtil;
  private final Map<String, Config> m_configs;
  private final Map<String, Object> m_configLocks;
  private final Map<String, ConfigFile> m_configFiles;
  private final Map<String, Object> m_configFileLocks;
  //TODO 线程池监控 学习

  public MemoryStatusCollector(Map<String, Config> m_configs,
      Map<String, Object> m_configLocks,
      Map<String, ConfigFile> m_configFiles,
      Map<String, Object> m_configFileLocks, ConfigUtil m_configUtil) {
    this.m_configs = m_configs;
    this.m_configLocks = m_configLocks;
    this.m_configFiles = m_configFiles;
    this.m_configFileLocks = m_configFileLocks;
    this.m_configUtil = m_configUtil;
  }

  public List<String> getAllUsedNamespaceName() {
    ArrayList<String> namespaces = Lists.newArrayList();
    m_configs.forEach((k, v) -> namespaces.add(k));
    return namespaces;
  }



  public String getAppId() {
    return m_configUtil.getAppId();
  }

  public String getCluster() {
    return m_configUtil.getCluster();
  }

  public String getApolloEnv() {
    return m_configUtil.getApolloEnv().name();
  }

  @Override
  public boolean isSupport(String tag) {
    return false;
  }

  @Override
  public void collect(MetricsEvent event) {
    return;
  }

  @Override
  public boolean isSamplesUpdated() {
    //TODO
    return true;
  }

  @Override
  public List<MetricsSample> export() {
    List<MetricsSample> samples = new ArrayList<>();
    HashMap<String, String> tag = new HashMap<>(3);
    tag.put("appId", getAppId());
    tag.put("cluster", getCluster());
    tag.put("env", getApolloEnv());
    samples.add(new GaugeMetricsSample<>("startup_parameters", 1, value -> 1, tag));
    return samples;
  }
}
