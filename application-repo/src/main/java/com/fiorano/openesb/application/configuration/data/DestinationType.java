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

package com.fiorano.openesb.application.configuration.data;

import java.io.Serializable;

/**
 *
 * Types 1) Topic 2) Queue
 * Denotes the Destination type of a port
 *
 * @author FSTPL
 * @since Jun 21, 2010 4:24:07 PM
 * @version 10
 */
public enum DestinationType implements Serializable {
    /**
     * Denotes Topic destination Type
     */
    TOPIC,
    /**
     * Denote Queue destination Type
     */
    QUEUE
}
