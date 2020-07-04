/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.config;

import com.google.cloud.healthcare.fdamystudies.config.BaseAppConfig;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile({"dev", "local", "e2e-test", "test", "qa", "mockit"})
public class AppConfigDevProfile extends BaseAppConfig {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder)
      throws NoSuchAlgorithmException, KeyManagementException {
    // TODO (Dhanya) Temporary fix for
    // javax.net.ssl.SSLHandshakeException: PKIX path building failed:
    // sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid
    // certification path to requested target

    TrustManager[] trustAllCerts =
        new TrustManager[] {
          new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return new X509Certificate[0];
            }

            public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
              // no implementation required
            }

            public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
              // no implementation required
            }
          }
        };
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

    int timeout = 120; // seconds
    RequestConfig config =
        RequestConfig.custom()
            .setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000)
            .setSocketTimeout(timeout * 1000)
            .setRedirectsEnabled(false)
            .build();

    HttpClient httpClient =
        HttpClientBuilder.create()
            .setDefaultRequestConfig(config)
            .setSSLContext(sslContext)
            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
            .disableRedirectHandling()
            .build();

    RestTemplate restTemplate =
        new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
    addInterceptors(restTemplate);
    return restTemplate;
  }
}
