package com.yuhangTao;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*静态资源配置，显示图片、文件*/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /*addResourceLocations指的是文件放置的目录，
     * addResoureHandler指的是对外暴露的访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*资源映射，通过"/**"访问所有的资源*/
        registry.addResourceHandler("/**")
                //加这句是因为我们要访问swagger文件，而它所发布的目录是这样的
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:f:/graduation/userupload/");
    }
}
