//package com.vivid.framework.security.filter;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
////客户端解析Token
//@Component
////@Slf4j
//public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        System.out.println("api access");
//        // 验证token并返回对应的用户信息，自行实现即可
//        //JwtTokenServer.validRS256()
////        if (user != null) {
////            String mobile = user.getMobile();
////
////            if (mobile != null && SecurityContextHolder.getContext().getAuthentication() == null) {
////                UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);
////
////                if (userDetails != null) {
////                    UsernamePasswordAuthenticationToken authentication =
////                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
////                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////
////                    SecurityContextHolder.getContext().setAuthentication(authentication);
////                }
////            }
////        }
//        chain.doFilter(request, response);
//    }
//}