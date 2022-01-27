package com.vivid.biz.flow.controller;

import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.dto.dict.RsaKeyDto;
import com.vivid.biz.flow.entity.code.CommonCodeEnt;
import com.vivid.biz.flow.service.CodeServiceImpl;
import com.vivid.biz.flow.utils.RsaKeyUtil;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.vivid.biz.flow.utils.RsaKeyUtil.getPublicKeyStr;

@RestController
@RequestMapping("/dict")
public class CodeDictController {
    @Autowired
    CodeServiceImpl codeService;

    //获取字典
    @RequestMapping(value = "/allcode_by_cate", method = {RequestMethod.GET})
    public ResponseResult<PageInfo<CommonCodeEnt>> getFlowHistoryByOrderNo(String category) {
        return ResponseResult.successed(codeService.getAllCodeByCategory(category));
    }
    //编辑字典
    @RequestMapping(value = "/update_code/{category}/{code}", method = {RequestMethod.POST})
    public ResponseResult updateCodeDict(@PathVariable("category") String category, @PathVariable("code") String code, @RequestBody CommonCodeEnt dto) {
        return ResponseResult.string(codeService.updateCodeDict(category,code, dto));
    }
    //新增字典项
    @RequestMapping(value = "/create_code", method = {RequestMethod.POST})
    public ResponseResult updateCodeDict(@RequestBody CommonCodeEnt dto) {
        return ResponseResult.string(codeService.createCodeDict(dto));
    }
    //生成对称密钥
    @RequestMapping(value = "/encrypt/create_key", method = {RequestMethod.GET})
    public ResponseResult createKey() {
        String error;
        try {
            Map<String, Object> keyMap = RsaKeyUtil.createKey();
            String privateKey = RsaKeyUtil.getPrivateKeyStr(keyMap);

            String publicKey = getPublicKeyStr(keyMap);
            Map<String,String> res = new HashMap<>();
            res.put(RsaKeyUtil.PRIVATE_KEY,privateKey);
            res.put(RsaKeyUtil.PUBLIC_KEY,publicKey);
            return ResponseResult.successed(res);
        }catch (Exception ex) {
            error = ex.toString();
        }
        return ResponseResult.failed(error);
    }
    //生成签名
    @RequestMapping(value = "/encrypt/create_sign", method = {RequestMethod.POST})
    public ResponseResult createSign(@RequestBody RsaKeyDto dto) {
        String error;
        try {
            byte[] bs = RsaKeyUtil.sign(dto.getPrivateKey(), dto.getRaw().getBytes());
            return ResponseResult.successed(Base64.getEncoder().encodeToString(bs));
        } catch (Exception ex) {
            error = ex.toString();
        }
        return ResponseResult.failed(error);
    }
    //验证签名
    @RequestMapping(value = "/encrypt/verify_sign", method = {RequestMethod.POST})
    public ResponseResult verifySign(@RequestBody RsaKeyDto dto) {
        String error;
        try {
            boolean res = RsaKeyUtil.verify(dto.getPublicKey(), dto.getRaw().getBytes(), Base64.getDecoder().decode(dto.getSign()));
            return ResponseResult.successed(res);
        } catch (Exception ex) {
            error = ex.toString();
        }
        return ResponseResult.failed(error);
    }
    //base64原文
    @RequestMapping(value = "/encrypt/base64_raw", method = {RequestMethod.GET})
    public ResponseResult base64Raw(@RequestParam("raw") String raw) {
        return ResponseResult.successed(Base64.getEncoder().encodeToString(raw.getBytes()));
    }
}