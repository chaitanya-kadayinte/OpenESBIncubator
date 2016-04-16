package com.fiorano.openesb.utils.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GarbageXMLWriter extends AbstractXMLWriter{

    // collect all the garbage
    protected boolean _startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        return true;
    }
}