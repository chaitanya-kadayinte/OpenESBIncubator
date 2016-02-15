package com.fiorano.openesb.events;

import com.fiorano.openesb.utils.exception.FioranoException;

import java.sql.ResultSet;

public abstract class SystemEvent extends Event{
    private boolean eventPersistent = true;

    /**
     *  This method is an abstract method and will get the type of the event.
     *
     *@return    The type of the system event.
     */
    public abstract EventType getEventType();

    /**
     *  This method sets the various parameters of <code>TifosiSystemEvent</code>
     *  from the java.sql.ResultSet object.
     *
     *@param  resultSet            The java.sql.ResultSet object from which the
     *      values are to be read.
     *@exception  FioranoException  If any error occurs while reading values from
     *      java.sql.ResultSet object.
     */
    public void setFieldValues(ResultSet resultSet)
            throws FioranoException
    {
        super.setFieldValues(resultSet);
    }

    public boolean getIsPersistent(){
        return eventPersistent;
    }

    public void setIsPersistent(boolean isPersistent){
        eventPersistent = isPersistent;
    }
}
