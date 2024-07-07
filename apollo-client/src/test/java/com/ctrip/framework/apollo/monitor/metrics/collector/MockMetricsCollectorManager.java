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

import java.util.ArrayList;
import java.util.List;

public class MockMetricsCollectorManager implements MetricsCollectorManager {
  private  static final List<MetricsCollector> collectors = new ArrayList<>();

  @Override
  public List<MetricsCollector> getCollectors() {
    return collectors;
  }

  public static void addCollector(MetricsCollector collectors) {
    MockMetricsCollectorManager.collectors.add(collectors);
  }
  public static void reset() {
    collectors.clear();
  }
}
