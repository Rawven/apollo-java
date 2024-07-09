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


import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.ERROR;
import static com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer.THROWABLE;

import com.ctrip.framework.apollo.exceptions.ApolloConfigException;
import com.ctrip.framework.apollo.monitor.exposer.ExceptionExposer;
import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.monitor.metrics.collector.AbstractMetricsCollector;
import com.ctrip.framework.apollo.monitor.metrics.model.CounterMetricsSample;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Rawven
 */
public class DefaultExceptionCollector extends AbstractMetricsCollector implements
    ExceptionExposer {

  public static final String EXCEPTION_NUM = "exception_num";
  private static final int MAX_EXCEPTIONS_SIZE = 25;
  private final BlockingQueue<String> exceptions = new ArrayBlockingQueue<>(MAX_EXCEPTIONS_SIZE);
  private final AtomicInteger exceptionNum = new AtomicInteger(0);

  public DefaultExceptionCollector() {
    super(ERROR);
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
    ApolloConfigException exception = event.getAttachmentValue(THROWABLE);
    exceptions.add(
        exception.getCause().getClass().getSimpleName() + exception.getCause().getMessage());
    exceptionNum.incrementAndGet();
  }

  @Override
  public void export0() {
    if (!counterSamples.containsKey(EXCEPTION_NUM)) {
      counterSamples.put(EXCEPTION_NUM, CounterMetricsSample.builder().name(EXCEPTION_NUM).value(0)
          .build());
    }
    counterSamples.get(EXCEPTION_NUM).setValue((double) exceptionNum.getAndSet(0));
  }

  @Override
  public String name() {
    return "ExceptionMetrics";
  }
}
