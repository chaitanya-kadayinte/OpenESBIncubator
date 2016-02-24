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

/**
 * <p><strong> </strong> represents </p>
 *
 * @author FSIPL
 * @created April 14, 2005
 * @version 1.0
 */
public interface ESBClassLoaderListener
{
    /**
     * @param oldClassLoader
     * @param newClassLoader
     */
    public void beforeUpdatingClassLoder(ClassLoader oldClassLoader, ClassLoader newClassLoader);

    /**
     * @param oldClassLoader
     * @param newClassLoader
     */
    public void afterUpdatingClassLoder(ClassLoader oldClassLoader, ClassLoader newClassLoader);
}
