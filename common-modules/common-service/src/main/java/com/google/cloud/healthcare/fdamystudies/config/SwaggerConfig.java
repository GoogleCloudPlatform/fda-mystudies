/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import com.google.cloud.healthcare.fdamystudies.beans.BaseResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Value;
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
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
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

  @Value("${component.name:}")
  private Optional<String> componentName;

  @Bean
  public Docket api() {
    Docket docket =
        new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.google.cloud.healthcare.fdamystudies"))
            .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
            .paths(PathSelectors.any())
            .build();

    ModelRef errorModel = new ModelRef(BaseResponse.class.getSimpleName());
    List<ResponseMessage> responseMessages =
        Arrays.asList(
            new ResponseMessageBuilder()
                .code(400)
                .message(CommonConstants.BAD_REQUEST_MESSAGE)
                .responseModel(new ModelRef(ValidationErrorResponse.class.getSimpleName()))
                .build(),
            new ResponseMessageBuilder()
                .code(401)
                .message(CommonConstants.UNAUTHORIZED_MESSAGE)
                .responseModel(errorModel)
                .build(),
            new ResponseMessageBuilder()
                .code(500)
                .message(CommonConstants.APPLICATION_ERROR_MESSAGE)
                .responseModel(errorModel)
                .build());

    docket
        .useDefaultResponseMessages(false)
        .globalResponseMessage(RequestMethod.GET, responseMessages)
        .globalResponseMessage(RequestMethod.POST, responseMessages)
        .globalResponseMessage(RequestMethod.PUT, responseMessages)
        .globalResponseMessage(RequestMethod.DELETE, responseMessages);
    return docket;
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "Api Documentation",
        WordUtils.capitalizeFully(componentName.get()),
        ApiInfo.DEFAULT.getVersion(),
        null,
        ApiInfo.DEFAULT_CONTACT,
        "View License",
        "https://github.com/GoogleCloudPlatform/fda-mystudies/blob/master/LICENSE.txt",
        Collections.emptyList());
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
