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


package com.fiorano.openesb.application;

import java.util.HashMap;

/**
 * @author Santhosh Kumar T
 */
public class MapThreadLocale extends ThreadLocal{
    private static MapThreadLocale SINGLETON = new MapThreadLocale();

    public static MapThreadLocale getInstance(){
        return SINGLETON;
    }

    private MapThreadLocale(){}

    protected Object initialValue(){
        return new HashMap();
    }

    public HashMap getMap(){
        return (HashMap)get();
    }
}
