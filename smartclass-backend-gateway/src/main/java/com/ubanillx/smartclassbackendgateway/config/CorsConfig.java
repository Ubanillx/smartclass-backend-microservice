package com.ubanillx.smartclassbackendgateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 跨域配置类
 * 处理网关的全局跨域配置
 */
@Configuration
public class CorsConfig {

    /**
     * 跨域过滤器配置
     * 这种方式比YAML配置更灵活，可以处理复杂的跨域需求
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 允许所有域名进行跨域调用
        corsConfig.addAllowedOriginPattern("*");
        
        // 允许所有头信息
        corsConfig.addAllowedHeader("*");
        
        // 允许所有HTTP方法
        corsConfig.addAllowedMethod("*");
        
        // 允许携带认证信息
        corsConfig.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        corsConfig.setMaxAge(3600L);
        
        // 暴露的头信息，让前端能够获取到
        corsConfig.addExposedHeader("Content-Length");
        corsConfig.addExposedHeader("Access-Control-Allow-Origin");
        corsConfig.addExposedHeader("Access-Control-Allow-Headers");
        corsConfig.addExposedHeader("Cache-Control");
        corsConfig.addExposedHeader("Content-Language");
        corsConfig.addExposedHeader("Content-Type");
        corsConfig.addExposedHeader("Expires");
        corsConfig.addExposedHeader("Last-Modified");
        corsConfig.addExposedHeader("Pragma");
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }

    /**
     * 处理OPTIONS预检请求的过滤器
     * 确保OPTIONS请求能够正确响应
     */
    @Bean
    public WebFilter corsOptionsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            ServerHttpRequest request = ctx.getRequest();
            if (HttpMethod.OPTIONS.equals(request.getMethod())) {
                ServerHttpResponse response = ctx.getResponse();
                HttpHeaders headers = response.getHeaders();
                
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                headers.add("Access-Control-Allow-Headers", "*");
                headers.add("Access-Control-Allow-Credentials", "true");
                headers.add("Access-Control-Max-Age", "3600");
                
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }
            return chain.filter(ctx);
        };
    }
} 