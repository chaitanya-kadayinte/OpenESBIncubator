/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2014, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 * <p>
 * Created by chaitanya on 05-02-2016.
 */

/**
 * Created by chaitanya on 05-02-2016.
 */
package com.fiorano.openesb.microservice.ccp.event.common.data;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MicroserviceConfiguration extends Data {
    private String configuration;

    public MicroserviceConfiguration() {
    }

    @Override
    public DataType getDataType() {
        return DataType.COMPONENT_CONFIGURATION;
    }

    @Override
    public void fromMessage(BytesMessage bytesMessage) throws JMSException {
       configuration =  bytesMessage.readUTF();
    }

    @Override
    public void toMessage(BytesMessage bytesMessage) throws JMSException {
        bytesMessage.writeUTF(configuration);
    }

    @Override
    public void fromStream(DataInput in) throws IOException {
        configuration = in.readUTF();
    }

    @Override
    public void toStream(DataOutput out) throws IOException {
        out.writeUTF(configuration);
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
