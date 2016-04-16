package com.fiorano.openesb.route;

public interface RouteService<RC extends RouteConfiguration> {
    Route createRoute(RC routeConfiguration) throws Exception;
}
