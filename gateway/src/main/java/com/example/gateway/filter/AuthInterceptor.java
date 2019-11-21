package com.example.gateway.filter;

import com.example.common.jwt.JwtInfo;
import com.example.common.utils.JwtTokenUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

@Slf4j
public class AuthInterceptor implements RequestInterceptor, GlobalFilter {


    @Value("${gate.startWith}")
    private String startWith;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String requestURI = request.getRequestURI();
        log.info("gateway收到请求地址 {}", requestURI);
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("gateway收到请求参数 {}");
        String token = request.getHeader("token");
        if(isPassUri(requestURI)){
            return chain.filter(exchange);
        }
        if (JwtTokenUtils.isExpir(token)) {
            log.info("token过期");
            return null;

        }

        JwtInfo jwtInfo = JwtTokenUtils.getUserInfoFromToken(token);

        return null;
    }

    //转发header请求参数
    @Override
    public void apply(RequestTemplate requestTemplate) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    requestTemplate.header(name, value);
                }
            }
        }
    }

    private boolean isPassUri(String requestUri) {
        boolean flag = false;
        for (String s : startWith.split(",")) {
            if (s.equals(requestUri)) {
                return true;
            }
        }
        return flag;
    }

}
