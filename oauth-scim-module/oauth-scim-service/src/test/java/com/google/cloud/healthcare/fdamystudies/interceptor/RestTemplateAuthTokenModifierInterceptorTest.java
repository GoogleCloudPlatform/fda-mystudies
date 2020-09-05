package com.google.cloud.healthcare.fdamystudies.interceptor;

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
  }
}
