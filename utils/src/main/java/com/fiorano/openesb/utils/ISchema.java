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

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Venkat
 * Date: Jul 11, 2008
 * Time: 11:03:40 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ISchema extends LSResourceResolver, EntityResolver {
    /**
     * Null structure
     */
    int NONE = 0;
    /**
     * Format definition is XSD schema (default)
     */
    int XSD = 1;
    /**
     * Format definition is DTD
     */
    int DTD = 2;

    int getDefinitionType();

    String getStructure();

    String getRootElementName();

    String getTargetNamespace();

    Map getImportedStructures();

    void setDefinitionType(int defType);

    void setStructure(String schema);

    void setRootElementName(String name);

    void setTargetNamespace(String targetNS);

    void addImportedStructure(String ns, String xsd);

    Object clone();

    void addImportedStructures(ISchema schema);
}
