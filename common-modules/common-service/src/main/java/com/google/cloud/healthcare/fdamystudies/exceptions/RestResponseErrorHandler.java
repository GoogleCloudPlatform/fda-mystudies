package com.google.cloud.healthcare.fdamystudies.exceptions;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.jayway.jsonpath.JsonPath;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

public class RestResponseErrorHandler implements ResponseErrorHandler {
  private XLogger logger = XLoggerFactory.getXLogger(RestResponseErrorHandler.class.getName());

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return new DefaultResponseErrorHandler().hasError(response);
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    ErrorCode errorCode = null;
    HttpHeaders headers = response.getHeaders();
    String responseBody = IOUtils.toString(response.getBody(), StandardCharsets.UTF_8.name());
    if (response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
      // handle 5xx errors
      errorCode = ErrorCode.APPLICATION_ERROR;
    } else if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
      errorCode = ErrorCode.UNAUTHORIZED;
    } else if (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
        && StringUtils.containsIgnoreCase(headers.getFirst("Content-Type"), "json")
        && StringUtils.contains(responseBody, "error_code")
        && StringUtils.contains(responseBody, "error_description")) {
      // handle 4xx errors
      String code = JsonPath.read(responseBody, "$.error_code");
      String description = JsonPath.read(responseBody, "$.error_description");
      errorCode = ErrorCode.fromCodeAndDescription(code, description);
    }

    errorCode = errorCode == null ? ErrorCode.APPLICATION_ERROR : errorCode;

    logger.error(
        String.format(
            "HTTP request using the RestTemplate failed with status=%d, Content-Type=%s, errorCode=%s and responseBody=%s",
            response.getRawStatusCode(),
            headers.getFirst("Content-Type"),
            errorCode,
            responseBody));
    throw new ErrorCodeException(errorCode);
  }
}
