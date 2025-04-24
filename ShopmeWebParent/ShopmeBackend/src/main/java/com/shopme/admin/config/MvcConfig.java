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

    /**
     * Any request made to "/user-photos/**" will be mapped to the corresponding files
     * in our file system, allowing those photos to be accessed, shared, or displayed. <br>
     * In other words, it makes the directory accessible.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userPhotosDir = "user-photos";
        Path userPhotosDirPath = Path.of(userPhotosDir);
        registry.addResourceHandler("/" + userPhotosDir + "/**")
                .addResourceLocations("file:/" + userPhotosDirPath.toFile().getAbsolutePath() + "/");

        final String categoryPhotosDir = "../category-photos";
        Path categoryPhotosDirPath = Path.of(categoryPhotosDir);
        registry.addResourceHandler("/category-photos/**")
                .addResourceLocations("file:/" + categoryPhotosDirPath.toFile().getAbsolutePath() + "/");
    }
}
