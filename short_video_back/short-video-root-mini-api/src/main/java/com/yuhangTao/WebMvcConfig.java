package com.yuhangTao;


import com.yuhangTao.interceptor.MiniInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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

    /*将拦截器注册到spring中*/
    @Bean
    public MiniInterceptor getInterceptor(){
        return new MiniInterceptor();
    }

    /*先注册拦截器。然后添加所要拦截的controller*/
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(getInterceptor()).addPathPatterns("/logout","/uploadface");
    }
}
