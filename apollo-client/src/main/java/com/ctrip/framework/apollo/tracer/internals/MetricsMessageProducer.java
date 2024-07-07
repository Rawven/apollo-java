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

import com.ctrip.framework.apollo.monitor.metrics.MetricsEvent;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.Transaction;

/**
 * metrics message producer
 * @author Rawven
 */
public class MetricsMessageProducer implements MessageProducer {

    public static final String APOLLO_CONFIG_EXCEPTION = "ApolloConfigException";
    public static final String TRACER = "Tracer";
    public static final String TRACER_ERROR = TRACER + ".Error";
    public static final String TRACER_EVENT = TRACER + ".Event";
    public static final String THROWABLE = TRACER + ".throwable";
    public static final String STATUS = TRACER + ".status";
    public static final String NAME_VALUE_PAIRS = TRACER + ".nameValuePairs";


    @Override
    public void logError(Throwable cause) {
        MetricsEvent.builder().withName(TRACER_ERROR)
            .withTag(TRACER)
            .putAttachment(THROWABLE,cause)
            .push();
    }

    @Override
    public void logError(String message, Throwable cause) {
        MetricsEvent.builder().withName(TRACER_ERROR)
            .withTag(TRACER)
            .putAttachment(THROWABLE,cause).push();
    }

    @Override
    public void logEvent(String type, String name) {
//        if(Objects.equals(type, APOLLO_CONFIG_EXCEPTION)) {
//            Metrics.push(MetricsEvent.builder().withName(TRACER_EVENT)
//                .withTag(TRACER).build());
//        }
    }

    @Override
    public void logEvent(String type, String name, String status,
        String nameValuePairs) {
        if(APOLLO_CONFIG_EXCEPTION.equals(type)) {
            MetricsEvent.builder().withName(TRACER_EVENT)
                .putAttachment(STATUS,status)
                .putAttachment(NAME_VALUE_PAIRS,nameValuePairs)
                .withTag(TRACER).push();
        }
    }

    @Override
    public Transaction newTransaction(String type, String name) {
        return null;
    }

}
