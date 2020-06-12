package com.slib.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Vincent.Lei on 2016/12/27.
 * Title :
 * Note : 如果是Activity或者Fragment请在getPageTitle()中返回页面标题,用于页面统计
 */

public class AESEncryptorUtil {
    private final static String HEX = "0123456789ABCDEF";

    /**
     * AES加密
     */
    public static String encrypt(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] result = encrypt(rawKey, cleartext.getBytes());
        return toHex(result);
    }

    /**
     * AES解密
     */
    public static String decrypt(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decrypt(rawKey, enc);
        return new String(result);
    }

//    private static byte[] getRawKey(byte[] seed) throws Exception {
//        KeyGenerator kgen = KeyGenerator.getInstance("AES");
//        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", new CryptoProvider());
//        sr.setSeed(seed);
//        kgen.init(128, sr); // 192 and 256 bits may not be available
//        SecretKey skey = kgen.generateKey();
//        return skey.getEncoded();
//    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        try {
            KeyGenerator kGen = KeyGenerator.getInstance("AES");
            SecureRandom sr;
            sr = new SecureRandom();
            sr.setSeed(seed);
            kGen.init(128, sr);
            SecretKey sKey = kGen.generateKey();
            return sKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(clear);
    }

    private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(encrypted);
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }


    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    /**
     * Implementation of Provider for SecureRandom. The implementation     supports the
     * "SHA1PRNG" algorithm described in JavaTM Cryptography Architecture, API
     * Specification & Reference
     */
//    public static class CryptoProvider extends Provider {
//        /**
//         * Creates a Provider and puts parameters
//         */
//        public CryptoProvider() {
//            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
//            put("SecureRandom.SHA1PRNG",
//                    "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
//            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
//        }
//    }
}
