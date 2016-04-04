/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 * <p>
 * All rights reserved.
 * <p>
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */
package com.fiorano.openesb.route.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import javax.jms.Message;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.fiorano.openesb.route.JMSMessageTransformer;
import org.xml.sax.InputSource;
import com.fiorano.openesb.utils.SAXUtil;
import com.fiorano.openesb.utils.TransformerUtil;
import com.fiorano.openesb.utils.StringUtil;

import sun.misc.BASE64Encoder;

public class JmsMessageTransformerImpl implements JMSMessageTransformer {
    // xsl that needs to be applied
    protected String xslString;
    protected String jmsXslString;

    // transformer type
    protected String transformerType;

    // templates
    private Templates templates = null;
    private Templates jmsTemplates = null;

    // default APP Context
    private String defaultAppContext = null;
    private Transformer jmsTransformer;
    private Transformer bodyTransformer;

    public JmsMessageTransformerImpl(String xsl, String transformerType)
            throws Exception {
        setXSL(xsl, transformerType);
    }

    public JmsMessageTransformerImpl(String xsl, String jmsXSL, String transformerType)
            throws Exception {
        setXSL(xsl, transformerType);
        setJMSXSL(jmsXSL, transformerType);
    }


    public String getXSL() {
        return xslString;
    }


    public String getJMSXSL() {
        return jmsXslString;
    }

    public void setDefaultAppContext(String context) {
        defaultAppContext = context;
    }


    public void setXSL(String xsl, String transformerType)
            throws Exception {
        if (equals(xslString, xsl) && equals(transformerType, this.transformerType))
            return;

        xslString = xsl;
        this.transformerType = transformerType;

        if (xslString == null) {
            templates = null;
            bodyTransformer = null;
        } else {
            TransformerFactory factory = null;

            if (StringUtil.isEmpty(this.transformerType))
                this.transformerType = TransformerUtil.XALAN_TRANSFORMER_FACTORY;
            factory = TransformerUtil.createFactory(this.transformerType);
            templates = factory.newTemplates(new StreamSource(new StringReader(xslString)));
            bodyTransformer = templates.newTransformer();

        }

    }

    public void setJMSXSL(String xsl, String transformerType)
            throws Exception {
        if (equals(jmsXslString, xsl) && equals(transformerType, this.transformerType))
            return;

        jmsXslString = xsl;
        this.transformerType = transformerType;

        if (jmsXslString == null) {
            jmsTemplates = null;
            jmsTransformer = null;
        } else {
            TransformerFactory factory = null;

            if (StringUtil.isEmpty(this.transformerType))
                this.transformerType = TransformerUtil.XALAN_TRANSFORMER_FACTORY;
            factory = TransformerUtil.createFactory(this.transformerType);

            jmsTemplates = factory.newTemplates(new StreamSource(new StringReader(jmsXslString)));
            jmsTransformer = jmsTemplates.newTransformer();

        }
    }

    public String transform(Message msg)
            throws Exception {
        if (jmsTemplates != null) {
            if (transformerType.equalsIgnoreCase("net.sf.saxon.TransformerFactoryImpl")) {
                jmsTransformer = jmsTemplates.newTransformer();
            }
            jmsTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            jmsTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
            String jmsResult = _transform(msg, jmsTransformer);
            InputSource is = new InputSource(new StringReader(jmsResult));
            SAXUtil.createSAXParser(false, false, false).parse(is, new JMSMessageHandler(msg));
        }

        if (templates == null)
            return JmsMessageUtil.getTextData(msg);

        if (transformerType.equalsIgnoreCase("net.sf.saxon.TransformerFactoryImpl")) {
            bodyTransformer = templates.newTransformer();
        }
        return _transform(msg, bodyTransformer);
    }

    private String _transform(Message msg, Transformer transformer)
            throws Exception {
        transformer.setParameter("_TIF_MESSAGE_", msg);

        // set headers in transformer
        HashMap header = JmsMessageUtil.getAllProperties(msg);

        if (header != null) {
            for (Object o : header.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String key = (String) entry.getKey();
                Object value = entry.getValue();
                if (value != null)
                    transformer.setParameter("_TIF_HEADER_" + key, value);
            }
        }

        // set message body in transformer
        String messageBody = JmsMessageUtil.getTextData(msg);

        if (messageBody != null)
            transformer.setParameter("_TIF_BODY_TEXT_", messageBody);

        // set bytes in transformer
        if (JmsMessageUtil.getBytesData(msg) != null) {
            // uuencode byte array
            String encodedBytes = (new BASE64Encoder()).encodeBuffer(JmsMessageUtil.getBytesData(msg));

            transformer.setParameter("_TIF_BODY_BYTE_", encodedBytes);
        }

        Hashtable attachments = JmsMessageUtil.getAttachments(msg);

        if (attachments != null) {
            Enumeration attachmentNames = attachments.keys();

            while (attachmentNames.hasMoreElements()) {
                String name = (String) attachmentNames.nextElement();

                // uuencode attachment byte array
                String encodedBytes = (new BASE64Encoder()).encodeBuffer((byte[])
                        attachments.get(name));

                transformer.setParameter("_TIF_ATTACH_" + name, encodedBytes);
            }
        }

        //  Get Application Context
        String appContext = null;

        CarryForwardContext carryFwdCtx = (CarryForwardContext)
                JmsMessageUtil.getCarryForwardContext(msg);

        if (carryFwdCtx != null)
            appContext = carryFwdCtx.getAppContext();

        // if the message doesnt come with a app context, then use the default app context.
        if (appContext == null && defaultAppContext != null)
            appContext = defaultAppContext;

        if (appContext == null || appContext.trim().equals(""))
            appContext = "<Context></Context>";

        if (messageBody == null || messageBody.trim().equals(""))
            messageBody = "<TIFOSI>tifosi</TIFOSI>";

        // Having app Context as param in mapper.
        transformer.setParameter("_TIF_APP_CONTEXT_", appContext);

        transformer.setURIResolver(new RouteURIResolver(messageBody));
        transformer.setErrorListener(
                new RouteTransformationErrorListener(null));

        StringWriter docWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(docWriter);
        StringReader stringReader = new StringReader(appContext);
        StreamSource source = new StreamSource(stringReader);

        transformer.transform(source, streamResult);
        return docWriter.toString();
    }

    private boolean equals(String source, String target) {
        if ((source == null) && (target == null))
            return true;

        if ((source == null) || (target == null))
            return false;

        if (source.equals(target))
            return true;

        return false;
    }
}

