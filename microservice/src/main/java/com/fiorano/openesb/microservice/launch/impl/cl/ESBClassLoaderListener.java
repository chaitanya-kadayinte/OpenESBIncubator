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
