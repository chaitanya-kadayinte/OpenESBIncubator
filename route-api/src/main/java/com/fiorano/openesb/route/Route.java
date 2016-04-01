/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2014, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 * <p>
 * Created by chaitanya on 11-01-2016.
 */

/**
 * Created by chaitanya on 11-01-2016.
 */
package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.transport.PortConfiguration;

public interface Route<M extends Message> {

    void start() throws Exception;
    void stop() throws Exception;
    void delete();
    void changeTargetDestination(PortConfiguration portConfiguration) throws Exception;
    void changeSourceDestination(PortConfiguration portConfiguration) throws Exception;
    void handleMessage(M message) throws Exception;
    String getSourceDestinationName();
    String getTargetDestinationName();
}
