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
package com.fiorano.openesb.utils.logging.api;

import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.LevelRangeFilter;

/**
 * Created by IntelliJ IDEA.
 * User: amit
 * Date: Mar 5, 2009
 * Time: 3:23:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ESBOutLogFilter extends LevelRangeFilter {

    private Level minLevel;
    private Level maxLevel;

    public ESBOutLogFilter(Level min, Level max){
        this.setLevelMin(min);
        this.setLevelMax(max);
        minLevel = min;
        maxLevel = max;
        this.activateOptions();
    }

    public int decide(LoggingEvent event){
        if(event.getThrowableInformation()==null && withinRange(event.getLevel())){
            return Filter.ACCEPT;
        }

        return Filter.DENY;

    }

    private boolean withinRange(Level level){

        boolean withinLevel = false;

        if(level.isGreaterOrEqual(maxLevel) && !(level.isGreaterOrEqual(minLevel) && !(level.toInt() == minLevel.toInt())))
                withinLevel = true;

        return withinLevel;
    }

}
