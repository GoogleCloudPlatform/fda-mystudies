package com.google.cloud.healthcare.fdamystudies.auditlog.filter;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import com.google.cloud.healthcare.fdamystudies.filter.BaseTokenIntrospectionFilter;

@Component
@Order(2)
public class TokenIntrospectionFilter extends BaseTokenIntrospectionFilter {

  private Map<String, String[]> uriTemplateAndMethods = new HashMap<>();

  @Autowired ServletContext context;

  @PostConstruct
  public void init() {
    uriTemplateAndMethods.put(
        String.format("%s/v1/events", context.getContextPath()),
        new String[] {HttpMethod.POST.name()});
  }

  @Override
  protected Map<String, String[]> getUriTemplateAndHttpMethodsMap() {
    return uriTemplateAndMethods;
  }
}
