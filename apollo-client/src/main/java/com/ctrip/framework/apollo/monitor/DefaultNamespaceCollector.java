package com.ctrip.framework.apollo.monitor;


import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.metrics.MetricsConstant;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.monitor.exposer.NamespaceExposer;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * @author Rawven
 */
public class DefaultNamespaceCollector extends AbstractMetricsCollector implements
    NamespaceExposer {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.
      ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
  public static final String NAMESPACE = "namespace";
  public static final String NAMESPACE_UPDATE_TIME = "namespace_update_time";
  public static final String NAMESPACE_FIRST_LOAD_SPEND = "namespace_firstLoadSpend";
  public static final String NAMESPACE_USAGE_COUNT = "namespace_usage_count";
  public static final String NAMESPACE_RELEASE_KEY = "namespace_release_key";
  private static final Logger logger = DeferredLoggerFactory.getLogger(
      DefaultNamespaceCollector.class);
  private final Map<String, Config> m_configs;
  private final Map<String, Object> m_configLocks;
  private final Map<String, ConfigFile> m_configFiles;
  private final Map<String, Object> m_configFileLocks;
  //TODO 对于数量恒定的namespace(用户正常配置使用情况下) 使用ConcurrentHashMap 内存固定不用考虑OOM
  private final Map<String, NamespaceMetrics> namespaces = Maps.newConcurrentMap();

  public DefaultNamespaceCollector(Map<String, Config> m_configs,
      Map<String, Object> m_configLocks,
      Map<String, ConfigFile> m_configFiles,
      Map<String, Object> m_configFileLocks) {
    super(NAMESPACE);
    this.m_configs = m_configs;
    this.m_configLocks = m_configLocks;
    this.m_configFiles = m_configFiles;
    this.m_configFileLocks = m_configFileLocks;
  }


  public Map<String, NamespaceMetrics> getAllNamespaceMetrics() {
    return namespaces;
  }

  @Override
  public String getNamespaceReleaseKey(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? null : namespaceMetrics.releaseKey;
  }

  @Override
  public long getNamespaceUsageCount(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? 0 : namespaceMetrics.usageCount;
  }

  @Override
  public String getNamespaceLatestUpdateTime(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? null
        : DATE_FORMATTER.format(Instant.ofEpochMilli(namespaceMetrics.latestUpdateTime));
  }

  @Override
  public long getNamespaceFirstLoadSpend(String namespace) {
    NamespaceMetrics namespaceMetrics = namespaces.get(namespace);
    return namespaceMetrics == null ? 0 : namespaceMetrics.firstLoadSpend;
  }

  @Override
  public List<String> getNamespaceItemName(String namespace) {
    Config config = m_configs.get(namespace);
    return config == null ? Collections.emptyList() : new ArrayList<>(config.getPropertyNames());
  }

  @Override
  public List<String> getAllNamespaceReleaseKey() {
    List<String> releaseKeys = Lists.newArrayList();
    namespaces.forEach((k, v) -> releaseKeys.add(k + ":" + v.releaseKey));
    return releaseKeys;
  }

  @Override
  public List<String> getAllNamespaceUsageCount() {
    List<String> usedTimes = Lists.newArrayList();
    namespaces.forEach((k, v) -> usedTimes.add(k + ":" + v.usageCount));
    return usedTimes;
  }

  @Override
  public List<String> getAllNamespacesLatestUpdateTime() {
    List<String> latestUpdateTimes = Lists.newArrayList();
    namespaces.forEach((k, v) -> latestUpdateTimes.add(
        k + ":" + DATE_FORMATTER.format(Instant.ofEpochMilli(v.latestUpdateTime))));
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
        k + ":" + v.firstLoadSpend));
    return firstLoadSpends;
  }

  @Override
  public List<String> getAllNamespaceItemName() {
    List<String> namespaceItems = Lists.newArrayList();
    m_configs.forEach((k, v) -> namespaceItems.add(v.getPropertyNames().toString()));
    return namespaceItems;
  }

  @Override
  public void collect0(MetricsEvent event) {
    String namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
    NamespaceMetrics namespaceMetrics = namespaces.computeIfAbsent(namespace,
        k -> new NamespaceMetrics());
    switch (event.getName()) {
      case NAMESPACE_USAGE_COUNT:
        namespaceMetrics.incrementUsedTime();
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
        String releaseKey = event.getAttachmentValue("releaseKey");
        namespaceMetrics.setReleaseKey(releaseKey);
        break;
      default:
        logger.warn("Unknown event: {}", event);
        break;
    }
  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
    namespaces.forEach((k, v) -> {
      samples.add(
          GaugeMetricsSample.builder().name("namespace_usage_count").value(v.usageCount)
              .putTag("namespace", k).apply(value -> (int) value).build());
      samples.add(GaugeMetricsSample.builder().name("namespace_first_load_spend")
          .value(v.firstLoadSpend).apply(value -> (long) value).putTag("namespace", k).build());
      samples.add(GaugeMetricsSample.builder().name("namespace_latest_update_time")
          .value(v.latestUpdateTime).apply(value -> (long) value).putTag("namespace", k).build());
    });

    return samples;
  }

  @Override
  public String name() {
    return "NamespaceMetrics";
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
      return "NamespaceInfo{" +
          "usedTime=" + usageCount +
          ", firstLoadSpend=" + firstLoadSpend +
          ", updateLatestTime='" + latestUpdateTime + '\'' + ", releaseKey='" + releaseKey + '\'' +
          '}';
    }

    public int getUsageCount() {
      return usageCount;
    }

    public void incrementUsedTime() {
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
