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

import com.fiorano.openesb.utils.exception.FioranoException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;

public class DocumentFactoryImpl
{
	/**
	*  Constructs a new DocumentFactoryImpl.
	*/
	public DocumentFactoryImpl ()
	{
	}

	/**
	*  Create an empty DOM document.
	*
	*  @return The newly created document.
	*
	*  @exception FioranoException Thrown if an error occurs while
	*             creating the document.
	*/
	public Document createDocument ()
		throws FioranoException
	{
		try
		{
			DocumentBuilder db = XMLUtils.createDocumentBuilder ();
			return db.newDocument ();
		}
		catch (Exception e)
		{
			throw new FioranoException (e);
		}
	}
}

