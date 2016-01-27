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
 * Created by chaitanya on 14-01-2016.
 * <p>
 * Created by chaitanya on 14-01-2016.
 */

/**
 * Created by chaitanya on 14-01-2016.
 */
package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.Route;
import com.fiorano.openesb.route.RouteService;
import com.fiorano.openesb.transport.TransportService;
import com.fiorano.openesb.transport.impl.jms.JMSMessage;
import com.fiorano.openesb.transport.impl.jms.JMSPort;
import com.fiorano.openesb.transport.impl.jms.JMSPortConfiguration;


public class JMSRouteServiceImpl implements RouteService<JMSRouteConfiguration> {
    private TransportService<JMSPort,JMSMessage,JMSPortConfiguration> transportService;


    public JMSRouteServiceImpl(TransportService<JMSPort,JMSMessage,JMSPortConfiguration>  transportService) {
        this.transportService = transportService;
    }

    public Route createRoute(JMSRouteConfiguration routeConfiguration) throws Exception {
        return new JMSRouteImpl(transportService,routeConfiguration);
    }


}
