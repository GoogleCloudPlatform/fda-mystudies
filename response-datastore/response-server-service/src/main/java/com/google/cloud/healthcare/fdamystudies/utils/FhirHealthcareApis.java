/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcare.Projects.Locations.Datasets.FhirStores;
import com.google.api.services.healthcare.v1.CloudHealthcareScopes;
import com.google.api.services.healthcare.v1.model.FhirStore;
import com.google.api.services.healthcare.v1.model.ListFhirStoresResponse;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

/**
 * FhirHealthcareApis
 *
 * @author
 */
@Component
public class FhirHealthcareApis {
  private XLogger logger = XLoggerFactory.getXLogger(FhirHealthcareApis.class.getName());
  /**
   * To create fhirStore
   *
   * @param datasetName
   * @param fhirStoreId
   * @throws ProcessResponseException
   */
  public void fhirStoreCreate(String datasetName, String fhirStoreId)
      throws ProcessResponseException {
    logger.entry("begin fhirStoreCreate()");

    try {
      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = AppUtil.createClient();

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
      throw new ProcessResponseException(e.getMessage());
    }
    logger.exit("fhirStoreCreate() - Ends ");
  }

  /**
   * To get the fhirStore
   *
   * @param fhirStoreName
   * @throws Exception
   */
  public void fhirStoreGet(String fhirStoreName) throws Exception {
    logger.entry("begin fhirStoreGet()");
    // Initialize the client, which will be used to interact with the service.
    CloudHealthcare client = AppUtil.createClient();

    // Create request and configure any parameters.
    FhirStores.Get request =
        client.projects().locations().datasets().fhirStores().get(fhirStoreName);

    // Execute the request and process the results.
    FhirStore store = request.execute();

    logger.debug("FHIR store retrieved: " + store.toPrettyString());
    System.out.println(store.toPrettyString());
    logger.exit("fhirStoreGet() - Ends ");
  }

  /**
   * To create FHIR Resource
   *
   * @param fhirStoreName
   * @param resourceType
   * @param requestJson
   * @return
   * @throws ProcessResponseException
   */
  public String fhirResourceCreate(String fhirStoreName, String resourceType, String requestJson)
      throws ProcessResponseException {

    logger.entry("begin fhirResourceCreate()");
    String fhirResponseJson = null;
    // Initialize the client, which will be used to interact with the service.
    try {
      CloudHealthcare client = AppUtil.createClient();
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
        logger.debug("Exception creating FHIR resource: " + response.getStatusLine().toString());
        throw new Exception();
      } else {
        if (response.getEntity() != null) {
          fhirResponseJson = EntityUtils.toString(responseEntity);
        }
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    logger.exit("fhirResourceCreate() - Ends ");
    return fhirResponseJson;
  }

  public String fhirResourceSearchPost(String resourceName, String search)
      throws ProcessResponseException {
    logger.entry("begin fhirResofhirResourceSearchPosturceCreate()");
    String responseJson = null;
    try {
      CloudHealthcare client = AppUtil.createClient();

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
        }
        //        else {
        //          logger.debug(
        //              "Exception searching POST FHIR resources: " +
        // response.getStatusLine().toString());
        //          throw new Exception();
        //        }
      } else {
        if (response.getEntity() != null) {
          responseJson = EntityUtils.toString(responseEntity);
        }
      }
    } catch (Exception e) {
      logger.error("fhirResourceSearchPost() - Error ", e);
    }
    logger.exit("fhirResourceSearchPost() - Ends ");
    return responseJson;
  }

  public String fhirResourceGetHistory(String resourceName) throws ProcessResponseException {
    logger.entry("begin fhirResourceGetHistory()");
    String responseJson = null;
    try {
      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = AppUtil.createClient();

      HttpClient httpClient = HttpClients.createDefault();
      String uri = String.format("%sv1/%s/_history", client.getRootUrl(), resourceName);
      URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());

      HttpUriRequest request =
          RequestBuilder.get()
              .setUri(uriBuilder.build())
              .addHeader("Content-Type", "application/fhir+json")
              .addHeader("Accept-Charset", "utf-8")
              .addHeader("Accept", "application/fhir+json; charset=utf-8")
              .build();

      // Execute the request and process the results.
      HttpResponse response = httpClient.execute(request);
      HttpEntity responseEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        logger.debug(
            "Exception while fetching FHIR resource history version: "
                + response.getStatusLine().toString());
        throw new Exception();
      } else {
        if (response.getEntity() != null) {
          responseJson = EntityUtils.toString(responseEntity);
        }
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
    logger.exit("fhirResourceGetHistory() - Ends ");
    return responseJson;
  }

  public void fhirResourcePatch(String resourceName, String data) throws ProcessResponseException {

    logger.entry("begin fhirResourcePatch()");

    try {
      // Initialize the client, which will be used to interact with the service.
      CloudHealthcare client = AppUtil.createClient();

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
        // throw new Exception();
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      // throw new ProcessResponseException(e.getMessage());
    }
    logger.exit("fhirResourcePatch() - Ends ");
  }

  public List<FhirStore> fhirStoreGetList(String fhirStoreName) throws Exception {

    logger.entry("begin fhirStoreGetList()");
    // Initialize the client, which will be used to interact with the service.
    CloudHealthcare client = AppUtil.createClient();

    // Create request and configure any parameters.
    FhirStores.List request =
        client.projects().locations().datasets().fhirStores().list(fhirStoreName);

    // Execute the request and process the results.
    ListFhirStoresResponse store = request.execute();
    List<FhirStore> fhirStores = store.getFhirStores();

    logger.debug("FHIR store retrieved: " + store.toPrettyString());
    logger.exit("fhirStoreGetList() - Ends ");
    return fhirStores;
  }

  private static String getAccessToken() throws IOException {
    GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));

    return credential.refreshAccessToken().getTokenValue();
  }

  public String fhirResourceGet(String resourceName) throws ProcessResponseException {
    logger.entry("begin fhirResourceGet()");
    String fhirResponseJson = null;
    // Initialize the client, which will be used to interact with the service.
    try {
      CloudHealthcare client = AppUtil.createClient();
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
      logger.error("error  fhirResourceGet(): " + e.getMessage(), e);
      //  throw new ProcessResponseException(e.getMessage());
    }
    logger.exit("fhirResourceGet() - Ends ");
    return fhirResponseJson;
  }

  public void fhirResourceDelete(String resourceName) throws ProcessResponseException {
    // Initialize the client, which will be used to interact with the service.

    logger.entry("fhirResourceDelete() - starts ");
    try {
      CloudHealthcare client = AppUtil.createClient();

      HttpClient httpClient = HttpClients.createDefault();
      String uri = String.format("%sv1/%s", client.getRootUrl(), resourceName);
      URIBuilder uriBuilder = new URIBuilder(uri).setParameter("access_token", getAccessToken());

      HttpUriRequest request =
          RequestBuilder.delete()
              .setUri(uriBuilder.build())
              .addHeader("Content-Type", "application/fhir+json")
              .addHeader("Accept-Charset", "utf-8")
              .addHeader("Accept", "application/fhir+json; charset=utf-8")
              .build();

      // Execute the request and process the results.
      // Regardless of whether the operation succeeds or
      // fails, the server returns a 200 OK HTTP status code. To check that the
      // resource was successfully deleted, search for or get the resource and
      // see if it exists.
      HttpResponse response = httpClient.execute(request);
      HttpEntity responseEntity = response.getEntity();
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        logger.debug(
            "Exception while deleting FHIR resource: " + response.getStatusLine().toString());
        // throw new Exception();
      }
    } catch (Exception e) {
      logger.error("fhirResourceDelete() - Error: " + e.getMessage(), e);
      // throw new ProcessResponseException(e.getMessage());
    }
    logger.exit("fhirResourceDelete() - Ends ");
  }

}
