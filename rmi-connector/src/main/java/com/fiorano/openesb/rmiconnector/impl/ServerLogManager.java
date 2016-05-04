package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.utils.ConfigReader;
import com.fiorano.openesb.utils.exception.FioranoException;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ServerLogManager {
    public String getTESLastOutLogs(int numberOfLines)  throws FioranoException {
        Properties p = new Properties();
        try {
            ConfigReader.readPropertiesFromFile(new File(System.getProperty("user.dir") +File.separator+"etc"+File.separator+"org.ops4j.pax.logging.cfg"),p );
            String path = p.getProperty("log4j.appender.fiorano.file");
            if(path.contains("${karaf.data}")){
                path = path.replace("${karaf.data}", System.getProperty("user.dir")+File.separator+"data");
            }
            if(path.contains("${karaf.base}")){
                path = path.replace("${karaf.base}", System.getProperty("user.dir"));
            }

            byte[] encoded = Files.readAllBytes(Paths.get(new File(path).toURI()));
            return new String(encoded);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        /*try {
            String path = ((FileAppender)Logger.getRootLogger().getAppender("log4j.appender.fiorano")).getFile();
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return "";
    }

    public String getTESLastErrLogs(int numberOfLines)  throws FioranoException{
        return "";
    }

    public String getMQLastErrLogs(int numberOfLines)  throws FioranoException{
        return "";
    }

    public String getMQLastOutLogs(int numberOfLines)  throws FioranoException{
        return "";
    }

    public void clearTESOutLogs()  throws FioranoException{
        Properties p = new Properties();
        try {
            ConfigReader.readPropertiesFromFile(new File(System.getProperty("user.dir") +File.separator+"etc"+File.separator+"org.ops4j.pax.logging.cfg"),p );
            String path = p.getProperty("log4j.appender.fiorano.file");
            if(path.contains("${karaf.data}")){
                path = path.replace("${karaf.data}", System.getProperty("user.dir")+File.separator+"data");
            }
            if(path.contains("${karaf.base}")){
                path = path.replace("${karaf.base}", System.getProperty("user.dir"));
            }
            new PrintWriter(path).close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
