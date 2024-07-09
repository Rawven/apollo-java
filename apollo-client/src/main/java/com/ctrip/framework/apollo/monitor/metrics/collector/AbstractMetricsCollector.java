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
package com.ctrip.framework.apollo.monitor.metrics.collector;

import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.monitor.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.MetricsSample;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Rawven
 */
public abstract class AbstractMetricsCollector implements MetricsCollector {

  public final Map<String, CounterMetricsSample> counterSamples = Maps.newHashMap();
  public final Map<String, GaugeMetricsSample> gaugeSamples = Maps.newHashMap();
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
    export0();
    List<MetricsSample> samples = new ArrayList<>();
    samples.addAll(counterSamples.values());
    samples.addAll(gaugeSamples.values());
    return samples;
  }

  public abstract void collect0(MetricsEvent event);

  public abstract void export0();

}
