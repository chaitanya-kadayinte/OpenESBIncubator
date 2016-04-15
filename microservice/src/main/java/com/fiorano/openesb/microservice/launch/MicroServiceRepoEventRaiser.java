package com.fiorano.openesb.microservice.launch;

import com.fiorano.openesb.events.Event;
import com.fiorano.openesb.events.EventsManager;
import com.fiorano.openesb.events.MicroServiceRepoUpdateEvent;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.osgi.framework.FrameworkUtil;

/**
 * Created by Janardhan on 3/25/2016.
 */
public class MicroServiceRepoEventRaiser {
    static EventsManager eventsManager = FrameworkUtil.getBundle(EventsManager.class).getBundleContext().getService(FrameworkUtil.getBundle(EventsManager.class).getBundleContext().getServiceReference(EventsManager.class));
    public static void generateServiceRepositoryEvent(String serviceGUID,
                                                      String serviceVersion,
                                                      String resourceName,
                                                      String serviceStatus, Event.EventCategory category, String description) throws FioranoException{

        MicroServiceRepoUpdateEvent event = new MicroServiceRepoUpdateEvent();

        event.setServiceGUID(serviceGUID);
        event.setServiceVersion(serviceVersion);
        event.setResourceName(resourceName);
        event.setServiceStatus(serviceStatus);
        event.setEventCategory(category);
        event.setEventDescription(description);
        event.setEventGenerationDate(System.currentTimeMillis());
        event.setEventStatus(serviceStatus);
        event.setSource("Open ESB");

        eventsManager.raiseEvent(event);
    }
}
