package com.ctrip.framework.apollo.internals;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ctrip.framework.apollo.build.MockInjector;
import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.reporter.MetricsReporter;
import com.ctrip.framework.apollo.monitor.reporter.JmxMetricsReporter;
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
