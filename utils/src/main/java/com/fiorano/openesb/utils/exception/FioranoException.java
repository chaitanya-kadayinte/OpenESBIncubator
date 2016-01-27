package com.fiorano.openesb.utils.exception;

/**
 * Created by Janardhan on 1/5/2016.
 */
public class FioranoException extends Exception {

    public FioranoException(Throwable th){
        super(th);
    }

    public FioranoException(String error) {
        super(error);
    }

    public FioranoException() {

    }
}
