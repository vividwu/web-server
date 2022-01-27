package com.vivid.biz.flow.config.web;

/**
 * Created by wuwei2_m on 2019/5/24.
 */

import com.vivid.biz.flow.dto.ui.DataTables;
import com.vivid.framework.common.data.ResponseResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 自定义客户端请求服务端返回的格式转换
 */
@ControllerAdvice
public class Page2DataTablesResponseAdvice implements ResponseBodyAdvice<Object> {

    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        boolean isFormater = false;
        //ResponseResult<Page<T>>
        //防止ResponseResult后没有显式定义泛型com.cyou.common.data.ResponseResult
        if(methodParameter.getParameterType() == ResponseResult.class && methodParameter.getGenericParameterType() instanceof ParameterizedType) {
            //ResponseResult<T>
            Type[] rawWarpperType = ((ParameterizedType)methodParameter.getGenericParameterType()).getActualTypeArguments();
            if(rawWarpperType != null && rawWarpperType.length>0)
            {
                //Page<T>
                if(rawWarpperType[0] instanceof ParameterizedType)  //排出ResponseResult<T>
                    isFormater = ((ParameterizedType)rawWarpperType[0]).getRawType() == com.github.pagehelper.PageInfo.class;
            }
        }
        System.out.println("dtpage begin formater=====>" + isFormater);
        return  isFormater;
    }

    public Object beforeBodyWrite(Object returnValue, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {

        //formater=page2dt
        if(serverHttpRequest.getURI().getQuery() != null &&
                serverHttpRequest.getURI().getQuery().contains("page2dt"))
        {
            return page2Datatables(returnValue);
        }
        else
            return returnValue;

    }

    private DataTables page2Datatables(Object returnValue){
        DataTables dataTables = new DataTables();
        Object res = ((ResponseResult)returnValue).getData();
        if(res == null)  //没有查询到数据
            return dataTables;
        com.github.pagehelper.PageInfo page = (com.github.pagehelper.PageInfo)res;
        dataTables.setRecordsFiltered(page.getTotal());
        dataTables.setRecordsTotal(page.getTotal());
        dataTables.setLength(page.getSize());
        dataTables.setStart(page.getStartRow());
        dataTables.setData(page.getList());
        return dataTables;
    }
}
