/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.io.IOException;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonSerialize(using = ErrorResponse.ErrorResponseSerializer.class)
public class ErrorResponse {

  private XLogger logger = XLoggerFactory.getXLogger(ErrorResponse.class.getName());

  private String errorDescription;

  private String errorType;

  private int status;

  private long timestamp = Instant.now().toEpochMilli();

  public ErrorResponse(RestClientResponseException restClientResponseException) {
    populateErrorFields(restClientResponseException);
  }

  private void populateErrorFields(RestClientResponseException restClientResponseException) {
    status = restClientResponseException.getRawStatusCode();
    errorType = restClientResponseException.getClass().getSimpleName();
    errorDescription = restClientResponseException.getResponseBodyAsString();

    // tomcat sets response body as html
    if (StringUtils.containsIgnoreCase(errorDescription, "html")) {
      errorDescription = extractMessageFromHtml(errorDescription);
    }
    errorDescription =
        StringUtils.defaultIfEmpty(errorDescription, restClientResponseException.getMessage());
  }

  private String extractMessageFromHtml(String html) {
    logger.entry(String.format("begin extractMessageFromHtml() with html %n%s", html));
    Document doc = Jsoup.parse(html);
    StringBuilder b = new StringBuilder();
    // select <p> elements and iterate
    doc.select("p")
        .forEach(
            e -> {
              if (StringUtils.contains(e.text(), "Message")
                  || StringUtils.contains(e.text(), "Description")) {
                String text = e.text().substring(e.text().indexOf(StringUtils.SPACE)).trim();
                b.append(text);
                if (!StringUtils.endsWith(text, ".")) {
                  b.append(". ");
                }
              }
            });
    String value = b.toString().trim();
    logger.exit(value);
    return value;
  }

  static class ErrorResponseSerializer extends StdSerializer<ErrorResponse> {

    private static final long serialVersionUID = 1L;

    public ErrorResponseSerializer() {
      super(ErrorResponse.class);
    }

    @Override
    public void serialize(
        ErrorResponse errorResponse,
        JsonGenerator jsonGenerator,
        SerializerProvider serializerProvider)
        throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeNumberField("status", errorResponse.status);
      jsonGenerator.writeStringField("error_type", errorResponse.errorType);
      jsonGenerator.writeNumberField("timestamp", errorResponse.timestamp);
      jsonGenerator.writeStringField("error_description", errorResponse.errorDescription);
      jsonGenerator.writeEndObject();
    }
  }
}
