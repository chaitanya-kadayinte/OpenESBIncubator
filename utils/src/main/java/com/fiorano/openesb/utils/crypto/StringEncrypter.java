package com.fiorano.openesb.utils.crypto;

import org.apache.commons.lang3.StringUtils;

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
