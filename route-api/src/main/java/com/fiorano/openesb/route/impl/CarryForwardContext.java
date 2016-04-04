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

import com.fiorano.openesb.utils.JmsMessageUtil;
import com.fiorano.openesb.utils.SourceContext;

import java.io.Serializable;
import java.util.*;

/**
 *  Represents a collection of <code>SourceContext</code> objects. A <code>SourceContext</code>
 *  object contains information about the service instance sending the data
 *  packet. Routing is done using this information and receiver of a data packet
 *  identifies the data sender through its <code>SourceContext</code>. The
 *  <code>CarryForwardContext</code> is part of the <code>javax.jms.Messgae</code> object
 *  which is exchanged across Fiorano channels and is set internally by the FPS
 *  over which the data is routed.
 *  The <code>CarryForwardContext</code> can be added to a message as a property
 *  using method {@link JmsMessageUtil#setCarryForwardContext(javax.jms.Message, Object)}.
 *  <code>SourceContext</code> objects keep getting added to this object while the document flows in a workflow.
 */
public class CarryForwardContext implements Serializable {
    private static final long serialVersionUID = -4012981779432749752L;

    Vector vecOfContextsCarriedFwd;

    Hashtable hashCarryFwdProps;

    public ArrayList<SourceContext> getVecOfContextsCarriedFwd() {
        return new ArrayList<SourceContext>(vecOfContextsCarriedFwd);
    }

    public void setVecOfContextsCarriedFwd(ArrayList<SourceContext> vecOfContextsCarriedFwd) {
        this.vecOfContextsCarriedFwd =  new Vector(vecOfContextsCarriedFwd);
    }

    public Hashtable getHashCarryFwdProps() {
        return hashCarryFwdProps;
    }

    public void setHashCarryFwdProps(Hashtable hashCarryFwdProps) {
        this.hashCarryFwdProps = hashCarryFwdProps;
    }

    private String appContext;

    public CarryForwardContext() {
        vecOfContextsCarriedFwd = new Vector();
        hashCarryFwdProps = new Hashtable();
    }


    public Enumeration getContexts() {
        return vecOfContextsCarriedFwd.elements();
    }


    public String getAppContext() {
        return appContext;
    }

    public Hashtable getCarryFwdProps() {
        return hashCarryFwdProps;
    }

    public String getCarryFwdProperty(String name) {
        return (String) hashCarryFwdProps.get(name);
    }

    public void setAppContext(String appContext) {
        this.appContext = appContext;
    }

    public void setCarryFwdProps(Hashtable properties) {
        hashCarryFwdProps = properties;
    }

    public void setCarryFwdProperty(String name, String value) {
        hashCarryFwdProps.put(name, value);
    }

    public void addContext(SourceContext context) {
        if (!vecOfContextsCarriedFwd.contains(context))
            vecOfContextsCarriedFwd.add(context);
    }

}
