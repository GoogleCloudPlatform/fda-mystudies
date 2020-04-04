/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

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
import com.google.cloud.healthcare.fdamystudies.controller.bean.AuthInfoBean;
import com.google.cloud.healthcare.fdamystudies.service.ClientService;
import com.google.cloud.healthcare.fdamystudies.service.UserDetailsService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

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

    logger.info("AuthenticationFilter doFilter() - starts");

    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    if (request instanceof HttpServletRequest) {
      if (!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
        String accessToken = httpServletRequest.getHeader("accessToken");
        String userId = httpServletRequest.getHeader("userId");
        String clientToken = httpServletRequest.getHeader("clientToken");
        boolean isValidClient = false;
        Integer isValidAccessToken = null;
        boolean isValid = false;
        boolean isInterceptorURL = false;
        ApplicationPropertyConfiguration applicationConfiguratation =
            BeanUtil.getBean(ApplicationPropertyConfiguration.class);
        String interceptorURL = applicationConfiguratation.getInterceptorUrls();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String[] list = interceptorURL.split(",");
        for (int i = 0; i < list.length; i++) {
          if (uri.endsWith(list[i].trim())) {
            isInterceptorURL = true;
          }
        }

        if (isInterceptorURL) {
          isValid = true;
          setCommonHeaders(httpServletResponse);
          chain.doFilter(request, response);
          logger.info(AppConstants.AUTHENTICATION_FILTER_ENDS);
        } else {
          if ((accessToken != null)
              && !StringUtils.isEmpty(accessToken)
              && (null != clientToken)
              && !StringUtils.isEmpty(clientToken)
              && (null != userId)
              && !StringUtils.isEmpty(userId)) {
            ClientService client = BeanUtil.getBean(ClientService.class);
            UserDetailsService userDetailsService = BeanUtil.getBean(UserDetailsService.class);
            try {
              isValidClient = client.isValidClient(clientToken, userId);

              if (isValidClient) {
                AuthInfoBean authInfo = BeanUtil.getBean(AuthInfoBean.class);
                authInfo.setAccessToken(accessToken);
                authInfo.setUserId(userId);
                isValidAccessToken = userDetailsService.validateAccessToken(authInfo);

                if (isValidAccessToken == 1) {
                  setCommonHeaders(httpServletResponse);
                  chain.doFilter(request, response);
                  logger.info(AppConstants.AUTHENTICATION_FILTER_ENDS);
                } else if (isValidAccessToken == 2) {
                  if (response instanceof HttpServletResponse) {
                    setCommonHeaders(httpServletResponse);

                    httpServletResponse.setHeader(AppConstants.STATUS, "401");
                    httpServletResponse.setHeader(
                        AppConstants.TITLE,
                        MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue());
                    httpServletResponse.setHeader(
                        AppConstants.MESSAGE,
                        MyStudiesUserRegUtil.ErrorCodes.INVALID_ACCESS_TOKEN_USER_ID.getValue());

                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    logger.info(AppConstants.AUTHENTICATION_FILTER_ENDS);
                  }
                } else {
                  if (response instanceof HttpServletResponse) {
                    setCommonHeaders(httpServletResponse);

                    httpServletResponse.setHeader(AppConstants.STATUS, "401");
                    httpServletResponse.setHeader(
                        AppConstants.TITLE,
                        MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue());
                    httpServletResponse.setHeader(
                        AppConstants.MESSAGE,
                        MyStudiesUserRegUtil.ErrorCodes.SESSION_EXPIRED_MSG.getValue());

                    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    logger.info(AppConstants.AUTHENTICATION_FILTER_ENDS);
                  }
                }

              } else {
                httpServletResponse.setHeader("status", "401");
                httpServletResponse.setHeader(
                    AppConstants.TITLE, MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue());
                httpServletResponse.setHeader(
                    AppConstants.MESSAGE,
                    MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENT_TOKEN.getValue());

                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                logger.info(AppConstants.AUTHENTICATION_FILTER_ENDS);
              }

            } catch (Exception e) {
              logger.error("AuthenticationFilter.doFilter(): ", e);
              setCommonHeaders(httpServletResponse);

              httpServletResponse.setHeader(AppConstants.STATUS, "500");
              httpServletResponse.setHeader(
                  AppConstants.TITLE, MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue());
              httpServletResponse.setHeader(
                  AppConstants.MESSAGE,
                  MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
              httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
              logger.info(AppConstants.AUTHENTICATION_FILTER_ENDS);
            }
          } else {
            logger.info("Invalid Request");
            setCommonHeaders(httpServletResponse);
            httpServletResponse.setHeader(AppConstants.STATUS, "400");
            httpServletResponse.setHeader(
                AppConstants.TITLE, MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue());
            httpServletResponse.setHeader(
                AppConstants.MESSAGE,
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.info(AppConstants.AUTHENTICATION_FILTER_ENDS);
          }
        }
      } else {
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        chain.doFilter(request, response);
      }
    }
  }

  private void setCommonHeaders(HttpServletResponse httpServletResponse) {
    httpServletResponse.setHeader(AppConstants.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    httpServletResponse.setHeader(AppConstants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
    httpServletResponse.setHeader(AppConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    httpServletResponse.setHeader(
        AppConstants.ACCESS_CONTROL_ALLOW_METHODS, AppConstants.HTTP_METHODS);
  }

  @Override
  public void destroy() {
    logger.warn("Destructing filter :{}", this);
  }
}
