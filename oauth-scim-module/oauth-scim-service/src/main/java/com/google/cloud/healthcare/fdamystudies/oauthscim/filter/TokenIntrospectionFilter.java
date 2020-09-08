/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.filter;

import com.google.cloud.healthcare.fdamystudies.filter.BaseTokenIntrospectionFilter;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class TokenIntrospectionFilter extends BaseTokenIntrospectionFilter {

  private Map<String, String[]> uriTemplateAndMethods = new HashMap<>();

  @Autowired ServletContext context;

  @PostConstruct
  public void init() {
    uriTemplateAndMethods.put(
        String.format("%s/users", context.getContextPath()), new String[] {HttpMethod.POST.name()});
    uriTemplateAndMethods.put(
        String.format("%s/users/{userId}", context.getContextPath()),
        new String[] {HttpMethod.PUT.name(), HttpMethod.DELETE.name()});

    uriTemplateAndMethods.put(
        String.format("%s/users/{userId}/change_password", context.getContextPath()),
        new String[] {HttpMethod.PUT.name()});
  }

  @Override
  protected Map<String, String[]> getUriTemplateAndHttpMethodsMap() {
    return uriTemplateAndMethods;
  }
}
