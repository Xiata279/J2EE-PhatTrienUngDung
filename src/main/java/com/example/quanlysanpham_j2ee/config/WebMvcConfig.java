package com.example.quanlysanpham_j2ee.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Phục vụ ảnh upload từ thư mục static/images/ trên filesystem
        String uploadPath = Paths.get("static/images").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath + "/");
    }
}
