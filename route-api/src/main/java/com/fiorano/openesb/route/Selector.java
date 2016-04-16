package com.fiorano.openesb.route;


import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.utils.exception.FioranoException;

public interface Selector {

    boolean isMessageSelected(String content) throws FioranoException;

}
