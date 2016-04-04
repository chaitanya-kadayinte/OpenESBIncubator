package com.fiorano.openesb.microservice.ccp.event.common.data;

import java.io.Serializable;

/**
 * Created by Janardhan on 4/4/2016.
 */
public class ConfigurationProperty implements Serializable{
    private String name;
    private String value;
    private boolean isEncrypted;
    private String type;
    private String configurationType;

    public ConfigurationProperty(){

    }

    public ConfigurationProperty(String name, String value, boolean encrypted, String type, String configurationType){
        this.name = name;
        this.value = value;
        this.isEncrypted = encrypted;
        this.type = type;
        this.configurationType = configurationType;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

    public boolean isEncrypted(){
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted){
        isEncrypted = encrypted;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getConfigurationType() {
        return configurationType;
    }

    public void setConfigurationType(String configurationType) {
        this.configurationType = configurationType;
    }
}
