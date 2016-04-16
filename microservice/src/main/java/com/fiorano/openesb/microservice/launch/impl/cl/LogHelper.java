
package com.fiorano.openesb.microservice.launch.impl.cl;

public class LogHelper {
    public static Object getOutMessage(String clm, int i, String log) {
        return log;
    }

    public static Throwable getOutMessage(String clm, int i, String uniqueComponentIdentifier, String s) {
        return new Exception(uniqueComponentIdentifier);
    }

    public static String getErrMessage(String clm, int i, String s) {
        return s;
    }
}
