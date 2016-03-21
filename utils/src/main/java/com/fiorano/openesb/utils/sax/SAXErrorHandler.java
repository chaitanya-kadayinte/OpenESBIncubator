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

import com.fiorano.openesb.utils.ErrorListener;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SAXErrorHandler implements ErrorHandler {
    private final ErrorListener delegate;

    public SAXErrorHandler(ErrorListener delegate){
        this.delegate = delegate;
    }

    public void error(SAXParseException exception) throws SAXException {
        try{
            delegate.error((Exception)exception);
        } catch(SAXException e){
            throw e;
        } catch(Exception e){
            throw new SAXException(e);
        }
    }

    public void fatalError(SAXParseException exception) throws SAXException {
        try{
            delegate.fatalError((Exception)exception);
        } catch(SAXException e){
            throw e;
        } catch(Exception e){
            throw new SAXException(e);
        }
    }

    public void warning(SAXParseException exception) throws SAXException {
        try{
            delegate.warning((Exception)exception);
        } catch(SAXException e){
            throw e;
        } catch(Exception e){
            throw new SAXException(e);
        }
    }
}