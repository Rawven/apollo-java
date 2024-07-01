package com.ctrip.framework.apollo.metrics;

import static com.ctrip.framework.apollo.core.utils.TimeUtil.DATE_FORMATTER;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.metrics.exposer.NamespaceMetricsExposer;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * @author Rawven
 */
public class DefaultNamespaceMetricsExposer extends AbstractMetricsCollector implements
    NamespaceMetricsExposer {

  private static final Logger logger = DeferredLoggerFactory.getLogger(DefaultNamespaceMetricsExposer.class);

  public static final String NAMESPACE = "namespace";
  public static final String NAMESPACE_UPDATE_TIME = "namespace_update_time";
  public static final String NAMESPACE_FIRST_LOAD_SPEND = "namespace_firstLoadSpend";
  public static final String NAMESPACE_USAGE_COUNT = "namespace_usage_count";
  private final Map<String, Config> m_configs;
  private final Map<String, Object> m_configLocks;
  private final Map<String, ConfigFile> m_configFiles;
  private final Map<String, Object> m_configFileLocks;
  //TODO 对于数量恒定的namespace(用户正常配置使用情况下) 使用ConcurrentHashMap 内存固定不用考虑OOM
  private final Map<String, NamespaceMetrics> namespaces = Maps.newConcurrentMap();

  public DefaultNamespaceMetricsExposer(Map<String, Config> m_configs,
      Map<String, Object> m_configLocks,
      Map<String, ConfigFile> m_configFiles,
      Map<String, Object> m_configFileLocks) {
    super(NAMESPACE);
    this.m_configs = m_configs;
    this.m_configLocks = m_configLocks;
    this.m_configFiles = m_configFiles;
    this.m_configFileLocks = m_configFileLocks;

  }

  @Override
  public List<String> getAllNamespaceUsageCount() {
    List<String> usedTimes = Lists.newArrayList();
    namespaces.forEach((k, v) -> usedTimes.add(k+":"+v.usageCount));
    return usedTimes;
  }

  @Override
  public List<String> getAllNamespacesLatestUpdateTime() {
    List<String> latestUpdateTimes = Lists.newArrayList();
    namespaces.forEach((k, v) -> latestUpdateTimes.add(k+":"+v.latestUpdateTime));
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
    namespaces.forEach((k, v) -> firstLoadSpends.add(k+":"+v.firstLoadSpend));
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
    NamespaceMetrics namespaceMetrics = namespaces.computeIfAbsent(namespace, k -> new NamespaceMetrics());
    switch (event.getName()) {
      case NAMESPACE_USAGE_COUNT:
        namespaceMetrics.incrementUsedTime();
        break;
      case NAMESPACE_UPDATE_TIME:
        long updateTime = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
        String formattedTime = DATE_FORMATTER.format(Instant.ofEpochMilli(updateTime));
        namespaceMetrics.setLatestUpdateTime(formattedTime);
        break;
      case NAMESPACE_FIRST_LOAD_SPEND:
        long firstLoadSpendTime = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
        namespaceMetrics.setFirstLoadSpend(firstLoadSpendTime);
        break;
      default:
        logger.warn("Unknown event: {}", event);
        break;
    }
  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
    namespaces.forEach((k, v) -> {
      samples.add(GaugeMetricsSample.builder().name("namespace_status_"+k).value(1)
          .putTag("usageCount", String.valueOf(v.usageCount)).putTag("firstLoadSpend",
              String.valueOf(v.firstLoadSpend)).putTag("latestUpdatedTime",v.latestUpdateTime).build());
    });

    return samples;
  }

  @Override
  public String name() {
    return "NamespaceMetrics";
  }

  public static class NamespaceMetrics {
    private   int usageCount;
    private  long firstLoadSpend;
    private  String latestUpdateTime;

    @Override
    public String toString() {
      return "NamespaceInfo{" +
          "usedTime=" + usageCount +
          ", firstLoadSpend=" + firstLoadSpend +
          ", updateLatestTime='" + latestUpdateTime + '\'' +
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

    public String getLatestUpdateTime() {
      return latestUpdateTime;
    }

    public void setLatestUpdateTime(String latestUpdateTime) {
      this.latestUpdateTime = latestUpdateTime;
    }
  }

}
