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





package com.fiorano.openesb.utils.logging;


import com.fiorano.openesb.utils.ExceptionUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Default Formatter
 *
 * @author FSIPL
 * @created June 20, 2005
 * @version 1.0
 */
public class DefaultFormatter extends Formatter
{
    private boolean m_bUseTimeStamp;
    private String dateFormat = "MM/dd/yyyy HH:mm:ss SSS";
    /**
     * @param props
     */
    public void configure(Properties props)
    {
        String cname = DefaultFormatter.class.getName();
        m_bUseTimeStamp = DefaultLogManager.getBooleanProperty(props, cname +".includetimestamp", true);
        dateFormat = DefaultLogManager.getStringProperty(props,cname+".dateformat","MM/dd/yyyy HH:mm:ss SSS");
    }

    /**
     * @param enable
     */
    public void useTimeStamp(boolean enable){
        m_bUseTimeStamp = enable;
    }

    public void useDateFormat(String format){
        dateFormat = format;
    }

    private String lineSeparator = System.getProperty("line.separator");
    /**
     * Format the given log record and return the formatted string.
     * <p>
     * The resulting formatted String will normally include a
     * localized and formated version of the LogRecord's message field.
     * The Formatter.formatMessage convenience method can (opti`    onally)
     * be used to localize and format the message field.
     *
     * @param logRecord
     * @return the formatted log record
     */
    public String format(LogRecord logRecord)
    {
        String strRecord="";

        if (m_bUseTimeStamp)
        {
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
            String date = formatter.format(new Date());

            strRecord = date + " : ";
        }
        strRecord = "!ENTRY \n" + strRecord + logRecord.getLevel() + " : ";
        strRecord = strRecord + logRecord.getMessage() + lineSeparator;

        if(logRecord.getThrown()!=null)
            strRecord = strRecord+ ExceptionUtil.getStackTrace(logRecord.getThrown());
        return strRecord;
    }
}
