// src/main/java/org/example/javademo/config/WebConfig.java
package org.example.javademo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 允許的來源（逗號分隔）。預設為本機開發三種常見來源，與你原先 CorsConfig 同語意
    @Value("#{'${app.cors.allowed-origins:http://localhost:*,http://127.0.0.1:*,http://10.0.2.2:*}'.split(',')}")
    private List<String> allowedOrigins;

    // 允許的方法（逗號分隔）
    @Value("#{'${app.cors.allowed-methods:GET,POST,PUT,PATCH,DELETE,OPTIONS}'.split(',')}")
    private List<String> allowedMethods;

    // 是否允許攜帶 Cookie/Authorization
    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    // 可選：要曝露給前端讀到的回應標頭（例如下載檔名）
    @Value("#{'${app.cors.exposed-headers:Content-Disposition}'.split(',')}")
    private List<String> exposedHeaders;

    // 預檢結果快取秒數
    @Value("${app.cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 注意：有 credentials 時不要用 allowedOrigins("*")；用 patterns 或列出白名單
                .allowedOriginPatterns(allowedOrigins.toArray(new String[0]))
                .allowedMethods(allowedMethods.toArray(new String[0]))
                .allowedHeaders("*")
                .exposedHeaders(exposedHeaders.toArray(new String[0]))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }
}
