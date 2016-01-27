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

package com.fiorano.openesb.utils;


import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;

public class TreeWalker implements org.w3c.dom.traversal.TreeWalker{
    private org.w3c.dom.traversal.TreeWalker walker = null;
    private Node startNode = null;

    public TreeWalker(Node node){
        Document doc = null;
        try
        {
            doc = (Document) node;
        }
        catch (Throwable thr)
        {
            // Not a document, no proble, we will get OwnerDocument.
            //
        }

        if (doc == null)
            doc = node.getOwnerDocument();
        walker = ((DocumentTraversal)doc).createTreeWalker(node, NodeFilter.SHOW_ALL, null, true);
        startNode = node;
    }

    /*-----------------------------[TreeWalker implementation]--------------------------------*/
    public Node getRoot(){
        return walker.getRoot();
    }

    public int getWhatToShow(){
        return walker.getWhatToShow();
    }

    public NodeFilter getFilter(){
        return walker.getFilter();
    }

    public boolean getExpandEntityReferences(){
        return walker.getExpandEntityReferences();
    }

    public Node getCurrentNode(){
        return walker.getCurrentNode();
    }

    public void setCurrentNode(Node node) throws DOMException{
        walker.setCurrentNode(node);
    }

    public Node parentNode(){
        return walker.parentNode();
    }

    public Node firstChild(){
        return walker.firstChild();
    }

    public Node lastChild(){
        return walker.lastChild();
    }

    public Node previousSibling(){
        return walker.previousSibling();
    }

    public Node nextSibling(){
        return walker.nextSibling();
    }

    public Node previousNode(){
        return walker.previousNode();
    }

    public Node nextNode(){
        return walker.nextNode();
    }


    /*---------------------------------[Utility Methods]----------------------------------------*/

    /**
     * Convenience method to walk only through elements with the specified
     * tag name.  This just calls getNext() and filters out the nodes which
     * aren't desired.  It returns null when the iteration completes.
     *
     * @param tag the tag to match, or null to indicate all elements
     * @return the next matching element, or else null
     */
    public Element getNextElement (String tag){
        for (Node next = nextNode(); next != null; next = nextNode()) {
            if (next.getNodeType () == Node.ELEMENT_NODE
                && (tag == null || tag.equals (next.getNodeName ())))
            return (Element) next;
        }
        reset();
        return null;
    }

    public void reset (){
        setCurrentNode(startNode);
    }

    public Node getCurrent(){
        return walker.getCurrentNode();
    }
}