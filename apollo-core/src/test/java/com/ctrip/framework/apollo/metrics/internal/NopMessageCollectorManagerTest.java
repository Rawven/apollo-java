package com.ctrip.framework.apollo.metrics.internal;

import com.ctrip.framework.apollo.metrics.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.metrics.collector.internal.NopMetricsCollectorManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NopMessageCollectorManagerTest {
  private MetricsCollectorManager metricsCollectorManager;

  @Before
  public void setUp() throws Exception {
    metricsCollectorManager = new NopMetricsCollectorManager();
  }

  @Test
  public void testGetNopMetricsCollectorManager() {
    Assert.assertTrue(metricsCollectorManager.getCollectors().isEmpty());
  }
}
