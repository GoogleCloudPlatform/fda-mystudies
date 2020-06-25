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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
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

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
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
}
