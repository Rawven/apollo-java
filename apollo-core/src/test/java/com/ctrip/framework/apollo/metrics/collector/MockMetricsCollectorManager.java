package com.ctrip.framework.apollo.metrics.collector;

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
