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
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
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
        ApplicationConfiguration applicationConfiguration =
            BeanUtil.getBean(ApplicationConfiguration.class);
        String interceptorURL = applicationConfiguration.getInterceptorUrls();
        String serverApiUrls = applicationConfiguration.getServerApiUrls();
        String uri = ((HttpServletRequest) request).getRequestURI();
        String[] list = interceptorURL.split(",");
        for (int i = 0; i < list.length; i++) {
          if (uri.endsWith(list[i].trim())) {
            isInterceptorURL = true;
            break;
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
          setCommonHeaders(httpServletResponse);
          chain.doFilter(request, response);

        } else if (isServerApiUrl) {
          String clientId = httpServletRequest.getHeader(AppConstants.CLIENT_ID_PARAM);
          String clientSecret = httpServletRequest.getHeader(AppConstants.CLIENT_SECRET_PARAM);
          CommonServiceImpl commonService = BeanUtil.getBean(CommonServiceImpl.class);
          boolean isAllowed = false;
          try {
            isAllowed = commonService.validateServerClientCredentials(clientId, clientSecret);
            if (isAllowed) {
              setCommonHeaders(httpServletResponse);
              chain.doFilter(request, response);
            } else {
              if (response instanceof HttpServletResponse) {
                setCommonHeaders(httpServletResponse);
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              }
            }
          } catch (UnAuthorizedRequestException e) {
            setCommonHeaders(httpServletResponse);

            httpServletResponse.setHeader(
                AppConstants.CODE, String.valueOf(ErrorCode.EC_718.code()));
            httpServletResponse.setHeader(
                AppConstants.USER_MESSAGE, ErrorCode.EC_718.errorMessage());
            httpServletResponse.setHeader(AppConstants.TYPE, AppConstants.ERROR_STR);
            httpServletResponse.setHeader(
                AppConstants.DETAIL_MESSAGE, ErrorCode.EC_719.errorMessage());

            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          } catch (InvalidRequestException e) {
            setCommonHeaders(httpServletResponse);

            httpServletResponse.setHeader(
                AppConstants.CODE, String.valueOf(ErrorCode.EC_701.code()));
            httpServletResponse.setHeader(
                AppConstants.USER_MESSAGE, ErrorCode.EC_711.errorMessage());
            httpServletResponse.setHeader(AppConstants.TYPE, AppConstants.ERROR_STR);
            httpServletResponse.setHeader(
                AppConstants.DETAIL_MESSAGE, ErrorCode.EC_701.errorMessage());

            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          } catch (Exception e) {
            logger.error("AuthenticationFilter doFilter : (error) ", e);
            setCommonHeaders(httpServletResponse);

            httpServletResponse.setHeader(
                AppConstants.CODE, String.valueOf(ErrorCode.EC_500.code()));
            httpServletResponse.setHeader(
                AppConstants.USER_MESSAGE, ErrorCode.EC_500.errorMessage());
            httpServletResponse.setHeader(AppConstants.TYPE, AppConstants.ERROR_STR);
            httpServletResponse.setHeader(
                AppConstants.DETAIL_MESSAGE, ErrorCode.EC_500.errorMessage());

            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          }
        } else {
          if ((accessToken != null)
              && !StringUtils.isBlank(accessToken)
              && (userId != null)
              && !StringUtils.isBlank(userId)
              && (null != clientToken)
              && !StringUtils.isBlank(clientToken)) {
            CommonService commonService = BeanUtil.getBean(CommonService.class);
            value = commonService.validateAccessToken(userId, accessToken, clientToken);
            if (value != null && value.intValue() == 1) {
              setCommonHeaders(httpServletResponse);
              chain.doFilter(request, response);

            } else {
              if (response instanceof HttpServletResponse) {
                setCommonHeaders(httpServletResponse);
                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              }
            }
          } else {
            setCommonHeaders(httpServletResponse);
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
    httpServletResponse.setHeader(AppConstants.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
    httpServletResponse.setHeader(AppConstants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
    httpServletResponse.setHeader(
        AppConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, AppConstants.TRUE_STR);
    httpServletResponse.setHeader(
        AppConstants.ACCESS_CONTROL_ALLOW_METHODS, AppConstants.HTTP_METHODS);
  }

  @Override
  public void destroy() {
    logger.warn("Destructing filter :{}", this);
  }
}
