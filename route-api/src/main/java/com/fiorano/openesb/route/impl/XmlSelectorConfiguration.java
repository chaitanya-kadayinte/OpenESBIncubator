package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.SelectorConfiguration;

import java.util.HashMap;
import java.util.Map;

public class XmlSelectorConfiguration extends SelectorConfiguration {

    private String xpath;
    private Map<String,String> nsPrefixMap = new HashMap<>();
    private String target;

    public XmlSelectorConfiguration(String target) {
        this.target = target;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public Map<String, String> getNsPrefixMap() {
        return nsPrefixMap;
    }

    public void setNsPrefixMap(Map<String, String> nsPrefixMap) {
        this.nsPrefixMap = nsPrefixMap;
    }

    public String getTarget() {
        return target;
    }
}
