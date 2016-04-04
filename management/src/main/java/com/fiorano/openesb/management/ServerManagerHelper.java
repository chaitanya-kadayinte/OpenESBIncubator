package com.fiorano.openesb.management;


import com.fiorano.openesb.utils.SystemUtil;
import com.fiorano.openesb.transport.impl.jms.TransportConfig;

import java.util.HashMap;

/**
 * Created by root on 3/24/16.
 */
public class ServerManagerHelper {

    public HashMap getServerDetails()  {
        HashMap fesDetails = new HashMap();
        fesDetails.put("JMS_URL", TransportConfig.getInstance().getValue("providerURL"));
        long freeMemory = (Runtime.getRuntime().freeMemory()) / 1024;
        long totalMemory = (Runtime.getRuntime().totalMemory()) / 1024;
        fesDetails.put("FREE_MEMORY",freeMemory);
        fesDetails.put("TOTALMEMORY",totalMemory);
        fesDetails.put("MEM_USAGE" , totalMemory- freeMemory);

        fesDetails.put("Process_Count",1);
        fesDetails.put("Thread_Count", Thread.currentThread().getThreadGroup().activeCount());
        fesDetails.put("CPU_Usage", SystemUtil.getProcessCPUUtilization());

        fesDetails.put("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
        fesDetails.put("JRE", System.getProperty("java.specification.version") + " " + System.getProperty("java.version"));
        fesDetails.put("HAEnabled", Boolean.parseBoolean(System.getProperty("HA_ENABLED")));
        fesDetails.put("BUILD_NO", getClass().getPackage().getImplementationVersion());
        fesDetails.put("FIORANO_PRODUCT", getClass().getPackage().getImplementationTitle());
        fesDetails.put("JVM", System.getProperty("sun.arch.data.model").contains("64") ? "64-bit" : "32-bit");
        return fesDetails;
    }

}
