/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.interceptor;

import com.google.cloud.healthcare.fdamystudies.service.OAuthService;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Component
public class RestTemplateAuthTokenModifierInterceptor implements ClientHttpRequestInterceptor {

  @Autowired private OAuthService oauthService;

  @Override
  public ClientHttpResponse intercept(
      HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    ClientHttpResponse response = execution.execute(request, body);
    if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
      String auth = request.getHeaders().getFirst("Authorization");
      if (StringUtils.startsWith(auth, "Bearer")) {
        request.getHeaders().set("Authorization", "Bearer " + oauthService.getNewAccessToken());
        return execution.execute(request, body);
      }
    }
    return response;
  }
}
