package com.fiorano.openesb.route.impl;

import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class RouteURIResolver implements URIResolver {
    String data = null;

    RouteURIResolver(String data){
        this.data = data;
    }

    public Source resolve(String href, String base)
            throws TransformerException{
        return new StreamSource(new StringReader(data));
    }
}

