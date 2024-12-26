package com.shopme.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * This class configures Spring MVC to expose (allow the clients to access) a specific directory
 * in the file system.
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private static final String UPLOAD_DIR = "user-photos";

    /**
     * Any request made to "/user-photos/**" will be mapped to the corresponding files
     * in our file system, allowing those photos to be accessed, shared, or displayed. <br>
     * In other words, it makes the directory accessible.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Path.of(UPLOAD_DIR);

        registry.addResourceHandler("/" + UPLOAD_DIR + "/**")
                .addResourceLocations("file:/" + path.toAbsolutePath() + "/");
    }
}
