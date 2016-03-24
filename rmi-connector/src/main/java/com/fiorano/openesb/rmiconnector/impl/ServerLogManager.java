package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.utils.exception.FioranoException;

/**
 * Created by Janardhan on 3/23/2016.
 */
public class ServerLogManager {
    public String getTESLastOutLogs(int numberOfLines)  throws FioranoException {
        return null;
    }

    public String getTESLastErrLogs(int numberOfLines)  throws FioranoException{
        return null;
    }

    public String getMQLastErrLogs(int numberOfLines)  throws FioranoException{
        return null;
    }

    public String getMQLastOutLogs(int numberOfLines)  throws FioranoException{
        return null;
    }

    public void clearTESOutLogs()  throws FioranoException{
        
    }

    public void clearTESMQOutLogs()  throws FioranoException{
        
    }

    public void clearTESErrLogs()  throws FioranoException{
        
    }

    public void clearTESMQErrLogs()  throws FioranoException{
        
    }

    public void exportFESLogs(String absolutePath, String absolutePath1)  throws FioranoException{
        
    }
}
