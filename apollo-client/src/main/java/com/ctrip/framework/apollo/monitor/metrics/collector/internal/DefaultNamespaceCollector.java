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


import static com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample.intConverter;
import static com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample.longConverter;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.monitor.exposer.NamespaceExposer;
import com.ctrip.framework.apollo.monitor.metrics.MetricsConstant;
import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.monitor.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import org.slf4j.Logger;

/**
 * @author Rawven
 */
public class DefaultNamespaceCollector extends AbstractMetricsCollector implements
    NamespaceExposer {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
  public static final String NAMESPACE_METRICS = "namespaceStatusMetrics";
  public static final String NAMESPACE_UPDATE_TIME = "namespace_latest_update_time";
  public static final String NAMESPACE_FIRST_LOAD_SPEND = "namespace_first_load_spend_time";
  public static final String NAMESPACE_USAGE_COUNT = "namespace_usage_count";
  public static final String NAMESPACE_RELEASE_KEY = "namespace_release_key";
  public static final String NAMESPACE_ITEM_NUM = "namespace_item_num";
  public static final String CONFIG_FILE_NUM = "config_file_num";
  public static final String NAMESPACE_LATEST_RELEASE_KEY = "namespace_latest_release_key";
  public static final String NAMESPACE_NOT_FOUND = "namespace_not_found";
  public static final String NAMESPACE_TIMEOUT = "namespace_timeout";
  private static final Logger logger = DeferredLoggerFactory.getLogger(
      DefaultNamespaceCollector.class);
  private final Map<String, Config> m_configs;
  private final Map<String, Object> m_configLocks;
  private final Map<String, ConfigFile> m_configFiles;
  private final Map<String, Object> m_configFileLocks;
  private final Map<String, NamespaceMetrics> namespaces = Maps.newConcurrentMap();
  private final List<String> namespace404 = Lists.newCopyOnWriteArrayList();
  private final List<String> namespaceTimeout = Lists.newCopyOnWriteArrayList();

  public DefaultNamespaceCollector(Map<String, Config> m_configs,
      Map<String, Object> m_configLocks,
      Map<String, ConfigFile> m_configFiles,
      Map<String, Object> m_configFileLocks) {
    super(NAMESPACE_METRICS, NAMESPACE_METRICS);
    this.m_configs = m_configs;
    this.m_configLocks = m_configLocks;
    this.m_configFiles = m_configFiles;
    this.m_configFileLocks = m_configFileLocks;
  }


  @Override
  public void collect0(MetricsEvent event) {
    String namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
    NamespaceMetrics namespaceMetrics = namespaces.computeIfAbsent(namespace,
        k -> new NamespaceMetrics());
    switch (event.getName()) {
      case NAMESPACE_USAGE_COUNT:
        namespaceMetrics.incrementUsageCount();
        break;
      case NAMESPACE_UPDATE_TIME:
        long updateTime = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
        namespaceMetrics.setLatestUpdateTime(updateTime);
        break;
      case NAMESPACE_FIRST_LOAD_SPEND:
        long firstLoadSpendTime = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
        namespaceMetrics.setFirstLoadSpend(firstLoadSpendTime);
        break;
      case NAMESPACE_RELEASE_KEY:
        String releaseKey = event.getAttachmentValue(NAMESPACE_LATEST_RELEASE_KEY);
        namespaceMetrics.setReleaseKey(releaseKey);
        break;
      case NAMESPACE_TIMEOUT:
        namespaceTimeout.add(namespace);
        break;
      case NAMESPACE_NOT_FOUND:
        namespace404.add(namespace);
        break;
      default:
        logger.warn("Unknown event: {}", event);
        break;
    }
  }

  @Override
  public void export0() {
    namespaces.forEach((k, v) -> {
      updateCounterSample(NAMESPACE_USAGE_COUNT, k, v.getUsageCount());
      updateGaugeSample(NAMESPACE_FIRST_LOAD_SPEND, k, v.getFirstLoadSpend(),
          longConverter);
      updateGaugeSample(NAMESPACE_UPDATE_TIME, k, v.getLatestUpdateTime(),
          longConverter);
      updateGaugeSample(NAMESPACE_ITEM_NUM, k, m_configs.get(k).getPropertyNames().size(),
          intConverter);
      updateGaugeSample(CONFIG_FILE_NUM, k, m_configFiles.size(), intConverter);
      updateGaugeSample(NAMESPACE_NOT_FOUND, k, namespace404.size(), intConverter);
      updateGaugeSample(NAMESPACE_TIMEOUT, k, namespaceTimeout.size(), intConverter);
    });
  }

  private void updateCounterSample(String key, String namespace, double value) {
    String mapKey = namespace + key;
    if (!counterSamples.containsKey(mapKey)) {
      counterSamples.put(mapKey,
          CounterMetricsSample.builder().putTag(NAMESPACE_METRICS, namespace).name(key).value(0)
              .build());
    }
    counterSamples.get(mapKey).resetValue(value);
  }

  @SuppressWarnings("unchecked")
  private void updateGaugeSample(String key, String namespace, Object value,
      ToDoubleFunction<Object> applyFunction) {
    String mapKey = namespace + key;
    if (!gaugeSamples.containsKey(mapKey)) {
      gaugeSamples.put(mapKey,
          GaugeMetricsSample.builder().putTag(NAMESPACE_METRICS, namespace).name(key).value(0)
              .apply(applyFunction).build());
    }
    gaugeSamples.get(mapKey).setValue(value);
  }

  @Override
  public String getNamespaceReleaseKey(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? null : namespaceMetrics.getReleaseKey();
  }

  @Override
  public long getNamespaceUsageCount(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? 0 : namespaceMetrics.getUsageCount();
  }

  @Override
  public String getNamespaceLatestUpdateTime(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? null
        : DATE_FORMATTER.format(Instant.ofEpochMilli(namespaceMetrics.getLatestUpdateTime()));
  }

  @Override
  public long getNamespaceFirstLoadSpend(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? 0 : namespaceMetrics.getFirstLoadSpend();
  }

  @Override
  public String getNamespace404() {
    return namespace404.toString();
  }

  @Override
  public String getNamespaceTimeout() {
    return namespaceTimeout.toString();
  }

  @Override
  public List<String> getNamespaceItemName(String namespace) {
    Config config = m_configs.get(namespace);
    return config == null ? Collections.emptyList() : new ArrayList<>(config.getPropertyNames());
  }

  @Override
  public List<String> getAllNamespaceReleaseKey() {
    List<String> releaseKeys = Lists.newArrayList();
    namespaces.forEach((k, v) -> releaseKeys.add(k + ":" + v.getReleaseKey()));
    return releaseKeys;
  }

  @Override
  public List<String> getAllNamespaceUsageCount() {
    List<String> usedTimes = Lists.newArrayList();
    namespaces.forEach((k, v) -> usedTimes.add(k + ":" + v.getUsageCount()));
    return usedTimes;
  }

  @Override
  public List<String> getAllNamespacesLatestUpdateTime() {
    List<String> latestUpdateTimes = Lists.newArrayList();
    namespaces.forEach((k, v) -> latestUpdateTimes.add(
        k + ":" + DATE_FORMATTER.format(Instant.ofEpochMilli(v.getLatestUpdateTime()))));
    return latestUpdateTimes;
  }

  @Override
  public List<String> getAllUsedNamespaceName() {
    ArrayList<String> namespaces = Lists.newArrayList();
    m_configs.forEach((k, v) -> namespaces.add(k));
    return namespaces;
  }

  @Override
  public List<String> getAllNamespaceFirstLoadSpend() {
    List<String> firstLoadSpends = Lists.newArrayList();
    namespaces.forEach((k, v) -> firstLoadSpends.add(
        k + ":" + v.getFirstLoadSpend()));
    return firstLoadSpends;
  }

  @Override
  public List<String> getAllNamespaceItemName() {
    List<String> namespaceItems = Lists.newArrayList();
    m_configs.forEach((k, v) -> namespaceItems.add(v.getPropertyNames().toString()));
    return namespaceItems;
  }

  public static class NamespaceMetrics {

    private int usageCount;
    private long firstLoadSpend;
    private long latestUpdateTime;
    private String releaseKey = "default";

    public String getReleaseKey() {
      return releaseKey;
    }

    public void setReleaseKey(String releaseKey) {
      this.releaseKey = releaseKey;
    }

    @Override
    public String toString() {
      return "NamespaceMetrics{" +
          "usageCount=" + usageCount +
          ", firstLoadSpend=" + firstLoadSpend +
          ", latestUpdateTime=" + latestUpdateTime +
          ", releaseKey='" + releaseKey + '\'' +
          '}';
    }

    public int getUsageCount() {
      return usageCount;
    }

    public void incrementUsageCount() {
      this.usageCount++;
    }

    public long getFirstLoadSpend() {
      return firstLoadSpend;
    }

    public void setFirstLoadSpend(long firstLoadSpend) {
      this.firstLoadSpend = firstLoadSpend;
    }

    public long getLatestUpdateTime() {
      return latestUpdateTime;
    }

    public void setLatestUpdateTime(long latestUpdateTime) {
      this.latestUpdateTime = latestUpdateTime;
    }
  }

}
