package com.vivid.framework.security.controller;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.security.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

@RestController
public class AuthController {
    @GetMapping("/oauth/token_key")
    public ResponseResult tokenKey()
    {
        String keys = null;
        try {
            keys = JwtTokenUtils.getPublicKeyString();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed(null,e.toString());
        }
        return ResponseResult.successed(keys);
    }
    @GetMapping("/oauth/extract_token")
    public ResponseResult tokenKey(String token)
    {
        String tkMap = null;
        try {
            tkMap = JwtTokenUtils.extractToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failed(null,e.getMessage());
        }
        return ResponseResult.successed(tkMap);
    }
}
