package com.ctrip.framework.apollo.metrics.internal;

import static org.junit.Assert.assertTrue;

import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.tracer.internals.DefaultMessageProducerManager;
import com.ctrip.framework.apollo.tracer.internals.MessageProducerComposite;
import com.ctrip.framework.apollo.tracer.internals.MetricsMessageProducer;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.MessageProducerManager;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class MetricsMessageProducerTest {
  private MessageProducerManager messageProducerManager;

  @Before
  public void setUp() throws Exception {
    System.setProperty(ApolloClientSystemConsts.APOLLO_MONITOR_ENABLED,"true");
    messageProducerManager = new DefaultMessageProducerManager();
  }

  @Test
  public void testGetMetricsProducer() throws Exception {
    MessageProducer producer = messageProducerManager.getProducer();
    assertTrue(producer instanceof MessageProducerComposite);
    List<MessageProducer> producers = ((MessageProducerComposite) producer).getProducers();
    assertTrue(producers.get(0) instanceof MetricsMessageProducer);
  }

}
