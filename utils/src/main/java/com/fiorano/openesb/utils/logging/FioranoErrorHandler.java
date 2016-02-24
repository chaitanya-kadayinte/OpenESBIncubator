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

import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * FioranoErrorHandler an extension of BaseHandler hansles only error logs
 * and discards all other log messages.
 *
 * @author FSIPL
 * @created June 22, 2005
 * @version 1.0
 */
public class FioranoErrorHandler
     extends FioranoBaseHandler
{
    /**
     * @param handler
     * @param props
     */
    public FioranoErrorHandler(Handler handler, Properties props)
    {
        super(handler, props);
    }

    /**
     * Get the log level specifying which messages will be
     * logged by this <tt>Handler</tt>.  Message levels lower
     * than this level will be discarded.
     *
     * @return the level of messages being logged.
     */
    public Level getLevel()
    {
        if (m_handler.getLevel().intValue() < Level.WARNING.intValue())
            return Level.WARNING;

        return super.getLevel();
    }

    /**
     * Check if this <tt>Handler</tt> would actually log a given <tt>LogRecord</tt>.
     * <p>
     * This method checks if the <tt>LogRecord</tt> has an appropriate
     * <tt>Level</tt> and  whether it satisfies any <tt>Filter</tt>.  It also
     * may make other <tt>Handler</tt> specific checks that might prevent a
     * handler from logging the <tt>LogRecord</tt>.
     * <p>
     *
     * @param record a <tt>LogRecord</tt>
     * @return true if the <tt>LogRecord</tt> would be logged.
     */
    public boolean isLoggable(LogRecord record)
    {
        if (!isErrorRecord(record))
            return false;

        return super.isLoggable(record);
    }


    /**
     * Publish a <tt>LogRecord</tt>.
     * <p>
     * The logging request was made initially to a <tt>Logger</tt> object,
     * which initialized the <tt>LogRecord</tt> and forwarded it here.
     * <p>
     * The <tt>Handler</tt>  is responsible for formatting the message, when and
     * if necessary.  The formatting should include localization.
     *
     * @param record description of the log event
     */
    public void publish(LogRecord record)
    {
        if (!isErrorRecord(record))
            return;

        super.publish(record);
    }


    private boolean isErrorRecord(LogRecord logRecord)
    {
        if (logRecord == null)
            return false;

        Level level = logRecord.getLevel();

        if (level == null)
            return false;

        if ((level == Level.SEVERE) || (level == Level.WARNING))
            return true;

        return false;
    }
}
