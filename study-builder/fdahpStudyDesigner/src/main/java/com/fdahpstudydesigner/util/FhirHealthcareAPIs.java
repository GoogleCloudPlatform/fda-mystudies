/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.util;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcare.Projects.Locations.Datasets.FhirStores;
import com.google.api.services.healthcare.v1.CloudHealthcareScopes;
import com.google.api.services.healthcare.v1.model.FhirStore;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FhirHealthcareAPIs {
  private XLogger logger = XLoggerFactory.getXLogger(FhirHealthcareAPIs.class.getName());
  public static final JsonFactory JSON_FACTORY = new JacksonFactory();
  public static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

  public void fhirStoreCreate(String datasetName, String fhirStoreId)
      throws GoogleJsonResponseException {
    logger.entry("begin fhirStoreCreate()");

    try {
      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = createClient();

      // Configure the FhirStore to be created.
      Map<String, String> labels = new HashMap<>();
      labels.put("env", "dev");
      String version = "R4";
      FhirStore content = new FhirStore().setLabels(labels).setVersion(version);

      // Create request and configure any parameters.
      FhirStores.Create request =
          client
              .projects()
              .locations()
              .datasets()
              .fhirStores()
              .create(datasetName, content)
              .setFhirStoreId(fhirStoreId);

      // Execute the request and process the results.
      FhirStore response = request.execute();

      logger.debug("FHIR store created: " + response.toPrettyString());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    logger.exit("fhirStoreCreate() - Ends ");
  }

  public void fhirStoreGet(String fhirStoreName) throws Exception {
    logger.entry("begin fhirStoreGet()");
    // Initialize the client, which will be used to interact with the service.
    CloudHealthcare client = createClient();

    // Create request and configure any parameters.
    FhirStores.Get request =
        client.projects().locations().datasets().fhirStores().get(fhirStoreName);

    // Execute the request and process the results.
    FhirStore store = request.execute();

    logger.debug("FHIR store retrieved: " + store.toPrettyString());
    System.out.println(store.toPrettyString());
    logger.exit("fhirStoreGet() - Ends ");
  }

  public String fhirResourceCreate(String fhirStoreName, String resourceType, String requestJson)
      throws GoogleJsonResponseException {

    logger.entry("begin fhirResourceCreate()");
    String fhirResponseJson = null;
    // Initialize the client, which will be used to interact with the service.
    try {

      CloudHealthcare client = createClient();
      HttpClient httpClient = HttpClients.createDefault();
      String uri =
          String.format("%sv1/%s/fhir/%s", client.getRootUrl(), fhirStoreName, resourceType);
      URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());

      StringEntity requestEntity = new StringEntity(requestJson);

      HttpUriRequest request =
          RequestBuilder.post()
              .setUri(uriBuilder.build())
              .setEntity(requestEntity)
              .addHeader("Content-Type", "application/fhir+json")
              .addHeader("Accept-Charset", "utf-8")
              .addHeader("Accept", "application/fhir+json; charset=utf-8")
              .build();

      // Execute the request and process the results.
      HttpResponse response = httpClient.execute(request);
      HttpEntity responseEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
        System.err.print(
            String.format(
                "Exception creating FHIR resource: %s\n", response.getStatusLine().toString()));
        responseEntity.writeTo(System.err);
        logger.debug("Exception creating FHIR resource: " + response.getStatusLine().toString());
        throw new Exception();
      } else {
        if (response.getEntity() != null) {
          fhirResponseJson = EntityUtils.toString(responseEntity);
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    logger.exit("fhirResourceCreate() - Ends ");
    return fhirResponseJson;
  }

  public String fhirResourceUpdate(
      String fhirStoreName, String resourceType, String json, String id)
      throws IOException, URISyntaxException {

    CloudHealthcare client = createClient();
    HttpClient httpClient = HttpClients.createDefault();
    String uri =
        String.format("%sv1/%s/fhir/%s/%s", client.getRootUrl(), fhirStoreName, resourceType, id);
    URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());
    String json1 = null;

    StringEntity requestEntity = new StringEntity(json);

    HttpUriRequest request =
        RequestBuilder.put(uriBuilder.build())
            .setEntity(requestEntity)
            .addHeader("Content-Type", "application/fhir+json")
            .addHeader("Accept-Charset", "utf-8")
            .addHeader("Accept", "application/fhir+json; charset=utf-8")
            .build();

    // Execute the request and process the results.
    HttpResponse response = httpClient.execute(request);
    HttpEntity responseEntity = response.getEntity();
    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
      System.err.print(
          String.format(
              "Exception creating FHIR resource: %s\n", response.getStatusLine().toString()));
      responseEntity.writeTo(System.err);
      // throw new RuntimeException();
    } else {
      if (response.getEntity() != null) {
        json1 = EntityUtils.toString(responseEntity);
      }
    }
    return json1;
  }

  public String fhirResourceSearchPost(String resourceName, String search)
      throws GoogleJsonResponseException {
    logger.entry("begin fhirResofhirResourceSearchPosturceCreate()");
    String responseJson = null;
    try {
      CloudHealthcare client = createClient();

      HttpClient httpClient = HttpClients.createDefault();
      String uri = String.format("%sv1/%s/_search?%s", client.getRootUrl(), resourceName, search);
      URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());
      StringEntity requestEntity = new StringEntity("");

      HttpUriRequest request =
          RequestBuilder.post()
              .setUri(uriBuilder.build())
              .setEntity(requestEntity)
              .addHeader("Content-Type", "application/fhir+json")
              .addHeader("Accept-Charset", "utf-8")
              .addHeader("Accept", "application/fhir+json; charset=utf-8")
              .build();

      // Execute the request and process the results.
      HttpResponse response = httpClient.execute(request);
      HttpEntity responseEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
          logger.debug("HttpStatus code " + response.getStatusLine().getStatusCode());
          return null;
        } else {
          logger.debug(
              "Exception searching POST FHIR resources: " + response.getStatusLine().toString());
          throw new Exception();
        }
      } else {
        if (response.getEntity() != null) {
          responseJson = EntityUtils.toString(responseEntity);
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    logger.exit("fhirResourceSearchPost() - Ends ");
    return responseJson;
  }

  public void fhirResourcePatch(String resourceName, String data)
      throws GoogleJsonResponseException {

    logger.entry("begin fhirResourcePatch()");

    try {
      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = createClient();

      HttpClient httpClient = HttpClients.createDefault();
      String uri = String.format("%sv1/%s", client.getRootUrl(), resourceName);
      URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());
      StringEntity requestEntity = new StringEntity(data);

      HttpUriRequest request =
          RequestBuilder.patch(uriBuilder.build())
              .setEntity(requestEntity)
              .addHeader("Content-Type", "application/json-patch+json")
              .addHeader("Accept-Charset", "utf-8")
              .addHeader("Accept", "application/fhir+json; charset=utf-8")
              .build();

      // Execute the request and process the results.
      HttpResponse response = httpClient.execute(request);
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        logger.debug("Exception patching FHIR resource: " + response.getStatusLine().toString());
        throw new Exception();
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    logger.exit("fhirResourcePatch() - Ends ");
  }

  private static String getAccessToken() throws IOException {
    GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));

    return credential.refreshAccessToken().getTokenValue();
  }

  public String fhirResourceGet(String resourceName) throws GoogleJsonResponseException {
    logger.entry("begin fhirResourceGet()");
    String fhirResponseJson = null;
    // Initialize the client, which will be used to interact with the service.
    try {
      CloudHealthcare client = createClient();
      HttpClient httpClient = HttpClients.createDefault();
      String uri = String.format("%sv1/%s", client.getRootUrl(), resourceName);
      URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());

      HttpUriRequest request = RequestBuilder.get().setUri(uriBuilder.build()).build();

      // Execute the request and process the results.
      HttpResponse response = httpClient.execute(request);
      HttpEntity responseEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        logger.debug(
            "Exception while fetching FHIR resource: " + response.getStatusLine().toString());
        throw new Exception();
      } else {
        if (response.getEntity() != null) {
          fhirResponseJson = EntityUtils.toString(responseEntity);
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
    logger.exit("fhirResourceGet() - Ends ");
    return fhirResponseJson;
  }

  private static CloudHealthcare createClient() throws IOException {
    // Use Application Default Credentials (ADC) to authenticate the requests
    // For more information see https://cloud.google.com/docs/authentication/production
    final GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));

    // Create a HttpRequestInitializer, which will provide a baseline configuration to all requests.
    HttpRequestInitializer requestInitializer =
        new HttpRequestInitializer() {
          @Override
          public void initialize(com.google.api.client.http.HttpRequest httpRequest)
              throws IOException {
            new HttpCredentialsAdapter(credential).initialize(httpRequest);
            httpRequest.setConnectTimeout(60000); // 1 minutes connect timeout
            httpRequest.setReadTimeout(60000); // 1 minutes read timeout
          }
        };

    // Build the client for interacting with the service.
    return new CloudHealthcare.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
        .setApplicationName("your-application-name")
        .build();
  }
}
