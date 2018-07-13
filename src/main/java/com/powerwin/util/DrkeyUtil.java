package com.powerwin.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;

public class DrkeyUtil {

    public static final int[] INDEXS = { 5, 0, 7, 2, 6, 1, 4, 3 };
    public static final int[] MASKS = { 0, 0, 0, 0, 0, 0, 0, 0 };
    public static final int[] bytes = new int[8];

    private final static String DES = "DES";

    public static void main(String[] args) {

        try {
            String data = "123456";
            String key = "%s72*fk&";
            System.err.println(encrypt(data, key));
            System.err.println(decrypt(encrypt(data, key), key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 0000D70712000044
        System.out.println("加密后：" + encode(1860, 4823));
        long test = decode(encode(1860, 4823));
        int uids = (int) (test >> 32 & (long) -1);
        int appids = (int) (test & (long) -1);
        System.out.println("解密后：" + uids + ";" + appids);

        String drkey = "0000FE100F0000030000C8110800000B04:F7:E4:12:FF:58";
        System.out.println("hashcode:" + drkey.hashCode());
        String appkey = drkey.substring(0, 16);
        String adskey = drkey.substring(16, 32);
        String macIdfa = drkey.substring(32, drkey.length());
        long appskey = decode(appkey);
        long adsskey = decode(adskey);
        int uid = (int) (appskey >> 32 & (long) -1);
        int appid = (int) (appskey & (long) -1);
        int cid = (int) (adsskey >> 32 & (long) -1);
        int adid = (int) (adsskey & (long) -1);
        String s = macIdfa.contains(":") ? "mac" + "->" + macIdfa : "idfa" + "->" + macIdfa;
        System.out.println("解密后:uid->" + uid + ";appid->" + appid + ";cid->" + cid + ";adid->" + adid + ";" + s);
    }

    private static byte[] int2bytes(int v1, int v2) {

        byte[] buf = new byte[8];
        for (int i = 0; i < 4; i++) {
            buf[3 - i] = (byte) ((v1 >> (i * 8)) & 0xff);
        }
        for (int i = 0; i < 4; i++) {
            buf[7 - i] = (byte) ((v2 >> (i * 8)) & 0xff);
        }
        return buf;
    }

    private static byte[] bytes_encode(byte[] buf) {

        if (buf.length != 8)
            return null;

        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            int f = INDEXS[i];
            result[i] = (byte) ((buf[f]) ^ (MASKS[i]));
            bytes[i] = (buf[f]) ^ (MASKS[i]);
        }

        return result;
    }

    // 加密
    public static String encode(int uid, int appid) {
        byte[] buf = int2bytes(uid, appid);
        byte[] bytes = bytes_encode(buf);
        return String.format("%02X%02X%02X%02X%02X%02X%02X%02X", bytes[0], bytes[1], bytes[2], bytes[3], bytes[4],
                bytes[5], bytes[6], bytes[7]);
    }

    // 解码
    public static long decode(String encstr) {
        int[] bytes = LongCodec.hex2bytes(encstr);
        int[] bytes2 = LongCodec.decode(bytes);
        return LongCodec.bytes2long(bytes2);
    }

    public static class LongCodec {
        public static int[] hex2bytes(String val) {
            int[] array = new int[8];
            if (val.length() != 16) {
                return null;
            }
            for (int i = 0; i < 16; i += 2) {
                String s = val.substring(i, i + 2);
                array[i / 2] = Integer.parseInt(s, 16);
            }
            return array;
        }

        public static long bytes2long(int[] bytes) {
            long num = 0L;
            for (int i = 0; i < 8; i++) {
                num |= (long) bytes[7 - i] << i * 8;
            }
            return num;
        }

        public static int[] decode(int[] bytes) {
            if (bytes.length != 8) {
                return null;
            }
            int[] array = new int[8];
            for (int i = 0; i < 8; i++) {
                array[INDEXS[i]] = (int) (bytes[i] ^ MASKS[i]);
            }
            return array;
        }
    }

    /**
    * DES加密：根据键值进行加密
    * @param data 加密数据字符串
    * @param key  密钥key字符串
    * @return
    * @throws Exception
    */
    public static String encrypt(String data, String key) throws Exception {
        byte[] bt = encrypt(data.getBytes(), key.getBytes());
        String strs = new BASE64Encoder().encode(bt);
        return strs;
    }

    /**
     * DES解密：根据键值进行解密
     * @param data 加密数据字符串
     * @param key  密钥key字符串
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key) throws IOException, Exception {
        if (data == null)
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] buf = decoder.decodeBuffer(data);
        byte[] bt = decrypt(buf, key.getBytes());
        return new String(bt);
    }

    /**
     * DES加密：根据键值进行加密
     * @param data 加密值byte数组
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    /**
     * DES解密：根据键值进行解密
     * @param data 加密值byte数组
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }
}
