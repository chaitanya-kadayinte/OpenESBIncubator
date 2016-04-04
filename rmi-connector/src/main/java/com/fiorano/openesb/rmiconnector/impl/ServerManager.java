package com.fiorano.openesb.rmiconnector.impl;

import com.fiorano.openesb.application.SystemInfo;
import com.fiorano.openesb.application.TESPerformanceStats;
import com.fiorano.openesb.transport.impl.jms.TransportConfig;
import com.fiorano.openesb.utils.exception.FioranoException;

import java.util.HashMap;

/**
 * Created by Janardhan on 3/23/2016.
 */
public class ServerManager {

    private static ServerManager serverManager;
    private RmiManager rmiManager;

    private ServerManager(RmiManager rmiManager){
        this.rmiManager = rmiManager;
    }

    public void restart() throws FioranoException {
        
    }

    public void shutdown() throws FioranoException {
        
    }

    public HashMap getServerDetails() throws FioranoException {
        HashMap fesDetails = new HashMap();
        fesDetails.put("JMS URL", TransportConfig.getInstance().getValue("providerURL"));
        fesDetails.put("Server IP", rmiManager.getIPAliases().get(rmiManager.getIPAliases().size()));
        fesDetails.put("Rmi Registry Port", rmiManager.getRmiRegistryPort());
        TESPerformanceStats stats = ServerInfo.getTESPerformanceStats();
        fesDetails.put("ProcessCount", String.valueOf(stats.getTotalProcessCount()));
        fesDetails.put("ThreadCount", String.valueOf(stats.getTotalThreadCount()));
        fesDetails.put("MemoryUsage", stats.getMemoryUsage());
        fesDetails.put("CPU Usage", stats.getCpuUtilization());
        SystemInfo sysInfo = ServerInfo.getTESSystemInfo();
        fesDetails.put("OS", sysInfo.getOSName() + " " + sysInfo.getOSVersion());
        fesDetails.put("JRE", sysInfo.getJREImplVendor() + " " + sysInfo.getJREImplVersion());
        fesDetails.put("HAEnabled", Boolean.parseBoolean(System.getProperty("HA_ENABLED")));
        fesDetails.put("BUILD_NO", getClass().getPackage().getImplementationVersion());
        fesDetails.put("FIORANO_PRODUCT", getClass().getPackage().getImplementationTitle());
        fesDetails.put("JVM", System.getProperty("sun.arch.data.model").contains("64") ? "64-bit" : "32-bit");
        return fesDetails;
    }

    public int getRemoteRMIPort() throws FioranoException {
        return 0;
    }

    public String getRemoteIPAddress() throws FioranoException {
        return null;
    }

    public static ServerManager GETINSTANCE(RmiManager rmiManager) {
        if(serverManager==null){
            serverManager = new ServerManager(rmiManager);
        }
        return serverManager;
    }
}
