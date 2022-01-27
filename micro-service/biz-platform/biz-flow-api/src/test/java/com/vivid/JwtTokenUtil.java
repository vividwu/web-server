package com.vivid;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.catalina.security.SecurityUtil;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import sun.security.rsa.RSAPublicKeyImpl;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String USERNAME = Claims.SUBJECT;
    private static final String CREATED = "created";

    private static final String AUTHORITIES = "authorities";

    private static final String SECRET = "MyJwtSecret";
    private static final PublicKey PUBLIC_KEY = getPublicKey();
    private static final PrivateKey PRIVATE_KEY = getPrivateKey();

    public static final long EXPIRE_TIME_REFRESH = 12 * 60 * 60 * 1000;

    public static final long EXPIRE_TIME_ACCESS = 60 * 30 * 1000;

    public static final String KEYSTORE_LOCATION = "keystore/xc.keystore";

    public static final String PUBLICKEY_LOCATION = "keystore/public.key";

    public static final String KEYSTORE_PASSWORD = "xuechengkeystore";

    public static final String KEY_PASSWORD = "xuecheng";

    public static final String KEYSTORE_ALIAS = "xckey";


    private static PublicKey getPublicKey() {
        ClassPathResource resource = new ClassPathResource(PUBLICKEY_LOCATION);
        PublicKey publicKey = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Charset.forName("utf-8")));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            publicKey = null;//RSAPublicKeyImpl.newKey(Base64.getDecoder().decode(buffer.toString()));

        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return publicKey;
    }

    private static PrivateKey getPrivateKey() {
        PrivateKey privateKey = null;
        try {
            ClassPathResource resource = new ClassPathResource(KEYSTORE_LOCATION);
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(resource.getInputStream(), KEYSTORE_PASSWORD.toCharArray());
            privateKey = (PrivateKey) ks.getKey(KEYSTORE_ALIAS, KEY_PASSWORD.toCharArray());
        } catch (IOException | CertificateException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            e.printStackTrace();
        }

        return privateKey;

    }


    @Test
    public void test01() {


        HashMap<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "guohao");
        tokenMap.put("roles", "r01,r02");
        tokenMap.put("ext", "1");
        String token = Jwts.builder().signWith(SignatureAlgorithm.RS256, PRIVATE_KEY).setClaims(tokenMap).setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000)).compact();

        System.out.println(token);

//            PublicKey publicKey = RSAPublicKeyImpl.newKey(Base64.getDecoder().decode(PUBLIC_KEY));

        Claims body = Jwts.parser().setSigningKey(PUBLIC_KEY).parseClaimsJws(token).getBody();
        System.out.println(body);

    }


    /**
     * 生成refreshToken令牌
     *
     * @param
     * @return 令牌
     */
    public static String generateToken(Authentication authentication, long expiredTimeOffset) {
        Map<String, Object> claims = new HashMap<>(3);
        claims.put(USERNAME, authentication.getPrincipal().toString());//SecurityUtil.getUsername(authentication));
        claims.put(CREATED, new Date());
        claims.put(AUTHORITIES, authentication.getAuthorities());
        return generateToken(claims, expiredTimeOffset);
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    public static String generateToken(Map<String, Object> claims, long expiredTimeOffset) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiredTimeOffset);
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.RS256, PRIVATE_KEY).compact();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public static String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 根据请求令牌获取登录认证信息
     *
     * @param
     * @return 用户名
     */
    public static Authentication getAuthenticationeFromToken(HttpServletRequest request) {
        Authentication authentication = null;
        // 获取请求携带的令牌
        String token = JwtTokenUtil.getToken(request);
        if (token != null) {
            // 请求令牌不能为空
            //if (SecurityUtil.getAuthentication() == null) {
                // 上下文中Authentication为空
                Claims claims = getClaimsFromToken(token);
                if (claims == null) {
                    return null;
                }
                String username = claims.getSubject();
                if (username == null) {
                    return null;
                }
                if (isTokenExpired(token)) {
                    return null;
                }
                Object authors = claims.get(AUTHORITIES);
                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                if (authors != null && authors instanceof List) {
                    for (Object object : (List) authors) {
                        authorities.add(new SimpleGrantedAuthority((String) ((Map) object).get("authority")));
                    }
                }
                authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
//            } else {
//                if (validateToken(token, SecurityUtil.getUsername())) {
//                    // 如果上下文中Authentication非空，且请求令牌合法，直接返回当前登录认证信息
//                    authentication = SecurityUtil.getAuthentication();
//                }
//            }
        }
        return authentication;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private static Claims getClaimsFromToken(String token) {
        return  Jwts.parser().setSigningKey(PUBLIC_KEY).parseClaimsJws(token).getBody();
    }

    /**
     * 验证令牌
     *
     * @param token
     * @param username
     * @return
     */
    public static Boolean validateToken(String token, String username) {
        String userName = getUsernameFromToken(token);
        return (userName.equals(username) && !isTokenExpired(token));
    }

    /**
     * 刷新令牌
     *
     * @param token
     * @return
     */
    public static String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(CREATED, new Date());
            refreshedToken = generateToken(claims, EXPIRE_TIME_REFRESH);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public static Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String tokenHead = "Bearer ";
        if (token == null) {
            token = request.getHeader("token");
        } else if (token.contains(tokenHead)) {
            token = token.substring(tokenHead.length());
        }
        if ("".equals(token)) {
            token = null;
        }
        return token;
    }
}
