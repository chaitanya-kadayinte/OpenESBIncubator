package com.fiorano.openesb.events;

import com.fiorano.openesb.utils.exception.FioranoException;

public interface EventListener {

    public void onEvent(Event event) throws FioranoException;
}
