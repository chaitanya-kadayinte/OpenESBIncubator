/**
 * Copyright (c) 1999-2007, Fiorano Software Technologies Pvt. Ltd. and affiliates.
 * Copyright (c) 2008-2015, Fiorano Software Pte. Ltd. and affiliates.
 *
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Fiorano Software ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * enclosed with this product or entered into with Fiorano.
 */

package com.fiorano.openesb.utils.crypto;

/**
 * Created by IntelliJ IDEA.
 * User: Lokesh
 * Date: 15 Jun, 2011
 * Time: 6:16:15 PM
 * To change this template use File | Settings | File Templates.
 */

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
