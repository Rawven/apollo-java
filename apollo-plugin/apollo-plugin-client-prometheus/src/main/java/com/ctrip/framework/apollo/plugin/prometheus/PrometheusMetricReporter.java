package com.ctrip.framework.apollo.plugin.prometheus;

import com.ctrip.framework.apollo.monitor.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.reporter.AbstractMetricsReporter;
import com.ctrip.framework.apollo.monitor.metrics.reporter.MetricsReporter;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.common.TextFormat;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rawven
 */
public class PrometheusMetricReporter extends AbstractMetricsReporter implements MetricsReporter {

  private static final Logger logger = LoggerFactory.getLogger(
      PrometheusMetricReporter.class);
  private final CollectorRegistry registry;
  private final Map<String, Collector.Describable> map = new HashMap<>();
  private final String PROMETHEUS = "prometheus";

  public PrometheusMetricReporter() {
    this.registry = new CollectorRegistry();
  }

  @Override
  public void doInit() {

  }

  @Override
  public boolean isSupport(String form) {
    return PROMETHEUS.equals(form);
  }

  @Override
  public void registerCounterSample(CounterMetricsSample sample) {
    String[][] tags = getTags(sample);
    Counter counter;
    if (!map.containsKey(sample.getName())) {
      counter = Counter.build()
          .name(sample.getName())
          .help("apollo")
          .labelNames(tags[0])
          .register(registry);
      map.put(sample.getName(), counter);
    } else {
      counter = (Counter) map.get(sample.getName());
    }
    counter.labels(tags[1]).inc(sample.getIncreaseValue());
  }

  @Override
  public void registerGaugeSample(GaugeMetricsSample<?> sample) {
    String[][] tags = getTags(sample);
    Gauge gauge;
    if (!map.containsKey(sample.getName())) {
      gauge = Gauge.build()
          .name(sample.getName())
          .help("apollo")
          .labelNames(tags[0])
          .register(registry);
      map.put(sample.getName(), gauge);
    } else {
      gauge = (Gauge) map.get(sample.getName());
    }
    gauge.labels(tags[1]).set(sample.getApplyValue());
  }


  @Override
  public String response() {
    StringWriter writer = new StringWriter();
    try {
      TextFormat.writeFormat(TextFormat.CONTENT_TYPE_OPENMETRICS_100, writer,
          registry.metricFamilySamples());
    } catch (IOException e) {
      logger.error("Write metrics to Prometheus format failed", e);
    }
    return writer.toString();
  }
}