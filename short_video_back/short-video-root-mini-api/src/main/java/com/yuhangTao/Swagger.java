package com.yuhangTao;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/*
 * 通过@Configuration注解，让Spring来加载该类配置。
 * 再通过@EnableSwagger2注解来启用Swagger2。
 * 通过http://localhost:8081/short_video/swagger-ui.html访问
 *
 * 常用注解：
 *- @Api()用于类；
 *表示标识这个类是swagger的资源
 *- @ApiOperation()用于方法；
 *表示一个http请求的操作
 *- @ApiParam()用于方法，参数，字段说明；
 *表示对参数的添加元数据（说明或是否必填等）
 *- @ApiModel()用于类
 *表示对类进行说明，用于参数用实体类接收
 *- @ApiModelProperty()用于方法，字段
 *表示对model属性的说明或者数据操作更改
 *- @ApiIgnore()用于类，方法，方法参数
 *表示这个方法或者类被忽略
 *- @ApiImplicitParam() 用于方法
 *表示单独的请求参数
 *- @ApiImplicitParams() 用于方法，包含多个 @ApiImplicitParam

 * */
@Configuration
@EnableSwagger2
public class Swagger {

    /*
     * 通过createRestApi函数创建Docket的Bean之后，apiInfo()用来创建该Api的基本信息
     * （这些基本信息会展现在文档页面中）。select()函数返回一个ApiSelectorBuilder实
     * 例用来控制哪些接口暴露给Swagger来展现，本例采用指定扫描的包路径来定义，Swagger
     * 会扫描该包下所有Controller定义的API，并产生文档内容（除了被@ApiIgnore指定的请求。
     */
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //扫描包
                .apis(RequestHandlerSelectors.basePackage("com.yuhangTao.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //设置页面标题
                .title("微信短视频后端api接口文档")
                //设置联系人
                .contact(new Contact("yuhangTao",
                        "https://github.com/code-refugee/graduation.git",
                        "taoyuh1011@foxmail.com"))
                //描述信息
                .description("欢迎访问短视频接口文档，这里是描述信息")
                .version("1.0")
                .build();
    }
}
