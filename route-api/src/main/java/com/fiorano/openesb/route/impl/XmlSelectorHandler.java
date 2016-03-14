package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.*;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.utils.JmsMessageUtil;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class XmlSelectorHandler implements RouteOperationHandler<JMSMessage> {
    private Selector selector;
    private XmlSelectorConfiguration selectorConfiguration;

    public XmlSelectorHandler(XmlSelectorConfiguration selectorConfiguration) {
        this.selectorConfiguration = selectorConfiguration;
        this.selector = new XMLContentSelector(selectorConfiguration);

    }

    @Override
    public void handleOperation(JMSMessage message) throws FilterMessageException, FioranoException {
        try {
            String content= selectorConfiguration.getTarget().equalsIgnoreCase("Body") ?
                    message.getBody() : message.getApplicationContext();
            if (!selector.isMessageSelected(content)) {
                throw new FilterMessageException();
            }
        } catch (JMSException e) {
            throw new FioranoException(e);
        }
    }
}
