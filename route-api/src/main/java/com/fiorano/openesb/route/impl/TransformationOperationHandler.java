package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.JMSMessageTransformer;
import com.fiorano.openesb.route.RouteOperationHandler;
import com.fiorano.openesb.route.RouteOperationType;
import com.fiorano.openesb.route.bundle.Activator;
import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.utils.StringUtil;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.TextMessage;

public class TransformationOperationHandler implements RouteOperationHandler<JMSMessage> {
    private JMSMessageTransformer msgTransformer;
    private Logger logger;
    private TransformationConfiguration configuration;

    public TransformationOperationHandler(TransformationConfiguration configuration) throws Exception {
        this.configuration = configuration;
        this.logger = LoggerFactory.getLogger(Activator.class);
        msgTransformer = new JmsMessageTransformerImpl(configuration.getXsl(), configuration.getJmsXsl(), configuration.getTransformerType());
    }

    public void handleOperation(JMSMessage message) throws FioranoException {
        if(StringUtil.isEmpty(configuration.getXsl()) && StringUtil.isEmpty(configuration.getJmsXsl())){
            return ;
        }
        try {
            javax.jms.TextMessage jmsMessage = (TextMessage) message.getMessage();
            String result = msgTransformer.transform(jmsMessage);
            if(configuration.getRouteOperationType() == RouteOperationType.ROUTE_TRANSFORM) {
                jmsMessage.setText(result);
            } else {
                CarryForwardContext carryForwardContext = JmsMessageUtil.getCarryForwardContext(jmsMessage);
                carryForwardContext.setAppContext(result);
                JmsMessageUtil.setCarryForwardContext(jmsMessage,carryForwardContext);
            }
        } catch (Exception e) {
            logger.error("Exception occured while message transformation : "  + e.getMessage());
        }
    }
}
