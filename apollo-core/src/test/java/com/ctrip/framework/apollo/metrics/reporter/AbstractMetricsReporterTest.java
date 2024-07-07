package com.ctrip.framework.apollo.metrics.reporter;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ctrip.framework.apollo.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import com.ctrip.framework.apollo.metrics.util.MeterType;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AbstractMetricsReporterTest {

  @Mock
  private MetricsCollector mockCollector;

  @InjectMocks
  private final AbstractMetricsReporter reporter = new AbstractMetricsReporter() {
    @Override
    protected void doInit() {
      // Do nothing for test purposes
    }

    @Override
    public void registerGaugeSample(GaugeMetricsSample<?> sample) {
      // Mock implementation for test purposes
    }

    @Override
    public String response() {
      return "test";
    }

    @Override
    public boolean isSupport(String form) {
      return "mock".equals(form);
    }

    @Override
    public void registerCounterSample(CounterMetricsSample sample) {
      // Mock implementation for test purposes
    }
  };



  @Test
  public void testInit() {
    List<MetricsCollector> collectors = Collections.singletonList(mockCollector);
    long collectPeriod = 10L;

    reporter.init(collectors, collectPeriod);

    assertNotNull(AbstractMetricsReporter.m_executorService);
  }
  @Test
  public void testIsSupport(){
     assertTrue(reporter.isSupport("mock"));
     assertFalse(reporter.isSupport("mock1"));
  }

  @Test
  public void testUpdateMetricsData() {
    MetricsSample mockSample = mock(MetricsSample.class);
    when(mockSample.getType()).thenReturn(MeterType.GAUGE);
    when(mockCollector.isSamplesUpdated()).thenReturn(true);
    when(mockCollector.export()).thenReturn(Collections.singletonList(mockSample));

    reporter.init(Collections.singletonList(mockCollector), 10L);
    reporter.updateMetricsData();

    verify(mockCollector, times(1)).isSamplesUpdated();
    verify(mockCollector, times(1)).export();
    verify(mockSample, times(1)).getType();
  }

  @Test
  public void testGetTags() {
    MetricsSample sample = mock(MetricsSample.class);
    Map<String, String> tags = new HashMap<>();
    tags.put("key1", "value1");
    tags.put("key2", "value2");

    when(sample.getTags()).thenReturn(tags);

    String[][] result = reporter.getTags(sample);

    assertArrayEquals(new String[]{"key1", "key2"}, result[0]);
    assertArrayEquals(new String[]{"value1", "value2"}, result[1]);
  }
}
