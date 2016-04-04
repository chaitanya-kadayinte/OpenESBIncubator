package com.fiorano.openesb.management;

/**
 * Created by root on 3/31/16.
 */
public class TransportConfig {
    private String userName;
    private String password;
    private String brokerURL;
    private String jmxURL;
    private String providerURL;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBrokerURL() {
        return brokerURL;
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

    public String getJmxURL() {
        return jmxURL;
    }

    public void setJmxURL(String jmxURL) {
        this.jmxURL = jmxURL;
    }

    public String getProviderURL() {
        return providerURL;
    }

    public void setProviderURL(String providerURL) {
        this.providerURL = providerURL;
    }
}
