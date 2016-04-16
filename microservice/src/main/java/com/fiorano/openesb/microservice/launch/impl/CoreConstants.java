package com.fiorano.openesb.microservice.launch.impl;


public interface CoreConstants
{
    public final static String NAME_DELIMITER = "__";

    public final static String EVENT_PROCESS_REGISTRATION_PREFIX = "Fiorano.Esb.Peer.Core:ServiceType=EventProcess,Name=";

    public final static String BUSINESS_COMPONENT_REGISTRATION_PREFIX = "Fiorano.Esb.Peer.Core:ServiceType=BusinessComponent,Name=";

    public final static String SYSTEM_ID_PREFIX = "ESBX__SYSTEM";

    public final static String SERVICE_PROVIDER_CF = "serviceprovidercf";

    public final static String CONFIGURATION_FILE_NAME = "tifosi.cfg";

    public final static String STARTUP_CONFIGURATION_FILE_NAME = "tifstartup.cfg";

    public final static String APPLICATION_CLOSED_CONNECTION = "APPLICATION_CLOSED_CONNECTION";

    public final static String APP_VERSION_DELIM = ":";

    public final static String NOT_AVAILABLE = "Not Available";
}