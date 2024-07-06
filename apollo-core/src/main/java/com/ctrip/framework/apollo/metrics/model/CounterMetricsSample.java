package com.ctrip.framework.apollo.metrics.model;

import com.ctrip.framework.apollo.metrics.util.MeterType;
import com.google.common.util.concurrent.AtomicDouble;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rawven
 */
public class CounterMetricsSample extends MetricsSample {

  private final AtomicDouble value;

  public CounterMetricsSample(String name, double num) {
    this.name = name;
    this.value = new AtomicDouble(num);
    this.type = MeterType.COUNTER;
  }

  public static <T> GaugeMetricsSample.Builder<T> builder() {
    return new GaugeMetricsSample.Builder<>();
  }

  public Double getValue() {
    return value.get();
  }

  public void setValue(Double value) {
    this.value.set(value);
  }

  public static class Builder {

    private String name;
    private double value;
    private final Map<String, String> tags = new HashMap<>();

    public CounterMetricsSample.Builder name(String name) {
      this.name = name;
      return this;
    }

    public CounterMetricsSample.Builder value(double value) {
      this.value = value;
      return this;
    }

    public CounterMetricsSample.Builder putTag(String key, String value) {
      this.tags.put(key, value);
      return this;
    }

    public CounterMetricsSample.Builder tags(Map<String, String> tags) {
      this.tags.putAll(tags);
      return this;
    }

    public CounterMetricsSample build() {
      CounterMetricsSample sample = new CounterMetricsSample(name, value);
      sample.tags.putAll(tags);
      return sample;
    }
  }
}
