
package com.fiorano.openesb.application;

import java.util.HashMap;

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
