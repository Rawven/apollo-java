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
package com.ctrip.framework.apollo.monitor.metrics.collector.internal;


import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.NAME_VALUE_PAIRS;
import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.STATUS;
import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.THROWABLE;
import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.TRACER;
import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.TRACER_ERROR;
import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.TRACER_EVENT;

import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.monitor.exposer.ExceptionExposer;
import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.monitor.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.model.GaugeMetricsSample;
import com.ctrip.framework.apollo.monitor.metrics.model.MetricsSample;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Rawven
 */
public class DefaultExceptionCollector extends AbstractMetricsCollector implements
    ExceptionExposer {

  public static final String NAMESPACE_404 = "namespace_404_num";
  public static final String NAMESPACE_TIMEOUT = "namespace_time_out";
  public static final String EXCEPTION_NUM = "exception_num";
  //TODO 会进行增长 但是不会有删除操作 使用ArrayBlockingQueue 线程安全且大小固定
  private static final int MAX_EXCEPTIONS_SIZE = 25;
  private final BlockingQueue<String> exceptions = new ArrayBlockingQueue<>(MAX_EXCEPTIONS_SIZE);
  //TODO 对于数量恒定的namespace(用户正常配置使用情况下) 使用CopyOnWrite 内存固定不用考虑OOM
  private final List<String> namespace404 = new CopyOnWriteArrayList<>();
  private final List<String> namespaceTimeout = new CopyOnWriteArrayList<>();

  public DefaultExceptionCollector() {
    super(TRACER);
  }

  @Override
  public String getNamespace404() {
    return namespace404.toString();
  }

  @Override
  public String getNamespaceTimeout() {
    return namespaceTimeout.toString();
  }

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
        ApolloConfigException exception = event.getAttachmentValue(THROWABLE);
        exceptions.add(
            exception.getCause().getClass().getSimpleName() + exception.getCause().getMessage());
        break;
      case TRACER_EVENT:
        String status = event.getAttachmentValue(STATUS);
        String namespace = event.getAttachmentValue(NAME_VALUE_PAIRS);
        switch (status) {
          case "404":
            namespace404.add(namespace);
            break;
          case NAMESPACE_TIMEOUT:
            namespaceTimeout.add(namespace);
            break;
          default:
            break;
        }
        break;
      default:
        break;
    }
  }

  @Override
  public List<MetricsSample> export0(List<MetricsSample> samples) {
    samples.add(
        GaugeMetricsSample.builder().name(EXCEPTION_NUM).value(exceptions.size())
            .apply(value -> (int) value).build());
    samples.add(
        GaugeMetricsSample.builder().name(NAMESPACE_404).value(namespace404.size())
            .apply(value -> (int) value).build());
    samples.add(GaugeMetricsSample.builder().name(NAMESPACE_TIMEOUT)
        .value(namespaceTimeout.size()).apply(value -> (int) value).build());
    return samples;
  }

  @Override
  public String name() {
    return "ExceptionMetrics";
  }
}
