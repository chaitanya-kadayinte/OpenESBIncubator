package com.fiorano.openesb.microservice.ccp;

import com.fiorano.openesb.microservice.ccp.event.ComponentCCPEvent;

public interface IEventListener {
    void onEvent(ComponentCCPEvent event) throws Exception;
}
