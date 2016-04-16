package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.RouteOperationConfiguration;

public class SenderSelectorConfiguration extends RouteOperationConfiguration {
    private String sourceName;
    private String appName_version;

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getAppName_version() {
        return appName_version;
    }

    public void setAppName_version(String appName_version) {
        this.appName_version = appName_version;
    }
}
