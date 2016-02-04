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

package com.fiorano.openesb.microservice.ccp.event.common;


import com.fiorano.openesb.microservice.ccp.event.CCPEventType;
import com.fiorano.openesb.microservice.ccp.event.ControlEvent;
import com.fiorano.openesb.microservice.ccp.event.common.data.Data;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a data event sent by components to Peer Server.
 * A data event is generally sent by components in response to {@link com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent} event sent from the Peer Server.<br><br>
 *
 * @see com.fiorano.openesb.microservice.ccp.event.CCPEventType#DATA
 * @author FSTPL
 * @version 10
 */
public class DataEvent extends ControlEvent {
    private Map<DataRequestEvent.DataIdentifier, Data> data;

    /**
     * Initializes a new Data Event
     */
    public DataEvent() {
        data = new HashMap<DataRequestEvent.DataIdentifier, Data>();
    }

    /**
     * Returns the response data from component process.
     * @return HashMap - Response data from component process.
     * @see #setData(java.util.Map)
     */
    public Map<DataRequestEvent.DataIdentifier, Data> getData() {
        return data;
    }

    /**
     * Sets the response data to be sent to Peer Server.
     * @param data Map of {@link com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent.DataIdentifier} vs. {@link Data} objects.
     * @see #getData()
     */
    public void setData(Map<DataRequestEvent.DataIdentifier, Data> data) {
        this.data = data;
    }

    /**
     * Returns the event type for the event represented by this object.
     * @return {@link com.fiorano.openesb.microservice.ccp.event.CCPEventType} enumeration constant representing data event from component i.e. {@link CCPEventType#DATA}
     */
    public CCPEventType getEventType() {
        return CCPEventType.DATA;
    }

    /**
     * Reads the values from the bytesMessage and sets the properties of this event object.
     * @param bytesMessage bytes message
     * @throws JMSException If an exception occurs while reading values from the message
     * @see #toMessage(javax.jms.BytesMessage)
     */
    @Override
    public void fromMessage(BytesMessage bytesMessage) throws JMSException {
        super.fromMessage(bytesMessage);
        int size = bytesMessage.readInt();
        for(int i=0;i< size;i++){
            DataRequestEvent.DataIdentifier identifier = DataRequestEvent.DataIdentifier.valueOf(bytesMessage.readUTF());
            Data data = Data.getDataObject(Data.DataType.valueOf(bytesMessage.readUTF()));
            data.fromMessage(bytesMessage);
            this.data.put(identifier, data);
        }
    }

    /**
     * Writes this event object to the bytesMessage.
     * @param bytesMessage bytes message
     * @throws JMSException If an exception occurs while writin value to the message
     * @see #fromMessage(javax.jms.BytesMessage)
     */
    @Override
    public void toMessage(BytesMessage bytesMessage) throws JMSException {
        super.toMessage(bytesMessage);
        bytesMessage.writeInt(data.size());
        for(Map.Entry<DataRequestEvent.DataIdentifier, Data> entry:data.entrySet()) {
            bytesMessage.writeUTF(entry.getKey().toString());
            bytesMessage.writeUTF(entry.getValue().getDataType().toString());
            entry.getValue().toMessage(bytesMessage);
        }
    }

    /**
     * Returns a string representation of the object.
     * @return string representation of the object.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Data Event Properties");
        builder.append("-------------------------------------");
        builder.append(super.toString());
        builder.append(" Data Sent : ").append(data != null ? data.toString() : "");
        return builder.toString();
    }
}
