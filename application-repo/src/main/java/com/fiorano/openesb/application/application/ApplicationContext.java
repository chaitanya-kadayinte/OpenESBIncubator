package com.fiorano.openesb.application.application;

import com.fiorano.openesb.application.DmiObjectTypes;
import com.fiorano.openesb.application.MapThreadLocale;
import com.fiorano.openesb.utils.exception.FioranoException;
import com.fiorano.openesb.utils.FioranoStaxParser;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ApplicationContext extends SchemaInstance{
    /**
     * element application-context in event process xml
     */
    public static final String ELEM_APPLICATION_CONTEXT = "application-context";

    /**
     * Returns ID of this object. This is used internally to identify different types of DMI objects.
     * @return the id of this object.
     */
    public int getObjectID(){
        return DmiObjectTypes.NEW_APPLICATION_CONTEXT;
    }

    /*-------------------------------------------------[ XMLIzation ]---------------------------------------------------*/

    /*
     * <application-context>
     *      ...super-class...
     * </application-context>
     */

    protected void toJXMLString(XMLStreamWriter writer, boolean writeSchema) throws XMLStreamException, FioranoException{
        MapThreadLocale.getInstance().getMap().put(ServiceInstance.ELEM_SERVICE_INSTANCE, "APPLICATION_CONTEXT");
        toJXMLString(writer, ELEM_APPLICATION_CONTEXT, writeSchema);
    }


    protected void toJXMLString(XMLStreamWriter writer) throws XMLStreamException, FioranoException {
        toJXMLString(writer, true);
    }

    protected void populate(FioranoStaxParser cursor) throws XMLStreamException, FioranoException{
        MapThreadLocale.getInstance().getMap().put(ServiceInstance.ELEM_SERVICE_INSTANCE, "APPLICATION_CONTEXT");
        populate(cursor, ELEM_APPLICATION_CONTEXT);
    }
}
