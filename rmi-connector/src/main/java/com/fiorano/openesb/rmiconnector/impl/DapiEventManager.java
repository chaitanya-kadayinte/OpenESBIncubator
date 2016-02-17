package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.events.ApplicationEvent;
import com.fiorano.openesb.events.Event;
import com.fiorano.openesb.events.EventListener;
import com.fiorano.openesb.events.EventsManager;
import com.fiorano.openesb.rmiconnector.api.IEventProcessManagerListener;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.queue.FioranoQueueImpl;
import com.fiorano.openesb.utils.queue.IFioranoQueue;

import java.rmi.NoSuchObjectException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.*;

/**
 * @author : amit,Suresh
 * @version : 2.0
 * @Date : Aug 12, 2008
 */
public class DapiEventManager implements EventListener {

    //Fes Event Manager
    private EventsManager fesEventManager;

    //Application Specific Event listeners
    private final Hashtable<String, IEventProcessManagerListener> appEventListeners = new Hashtable<String, IEventProcessManagerListener>();

     private static final String DAPICONSTANT = "$";

    private static final String DELIMITER = "__";
    //Max Number of Events to Store in Queue. If the count is -1. It is INFINITE
    private int maxBufferedEventsCount = 256;
    //Queue to Store the System Events
    private final transient IFioranoQueue eventsQueue;
    //Queue to Store the Configuration Events
    //Condition to check before reading Events from Queue.
    private boolean isConnectionAlive;
    //Separate Thread to read events from Queue.
    private ReceiverThread receiverThread;
    //Configuration Repository Event Receiver Thread
    //Service to execute event tasks in a thread pool
    private ExecutorService exec;

    /**
     * stores the ipaddress(s) of all clients connected to the fes.
     * key-handleID vs object - ipaddress
     */
    private Hashtable<String, String> clientIPAddresses;


    /**
     * Constructor
     *
     * @param fesEventManager fes events manager
     */
//    public DapiEventManager(IFESEventsManager fesEventManager) {
//        this.fesEventManager = fesEventManager;
//    }

    /**
     * Constructor
     *
     * @param fesEventManager fesEvents Mangaer
     */
    public DapiEventManager(EventsManager fesEventManager) {
        this.fesEventManager = fesEventManager;
        this.eventsQueue = new FioranoQueueImpl();
    }

    public void setFesEventManager(EventsManager fesEventManager) {
        this.fesEventManager = fesEventManager;
    }

    public void setClientIPAddresses(Hashtable<String, String> clientIPAddresses) {
        this.clientIPAddresses = clientIPAddresses;
    }

    /**
     * On FES Event
     *
     * @param event FES event
     * @throws FioranoException
     */
    public void onEvent(Event event) throws FioranoException {
        pushEventToQueue(event);
    }

    private void pushEventToQueue(Event event) {
        if (event == null) {
            return;
        }
        synchronized (eventsQueue) {
            if (maxBufferedEventsCount < 0 || eventsQueue.getSize() < maxBufferedEventsCount) {
                eventsQueue.pushWithNotify(event);
                return;
            }
        }
       System.out.println("cannot deliover event");
    }


    public Event readEvent(long timeout) throws FioranoException {
        Object obj = eventsQueue.popWithWait(timeout);

        // long indicated that the connection handle is closed.
        if (obj instanceof Long)
            return null;
        else
            return (Event) obj;
    }

    private void startReceiving() {
        receiverThread = new ReceiverThread();
        receiverThread.setName("DAPI_EVENT_MANAGER_THREAD_FOR_RECEIVING_SYSTEM_EVENTS");
        receiverThread.start();
    }

    private void stopRunning() {
        isConnectionAlive = false;
        receiverThread = null;
    }

    class ReceiverThread extends Thread {
        public void run() {
            while (isConnectionAlive) {
                try {
                    Event event = readEvent(0);
                    if (event != null)
                        processEvent(event);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    isConnectionAlive = false;
                }
            }
        }
    }

    private void processEvent(Event event) {
         if (event instanceof ApplicationEvent) {
             final ApplicationEvent appEvent = (ApplicationEvent) event;
             final String appGUID = appEvent.getApplicationGUID();
             final float appVersion = Float.parseFloat(appEvent.getApplicationVersion());
             synchronized (appEventListeners) {
                 Enumeration keys = appEventListeners.keys();
                 while (keys.hasMoreElements()) {
                     String key = (String) keys.nextElement();
                     String substr = key.substring(key.indexOf(DAPICONSTANT) + 1);
                     if (substr.equalsIgnoreCase(appGUID + DELIMITER + appVersion)) {
                         final String handleId = key.substring(0, key.indexOf(DAPICONSTANT));
                         final IEventProcessManagerListener appEventListener = appEventListeners.get(key);
                         exec.execute(new Runnable() {
                             public void run() {
                                 try {
                                     if (appEvent.getApplicationEventType() == ApplicationEvent.ApplicationEventType.APPLICATION_LAUNCHED)
                                         appEventListener.eventProcessStarted(appVersion);
                                     else if (appEvent.getApplicationEventType() == ApplicationEvent.ApplicationEventType.APPLICATION_LAUNCH_STARTED)
                                         appEventListener.eventProcessStarting(appVersion);
                                     else if (appEvent.getApplicationEventType() == ApplicationEvent.ApplicationEventType.APPLICATION_STOPPED)
                                         appEventListener.eventProcessStopped(appVersion);
                                 } catch (NoSuchObjectException e) {
                                     //ignore
                                     // Dont want to log unnecessarily
                                 } catch (Throwable t) {
                                     t.printStackTrace();
                                 }
                             }
                         });
                     }
                 }
             }
         }
    }


    public void registerApplicationEventListener(IEventProcessManagerListener appEventListener, String appGUID, float appVersion, String handleId) {
        String key = handleId + DAPICONSTANT + appGUID + DELIMITER +appVersion;
        if (appEventListeners.containsKey(key))
            appEventListeners.remove(key);
        appEventListeners.put(key, appEventListener);
    }

    public void unRegisterApplicationEventListener(IEventProcessManagerListener appEventListener, String appGUID, float appVersion, String handleId) {
        String key = handleId + DAPICONSTANT + appGUID +DELIMITER+ appVersion;
        if (appEventListeners.containsKey(key))
            appEventListeners.remove(key);
    }

    public void startEventListener() {
        // In customer development scenario, a lot of changes would be made with many events received by eStudio amid frequent pauses.
        // In the default cached thread pool, any pause greater than 60 secs would terminate all threads in the pool leading to
        // significant repeated thread creation overhead. Resolving by setting the keepAliveTime to 30 mins.
        // To prevent initial application launch to appear delayed, starting with 5 threads in the core pool rather than 0 threads.

//        exec = Executors.newCachedThreadPool(new DaemonThreadFactory());
        exec = new ThreadPoolExecutor(5, Integer.MAX_VALUE,
                30L, TimeUnit.MINUTES,
                new SynchronousQueue<Runnable>(),
                new DaemonThreadFactory());
        if (fesEventManager != null)
            fesEventManager.registerEventListener(this);

        isConnectionAlive = true;
        startReceiving();
    }


    public void stopEventListener() {
        if (fesEventManager != null)
            fesEventManager.unRegisterEventListener(this);

        stopRunning();
        removeAllListeners();
        exec.shutdown();
    }

    public void unregisterAllApplicationListeners(String handleID) {
        synchronized (appEventListeners) {
            Enumeration keys = appEventListeners.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String substr = key.substring(0, key.indexOf(DAPICONSTANT));
                if (substr.equalsIgnoreCase(handleID)) {
                    appEventListeners.remove(key);
                }
            }
        }
    }

    private void removeAllListeners() {
        appEventListeners.clear();
    }

    public void unRegisterOldListeners(String handleID) {
        unregisterAllApplicationListeners(handleID);
    }

    private class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        }
    }
}
