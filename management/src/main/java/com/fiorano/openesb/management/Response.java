package com.fiorano.openesb.management;

import java.util.Map;

/**
 * Created by root on 3/14/16.
 */
public class Response {
    private Map<String,ApplicationHeader> applications;

    private String message ;

    private boolean status ;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Map<String, ApplicationHeader> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, ApplicationHeader> applications) {
        this.applications = applications;
    }
}
