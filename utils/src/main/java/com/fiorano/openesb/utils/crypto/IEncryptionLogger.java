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
package com.fiorano.openesb.utils.crypto;

/**
 * Created with IntelliJ IDEA.
 * User: chaitanya
 * Date: 7/1/13
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IEncryptionLogger {

    public void trace(String message);

    public void debug(String message);

    public void info(String message);

    public void warn(String message);

    public void error(String message);

    public void trace(String message, Throwable t);

    public void debug(String message, Throwable t);

    public void info(String message, Throwable t);

    public void warn(String message, Throwable t);

    public void error(String message, Throwable t);
}
