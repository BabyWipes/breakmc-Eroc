package com.breakmc.eroc.utils;

public enum TimeUnit
{
    MILLISECOND(1L), 
    SECOND(1000L), 
    MINUTE(60000L), 
    HOUR(3600000L);
    
    private long time;
    
    private TimeUnit(final long time) {
        this.time = time;
    }
    
    public long getTime() {
        return this.time;
    }
}
