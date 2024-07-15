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
package com.ctrip.framework.apollo.monitor.internal;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.monitor.internal.model.MetricsEvent;
import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.internal.collector.MetricsCollectorManager;
import com.ctrip.framework.apollo.monitor.internal.collector.MockMetricsCollectorManager;
import com.ctrip.framework.apollo.util.ConfigUtil;
import org.junit.Test;

public class ApolloMetricsTest {
  private MetricsCollector mockCollector;
  private MetricsEvent mockEvent;

  @Test
  public void testPushMetricsEvent() {
    System.setProperty(ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_ENABLED, "true");
    MockInjector.setInstance(ConfigUtil.class, new ConfigUtil());
    MockInjector.setInstance(MetricsCollectorManager.class,new MockMetricsCollectorManager());
    mockCollector = mock(MetricsCollector.class);
    mockEvent = mock(MetricsEvent.class);
    MockMetricsCollectorManager.addCollector(mockCollector);
    when(mockCollector.isSupport(any())).thenReturn(true);
    // Call the method under test
    ApolloMetrics.push(mockEvent);

    // Verify the interactions
    verify(mockCollector).collect(any());

    MockMetricsCollectorManager.reset();
  }


}
