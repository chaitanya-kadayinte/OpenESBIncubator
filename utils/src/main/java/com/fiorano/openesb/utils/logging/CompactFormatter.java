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

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CompactFormatter extends Formatter
{
    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.
    private String lineSeparator = System.getProperty("line.separator");

    public String format(LogRecord record) {
        String logMessage = formatMessage(record);

        if (!logMessage.endsWith(lineSeparator)) {
            logMessage += lineSeparator;
        }

        if (record.getThrown() != null) {
            logMessage += ExceptionUtil.getStackTrace(record.getThrown()) + lineSeparator;
        }
        return logMessage;
    }
}
