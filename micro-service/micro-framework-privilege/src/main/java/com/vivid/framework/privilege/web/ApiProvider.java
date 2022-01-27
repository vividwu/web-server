package com.vivid.framework.privilege.web;

import com.vivid.framework.common.data.ResponseResult;
import com.vivid.framework.common.data.privilege.RegistDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="privilege-web",path = "api",fallback = ApiProvider.class)
public interface ApiProvider {
    @GetMapping("/regist")
    ResponseResult regist(RegistDto dto);
}
