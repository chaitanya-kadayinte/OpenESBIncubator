///**
// * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
// * Copyright (c) 2008-2014, Fiorano Software Pte. Ltd. and affiliates.
// * <p>
// * All rights reserved.
// * <p>
// * This software is the confidential and proprietary information
// * of Fiorano Software ("Confidential Information").  You
// * shall not disclose such Confidential Information and shall use
// * it only in accordance with the terms of the license agreement
// * enclosed with this product or entered into with Fiorano.
// * <p>
// * Created by chaitanya on 21-02-2016.
// */
//
///**
// * Created by chaitanya on 21-02-2016.
// */
//package com.fiorano.openesb.microservice.launch.impl;
//
//import com.fiorano.openesb.utils.FioranoStaxParser;
//import com.fiorano.openesb.utils.I18NUtil;
//import com.fiorano.openesb.utils.exception.FioranoException;
//import org.xml.sax.InputSource;
//
//import javax.management.MBeanServer;
//import javax.management.MBeanServerFactory;
//import javax.net.ssl.SSLSession;
//import javax.xml.namespace.QName;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.transform.stream.StreamResult;
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.security.Provider;
//import java.security.Security;
//import java.util.Hashtable;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import static com.fiorano.openesb.utils.GateWayConstants.*;
//
//public class WebServiceDeploymentHelper {
//    private Map<String, String> backupSSLValues;
//
//    private void unDeployWS() throws FioranoException {
//        String adminURL = null;
//        String contextName = null;
//        Options options;
//        boolean isSSLEnabled = false;
//
//        String basicAuthuser = "admin";
//        String basicAuthpassword = "admin";
//
//        Hashtable<String, Object> ht = getWSStubConfiguration();
//        try {
//            if ((serviceInstName.equals(ht.get(STUB_INSTANCE_NAME)))) {
//                isSSLEnabled = ht.get(USE_SSL) != null && (Boolean) ht.get(USE_SSL);
//                handleSSLForStub(isSSLEnabled, ht);
//
//                adminURL = getJettyURL(isSSLEnabled) + WSStub_CONTEXT + "FAdminService";
//                contextName = (String) ht.get(CONTEXT_NAME);
//            }
//
//            if (contextName != null) {
//                AdminClient adminClient = new AdminClient();
//                adminClient.setLogin("admin", "admin");
//                try {
//                    if (ht.containsKey(BASIC_AUTH_USER))
//                        basicAuthuser = (String) ht.get(BASIC_AUTH_USER);
//
//                    if (ht.containsKey(BASIC_AUTH_PASSWD))
//                        basicAuthpassword = (String) ht.get(BASIC_AUTH_PASSWD);
//
//                    options = new Options(new String[]{"-l" + adminURL, "-u" + basicAuthuser, "-w" + basicAuthpassword});
//                } catch (MalformedURLException e) {
//                    throw new FioranoException(Bundle.ERROR_UNDEPLOYING_WEBSERVICE.toUpperCase(), e.getMessage());
//                }
//
//                WSDDUndeployment wsddUnDeployment = new WSDDUndeployment();
//                QName qName = new QName(contextName);
//                wsddUnDeployment.addService(qName);
//                String str;
//
//                try {
//                    str = toString(wsddUnDeployment);
//                    adminClient.process(options, new ByteArrayInputStream(str.getBytes()));
//                } catch (Exception e) {
//                    throw new FioranoException(Bundle.ERROR_UNDEPLOYING_WEBSERVICE.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_UNDEPLOYING_WEBSERVICE, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName), e);
//                }
//
//                String wsdlDirPath = System.getProperty("FIORANO_HOME") + File.separator + "esb" + File.separator + "server"
//                        + File.separator + "jetty" + File.separator + "fps" + File.separator + "webapps" + File.separator + "bcwsgateway"
//                        + File.separator + "wsdls" + File.separator + DeploymentUtil.getProfileName() + File.separator + contextName;
//
//                File wsdlDir = new File(wsdlDirPath);
//                if (wsdlDir.exists() && wsdlDir.isDirectory())
//                    FileUtil.deleteDir(wsdlDir);
//
//            }
//        } finally {
//            if (isSSLEnabled)
//                restoreSSLSettings(ht);
//        }
//    }
//
//    private void unDeployRESTStub() throws FioranoException {
//        Hashtable<String, Object> unDeploymentInfo = getRESTStubConfiguration();
//
//        String userName = (String) unDeploymentInfo.get(BASIC_AUTH_USER);
//        String password = (String) unDeploymentInfo.get(BASIC_AUTH_PASSWD);
//
//        boolean isSSLEnabled = unDeploymentInfo.get(USE_SSL) != null && (Boolean) unDeploymentInfo.get(USE_SSL);
//        handleSSLForStub(isSSLEnabled, unDeploymentInfo);
//
//        BufferedOutputStream bos = null;
//        HttpURLConnection urlConn = null;
//        try {
//            String jettyURL = getJettyURL(isSSLEnabled);
//            urlConn = getURLConnection((jettyURL.endsWith("/") ? jettyURL.substring(0, jettyURL.length() - 1)
//                    : jettyURL) + RESTGATEWAY_SEGMENT, userName, password);
//            urlConn.setRequestMethod("POST");
//            urlConn.setDoOutput(true);
//            urlConn.setRequestProperty("Content-Type", "text/xml");
//            try {
//                bos = new BufferedOutputStream(urlConn.getOutputStream());
//                bos.write(buildUndeploymentXML(unDeploymentInfo, RESTSTUB_GUID).getBytes());
//            } finally {
//                if (bos != null) {
//                    try {
//                        bos.flush();
//                        bos.close();
//                    } catch (IOException e) {
//                        //Ignore
//                    }
//                }
//            }
//            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                throw new FioranoException(Bundle.ERROR_UNDEPLOYING_RESTSERVICE.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_UNDEPLOYING_RESTSERVICE, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName));
//            }
//        } catch (Exception e) {
//            throw new FioranoException(Bundle.ERROR_UNDEPLOYING_RESTSERVICE.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_UNDEPLOYING_RESTSERVICE, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName), e);
//        } finally {
//            if (isSSLEnabled)
//                restoreSSLSettings(unDeploymentInfo);
//        }
//    }
//
//    public void unDeployHttpStub() throws FioranoException {
//        Hashtable<String, Object> unDeploymentInfo = getHttpStubConfiguration();
//
//        boolean authenticationEnabled = unDeploymentInfo.get(HS_BASIC_AUTH_ENABLED) != null && Boolean.valueOf(unDeploymentInfo.get(HS_BASIC_AUTH_ENABLED).toString());
//        String userName = (String) unDeploymentInfo.get(HS_BASIC_AUTH_USERNAME);
//        String password = (String) unDeploymentInfo.get(HS_BASIC_AUTH_PASSWORD);
//
//        boolean isSSLEnabled = unDeploymentInfo.get(USE_SSL) != null && (Boolean) unDeploymentInfo.get(USE_SSL);
//        handleSSLForStub(isSSLEnabled, unDeploymentInfo);
//
//        BufferedOutputStream bos = null;
//        try {
//            String jettyURL = getJettyURL(isSSLEnabled);
//            HttpURLConnection urlConn = getURLConnection((jettyURL.endsWith("/") ? jettyURL.substring(0, jettyURL.length() - 1)
//                    : jettyURL) + HTTP_GATEWAY_SEGMENT, authenticationEnabled ? userName : "", password);
//            urlConn.setRequestMethod("POST");
//            urlConn.setDoOutput(true);
//            urlConn.setRequestProperty("Content-Type", "text/xml");
//            try {
//                bos = new BufferedOutputStream(urlConn.getOutputStream());
//                bos.write(buildUndeploymentXML(unDeploymentInfo, HTTPSTUB).getBytes());
//            } finally {
//                if (bos != null) {
//                    try {
//                        bos.flush();
//                        bos.close();
//                    } catch (IOException e) {
//                        //Ignore
//                    }
//                }
//            }
//            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                throw new FioranoException(Bundle.ERROR_UNDEPLOYING_HTTPSERVICE.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_UNDEPLOYING_HTTPSERVICE, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName));
//            }
//        } catch (Exception e) {
//            throw new FioranoException(Bundle.ERROR_UNDEPLOYING_HTTPSERVICE.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_UNDEPLOYING_HTTPSERVICE, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName), e);
//        } finally {
//            if (isSSLEnabled)
//                restoreSSLSettings(unDeploymentInfo);
//        }
//    }
//
//
//    /******************************************** Auxiliary methods for undeploying stubs ****************************************/
//
//    private static class TrustingManager implements javax.net.ssl.TrustManager,
//            javax.net.ssl.X509TrustManager {
//        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//            return null;
//        }
//
//        public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
//            return true;
//        }
//
//        public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
//            return true;
//        }
//
//        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
//                throws java.security.cert.CertificateException {
//        }
//
//        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
//                throws java.security.cert.CertificateException {
//        }
//    }
//
//
//    private String getJettyURL(boolean isSSLEnabled) throws FioranoException {
//        MBeanServer mbeanServer = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
//
//        JettyServiceConfig config;
//        try {
//            config = (JettyServiceConfig) JMXUtil.getObjectInstance(mbeanServer, "Fiorano.Esb.Jetty:ServiceType=Jetty,Name=Jetty,type=config");
//        } catch (Exception e) {
//            throw new FioranoException(Bundle.ERROR_GETTING_JETTY_CONFIG_OBJECT_INSTANCE.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_GETTING_JETTY_CONFIG_OBJECT_INSTANCE), e);
//        }
//
//        int jettyPort;
//        if (isSSLEnabled)
//            jettyPort = config.getSSLPortNumber();
//        else
//            jettyPort = config.getPortNumber();
//
//        String protocol;
//        if (isSSLEnabled)
//            protocol = "https://";
//        else
//            protocol = "http://";
//
//        return protocol + "localhost" + ":" + jettyPort;
//
//    }
//
//    private void setSSLSettings(Hashtable ht) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
//        if (ht == null)
//            return;
//
//        System.setProperty(AXIS_SOCKET_SECURE_FACTORY, FioranoSSLSocketFactory.class.getName());
//
//        System.setProperty(HTTPS_USE, "true");
//
//        /*
//        * We want to dynamically register the SunJSSE provider
//        * because we don't want people to have to modify their
//        * JVM setups manually.
//        */
//        String provider;
//        if (ht.containsKey(HTTPS_SECURITY_PROVIDER_CLASS)) {
//            System.setProperty(HTTPS_SECURITY_PROVIDER_CLASS, (String) ht.get(HTTPS_SECURITY_PROVIDER_CLASS));
//            provider = (String) ht.get(HTTPS_SECURITY_PROVIDER_CLASS);
//        } else {
//            System.setProperty(HTTPS_SECURITY_PROVIDER_CLASS, "com.sun.net.ssl.internal.ssl.Provider");
//            provider = "com.sun.net.ssl.internal.ssl.Provider";
//        }
//
//        Security.addProvider((Provider) Class.forName(provider).newInstance());
//
//        System.setProperty(HTTPS_PROTOCOL_HANDLER_PACKAGES, "com.sun.net.ssl.internal.www.protocol");
//
//        // Setup KeyStore
//        if (ht.containsKey(KEY_STORE_TYPE)) {
//            System.setProperty(HTTPS_KEY_STORE_TYPE, (String) ht.get(KEY_STORE_TYPE));
//        } else
//            System.setProperty(HTTPS_KEY_STORE_TYPE, "JKS");
//
//
//        if (ht.containsKey(KEY_STORE_LOCATION)) {
//            System.setProperty(HTTPS_KEY_STORE, (String) ht.get(KEY_STORE_LOCATION));
//        }
//
//        if (ht.containsKey(KEY_STORE_PASSWORD)) {
//            System.setProperty(HTTPS_KEY_STORE_PASSWORD, (String) ht.get(KEY_STORE_PASSWORD));
//        }
//
//        // Setup TrustStore
//        if (ht.containsKey(TRUST_STORE_TYPE)) {
//            System.setProperty(HTTPS_TRUST_STORE_TYPE, (String) ht.get(TRUST_STORE_TYPE));
//        } else
//            System.setProperty(HTTPS_TRUST_STORE_TYPE, "JKS");
//
//        if (ht.containsKey(TRUST_STORE_LOCATION)) {
//            System.setProperty(HTTPS_TRUST_STORE, (String) ht.get(TRUST_STORE_LOCATION));
//        }
//
//        if (ht.containsKey(TRUST_STORE_PASSWORD)) {
//            System.setProperty(HTTPS_TRUST_STORE_PASSWORD, (String) ht.get(TRUST_STORE_PASSWORD));
//        }
//
//        if (ht.containsKey(KEY_CLIENT_PASSWORD)) {
//            System.setProperty(HTTPS_KEY_CLIENT_PASSWORD, (String) ht.get(KEY_CLIENT_PASSWORD));
//        }
//    }
//
//    private void backupOriginalSSLSettings() {
//        if (backupSSLValues.size() > 0)
//            return;
//
//        if (System.getProperty(AXIS_SOCKET_SECURE_FACTORY) != null)
//            backupSSLValues.put(AXIS_SOCKET_SECURE_FACTORY, System.getProperty(AXIS_SOCKET_SECURE_FACTORY));
//        else
//            backupSSLValues.put(AXIS_SOCKET_SECURE_FACTORY, null);
//
//        if (System.getProperty(HTTPS_USE) != null)
//            backupSSLValues.put(HTTPS_USE, System.getProperty(HTTPS_USE));
//        else
//            backupSSLValues.put(HTTPS_USE, null);
//
//        if (System.getProperty(HTTPS_SECURITY_PROVIDER_CLASS) != null)
//            backupSSLValues.put(HTTPS_SECURITY_PROVIDER_CLASS, System.getProperty(HTTPS_SECURITY_PROVIDER_CLASS));
//        else
//            backupSSLValues.put(HTTPS_SECURITY_PROVIDER_CLASS, null);
//
//        if (System.getProperty(HTTPS_PROTOCOL_HANDLER_PACKAGES) != null)
//            backupSSLValues.put(HTTPS_PROTOCOL_HANDLER_PACKAGES, System.getProperty(HTTPS_PROTOCOL_HANDLER_PACKAGES));
//        else
//            backupSSLValues.put(HTTPS_PROTOCOL_HANDLER_PACKAGES, null);
//
//        if (System.getProperty(HTTPS_KEY_STORE_TYPE) != null)
//            backupSSLValues.put(HTTPS_KEY_STORE_TYPE, System.getProperty(HTTPS_KEY_STORE_TYPE));
//        else
//            backupSSLValues.put(HTTPS_KEY_STORE_TYPE, null);
//
//        if (System.getProperty(HTTPS_KEY_STORE) != null)
//            backupSSLValues.put(HTTPS_KEY_STORE, System.getProperty(HTTPS_KEY_STORE));
//        else
//            backupSSLValues.put(HTTPS_KEY_STORE, null);
//
//        if (System.getProperty(HTTPS_KEY_STORE_PASSWORD) != null)
//            backupSSLValues.put(HTTPS_KEY_STORE_PASSWORD, System.getProperty(HTTPS_KEY_STORE_PASSWORD));
//        else
//            backupSSLValues.put(HTTPS_KEY_STORE_PASSWORD, null);
//
//        if (System.getProperty(HTTPS_TRUST_STORE_TYPE) != null)
//            backupSSLValues.put(HTTPS_TRUST_STORE_TYPE, System.getProperty(HTTPS_TRUST_STORE_TYPE));
//        else
//            backupSSLValues.put(HTTPS_TRUST_STORE_TYPE, null);
//
//        if (System.getProperty(HTTPS_TRUST_STORE) != null)
//            backupSSLValues.put(HTTPS_TRUST_STORE, System.getProperty(HTTPS_TRUST_STORE));
//        else
//            backupSSLValues.put(HTTPS_TRUST_STORE, null);
//
//        if (System.getProperty(HTTPS_TRUST_STORE_PASSWORD) != null)
//            backupSSLValues.put(HTTPS_TRUST_STORE_PASSWORD, System.getProperty(HTTPS_TRUST_STORE_PASSWORD));
//        else
//            backupSSLValues.put(HTTPS_TRUST_STORE_PASSWORD, null);
//
//        if (System.getProperty(HTTPS_KEY_CLIENT_PASSWORD) != null)
//            backupSSLValues.put(HTTPS_KEY_CLIENT_PASSWORD, System.getProperty(HTTPS_KEY_CLIENT_PASSWORD));
//        else
//            backupSSLValues.put(HTTPS_KEY_CLIENT_PASSWORD, null);
//    }
//
//    private void restoreSSLSettings(Hashtable<String, Object> unDeploymentInfo) {
//
//        for (Map.Entry<String, String> entry : backupSSLValues.entrySet()) {
//            String value = entry.getValue();
//            if (value != null)
//                System.setProperty(entry.getKey(), value);
//            else
//                System.clearProperty(entry.getKey());
//        }
//        if (unDeploymentInfo.get(IGNORE_HOSTNAME_MISMATCH) != null && (Boolean) unDeploymentInfo.get(IGNORE_HOSTNAME_MISMATCH)) {
//            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
//        }
//        if (unDeploymentInfo.get(ACCEPT_SERVER_CERTIFICATE) != null && (Boolean) unDeploymentInfo.get(ACCEPT_SERVER_CERTIFICATE)) {
//            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory);
//        }
//
//    }
//
//    private void handleSSLForStub(boolean isSSLEnabled, Hashtable<String, Object> unDeploymentInfo) {
//        if (isSSLEnabled) {
//            try {
//                setSSLSettings(unDeploymentInfo);
//                if (unDeploymentInfo.get(IGNORE_HOSTNAME_MISMATCH) != null && (Boolean) unDeploymentInfo.get(IGNORE_HOSTNAME_MISMATCH)) {
//                    javax.net.ssl.HostnameVerifier hv = new javax.net.ssl.HostnameVerifier() {
//                        public boolean verify(String urlHostname, SSLSession certHostname) {
//                            return true;
//                        }
//                    };
//                    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(hv);
//                }
//                if (unDeploymentInfo.get(ACCEPT_SERVER_CERTIFICATE) != null && (Boolean) unDeploymentInfo.get(ACCEPT_SERVER_CERTIFICATE)) {
//                    javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[]{new TrustingManager()};
//                    javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
//                    sc.init(null, trustAllCerts, null);
//                    javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//                }
//            } catch (Exception e) {
//                coreLogger.warn(Bundle.class, Bundle.ERROR_SET_SSL_PARAMS, e);
//            }
//        }
//    }
//
//    private static String buildUndeploymentXML(Hashtable<String, Object> unDeploymentInfo, String stubGUID) throws Exception {
//        String contextName = (String) unDeploymentInfo.get(stubGUID.equals(HTTPSTUB) ? "contextName"
//                : "serviceName");
//        String appGUID = (String) unDeploymentInfo.get(APP_GUID);
//        String instanceName = (String) unDeploymentInfo.get(STUB_INSTANCE_NAME);
//
//        StringWriter writer = new StringWriter();
//        XMLCreator xmlCreator = new XMLCreator(new StreamResult(writer), true, false);
//        xmlCreator.startDocument();
//        xmlCreator.addAttribute("xmlns", stubGUID.equals(HTTPSTUB) ? "http://www.fiorano.com/bc/httpdd"
//                : "http://www.fiorano.com/gateways/restdd");
//        xmlCreator.startElement("undeployment");
//        xmlCreator.addAttribute("name", contextName);
//        xmlCreator.startElement("context");
//
//        xmlCreator.addAttribute("name", "applicationGUID");
//        xmlCreator.addAttribute("value", appGUID);
//        xmlCreator.startElement("parameter");
//        xmlCreator.endElement();
//
//        xmlCreator.addAttribute("name", "serviceInstanceName");
//        xmlCreator.addAttribute("value", instanceName);
//        xmlCreator.startElement("parameter");
//        xmlCreator.endElement();
//
//        xmlCreator.endElement();
//        xmlCreator.endElement();
//        xmlCreator.endDocument();
//
//        writer.flush();
//        writer.close();
//
//        return writer.toString();
//    }
//
//    private static HttpURLConnection getURLConnection(String url, String username, String password)
//            throws IOException {
//        HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
//
//        if (!StringUtil.isEmpty(username)) {
//            String userPassword = username + ":" + password;
//            String encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
//
//            urlConn.setRequestProperty("Authorization", "Basic " + encoding);
//        }
//        return urlConn;
//    }
//
//    private static String toString(WSDDElement element) throws IOException {
//        StringWriter writer = new StringWriter();
//        SerializationContext context = new SerializationContext(writer);
//
//        context.setPretty(true);
//        context.setSendDecl(false);
//        context.setDoMultiRefs(false);
//        element.writeToContext(context);
//        return writer.toString();
//    }
//
//    /**************************************************************************************************************************/
//
//    @SuppressWarnings("ALL")
//    private Hashtable<String, Object> getWSStubConfiguration() throws FioranoException {
//        if (!slp.getServiceGUID().equalsIgnoreCase(WSSTUB) || slp.getConfiguration() == null)
//            return null;
//
//        Hashtable<String, Object> webInstanceData = new Hashtable<String, Object>();
//        webInstanceData.put(STUB_INSTANCE_NAME, serviceInstName);
//
//        // Parse the CDATA section and fetch the required data
//        FioranoStaxParser parser = null;
//        try {
//            parser = new FioranoStaxParser(new StringReader(slp.getConfiguration()));
//            parser.markCursor("java");
//            while (parser.nextElement()) {
//                if (parser.getLocalName().equalsIgnoreCase("object")) {
//                    String className = parser.getAttributeValue(null, "class");
//                    parser.markCursor(parser.getLocalName());
//                    if (className.equalsIgnoreCase(WSSTUB_PM)) {
//                        while (parser.nextElement()) {
//                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                String propertyName = parser.getAttributeValue(null, "property");
//                                if (propertyName.equalsIgnoreCase(CONTEXT_NAME) ||
//                                        propertyName.equalsIgnoreCase(CONTEXT_DESCRIPTION) ||
//                                        propertyName.equalsIgnoreCase(OPERATION_CONFIG) ||
//                                        propertyName.equalsIgnoreCase(SUPPORT_SSL) ||
//                                        propertyName.equalsIgnoreCase(KEY_STORE_LOCATION) ||
//                                        propertyName.equalsIgnoreCase(KEY_STORE_PASSWORD) ||
//                                        propertyName.equalsIgnoreCase(TRUST_STORE_LOCATION) ||
//                                        propertyName.equalsIgnoreCase(TRUST_STORE_PASSWORD) ||
//                                        propertyName.equalsIgnoreCase(KEY_STORE_TYPE) ||
//                                        propertyName.equalsIgnoreCase(TRUST_STORE_TYPE) ||
//                                        propertyName.equalsIgnoreCase(BASIC_AUTH_USER) ||
//                                        propertyName.equalsIgnoreCase(BASIC_AUTH_PASSWD)) {
//
//                                    if (propertyName.equalsIgnoreCase(OPERATION_CONFIG)) {
//                                        parser.markCursor(parser.getLocalName());
//                                        while (parser.nextElement()) {
//                                            if (parser.getLocalName().equalsIgnoreCase("object")) {
//                                                parser.markCursor(parser.getLocalName());
//
//                                                while (parser.nextElement()) {
//                                                    //parser.nextElement();
//                                                    if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                                        propertyName = parser.getAttributeValue(null, "property");
//                                                        if (propertyName.equalsIgnoreCase(OPERATION_DESCRIPTION) ||
//                                                                propertyName.equalsIgnoreCase(OPERATION_NAME)) {
//                                                            // Move to Next element, which is String
//                                                            parser.nextElement();
//                                                            // Get the value
//                                                            String value = parser.getText();
//                                                            webInstanceData.put(propertyName, value);
//                                                        }
//                                                    }
//                                                }
//                                                parser.resetCursor();
//                                            }
//                                        }
//                                        parser.resetCursor();
//                                    } else if (propertyName.equalsIgnoreCase(SUPPORT_SSL)) {
//                                        webInstanceData.put(propertyName, true);
//                                    } else if (propertyName.equalsIgnoreCase(KEY_STORE_PASSWORD) || propertyName.equalsIgnoreCase(TRUST_STORE_PASSWORD)
//                                            || propertyName.equalsIgnoreCase(BASIC_AUTH_PASSWD)) {
//                                        boolean isEncrypted = Boolean.parseBoolean(parser.getAttributeValue(null, "isEncrypted"));
//                                        // Move to Next element, which is String
//                                        parser.nextElement();
//                                        // Get the value
//                                        if (isEncrypted) {
//                                            String encryptedValue = parser.getText();
//                                            try {
//                                                String originalValue = StringEncrypter.getDefaultInstance().decrypt(encryptedValue);
//                                                webInstanceData.put(propertyName, originalValue);
//                                            } catch (StringEncrypter.EncryptionException e) {
//                                                //Ignore
//                                            }
//                                        } else {
//                                            String value = parser.getText();
//                                            webInstanceData.put(propertyName, value);
//                                        }
//                                    } else {
//                                        // Move to Next element, which is String
//                                        parser.nextElement();
//                                        // Get the value
//                                        String value = parser.getText();
//                                        webInstanceData.put(propertyName, value);
//                                    }
//                                } else
//                                    parser.skipElement(parser.getLocalName());
//                            }
//                        }
//                    } else if (className.equalsIgnoreCase(WSSTUB_PM_NEW)) {
//
//
//                        while (parser.nextElement()) {
//                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                String propertyName = parser.getAttributeValue(null, "property");
//                                if (propertyName.equalsIgnoreCase(TRANSPORT_SECURTIY_CONFIG)
//                                        || propertyName.equalsIgnoreCase(WSDEFINITION_CONFIG)) {
//                                    if (propertyName.equalsIgnoreCase(WSDEFINITION_CONFIG)) {
//                                        parser.markCursor(parser.getLocalName());
//
//                                        while (parser.nextElement()) {
//                                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                                propertyName = parser.getAttributeValue(null, "property");
//                                                if (propertyName.equalsIgnoreCase(WSDL)
//                                                        || propertyName.equalsIgnoreCase(BASEURI)) {
//                                                    parser.markCursor(parser.getLocalName());
//                                                    while (parser.nextElement()) {
//                                                        // Get the value
//                                                        String value = parser.getText();
//                                                        webInstanceData.put(propertyName, value);
//                                                    }
//                                                    parser.resetCursor();
//                                                } else if (propertyName.equalsIgnoreCase(STORE_IMPORTS_LOCALLY)) {
//                                                    parser.markCursor(parser.getLocalName());
//                                                    while (parser.nextElement()) {
//                                                        // Get the value
//                                                        String value = parser.getText();
//                                                        webInstanceData.put(propertyName, Boolean.parseBoolean(value));
//                                                    }
//                                                    parser.resetCursor();
//                                                } else parser.skipElement(parser.getLocalName());
//                                            }
//                                        }
//                                        parser.resetCursor();
//                                    } else if (propertyName.equalsIgnoreCase(TRANSPORT_SECURTIY_CONFIG)) {
//                                        String customClass = null;
//                                        boolean customPasswdEncryption = false;
//                                        boolean passwdFromVault = false;
//                                        boolean isEncrypted = false;
//                                        String basicAuthPasswd = null;
//                                        parser.markCursor(parser.getLocalName());
//
//                                        while (parser.nextElement()) {
//                                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                                propertyName = parser.getAttributeValue(null, "property");
//                                                if (propertyName.equalsIgnoreCase(HTTP_AUTH_CONFIG)) {
//                                                    parser.markCursor(parser.getLocalName());
//
//                                                    while (parser.nextElement()) {
//                                                        if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                                            propertyName = parser.getAttributeValue(null, "property");
//                                                            if (propertyName.equalsIgnoreCase(BASIC_AUTH_USER)) {
//                                                                parser.markCursor(parser.getLocalName());
//
//                                                                while (parser.nextElement()) {
//                                                                    // Get the value
//                                                                    String value = parser.getText();
//                                                                    webInstanceData.put(propertyName, value);
//                                                                }
//                                                                parser.resetCursor();
//                                                            } else if (propertyName.equalsIgnoreCase(BASIC_AUTH_PASSWD)) {
//                                                                isEncrypted = Boolean.parseBoolean(parser.getAttributeValue(null, "isEncrypted"));
//                                                                parser.markCursor(parser.getLocalName());
//
//                                                                while (parser.nextElement()) {
//                                                                    // Get the value
////                                                                    if (isEncrypted) {
////                                                                        String encryptedValue = parser.getText();
////                                                                        try {
////                                                                            String originalValue = StringEncrypter.getDefaultInstance().decrypt(encryptedValue);
////                                                                            webInstanceData.put(propertyName, originalValue);
////                                                                        } catch (StringEncrypter.EncryptionException e) {
////                                                                            //Ignore
////                                                                        }
////                                                                    } else {
//                                                                    String value = parser.getText();
//                                                                    webInstanceData.put(propertyName, value);
//                                                                    basicAuthPasswd = value;
////                                                                    }
//                                                                }
//                                                                parser.resetCursor();
//
//                                                            } else if (propertyName.equalsIgnoreCase(USE_HTTP_AUTH)) {
//                                                                parser.markCursor(parser.getLocalName());
//
//                                                                while (parser.nextElement()) {
//                                                                    // Get the value
//                                                                    String value = parser.getText();
//                                                                    webInstanceData.put(propertyName, Boolean.parseBoolean(value));
//                                                                }
//                                                                parser.resetCursor();
//                                                            } else if (propertyName.equalsIgnoreCase(CUSTOM_CLASS)) {
//                                                                parser.markCursor(parser.getLocalName());
//
//                                                                while (parser.nextElement()) {
//                                                                    // Get the value
//                                                                    String value = parser.getText();
//                                                                    customClass = value;
//                                                                }
//                                                                parser.resetCursor();
//                                                            } else if (propertyName.equalsIgnoreCase(CUSTOM_PASSWORD_ENCRYPTION)) {
//                                                                parser.markCursor(parser.getLocalName());
//
//                                                                while (parser.nextElement()) {
//                                                                    // Get the value
//                                                                    String value = parser.getText();
//                                                                    customPasswdEncryption = "true".equalsIgnoreCase(value);
//                                                                }
//                                                                parser.resetCursor();
//                                                            } else if (propertyName.equalsIgnoreCase(PASSWORD_FROM_VAULT)) {
//                                                                parser.markCursor(parser.getLocalName());
//
//                                                                while (parser.nextElement()) {
//                                                                    // Get the value
//                                                                    String value = parser.getText();
//                                                                    passwdFromVault = "true".equalsIgnoreCase(value);
//                                                                }
//                                                                parser.resetCursor();
//                                                            } else parser.skipElement(parser.getLocalName());
//                                                        }
//                                                    }
//                                                    parser.resetCursor();
//
//                                                } else if (propertyName.equalsIgnoreCase(SSL_CONFIG)) {
//                                                    parseSSLConfiguration(webInstanceData, parser);
//                                                } else parser.skipElement(parser.getLocalName());
//                                            }
//                                        }
//                                        if (basicAuthPasswd != null && isEncrypted) {
//                                            String decryptedPassword = decryptPassword(basicAuthPasswd, customClass, customPasswdEncryption, passwdFromVault);
//                                            webInstanceData.put(BASIC_AUTH_PASSWD, decryptedPassword);
//                                        }
//                                        parser.resetCursor();
//                                    }
//
//                                } else
//                                    parser.skipElement(parser.getLocalName());
//                            }
//                        }
//                    }
//                    parser.resetCursor();
//                }
//            }
//        } catch (XMLStreamException e) {
//            throw new FioranoException(Bundle.ERROR_PARSING_WSSTUB_CONFIGURATION.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_PARSING_WSSTUB_CONFIGURATION, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName), e);
//        } finally {
//            parser.resetCursor();
//        }
//
//        List<ManageableProperty> properties = slp.getManageableProperties();
//        for (ManageableProperty prop : properties) {
//            String propName = webInstanceManagableProps.get(prop.getName());
//            if (propName != null) {
//                if (webInstanceData.containsKey(propName)) {
//                    webInstanceData.remove(propName);
//                }
//                webInstanceData.put(propName, prop.getValue());
//            }
//        }
//
//        if (!webInstanceData.containsKey(USE_SSL)) {
//            if (!webInstanceData.containsKey(SUPPORT_SSL))
//                webInstanceData.put(USE_SSL, false);
//            else webInstanceData.put(USE_SSL, (Boolean) webInstanceData.get(SUPPORT_SSL));
//        }
//
//        if (webInstanceData.containsKey(WSDL)) {
//            String contextName;
//            contextName = getContextName((String) webInstanceData.get(WSDL), (String) webInstanceData.get(BASEURI));
//            webInstanceData.put(CONTEXT_NAME, contextName);
//        }
//
//        return webInstanceData;
//    }
//
//    private Hashtable<String, Object> getHttpStubConfiguration() throws FioranoException {
//        if (!slp.getServiceGUID().equalsIgnoreCase(HTTPSTUB) || slp.getConfiguration() == null)
//            return null;
//
//        boolean isEncrypted = false;
//        boolean ishttpAuthPasswdEncrypted = false;
//        String hsBasicAuthPassword = null;
//        String httpCustomClass = null;
//        boolean isHttpPasswordFromVault = false;
//        boolean isHttpCustomEncrypted = false;
//
//
//        Hashtable<String, Object> webInstanceData = new Hashtable<String, Object>();
//        webInstanceData.put(STUB_INSTANCE_NAME, serviceInstName);
//        webInstanceData.put(APP_GUID, appGUID);
//
//        // Parse the CDATA section and fetch the required data
//        FioranoStaxParser parser = null;
//        try {
//            parser = new FioranoStaxParser(new StringReader(slp.getConfiguration()));
//            parser.markCursor("java");
//            while (parser.nextElement()) {
//                if (parser.getLocalName().equalsIgnoreCase("object")) {
//                    String className = parser.getAttributeValue(null, "class");
//                    parser.markCursor(parser.getLocalName());
//                    if (className.equalsIgnoreCase(HTTPSTUBPM)) {
//                        while (parser.nextElement()) {
//                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                String propertyName = parser.getAttributeValue(null, "property");
//                                if (propertyName.equalsIgnoreCase(CONTEXT_NAME) ||
//                                        propertyName.equalsIgnoreCase(SUPPORT_SSL) ||
//                                        propertyName.equalsIgnoreCase(KEY_STORE_LOCATION) ||
//                                        propertyName.equalsIgnoreCase(TRUST_STORE_LOCATION) ||
//                                        propertyName.equalsIgnoreCase(KEY_STORE_PASSWORD) ||
//                                        propertyName.equalsIgnoreCase(TRUST_STORE_PASSWORD) ||
//                                        propertyName.equalsIgnoreCase(KEY_CLIENT_PASSWORD) ||
//                                        propertyName.equalsIgnoreCase(HS_BASIC_AUTH_ENABLED) ||
//                                        propertyName.equalsIgnoreCase(HS_BASIC_AUTH_USERNAME) ||
//                                        propertyName.equalsIgnoreCase(HS_BASIC_AUTH_PASSWORD) ||
//                                        propertyName.equalsIgnoreCase(KEY_STORE_TYPE) ||
//                                        propertyName.equalsIgnoreCase(TRUST_STORE_TYPE) ||
//                                        propertyName.equalsIgnoreCase(CUSTOM_CLASS) ||
//                                        propertyName.equalsIgnoreCase(CUSTOM_PASSWORD_ENCRYPTION) ||
//                                        propertyName.equalsIgnoreCase(PASSWORD_FROM_VAULT)) {
//                                    if (propertyName.equalsIgnoreCase(PASSWORD_FROM_VAULT)) {
//                                        parser.markCursor(parser.getLocalName());
//                                        while (parser.nextElement()) {
//                                            // Get the value
//                                            String value = parser.getText();
//                                            isHttpPasswordFromVault = "true".equalsIgnoreCase(value);
//
//                                        }
//                                        parser.resetCursor();
//                                    } else if (propertyName.equalsIgnoreCase(CUSTOM_PASSWORD_ENCRYPTION)) {
//                                        parser.markCursor(parser.getLocalName());
//                                        while (parser.nextElement()) {
//                                            // Get the value
//                                            String value = parser.getText();
//                                            isHttpCustomEncrypted = "true".equalsIgnoreCase(value);
//
//                                        }
//                                        parser.resetCursor();
//                                    } else if (propertyName.equalsIgnoreCase(CUSTOM_CLASS)) {
//                                        parser.markCursor(parser.getLocalName());
//                                        while (parser.nextElement()) {
//                                            // Get the value
//                                            httpCustomClass = parser.getText();
//
//                                        }
//                                        parser.resetCursor();
//                                    } else if (propertyName.equalsIgnoreCase(SUPPORT_SSL) ||
//                                            propertyName.equalsIgnoreCase(HS_BASIC_AUTH_ENABLED)) {
//                                        parser.markCursor(parser.getLocalName());
//                                        while (parser.nextElement()) {
//                                            // Get the value
//                                            String value = parser.getText();
//                                            webInstanceData.put(propertyName, Boolean.parseBoolean(value));
//                                        }
//                                        parser.resetCursor();
//                                    } else if (propertyName.equalsIgnoreCase(KEY_STORE_PASSWORD) ||
//                                            propertyName.equalsIgnoreCase(HS_BASIC_AUTH_PASSWORD) ||
//                                            propertyName.equals(KEY_CLIENT_PASSWORD) ||
//                                            propertyName.equals(TRUST_STORE_PASSWORD)) {
//
//                                        isEncrypted = Boolean.parseBoolean(parser.getAttributeValue(null, "isEncrypted"));
//                                        webInstanceData.put(IS_SSL_PASSWD_ENCRYPTED, isEncrypted);
//                                        parser.markCursor(parser.getLocalName());
//
//                                        while (parser.nextElement()) {
//                                            // Get the value
////                                            if (isEncrypted) {
////                                                String encryptedValue = parser.getText();
////                                                try {
////                                                    String originalValue = StringEncrypter.getDefaultInstance().decrypt(encryptedValue);
////                                                    webInstanceData.put(propertyName, originalValue);
////                                                } catch (StringEncrypter.EncryptionException e) {
////                                                    //Ignore
////                                                }
////                                            } else {
//                                            String value = parser.getText();
//                                            webInstanceData.put(propertyName, value);
//
//                                            if (HS_BASIC_AUTH_PASSWORD.equalsIgnoreCase(propertyName)) {
//                                                hsBasicAuthPassword = value;
//                                                ishttpAuthPasswdEncrypted = isEncrypted;
//                                            }
//                                            parser.resetCursor();
//                                        }
//                                    } else {
//                                        // Move to Next element, which is String
//                                        parser.nextElement();
//                                        // Get the value
//                                        String value = parser.getText();
//                                        webInstanceData.put(propertyName, value);
//                                    }
//                                } else if (propertyName.equalsIgnoreCase(SSL_CONFIG)) {
//                                    parseSSLConfiguration(webInstanceData, parser);
//                                } else
//                                    parser.skipElement(parser.getLocalName());
//                            }
//                        }
//                    }
//                    parser.resetCursor();
//                }
//            }
//
//        } catch (XMLStreamException e) {
//            throw new FioranoException(Bundle.ERROR_PARSING_HTTPSTUB_CONFIGURATION.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_PARSING_HTTPSTUB_CONFIGURATION, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName), e);
//        } finally {
//            parser.resetCursor();
//        }
//
//        if (isEncrypted) {
//            if (!StringUtil.isEmpty(hsBasicAuthPassword) && ishttpAuthPasswdEncrypted) {
//                String decryptPassword = decryptPassword(hsBasicAuthPassword,
//                        httpCustomClass,
//                        isHttpCustomEncrypted,
//                        isHttpPasswordFromVault);
//                webInstanceData.put(HS_BASIC_AUTH_PASSWORD, decryptPassword);
//            }
//        }
//
//        List<ManageableProperty> properties = slp.getManageableProperties();
//        Iterator iter = properties.iterator();
//
//        while (iter.hasNext()) {
//            ManageableProperty prop = (ManageableProperty) iter.next();
//            String propName = webInstanceManagableProps.get(prop.getName());
//            if (propName != null) {
//                if (webInstanceData.containsKey(propName)) {
//                    webInstanceData.remove(propName);
//                }
//                webInstanceData.put(propName, prop.getValue());
//            }
//        }
//        if (!webInstanceData.containsKey(USE_SSL)) {
//            if (!webInstanceData.containsKey(SUPPORT_SSL))
//                webInstanceData.put(USE_SSL, false);
//            else if (webInstanceData.containsKey(SUPPORT_SSL))
//                webInstanceData.put(USE_SSL, true);
//        }
//
//        return webInstanceData;
//
//    }
//
//    private Hashtable<String, Object> getRESTStubConfiguration() throws FioranoException {
//        if (!slp.getServiceGUID().equalsIgnoreCase(RESTSTUB_GUID) || slp.getConfiguration() == null)
//            return null;
//
//        Hashtable<String, Object> webInstanceData = new Hashtable<String, Object>();
//        webInstanceData.put(STUB_INSTANCE_NAME, serviceInstName);
//        webInstanceData.put(APP_GUID, appGUID);
//
//        // Parse the CDATA section and fetch the required data
//        FioranoStaxParser parser = null;
//        try {
//            parser = new FioranoStaxParser(new StringReader(slp.getConfiguration()));
//            parser.markCursor("java");
//            while (parser.nextElement()) {
//                if (parser.getLocalName().equalsIgnoreCase("object")) {
//                    String className = parser.getAttributeValue(null, "class");
//                    parser.markCursor(parser.getLocalName());
//                    if (className.equalsIgnoreCase(RESTSTUB_CONFIG_CLASSNAME)) {
//                        while (parser.nextElement()) {
//                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                String propertyName = parser.getAttributeValue(null, "property");
//                                if (propertyName.equalsIgnoreCase(TRANSPORT_SECURTIY_CONFIG)
//                                        || propertyName.equalsIgnoreCase(WADL_CONFIGURATION) || propertyName.equalsIgnoreCase(RESTFUL_SERVICE_NAME)) {
//                                    if (propertyName.equalsIgnoreCase(RESTFUL_SERVICE_NAME)) {
//                                        parser.markCursor(parser.getLocalName());
//                                        while (parser.nextElement()) {
//                                            // Get the value
//                                            String value = parser.getText();
//                                            webInstanceData.put(propertyName, value);
//                                        }
//                                        parser.resetCursor();
//                                    }
//                                    if (propertyName.equalsIgnoreCase(WADL_CONFIGURATION)) {
//                                        parser.markCursor(parser.getLocalName());
//
//                                        while (parser.nextElement()) {
//                                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                                propertyName = parser.getAttributeValue(null, "property");
//                                                if (propertyName.equalsIgnoreCase(WADL)) {
//                                                    parser.markCursor(parser.getLocalName());
//                                                    while (parser.nextElement()) {
//                                                        // Get the value
//                                                        String value = parser.getText();
//                                                        webInstanceData.put(propertyName, value);
//                                                    }
//                                                    parser.resetCursor();
//                                                } else parser.skipElement(parser.getLocalName());
//                                            }
//                                        }
//                                        parser.resetCursor();
//                                    } else if (propertyName.equalsIgnoreCase(TRANSPORT_SECURTIY_CONFIG)) {
//                                        parser.markCursor(parser.getLocalName());
//
//                                        while (parser.nextElement()) {
//                                            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                                                propertyName = parser.getAttributeValue(null, "property");
//                                                if (propertyName.equalsIgnoreCase(HTTP_AUTH_CONFIG)) {
//                                                    parseHTTPAuthentication(webInstanceData, parser);
//                                                } else if (propertyName.equalsIgnoreCase(SSL_CONFIG)) {
//                                                    parseSSLConfiguration(webInstanceData, parser);
//                                                } else parser.skipElement(parser.getLocalName());
//                                            }
//                                        }
//                                        parser.resetCursor();
//                                    }
//
//                                } else
//                                    parser.skipElement(parser.getLocalName());
//                            }
//                        }
//                    }
//                    parser.resetCursor();
//                }
//            }
//        } catch (XMLStreamException e) {
//            throw new FioranoException(Bundle.ERROR_PARSING_RESTSTUB_CONFIGURATION.toUpperCase(), I18NUtil.getMessage(Bundle.class, Bundle.ERROR_PARSING_RESTSTUB_CONFIGURATION, appGUID + ITifosiConstants.APP_VERSION_DELIM + appVersion, serviceInstName), e);
//        } finally {
//            try {
//                parser.disposeParser();
//            } catch (XMLStreamException e) {
//                //Ignore
//            }
//        }
//
//        List<ManageableProperty> properties = slp.getManageableProperties();
//        //todo see if this is necessary
//        for (ManageableProperty property : properties) {
//            String propName = webInstanceManagableProps.get(property.getName());
//            if (propName != null) {
//                if (webInstanceData.containsKey(propName)) {
//                    webInstanceData.remove(propName);
//                }
//                webInstanceData.put(propName, property.getValue());
//            }
//        }
//
//        if (!webInstanceData.containsKey(USE_SSL)) {
//            if (!webInstanceData.containsKey(SUPPORT_SSL))
//                webInstanceData.put(USE_SSL, false);
//            else if (webInstanceData.containsKey(SUPPORT_SSL))
//                webInstanceData.put(USE_SSL, true);
//        }
//
//        return webInstanceData;
//    }
//
//    private void parseHTTPAuthentication(Hashtable<String, Object> webInstanceData, FioranoStaxParser parser) throws XMLStreamException {
//        String propertyName;
//
//        boolean isEncrypted = false;
//        while (parser.nextElement()) {
//            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                propertyName = parser.getAttributeValue(null, "property");
//                if (propertyName.equalsIgnoreCase(BASIC_AUTH_USER)) {
//                    parser.markCursor(parser.getLocalName());
//
//                    while (parser.nextElement()) {
//                        // Get the value
//                        String value = parser.getText();
//                        webInstanceData.put(propertyName, value);
//                    }
//                    parser.resetCursor();
//                } else if (propertyName.equalsIgnoreCase(BASIC_AUTH_PASSWD)) {
//                    isEncrypted = Boolean.parseBoolean(parser.getAttributeValue(null, "isEncrypted"));
//                    webInstanceData.put(IS_BASIC_AUTH_PASSWD_ENCRYPTED, isEncrypted);
//                    parser.markCursor(parser.getLocalName());
//
//                    while (parser.nextElement()) {
//                        // Get the value
////                        if (isEncrypted) {
////                            String encryptedValue = parser.getText();
////                            try {
////                                String originalValue = StringEncrypter.getDefaultInstance().decrypt(encryptedValue);
////                                webInstanceData.put(propertyName, originalValue);
////                            } catch (StringEncrypter.EncryptionException e) {
////                                //Ignore
////                            }
////                        } else {
//                        String value = parser.getText();
//                        webInstanceData.put(propertyName, value);
////                        }
//                    }
//                    parser.resetCursor();
//
//                } else if (propertyName.equalsIgnoreCase(USE_HTTP_AUTH)) {
//                    parser.markCursor(parser.getLocalName());
//
//                    while (parser.nextElement()) {
//                        // Get the value
//                        String value = parser.getText();
//                        webInstanceData.put(propertyName, Boolean.parseBoolean(value));
//                    }
//                    parser.resetCursor();
//                } else if (propertyName.equalsIgnoreCase(CUSTOM_CLASS)) {
//                    parser.markCursor(parser.getLocalName());
//
//                    while (parser.nextElement()) {
//                        // Get the value
//                        String value = parser.getText();
//                        webInstanceData.put(propertyName, value);
//                    }
//                    parser.resetCursor();
//                } else if (propertyName.equalsIgnoreCase(CUSTOM_PASSWORD_ENCRYPTION)) {
//                    parser.markCursor(parser.getLocalName());
//
//                    while (parser.nextElement()) {
//                        // Get the value
//                        String value = parser.getText();
//                        webInstanceData.put(propertyName, "true".equalsIgnoreCase(value));
//                    }
//                    parser.resetCursor();
//                } else if (propertyName.equalsIgnoreCase(PASSWORD_FROM_VAULT)) {
//                    parser.markCursor(parser.getLocalName());
//
//                    while (parser.nextElement()) {
//                        // Get the value
//                        String value = parser.getText();
//                        webInstanceData.put(propertyName, "true".equalsIgnoreCase(value));
//                    }
//                    parser.resetCursor();
//                } else parser.skipElement(parser.getLocalName());
//            }
//        }
//        parser.resetCursor();
//        if (webInstanceData.get(BASIC_AUTH_PASSWD) != null &&
//                webInstanceData.get(IS_BASIC_AUTH_PASSWD_ENCRYPTED) != null &&
//                Boolean.valueOf(webInstanceData.get(IS_BASIC_AUTH_PASSWD_ENCRYPTED).toString())) {
//
//            String cClass = (String) webInstanceData.get(CUSTOM_CLASS);
//            Boolean passEnc = webInstanceData.get(CUSTOM_PASSWORD_ENCRYPTION) == null ? false : Boolean.valueOf(webInstanceData.get(CUSTOM_PASSWORD_ENCRYPTION).toString());
//            Boolean passV = webInstanceData.get(PASSWORD_FROM_VAULT) == null ? false : Boolean.valueOf(webInstanceData.get(PASSWORD_FROM_VAULT).toString());
//
//            String decryptedPassword = decryptPassword((String) webInstanceData.get(BASIC_AUTH_PASSWD), cClass, passEnc, passV);
//            webInstanceData.put(BASIC_AUTH_PASSWD, decryptedPassword);
//            webInstanceData.remove(CUSTOM_CLASS);
//            webInstanceData.remove(CUSTOM_PASSWORD_ENCRYPTION);
//            webInstanceData.remove(PASSWORD_FROM_VAULT);
//            webInstanceData.remove(IS_BASIC_AUTH_PASSWD_ENCRYPTED);
//        }
//    }
//
//    private void parseSSLConfiguration(Hashtable<String, Object> webInstanceData, FioranoStaxParser parser) throws XMLStreamException {
//        String propertyName;
//        boolean foundNamedConfig = false;
//        parser.markCursor(parser.getLocalName());
//
//        while (parser.nextElement()) {
//            if (parser.getLocalName().equalsIgnoreCase("void")) {
//                propertyName = parser.getAttributeValue(null, "property");
//
//                if (propertyName.equalsIgnoreCase(CONFIG_NAME)) {
//                    parser.markCursor(parser.getLocalName());
//
//                    while (parser.nextElement()) {
//                        // Get the value
//                        String value = parser.getText();
//                        Map<String, String> namedConfigs = slp.getNamedConfigurations();
//                        String sslConfig = namedConfigs.get(ConfigurationRepoConstants.RESOURCE + "__" + value);
//                        parseSSLNamedConfig(webInstanceData, sslConfig);
//                        foundNamedConfig = true;
//                    }
//                    parser.resetCursor();
//                } else {
//                    parseSSLNonNamedConfigs(webInstanceData, parser, propertyName, foundNamedConfig);
//                }
//            }
//        }
//        parser.resetCursor();
//        String cClass = (String) webInstanceData.get(CUSTOM_CLASS);
//        Boolean passEnc = webInstanceData.get(CUSTOM_PASSWORD_ENCRYPTION) == null ? false : Boolean.valueOf(webInstanceData.get(CUSTOM_PASSWORD_ENCRYPTION).toString());
//        Boolean passV = webInstanceData.get(PASSWORD_FROM_VAULT) == null ? false : Boolean.valueOf(webInstanceData.get(PASSWORD_FROM_VAULT).toString());
//
//        if (webInstanceData.get(IS_SSL_PASSWD_ENCRYPTED) != null &&
//                Boolean.valueOf(webInstanceData.get(IS_SSL_PASSWD_ENCRYPTED).toString())) {
//            if (!StringUtil.isEmpty((String) webInstanceData.get(KEY_STORE_PASSWORD))) {
//                String keyStorePasswd = decryptPassword((String) webInstanceData.get(KEY_STORE_PASSWORD), cClass, passEnc, passV);
//                webInstanceData.put(KEY_STORE_PASSWORD, keyStorePasswd);
//            }
//            if (!StringUtil.isEmpty((String) webInstanceData.get(KEY_CLIENT_PASSWORD))) {
//
//                String keyClientPasswd = decryptPassword((String) webInstanceData.get(KEY_CLIENT_PASSWORD), cClass, passEnc, passV);
//                webInstanceData.put(KEY_CLIENT_PASSWORD, keyClientPasswd);
//            }
//            if (!StringUtil.isEmpty((String) webInstanceData.get(TRUST_STORE_PASSWORD))) {
//
//                String trustStorePasswd = decryptPassword((String) webInstanceData.get(TRUST_STORE_PASSWORD), cClass, passEnc, passV);
//                webInstanceData.put(TRUST_STORE_PASSWORD, trustStorePasswd);
//            }
//        }
//        webInstanceData.remove(CUSTOM_CLASS);
//        webInstanceData.remove(CUSTOM_PASSWORD_ENCRYPTION);
//        webInstanceData.remove(PASSWORD_FROM_VAULT);
//        webInstanceData.remove(IS_SSL_PASSWD_ENCRYPTED);
//    }
//
//    private void parseSSLNonNamedConfigs(Hashtable<String, Object> webInstanceData, FioranoStaxParser parser, String propertyName, boolean foundNamedConfig) throws XMLStreamException {
//        String customClass = null;
//        boolean isEncrypted = false;
//
//        if (propertyName.equalsIgnoreCase(KEY_STORE_LOCATION)
//                || propertyName.equalsIgnoreCase(TRUST_STORE_LOCATION)
//                || propertyName.equalsIgnoreCase(KEY_STORE_TYPE)
//                || propertyName.equalsIgnoreCase(TRUST_STORE_TYPE)) {
//            parser.markCursor(parser.getLocalName());
//
//            while (parser.nextElement()) {
//                // Get the value
//                if (!foundNamedConfig) {
//                    String value = parser.getText();
//                    webInstanceData.put(propertyName, value);
//                }
//            }
//            parser.resetCursor();
//        } else if (propertyName.equalsIgnoreCase(KEY_CLIENT_PASSWORD)
//                || propertyName.equalsIgnoreCase(KEY_STORE_PASSWORD)
//                || propertyName.equalsIgnoreCase(TRUST_STORE_PASSWORD)) {
//            isEncrypted = Boolean.parseBoolean(parser.getAttributeValue(null, "isEncrypted"));
//            webInstanceData.put(IS_SSL_PASSWD_ENCRYPTED, isEncrypted);
//            parser.markCursor(parser.getLocalName());
//
//            while (parser.nextElement()) {
//                // Get the value
//                if (!foundNamedConfig) {
////                    if (isEncrypted) {
////                        String encryptedValue = parser.getText();
////                        try {
////                            String originalValue = StringEncrypter.getDefaultInstance().decrypt(encryptedValue);
////                            webInstanceData.put(propertyName, originalValue);
////                        } catch (StringEncrypter.EncryptionException e) {
////                            //Ignore
////                        }
////                    } else {
//                    String value = parser.getText();
//                    webInstanceData.put(propertyName, value);
////                    }
//                }
//            }
//            parser.resetCursor();
//        } else if (propertyName.equalsIgnoreCase(USE_SSL)
//                || propertyName.equalsIgnoreCase(ACCEPT_SERVER_CERTIFICATE)
//                || propertyName.equalsIgnoreCase(IGNORE_HOSTNAME_MISMATCH)) {
//            parser.markCursor(parser.getLocalName());
//
//            while (parser.nextElement()) {
//                // Get the value
//                if (!foundNamedConfig) {
//                    String value = parser.getText();
//                    webInstanceData.put(propertyName, Boolean.parseBoolean(value));
//                }
//            }
//            parser.resetCursor();
//        } else if (propertyName.equalsIgnoreCase(CUSTOM_CLASS)) {
//            parser.markCursor(parser.getLocalName());
//
//            while (parser.nextElement()) {
//                // Get the value
//                if (!foundNamedConfig) {
//                    String value = parser.getText();
//                    webInstanceData.put(propertyName, value);
//                }
//            }
//            parser.resetCursor();
//        } else if (propertyName.equalsIgnoreCase(CUSTOM_PASSWORD_ENCRYPTION)) {
//            parser.markCursor(parser.getLocalName());
//
//            while (parser.nextElement()) {
//                // Get the value
//                if (!foundNamedConfig) {
//                    String value = parser.getText();
//                    webInstanceData.put(propertyName, value);
//                }
//            }
//            parser.resetCursor();
//        } else if (propertyName.equalsIgnoreCase(PASSWORD_FROM_VAULT)) {
//            parser.markCursor(parser.getLocalName());
//
//            while (parser.nextElement()) {
//                // Get the value
//                if (!foundNamedConfig) {
//                    String value = parser.getText();
//                    webInstanceData.put(propertyName, "true".equalsIgnoreCase(value));
//                }
//            }
//            parser.resetCursor();
//        } else parser.skipElement(parser.getLocalName());
//
//    }
//
//    private void parseSSLNamedConfig(Hashtable<String, Object> webInstanceData, String configuration) throws XMLStreamException {
//        FioranoStaxParser staxParser = null;
//
//        try {
//            staxParser = new FioranoStaxParser(new StringReader(configuration));
//            staxParser.markCursor(NAMED_CONFIGURATION);
//
//            while (staxParser.nextElement()) {
//                String localName = staxParser.getLocalName();
//                if (localName.equalsIgnoreCase(USE_SSL)
//                        || localName.equalsIgnoreCase(ACCEPT_SERVER_CERTIFICATE)
//                        || localName.equalsIgnoreCase(IGNORE_HOSTNAME_MISMATCH)) {
//                    staxParser.markCursor(staxParser.getLocalName());
//
//                    String valueSSL = staxParser.getText();
//                    webInstanceData.put(localName, Boolean.parseBoolean(valueSSL));
//                    staxParser.resetCursor();
//
//                } else if (localName.equalsIgnoreCase(TRUST_STORE_PASSWORD)
//                        || localName.equalsIgnoreCase(KEY_STORE_PASSWORD)
//                        || localName.equalsIgnoreCase(KEY_CLIENT_PASSWORD)) {
//                    staxParser.markCursor(staxParser.getLocalName());
//                    String encryptedValue = staxParser.getText();
//                    //Assuming encrypted
//                    webInstanceData.put(IS_SSL_PASSWD_ENCRYPTED, true);
////                    String originalValue = null;
////                    try {
////                        originalValue = StringEncrypter.getDefaultInstance().decrypt(encryptedValue);
////                    } catch (StringEncrypter.EncryptionException e) {
////                        //Ignore
////                    }
//                    webInstanceData.put(localName, encryptedValue);
//                    staxParser.resetCursor();
//
//                } else if (localName.equalsIgnoreCase(KEY_STORE_LOCATION)
//                        || localName.equalsIgnoreCase(TRUST_STORE_LOCATION)
//                        || localName.equalsIgnoreCase(KEY_STORE_TYPE)
//                        || localName.equalsIgnoreCase(TRUST_STORE_TYPE)) {
//                    staxParser.markCursor(staxParser.getLocalName());
//                    String value = staxParser.getText();
//                    webInstanceData.put(localName, value);
//                    staxParser.resetCursor();
//                } else if (localName.equalsIgnoreCase(CUSTOM_CLASS)) {
//                    staxParser.markCursor(staxParser.getLocalName());
//
//                    String value = staxParser.getText();
//                    webInstanceData.put(localName, value);
//
//                    staxParser.resetCursor();
//                } else if (localName.equalsIgnoreCase(CUSTOM_PASSWORD_ENCRYPTION)) {
//                    staxParser.markCursor(staxParser.getLocalName());
//
//                    String value = staxParser.getText();
//                    webInstanceData.put(localName, "true".equalsIgnoreCase(value));
//
//                    staxParser.resetCursor();
//                } else if (localName.equalsIgnoreCase(PASSWORD_FROM_VAULT)) {
//                    staxParser.markCursor(staxParser.getLocalName());
//
//                    String value = staxParser.getText();
//                    webInstanceData.put(localName, "true".equalsIgnoreCase(value));
//
//                    staxParser.resetCursor();
//                }
//
//            }
//            staxParser.resetCursor();
//        } finally {
//            if (staxParser != null)
//                staxParser.disposeParser();
//        }
//    }
//
//    private String getContextName(String wsdl, String baseUri) throws FioranoException {
//        try {
//            WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
//            wsdlReader.setExtensionRegistry(new ExtendedWSDLRegistry());
//            wsdlReader.setFeature(com.ibm.wsdl.Constants.FEATURE_VERBOSE, false);
//            wsdlReader.setFeature(com.ibm.wsdl.Constants.FEATURE_IMPORT_DOCUMENTS, true);
//            Definition definition = wsdlReader.readWSDL(baseUri, new InputSource(new StringReader(wsdl)));
//            WSDLParser parser = new WSDLParser(definition, null);
//            Iterator serviceIterator = parser.getAllServices().values().iterator();
//
//            // Only using one service per stub in the new model, hence taking first element
//            WSDLServiceInfo serviceInfo = (WSDLServiceInfo) serviceIterator.next();
//
//            return serviceInfo.getQName().getLocalPart();
//
//        } catch (Exception e) {
//            throw new FioranoException(e);
//        }
//    }
//
//    private void initializeManagableProps() {
//        webInstanceManagableProps.put(CONTEXT_NAME_MANAGEABLE_PROP, CONTEXT_NAME);
//        webInstanceManagableProps.put(FES_AUTH_USER_MANAGEABLE_PROP, FES_AUTH_USER);
//        webInstanceManagableProps.put(FES_AUTH_PASSWD_MANAGEABLE_PROP, FES_AUTH_PASSWD);
//    }
//
//}
