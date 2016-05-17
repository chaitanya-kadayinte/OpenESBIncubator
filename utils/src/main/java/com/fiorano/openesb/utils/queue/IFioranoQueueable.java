/*
 * Copyright (c) Fiorano Software Pte. Ltd. and affiliates. All rights reserved. http://www.fiorano.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
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
