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

    public static final String ERROR_METRICS = "errorMetrics";
    public static final String THROWABLE = ERROR_METRICS + ".throwable";


    @Override
    public void logError(Throwable cause) {
        MetricsEvent.builder().withName(ERROR_METRICS)
            .withTag(ERROR_METRICS)
            .putAttachment(THROWABLE,cause)
            .push();
    }

    @Override
    public void logError(String message, Throwable cause) {
        MetricsEvent.builder().withName(ERROR_METRICS)
            .withTag(ERROR_METRICS)
            .putAttachment(THROWABLE,cause).push();
    }

    @Override
    public void logEvent(String type, String name) {
        //
    }

    @Override
    public void logEvent(String type, String name, String status,
        String nameValuePairs) {
       //
    }

    @Override
    public Transaction newTransaction(String type, String name) {
        return null;
    }

}
