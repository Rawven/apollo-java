package com.ctrip.framework.apollo.metrics;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import org.junit.Assert;
import org.junit.Test;

public class MetricsTest {

  @Test
  public void testIsMetricsEnabled() {
    Assert.assertFalse(Metrics.isMetricsEnabled());
    System.setProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED, "true");
    Assert.assertTrue(Metrics.isMetricsEnabled());
  }


}
