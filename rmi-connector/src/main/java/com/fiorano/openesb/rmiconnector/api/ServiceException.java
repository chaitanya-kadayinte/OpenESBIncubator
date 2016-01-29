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

package com.fiorano.openesb.rmiconnector.api;

/**
 * This exception is thrown by the enterprise server to rmi clients if some exception has occured on server side while performing operation
 * @author FSTPL
 * @version 10
 * 
 */
public class ServiceException extends Exception {


   	public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
	public static final String ZIP_TOO_BIG_ERROR = "ZIP_TOO_BIG_ERROR";
    private static final long serialVersionUID = 869149237802891848L;

    private final String errorCode;



    /**
     * Default Constructor
     */
	public ServiceException() {
		this("Internal Server Error", INTERNAL_ERROR);
	}

    /**
     * Constructs a service exception with specified error code and exception
     * @param errorCode Error code of the exception
     * @param e Exception thrown
     */
    public ServiceException(String errorCode, Exception e){
        super(e.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructs a service exception with specified message
     * @param message error message
     */
	public ServiceException(String message) {
		this(INTERNAL_ERROR,message);
	}

    /**
     * Constructs a service exception with specified message and error code
     * @param message error message
     * @param errorCode error code
     */
	public ServiceException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

    /**
     * Returns error code
     * @return int - Error code
     */
	public String getErrorCode() {
		return errorCode;
	}

}
