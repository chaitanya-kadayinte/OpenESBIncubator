package com.fiorano.openesb.transport.impl.jms;

import com.fiorano.openesb.transport.ConnectionConfiguration;

public class JMSConnectionConfiguration implements ConnectionConfiguration {
    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
