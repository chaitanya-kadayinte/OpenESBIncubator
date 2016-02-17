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