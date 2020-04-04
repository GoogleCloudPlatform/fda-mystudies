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
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (request instanceof HttpServletRequest) {
      if (!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
        String userId = httpServletRequest.getHeader("userId");
        String accessToken = httpServletRequest.getHeader("accessToken");
        String clientToken = httpServletRequest.getHeader("clientToken");

        Integer value = null;
        boolean isInterceptorURL = false;
        boolean isInvalidURL = false;
        ApplicationPropertyConfiguration applicationConfiguratation =
            BeanUtil.getBean(ApplicationPropertyConfiguration.class);
        String interceptorURL = applicationConfiguratation.getInterceptorUrls();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String[] list = interceptorURL.split(",");
        for (int i = 0; i < list.length; i++) {
          logger.info(list[i]);
          if (uri.endsWith(list[i].trim())) {
            isInterceptorURL = true;
          }
        }

        if (isInterceptorURL) {
          setCommonHeaders(httpServletResponse);
          chain.doFilter(request, response);
        } else {
          if ((accessToken != null)
              && !StringUtils.isEmpty(accessToken)
              && (userId != null)
              && !StringUtils.isEmpty(userId)
              && (null != clientToken)
              && !StringUtils.isEmpty(clientToken)) {
            CommonServiceImpl commonService = BeanUtil.getBean(CommonServiceImpl.class);
            value = commonService.validateAccessToken(userId, accessToken, clientToken);
            value = 1;
            if (value == 1) {
              setCommonHeaders(httpServletResponse);
              chain.doFilter(request, response);
            } else {
              if (response instanceof HttpServletResponse) {
                setCommonHeaders(httpServletResponse);
                httpServletResponse.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getValue());
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              }
            }
          } else {
            setCommonHeaders(httpServletResponse);
            httpServletResponse.sendError(
                HttpServletResponse.SC_UNAUTHORIZED, ErrorCodes.UNAUTHORIZED.getValue());
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          }
        }
      } else {
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        chain.doFilter(request, response);
      }
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
