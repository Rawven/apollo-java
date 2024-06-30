package com.ctrip.framework.apollo.metrics;

import static com.ctrip.framework.apollo.metrics.MetricsConstant.NAME_VALUE_PAIRS;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.STATUS;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.THROWABLE;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.TRACER_ERROR;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.TRACER_EVENT;

import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.metrics.exposer.ExceptionMetricsExposer;
import com.ctrip.framework.apollo.metrics.model.CounterMetricsSample;
import com.ctrip.framework.apollo.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.metrics.model.MetricsSample;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Rawven
 */
public class DefaultExceptionMetricsExposer extends AbstractMetricsCollector implements
    ExceptionMetricsExposer {


  public static final String NAMESPACE404 = "namespace404";
  public static final String NAMESPACE_TIMEOUT = "namespaceTimeout";
  //TODO 会进行增长 但是不会有删除操作 ArrayBlockingQueue 线程安全且大小固定
  private static final int MAX_EXCEPTIONS_SIZE = 25;
  private final BlockingQueue<String> exceptions = new ArrayBlockingQueue<>(MAX_EXCEPTIONS_SIZE);
  //TODO 对于数量恒定的namespace(用户正常配置使用情况下) 使用CopyOnWrite 内存固定不用考虑OOM
  private final List<String> namespace404 = new CopyOnWriteArrayList<>();
  private final List<String> namespaceTimeout = new CopyOnWriteArrayList<>();

  public DefaultExceptionMetricsExposer() {
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
    return new ArrayList<>(exceptions);
  }

  @Override
  public void collect0(MetricsEvent event) {
    switch (event.getName()) {
      case TRACER_ERROR:
        //Tracer.logError
        ApolloConfigException exception = event.getAttachmentValue(THROWABLE);
        exceptions.add(exception.getCause().getClass().getSimpleName()+exception.getCause().getMessage());
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

  @Override
  public String name() {
    return "ExceptionMetrics";
  }
}
