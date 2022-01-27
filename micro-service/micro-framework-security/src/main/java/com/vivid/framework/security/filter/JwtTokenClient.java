package com.vivid.framework.security.filter;

import org.springframework.web.client.RestTemplate;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenClient {
    private static String publicKey;

    public static String getPublicKey() {
        if (publicKey == null) {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> result = new HashMap<>();
            result = restTemplate.getForObject("http://localhost:8880/oauth/token_key", result.getClass());
            System.out.println(result);
            if (result != null && result.get("data") != null ) {
                publicKey = result.get("data");
            }
        }
        return publicKey;
    }
}
