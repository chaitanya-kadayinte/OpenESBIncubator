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
