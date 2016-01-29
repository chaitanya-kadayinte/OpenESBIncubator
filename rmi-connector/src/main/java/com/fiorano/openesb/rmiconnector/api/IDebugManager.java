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

import java.rmi.Remote;

/**
 * This Interface is not implemented yet.
 *
 */
public interface IDebugManager extends Remote {

	public void deleteServiceInstance(String name);

	public void startServiceInstance(String name);

	public void stopServiceInstance(String name);

	public void createRoute(String name, String source, String target);

	public void changeRouteSource(String name, String oldSource, String newSource);

	public void changeRouteTarget(String name, String oldTarget, String newTarget);

	public void deleteRoute(String name);

	public void setConfig(String serviceInstanceName, byte[] config);

}

//(*) Delete a service instance
//(*) Stop a service instance
//(*) Start a service instance
//(*) Change the configuration of a service instance
//(*) Create a route
//(*) Change the route
//(*) Delete the route

//(*) Create a service instance

//(*) Change the configuration of a service instance
//(*) Change log parameters 
//(*) Add Breaktpoint
//(*) Remove Breakpoint
