package com.fiorano.openesb.utils.config;

import com.fiorano.openesb.utils.FioranoStaxParser;
import com.fiorano.openesb.utils.exception.FioranoException;

import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fiorano.openesb.utils.config.ServiceConfigurationParser.ConfigurationMarkups.*;

//todo replace this with loading the configuration with custom classloader with required dependencies.
public class ServiceConfigurationParser {

    private ServiceConfigurationParser() {
    }

    public Hashtable<String, Object> parseRESTStubConfiguration(String configuration) throws FioranoException {

        if (configuration == null)
            return null;
        Hashtable<String, Object> webInstanceData = new Hashtable<>();

        FioranoStaxParser parser = null;
        try {
            parser = new FioranoStaxParser(new StringReader(configuration));
            parser.markCursor("java");
            while (parser.nextElement()) {
                if (parser.getLocalName().equalsIgnoreCase("object")) {
                    String className = parser.getAttributeValue(null, "class");
                    parser.markCursor(parser.getLocalName());
                    if (className.equalsIgnoreCase(RESTSTUB_CONFIG_CLASSNAME)) {
                        while (parser.nextElement()) {
                            if (parser.getLocalName().equalsIgnoreCase("void")) {
                                String propertyName = parser.getAttributeValue(null, "property");
                                if (propertyName.equalsIgnoreCase(RESTFUL_SERVICE_NAME)) {
                                    parser.markCursor(parser.getLocalName());
                                    while (parser.nextElement()) {
                                        String value = parser.getText();
                                        webInstanceData.put(propertyName, value);
                                    }
                                    parser.resetCursor();
                                }
                                if (propertyName.equalsIgnoreCase(WADL_CONFIGURATION)) {
                                    parser.markCursor(parser.getLocalName());

                                    while (parser.nextElement()) {
                                        if (parser.getLocalName().equalsIgnoreCase("void")) {
                                            propertyName = parser.getAttributeValue(null, "property");
                                            if (propertyName.equalsIgnoreCase(WADL)) {
                                                parser.markCursor(parser.getLocalName());
                                                while (parser.nextElement()) {
                                                    String value = parser.getText();
                                                    webInstanceData.put(propertyName, value);
                                                }
                                                parser.resetCursor();
                                            } else if (propertyName.equalsIgnoreCase("grammars")) {
                                                Map<String, String> locations = new LinkedHashMap<>();
                                                if (parser.markCursor(parser.getLocalName())) {
                                                    while (parser.nextElement()) {
                                                        if (parser.getLocalName().equalsIgnoreCase("object")) {
                                                            String location = null;
                                                            String reference = null;
                                                            if (parser.markCursor("object")) {
                                                                while (parser.nextElement()) {
                                                                    if (parser.getLocalName().equalsIgnoreCase("void")) {
                                                                        propertyName = parser.getAttributeValue(null, "property");
                                                                        if ("location".equalsIgnoreCase(propertyName)) {
                                                                            parser.markCursor("string");
                                                                            location = parser.getText();
                                                                            parser.resetCursor();
                                                                        } else if ("reference".equalsIgnoreCase(propertyName)) {
                                                                            parser.markCursor("string");
                                                                            reference = parser.getText();
                                                                            parser.resetCursor();
                                                                        }
                                                                    }
                                                                }
                                                                locations.put(reference, location);
                                                                parser.resetCursor();
                                                                parser.resetCursor();
                                                                parser.resetCursor();
                                                            }
                                                        }
                                                        parser.resetCursor();
                                                    }
                                                    webInstanceData.put(SCHEMA_LOCATIONS, locations);
                                                } else {
                                                    parser.skipElement(parser.getLocalName());
                                                }
                                            }
                                        }
                                        parser.resetCursor();
                                    }
                                } else {
                                    parser.skipElement(parser.getLocalName());
                                }
                            }
                        }
                    }
                    parser.resetCursor();
                }
            }
        } catch (XMLStreamException e) {
            throw new FioranoException("");
        } finally {
            if (parser != null) {
                parser.resetCursor();
            }
        }
        return webInstanceData;
    }

    public static ServiceConfigurationParser INSTANCE() {
        return new ServiceConfigurationParser();
    }


    public static class ConfigurationMarkups {

        public static final String SCHEMA_LOCATIONS = "SchemaLocations";

        public static final String RESTSTUB_CONFIG_CLASSNAME = "com.fiorano.services.reststub.configuration.RESTStubConfiguration";

        public static final String WADL_CONFIGURATION = "wadlConfiguration";

        public static final String RESTFUL_SERVICE_NAME = "serviceName";

        public static final String WADL = "wadl";

    }
}
