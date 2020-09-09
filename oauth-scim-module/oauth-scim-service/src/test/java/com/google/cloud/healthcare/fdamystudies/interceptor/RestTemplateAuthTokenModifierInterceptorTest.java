/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.interceptor;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestTemplateAuthTokenModifierInterceptorTest extends BaseMockIT {

  @Autowired RestTemplate restTemplate;

  @Test
  public void shouldReturnSuccess() {
    String url = "http://localhost:8080/restTemplateAuthTokenModifierInterceptor";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", INVALID_BEARER_TOKEN);

    HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);

    ResponseEntity<Void> response =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, Void.class);
    assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));

    verify(
        1,
        getRequestedFor(urlEqualTo("/restTemplateAuthTokenModifierInterceptor"))
            .withHeader("Authorization", equalTo(VALID_BEARER_TOKEN)));
  }
}
