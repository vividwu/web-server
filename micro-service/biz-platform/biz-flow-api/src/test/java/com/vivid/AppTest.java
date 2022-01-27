package com.vivid;

import static org.junit.Assert.assertTrue;

import io.jsonwebtoken.*;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.util.Assert;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Unit test for simple FlowApp.
 */
public class AppTest {
    @Test
    public void decode(){
        System.out.println(URLEncoder.encode("w.6"));
        System.out.println(URLEncoder.encode("a123123&1xxDC"));
        System.out.println(URLEncoder.encode("!@#$?+-"));
        System.out.println(java.net.URLDecoder.decode("w.6"));
        System.out.println(java.net.URLDecoder.decode("%21%40%23%24%3F%2B-"));
    }
    @Test
    public void printKeyTest() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        Key publicKey = keyPair.getPublic();
        Key privateKey = keyPair.getPrivate();
        System.out.println((new BASE64Encoder()).encode(privateKey.getEncoded()));
        System.out.println((new BASE64Encoder()).encode(publicKey.getEncoded()));
    }

    public Key str2privateKey() throws Exception {
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALUOUYKwCBqAnywJLPtCOFDnIAK5ObjNis0auw3/fFadrWaIAJOqWzksq1GpVXRhIusgb4JumkueIXu/Ba2EsWU0KUU5X14ubX3x76kVS34oa4X92lDuwwTR1TSflKpOGqzD9v+Ho7gOA4tKLgpfVtCbFbfliL4k0FdY3LKrqga/AgMBAAECgYAMnqrfyRVHg5+Umi8gdEUonnOEvRRg5TK/iRVwrFYmU2c6tlAT+tPQ8GQglQT3z613ZhNfrAXlL2h2D6J9rboPjceJ0RZ+wQrokpOCROP41uMQPFXadcwEq+9OOueHfTGXHqfAn1wc997da+1fgdSBaW8WzMMynqCZisW8v7TVgQJBANmkgy4Gv0oyVD6L3pBXe+SAV9dqO+egiEB+38VQulrZUlZ3aFgW\n" +
                "Y+bsZseKqQ8Q1Z9ibRpEno2fuMt9fo1g6JkCQQDU9xs6Z2y0KnvjIg/9NLJNHXFvLOYyQqDitidy\n" +
                "YH8nM6lSNo1fQjbJnyPnzjpaXDcs/UEpwMumQL/9DEnIHckXAkB/VyMtpeL8fZAO/HVfTOB/ZJyA\n" +
                "vHaKYsH1woYZA8/VYfwr/Td++rK/JShJrhaWawoidEtTqDyArqhH34hRHhdZAkEAhPR8oDq/h79a\n" +
                "NtQmLUs/4Zr3HZRnZotoTAGyoNqTp5K+K+B45Da/Y6Kh1O9QSOd3XvQBQaLmwTV9ZurMCU5nIwJA\n" +
                "LR02NtWeTsvyUkIRWLisu5qmvArWBNBt5TLkVodSslGcug36q0J/ZqtE0i44q8+r5xFxH0D6kCZS\n" +
                "A+vYavYDHQ==";
        //privateKey="eyJ1c2VyX25hbWUiOiJ3dXdlaTJfbSIsImRpc3BsYXlOYW1lIjoi5ZC05LyfIiwic2NvcGUiOlsicmVhZCJdLCJleHAiOjE1NDM4MDYzMjAsImp0aSI6ImE0MWY0NWYwLTIyYTctNGYwZS1hN2Q1LTVlNDAzZWI1NjViZiIsImNsaWVudF9pZCI6IlNhbXBsZUNsaWVudElkIiwidXNlcm5hbWUiOiJ3dXdlaTJfbUBjeW91LWluYy5jb20ifQ";
        byte[] keyBytes;
        keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    @Test
    public void EncodeTest() throws Exception {
        Key key = str2privateKey();
        String s = encode("test", "subject", 3600, null, key);
        System.out.println(s);
    }

    public static String encode(String account, String subject, long expired, Map<String, Object> data, Key privateKey) {
        try {
            Map<String, Object> HEADER = new HashMap<>();
            HEADER.put("type", "JWT");
            HEADER.put("sign", "RS256");
            Long nowMillis = System.currentTimeMillis();
            Date createTime = new Date(nowMillis);
            Date expiredTime = new Date(nowMillis + expired);
            JwtBuilder builder = Jwts.builder();
            builder.setHeader(HEADER).setId(UUID.randomUUID().toString())
                    .setIssuer("颁发者").setAudience(account)
                    .setSubject(subject).setIssuedAt(createTime)
                    .setNotBefore(createTime).setExpiration(expiredTime)
                    .signWith(SignatureAlgorithm.HS256, privateKey);
            builder.claim("data", data);
            return builder.compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public PrivateKey getPrivateKey() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jwt.jks");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, "keypass".toCharArray());
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("jwt", "keypass".toCharArray());
        System.out.println(privateKey);
        return privateKey;
    }

    private PublicKey getPublicKey() throws KeyStoreException,
            IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        ClassPathResource resource = new ClassPathResource("jwt.jks");
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(resource.getInputStream(), "keypass".toCharArray());
        PublicKey publicKey = keyStore.getCertificate("jwt").getPublicKey();
        return publicKey;
    }

    private PublicKey getPublicKey2() throws KeyStoreException,
            IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        PublicKey publicKey = CertificateFactory.getInstance("X.509")
                .generateCertificate(new ClassPathResource("jwt.jks").getInputStream())
                .getPublicKey();
        return publicKey;
    }

    @Test
    public void testPrivateKey() throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "vivid");
        tokenMap.put("num", 456);
        String token = Jwts.builder().signWith(SignatureAlgorithm.RS256, getPrivateKey())
                .setClaims(tokenMap)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)).compact();
        System.out.println(token);
        //Assert.state(privateKey instanceof RSAPrivateKey, "KeyPair must be an RSA ");

        PublicKey publicKey = getPublicKey();
        String verifierKey = "-----BEGIN PUBLIC KEY-----\n" + new String(Base64.getEncoder().encode(publicKey.getEncoded())) + "\n-----END PUBLIC KEY-----";
        //String verifierKey = new String(Base64.getEncoder().encode(publicKey.getEncoded()));
        System.out.println(verifierKey);

        Claims body = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody();
        System.out.println(body);
        org.springframework.security.jwt.Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(verifierKey));
        String claims = jwt.getClaims();
        System.out.println(claims);
        //getTokenFromPublicString(verifierKey, token);
    }
    @Test
    public void publicStrTest() throws CertificateException {
        getTokenFromPublicString("","");
    }
    private void getTokenFromPublicString(String publicKeyStr, String token) throws CertificateException {
        PublicKey publicKey = CertificateFactory.getInstance("X.509")
                .generateCertificate(
                new ByteArrayInputStream(Base64.getDecoder().decode(publicKeyStr.getBytes())))
                .getPublicKey();
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(publicKey)
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println(claims.get("name"));
    }

    @Test
    public void publicKeyStringTest() {
        ClassPathResource resource = new ClassPathResource("jwt.jks");
        PublicKey publicKey = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Charset.forName("utf-8")));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            System.out.println(buffer);
            //System.out.println(Base64.getDecoder().decode(buffer.toString()));
            publicKey = null;//RSAPublicKeyImpl.newKey(Base64.getDecoder().decode(buffer.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
