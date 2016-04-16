package com.fiorano.openesb.utils.logging.api;

import org.apache.log4j.Level;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.LevelRangeFilter;

public class ESBErrorLogFilter extends LevelRangeFilter {

    private Level minLevel;
    private Level maxLevel;

    public ESBErrorLogFilter(Level min, Level max){
        this.setLevelMin(min);
        this.setLevelMax(max);
        minLevel = min;
        maxLevel = max;
        this.activateOptions();
    }

    public int decide(LoggingEvent event){
        if(event.getThrowableInformation()!=null || withinRange(event.getLevel())){
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
