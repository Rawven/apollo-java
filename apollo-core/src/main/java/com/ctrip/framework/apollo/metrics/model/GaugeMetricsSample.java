package com.ctrip.framework.apollo.metrics.model;

import com.ctrip.framework.apollo.metrics.util.MeterType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

public class GaugeMetricsSample<T> extends MetricsSample {

  private T value;
  private ToDoubleFunction<T> apply;

  public GaugeMetricsSample(String name, T value, ToDoubleFunction<T> apply) {
    this.name = name;
    this.value = value;
    this.apply = apply;
    this.type = MeterType.GAUGE;
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public ToDoubleFunction<T> getApply() {
    return this.apply;
  }

  public void setApply(ToDoubleFunction<T> apply) {
    this.apply = apply;
  }

  public double getApplyValue() {
    return getApply().applyAsDouble(getValue());
  }

  public static class Builder<T> {

    private String name;
    private T value;
    private ToDoubleFunction<T> apply = t -> 1;
    private final Map<String, String> tags = new HashMap<>();

    public Builder<T> name(String name) {
      this.name = name;
      return this;
    }

    public Builder<T> value(T value) {
      this.value = value;
      return this;
    }

    public Builder<T> apply(ToDoubleFunction<T> apply) {
      this.apply = apply;
      return this;
    }

    public Builder<T> putTag(String key, String value) {
      this.tags.put(key, value);
      return this;
    }

    public Builder<T> tags(Map<String, String> tags) {
      this.tags.putAll(tags);
      return this;
    }

    public GaugeMetricsSample<T> build() {
      GaugeMetricsSample<T> sample = new GaugeMetricsSample<>(name, value, apply);
      sample.tags.putAll(tags);
      return sample;
    }
  }
}

