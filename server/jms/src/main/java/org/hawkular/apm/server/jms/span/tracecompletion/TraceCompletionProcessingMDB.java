/*
 * Copyright 2015-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hawkular.apm.server.jms.span.tracecompletion;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.MessageListener;

import org.hawkular.apm.server.api.services.SpanCache;
import org.hawkular.apm.server.jms.RetryCapableMDB;
import org.hawkular.apm.server.processor.zipkin.CompletionTimeProcessing;
import org.hawkular.apm.server.processor.zipkin.CompletionTimeProcessingDeriver;
import org.hawkular.apm.server.processor.zipkin.CompletionTimeProcessingPublisher;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author Pavol Loffay
 */
@MessageDriven(name = "CompletionTimeProcessing_TraceCompletionProcessingDeriver",
        messageListenerInterface = MessageListener.class,
        activationConfig =  {
            @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
            @ActivationConfigProperty(propertyName = "destination", propertyValue ="SpanTraceCompletionProcessing"),
            @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
            @ActivationConfigProperty(propertyName = "clientID", propertyValue = TraceCompletionProcessingMDB.SUBSCRIBER),
            @ActivationConfigProperty(propertyName = "subscriptionName",
                    propertyValue = TraceCompletionProcessingMDB.SUBSCRIBER),
            @ActivationConfigProperty(propertyName = "messageSelector",
                    propertyValue = "subscriber IS NULL OR subscriber = '"+TraceCompletionProcessingMDB.SUBSCRIBER+"'")
})
public class TraceCompletionProcessingMDB extends RetryCapableMDB<CompletionTimeProcessing, CompletionTimeProcessing> {

    public static final String SUBSCRIBER = "SpanTraceCompletionProcessingDeriver";

    @Inject
    private SpanCache spanCache;

    @Inject
    private CompletionTimeProcessingPublisher traceCompletionTimePublisher;

    @Inject
    private TraceCompletionProcessingPublisherJMS processingPublisherJMS;

    public TraceCompletionProcessingMDB() {
        super(SUBSCRIBER);
    }

    @PostConstruct
    public void init() {
        setProcessor(new CompletionTimeProcessingDeriver(spanCache));
        setRetryPublisher(processingPublisherJMS);
        setPublisher(traceCompletionTimePublisher);
        setTypeReference(new TypeReference<List<CompletionTimeProcessing>>() {
        });
    }
}
