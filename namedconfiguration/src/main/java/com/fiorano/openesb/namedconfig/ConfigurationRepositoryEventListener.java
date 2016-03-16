package com.fiorano.openesb.namedconfig;

import com.fiorano.openesb.application.configuration.data.NamedObject;

/**
 * Created by Janardhan on 3/15/2016.
 */
public interface ConfigurationRepositoryEventListener {

    /**
     * This API is invoked whenever a configuration is persisted into configuration repository
     * @param namedObject An object containing essential parameters which define the configuration persisted
     */
    public void configurationPersisted(NamedObject namedObject);

    /**
     * This API is invoked whenever a configuration is deleted from configuration repository
     * @param namedObject An object containing essential parameters which define the configuration deleted
     */
    public void configurationDeleted(NamedObject namedObject);
}
