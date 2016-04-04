/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 * <p/>
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */
package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.Selector;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.xml.NameSpaceContextImpl;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

public class XMLContentSelector implements Selector {

    private final XPath xPath;
    private XmlSelectorConfiguration selectorConfiguration;

    public XMLContentSelector(XmlSelectorConfiguration selectionConfiguration) {
        this.selectorConfiguration = selectionConfiguration;
        XPathFactory xpf = XPathFactory.newInstance();
        xPath = xpf.newXPath();
        xPath.setNamespaceContext(new NameSpaceContextImpl(selectionConfiguration.getNsPrefixMap()));
    }

    public boolean isMessageSelected(String message) throws FioranoException {
        Object evaluate;
        try {
            evaluate = xPath.evaluate(selectorConfiguration.getXpath(), new InputSource(new StringReader(message)), XPathConstants.BOOLEAN);
            return (boolean) evaluate;
        } catch (XPathExpressionException e) {
            throw new FioranoException(e);
        }
    }
}
