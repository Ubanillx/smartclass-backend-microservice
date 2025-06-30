package com.ubanillx.smartclassbackendfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

// 启动Spring Boot应用程序的注解，标识这是一个Spring Boot应用
@SpringBootApplication

// 扫描Servlet组件，将自定义的Servlet、Filter、Listener组件注册到Spring容器中
@ServletComponentScan

// 启用AspectJ自动代理，对目标类进行代理，同时暴露当前代理对象以便在目标类内部调用
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)

// 启用定时任务调度，允许在应用中使用定时任务
@EnableScheduling

// 启用异步方法执行，允许在应用中使用异步方式执行方法
@EnableAsync

@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.ubanillx.smartclassbackendserviceclient.service"})

public class SmartclassBackendFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartclassBackendFileApplication.class, args);
    }

}
