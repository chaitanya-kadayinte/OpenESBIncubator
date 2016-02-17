/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */
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

