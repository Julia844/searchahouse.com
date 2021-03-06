package edu.searchahouse.admin.config.mvc;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("sections/home/home");
        registry.addViewController("/accessDenied").setViewName("sections/errors/accessDenied");
    }

    /**
     * Ensure that static resources are served by container’s default servlet. Configure the static resources by overriding the addResourceHandlers() method of
     * the WebMvcConfigurerAdapter class. Ensure that requests made to static resources are delegated forward to the container’s default servlet. This is done
     * by overriding the configureDefaultServletHandling() method of the WebMvcConfigurerAdapter class.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    /**
     * Configure the exception resolver bean.
     */
    @Bean
    public SimpleMappingExceptionResolver exceptionResolver() {
        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();

        Properties exceptionMappings = new Properties();

        exceptionMappings.put("java.lang.Exception", "sections/error/error");
        exceptionMappings.put("java.lang.RuntimeException", "sections/error/error");

        exceptionResolver.setExceptionMappings(exceptionMappings);

        Properties statusCodes = new Properties();

        statusCodes.put("sections/error/404", "404");
        statusCodes.put("sections/error/error", "500");

        exceptionResolver.setStatusCodes(statusCodes);

        return exceptionResolver;
    }

}
