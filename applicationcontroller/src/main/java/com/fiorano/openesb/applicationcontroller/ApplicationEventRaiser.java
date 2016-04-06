package com.fiorano.openesb.applicationcontroller;

import com.fiorano.openesb.events.*;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.osgi.framework.FrameworkUtil;

/**
 * Created by Janardhan on 3/24/2016.
 */
public class ApplicationEventRaiser {
    static EventsManager eventsManager = FrameworkUtil.getBundle(EventsManager.class).getBundleContext().getService(FrameworkUtil.getBundle(EventsManager.class).getBundleContext().getServiceReference(EventsManager.class));
    public static void generateApplicationEvent(ApplicationEvent.ApplicationEventType eventType, Event.EventCategory category, String appGUID,
                                          String appName, String version, String description)
            throws FioranoException
    {
        ApplicationEvent event = new ApplicationEvent();
        event.setApplicationEventType(eventType);
        event.setSource("openESB");
        event.setEventGenerationDate(System.currentTimeMillis());
        event.setApplicationGUID(appGUID);
        event.setApplicationName(appName);
        event.setApplicationVersion(version);
        event.setEventDescription(description);
        event.setEventCategory(category);
        eventsManager.raiseEvent(event);
    }

    public static void generateRouteEvent(ApplicationEvent.ApplicationEventType eventType, Event.EventCategory category, String appGUID,
                                                String appName, String version, String routeGuid, String description)
            throws FioranoException
    {
        RouteEvent event = new RouteEvent();
        event.setRouteGUID(routeGuid);
        event.setApplicationEventType(eventType);
        event.setSource("openESB");
        event.setEventGenerationDate(System.currentTimeMillis());
        event.setApplicationGUID(appGUID);
        event.setApplicationName(appName);
        event.setApplicationVersion(version);
        event.setEventDescription(description);
        event.setEventCategory(category);
        eventsManager.raiseEvent(event);
    }
}
