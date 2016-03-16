package com.fiorano.openesb.rmiclient.tests;

import com.fiorano.openesb.rmiconnector.impl.IDistributedRemoteObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by Janardhan on 3/7/2016.
 */
public class AbstractEventListener implements IDistributedRemoteObject {

    public AbstractEventListener() {
        loadMethods();
    }

    private Hashtable<String,Method> methods= new Hashtable<String,Method>();
    private void loadMethods(){
        Method [] meths =this.getClass().getMethods();
        for (Method m : meths){
            methods.put(m.getName(),m);
        }
    }

    public Object invoke(String methodName, Object[] args, HashMap additionalInfo) throws RemoteException {
        Object ret=null;
        try {
            Method method = methods.get(methodName);
            ret  =  method.invoke(this,args);
        } catch (IllegalAccessException iae) {
            //Logger.logException(ServerConnectionActivator.PLUGIN_ID, NLSHelper.bindWithQuotes(Messages_server_connection.AbstractEventListener_0, methodName), iae);
            iae.printStackTrace();
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
           // Logger.logException(ServerConnectionActivator.PLUGIN_ID, NLSHelper.bindWithQuotes(Messages_server_connection.AbstractEventListener_0, methodName), ite.getTargetException());
        }
        return ret;
    }

    public void unreferenced() {
    }
}

