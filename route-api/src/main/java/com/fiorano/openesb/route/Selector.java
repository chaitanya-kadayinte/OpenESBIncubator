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


package com.fiorano.openesb.route;


import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.utils.exception.FioranoException;

/**
 * <p><strong> </strong> represents </p>
 *
 * @author FSIPL
 * @version 1.0
 * @created December 30, 2004
 */

public interface Selector {

    boolean isMessageSelected(String content) throws FioranoException;

}
