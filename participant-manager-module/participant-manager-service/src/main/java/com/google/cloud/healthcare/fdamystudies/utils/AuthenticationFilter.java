/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;

@Component
public class AuthenticationFilter implements Filter {

  private static final String GET_POST_PUT_DELETE_OPTIONS_HEAD =
      "GET, POST, PUT, DELETE, OPTIONS, HEAD";
  private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
  private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
  private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
  private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
  private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
    logger.info("Initializing filter :{}", this);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (request instanceof HttpServletRequest) {
      if (!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
        String urAdminAuthId = httpServletRequest.getHeader("urAdminAuthId");
        String accessToken = httpServletRequest.getHeader("auth");
        String clientId = "";
        String secretKey = "";
        Integer value = null;
        boolean isInterceptorUrl = false;
        ApplicationPropertyConfiguration applicationConfiguratation =
            BeanUtil.getBean(ApplicationPropertyConfiguration.class);
        String interceptorUrl = applicationConfiguratation.getInterceptorUrls();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String[] list = interceptorUrl.split(",");
        for (int i = 0; i < list.length; i++) {
          Pattern pattern = Pattern.compile(list[i].trim());
          logger.info(list[i]);
          if (pattern.matcher(uri).matches()) {
            isInterceptorUrl = true;
            break;
          }
        }
        if (isInterceptorUrl) {
          httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
          httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, "*");
          httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
          httpServletResponse.setHeader(
              ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD);
          chain.doFilter(request, response);
        } else {
          if ((accessToken != null)
              && !StringUtils.isEmpty(accessToken)
              && (urAdminAuthId != null)
              && !StringUtils.isEmpty(urAdminAuthId)
              && !StringUtils.isEmpty(clientId)
              && !StringUtils.isEmpty(secretKey)) {
            CommonService commonService = BeanUtil.getBean(CommonService.class);
            value =
                commonService.validateAccessToken(urAdminAuthId, accessToken, clientId, secretKey);
            if (value != null && value == 1) {
              httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
              httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, "*");
              httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
              httpServletResponse.setHeader(
                  ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD);
              chain.doFilter(request, response);
            } else {
              if (response instanceof HttpServletResponse) {
                httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, "*");
                httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
                httpServletResponse.setHeader(
                    ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD);
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              }
            }
          } else {
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, "*");
            httpServletResponse.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
            httpServletResponse.setHeader(
                ACCESS_CONTROL_ALLOW_METHODS, GET_POST_PUT_DELETE_OPTIONS_HEAD);
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          }
        }
      } else {
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        chain.doFilter(request, response);
      }
    }
  }

  @Override
  public void destroy() {
    logger.warn("Destructing filter :{}", this);
  }
}
