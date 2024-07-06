package com.ctrip.framework.apollo.metrics.collector;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AbstractMetricsCollectorTest {

  private AbstractMetricsCollector metricsCollector;

  @Before
  public void setUp() {
    metricsCollector = new AbstractMetricsCollector("tag1", "tag2") {
      @Override
      public String name() {
        return "MockMetricsCollector";
      }

      @Override
      public void collect0(MetricsEvent event) {
        // 模拟实现
      }

      @Override
      public List<MetricsSample> export0(List<MetricsSample> samples) {
        // 模拟实现
        return samples;
      }
    };
  }

  @Test
  public void testConstructorInitialization() {
    assertNotNull(metricsCollector);
  }

  @Test
  public void testIsSupport() {
    MetricsEvent event = Mockito.mock(MetricsEvent.class);

    when(event.getTag()).thenReturn("tag1");
    assertTrue(metricsCollector.isSupport(event));

    when(event.getTag()).thenReturn("tag3");
    assertFalse(metricsCollector.isSupport(event));
  }

  @Test
  public void testCollect() {
    MetricsEvent event = Mockito.mock(MetricsEvent.class);
    metricsCollector.collect(event);
    assertTrue(metricsCollector.isSamplesUpdated());
  }

  @Test
  public void testIsSamplesUpdated() {
    MetricsEvent event = Mockito.mock(MetricsEvent.class);
    metricsCollector.collect(event);
    assertTrue(metricsCollector.isSamplesUpdated());
    assertFalse(metricsCollector.isSamplesUpdated());
  }

  @Test
  public void testExport() {
    List<MetricsSample> samples = metricsCollector.export();
    assertNotNull(samples);
  }
}