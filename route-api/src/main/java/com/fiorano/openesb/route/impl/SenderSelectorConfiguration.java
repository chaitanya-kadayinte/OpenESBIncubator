package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.RouteOperationConfiguration;

public class SenderSelectorConfiguration extends RouteOperationConfiguration {
    private String sourceName;
    private String appID;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }
}
