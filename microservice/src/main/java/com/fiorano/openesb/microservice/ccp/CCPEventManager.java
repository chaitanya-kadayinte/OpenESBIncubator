/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fiorano.openesb.microservice.ccp;

import com.fiorano.openesb.microservice.ccp.event.CCPEventType;
import com.fiorano.openesb.microservice.ccp.event.ComponentCCPEvent;
import com.fiorano.openesb.microservice.ccp.event.ControlEvent;
import com.fiorano.openesb.microservice.ccp.event.EventFactory;
import com.fiorano.openesb.transport.*;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.transport.impl.jms.JMSMessageConfiguration;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;
import com.fiorano.openesb.transport.impl.jms.JMSProducerConfiguration;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CCPEventManager implements MessageListener<Message<BytesMessage>> {
    private Map<CCPEventType, Map<String, IEventListener>> eventListeners;
    private Map<Long, CCPResponseCallback> pendingResponses;
    private Map<Long, Integer> trackResponseCount;
    private TransportService<Port, Message> transportService;
    private final Object syncObj = new Object();
    private CCPEventGenerator ccpEventGenerator;

    public CCPEventManager(final TransportService<Port, Message> transportService) throws Exception {
        this.transportService = transportService;
        eventListeners = new HashMap<>();
        pendingResponses = new HashMap<>();
        trackResponseCount = new HashMap<>();
    }

    public void init() throws Exception {
        ccpEventGenerator = new CCPEventGenerator();
        createDestinations();
    }

    TransportService<Port, Message> getTransportService() {
        return transportService;
    }

    void createDestinations() throws Exception {
        JMSPortConfiguration jmsPortConfiguration = new JMSPortConfiguration();
        jmsPortConfiguration.setName("CCP_PEER_TO_COMPONENT_TRANSPORT");
        jmsPortConfiguration.setPortType(JMSPortConfiguration.PortType.TOPIC);
        ccpEventGenerator.setSendTopic(transportService.enablePort(jmsPortConfiguration));

        JMSPortConfiguration cToP = new JMSPortConfiguration();
        cToP.setPortType(JMSPortConfiguration.PortType.TOPIC);
        cToP.setName("CCP_COMPONENT_TO_PEER_TRANSPORT");
        transportService.enablePort(cToP);
    }

    public CCPEventGenerator getCcpEventGenerator() {
        return ccpEventGenerator;
    }

    public synchronized void registerListener(ComponentWorkflowListener listener, CCPEventType... eventTypes) {
        for (CCPEventType eventType : eventTypes) {
            if (eventListeners.get(eventType) == null) {
                eventListeners.put(eventType, new ConcurrentHashMap<String, IEventListener>());
            }
            eventListeners.get(eventType).remove(listener.getApplicationName() + "__" + listener.getApplicationVersion() + "__" + listener.getComponentInstanceName());
            eventListeners.get(eventType).put(listener.getApplicationName() + "__" + listener.getApplicationVersion() + "__" + listener.getComponentInstanceName(), listener);
        }
    }

    public synchronized void unregisterListener(ComponentWorkflowListener listener, CCPEventType... eventTypes) {
        for (CCPEventType eventType : eventTypes) {
            if (eventListeners.get(eventType) == null) continue;
            eventListeners.get(eventType).remove(listener.getApplicationName() + "__" + listener.getApplicationVersion() + "__" + listener.getComponentInstanceName());
        }
    }

    public void messageReceived(Message<BytesMessage> msg) throws FioranoException {
        BytesMessage bytesMessage = (BytesMessage) msg;
        try {
            bytesMessage.reset();
            java.lang.String component = bytesMessage.getStringProperty(ControlEvent.SOURCE_OBJECT);
            CCPEventType eventType = CCPEventType.valueOf(bytesMessage.getStringProperty(ControlEvent.EVENT_TYPE_HEADER));
            ControlEvent event = EventFactory.getEvent(eventType);
            event.fromMessage(bytesMessage);
            synchronized (syncObj) {
                if (pendingResponses.get(event.getCorrelationID()) != null) {
                    pendingResponses.get(event.getCorrelationID()).onResponse(event.getCorrelationID(), new ComponentCCPEvent(component, event));
                    trackResponseCount.put(event.getCorrelationID(), trackResponseCount.get(event.getCorrelationID()) - 1);
                    // If i have got all the replies, remove the request from pending responses.
                    if (trackResponseCount.get(event.getCorrelationID()) <= 0) {
                        trackResponseCount.remove(event.getCorrelationID());
                        pendingResponses.remove(event.getCorrelationID());
                    }
                }
            }
            synchronized (this) {
                Map<String, IEventListener> eventListenerMap = eventListeners.get(eventType);
                if (eventListenerMap != null) {
                    for (IEventListener listener : eventListenerMap.values())
                        listener.onEvent(new ComponentCCPEvent(component, event));
                }
            }
        } catch (Exception e) {
            //todo
        }
    }

    void registerCallback(ControlEvent event, CCPResponseCallback callback, String... componentIdentifiers) {
        synchronized (syncObj) {
            pendingResponses.put(event.getEventId(), callback);
            trackResponseCount.put(event.getEventId(), componentIdentifiers.length);
        }
    }

    public void removeCallback(long eventID) {
        synchronized (syncObj) {
            trackResponseCount.remove(eventID);
            pendingResponses.remove(eventID);
        }
    }

    public class CCPEventGenerator {
        private Port sendTopic;
        private CCPEventManager ccpEventManager;


        /**
         * Send the control event to the list of component instances as specified.
         *
         * @param event                the event to be sent
         * @param componentIdentifiers This should be <applicationName>__<componentInstanceName>
         */
        public void sendEvent(ControlEvent event, String... componentIdentifiers) throws Exception {
            sendEvent(event, null, componentIdentifiers);
        }

        /**
         * Send the control event to the list of component instances as specified. If the event requires a response
         * the callback would be called on a response. This can be used only for a single response for the request.
         * If the component will be sending periodic responses, then go with the tradition approach of listeneing
         * for that category of messages.
         *
         * @param event                - Event to be published
         * @param callback             - Callback to be called on response
         * @param componentIdentifiers - This should be <applicationName>__<componentInstanceName>
         * @throws Exception - Exception
         */
        public void sendEvent(ControlEvent event, CCPResponseCallback callback, String... componentIdentifiers) throws Exception {
            try {
                TransportService<Port, Message> transportService = ccpEventManager.getTransportService();
                BytesMessage message = (BytesMessage) transportService.createMessage(new JMSMessageConfiguration(JMSMessageConfiguration.MessageType.Bytes));
                event.toMessage(message);
                StringBuilder target = new StringBuilder();
                for (String instance : componentIdentifiers) target.append(instance).append(";");
                if (target.toString().length() == 0)
                    throw new FioranoException("NO_TARGET_COMPONENT_FOR_CCP_EVENT" + event.getEventId());
                message.setJMSPriority(event.getPriority());
                message.setJMSExpiration(event.getExpiryTime());
                message.setStringProperty(ControlEvent.TARGET_OBJECTS, target.toString());
                message.setStringProperty(ControlEvent.EVENT_TYPE_HEADER, event.getEventType().toString());
                Producer<Message> producer = transportService.createProducer(sendTopic, new JMSProducerConfiguration());
                producer.send(new JMSMessage(message));

            } catch (JMSException e) {
                throw new FioranoException(e);
            }

            if (event.isReplyNeeded() && callback != null) {
                registerCallback(event, callback, componentIdentifiers);
            }
        }

        public void setSendTopic(Port sendTopic) {
            this.sendTopic = sendTopic;
        }
    }
}