package com.fiorano.openesb.jmsroute.impl;

import com.fiorano.openesb.route.FilterMessageException;
import com.fiorano.openesb.route.Route;
import com.fiorano.openesb.route.RouteConfiguration;
import com.fiorano.openesb.route.RouteOperationHandler;
import com.fiorano.openesb.route.impl.AbstractRouteImpl;
import com.fiorano.openesb.transport.*;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.transport.impl.jms.JMSPort;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;
import com.fiorano.openesb.transport.impl.jms.JMSProducerConfiguration;

public class JMSRouteImpl extends AbstractRouteImpl<JMSMessage> implements Route<JMSMessage> {

    private Producer<JMSMessage> producer;
    private JMSPort sourcePort;
    private TransportService<JMSPort, JMSMessage> transportService;
    private RouteConfiguration routeConfiguration;
    private Consumer<JMSMessage> messageConsumer;

    public JMSRouteImpl(final TransportService<JMSPort, JMSMessage> transportService, final RouteConfiguration routeConfiguration) throws Exception {
        super(routeConfiguration.getRouteOperationConfigurations());
        this.transportService = transportService;
        this.routeConfiguration = routeConfiguration;

        routeOperationHandlers.add(new RouteOperationHandler<JMSMessage>() {
            public void handleOperation(JMSMessage message) throws FilterMessageException {
                try {
                    producer.send(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sourcePort = transportService.enablePort(routeConfiguration.getSourceConfiguration());
    }

    public void start() throws Exception {
        producer = transportService.createProducer(transportService.enablePort(routeConfiguration.getDestinationConfiguration()), new JMSProducerConfiguration());
        messageConsumer = transportService.createConsumer(sourcePort, routeConfiguration.getConsumerConfiguration());

        messageConsumer.attachMessageListener(new MessageListener<JMSMessage>() {
            public void messageReceived(JMSMessage message) {
                try {
                    handleMessage(message);
                } catch (Exception e) {
                    //todo
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() throws Exception {
        messageConsumer.close();
        producer.close();
    }

    public void delete() {

    }

    public void changeTargetDestination(PortConfiguration portConfiguration) throws Exception{
        producer.close();
        producer = null;
        producer = transportService.createProducer(transportService.enablePort(portConfiguration), new JMSProducerConfiguration());

    }

    public void changeSourceDestination(PortConfiguration portConfiguration) throws Exception{
        messageConsumer.close();
        messageConsumer = transportService.createConsumer(sourcePort, routeConfiguration.getConsumerConfiguration());

        messageConsumer.attachMessageListener(new MessageListener<JMSMessage>() {
            public void messageReceived(JMSMessage message) {
                try {
                    handleMessage(message);
                } catch (Exception e) {
                    //todo
                    e.printStackTrace();
                }
            }
        });

    }

}
