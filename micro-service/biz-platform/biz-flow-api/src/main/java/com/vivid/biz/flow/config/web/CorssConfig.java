//package com.vivid.biz.flow.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.codec.ServerCodecConfigurer;
//import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.web.cors.reactive.CorsUtils;
//import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//
///**
// * Created by wuwei2_m on 2019/1/23.
// * 跨域允许
// */
//@Configuration
//public class CorssConfig {
//
//    private static final String MAX_AGE = "18000L";
//
//    //这里为支持的请求头，如果有自定义的header字段请自己添加（如username，这里不能使用*）
//    private static final String ALLOWED_HEADERS = "x-requested-with,authorization,Content-Type,Authorization,credential,X-XSRF-TOKEN,token,username,client_id";
//    private static final String ALLOWED_METHODS = "*";
//    private static final String ALLOWED_ORIGIN = "*";
//    @Bean
//    public WebFilter corsFilter() {
//        return (ServerWebExchange ctx, WebFilterChain chain) -> {
//            ServerHttpRequest request = ctx.getRequest();
//            if (CorsUtils.isCorsRequest(request)) {
//                HttpHeaders requestHeaders = request.getHeaders();
//                ServerHttpResponse response = ctx.getResponse();
//                HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
//                HttpHeaders headers = response.getHeaders();
//                /* ie下不会发送Authorization，客户端拒绝接收数据
//                    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
//                    headers.addAll(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders.getAccessControlRequestHeaders());
//                    if(requestMethod != null){
//                        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
//                    }
//                */
//                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN);
//                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
//                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,ALLOWED_METHODS);
//                headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
//                headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
//                headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);
//                if (request.getMethod() == HttpMethod.OPTIONS) {
//                    response.setStatusCode(HttpStatus.OK);
//                    return Mono.empty();
//                }
//
//            }
//            return chain.filter(ctx);
//        };
//    }
//
//    @Bean
//    public ServerCodecConfigurer serverCodecConfigurer() {
//        return new DefaultServerCodecConfigurer();
//    }
//    /**
//     * 解决 Only one connection receive subscriber allowed with Content-Type application/x-www-form-urlencoded
//     * spring boot2.0.5 ~ 2.1.0 problm
//     * see https://github.com/spring-cloud/spring-cloud-gateway/issues/541
//     * @return
//     */
//    @Bean
//    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
//        HiddenHttpMethodFilter hiddenHttpMethodFilter = new HiddenHttpMethodFilter() {
//            @Override
//            public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//                return chain.filter(exchange);
//            }
//        };
//        return hiddenHttpMethodFilter;
//    }
//}