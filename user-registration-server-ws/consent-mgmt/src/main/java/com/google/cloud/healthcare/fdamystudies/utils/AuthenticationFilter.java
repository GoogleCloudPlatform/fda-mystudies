/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import java.io.IOException;
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
import com.google.cloud.healthcare.fdamystudies.service.CommonServiceImpl;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil.ErrorCodes;

@Component
public class AuthenticationFilter implements Filter {

  private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

  @Override
  public void init(final FilterConfig filterConfig) throws ServletException {
    logger.info("Initializing filter :{}", this);
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;

      if (!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
        String userId = httpServletRequest.getHeader("userId");
        String accessToken = httpServletRequest.getHeader("accessToken");
        String clientToken = httpServletRequest.getHeader("clientToken");

        boolean isInterceptorUrl = checkIfInterceptorUrl(httpServletRequest);

        if (isInterceptorUrl) {
          setCommonHeaders(httpServletResponse);
          chain.doFilter(httpServletRequest, httpServletResponse);
        } else {
          validateTokenAndSetHeaders(
              chain, httpServletRequest, httpServletResponse, userId, accessToken, clientToken);
        }
      } else {
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        chain.doFilter(request, response);
      }
    }
  }

  private boolean checkIfInterceptorUrl(HttpServletRequest httpServletRequest) {
    boolean isInterceptorUrl = false;
    ApplicationPropertyConfiguration applicationConfiguratation =
        BeanUtil.getBean(ApplicationPropertyConfiguration.class);
    String interceptorUrl = applicationConfiguratation.getInterceptorUrls();
    String uri = httpServletRequest.getRequestURI();
    String[] list = interceptorUrl.split(",");
    for (int i = 0; i < list.length; i++) {
      logger.info(list[i]);
      if (uri.endsWith(list[i].trim())) {
        isInterceptorUrl = true;
      }
    }
    return isInterceptorUrl;
  }

  private void validateTokenAndSetHeaders(
      FilterChain chain,
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      String userId,
      String accessToken,
      String clientToken)
      throws IOException, ServletException {
    Integer value;
    if ((accessToken != null)
        && !StringUtils.isEmpty(accessToken)
        && (userId != null)
        && !StringUtils.isEmpty(userId)
        && (null != clientToken)
        && !StringUtils.isEmpty(clientToken)) {
      CommonServiceImpl commonService = BeanUtil.getBean(CommonServiceImpl.class);
      value = commonService.validateAccessToken(userId, accessToken, clientToken);
      if (null != value && 1 == value) {
        setCommonHeaders(httpServletResponse);
        chain.doFilter(httpServletRequest, httpServletResponse);
      } else {
        setCommonHeaders(httpServletResponse);
        httpServletResponse.sendError(
            HttpServletResponse.SC_UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getValue());
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      }
    } else {
      setCommonHeaders(httpServletResponse);
      httpServletResponse.sendError(
          HttpServletResponse.SC_UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getValue());
      httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
  }

  private void setCommonHeaders(HttpServletResponse httpServletResponse) {
    httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
    httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
    httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
    httpServletResponse.setHeader(
        "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
  }

  @Override
  public void destroy() {
    logger.warn("Destructing filter :{}", this);
  }
}
