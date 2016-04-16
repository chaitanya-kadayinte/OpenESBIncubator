package com.fiorano.openesb.utils;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.EntityResolver;

import java.util.Map;

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
