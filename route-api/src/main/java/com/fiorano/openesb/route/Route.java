package com.fiorano.openesb.route;

import com.fiorano.openesb.transport.Message;
import com.fiorano.openesb.transport.PortConfiguration;

public interface Route<M extends Message> {

    void start() throws Exception;
    void stop() throws Exception;
    void delete();
    void changeTargetDestination(PortConfiguration portConfiguration) throws Exception;
    void changeSourceDestination(PortConfiguration portConfiguration) throws Exception;
    void handleMessage(M message) throws Exception;
    String getSourceDestinationName();
    String getTargetDestinationName();
    void modifyHandler(RouteOperationConfiguration configuration) throws Exception;
}
