package com.fiorano.openesb.microservice.ccp;

import com.fiorano.openesb.microservice.ccp.event.ComponentCCPEvent;

public interface CCPResponseCallback {
    void onResponse(Long requestID, ComponentCCPEvent event);
}