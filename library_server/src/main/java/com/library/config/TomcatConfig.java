package com.library.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat 配置：允许 URL 查询参数中包含 UTF-8 中文字符
 */
@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.setURIEncoding("UTF-8");
            // 允许 URL 中使用特殊字符（中文等 UTF-8 多字节字符）
            connector.setProperty("relaxedQueryChars", "[]|{}^\\`\"<>");
        });
    }
}
