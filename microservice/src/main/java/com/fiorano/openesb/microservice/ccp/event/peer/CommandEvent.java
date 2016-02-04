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

package com.fiorano.openesb.microservice.ccp.event.peer;


import com.fiorano.openesb.microservice.ccp.event.CCPEventType;
import com.fiorano.openesb.microservice.ccp.event.ControlEvent;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a command event sent by Peer Server to components e.g. 'shutdown command' or
 * 'Set Log Level' command etc. Component Process should acknowledge these commands by sending appropriate status event(s)
 * indicating the result of the operation performed for this command.<br><br>
 *
 * @see com.fiorano.openesb.microservice.ccp.event.CCPEventType#COMMAND
 *
 * @author FSTPL
 * @version 10
 */
public class CommandEvent extends ControlEvent {
    private Command command;
    private Map<String, String> arguments;

    /**
     * Initializes a new Command Event
     */
    public CommandEvent() {
        arguments = new HashMap<String, String>();
    }

    /**
     * Returns the {@link Command} that should be executed by the recipient of this event.
     * @return Command - An Object of Command containing the command to be executed
     * @see #setCommand(com.fiorano.openesb.microservice.ccp.event.peer.CommandEvent.Command)
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Sets the {@link Command} value to the specified value.
     * @param command  An Object of Command
     * @see #getCommand()
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * Returns the arguments that are passed along with this command
     * @return HashMap - List of arguments passes with the command
     * @see #setArguments(java.util.Map)
     */
    public Map<String, String> getArguments() {
        return arguments;
    }

    /**
     * Sets the arguments required to execute this command (if necessary/applicable).
     * @param arguments - List of arguments
     * @see #getArguments()
     */
    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }

    /**
     * Returns the event type for the event represented by this object.
     * @return {@link com.fiorano.openesb.microservice.ccp.event.CCPEventType} - An enumeration constant representing the command event i.e. {@link CCPEventType#COMMAND}
     */
    public CCPEventType getEventType() {
        return CCPEventType.COMMAND;
    }

    /**
     * An enumeration listing of the available set of commands that Peer Server may send to component process.
     * @author FSTPL
     * @version 10
     */
    public enum Command{
        /**
         * This command is sent by Peer Server to component process to suggest that component process should initiate shutdown sequence.
         */
        INITIATE_SHUTDOWN,

        /**
         * This command is sent by Peer Server to component process to request for the current state of the component process i.e. running or error state etc.
         */
        REPORT_STATE,

        /**
         * This command is sent by Peer Server to component process whenever user changes log level of the component process while component is running.
         * The value of new log level will be sent along with this request.
         */
        SET_LOGLEVEL,

        /**
         * This command is sent by Peer Server to component process to flush messages
         */
        FLUSH_MESSAGES
    }

    /**
     * Reads the values from the bytesMessage and sets the properties of this event object.
     * @param bytesMessage Bytes message
     * @throws JMSException If an exception occurs while reading values from the message
     * @see #toMessage(javax.jms.BytesMessage)
     */
    @Override
    public void fromMessage(BytesMessage bytesMessage) throws JMSException {
        super.fromMessage(bytesMessage);
        command = Command.valueOf(bytesMessage.readUTF());
        int size = bytesMessage.readInt();
        for(int i=0;i< size;i++)
            arguments.put(bytesMessage.readUTF(), bytesMessage.readUTF());
    }

    /**
     * Writes this event object to the bytesMessage.
     * @param bytesMessage Bytes message
     * @throws JMSException If an exception occurs while writing value to the message
     * @see #fromMessage(javax.jms.BytesMessage)
     */
    @Override
    public void toMessage(BytesMessage bytesMessage) throws JMSException {
        super.toMessage(bytesMessage);
        bytesMessage.writeUTF(command.toString());
        bytesMessage.writeInt(arguments.size());
        for(Map.Entry<String, String> entry:arguments.entrySet()) {
            bytesMessage.writeUTF(entry.getKey());
            bytesMessage.writeUTF(entry.getValue());
        }
    }

    /**
     * Returns a string representation of the object.
     * @return String - String representation of the object.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Command Event Properties");
        builder.append("-------------------------------------");
        builder.append(super.toString());
        builder.append(" Command : ").append(command.name());
        builder.append(" Arguments : ").append(arguments.toString());
        return builder.toString();
    }
}
