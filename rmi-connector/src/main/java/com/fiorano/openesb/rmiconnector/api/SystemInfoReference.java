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
package com.fiorano.openesb.rmiconnector.api;

import java.io.Serializable;

/**
 *  This class represents system information such as name and version of the operating
 *  system, version of JRE specification and implementation, vendor of JRE
 *  implementation, version of JVM specification and implementation, vendor and
 *  name of JVM implementation.
 *
 *  <p>All the information pertaining to the system is returned in the form
 *  of <code>SystemInfoReference</code>.
 *  @author FSTPL
 *  @version 10
 */
public class SystemInfoReference  implements Serializable {


    private static final long serialVersionUID = 71885766004646969L;
    private String opSysName;
    private String opSysVersion;

    private String jreSpecVersion;
    private String jreImplVersion;
    private String jreImplVendor;

    private String jvmSpecVersion;
    private String jvmImplVersion;
    private String jvmImplVendor;
    private String jvmImplName;

    /**
     * This method returns the operating system name
     * @return String - Name of the operating system
     */
    public String getOpSysName() {
        return opSysName;
    }
    /**
     * This method returns operating system version
     * @return String - Version of the operating system
     */
    public String getOpSysVersion() {
        return opSysVersion;
    }
    /**
     * This method returns the JRE Specification version
     * @return String - JRE Specification version
     */
    public String getJreSpecVersion() {
        return jreSpecVersion;
    }
    /**
     * This method returns the JRE Implementation version
     * @return String - JRE Implementation version
     */
    public String getJreImplVersion() {
        return jreImplVersion;
    }
    /**
     * This method returns the JRE Implementation vendor
     * @return String - JRE Implementation vendor
     */
    public String getJreImplVendor() {
        return jreImplVendor;
    }
    /**
     * This method returns the JVM Specification version
     * @return String - JVM Specification version
     */
    public String getJvmSpecVersion() {
        return jvmSpecVersion;
    }
    /**
     * This method returns the JVM Implementation version
     * @return String - JVM Implementation version
     */
    public String getJvmImplVersion() {
        return jvmImplVersion;
    }
    /**
     * This method returns the JVM Implementation vendor
     * @return String - JVM Implementation vendor
     */
    public String getJvmImplVendor() {
        return jvmImplVendor;
    }
    /**
     * This method returns the JVM Implementation name
     * @return String - JVM Implementation name
     */
    public String getJvmImplName() {
        return jvmImplName;
    }

    /******************************************** setters *************************/
    /**
     * This method sets the operating system name
     * @param opSysName Operating system name
     */
    public void setOpSysName(String opSysName) {
        this.opSysName = opSysName;
    }

    /**
     * This method sets the operating system version
     * @param opSysVersion Operating system version
     */
    public void setOpSysVersion(String opSysVersion) {
        this.opSysVersion = opSysVersion;
    }

    /**
     * This method sets the JRE specification version
     * @param jreSpecVersion JRE specification version
     */
    public void setJreSpecVersion(String jreSpecVersion) {
        this.jreSpecVersion = jreSpecVersion;
    }

    /**
     * This method sets the JRE implementation version
     * @param jreImplVersion JRE implementation version
     */
    public void setJreImplVersion(String jreImplVersion) {
        this.jreImplVersion = jreImplVersion;
    }

    /**
     * This method sets the JRE implementation vendor
     * @param jreImplVendor JRE implementation vendor
     */
    public void setJreImplVendor(String jreImplVendor) {
        this.jreImplVendor = jreImplVendor;
    }

    /**
     * This method sets the JVM specification version
     * @param jvmSpecVersion JVM specification version
     */
    public void setJvmSpecVersion(String jvmSpecVersion) {
        this.jvmSpecVersion = jvmSpecVersion;
    }

    /**
     * This method sets the JVM implementation version
     * @param jvmImplVersion JVM implementation version
     */
    public void setJvmImplVersion(String jvmImplVersion) {
        this.jvmImplVersion = jvmImplVersion;
    }

    /**
     * This method sets the JVM implementation vendor
     * @param jvmImplVendor JVM implementation vendor
     */
    public void setJvmImplVendor(String jvmImplVendor) {
        this.jvmImplVendor = jvmImplVendor;
    }

    /**
     * This method sets the JVM implementation name
     * @param jvmImplName JVM implementation name
     */
    public void setJvmImplName(String jvmImplName) {
        this.jvmImplName = jvmImplName;
    }
}
