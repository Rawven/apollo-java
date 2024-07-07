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

import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.Transaction;
import java.util.List;

/**
 * message producer composite
 * @author Rawven
 */
public class MessageProducerComposite implements MessageProducer {
    private final List<MessageProducer> producers;

    public MessageProducerComposite(List<MessageProducer> list) {
        this.producers = list;
    }
    @Override
    public void logError(Throwable cause) {
         producers.forEach(producer -> producer.logError(cause));
    }

    @Override
    public void logError(String message, Throwable cause) {
        producers.forEach(producer -> producer.logError(message, cause));
    }

    @Override
    public void logEvent(String type, String name) {
        producers.forEach(producer -> producer.logEvent(type, name));
    }

    @Override
    public void logEvent(String type, String name, String status,
        String nameValuePairs) {
        producers.forEach(producer -> producer.logEvent(type, name, status, nameValuePairs));
    }

    @Override
    public Transaction newTransaction(String type, String name) {
        for (MessageProducer producer : producers) {
            Transaction transaction = producer.newTransaction(type, name);
            if (transaction != null) {
                return transaction;
            }
        }
        return NullMessageProducer.NULL_TRANSACTION;
    }
    public List<MessageProducer> getProducers(){
        return producers;
    }
}
