package com.vivid.biz.flow.utils;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
//https://blog.csdn.net/rainyear/article/details/84761288
public class RsaKeyUtil {
    public static final String KEY_ALGORITHM = "RSA";
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final String SIGNATURE_ALGORITHM="MD5withRSA";
    public static final String SHA256="SHA256withRSA";

    public static Map<String, Object> createKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    //获得公钥字符串
    public static String getPublicKeyStr(Map<String, Object> keyMap) throws Exception {
//        //获得map中的公钥对象 转为key对象
//        Key key = (Key) keyMap.get(PUBLIC_KEY);
//        //编码返回字符串
//        return bytesToHex(encryptBASE64(key.getEncoded()).getBytes());
        return new String(Base64.getEncoder().encode(((PublicKey)keyMap.get(PUBLIC_KEY)).getEncoded()));

    }

    //获得私钥字符串
    public static String getPrivateKeyStr(Map<String, Object> keyMap) throws Exception {
//        //获得map中的私钥对象 转为key对象
////        Key key = (Key) keyMap.get(PRIVATE_KEY);
////        //编码返回字符串
////        return bytesToHex(encryptBASE64(key.getEncoded()).getBytes());
        return new String(Base64.getEncoder().encode(((PrivateKey)keyMap.get(PRIVATE_KEY)).getEncoded()));
    }

    //编码返回字符串
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
     public static byte[] decrypt(String privateStr, byte[] bt_encrypted)throws Exception {
         PrivateKey privateKey = getPrivateKey(privateStr);
         Cipher cipher = Cipher.getInstance("RSA");
         cipher.init(Cipher.DECRYPT_MODE, privateKey);
         byte[] bt_original = cipher.doFinal(bt_encrypted);
         return bt_original;
     }
    //************************加密解密**************************
    public static byte[] encrypt(String publicKeyStr,byte[] bt_plaintext)throws Exception {
        PublicKey publicKey = getPublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bt_encrypted = cipher.doFinal(bt_plaintext);
        return bt_encrypted;
    }

     public static byte[] sign(String privateKeyStr,byte[] data) throws Exception {
         PrivateKey priK = getPrivateKey(privateKeyStr);
         Signature sig = Signature.getInstance(SHA256);//SIGNATURE_ALGORITHM);
         sig.initSign(priK);
         sig.update(data);
         return sig.sign();
     }

   public static boolean verify(String publicKeyStr,byte[] data,byte[] sign) throws Exception{
      PublicKey pubK = getPublicKey(publicKeyStr);
      Signature sig = Signature.getInstance(SHA256);
      sig.initVerify(pubK);
      sig.update(data);
      return sig.verify(sign);
  }
    public static void main(String[] args) throws Exception {
        Map<String, Object> keyMap = RsaKeyUtil.createKey();
        String privateKey = getPrivateKeyStr(keyMap);
        //privateKey = formatPkcs8ToPkcs1(privateKey);
        System.out.println("私钥");
        System.out.println(privateKey);

        String publicKey = getPublicKeyStr(keyMap);
        System.out.println("公钥");
        System.out.println(publicKey);

        String raw = "{\"aid\":\"123\",\"uid\":\"001\",\"udisplay\":\"vivid\",\"exp\":1624448581}";
        System.out.println("base64原文："+Base64.getEncoder().encodeToString(raw.getBytes()));
//        byte[] encrypt = encrypt(publicKey, raw.getBytes());
//        System.out.println("加密后："+Base64.getEncoder().encodeToString(encrypt));
//
//        byte[] bt_original = decrypt(privateKey,encrypt);
//        String str_original = new String(bt_original);
//        System.out.println("解密结果:"+str_original);

        byte[] signature=sign(privateKey, raw.getBytes());
        System.out.println("产生签名："+Base64.getEncoder().encodeToString(signature));
        boolean status=verify(publicKey, raw.getBytes(), signature);
        System.out.println("验证结果："+status);
    }
    //format PKCS#8 to PKCS#1
    public static String formatPkcs8ToPkcs1(String rawKey) throws Exception {
        String result = null;
        //extract valid key content
        String validKey = rawKey;//RsaPemUtil.extractFromPem(rawKey); // pem文件多行合并为一行
        if (StringUtils.isNotEmpty(validKey))
        {
            //将BASE64编码的私钥字符串进行解码
            byte[] encodeByte = Base64.getDecoder().decode(validKey);

            //==========
            //pkcs8Bytes contains PKCS#8 DER-encoded key as a byte[]
            PrivateKeyInfo pki = PrivateKeyInfo.getInstance(encodeByte);
            RSAPrivateKeyStructure pkcs1Key = RSAPrivateKeyStructure.getInstance(pki.getPrivateKey());
            byte[] pkcs1Bytes = pkcs1Key.getEncoded();//etc.
            //==========

            String type = "RSA PRIVATE KEY";
            result = format2PemString(type, pkcs1Bytes);
        }
        return result;
    }
    // Write to pem file
    // 字符串换行显示
    private static String format2PemString(String type, byte[] privateKeyPKCS1) throws Exception {
        PemObject pemObject = new PemObject(type, privateKeyPKCS1);
        StringWriter stringWriter = new StringWriter();
        PemWriter pemWriter = new PemWriter(stringWriter);
        pemWriter.writeObject(pemObject);
        pemWriter.close();
        String pemString = stringWriter.toString();
        return pemString;
    }
}