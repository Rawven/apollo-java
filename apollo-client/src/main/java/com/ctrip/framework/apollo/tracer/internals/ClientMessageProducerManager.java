package com.ctrip.framework.apollo.tracer.internals;

import com.ctrip.framework.apollo.core.utils.ClassLoaderUtil;
import com.ctrip.framework.apollo.monitor.metrics.Metrics;
import com.ctrip.framework.apollo.tracer.internals.cat.CatMessageProducer;
import com.ctrip.framework.apollo.tracer.internals.cat.CatNames;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.MessageProducerManager;
import java.util.ArrayList;
import java.util.List;

public class ClientMessageProducerManager implements MessageProducerManager {
  private static MessageProducer producer;

  public ClientMessageProducerManager() {
    List<MessageProducer> producers = new ArrayList<>();
    if(Metrics.isClientMonitorEnabled()){
      producers.add(new MetricsMessageProducer());
    }
    if (ClassLoaderUtil.isClassPresent(CatNames.CAT_CLASS)) {
      producers.add(new CatMessageProducer());
    }
    if (producers.isEmpty()) {
      producers.add(new NullMessageProducer());
    }
    producer = new MessageProducerComposite(producers);
  }
  @Override
  public MessageProducer getProducer() {
    return producer;
  }

  @Override
  public int getOrder() {
    return -1;
  }
}
