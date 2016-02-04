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

package com.fiorano.openesb.microservice.ccp.event.common;


/**
 * This class serves as a factory for getting new instances of {@link com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent}. The particular implementation of
 * request event returned by factory method {@link #getRequestEvent(int)} depends on the argument passed to the method.
 * @author FSTPL
 * @version 10
 */
public class RequestEventFactory {

    /**
     * This integer constant is used to represent request of type {@link com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent}.
     */
    public static final int DATA_REQUEST_EVENT = 1;

    /**
     * This integer constant is used to represent request of type {@link com.fiorano.openesb.microservice.ccp.event.common.LogLevelRequestEvent}.
     */
    public static final int LOG_LEVEL_REQUEST_EVENT = 2;

    /**
     * Factory method to obtain a new instance of {@link com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent} as per the argument passed to the method.
     * @param requestType Type of {@link com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent} to be returned. The request type should be one of the
     * constant integers specified in this class. Otherwise, an exception saying that request type is not valid may be thrown.
     * @return DataRequestEvent - Request type represented by the passed argument
     * @exception IllegalArgumentException if the request type is not found.
     */
    public static DataRequestEvent getRequestEvent(int requestType) {
        switch (requestType) {
            case DATA_REQUEST_EVENT:
                return new DataRequestEvent();
            case LOG_LEVEL_REQUEST_EVENT:
                return new LogLevelRequestEvent();
            default:
                throw new IllegalArgumentException("EVENT_NOT_FOUND");
        }
    }
}
