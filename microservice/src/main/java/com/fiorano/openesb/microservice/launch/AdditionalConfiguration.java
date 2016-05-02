package com.fiorano.openesb.microservice.launch;

public interface AdditionalConfiguration {
    public String getSchemaRepoPath() ;

    public String getJettyUrl();

    public String getJettySSLUrl();

    public String getCompRepoPath();

    public String getProviderUrl();
}
