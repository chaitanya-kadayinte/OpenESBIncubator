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

/**
* The class <code>XMLReadErrorHandler</code> defines the xml read error
* handler.
*
* @author  Shankar
* @version 1.0 04/20/2001
* @since   SQLBridge 1.0
*/

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.PrintWriter;

/**
* Error handler to report errors and warnings
*/
public class XMLReadErrorHandler implements ErrorHandler
{
	/** Error handler output goes here */
	private PrintWriter out;

	public XMLReadErrorHandler (PrintWriter out)
	{
		this.out = out;
	}

	/**
	* Returns a string describing parse exception details.
	*/
	private String getParseExceptionInfo (SAXParseException spe)
	{
		String systemId = spe.getSystemId ();
		if (systemId == null)
		{
			systemId = "null";
		}
		String info = "URI=" + systemId +
			" Line=" + spe.getLineNumber () +
			": " + spe.getMessage ();
		return info;
	}

	// The following methods are standard SAX ErrorHandler methods.

	public void warning (SAXParseException spe)
		throws SAXException
	{
		out.println ("Warning: " + getParseExceptionInfo (spe));
	}

	public void error (SAXParseException spe)
		throws SAXException
	{
		String message = "Error: " + getParseExceptionInfo (spe);
		throw new SAXException (message);
	}

	public void fatalError (SAXParseException spe)
		throws SAXException
	{
		String message = "Fatal Error: " + getParseExceptionInfo (spe);
		throw new SAXException (message);
	}
}