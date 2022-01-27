package com.vivid.biz.flow.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivid.biz.flow.dto.flow.ApplySubmitDto;
import com.vivid.biz.flow.dto.flow.FormDbDataDto;
import com.vivid.framework.security.dto.SecurityUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
@Component
public class FlowEngineUtil {
    @Value("${workflow.host}")
    String workflowHost;
    @Value("${workflow.app-id}")
    String appId;
    @Value("${workflow.private-key}")
    String privateKey;

    public Map post2WorkflowEngineMap(String path, Object dto, Map userMap) throws Exception {
        HttpHeaders headers = getHeaders(userMap);
        HttpEntity<?> requestEntity = new HttpEntity<>(dto, headers);
        String result = new RestTemplate().postForObject(new URI(workflowHost + path), requestEntity, String.class);
        //restTemplate.exchange(url, HttpMethod.POST, entity2, JSONObject.class)
        Map map = new ObjectMapper().readValue(result, Map.class);
        return map;
    }
    public Map post2WorkflowEngineUrl(String path, Map userMap) throws Exception {
        HttpHeaders headers = getHeaders(userMap);
        HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);
        String result = new RestTemplate().postForObject(new URI(workflowHost + path), requestEntity, String.class);
        //restTemplate.exchange(url, HttpMethod.POST, entity2, JSONObject.class)
        Map map = new ObjectMapper().readValue(result, Map.class);
        return map;
    }
    public Map getWorkflowEngineFormDbDataDto(String path, SecurityUser user) throws Exception {
        Map userMap = user.getAdditionalInformation();
        HttpHeaders headers = getHeaders(userMap);
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> result = new RestTemplate().exchange(new URI(workflowHost + path), HttpMethod.GET, entity, String.class);
        FormDbDataDto dto = new ObjectMapper().readValue(result.getBody(), FormDbDataDto.class);
        return dto.getData();
    }
    private HttpHeaders getHeaders(Map userMap) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);  //10位Time,uid,udisplay
        String userJson = String.format("{\"aid\":\"%s\",\"uid\":\"%s\",\"udisplay\":\"%s\",\"exp\":%d}", appId,userMap.get("uid"),userMap.get("udisplay"),new Date().getTime() / 1000, userMap.get("uid"), userMap.get("udisplay"));
        String sign = Base64.getEncoder().encodeToString(RsaKeyUtil.sign(privateKey,userJson.getBytes()));
        String auth = "bearer "+Base64.getEncoder().encodeToString(userJson.getBytes())+"."+sign;
        headers.set("Authorization", auth);  //时间+接口名+参数？
        headers.set("aid", appId);
        return headers;
    }
}
