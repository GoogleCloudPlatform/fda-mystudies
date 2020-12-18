/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.ErrorResponse;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class FilterChainExceptionHandler extends OncePerRequestFilter {

  private XLogger logger = XLoggerFactory.getXLogger(FilterChainExceptionHandler.class.getName());

  @Value("${cors.allowed.origins:}")
  private String corsAllowedOrigins;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      addCorsHeaders(request, response);
      filterChain.doFilter(request, response);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      ErrorResponse er = new ErrorResponse(e);
      logger.error(
          String.format(
              "%s failed due to RestClientResponseException, response=%s",
              request.getRequestURI(), er),
          e);
      response.setStatus(e.getRawStatusCode());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      JsonNode reponse = JsonUtils.getObjectMapper().convertValue(er, JsonNode.class);
      response.getOutputStream().write(reponse.toString().getBytes());
    } catch (Exception e) {
      logger.error(String.format("%s failed with an exception", request.getRequestURI()), e);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      JsonNode reponse =
          JsonUtils.getObjectMapper().convertValue(ErrorCode.APPLICATION_ERROR, JsonNode.class);
      response.getOutputStream().write(reponse.toString().getBytes());
    }
  }

  private void addCorsHeaders(HttpServletRequest request, HttpServletResponse res) {
    if (StringUtils.isEmpty(corsAllowedOrigins)) {
      return;
    }

    res.setHeader("Access-Control-Allow-Headers", "*");
    res.setHeader("Access-Control-Allow-Methods", "*");

    if (!StringUtils.contains(corsAllowedOrigins, ",")) {
      res.setHeader("Access-Control-Allow-Origin", corsAllowedOrigins);
    } else {
      String[] corsOrgins = corsAllowedOrigins.split(",");
      for (String url : corsOrgins) {
        if (StringUtils.contains(url, request.getHeader("Origin"))) {
          res.setHeader("Access-Control-Allow-Origin", url);
          break;
        }
      }
    }
  }
}
