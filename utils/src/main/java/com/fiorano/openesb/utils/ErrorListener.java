package com.fiorano.openesb.utils;

public interface ErrorListener {
    public void warning(Exception exception) throws Exception;
    public void error(Exception exception) throws Exception;
    public void fatalError(Exception exception) throws Exception;
}