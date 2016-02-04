package com.fiorano.openesb.utils;

/**
 * Created by Janardhan on 1/5/2016.
 */
public class Util{

    public static int hashCode(Object obj){
        return obj!=null ? obj.hashCode() : 0;
    }

    // Returns true if the specified arguments are equal, or both null.
    public static boolean equals(Object a, Object b){
        return a==null
                ? b==null
                : b!=null && a.equals(b);
    }

    public static Boolean toBoolean(boolean b){
        return b ? Boolean.TRUE : Boolean.FALSE;
    }

    public static boolean isWindows(){
        String os = System.getProperty("os.name");

        if(os==null)
            return false;

        int index = os.toLowerCase().indexOf("win");

        return (index!=-1);
    }
}
