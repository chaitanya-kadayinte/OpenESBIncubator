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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Enumeration;
import java.util.Vector;

/**
 * @author maverick
 * @created Nov 09, 2006
 * @version 1.0
 */
public class FioranoStackSerializer {
  public FioranoStackSerializer() {
  }
  public static void writeElement(String object, String value, XMLStreamWriter writer) throws XMLStreamException
  {
      if(object != null)
      {
          if(value == null)
              value="";

          writer.writeStartElement(object);
          writer.writeCharacters(value);
          writer.writeEndElement();
      }
  }
  public static void writeVector(String object, Vector vals, XMLStreamWriter writer) throws XMLStreamException
  {
      if (vals != null && vals.size() > 0)
      {
          Enumeration enum1 = vals.elements();
          while (enum1.hasMoreElements())
          {
              String name = (String) enum1.nextElement();

              writer.writeStartElement(object);
              writer.writeCharacters(name);
              writer.writeEndElement();
          }
      }
    }
}
