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





package com.fiorano.openesb.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class I18N {
    private static String getPackage(Class clazz){
        String name = clazz.getName();
        int index = name.lastIndexOf('.');
        return index==-1 ? name : name.substring(0, index);
    }

    private static synchronized ResourceBundle getBundle(Class clazz){
        if(System.getProperty("APP_NAME")!=null && System.getProperty("APP_NAME").startsWith("api")){
            try{
                Class.forName(getPackage(clazz)+".APIBundle");
                return ResourceBundle.getBundle(getPackage(clazz)+".APIBundle", Locale.getDefault(), ClassUtil.getClassLoader(Class.forName(getPackage(clazz)+".APIBundle")));
            } catch (ClassNotFoundException e){
                return ResourceBundle.getBundle(getPackage(clazz)+".Bundle", Locale.getDefault(), ClassUtil.getClassLoader(clazz));
            }
        }
        return ResourceBundle.getBundle(getPackage(clazz)+".Bundle", Locale.getDefault(), ClassUtil.getClassLoader(clazz)); //NOI18N
    }

    public static String getMessage(Class clazz, String key){
        return getBundle(clazz).getString(key);
    }

    public static String getMessage(Class clazz, String key, Object params[]){
        return MessageFormat.format(getBundle(clazz).getString(key), params);
    }

    public static String getMessage(Class clazz, String key, Object param){
        return MessageFormat.format(getBundle(clazz).getString(key), new Object[]{param});
    }

    public static String getMessage(Class clazz, String key, Object param1, Object param2){
        return MessageFormat.format(getBundle(clazz).getString(key), new Object[]{param1, param2});
    }

    public static String getMessage(Class clazz, String key, Object param1, Object param2, Object param3){
        return MessageFormat.format(getBundle(clazz).getString(key), new Object[]{param1, param2, param3});
    }

    public static String getMessage(Class clazz, String key, Object param1, Object param2, Object param3, Object param4){
        return MessageFormat.format(getBundle(clazz).getString(key), new Object[]{param1, param2, param3, param4});
    }

    public static String getMessage(Class clazz, String key, Object param1, Object param2, Object param3, Object param4, Object param5){
        return MessageFormat.format(getBundle(clazz).getString(key), new Object[]{param1, param2, param3, param4, param5});
    }

    public static String getMessage(Class clazz, String key, Object param1, Object param2, Object param3, Object param4, Object param5, Object param6){
        return MessageFormat.format(getBundle(clazz).getString(key), new Object[]{param1, param2, param3, param4, param5, param6});
    }
}
