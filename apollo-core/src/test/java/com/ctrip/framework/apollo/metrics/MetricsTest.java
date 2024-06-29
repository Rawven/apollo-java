package com.ctrip.framework.apollo.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.internal.MockMetricsCollectorManager;
import org.junit.Before;
import org.junit.Test;

public class MetricsTest {
  private MetricsCollector mockCollector;
  private MetricsEvent mockEvent;

  @Before
  public void setUp() {
    mockCollector = mock(MetricsCollector.class);
    mockEvent = mock(MetricsEvent.class);
    MockMetricsCollectorManager.addCollector(mockCollector);
    when(mockCollector.isSupport(any())).thenReturn(true);
  }

  @Test
  public void testPushMetricsEvent() {
    // Enable metrics
    System.setProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED, "true");

    // Call the method under test
    Metrics.push(mockEvent);

    // Verify the interactions
    verify(mockCollector).collect(any());

    MockMetricsCollectorManager.reset();
  }

  @Test
  public void testPushMetricsEventWhenDisabled() {
    // Disable metrics
    System.setProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED, "false");

    // Call the method under test
    Metrics.push(mockEvent);

    // Verify no interactions
    verify(mockCollector, never()).collect(any());

    MockMetricsCollectorManager.reset();
  }

  @Test
  public void testIsMetricsEnabled() {
    // Set system property to enable metrics
    System.setProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED, "true");
    assertTrue(Metrics.isMetricsEnabled());

    // Set system property to disable metrics
    System.setProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED, "false");
    assertFalse(Metrics.isMetricsEnabled());

    // Clear the system property and use Foundation
    System.clearProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED);
    assertFalse(Metrics.isMetricsEnabled());
    MockMetricsCollectorManager.reset();
  }
}
