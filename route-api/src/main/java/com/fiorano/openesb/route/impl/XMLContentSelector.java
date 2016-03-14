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
package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.Selector;
import javax.xml.transform.sax.SAXSource;
import java.util.Iterator;
import java.util.Map;
import java.io.StringReader;

import com.fiorano.openesb.utils.exception.FioranoException;
import net.sf.saxon.trans.XPathException;
import org.xml.sax.InputSource;
import net.sf.saxon.sxpath.XPathEvaluator;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.trans.IndependentContext;

public class XMLContentSelector implements Selector {

    private boolean bUseXPath_1_0 ;
    private XmlSelectorConfiguration selectorConfiguration;

    public XMLContentSelector(XmlSelectorConfiguration selectionConfiguration) {
        this.selectorConfiguration = selectionConfiguration;
    }

    public boolean isMessageSelected(String message) throws FioranoException {
        if(message==null)
            return false;

        InputSource is = new InputSource(new StringReader(message));
        SAXSource ss = new SAXSource(is);

        XPathEvaluator evaluator = new XPathEvaluator();


        if(bUseXPath_1_0){
            //In SAXON, for XPath 1.0 support context.isInBackwardsCompatibleMode
            // should return true.
            evaluator.setStaticContext(
                    new IndependentContext(){
                        public boolean isInBackwardsCompatibleMode(){
                            return true;
                        }
                    });
        }

        // Copied from CBR Code.
        IndependentContext stx = evaluator.getStaticContext();

        if(selectorConfiguration.getNsPrefixMap()!=null){
            Iterator it = selectorConfiguration.getNsPrefixMap().entrySet().iterator();

            while(it.hasNext()){
                Map.Entry entry = (Map.Entry)it.next();
                if (((entry.getKey()) instanceof String) && ((entry.getValue()) instanceof String))
                     stx.declareNamespace((String)entry.getKey(),(String)entry.getValue());
            }
        }

        XPathExpression xpath = null;
        Object result;
        try {
            xpath = evaluator.createExpression(selectorConfiguration.getXpath());
            result = xpath.evaluateSingle(ss);
        } catch (XPathException e) {
            throw new FioranoException(e);
        }


        return result!=null && (!(result instanceof Boolean) || result.equals(Boolean.TRUE));

    }

    public void setUseXPath_1_0(boolean bUseXPath_1_0){
        this.bUseXPath_1_0 = bUseXPath_1_0;
    }

}
