package com.fiorano.openesb.microservice.ccp.event;

import com.fiorano.openesb.microservice.ccp.event.common.DataEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent;
import com.fiorano.openesb.microservice.ccp.event.component.HandShakeAckEvent;
import com.fiorano.openesb.microservice.ccp.event.component.StatusEvent;
import com.fiorano.openesb.microservice.ccp.event.peer.CommandEvent;
import com.fiorano.openesb.microservice.ccp.event.peer.HandShakeEvent;

public class EventFactory {

    public static ControlEvent getEvent(CCPEventType type){
        switch(type){
            case DATA_REQUEST:
                return new DataRequestEvent();
            case DATA:
                return new DataEvent();
            case HANDSHAKE_INITIATE:
                return new HandShakeEvent();
            case HANDSHAKE_ACK:
                return new HandShakeAckEvent();
            case COMMAND:
                return new CommandEvent();
            case STATUS:
                return new StatusEvent();
        }
        throw new IllegalArgumentException("EVENT_NOT_FOUND");
    }
}