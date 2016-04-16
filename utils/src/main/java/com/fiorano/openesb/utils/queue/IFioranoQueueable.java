package com.fiorano.openesb.utils.queue;

public interface IFioranoQueueable
{
    /**
       @roseuid 35EDB98C0362
     */
    public IFioranoQueueable getNext();

    /**
       @roseuid 35EDB98C038A
     */
    public void setNext(IFioranoQueueable next);

    /**
       @return the data stored in this node
     */
    public Object getData();
}
