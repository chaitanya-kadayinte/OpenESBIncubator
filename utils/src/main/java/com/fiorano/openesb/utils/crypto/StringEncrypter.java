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

import org.apache.commons.lang3.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Hari
 * Date: 15 Dec, 2012
 * Time: 6:16:15 PM
 * To change this template use File | Settings | File Templates.
 */

/* factory class to instanstiate Encryter classes.*/
public class StringEncrypter {

    private static ICustomEncryptor defaultInstance;
    private static String encryptionKey = CommonConstants.DEFAULT_ENCRYPTION_KEY;
    private static String oldkey = CommonConstants.DEFAULT_ENCRYPTION_KEY;
    private static String newKey = CommonConstants.DEFAULT_ENCRYPTION_KEY;

    public static void setEncryptionKey(String encryptionKey) {
        oldkey = StringEncrypter.encryptionKey;
        StringEncrypter.encryptionKey = encryptionKey;
        newKey =  encryptionKey;
    }

    public static void resetEncryptionKey(){
        oldkey = StringEncrypter.encryptionKey;
        StringEncrypter.encryptionKey = CommonConstants.DEFAULT_ENCRYPTION_KEY;
        newKey = CommonConstants.DEFAULT_ENCRYPTION_KEY;
    }

    public static ICustomEncryptor getDefaultInstance() throws StringEncrypter.EncryptionException {
            if (defaultInstance == null || !oldkey.equals(newKey))
                defaultInstance = new CustomEncryptorDefaultImpl( CommonConstants.AES_ENCRYPTION_SCHEME,encryptionKey);
            return defaultInstance;
    }

    public static ICustomEncryptor getDefaultInstance(String customClass) throws StringEncrypter.EncryptionException {
        try {

            if(!StringUtils.isEmpty(customClass)){
                return (ICustomEncryptor)Thread.currentThread().getContextClassLoader().loadClass(customClass).newInstance();
            }
            else {
                return getDefaultInstance();
            }
        }catch(ClassNotFoundException ex){
            throw new StringEncrypter.EncryptionException(ex);
        } catch (InstantiationException e) {
            throw new StringEncrypter.EncryptionException(e);
        } catch (IllegalAccessException e) {
            throw new StringEncrypter.EncryptionException(e);
        }
    }

    /**
     * wrapper exception class
     */
    public static class EncryptionException extends Exception {
        public EncryptionException(Throwable t) {
            super(t);
        }
    }

}
