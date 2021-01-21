/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig extends WebMvcConfigurationSupport {
  @Bean
  public Docket api() {
    Docket docket =
        new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.google.cloud.healthcare.fdamystudies"))
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
            .paths(PathSelectors.any())
            .build();

    List<ResponseMessage> responses = new ArrayList<>();
    responses.add(
        new ResponseMessageBuilder()
            .code(ErrorCode.APPLICATION_ERROR.getStatus())
            .message(ErrorCode.APPLICATION_ERROR.getDescription())
            .build());
    responses.add(
        new ResponseMessageBuilder()
            .code(ErrorCode.UNAUTHORIZED.getStatus())
            .message(ErrorCode.UNAUTHORIZED.getDescription())
            .build());

    docket
        .useDefaultResponseMessages(false)
        .globalResponseMessage(RequestMethod.GET, responses)
        .globalResponseMessage(RequestMethod.POST, responses)
        .globalResponseMessage(RequestMethod.PUT, responses)
        .globalResponseMessage(RequestMethod.DELETE, responses);
    return docket;
  }

  @Bean
  public UiConfiguration tryItOutConfig() {
    final String[] methodsWithTryItOutButton = {};
    return UiConfigurationBuilder.builder()
        .supportedSubmitMethods(methodsWithTryItOutButton)
        .build();
  }

  @Override
  protected void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry
        .addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
    registry
        .addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");

    registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
    registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
    registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
  }
}
