package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.RouteOperationConfiguration;

public class TransformationConfiguration extends RouteOperationConfiguration {
    private String xsl;
    private String jmsXsl;
    private String transformerType;

    public String getXsl() {
        return xsl;
    }

    public String getTransformerType() {
        return transformerType;
    }

    public void setXsl(String xsl) {
        this.xsl = xsl;
    }

    public void setTransformerType(String transformerType) {
        this.transformerType = transformerType;
    }

    public String getJmsXsl() {
        return jmsXsl;
    }

    public void setJmsXsl(String jmsXsl) {
        this.jmsXsl = jmsXsl;
    }

}
