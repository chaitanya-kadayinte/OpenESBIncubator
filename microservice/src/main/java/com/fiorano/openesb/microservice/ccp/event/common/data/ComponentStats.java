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

public class ComponentStats extends Data {
    @Override
    /**
     * Returns the data type for the data represented by this object.
     *
     * @return DataType - Data type value for this data object i.e. i.e. {@link com.fiorano.openesb.microservice.ccp.event.common.data.Data.DataType#COMPONENT_STATS}
     * @see com.fiorano.openesb.microservice.ccp.event.common.data.Data.DataType
     */
    public DataType getDataType() {
        return DataType.COMPONENT_STATS;
    }

    private String value;

    /**
     * This method initializes an object of this class
     * @param value Component Statistics of the component 
     */
    public ComponentStats(String value) {
        this.value = value;
    }


    /**
     * Default Constructor
     */
    @SuppressWarnings("UnusedDeclaration")
    public ComponentStats() {
    }

    /**
     * Returns the stats of the component  as a String
     * @return String - stats of component 
     */
    public String toString() {
        return this.value;
    }

    /**
     * Returns the stats of the component  if the component is not launched in memory.
     *
     * @return String - stats of component 
     * @see #setValue(String)
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the stats of the component 
     *
     * @param value stats of the component 
     * @see #getValue()
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Reads the values from the bytesMessage and sets the properties of this data object.
     *
     * @param bytesMessage Bytes message
     * @throws JMSException If an exception occurs while reading values from the message
     * @see #toMessage(javax.jms.BytesMessage)
     */
    public void fromMessage(BytesMessage bytesMessage) throws JMSException {
        value = bytesMessage.readUTF();
    }

    /**
     * Writes this data object to the bytesMessage.
     *
     * @param bytesMessage Bytes message
     * @throws JMSException If an exception occurs while writing value to the message
     * @see #fromMessage(javax.jms.BytesMessage)
     */
    public void toMessage(BytesMessage bytesMessage) throws JMSException {
        bytesMessage.writeUTF(value);
    }

    /**
     * Reads the values from the data input stream and sets the properties of this data object.
     *
     * @param in Input data stream
     * @throws IOException If an exception occurs while reading values from the stream
     * @see #toStream(java.io.DataOutput)
     */
    public void fromStream(DataInput in) throws IOException {
        value = in.readUTF();
    }

    /**
     * Writes this data object to the data stream.
     *
     * @param out Output data stream
     * @throws IOException If an exception occurs while writing value to the stream
     * @see #fromStream(java.io.DataInput)
     */
    public void toStream(DataOutput out) throws IOException {
        out.writeUTF(value);
    }

}
