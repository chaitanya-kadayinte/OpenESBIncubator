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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class NestedElementWriter extends AbstractXMLWriter{

    // return false if can't be handled
    protected boolean _startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try{
            StringWriter writer = new StringWriter();
            NestedElementCreator creator = new NestedElementCreator(new StreamResult(writer), true, true);
            creator.start(reader, uri, localName, qName, attributes);
            setObject(writer);
            return true;
        } catch(TransformerConfigurationException e){
            throw new SAXException(e);
        }
    }

    public void _endElement(String uri, String localName, String qName) throws SAXException {
        super._endElement(uri, localName, qName);
    }
}