package com.fiorano.openesb.microservice.ccp.event.common.data;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Janardhan on 4/4/2016.
 */
public class ManageableProperties extends Data {
    private String configuration;

    public ManageableProperties() {
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
