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





package com.fiorano.openesb.microservice.launch.impl.cl;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * <p><strong> </strong> represents </p>
 *
 * @author FSIPL
 * @created April 15, 2005
 * @version 1.0
 */
public class ESBURLClassLoader extends URLClassLoader
{
    /**
     * @param urls
     * @param parent
     */
    public ESBURLClassLoader(URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
    }

    /**
     * Returns name for object
     *
     * @return
     */
    public String getName()
    {
        return toString();
    }
}
