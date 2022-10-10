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
import com.google.cloud.healthcare.fdamystudies.bean.Answer;
import com.google.cloud.healthcare.fdamystudies.bean.ItemsQuestionnaireResponse;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireResponseEntry;
import com.google.cloud.healthcare.fdamystudies.bean.ResponseRows;
import com.google.cloud.healthcare.fdamystudies.bean.SavedActivityResponse;
import com.google.cloud.healthcare.fdamystudies.bean.SearchQuestionnaireResponseFhirBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
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
/**
 * To get the Fhir response
 *
 * @author
 */
@Component
public class GetResponsefhirApi {
  @Autowired private ApplicationConfiguration appConfig;
  private XLogger logger = XLoggerFactory.getXLogger(GetResponsefhirApi.class.getName());
  /**
   * To get the fhirResource
   *
   * @param resourceName
   * @return
   * @throws ProcessResponseException
   */
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
  /**
   * Initial StoredResponseBean
   *
   * @return
   */
  public StoredResponseBean initStoredResponseBean() {
    StoredResponseBean retStoredResponseBean = new StoredResponseBean();
    List<String> schemaNameList = Arrays.asList(AppConstants.RESPONSE_DATA_SCHEMA_NAME_LEGACY);
    retStoredResponseBean.setSchemaName(schemaNameList);
    retStoredResponseBean.setQueryName(AppConstants.RESPONSE_DATA_QUERY_NAME_LEGACY);
    return retStoredResponseBean;
  }
  /**
   * convert the FhirResponseData To Bean
   *
   * @param participantId
   * @param fhirBeans
   * @param storedResponseBean
   * @return
   */
  public StoredResponseBean convertFhirResponseDataToBean(
      String participantId,
      List<SearchQuestionnaireResponseFhirBean> fhirBeans,
      StoredResponseBean storedResponseBean) {
    logger.entry("begin convertResponseDataToBean()");
    List<ResponseRows> responsesList = new ArrayList<>();
    for (SearchQuestionnaireResponseFhirBean fhirBean : fhirBeans) {
      for (QuestionnaireResponseEntry activityResponseMap : fhirBean.getEntry()) {
        ResponseRows responsesRow = new ResponseRows();
        // Add participant Id
        Map<Object, Object> mapPartId = new HashMap<>();
        Map<Object, Object> mapPartIdValue = new HashMap<>();
        mapPartIdValue.put(AppConstants.VALUE_KEY_STR, participantId);
        mapPartId.put(AppConstants.PARTICIPANT_ID_RESPONSE, mapPartIdValue);
        responsesRow.getData().add(mapPartId);

        // Add Created Timestamp
        Map<Object, Object> mapTstimestamp = new HashMap<>();
        Map<Object, Object> mapTsValueFortimestamp = new HashMap<>();
        //  Map<Object, Object> mapTsValue = new HashMap<>();

        // Format timestamp to date
        String timestampFromResponse = null;
        long timestampFromResponse1 = 0;
        try {
          timestampFromResponse = activityResponseMap.getResource().getAuthored();
          timestampFromResponse =
              timestampFromResponse.substring(0, timestampFromResponse.length() - 10);
          LocalDateTime localDateTime =
              LocalDateTime.parse(
                  timestampFromResponse, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
          long millis = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
          Date d = new Date(millis);
          ZoneOffset zoneOffset = ZoneOffset.of("+0000");
          TimeZone timezone = TimeZone.getTimeZone(zoneOffset);
          //   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
          //  sdf.setTimeZone(timezone);
          //  String formattedDate = sdf.format(millis);
          // String dtr = sdf.format(localDateTime.parse(d.toString()));
          SimpleDateFormat sdf1 = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
          sdf1.setTimeZone(timezone);
          Date parsedDate = sdf1.parse(d.toString());
          SimpleDateFormat print = new SimpleDateFormat(AppConstants.ISO_DATE_FORMAT_RESPONSE);
          String formattedDate = print.format(parsedDate);
          System.out.println(print.format(parsedDate));
          mapTsValueFortimestamp.put(AppConstants.VALUE_KEY_STR, formattedDate);
          mapTstimestamp.put(AppConstants.CREATED_RESPONSE, mapTsValueFortimestamp);
          responsesRow.getData().add(mapTstimestamp);
        } catch (Exception ne) {
          logger.error(
              "Could not format createdTimestamp field to long. createdTimestamp value is: "
                  + timestampFromResponse);
          // mapTsValue.put(AppConstants.VALUE_KEY_STR, String.valueOf(timestampFromResponse));
        }
        // new structure for responsefhir
        List<ItemsQuestionnaireResponse> items = activityResponseMap.getResource().getItem();
        for (ItemsQuestionnaireResponse item : items) {

          //  Map<Object, Object> mapTs = new HashMap<>();
          List<Answer> answers = item.getAnswer();
          for (Answer answer : answers) {
            Answer answerFromResponse = null;
            boolean b1 = !answer.getItem().isEmpty();
            // for activetask response
            if (!answer.getItem().isEmpty()) {
              List<ItemsQuestionnaireResponse> activetaskitems = answer.getItem();
              for (ItemsQuestionnaireResponse activetaskitem : activetaskitems) {
                Map<Object, Object> mapTs = new HashMap<>();
                answerFromResponse = activetaskitem.getAnswer().get(0);
                if (answerFromResponse.getValueBoolean() != null) {
                  Map<Object, Object> mapTsValue = new HashMap<>();
                  Boolean valueBoolean = answerFromResponse.getValueBoolean();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueBoolean);
                  mapTs.put(activetaskitem.getLinkId(), mapTsValue);
                } else if (answerFromResponse.getValueDate() != null) {
                  Map<Object, Object> mapTsValue = new HashMap<>();
                  String valueDate = answerFromResponse.getValueDate();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDate);
                  mapTs.put(activetaskitem.getLinkId(), mapTsValue);
                } else if (answerFromResponse.getValueDateTime() != null) {
                  Map<Object, Object> mapTsValue = new HashMap<>();
                  String valueDateTime = answerFromResponse.getValueDateTime();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDateTime);
                  mapTs.put(activetaskitem.getLinkId(), mapTsValue);
                } else if (answerFromResponse.getValueDecimal() != null) {
                  Map<Object, Object> mapTsValue = new HashMap<>();
                  Double valueDecimal = answerFromResponse.getValueDecimal();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDecimal);
                  mapTs.put(activetaskitem.getLinkId(), mapTsValue);
                } else if (answerFromResponse.getValueInteger() != null) {
                  Map<Object, Object> mapTsValue = new HashMap<>();
                  Integer valueInteger = answerFromResponse.getValueInteger();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueInteger);
                  mapTs.put(activetaskitem.getLinkId(), mapTsValue);
                } else if (answerFromResponse.getValueString() != null) {
                  Map<Object, Object> mapTsValue = new HashMap<>();
                  String valueString = answerFromResponse.getValueString();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueString);
                  mapTs.put(activetaskitem.getLinkId(), mapTsValue);
                } else if (answerFromResponse.getValueTime() != null) {
                  Map<Object, Object> mapTsValue = new HashMap<>();
                  String valueTime = answerFromResponse.getValueTime();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueTime);
                  mapTs.put(activetaskitem.getLinkId(), mapTsValue);
                }
                responsesRow.getData().add(mapTs);
              }
            } else {
              // for questionnaireresponse
              answerFromResponse = item.getAnswer().get(0);
              Map<Object, Object> mapTs = new HashMap<>();
              Map<Object, Object> mapTsValue = new HashMap<>();

              if (answerFromResponse.getValueBoolean() != null) {
                Boolean valueBoolean = answerFromResponse.getValueBoolean();
                mapTsValue.put(AppConstants.VALUE_KEY_STR, valueBoolean);
                mapTs.put(item.getLinkId(), mapTsValue);
              } else if (answerFromResponse.getValueDate() != null) {
                String valueDate = answerFromResponse.getValueDate();
                mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDate);
                mapTs.put(item.getLinkId(), mapTsValue);
              } else if (answerFromResponse.getValueDateTime() != null) {
                String valueDateTime = answerFromResponse.getValueDateTime();
                mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDateTime);
                mapTs.put(item.getLinkId(), mapTsValue);
              } else if (answerFromResponse.getValueDecimal() != null) {

                Double valueDecimal = answerFromResponse.getValueDecimal();
                mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDecimal);
                mapTs.put(item.getLinkId(), mapTsValue);
              } else if (answerFromResponse.getValueInteger() != null) {
                Integer valueInteger = answerFromResponse.getValueInteger();
                mapTsValue.put(AppConstants.VALUE_KEY_STR, valueInteger);
                mapTs.put(item.getLinkId(), mapTsValue);
              } else if (answerFromResponse.getValueString() != null) {
                String valueString = answerFromResponse.getValueString();
                mapTsValue.put(AppConstants.VALUE_KEY_STR, valueString);
                mapTs.put(item.getLinkId(), mapTsValue);
              } else if (answerFromResponse.getValueTime() != null) {
                String valueTime = answerFromResponse.getValueTime();
                mapTsValue.put(AppConstants.VALUE_KEY_STR, valueTime);
                mapTs.put(item.getLinkId(), mapTsValue);
              }
              responsesRow.getData().add(mapTs);
            }
          }
          // for resource
          boolean b1 = !item.getItem().isEmpty();
          if (!item.getItem().isEmpty()) {
            System.out.println("item:answer is not empty");
            List<ItemsQuestionnaireResponse> resourceitems = item.getItem();
            for (ItemsQuestionnaireResponse resouceItem : resourceitems) {
              Map<Object, Object> mapTs = new HashMap<>();
              if (!resouceItem.getAnswer().isEmpty()) {
                Answer answerFromResouce = resouceItem.getAnswer().get(0);
                // Answer answerFromResouce = resouceItem.getAnswer();
                Map<Object, Object> mapTsValue = new HashMap<>();

                if (answerFromResouce.getValueBoolean() != null) {
                  Boolean valueBoolean = answerFromResouce.getValueBoolean();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueBoolean);
                  mapTs.put(AppConstants.ANCHOR_DATE_VALUE, mapTsValue);
                } else if (answerFromResouce.getValueDate() != null) {
                  String valueDate = answerFromResouce.getValueDate();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDate);
                  mapTs.put(AppConstants.ANCHOR_DATE_VALUE, mapTsValue);
                } else if (answerFromResouce.getValueDateTime() != null) {
                  String valueDateTime = answerFromResouce.getValueDateTime();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDateTime);
                  mapTs.put(AppConstants.ANCHOR_DATE_VALUE, mapTsValue);
                } else if (answerFromResouce.getValueDecimal() != null) {

                  Double valueDecimal = answerFromResouce.getValueDecimal();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueDecimal);
                  mapTs.put(AppConstants.ANCHOR_DATE_VALUE, mapTsValue);
                } else if (answerFromResouce.getValueInteger() != null) {
                  Integer valueInteger = answerFromResouce.getValueInteger();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueInteger);
                  mapTs.put(AppConstants.ANCHOR_DATE_VALUE, mapTsValue);
                } else if (answerFromResouce.getValueString() != null) {
                  String valueString = answerFromResouce.getValueString();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueString);
                  mapTs.put(AppConstants.ANCHOR_DATE_VALUE, mapTsValue);
                } else if (answerFromResouce.getValueTime() != null) {
                  String valueTime = answerFromResouce.getValueTime();
                  mapTsValue.put(AppConstants.VALUE_KEY_STR, valueTime);
                  mapTs.put(AppConstants.ANCHOR_DATE_VALUE, mapTsValue);
                }
                responsesRow.getData().add(mapTs);
              }
            }
          }
        }
        // ends here
        //  responsesRow.getData().add(mapTs);
        SavedActivityResponse savedActivityResponse =
            new Gson()
                .fromJson(new Gson().toJson(activityResponseMap), SavedActivityResponse.class);
        List<Object> results = savedActivityResponse.getResults();
        this.addResponsesToMap(responsesRow, results);
        responsesList.add(responsesRow);
        storedResponseBean.setRows(responsesList);
      }
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
