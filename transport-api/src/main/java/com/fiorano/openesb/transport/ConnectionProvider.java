package com.fiorano.openesb.transport;

public interface ConnectionProvider<C,CC extends ConnectionConfiguration> {
    void prepareConnectionMD(CC cc) throws Exception;
    void releaseConnectionMD(CC cc);
    C createConnection(CC cc) throws Exception;

}
