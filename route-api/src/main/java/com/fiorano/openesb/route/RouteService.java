package com.fiorano.openesb.route;

public interface RouteService<RC extends RouteConfiguration> {
    Route createRoute(String routeName, RC routeConfiguration) throws Exception;
}
