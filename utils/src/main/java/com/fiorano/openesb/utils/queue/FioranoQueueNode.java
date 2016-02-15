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

/**
*   class </code> FioranoQueueNode </code> represents a single
*   node in a Queue which mantains a link to the next node in a Queue.
*/
public class FioranoQueueNode implements IFioranoQueueable
{
    // next pointer for this node.
    private IFioranoQueueable m_next = null;

    //  data stored in this node.
    private Object m_data;

    /**
    *   Creates a new FioranoQueueNode containing a reference to
    *   a given node.
    */
    public FioranoQueueNode (Object data)
    {
        m_data = data;
    }

    /**
       @return the next node, null if this is the last node
     */
    public IFioranoQueueable getNext()
    {
        return m_next;
    }

    /**
       @roseuid set the next pointer to given node.
     */
    public void setNext(IFioranoQueueable next)
    {
        m_next = next;
    }

    /**
       @return the data stored in this node
     */
    public Object getData ()
    {
        return m_data;
    }
}
