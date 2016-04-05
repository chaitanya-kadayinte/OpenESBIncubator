package com.fiorano.openesb.microservice.ccp.event.common.data;

import com.fiorano.openesb.application.application.InputPortInstance;
import com.fiorano.openesb.application.application.OutputPortInstance;
import com.fiorano.openesb.application.application.PortInstance;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Janardhan on 4/4/2016.
 */
public class PortConfiguration extends Data {
    private Map<String, List<PortInstance>> portInstances;
    public PortConfiguration() {
    }

    @Override
    public DataType getDataType() {
        return DataType.PORT_CONFIGURATION;
    }

    @Override
    public void fromMessage(BytesMessage bytesMessage) throws JMSException {
        int numConfigs = bytesMessage.readInt();
        if(numConfigs < 1)
            return;

        portInstances = new HashMap<>();
        for(int i = 0; i < numConfigs; i++){
            String key = bytesMessage.readUTF();
            int j=bytesMessage.readInt();
            ArrayList list = new ArrayList();
            if(j>0){
                for(int k=0;k<=j;k++){
                    if(key.equals("IN_PORTS")){
                        InputPortInstance inputPortInstance = new InputPortInstance();
                        inputPortInstance.fromMessage(bytesMessage);
                        list.add(inputPortInstance);
                    }else if (key.equals("OUT_PORTS")){
                        OutputPortInstance outputPortInstance = new OutputPortInstance();
                        outputPortInstance.fromMessage(bytesMessage);
                        list.add(outputPortInstance);
                    }
                }
            }
            portInstances.put(key, list);
        }
    }

    @Override
    public void toMessage(BytesMessage bytesMessage) throws JMSException {
        if(portInstances == null){
            bytesMessage.writeInt(-1);
            return;
        }
        bytesMessage.writeInt(portInstances.size());
        for(String configKey : portInstances.keySet()){
            bytesMessage.writeUTF(configKey);
            List<PortInstance> list = portInstances.get(configKey);
            bytesMessage.writeInt(list.size());
            for(PortInstance portInstance : list){
                portInstance.toMessage(bytesMessage);
            }
        }
    }

    @Override
    public void fromStream(DataInput in) throws IOException {

    }

    @Override
    public void toStream(DataOutput out) throws IOException {

    }
    public Map<String, List<PortInstance>> getPortInstances() {
        return portInstances;
    }

    public void setPortInstances(Map<String, List<PortInstance>> portInstances) {
        this.portInstances = portInstances;
    }
}
