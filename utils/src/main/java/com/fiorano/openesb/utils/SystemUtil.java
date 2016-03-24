package com.fiorano.openesb.utils;

/*import com.sun.management.OperatingSystemMXBean;*/

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;

/**
 * Created by Janardhan on 3/23/2016.
 */
public class SystemUtil {

    public static Double getProcessCPUUtilization() {
        try {
           /* OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            return operatingSystemMXBean.getProcessCpuLoad();*/
            return -1d;
        } catch (Exception e){
            return -1d;
        }
    }

    public static String formatCPUUtilization(double d) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        if(d < 0) {
            return "NA";
        } else {
            return decimalFormat.format(d*100).replace(",",".");
        }
    }
}
