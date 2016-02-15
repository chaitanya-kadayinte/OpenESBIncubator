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

import java.util.NoSuchElementException;

/**
*   class </code> FioranoQueueEnumerator </code> implements
*   Enumerator used for enumerating the contents of a Queue.
*/
public class FioranoQueueEnumerator implements java.util.Enumeration
{
    //  current position of pointer in the Queue.
    private IFioranoQueueable m_current;

    // Total size of Queue
    private int m_iSize;

    /**
     * Create a new enumerator, starting from the given node.
     */
    public FioranoQueueEnumerator (IFioranoQueueable head, int size)
    {
        m_current = head;
        m_iSize = size;
    }

    /**
     *  @return true is more data ia available in this Queue.
     */
    public boolean hasMoreElements()
    {
        return (m_current != null);
    }

    /**
     *  @return next element from this Queue.
     *  @throws NoSuchElementException if there is the enumerator
     *          has reached end of the Queue.
     */
    public Object nextElement()
    {
        if (m_current == null)
            throw new NoSuchElementException("No More Elements.");

        IFioranoQueueable toReturn = m_current;
        m_current = m_current.getNext ();
        return toReturn.getData ();
    }

    /**
     *  @return size of Queue
     */
    public int getLength ()
    {
        return m_iSize;
    }
}
