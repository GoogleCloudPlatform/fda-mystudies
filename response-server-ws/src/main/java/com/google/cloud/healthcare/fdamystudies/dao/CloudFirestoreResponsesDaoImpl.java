/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.healthcare.fdamystudies.bean.ResponseRows;
import com.google.cloud.healthcare.fdamystudies.bean.SavedActivityResponse;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import com.google.gson.Gson;

@Repository
@Qualifier("cloudFirestoreResponsesDaoImpl")
public class CloudFirestoreResponsesDaoImpl implements ResponsesDao {
  @Autowired
  private ApplicationConfiguration appConfig;
  private Firestore responsesDb;
  private static final Logger logger =
      LoggerFactory.getLogger(CloudFirestoreResponsesDaoImpl.class);

  @Override
  @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
  public void saveStudyMetadata(String studyCollectionName, String studyId,
      Map<String, Object> dataToStore) throws ProcessResponseException {
    if (studyCollectionName != null && studyId != null && dataToStore != null) {
      try {

        logger.info("saveStudyMetadata() : \n Study Collection Name: " + studyCollectionName);
        initializeFirestore();
        ApiFuture<WriteResult> cr =
            this.responsesDb.collection(studyCollectionName).document(studyId).set(dataToStore);
        logger.debug("saveStudyMetadata() : \n Study Collection Name: " + studyCollectionName
            + " added successfully");

      } catch (Exception e) {
        logger.error(e.getMessage(), e);
        throw new ProcessResponseException(e.getMessage());
      }
    } else {

      throw new ProcessResponseException(
          "CloudFirestoreResponsesDaoImpl.saveStudyMetadata() - Study Collection is null or dataToStore is null");
    }
  }

  @Override
  @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
  public void saveActivityResponseData(String studyId, String studyCollectionName,
      String participantCollectionName, String activitiesCollectionName,
      Map<String, Object> dataToStoreParticipantCollectionMap,
      Map<String, Object> dataToStoreActivityResults) throws ProcessResponseException {
    try {
      initializeFirestore();
      String participantId =
          (String) dataToStoreParticipantCollectionMap.get(AppConstants.PARTICIPANT_ID_KEY);
      DocumentReference partColl = this.responsesDb.collection(studyCollectionName)
          .document(studyId).collection(participantCollectionName).document(participantId);
      List<ApiFuture<WriteResult>> futures = new ArrayList<>();
      boolean dr2 = futures.add(partColl.set(dataToStoreParticipantCollectionMap));
      if (dr2) {
        partColl.collection(activitiesCollectionName).document().set(dataToStoreActivityResults);
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
  }



  @Override
  public StoredResponseBean getActivityResponseDataForParticipant(String studyCollectionName,
      String studyId, String siteId, String participantId, String activityId)
      throws ProcessResponseException {
    try {
      initializeFirestore();
      // Firestore does not allow compound queries without creating an index. Indexes can be created
      // only through the console or CLI, not programmatically. So this method will not depend on
      // the
      // index to sort the data
      // based on timestamp in firestore. It will do the sort on the query result object

      final Query query = responsesDb.collection(studyCollectionName).document(studyId)
          .collection(AppUtil.makeParticipantCollectionName(studyId, siteId))
          .document(participantId).collection(AppUtil.makeActivitiesCollectionName(studyId, siteId))
          .whereEqualTo("activityId", activityId);

      final ApiFuture<QuerySnapshot> querySnapshot = query.get();
      List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();
      StoredResponseBean storedResponseBean = initStoredResponseBean();
      List<Map<String, Object>> activityResponseMapList = new ArrayList<Map<String, Object>>();
      for (QueryDocumentSnapshot document : documents) {
        Map<String, Object> mapObj = document.getData();
        activityResponseMapList.add(mapObj);
      }

      if (activityResponseMapList.size() > 0) {
        String lastResponseOnly = appConfig.getLastResponseOnly();

        if (!StringUtils.isBlank(lastResponseOnly)
            && lastResponseOnly.equalsIgnoreCase(AppConstants.TRUE_STR)) {
          activityResponseMapList = filterResponseListByTimestamp(activityResponseMapList);
        }
        storedResponseBean = convertResponseDataToBean(activityResponseMapList, storedResponseBean);
        return storedResponseBean;
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
    return null;
  }


  private void initializeFirestore() {
    if (this.responsesDb == null) {
      logger.debug("In CloudFirestoreResponsesDaoImpl constructor, initializing Firestore");
      FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
          .setProjectId(appConfig.getFirestoreProjectId()).build();
      logger.debug("In CloudFirestoreResponsesDaoImpl constructor, Firestore Options initialized");
      this.responsesDb = firestoreOptions.getService();
      logger.debug("In CloudFirestoreResponsesDaoImpl constructor, Firestore DB initialized");
    }
  }

  private StoredResponseBean convertResponseDataToBean(
      List<Map<String, Object>> activityResponseMapList, StoredResponseBean storedResponseBean) {

    List<Map<String, Object>> responsesList = new ArrayList<Map<String, Object>>();
    for (Map<String, Object> activityResponseMap : activityResponseMapList) {
      SavedActivityResponse savedActivityResponse =
          new Gson().fromJson(new Gson().toJson(activityResponseMap), SavedActivityResponse.class);
      // Add participant Id
      Map<String, Object> mapPartId = new HashMap<String, Object>();
      Map<String, String> mapPartIdValue = new HashMap<String, String>();
      mapPartIdValue.put(AppConstants.VALUE_KEY_STR,
          (String) activityResponseMap.get(AppConstants.PARTICIPANT_ID_KEY));
      mapPartId.put(AppConstants.PARTICIPANT_ID_RESPONSE, mapPartIdValue);
      responsesList.add(mapPartId);

      // Add Created Timestamp
      Map<String, Object> mapTS = new HashMap<String, Object>();
      Map<String, String> mapTSValue = new HashMap<String, String>();

      // Format timestamp to date
      long timestampFromResponse = 0;
      try {
        timestampFromResponse =
            Long.parseLong((String) activityResponseMap.get(AppConstants.CREATED_TS_KEY));
        DateFormat simpleDateFormat = new SimpleDateFormat(AppConstants.DATE_FORMAT_RESPONSE);
        String formattedDate = simpleDateFormat.format(timestampFromResponse);
        mapTSValue.put(AppConstants.VALUE_KEY_STR, formattedDate);

      } catch (NumberFormatException ne) {
        logger.error("Could not format createdTimestamp field to long. createdTimestamp value is: "
            + timestampFromResponse);
        mapTSValue.put(AppConstants.VALUE_KEY_STR, String.valueOf(timestampFromResponse));
      }

      mapTS.put(AppConstants.CREATED_RESPONSE, mapTSValue);

      responsesList.add(mapTS);

      List<Object> results = savedActivityResponse.getResults();
      for (Object result : results) {
        if (result instanceof Map) {
          String questionResultType = (String) ((Map) result).get(AppConstants.RESULT_TYPE_KEY);
          if (!StringUtils.isBlank(questionResultType) && StringUtils
              .containsIgnoreCase(appConfig.getResponseSupportedQType(), questionResultType)) {
            String questionIdKey = (String) ((Map) result).get(AppConstants.QUESTION_ID_KEY);
            String questionValue = (String) ((Map) result).get(AppConstants.VALUE_KEY_STR);
            Map<String, Object> tempMapForQuestions = new HashMap<String, Object>();
            Map<String, String> tempMapQuestionsValue = new HashMap<String, String>();
            tempMapQuestionsValue.put(AppConstants.VALUE_KEY_STR, questionValue);
            tempMapForQuestions.put(questionIdKey, tempMapQuestionsValue);
            responsesList.add(tempMapForQuestions);
          }
        }
      }
      ResponseRows responseRows = new ResponseRows();
      responseRows.setData(responsesList);
      storedResponseBean.setRows(responseRows);
    }
    if (storedResponseBean.getRows() != null && storedResponseBean.getRows().getData() != null) {
      storedResponseBean.setRowCount(storedResponseBean.getRows().getData().size());
    }
    // TEST CODE - BEGIN
    String jsonStr = new Gson().toJson(storedResponseBean);
    logger.debug(jsonStr);
    // TEST CODE - END
    return storedResponseBean;
  }

  private List<Map<String, Object>> filterResponseListByTimestamp(
      List<Map<String, Object>> activityResponseMapList) {

    activityResponseMapList.sort(Comparator.nullsLast(
        Comparator.comparing(m -> Long.parseLong((String) m.get(AppConstants.CREATED_TS_KEY)),
            Comparator.nullsLast(Comparator.reverseOrder()))));
    // Get the latest response for activityId, bases on ordering by timestamp value
    activityResponseMapList = Arrays.asList(activityResponseMapList.get(0));

    return activityResponseMapList;
  }

  private StoredResponseBean initStoredResponseBean() {
    StoredResponseBean retStoredResponseBean = new StoredResponseBean();
    List<String> schemaNameList = Arrays.asList(AppConstants.RESPONSE_DATA_SCHEMA_NAME_LEGACY);
    retStoredResponseBean.setSchemaName(schemaNameList);
    retStoredResponseBean.setQueryName(AppConstants.RESPONSE_DATA_QUERY_NAME_LEGACY);
    return retStoredResponseBean;
  }


}
