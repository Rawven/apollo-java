package com.ctrip.framework.apollo.metrics.collector;

import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rawven
 */
public abstract class AbstractMetricsCollector implements MetricsCollector {

  private final AtomicBoolean isUpdated = new AtomicBoolean();
  private final List<String> tags;

  public AbstractMetricsCollector(String... tags) {
    this.tags = Arrays.asList(tags);
  }

  @Override
  public boolean isSupport(MetricsEvent event) {
    for (String need : tags) {
      if (need.equals(event.getTag())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void collect(MetricsEvent event) {
    collect0(event);
    isUpdated.set(true);
  }

  @Override
  public boolean isSamplesUpdated() {
    return isUpdated.getAndSet(false);
  }

  @Override
  public List<MetricsSample> export() {
    List<MetricsSample> samples = new ArrayList<>();
    return export0(samples);
  }

  public abstract void collect0(MetricsEvent event);

  public abstract List<MetricsSample> export0(List<MetricsSample> samples);

}
