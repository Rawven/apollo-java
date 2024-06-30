package com.ctrip.framework.apollo.metrics.collector;

import static com.ctrip.framework.apollo.metrics.MetricsConstant.NAME_VALUE_PAIRS;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.STATUS;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.THROWABLE;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.TRACER_ERROR;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.TRACER_EVENT;

import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.metrics.MetricsConstant;
import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rawven
 */
public class TracerEventCollector extends AbstractMetricsCollector implements
    TracerEventCollectorMBean {


  public static final String NAMESPACE404 = "namespace404";
  public static final String NAMESPACE_TIMEOUT = "namespaceTimeout";
  private final List<ApolloConfigException> exceptions = new ArrayList<>();
  private final List<String> namespace404 = new ArrayList<>();
  private final List<String> namespaceTimeout = new ArrayList<>();

  public TracerEventCollector() {
    super(MetricsConstant.TRACER);
  }

  @Override
  public String getNamespace404() {
    return namespace404.toString();
  }

  @Override
  public String getNamespaceTimeout() {
    return namespaceTimeout.toString();
  }


  //TODO 报错信息该如何展示
  @Override
  public Integer getExceptionNum() {
    return exceptions.size();
  }

  @Override
  public List<String> getExceptionDetails() {
    List<String> exceptionDetails = new ArrayList<>();
    for (ApolloConfigException exception : exceptions) {
      exceptionDetails.add(exception.getCause().getClass().getSimpleName()+":"+exception.getCause().getMessage());
    }
    return exceptionDetails;
  }

  @Override
  public void collect0(MetricsEvent event) {
    switch (event.getName()) {
      case TRACER_ERROR:
        //Tracer.logError
        ApolloConfigException exception = event.getAttachmentValue(THROWABLE);
        exceptions.add(exception);
        break;
      case TRACER_EVENT:
        String status = event.getAttachmentValue(STATUS);
        String namespace = event.getAttachmentValue(NAME_VALUE_PAIRS);
        if (status.equals(NAMESPACE404)) {
          namespace404.add(namespace);
        } else if (status.equals(NAMESPACE_TIMEOUT)) {
          namespaceTimeout.add(namespace);
        }
        break;
      default:
        break;
    }
  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
    samples.add(
        CounterMetricsSample.builder().name("exceptionNum").value(exceptions.size()).build());

    //TODO 非数值类型的指标
    samples.add(GaugeMetricsSample.builder().name(NAMESPACE404).value(namespace404.size())
        .apply(value -> (double) namespace404.size()).putTag("data", namespace404.toString())
        .build());
    samples.add(GaugeMetricsSample.builder().name(NAMESPACE_TIMEOUT).value(namespaceTimeout.size())
        .apply(value -> (double) namespaceTimeout.size())
        .putTag("data", namespaceTimeout.toString()).build());
    return samples;
  }
}
