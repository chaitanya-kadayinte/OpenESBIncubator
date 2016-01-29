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
package com.fiorano.openesb.rmiconnector.api.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.HashMap;
import java.io.Serializable;

/**
 * The Method call interceptor which is exported to the client connected via rmi.
 * @author Vishnu (chander)
 */

public class RemoteClientInterceptor implements InvocationHandler, Serializable {

    private static final long serialVersionUID = 420549831750656506L;
    private IRemoteServerProxy serverProxy;


    public RemoteClientInterceptor(IRemoteServerProxy serverProxy) {
        this.serverProxy = serverProxy;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        HashMap additionalInfo=captureData(method);
        return serverProxy.invoke(method.getName(), args, additionalInfo);
    }

    private HashMap captureData(Method method)
    {
        HashMap additionalInfo = new HashMap();
        //get clients langauage --> for the feature, a server in england can serve clients in japan... i.e. errors / messages can be in japanese.
        additionalInfo.put(Locale.class.toString(), Locale.getDefault());

        //get his ipaddresses if u want to :) --> not including this as of now.
        additionalInfo.put("method_parameter_types", getArgumentClassesKey(method));
        return additionalInfo;
    }

    private String getArgumentClassesKey(Method m) {
        Class argClasses[] = m.getParameterTypes();
        if (argClasses.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Class c : argClasses) {
            builder.append(c.getName());
            builder.append(':');
        }
        String args = builder.toString();
        args = args.substring(0, args.lastIndexOf(":"));
        return args;
    }
}
