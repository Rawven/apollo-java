package com.ctrip.framework.apollo.monitor.metrics.reporter;

import com.ctrip.framework.apollo.core.utils.ApolloThreadFactory;
import com.ctrip.framework.apollo.core.utils.DeferredLoggerFactory;
import com.ctrip.framework.apollo.monitor.metrics.collector.MetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.MetricsSample;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

/**
 * General framework for access monitoring systems
 *
 * @author Rawven
 */
@SuppressWarnings("all")
public abstract class AbstractMetricsReporter implements MetricsReporter {

  protected static final Logger log = DeferredLoggerFactory.getLogger(
      AbstractMetricsReporter.class);
  public static ScheduledExecutorService m_executorService;
  protected List<MetricsCollector> collectors;

  @Override
  public void init(List<MetricsCollector> collectors, long collectPeriod) {
    doInit();
    this.collectors = collectors;
    initScheduleMetricsCollectSync(collectPeriod);
  }

  protected abstract void doInit();

  private void initScheduleMetricsCollectSync(long collectPeriod) {
    //collect metrics data schedule
    m_executorService = Executors.newScheduledThreadPool(1,
        ApolloThreadFactory.create("MetricsReporter", true));
    m_executorService.scheduleAtFixedRate(new Runnable() {
      @Override
      public void run() {
        try {
          updateMetricsData();
        } catch (Throwable ex) {
          //ignore
        }
      }
      //need to give enough time to initialize
    }, 5, collectPeriod, TimeUnit.SECONDS);
  }

  protected void updateMetricsData() {
    log.debug("Start to update metrics data job");
    for (MetricsCollector collector : collectors) {
      if (!collector.isSamplesUpdated()) {
        continue;
      }
      List<MetricsSample> export = collector.export();
      for (MetricsSample metricsSample : export) {
        registerSample(metricsSample);
      }
    }
  }

  protected void registerSample(MetricsSample sample) {
    try {
      switch (sample.getType()) {
        case GAUGE:
          registerGaugeSample((GaugeMetricsSample<?>) sample);
          break;
        case COUNTER:
          registerCounterSample((CounterMetricsSample) sample);
          break;
        default:
          log.warn("UnSupport sample type: {}", sample.getType());
          break;
      }
    } catch (Exception e) {
      log.error("Register sample error", e);
    }
  }

  protected String[][] getTags(MetricsSample sample) {
    Map<String, String> tags = sample.getTags();
    if (tags == null || tags.isEmpty()) {
      return new String[][]{new String[0], new String[0]};
    }
    String[] labelNames = tags.keySet().toArray(new String[0]);
    String[] labelValues = tags.values().toArray(new String[0]);
    return new String[][]{labelNames, labelValues};
  }

}
