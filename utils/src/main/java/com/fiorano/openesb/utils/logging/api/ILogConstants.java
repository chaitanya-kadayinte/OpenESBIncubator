package com.fiorano.openesb.utils.logging.api;

public interface ILogConstants
{
    // defines the parent logger for all fiorano products
    public String PARENT_LOGGER = "fiorano";

    public int      FILE_HANDLER_TYPE = 1;
    public int      CONSOLE_HANDLER_TYPE = 2;
    public int      UNKNOWN_HANDLER_TYPE = 3;

    public String LOG_HANDLER = "java.util.logging.handler";
    public String LOG_HANDLER_DEF = "java.util.logging.FileHandler";

//    public String   ALL_Client_ID = "ALL_Client_ID";

    // log record suffix
    //  public String   LOG_RECORD_SUFFIX = "#### ";
}
