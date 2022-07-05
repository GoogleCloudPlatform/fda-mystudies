/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcareScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireResponseEntry;
import com.google.cloud.healthcare.fdamystudies.bean.ResponseRows;
import com.google.cloud.healthcare.fdamystudies.bean.SavedActivityResponse;
import com.google.cloud.healthcare.fdamystudies.bean.SearchQuestionnaireResponseFhirBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class getResponsefhirApi {
  @Autowired private ApplicationConfiguration appConfig;
  private XLogger logger = XLoggerFactory.getXLogger(getResponsefhirApi.class.getName());

  public String getfhirResource(String resourceName) throws ProcessResponseException {
    logger.entry("begin getfhirSource()");
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
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
    logger.exit("fhirResourceGet() - Ends ");
    return fhirResponseJson;
  }

  private static String getAccessToken() throws IOException {
    GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));

    return credential.refreshAccessToken().getTokenValue();
  }

  public StoredResponseBean initStoredResponseBean() {
    StoredResponseBean retStoredResponseBean = new StoredResponseBean();
    List<String> schemaNameList = Arrays.asList(AppConstants.RESPONSE_DATA_SCHEMA_NAME_LEGACY);
    retStoredResponseBean.setSchemaName(schemaNameList);
    retStoredResponseBean.setQueryName(AppConstants.RESPONSE_DATA_QUERY_NAME_LEGACY);
    return retStoredResponseBean;
  }

  public StoredResponseBean convertFHIRReesponseDataToBean(
      String participantId,
      SearchQuestionnaireResponseFhirBean searchResponseFhirbean,
      StoredResponseBean storedResponseBean) {
    logger.entry("begin convertResponseDataToBean()");
    List<ResponseRows> responsesList = new ArrayList<>();
    for (QuestionnaireResponseEntry activityResponseMap : searchResponseFhirbean.getEntry()) {
      ResponseRows responsesRow = new ResponseRows();
      // Add participant Id
      Map<Object, Object> mapPartId = new HashMap<>();
      Map<Object, Object> mapPartIdValue = new HashMap<>();
      mapPartIdValue.put(AppConstants.VALUE_KEY_STR, participantId);
      mapPartId.put(AppConstants.PARTICIPANT_ID_RESPONSE, mapPartIdValue);
      responsesRow.getData().add(mapPartId);

      // Add Created Timestamp
      Map<Object, Object> mapTS = new HashMap<>();
      Map<Object, Object> mapTsValue = new HashMap<>();

      // Format timestamp to date
      String timestampFromResponse = null;
      try {
        timestampFromResponse = activityResponseMap.getResource().getAuthored();

        /*DateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.ISO_DATE_FORMAT_RESPONSE);
        String formattedDate = simpleDateFormat.format(timestampFromResponse);*/
        mapTsValue.put(AppConstants.VALUE_KEY_STR, timestampFromResponse);

      } catch (NumberFormatException ne) {
        logger.error(
            "Could not format createdTimestamp field to long. createdTimestamp value is: "
                + timestampFromResponse);
        mapTsValue.put(AppConstants.VALUE_KEY_STR, String.valueOf(timestampFromResponse));
      }

      mapTS.put(AppConstants.CREATED_RESPONSE, mapTsValue);
      responsesRow.getData().add(mapTS);
      SavedActivityResponse savedActivityResponse =
          new Gson().fromJson(new Gson().toJson(activityResponseMap), SavedActivityResponse.class);
      List<Object> results = savedActivityResponse.getResults();
      this.addResponsesToMap(responsesRow, results);
      responsesList.add(responsesRow);
      storedResponseBean.setRows(responsesList);
    }
    if (storedResponseBean.getRows() != null) {
      storedResponseBean.setRowCount(storedResponseBean.getRows().size());
    }
    return storedResponseBean;
  }

  private void addResponsesToMap(ResponseRows responsesRow, List<Object> results) {
    logger.entry("begin addResponsesToMap()");
    if (results != null) {
      for (Object result : results) {
        if (result instanceof Map) {
          Map<String, Object> mapResult = (Map<String, Object>) result;
          String questionResultType = (String) mapResult.get(AppConstants.RESULT_TYPE_KEY);
          String questionIdKey = null;
          String questionValue = null;
          Map<Object, Object> tempMapForQuestions = new HashMap<>();
          Map<Object, Object> tempMapQuestionsValue = new HashMap<>();

          if (!StringUtils.isBlank(questionResultType)) {
            if (questionResultType.equalsIgnoreCase(AppConstants.GROUPED_FIELD_KEY)) {
              Map<String, Object> resultsForm =
                  (Map<String, Object>) mapResult.get("actvityValueGroup");
              List<Object> obj = (List<Object>) resultsForm.get("results");
              this.addResponsesToMap(responsesRow, obj);

            } else {
              questionIdKey = (String) mapResult.get(AppConstants.QUESTION_ID_KEY);
              questionValue = (String) mapResult.get(AppConstants.VALUE_KEY_STR);
              if (StringUtils.containsIgnoreCase(
                      appConfig.getResponseSupportedQTypeDouble(), questionResultType)
                  && !StringUtils.isBlank(questionValue)) {
                Double questionValueDouble = null;
                try {
                  questionValueDouble = Double.parseDouble(questionValue);
                  tempMapQuestionsValue.put(AppConstants.VALUE_KEY_STR, questionValueDouble);
                  tempMapForQuestions.put(questionIdKey, tempMapQuestionsValue);
                  responsesRow.getData().add(tempMapForQuestions);
                } catch (NumberFormatException e) {
                  logger.error(
                      "Could not format value to Double. Value input string is: " + questionValue);
                }
              } else if (StringUtils.containsIgnoreCase(
                      appConfig.getResponseSupportedQTypeDate(), questionResultType)
                  && !StringUtils.isBlank(questionValue)) {
                tempMapQuestionsValue.put(AppConstants.VALUE_KEY_STR, questionValue);
                tempMapForQuestions.put(questionIdKey, tempMapQuestionsValue);
                responsesRow.getData().add(tempMapForQuestions);
              } else {
                if (appConfig.getSupportStringResponse().equalsIgnoreCase(AppConstants.TRUE_STR)
                    && StringUtils.containsIgnoreCase(
                        appConfig.getResponseSupportedQTypeString(), questionResultType)
                    && !StringUtils.isBlank(questionValue)) {
                  tempMapQuestionsValue.put(AppConstants.VALUE_KEY_STR, questionValue);
                  tempMapForQuestions.put(questionIdKey, tempMapQuestionsValue);
                  responsesRow.getData().add(tempMapForQuestions);
                }
              }
            }
          }
        }
      }
    }
  }
}
