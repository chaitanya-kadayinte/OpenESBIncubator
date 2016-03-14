package com.fiorano.openesb.route.impl;

import com.fiorano.openesb.route.Selector;
import com.fiorano.openesb.route.SelectorConfiguration;


public abstract class AbstractSelectorHandler<SC extends SelectorConfiguration> implements Selector {
    protected SC selectorConfiguration;
    public AbstractSelectorHandler(SC selectorConfiguration) {
        this.selectorConfiguration = selectorConfiguration;
    }
}
