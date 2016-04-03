package com.fiorano.openesb.route;

public abstract class RouteOperationConfiguration {
    RouteOperationType operationType;

    public RouteOperationType getRouteOperationType() {
        return operationType;
    }

    public void setRouteOperationType(RouteOperationType operationType) {
        this.operationType = operationType;
    }
}
