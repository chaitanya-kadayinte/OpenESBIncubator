package com.fiorano.openesb.management;

import java.util.List;

/**
 * Created by root on 3/14/16.
 */
public class Response {
    private List<String> applications;

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

    public List<String> getApplications() {
        return applications;
    }

    public void setApplications(List<String> applications) {
        this.applications = applications;
    }
}
