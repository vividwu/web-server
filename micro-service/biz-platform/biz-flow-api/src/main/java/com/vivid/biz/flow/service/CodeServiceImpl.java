package com.vivid.biz.flow.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vivid.biz.flow.config.web.RequestHolder;
import com.vivid.biz.flow.dto.dict.DicConfParamsDto;
import com.vivid.biz.flow.entity.code.CommonCodeEnt;
import com.vivid.biz.flow.entity.code.DicConfEnt;
import com.vivid.biz.flow.repository.code.CommonCodeMapper;
import com.vivid.biz.flow.repository.code.DicConfMapper;
import com.vivid.framework.security.dto.SecurityUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CodeServiceImpl {
    @Autowired
    CommonCodeMapper commonCodeMapper;
    @Value("${workflow.host}")
    String workflowHost;
    @Autowired
    DicConfMapper dicConfMapper;

    public List<Map> getDicConfByQueryCode(String queryCode) throws IOException {
        DicConfEnt dicConfEnt = dicConfMapper.selectOne(new QueryWrapper<DicConfEnt>().lambda()
                .eq(DicConfEnt::getQueryCode, queryCode));
        if("mysql".equals(dicConfEnt.getDsType())){
            DicConfParamsDto sql = new ObjectMapper().readValue(dicConfEnt.getParams(), DicConfParamsDto.class);
            List<Map> sqlList = dicConfMapper.getSqlList(sql.getSql());
            return sqlList;
        }
        return null;
    }
    public PageInfo<CommonCodeEnt> getAllCodeByCategory(String category) {
        LambdaQueryWrapper<CommonCodeEnt> query = new QueryWrapper<CommonCodeEnt>().lambda()
                .eq(CommonCodeEnt::getDeleted, "no");
        if (StringUtils.isNotEmpty(category))
            query.eq(CommonCodeEnt::getCategory, category);
        PageHelper.startPage(RequestHolder.getPage().getNum(),RequestHolder.getPage().getSize());
        List<CommonCodeEnt> list = commonCodeMapper.selectList(query);
        return new PageInfo<>(list);
    }
    public String updateCodeDict(String category, String code, CommonCodeEnt dto){
        LambdaQueryWrapper<CommonCodeEnt> eq = new QueryWrapper<CommonCodeEnt>().lambda()
                .eq(CommonCodeEnt::getCode, code).eq(CommonCodeEnt::getCategory, category);
        CommonCodeEnt commonCodeEnt = commonCodeMapper.selectOne(eq);
        commonCodeEnt.setSort(dto.getSort());
        commonCodeEnt.setText(dto.getText());
        commonCodeEnt.setRemark(dto.getRemark());
        commonCodeMapper.update(commonCodeEnt,eq);
        if("script_const".equals(category)){  //更新公共脚本
            //查找所有变量的定义
            return post2Api(dto);
        }
        return null;
    }
    public String createCodeDict(CommonCodeEnt dto){
        if(StringUtils.isEmpty(dto.getCode()) || StringUtils.isEmpty(dto.getCategory()))
            return "编码和分类不能为空";

        LambdaQueryWrapper<CommonCodeEnt> eq = new QueryWrapper<CommonCodeEnt>().lambda()
                .eq(CommonCodeEnt::getCode, dto.getCode()).eq(CommonCodeEnt::getCategory, dto.getCategory());
        Integer count = commonCodeMapper.selectCount(eq);
        if(count>0)
            return "存在该字典项";
        dto.setDeleted("no");
        commonCodeMapper.insert(dto);
        if("script_const".equals(dto.getCategory())){  //更新公共脚本
            //查找所有变量的定义
            return post2Api(dto);
        }
        return null;
    }
    private String post2Api(CommonCodeEnt dto){
        String sc = getCommonScript(dto.getCategory());
        try {
            SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Map userMap = user.getAdditionalInformation();
            String param = String.format("file_name=%s&content=%s",
                    "common.vds",
                    URLEncoder.encode(sc, "UTF-8"));
            Map map = postWorkflowEngine2UpdateCommonScript("/openapi/common_script/write",userMap,param);
            if (map != null && !(Boolean) map.get("success")) {
                return map.get("message").toString();
            }
            return null;
        }catch (Exception ex){
            return ex.toString();
        }
    }
    public String getCommonScript(String category){
        List<CommonCodeEnt> commonCodeEnts = commonCodeMapper.selectList(new QueryWrapper<CommonCodeEnt>().lambda()
                .eq(CommonCodeEnt::getCategory, category));
        StringBuilder sc = new StringBuilder();
        commonCodeEnts.stream().forEach(x->{
            sc.append("let ").append(x.getCode()).append("=").append(x.getText()).append(";\n");
        });
        return sc.toString();
    }
    private Map postWorkflowEngine2UpdateCommonScript(String path, Map userMap, String param) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  //10位Time,uid,udisplay
        String auth = String.format("bearer %d,%s,%s", new Date().getTime() / 1000, userMap.get("uid"), userMap.get("udisplay"));
        headers.set("Authorization", auth);
        headers.set("aid", "123");
        HttpEntity<?> requestEntity = new HttpEntity<>(param, headers);
        String result = new RestTemplate().postForObject(new URI(workflowHost + path), requestEntity, String.class);
        //restTemplate.exchange(url, HttpMethod.POST, entity2, JSONObject.class)
        Map map = new ObjectMapper().readValue(result, Map.class);
        return map;
    }
}
