/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
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
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.CommonServiceImpl;

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
      if (!AppConstants.OPTIONS_METHOD.equalsIgnoreCase(httpServletRequest.getMethod())) {
        String userId = httpServletRequest.getHeader(AppConstants.USER_ID_KEY);
        String accessToken = httpServletRequest.getHeader(AppConstants.ACCESS_TOKEN_KEY);
        String clientToken = httpServletRequest.getHeader(AppConstants.CLIENT_TOKEN_KEY);
        Integer value = null;
        boolean isValid = false;
        boolean isInterceptorURL = false;
        boolean isServerApiUrl = false;
        String appMessage = "";
        ApplicationConfiguration applicationConfiguration =
            BeanUtil.getBean(ApplicationConfiguration.class);
        String interceptorURL = applicationConfiguration.getInterceptorUrls();
        String serverApiUrls = applicationConfiguration.getServerApiUrls();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String[] list = interceptorURL.split(",");
        for (int i = 0; i < list.length; i++) {
          if (uri.endsWith(list[i].trim())) {
            isInterceptorURL = true;
          }
        }
        if (!isInterceptorURL) {
          String[] listServerApiUrls = serverApiUrls.split(",");
          for (int i = 0; i < listServerApiUrls.length; i++) {
            if (uri.endsWith(listServerApiUrls[i].trim())) {
              isServerApiUrl = true;
              break;
            }
          }
        }
        if (isInterceptorURL) {
          httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
          httpServletResponse.setHeader("Access-Control-Allow-Methods",
              "GET, POST, PUT, DELETE, OPTIONS, HEAD");
          chain.doFilter(request, response);
        } else if (isServerApiUrl) {
          String applicationId = httpServletRequest.getHeader(AppConstants.APPLICATION_ID_HEADER);
          String clientId = httpServletRequest.getHeader(AppConstants.CLIENT_ID_PARAM);
          String clientSecret = httpServletRequest.getHeader(AppConstants.CLIENT_SECRET_PARAM);
          CommonServiceImpl commonService = BeanUtil.getBean(CommonServiceImpl.class);
          boolean isAllowed =
              commonService.validateServerClientCredentials(applicationId, clientId, clientSecret);
          if (isAllowed) {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpServletResponse.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            chain.doFilter(request, response);
          } else {
            if (response instanceof HttpServletResponse) {
              httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
              httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
              httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
              httpServletResponse.setHeader("Access-Control-Allow-Methods",
                  "GET, POST, PUT, DELETE, OPTIONS, HEAD");
              httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
          }
        } else {
          if ((accessToken != null) && !StringUtils.isEmpty(accessToken) && (userId != null)
              && !StringUtils.isEmpty(userId) && (null != clientToken)
              && !StringUtils.isEmpty(clientToken)) {
            CommonService commonService = BeanUtil.getBean(CommonService.class);
            value = commonService.validateAccessToken(userId, accessToken, clientToken);
            if (value != null && value.intValue() == 1) {
              httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
              httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
              httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
              httpServletResponse.setHeader("Access-Control-Allow-Methods",
                  "GET, POST, PUT, DELETE, OPTIONS, HEAD");
              chain.doFilter(request, response);
            } else {
              if (response instanceof HttpServletResponse) {
                httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
                httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpServletResponse.setHeader("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              }
            }
          } else {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpServletResponse.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
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
