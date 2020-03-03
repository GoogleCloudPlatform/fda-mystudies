/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
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
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;

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
        String urAdminAuthId = httpServletRequest.getHeader("urAdminAuthId");
        String accessToken = httpServletRequest.getHeader("auth");
        String clientId = "";
        String secretKey = "";
        Integer value = null;
        boolean isValid = false;
        boolean isInterceptorURL = false;
        ApplicationConfiguratation applicationConfiguratation =
            BeanUtil.getBean(ApplicationConfiguratation.class);
        String interceptorURL = applicationConfiguratation.getInterceptorUrls();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String[] list = interceptorURL.split(",");
        for (int i = 0; i < list.length; i++) {
          Pattern pattern = Pattern.compile(list[i].trim());
          if (pattern.matcher(uri).matches()) {
            isInterceptorURL = true;
            break;
          }
        }
        if (isInterceptorURL) {
          isValid = true;
          httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
          httpServletResponse.setHeader(
              "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
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
              httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
              httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
              httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
              httpServletResponse.setHeader(
                  "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
              chain.doFilter(request, response);
            } else {
              if (response instanceof HttpServletResponse) {
                httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
                httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
                httpServletResponse.setHeader(
                    "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              }
            }
          } else {
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            httpServletResponse.setHeader(
                "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
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
