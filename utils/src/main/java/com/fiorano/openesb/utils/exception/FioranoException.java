package com.fiorano.openesb.utils.exception;

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
