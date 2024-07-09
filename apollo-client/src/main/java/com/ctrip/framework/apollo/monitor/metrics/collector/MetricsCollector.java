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
import com.ctrip.framework.apollo.monitor.metrics.model.MetricsSample;
import java.util.List;

/**
 * @author Rawven
 */
public interface MetricsCollector {


  String name();

  /**
   * is support the event
   */
  boolean isSupport(MetricsEvent event);

  /**
   * collect metrics from event
   */
  void collect(MetricsEvent event);

  /**
   * is samples updated
   */
  boolean isSamplesUpdated();

  /**
   * export to a format recognized by the monitoring system
   */
  List<MetricsSample> export();


}
