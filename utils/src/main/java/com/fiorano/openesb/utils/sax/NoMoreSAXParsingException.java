package com.fiorano.openesb.utils.sax;

import org.xml.sax.SAXException;

public class NoMoreSAXParsingException extends SAXException{
    private Object data;

    public NoMoreSAXParsingException(Object data){
        super((String)null);
        this.data = data;
    }

    public NoMoreSAXParsingException(String message, Object data){
        super(message);
        this.data = data;
    }

    public NoMoreSAXParsingException(Object data, Exception e){
        super(e);
        this.data = data;
    }

    public NoMoreSAXParsingException(String message, Exception e, Object data){
        super(message, e);
        this.data = data;
    }

    public Object getData(){
        return data;
    }
}