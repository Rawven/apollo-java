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
package com.ctrip.framework.apollo.tracer.internals;

import com.ctrip.framework.apollo.core.utils.ClassLoaderUtil;
import com.ctrip.framework.apollo.tracer.internals.cat.CatMessageProducer;
import com.ctrip.framework.apollo.tracer.internals.cat.CatNames;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.MessageProducerManager;
import com.ctrip.framework.apollo.util.ConfigUtil;
import java.util.ArrayList;
import java.util.List;

public class ClientMessageProducerManager implements MessageProducerManager {
  private static MessageProducer producer;
  private static ConfigUtil m_configUtil = new ConfigUtil();

  public ClientMessageProducerManager() {
    List<MessageProducer> producers = new ArrayList<>();
    if(m_configUtil.isClientMonitorEnabled()){
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
