package com.fiorano.openesb.microservice.launch.impl.cl;

import java.net.URL;
import java.net.URLClassLoader;

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
