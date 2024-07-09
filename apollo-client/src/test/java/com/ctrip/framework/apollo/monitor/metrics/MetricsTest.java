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
package com.ctrip.framework.apollo.monitor.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.collector.MockMetricsCollectorManager;
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
    System.setProperty(ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_ENABLED, "true");

    // Call the method under test
    Metrics.push(mockEvent);

    // Verify the interactions
    verify(mockCollector).collect(any());

    MockMetricsCollectorManager.reset();
  }

  @Test
  public void testPushMetricsEventWhenDisabled() {
    // Disable metrics
    System.setProperty(ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_ENABLED, "false");

    // Call the method under test
    Metrics.push(mockEvent);

    // Verify no interactions
    verify(mockCollector, never()).collect(any());

    MockMetricsCollectorManager.reset();
  }


}
