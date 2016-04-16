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
