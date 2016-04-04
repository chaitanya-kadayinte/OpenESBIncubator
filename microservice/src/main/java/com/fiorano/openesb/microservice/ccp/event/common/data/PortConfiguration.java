package com.fiorano.openesb.microservice.ccp.event.common.data;

import com.fiorano.openesb.application.DmiObject;
import com.fiorano.openesb.application.application.PortInstance;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Janardhan on 4/4/2016.
 */
public class PortConfiguration extends Data {
    private ArrayList<PortInstance> portInstances;
    public PortConfiguration() {
    }

    @Override
    public DataType getDataType() {
        return DataType.PORT_CONFIGURATION;
    }

    @Override
    public void fromMessage(BytesMessage bytesMessage) throws JMSException {

    }

    @Override
    public void toMessage(BytesMessage bytesMessage) throws JMSException {

    }

    public void fromMessage(ObjectMessage objectMessage) throws JMSException {
        portInstances = (ArrayList<PortInstance>) objectMessage.getObject();
    }

    public void toMessage(ObjectMessage objectMessage) throws JMSException {
        objectMessage.setObject(portInstances);
    }

    @Override
    public void fromStream(DataInput in) throws IOException {

    }

    @Override
    public void toStream(DataOutput out) throws IOException {

    }
    public ArrayList<PortInstance> getPortInstances() {
        return portInstances;
    }

    public void setPortInstances(ArrayList<PortInstance> portInstances) {
        this.portInstances = portInstances;
    }
}
