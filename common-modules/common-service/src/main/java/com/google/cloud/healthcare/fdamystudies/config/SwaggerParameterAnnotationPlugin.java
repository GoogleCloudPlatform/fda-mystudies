/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import static springfox.bean.validators.plugins.Validators.annotationFromBean;
import static springfox.bean.validators.plugins.Validators.annotationFromField;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import springfox.bean.validators.plugins.Validators;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

@Component
@Order(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)
public class SwaggerParameterAnnotationPlugin implements ModelPropertyBuilderPlugin {

  @Override
  public boolean supports(DocumentationType delimiter) {
    // we simply support all documentationTypes!
    return true;
  }

  @Override
  public void apply(ModelPropertyContext context) {
    Optional<NotBlank> notBlank = extractAnnotation(context, NotBlank.class);
    if (notBlank.isPresent()) {
      context.getBuilder().required(true);
    }

    Optional<NotEmpty> notEmpty = extractAnnotation(context, NotEmpty.class);
    if (notEmpty.isPresent()) {
      context.getBuilder().required(true);
    }
  }

  @VisibleForTesting
  <T> Optional<T> extractAnnotation(ModelPropertyContext context, Class claz) {
    return annotationFromBean(context, claz).or(annotationFromField(context, claz));
  }
}
