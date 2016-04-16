package com.fiorano.openesb.route.impl;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import java.util.logging.Logger;

public class RouteTransformationErrorListener implements ErrorListener {
    //TODO - implement logger
    Logger logger;

    public RouteTransformationErrorListener(Logger logger)
    {
        logger = logger;
    }
    public void warning(TransformerException exception) throws TransformerException {

    }

    public void error(TransformerException exception) throws TransformerException {

    }

    public void fatalError(TransformerException exception) throws TransformerException {

    }
}

