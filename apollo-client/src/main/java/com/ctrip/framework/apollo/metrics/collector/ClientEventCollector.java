package com.ctrip.framework.apollo.metrics.collector;

import static com.ctrip.framework.apollo.core.utils.TimeUtil.DATE_FORMATTER;

import com.ctrip.framework.apollo.metrics.MetricsConstant;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * @author Rawven
 */
public class ClientEventCollector extends AbstractMetricsCollector implements
    ClientEventCollectorMBean {

  public static final String CLIENT = "Client";
  public static final String NAMESPACE_UPDATE_TIME = "namespace_update_time";
  public static final String NAMESPACE_FIRST_LOAD_SPEND = "namespace_firstLoadSpend";
  public static final String NAMESPACE_USAGE_COUNT = "namespace_usage_count";
  private final Map<String, Integer> namespaceUsedTime = Maps.newHashMap();
  private final Map<String, Long> namespaceFirstLoadSpend = Maps.newHashMap();
  private final Map<String, String> namespaceUpdateLatestTime = Maps.newHashMap();

  public ClientEventCollector() {
    super(CLIENT);
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
}
