/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.ConsumerConfiguration;
import com.fiorano.openesb.transport.PortConfiguration;

import java.util.List;

public interface RouteConfiguration {

    PortConfiguration getSourceConfiguration();

    PortConfiguration getDestinationConfiguration();

    ConsumerConfiguration getConsumerConfiguration();

    List<RouteOperationConfiguration> getRouteOperationConfigurations();

    void setSourceConfiguration(PortConfiguration sourceConfiguration);

    void setDestinationConfiguration(PortConfiguration destinationConfiguration);

    String getJmsSelector();

    void setJmsSelector(String jmsSelector);

}
