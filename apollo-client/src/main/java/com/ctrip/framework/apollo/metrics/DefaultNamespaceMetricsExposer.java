package com.ctrip.framework.apollo.metrics;

import static com.ctrip.framework.apollo.core.utils.TimeUtil.DATE_FORMATTER;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigFile;
import com.ctrip.framework.apollo.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.metrics.exposer.NamespaceMetricsExposer;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Rawven
 */
public class DefaultNamespaceMetricsExposer extends AbstractMetricsCollector implements
    NamespaceMetricsExposer {

  public static final String NAMESPACE = "namespace";
  public static final String NAMESPACE_UPDATE_TIME = "namespace_update_time";
  public static final String NAMESPACE_FIRST_LOAD_SPEND = "namespace_firstLoadSpend";
  public static final String NAMESPACE_USAGE_COUNT = "namespace_usage_count";
  private final Map<String, Config> m_configs;
  private final Map<String, Object> m_configLocks;
  private final Map<String, ConfigFile> m_configFiles;
  private final Map<String, Object> m_configFileLocks;
  //TODO 对于数量恒定的namespace(用户正常配置使用情况下) 使用ConcurrentHashMap 内存固定不用考虑OOM
  private final Map<String, Integer> namespaceUsedTime = Maps.newConcurrentMap();
  private final Map<String, Long> namespaceFirstLoadSpend = Maps.newConcurrentMap();
  private final Map<String, String> namespaceUpdateLatestTime = Maps.newConcurrentMap();

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
  public String getAllNamespaceUsedTimes() {
    return namespaceUsedTime.toString();
  }

  @Override
  public String getNamespaceLatestUpdateTime() {
    return namespaceUpdateLatestTime.toString();
  }

  @Override
  public List<String> getAllUsedNamespaceName() {
    ArrayList<String> namespaces = Lists.newArrayList();
    m_configs.forEach((k, v) -> namespaces.add(k));
    return namespaces;
  }
  @Override
  public void collect0(MetricsEvent event) {
    String namespace;
    long time;
    switch (event.getName()) {
      case NAMESPACE_USAGE_COUNT:
        namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
        namespaceUsedTime.put(namespace, namespaceUsedTime.getOrDefault(namespace, 0) + 1);
        break;
      case NAMESPACE_UPDATE_TIME:
        namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
        time = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
        String formattedTime = DATE_FORMATTER.format(Instant.ofEpochMilli(time));
        namespaceUpdateLatestTime.put(namespace, formattedTime);
        break;
      case NAMESPACE_FIRST_LOAD_SPEND:
        namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
        time = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
        namespaceFirstLoadSpend.put(namespace, time);
        break;
      default:
    }
  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
    namespaceUsedTime.forEach((k, v) -> {
      samples.add(
          CounterMetricsSample.builder().name("namespace_" + k + "_usedTimes").value(v).build());
    });
    namespaceFirstLoadSpend.forEach((k, v) -> {
      samples.add(CounterMetricsSample.builder().name(NAMESPACE_FIRST_LOAD_SPEND + "_" + k).value(v)
          .build());
    });
    //TODO 非数值类型的指标
    samples.add(GaugeMetricsSample.builder().name(NAMESPACE_UPDATE_TIME).value(1)
        .tags(namespaceUpdateLatestTime).build());


    return samples;
  }

  @Override
  public String name() {
    return "NamespaceMetrics";
  }
}
