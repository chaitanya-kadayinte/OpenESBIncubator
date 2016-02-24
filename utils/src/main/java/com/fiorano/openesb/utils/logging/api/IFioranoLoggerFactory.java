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





package com.fiorano.openesb.utils.logging.api;

import com.fiorano.openesb.utils.exception.FioranoException;
import org.apache.log4j.Logger;

import java.util.Hashtable;

/**
 * <p><strong> </strong> represents </p>
 *
 * @author FSIPL
 * @created December 30, 2004
 * @version 1.0
 */
public interface IFioranoLoggerFactory
{

    public Logger getLog4JLogger(String category)
        throws FioranoException;

    /**
     * All Module calls this method to get instance of IFioranoLogger
     *
     * @param category String
     * @return IFioranoLogger
     * @throws FioranoException
     */
    public IFioranoLogger getLogger(String category)
        throws FioranoException;

    /**
     * Sets trace level for object
     *
     * @param component
     * @param level
     */
    public void setTraceLevel(String component, int level);

    /**
     * Sets trace level for object
     *
     * @param level
     */
    public void setTraceLevel(int level);

    /**
     * Returns all components for object
     *
     * @return
     */
    public Hashtable getAllComponents();

    /**
     * Returns level for component for object
     *
     * @param component
     * @return
     */
    public int getLevelForComponent(String component);
}
