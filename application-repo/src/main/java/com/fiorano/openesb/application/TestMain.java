package com.fiorano.openesb.application;

import com.fiorano.openesb.application.application.Application;
import com.fiorano.openesb.application.application.ApplicationParser;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.io.File;

/**
 * Created by Janardhan on 1/6/2016.
 */
public class TestMain {
    public static void main(String [] args){
        try {
           Application application = ApplicationParser.readApplication(new File("D:\\sources_dec22\\installer\\esb\\server\\repository\\applications\\SIMPLECHAT\\1.0"), false);
          System.out.println(application.toString());
        } catch (FioranoException e) {
            e.printStackTrace();
        }
    }
}
