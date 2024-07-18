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
package com.ctrip.framework.apollo.monitor.tracer;

import static org.junit.Assert.assertTrue;

import com.ctrip.framework.apollo.tracer.internals.ClientMessageProducerManager;
import com.ctrip.framework.apollo.tracer.internals.MessageProducerComposite;
import com.ctrip.framework.apollo.tracer.internals.cat.CatMessageProducer;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.MessageProducerManager;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class ClientMessageProducerManagerTest {
  private MessageProducerManager messageProducerManager;

  @Before
  public void setUp() throws Exception {
    messageProducerManager = new ClientMessageProducerManager();
  }

  @Test
  public void testGetProducer() throws Exception {
      MessageProducer producer = messageProducerManager.getProducer();
      assertTrue(producer instanceof MessageProducerComposite);
      List<MessageProducer> producers = ((MessageProducerComposite) producer).getProducers();
      assertTrue(producers.get(0) instanceof CatMessageProducer);
  }

}