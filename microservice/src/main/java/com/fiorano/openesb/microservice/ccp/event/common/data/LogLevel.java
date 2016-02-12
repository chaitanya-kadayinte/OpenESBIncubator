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

package com.fiorano.openesb.microservice.ccp.event.common.data;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * This class represents Log Level data sent by components to Peer Server.
 * A data event is generally sent by component in response to {@link com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent} event from Peer Server.<br><br>
 *
 * @see com.fiorano.openesb.microservice.ccp.event.common.data.Data.DataType#LOG_LEVEL
 * @author FSTPL
 * @version 10
 */
public class LogLevel extends Data {
    private HashMap<String, Level> loggerLevels;

    /**
     * Returns the map of all the loggers with their log levels.
     * @return HashMap - Log Level for all the loggers
     * @see #setLoggerLevels(java.util.HashMap)
     */
    public HashMap<String, Level> getLoggerLevels() {
        return loggerLevels;
    }

    /**
     * Sets log levels for all loggers through a HashMap<Key, Value> where Key represents logger name and Value represents logger's log level.
     * @param loggerLevels Log Level for all the loggers
     * @see #getLoggerLevels()
     */
    public void setLoggerLevels(HashMap<String, Level> loggerLevels) {
        this.loggerLevels = loggerLevels;
    }

    /**
     * Returns the data type for the data represented by this object.
     * @return DataType - Data type value for this data object i.e. {@link com.fiorano.openesb.microservice.ccp.event.common.data.Data.DataType#LOG_LEVEL}
     * @see com.fiorano.openesb.microservice.ccp.event.common.data.Data.DataType
     */
    public DataType getDataType() {
        return DataType.LOG_LEVEL;
    }

    /**
     * Reads the values from the bytesMessage and sets the properties of this data object.
     * @param bytesMessage Bytes message
     * @throws JMSException If an exception occurs while reading values from the message
     * @see #toMessage(javax.jms.BytesMessage)
     */
    public void fromMessage(BytesMessage bytesMessage) throws JMSException {
        int numLoggers = bytesMessage.readInt();
        if(numLoggers < 1)
            return;

        loggerLevels = new HashMap<>();
        for(int i = 0; i < numLoggers; i++){
            String loggerName = bytesMessage.readUTF();
            int intLogLevel = bytesMessage.readInt();
            Level logLevel = getLevel(intLogLevel);
            loggerLevels.put(loggerName, logLevel);
        }
    }

    private Level getLevel(int logLevel) {
        Level level = null;

        if(logLevel == Level.OFF.intValue())
            level = Level.OFF;
        else if(logLevel == Level.SEVERE.intValue())
            level = Level.SEVERE;
        else if(logLevel == Level.WARNING.intValue())
            level = Level.WARNING;
        else if(logLevel == Level.INFO.intValue())
            level = Level.INFO;
        else if(logLevel == Level.CONFIG.intValue())
            level = Level.CONFIG;
        else if(logLevel == Level.FINE.intValue())
            level = Level.FINE;
        else if(logLevel == Level.FINER.intValue())
            level = Level.FINER;
        else if(logLevel == Level.FINEST.intValue())
            level = Level.FINEST;
        else if(logLevel == Level.ALL.intValue())
            level = Level.ALL;

        return level;
    }

    /**
     * Writes this data object to the bytesMessage.
     * @param bytesMessage Bytes message
     * @throws JMSException If an exception occurs while writin value to the message
     * @see #fromMessage(javax.jms.BytesMessage)
     */
    public void toMessage(BytesMessage bytesMessage) throws JMSException {
        if(loggerLevels == null){
            bytesMessage.writeInt(-1);
            return;
        }

        bytesMessage.writeInt(loggerLevels.size());
        for(String loggerName : loggerLevels.keySet()){
            bytesMessage.writeUTF(loggerName);
            Level level = loggerLevels.get(loggerName);
            bytesMessage.writeInt(level != null ? level.intValue() : -1);
        }
    }

    /**
     * Reads the values from the data input stream and sets the properties of this data object.
     * @param in Input data stream
     * @throws IOException If an exception occurs while reading values from the stream
     * @see #toStream(java.io.DataOutput)
     */
    public void fromStream(DataInput in) throws IOException {
        int numLoggers = in.readInt();
        if(numLoggers < 1)
            return;

        loggerLevels = new HashMap<>();
        for(int i = 0; i < numLoggers; i++){
            String loggerName = in.readUTF();
            int intLogLevel = in.readInt();
            Level logLevel = getLevel(intLogLevel);
            loggerLevels.put(loggerName, logLevel);
        }
    }

    /**
     * Writes this data object to the data stream.
     * @param out Output data stream
     * @throws IOException If an exception occurs while writing value to the stream
     * @see #fromStream(java.io.DataInput)
     */
    public void toStream(DataOutput out) throws IOException {
        if(loggerLevels == null){
            out.writeInt(-1);
            return;
        }

        out.writeInt(loggerLevels.size());
        for(String loggerName : loggerLevels.keySet()){
            out.writeUTF(loggerName);
            Level level = loggerLevels.get(loggerName);
            out.writeInt(level != null ? level.intValue() : -1);
        }
    }
}
