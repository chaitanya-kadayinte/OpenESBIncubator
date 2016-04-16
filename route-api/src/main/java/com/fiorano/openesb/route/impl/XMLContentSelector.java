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
