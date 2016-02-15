package com.fiorano.openesb.events;

import java.util.*;

public class EventsManager implements EventListener {

    // EventListeners list
    private List eventListeners = new ArrayList();

    public EventsManager(){

    }

    public void registerEventListener(EventListener fesEventListener) {
        if(!eventListeners.contains(fesEventListener))
            eventListeners.add(fesEventListener);
    }

    public void unRegisterEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }

    public void handleEvent(Event event) {

            //Notify the EventListeners
            if (!eventListeners.isEmpty()) {
                Iterator listeners = this.eventListeners.iterator();
                while (listeners.hasNext()) {
                    EventListener listener = (EventListener) listeners.next();
                    try {
                        listener.onEvent(event);
                    } catch (Throwable e) {

                    }
                }
            }
        }

    public synchronized void onEvent(Event event) {
        handleEvent(event);
    }
}
