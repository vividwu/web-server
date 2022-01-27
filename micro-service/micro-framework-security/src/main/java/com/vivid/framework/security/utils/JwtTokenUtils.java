package com.vivid.framework.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class JwtTokenUtils {
    private static String verifierKey;
    private static PrivateKey privateKey;

    private static PublicKey getPublicKey() throws KeyStoreException,
            IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        ClassPathResource resource = new ClassPathResource("jwt.jks");  //该文件需要手动拷贝到编译后的目录下，Maven打包会修改内容导致不能解析
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(resource.getInputStream(), "keypass".toCharArray());
        PublicKey publicKey = keyStore.getCertificate("jwt").getPublicKey();
        return publicKey;
    }
    private static PrivateKey getPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jwt.jks");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, "keypass".toCharArray());
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("jwt", "keypass".toCharArray());
        return privateKey;
    }
    public static String getPublicKeyString() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        if (JwtTokenUtils.verifierKey == null) {
            PublicKey publicKey = JwtTokenUtils.getPublicKey();
            String verifierKey = "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.getEncoder().encode(publicKey.getEncoded())) + "\n-----END PUBLIC KEY-----";
            return verifierKey;
        } else {
            return verifierKey;
        }
    }
    public static String createToken(Map<String,Object> tokenMap) {
        try {
            if (privateKey == null) {
                privateKey = JwtTokenUtils.getPrivateKey();
            }
            //生成jwt令牌
            Jwt jwt = JwtHelper.encode(new ObjectMapper().writeValueAsString(tokenMap), new RsaSigner((RSAPrivateKey)privateKey));
            //取出jwt令牌
            String token = jwt.getEncoded();
            return token;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static String extractToken(String tokenStr) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
            if (privateKey == null) {
                privateKey = JwtTokenUtils.getPrivateKey();
            }
            //生成jwt令牌
            Jwt jwt = JwtHelper.decode(tokenStr);
            //取出jwt令牌
            String token = jwt.getClaims();
            return token;
    }
}
