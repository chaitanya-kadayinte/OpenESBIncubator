package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.JMSMessageTransformer;
import com.fiorano.openesb.route.RouteOperationHandler;
import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.utils.exception.FioranoException;

public class TransformationOperationHandler implements RouteOperationHandler<JMSMessage> {
    private JMSMessageTransformer msgTransformer;
    public TransformationOperationHandler(TransformationConfiguration configuration) throws Exception {
        msgTransformer = new JmsMessageTransformerImpl(configuration.getXsl(),configuration.getTransformerType());
    }

    public void handleOperation(JMSMessage message) throws FioranoException {
        _applyTransformation(message);
    }

    public Message _applyTransformation(JMSMessage message) throws FioranoException {
        try {
            javax.jms.Message jmsMessage = message.getMessage();
            msgTransformer.transform(jmsMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }
}
