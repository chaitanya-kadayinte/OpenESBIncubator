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

import com.fiorano.openesb.utils.ClarkName;
import com.fiorano.openesb.utils.SAXUtil;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


public class XMLRootElementFinder extends DefaultHandler{
    private XMLRootElementFinder(){}

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException{
        throw new NoMoreSAXParsingException(ClarkName.toClarkName(namespaceURI, localName));
    }

    public static String findRootElement(InputSource inputSource)
            throws SAXException, ParserConfigurationException, IOException{
        SAXParser parser = SAXUtil.createSAXParserFactory(true, true, false).newSAXParser();
        try{
            parser.parse(inputSource, new XMLRootElementFinder());
            return null;
        } catch(NoMoreSAXParsingException e){
            return (String)e.getData();
        }
    }

    public static String findRootElement(Reader reader)
            throws SAXException, ParserConfigurationException, IOException{
        return findRootElement(new InputSource(reader));
    }

    public static String findRootElement(InputStream stream)
            throws SAXException, ParserConfigurationException, IOException{
        return findRootElement(new InputSource(stream));
    }
}
