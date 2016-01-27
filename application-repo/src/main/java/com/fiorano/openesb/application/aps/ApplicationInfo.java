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

package com.fiorano.openesb.application.aps;

import com.fiorano.openesb.application.*;
import com.fiorano.openesb.utils.exception.FioranoException;

/**
 *  Description of the Class
 *
 * @author vineet
 * @created November 17, 2005
 * @version 1.0
 */
public class ApplicationInfo extends DmiObject
{
    private ApplicationStateDetails m_appStateDetails;

    private String  m_userName = null;
    private String  m_passwd = null;
    private String  m_appVersion = null;


    /**
     *  Gets the objectID attribute of the ApplicationInfo object
     *
     * @return The objectID value
     */
    public int getObjectID()
    {
        return DmiObjectTypes.APP_STATE_INFO;
    }


    /**
     *  Gets the appStateDetails attribute of the ApplicationInfo object
     *
     * @return The appStateDetails value
     */
    public ApplicationStateDetails getAppStateDetails()
    {
        return m_appStateDetails;
    }


    /**
     *  Gets the userName attribute of the ApplicationInfo object
     *
     * @return The userName value
     */
    public String getUserName()
    {
        return m_userName;
    }


    /**
     *  Gets the password attribute of the ApplicationInfo object
     *
     * @return The password value
     */
    public String getPassword()
    {
        return m_passwd;
    }


    /**
     *  Gets the appVersion attribute of the ApplicationInfo object
     *
     * @return The appVersion value
     */
    public String getAppVersion()
    {
        return m_appVersion;
    }


    /**
     *  Sets the appStateDetails attribute of the ApplicationInfo object
     *
     * @param appStateDetails The new appStateDetails value
     */
    public void setAppStateDetails(ApplicationStateDetails appStateDetails)
    {
        m_appStateDetails = appStateDetails;
    }


    /**
     *  Sets the userName attribute of the ApplicationInfo object
     *
     * @param userName The new userName value
     */
    public void setUserName(String userName)
    {
        m_userName = userName;
    }


    /**
     *  Sets the password attribute of the ApplicationInfo object
     *
     * @param passwd The new password value
     */
    public void setPassword(String passwd)
    {
        m_passwd = passwd;
    }


    /**
     *  Sets the appVersion attribute of the ApplicationInfo object
     *
     * @param appVersion The new appVersion value
     */
    public void setAppVersion(String appVersion)
    {
        m_appVersion = appVersion;
    }


    /**
     *  Description of the Method
     */
    public void reset()
    {
    }


    /**
     *  Description of the Method
     *
     * @exception FioranoException Description of the Exception
     */
    public void validate()
        throws FioranoException
    {
    }

}
