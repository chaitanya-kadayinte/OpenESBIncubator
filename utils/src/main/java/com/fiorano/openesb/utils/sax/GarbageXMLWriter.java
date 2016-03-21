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

public class GarbageXMLWriter extends AbstractXMLWriter{

    // collect all the garbage
    protected boolean _startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        return true;
    }
}