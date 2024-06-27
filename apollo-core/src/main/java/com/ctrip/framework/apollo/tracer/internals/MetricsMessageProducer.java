package com.ctrip.framework.apollo.tracer.internals;

import static com.ctrip.framework.apollo.metrics.MetricsConstant.APOLLO_CONFIG_EXCEPTION;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.NAME_VALUE_PAIRS;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.STATUS;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.THROWABLE;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.TRACER;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.TRACER_ERROR;
import static com.ctrip.framework.apollo.metrics.MetricsConstant.TRACER_EVENT;

import com.ctrip.framework.apollo.metrics.MetricsEvent;
import com.ctrip.framework.apollo.tracer.spi.MessageProducer;
import com.ctrip.framework.apollo.tracer.spi.Transaction;
import java.util.Objects;

public class MetricsMessageProducer implements MessageProducer {



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
        if(Objects.equals(type,APOLLO_CONFIG_EXCEPTION)) {
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
