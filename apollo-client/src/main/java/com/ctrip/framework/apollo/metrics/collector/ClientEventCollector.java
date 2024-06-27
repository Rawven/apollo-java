package com.ctrip.framework.apollo.metrics.collector;

import static com.ctrip.framework.apollo.core.utils.TimeUtil.DATE_FORMATTER;

import com.ctrip.framework.apollo.metrics.MetricsConstant;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.google.common.collect.Maps;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Rawven
 */
public class ClientEventCollector implements MetricsCollector {

  public static final String CLIENT = "Client";
  public static final String CLIENT_ = CLIENT + "_";
  public static final String NAMESPACE_UPDATE = CLIENT_ + "update";
  public static final String NAMESPACE_FIRST_LOAD_SPEND = CLIENT_ + "namespaceFirstLoadSpend";
  public static final String NAMESPACE_USED = CLIENT_ + "namespaceUsed";
  public static final String NAMESPACE_USED_TIMES = CLIENT_ + "namespaceUsedTimes";
  private final Map<String, Integer> namespaceUsed = Maps.newHashMap();
  private final Map<String, Long> namespaceFirstLoadSpend = Maps.newHashMap();
  private final Map<String, String> namespaceUpdate = Maps.newHashMap();


  public String getAllNamespaceUsedTimes() {
    return namespaceUsed.toString();
  }

  public String getNamespaceLatestUpdate() {
    return namespaceUpdate.toString();
  }

  @Override
  public boolean isSupport(String tag) {
    return CLIENT.equals(tag);
  }

  @Override
  public void collect(MetricsEvent event) {
    switch (event.getName()) {
      case NAMESPACE_USED_TIMES:
        String namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
        namespaceUsed.put(namespace, namespaceUsed.getOrDefault(namespace, 0) + 1);
        break;
      case NAMESPACE_UPDATE:
        remoteUpdate(event);
        break;
      case NAMESPACE_FIRST_LOAD_SPEND:
        namespaceLoadFirstTime(event);
        break;
      default:
    }
  }

  private void remoteUpdate(MetricsEvent event) {
    String namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
    long time = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
    String formattedTime = DATE_FORMATTER.format(Instant.ofEpochMilli(time));
    namespaceUpdate.put(namespace, formattedTime);
  }

  private void namespaceLoadFirstTime(MetricsEvent event) {
    String namespace = event.getAttachmentValue(MetricsConstant.NAMESPACE);
    long time = event.getAttachmentValue(MetricsConstant.TIMESTAMP);
    namespaceFirstLoadSpend.put(namespace, time);
  }


  @Override
  public boolean isSamplesUpdated() {
    //TODO
    return true;
  }

  @Override
  public List<MetricsSample> export() {
    List<MetricsSample> samples = new ArrayList<>();
    namespaceUsed.forEach((k, v) -> {
      samples.add(new GaugeMetricsSample<>("namespace_" + k + "_used_times", v, value -> value));
    });
    HashMap<String, String> map = Maps.newHashMap();
    map.putAll(namespaceUpdate);
    samples.add(new GaugeMetricsSample<>(NAMESPACE_UPDATE, 1, value -> 1, map));
    namespaceFirstLoadSpend.forEach((k, v) -> {
      samples.add(new CounterMetricsSample(NAMESPACE_FIRST_LOAD_SPEND + "_" + k, v));
    });
    return samples;
  }
}
