package com.fiorano.openesb.utils;

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