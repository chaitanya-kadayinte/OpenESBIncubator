package com.fiorano.openesb.utils.crypto;

public interface CommonConstants {

    String AES_ENCRYPTION_SCHEME = "AES";
    String DES_ENCRYPTION_SCHEME = "DES";
    String DESEDE_ENCRYPTION_SCHEME = "DESede";
    String DEFAULT_ENCRYPTION_KEY = "DEFAULTFIORANOENCRYPTIONKE";
    String MD5 = "MD5";
    String UNICODE_FORMAT = "UTF8";
    String JCE_KEYSTORE = "JCEKS";
    String DEFAULT_ALIAS = "admin";
    String DEFAULT_STOREPASS = "passwd";

    //config name
    String KEY_STORE_CONFIG = "keystoreConfig";
    //resource type
    String KEY_STORE_RESOURCE_TYPE = "Keystore";
    //encryption key name - used in setting encryption key as a sytem property in case of external cps launch 
    String AES_ENCRYPTION_KEY = "AES_ENCRYPTION_KEY";

    String MSG_ENCRYPTION_CONFIG = "MessageEncryptionConfiguration";
    String MSG_ENCRYPTION_CONFIG_TYPE = "com.fiorano.services.common.security.MessageEncryptionConfiguration";
}
