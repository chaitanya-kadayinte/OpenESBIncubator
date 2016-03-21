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





package com.fiorano.openesb.utils;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import java.io.StringReader;

/**
 * @author Santhosh Kumar T
 *         Created On: Sep 24, 2004 1:27:04 PM
 */
public class NamespaceSupportReader extends XMLFilterImpl {
    private XNamespaceSupport nsSupport;
    private boolean needNewContext = true;

    public NamespaceSupportReader(XNamespaceSupport nsSupport){
        this.nsSupport = nsSupport;
    }

    public NamespaceSupportReader(){
    }

    public XNamespaceSupport getNamespaceSupport(){
        return nsSupport;
    }

    public void startDocument() throws SAXException {
        if(nsSupport==null)
            nsSupport = new XNamespaceSupport();
        super.startDocument();
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if(needNewContext){
            nsSupport.pushContext();
            needNewContext = false;
        }
        nsSupport.declarePrefix(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        nsSupport.popContext();
        super.endElement(uri, localName, qName);
    }

    public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts) throws SAXException {
        if(needNewContext) nsSupport.pushContext();
        super.startElement(namespaceURI, localName, qualifiedName, atts);
        needNewContext = true;
    }

    public static void main(String[] args) throws Exception {
        String xml = "<test xmlns:a=\"hai\" xmlns:b=\"hello\"></test>";  //NOI18N
        XMLReader reader = SAXUtil.createSAXParserFactory(true, true, false).newSAXParser().getXMLReader();
        NamespaceSupportReader filter = new NamespaceSupportReader();
        filter.setParent(reader);
        filter.parse(new InputSource(new StringReader(xml)));
    }
}