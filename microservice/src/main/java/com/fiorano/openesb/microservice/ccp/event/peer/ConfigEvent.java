package com.fiorano.openesb.microservice.ccp.event.peer;

import com.fiorano.openesb.microservice.ccp.CCPEventType;
import com.fiorano.openesb.microservice.ccp.event.ControlEvent;
import com.fiorano.openesb.microservice.ccp.event.common.DataRequestEvent;
import com.fiorano.openesb.microservice.ccp.event.common.data.Data;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.swing.event.DocumentEvent;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Map;

public class ConfigEvent extends ControlEvent {
    private Map<String ,Object> data;

    @Override
    public CCPEventType getEventType() {
        return CCPEventType.CONFIG;
    }

    public void fromMessage(BytesMessage bytesMessage) throws JMSException {
        super.fromMessage(bytesMessage);
        int size = bytesMessage.readInt();
        for(int i=0;i< size;i++){
            String  identifier = String.valueOf(bytesMessage.readUTF());
            Data data = Data.getDataObject(Data.DataType.valueOf(bytesMessage.readUTF()));
            data.fromMessage(bytesMessage);
            this.data.put(identifier, data);
        }

    }

        public void toMessage(BytesMessage bytesMessage) throws JMSException {
            super.toMessage(bytesMessage);
            bytesMessage.writeInt(data.size());
            for(Map.Entry<String, Object> entry :data.entrySet()) {
                bytesMessage.writeUTF(entry.getKey().toString());
                bytesMessage.writeUTF(entry.getValue().toString());
            }
    }


    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Data Event Properties");
        builder.append("-------------------------------------");
        builder.append(super.toString());
        builder.append(" Data Sent : ").append(data != null ? data.toString() : "");
        return builder.toString();
    }
}
