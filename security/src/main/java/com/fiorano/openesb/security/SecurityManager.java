package com.fiorano.openesb.security;

import org.apache.karaf.jaas.config.JaasRealm;
import org.apache.karaf.jaas.modules.BackingEngine;
import org.apache.karaf.jaas.modules.BackingEngineFactory;
import org.apache.karaf.jaas.modules.properties.PropertiesBackingEngineFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.security.auth.callback.*;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SecurityManager {

    private BackingEngine backingEngine;
    private Map<String, ConnectionHandle> connectionHandleMap = new HashMap();
    public SecurityManager(BundleContext context){
        ServiceReference<JaasRealm> realmReference = context.getServiceReference(JaasRealm.class);
        ServiceReference<BackingEngineFactory> references3 = context.getServiceReference(BackingEngineFactory.class);
        JaasRealm jaasRealm = context.getService(realmReference);
        PropertiesBackingEngineFactory propertiesBackingEngineFactory = (PropertiesBackingEngineFactory) context.getService(references3);
        this.backingEngine = propertiesBackingEngineFactory.build(jaasRealm.getEntries()[0].getOptions());
    }

    public String login(final String userName, final String password) throws LoginException {
        List usersList = backingEngine.listUsers();
        LoginContext loginContext = new LoginContext("karaf", new CallbackHandler() {
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                NameCallback nameCallback = (NameCallback) callbacks[0];
                PasswordCallback passwordCallback = (PasswordCallback) callbacks[1];
                nameCallback.setName(userName);
                passwordCallback.setPassword(password.toCharArray());
            }
        });
        loginContext.login();
        String handleId = String.valueOf(new Random().nextInt());
        return handleId;
    }

    public void addConnectionHandle(String handleID, ConnectionHandle connectionHandle){
        connectionHandleMap.put(handleID, connectionHandle);
    }

    public ConnectionHandle removeConnectionHandle(String handleID){
       return connectionHandleMap.remove(handleID);
    }


    public ConnectionHandle getConnectionHandle(String handleId) {
        if(connectionHandleMap.containsKey(handleId)){
            return connectionHandleMap.get(handleId);
        }
        return null;
    }

    public String getUserName(String handleID){
        if(connectionHandleMap.containsKey(handleID)){
           return connectionHandleMap.get(handleID).getUserName();
        }
        return null;
    }
}
