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





package com.fiorano.openesb.utils;

/**
 * @author Santhosh Kumar T
 * Created On: Oct 5, 2004 5:32:11 AM
 */
public interface ErrorListener {
    public void warning(Exception exception) throws Exception;
    public void error(Exception exception) throws Exception;
    public void fatalError(Exception exception) throws Exception;
}