/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.fiorano.openesb.microservice.launch.impl.cl;

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
