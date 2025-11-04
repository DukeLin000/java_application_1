package org.example.javademo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${app.files.root:./uploads}")
    private String root;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 將 /files/** 映射到本機檔案系統目錄（可被瀏覽器直接讀取）
        String location = "file:" + (root.endsWith("/") ? root : root + "/");
        registry.addResourceHandler("/files/**")
                .addResourceLocations(location)
                .setCachePeriod(3600); // 1h
    }
}
