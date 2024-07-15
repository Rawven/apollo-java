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
package com.ctrip.framework.apollo.monitor.internal.exporter.internals;

import static com.ctrip.framework.apollo.monitor.internal.MetricsConstant.JMX;
import static com.ctrip.framework.apollo.monitor.internal.MetricsConstant.MBEAN_NAME;

import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.internal.exporter.MetricsExporter;
import com.ctrip.framework.apollo.monitor.internal.model.CounterModel;
import com.ctrip.framework.apollo.monitor.internal.model.GaugeModel;
import com.ctrip.framework.apollo.monitor.internal.util.JMXUtil;
import java.util.List;

/**
 * @author Rawven
 */
public class JmxMetricsExporter implements MetricsExporter {

  @Override
  public void init(List<MetricsCollector> collectors, long collectPeriod) {
    collectors.forEach(metricsCollector ->
        JMXUtil.register(MBEAN_NAME + metricsCollector.name(),
            metricsCollector));
  }

  @Override
  public boolean isSupport(String form) {
    return JMX.equals(form);
  }

  @Override
  public void registerCounterSample(CounterModel sample) {
  }

  @Override
  public void registerGaugeSample(GaugeModel<?> sample) {
  }

  @Override
  public String response() {
    return "JMX has no format data";
  }

  @Override
  public int getOrder() {
    return 2;
  }
}
