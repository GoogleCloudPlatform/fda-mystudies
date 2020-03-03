package com.google.cloud.healthcare.fdamystudies.util;

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
        String invalidURL = applicationConfiguratation.getInvalidUrl();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String[] list = interceptorURL.split(",");
        String[] invalidUrlList = invalidURL.split(",");
        for (int i = 0; i < list.length; i++) {
          logger.info(list[i]);
          if (uri.endsWith(list[i].trim())) {
            isInterceptorURL = true;
          }
        }
        // TODO: removed below Logic once studyState API implemented during sprint 3 development
        for (int i = 0; i < invalidUrlList.length; i++) {
          if (uri.endsWith(invalidUrlList[i].trim())) {
            isInvalidURL = true;
          }
        }
        // End Here

        if (isInterceptorURL) {
          httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
          httpServletResponse.setHeader(
              "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
          chain.doFilter(request, response);
        } else if (isInvalidURL) {
          httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
          httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
          httpServletResponse.setHeader(
              "Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
          httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
          if ((accessToken != null)
              && !StringUtils.isEmpty(accessToken)
              && (userId != null)
              && !StringUtils.isEmpty(userId)
              && (null != clientToken)
              && !StringUtils.isEmpty(clientToken)) {
            CommonServiceImpl commonService = BeanUtil.getBean(CommonServiceImpl.class);
            value = commonService.validateAccessToken(userId, accessToken, clientToken);
            if (value == 1) {
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
