
package com.fiorano.openesb.application.configuration.data;

import java.io.Serializable;

public enum DestinationType implements Serializable {
    /**
     * Denotes Topic destination Type
     */
    TOPIC,
    /**
     * Denote Queue destination Type
     */
    QUEUE
}
