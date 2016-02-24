/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */





package com.fiorano.openesb.utils.logging.api;

/**
 * <p><strong> </strong> represents </p>
 *
 * @author FSIPL
 * @created March 29, 2005
 * @version 1.0
 */
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
