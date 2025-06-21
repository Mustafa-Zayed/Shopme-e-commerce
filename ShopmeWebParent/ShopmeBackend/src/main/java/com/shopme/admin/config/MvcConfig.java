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
        registerResourceHandler(registry, "user-photos", "user-photos");
        registerResourceHandler(registry, "category-photos", "../category-photos");
        registerResourceHandler(registry, "brand-logos", "../brand-logos");
        registerResourceHandler(registry, "product-images", "../product-images");
    }

    private void registerResourceHandler(ResourceHandlerRegistry registry, String dirName,
                                         String directoryPath) {
        Path path = Path.of(directoryPath);
        registry.addResourceHandler("/" + dirName + "/**")
                .addResourceLocations("file:/" + path.toFile().getAbsolutePath() + "/");
    }
}
