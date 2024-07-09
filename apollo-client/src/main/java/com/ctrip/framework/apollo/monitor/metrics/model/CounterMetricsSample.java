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
package com.ctrip.framework.apollo.monitor.metrics.model;

import com.ctrip.framework.apollo.monitor.metrics.util.MeterType;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rawven
 */
public class CounterMetricsSample extends MetricsSample {

  private  double nowValue;
  private  double increaseValue;

  public CounterMetricsSample(String name, double num) {
    this.name = name;
    this.nowValue = num;
    this.increaseValue = num;
    this.type = MeterType.COUNTER;
  }

  public static CounterBuilder builder() {
    return new CounterBuilder();
  }
  public void resetValue(double value) {
    increaseValue = value - nowValue;
    nowValue = value;
  }

  public Double getValue() {
    return nowValue;
  }
  public Double getIncreaseValue() {
    return increaseValue;
  }

  public void setValue(Double value) {
    this.nowValue=value;
  }

  public static class CounterBuilder {

    private final Map<String, String> tags = new HashMap<>();
    private String name;
    private double value;

    public CounterBuilder name(String name) {
      this.name = name;
      return this;
    }

    public CounterBuilder value(double value) {
      this.value = value;
      return this;
    }

    public CounterBuilder putTag(String key, String value) {
      this.tags.put(key, value);
      return this;
    }

    public CounterBuilder tags(Map<String, String> tags) {
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
