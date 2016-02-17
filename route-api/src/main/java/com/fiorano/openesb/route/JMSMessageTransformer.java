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

import javax.jms.Message;

public interface JMSMessageTransformer {

    /**
     * The function takes in a message and returns the transformed message.
     * Usage : Route provides an api which allows this call back to be set.
     * On receipt of any message a transformation can be applied.
     */
    public String transform(Message msg)
            throws Exception;
}
