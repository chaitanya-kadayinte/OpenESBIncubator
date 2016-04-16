package com.fiorano.openesb.applicationcontroller;

public class ApplicationControllerConstants
{
    /**
     * Suffix for routes created when a debugger is set on the routes.
     */
    public static final String DEBUG_ROUTE_SUFFIX = "DEBUG";

    public static final String ESB_OBJECTS_NAME_SEPARATOR = "__";

    /**
     * Suffix for destinations used for receiving messages
     * on routes with breakpoint.
     */
    public static final String RECEIVE_DESTINATION_SUFFIX = "__C";

    /**
     * Suffix for destination that are linked with the other
     * end of the route on which break point has been set.
     */
    public static final String SEND_DESTINATION_SUFFIX = "__D";

    public static final String RUNNING_APPLICATION_LIST = "RUNNING_APPLICATION_LIST";

    public static final String FIORANO_INITCONTEXT_FACTORY= "fiorano.jms.runtime.naming.FioranoInitialContextFactory";

    //Log Entry for Application Controller Manager
    public static final String APPLICATION_CREATED = "APPLICATION_CREATED";

    //initial Capacity of HashTable that is created in Application Controller
    public static final int INITIAL_CAPACITY = 5;
}
