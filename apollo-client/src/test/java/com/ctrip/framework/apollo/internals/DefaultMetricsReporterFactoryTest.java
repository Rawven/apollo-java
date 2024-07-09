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
package com.ctrip.framework.apollo.internals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.monitor.metrics.reporter.internals.JmxMetricsReporter;
import com.ctrip.framework.apollo.util.ConfigUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class DefaultMetricsReporterFactoryTest {

  private DefaultMetricsReporterFactory defaultMetricsReporterFactory;

  public void setUp(String form, String period) {
    if (form!=null){
      System.setProperty(ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_FORM,form);
    }
    if (period!=null){
      System.setProperty(ApolloClientSystemConsts.APOLLO_CLIENT_MONITOR_EXPORT_PERIOD,period);
    }
    ConfigUtil mockConfigUtil = new ConfigUtil();
    defaultMetricsReporterFactory = new DefaultMetricsReporterFactory();
    MockInjector.setInstance(ConfigUtil.class, mockConfigUtil);
  }

  @Test
  public void testGetMetricsReporter_Success() {
    setUp("jmx","300");
    List<MetricsCollector> collectors = new ArrayList<>();
    MetricsReporter reporter = defaultMetricsReporterFactory.getMetricsReporter(collectors);
    assertNotNull(reporter);
    assertTrue(reporter instanceof JmxMetricsReporter);
  }

  @Test(expected = ApolloConfigException.class)
  public void testGetMetricsReporter_NoSupportedReporter() {
    setUp("prometheus","300");
    List<MetricsCollector> collectors = new ArrayList<>();
    defaultMetricsReporterFactory.getMetricsReporter(collectors);
  }

  @Test
  public void testGetMetricsReporter_NullForm() {
    setUp(null,null);
    List<MetricsCollector> collectors = new ArrayList<>();
    MetricsReporter reporter = defaultMetricsReporterFactory.getMetricsReporter(collectors);
    assertNull(reporter);
  }
}
