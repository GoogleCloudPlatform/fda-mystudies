/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_ID;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_TYPE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVITY_VERSION;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.RUN_ID;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_METADATA_CONJOINED_WITH_RESPONSE_DATA;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_METADATA_CONJOINING_WITH_RESPONSE_DATA_FAILED;

import com.google.cloud.healthcare.fdamystudies.bean.ActivityMetadataBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityValueGroupBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStepsBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ResponseServerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.ResponsesDao;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ActivityResponseProcessorServiceImpl implements ActivityResponseProcessorService {
  @Autowired
  @Qualifier("cloudFirestoreResponsesDaoImpl")
  private ResponsesDao responsesDao;

  @Autowired private ApplicationConfiguration appConfig;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  private XLogger logger =
      XLoggerFactory.getXLogger(ActivityResponseProcessorServiceImpl.class.getName());

  @Override
  public void saveActivityResponseDataForParticipant(
      QuestionnaireActivityStructureBean activityMetadataBeanFromWcp,
      ActivityResponseBean questionnaireActivityResponseBean,
      AuditLogEventRequest auditRequest)
      throws Exception {
    logger.entry("begin saveActivityResponseDataForParticipant()");
    if (activityMetadataBeanFromWcp == null) {
      throw new ProcessResponseException("QuestionnaireActivityStructureBean is null.");
    }
    if (questionnaireActivityResponseBean == null) {
      throw new ProcessResponseException("QuestionnaireActivityResponseBean is null.");
    }
    ActivityMetadataBean activityMetadataResponse = questionnaireActivityResponseBean.getMetadata();
    if (activityMetadataResponse == null) {
      throw new ProcessResponseException("ActivityMetadataBean is null ");
    }
    List<QuestionnaireActivityStepsBean> questionnaireResponses =
        questionnaireActivityResponseBean.getData().getResults();
    if (questionnaireResponses == null) {
      throw new ProcessResponseException(
          "QuestionnaireActivityResponseBean is null for activity Id: "
              + activityMetadataResponse.getActivityId());
    }

    List<QuestionnaireActivityStepsBean> questionnaireMetadata =
        activityMetadataBeanFromWcp.getSteps();
    if (questionnaireMetadata == null) {
      throw new ProcessResponseException(
          "QuestionnaireActivityStructureBean is null for activity Id: "
              + activityMetadataResponse.getActivityId());
    }
    if (activityMetadataResponse
        .getActivityId()
        .equalsIgnoreCase(activityMetadataBeanFromWcp.getMetadata().getActivityId())) {
      processActivityResponses(questionnaireResponses, questionnaireMetadata);
      Map<String, String> map = new HashedMap<>();
      map.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
      map.put(ACTIVITY_ID, activityMetadataBeanFromWcp.getMetadata().getActivityId());
      map.put(ACTIVITY_VERSION, activityMetadataBeanFromWcp.getMetadata().getVersion());
      map.put(RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
      responseServerAuditLogHelper.logEvent(
          ACTIVITY_METADATA_CONJOINED_WITH_RESPONSE_DATA, auditRequest, map);
      String rawResponseData = null;
      if (appConfig.getSaveRawResponseData().equalsIgnoreCase(AppConstants.TRUE_STR)) {
        rawResponseData = getRawJsonInputData(questionnaireActivityResponseBean);
      }
      this.saveActivityResponseData(questionnaireActivityResponseBean, rawResponseData);
    } else {
      logger.error(
          "saveActivityResponseDataForParticipant() - "
              + "The activity ID in the response does not match"
              + " activity ID in the metadata provided.\n"
              + "Activity Id in response: "
              + activityMetadataResponse.getActivityId()
              + "\n"
              + "Activity Id in metadata: "
              + activityMetadataBeanFromWcp.getMetadata().getActivityId());
      Map<String, String> map = new HashedMap<>();
      map.put(ACTIVITY_TYPE, questionnaireActivityResponseBean.getType());
      map.put(ACTIVITY_ID, activityMetadataBeanFromWcp.getMetadata().getActivityId());
      map.put(ACTIVITY_VERSION, activityMetadataBeanFromWcp.getMetadata().getVersion());
      map.put(RUN_ID, questionnaireActivityResponseBean.getMetadata().getActivityRunId());
      responseServerAuditLogHelper.logEvent(
          ACTIVITY_METADATA_CONJOINING_WITH_RESPONSE_DATA_FAILED, auditRequest, map);
      throw new ProcessResponseException(
          "The activity ID in the response does not match activity ID in the metadata provided.");
    }
    logger.exit("saveActivityResponseDataForParticipant() - ends ");
  }

  @Override
  public StoredResponseBean getActivityResponseDataForParticipant(
      String studyId, String siteId, String participantId, String activityId, String questionKey)
      throws ProcessResponseException {
    if (StringUtils.isBlank(studyId)) {
      throw new ProcessResponseException(
          "getActivityResponseDataForParticipant() method: Study Id argument is null or empty.");
    }
    String studyCollectionName = AppUtil.makeStudyCollectionName(studyId);
    return responsesDao.getActivityResponseDataForParticipant(
        studyCollectionName, studyId, siteId, participantId, activityId, questionKey);
  }

  @Override
  public void updateWithdrawalStatusForParticipant(String studyId, String participantId)
      throws ProcessResponseException {
    if (StringUtils.isBlank(studyId) || StringUtils.isBlank(participantId)) {
      throw new ProcessResponseException(
          "updateWithdrawalStatusForParticipant() method: "
              + "Study Id argument or Participant Id argument is null or empty.");
    }
    String studyCollectionName = AppUtil.makeStudyCollectionName(studyId);
    responsesDao.updateWithdrawalStatusForParticipant(studyCollectionName, studyId, participantId);
  }

  private void processActivityResponses(
      List<QuestionnaireActivityStepsBean> questionnaireResponses,
      List<QuestionnaireActivityStepsBean> activityMetadataBeanFromWcp) {
    QuestionnaireActivityStepsBean scoreSumResponseBean = null;
    for (QuestionnaireActivityStepsBean responseBean : questionnaireResponses) {
      if (responseBean.getKey().equals(AppConstants.DUMMY_SUM_QUESTION_KEY)) {
        scoreSumResponseBean = responseBean;
      }
      if (responseBean.getResultType().equalsIgnoreCase(AppConstants.GROUPED_FIELD_KEY)) {
        ActivityValueGroupBean valueGroupResponse =
            getValueGroupResponses(activityMetadataBeanFromWcp, responseBean);
        responseBean.setActvityValueGroup(valueGroupResponse);
        // Remove the value object, as we have plugged in the metadata and added to the
        // ActivityValueGroupBean
        responseBean.setValue(AppConstants.EMPTY_STR);
      } else {
        plugInMetadataToResponses(activityMetadataBeanFromWcp, responseBean, false);
      }
    }
    // We might want to hide the dummy sum question from users with conditional branching,
    // which will cause response for it
    // to be absent.
    if (scoreSumResponseBean == null) {
      // Try to create a response for the dummy sum question by copying from metadata.
      scoreSumResponseBean = maybeCreateDummySumResponseFromMetadata(activityMetadataBeanFromWcp);
      if (scoreSumResponseBean != null) {
        // If copying is successful, add it to the list of responses.
        questionnaireResponses.add(scoreSumResponseBean);
      }
    }
    if (scoreSumResponseBean != null) {
      // Iterate through responses for a second pass to calculate the score sum
      // if the dummy sum question presents.
      calculateScoreSum(questionnaireResponses, scoreSumResponseBean);
    }
  }

  // Returns an empty response with metadata copied from the dummy sum question, or
  // null if the dummy sum question is not found in metadata.
  private static QuestionnaireActivityStepsBean maybeCreateDummySumResponseFromMetadata(
      List<QuestionnaireActivityStepsBean> activityMetadataBeanFromWcp) {
    List<QuestionnaireActivityStepsBean> metadataMatchList =
        activityMetadataBeanFromWcp
            .stream()
            .filter(
                QuestionnaireActivityStepsBeanPredicate.questionKeyMatch(
                    AppConstants.DUMMY_SUM_QUESTION_KEY))
            .collect(Collectors.<QuestionnaireActivityStepsBean>toList());
    // Return null if dummy sum question is not found from metadata.
    if (metadataMatchList == null || metadataMatchList.size() != 1) {
      return null;
    }
    // Otherwise, create a new entry and copy contents from metadata.
    QuestionnaireActivityStepsBean responseBean = new QuestionnaireActivityStepsBean();
    QuestionnaireActivityStepsBean metadataMatchBean = metadataMatchList.get(0);
    responseBean.setResultType(metadataMatchBean.getResultType());
    responseBean.setKey(metadataMatchBean.getKey());
    responseBean.setSkippable(metadataMatchBean.getSkippable());
    responseBean.setRepeatable(metadataMatchBean.getRepeatable());
    responseBean.setSkipped(false);
    responseBean.setText(metadataMatchBean.getText());
    responseBean.setTitle(metadataMatchBean.getTitle());
    return responseBean;
  }

  // Converts one response value to double in a best-effort manner. Returns 0 if conversion fails.
  private double convertResponseValueToDouble(Object value) {
    if (value instanceof Double) {
      return ((Double) value).doubleValue();
    } else if (value instanceof Integer) {
      return ((Integer) value).doubleValue();
    } else if (value instanceof String) {
      return Double.parseDouble((String) value);
    } else {
      logger.error(
          "convertResponseValueToDouble() - "
              + "Unhandled value type: "
              + value.getClass().getName());
    }
    return 0;
  }

  // Calculates score sum in questionnaireResponses and store it
  // to the value of scoreSumRespnoseBean.
  private void calculateScoreSum(
      List<QuestionnaireActivityStepsBean> questionnaireResponses,
      QuestionnaireActivityStepsBean scoreSumResponseBean) {
    double sum = 0;
    for (QuestionnaireActivityStepsBean responseBean : questionnaireResponses) {
      if (responseBean == scoreSumResponseBean) {
        continue;
      }
      Object value = responseBean.getValue();
      // If the response value type is a list, iterate through all items and add up.
      if (value instanceof List) {
        List<Object> valueList = (ArrayList<Object>) value;
        for (Object o : valueList) {
          sum = sum + convertResponseValueToDouble(o);
        }
      } else {
        // Otherwise, just convert the single response value to double.
        sum = sum + convertResponseValueToDouble(value);
      }
    }
    scoreSumResponseBean.setValue(new Double(sum));
  }

  private ActivityValueGroupBean getValueGroupResponses(
      List<QuestionnaireActivityStepsBean> activityMetadataBeanFromWcp,
      QuestionnaireActivityStepsBean responseBean) {
    ActivityValueGroupBean activityValueGroupBeanRet = new ActivityValueGroupBean();
    List<QuestionnaireActivityStepsBean> valueResponseBeanList =
        new ArrayList<QuestionnaireActivityStepsBean>();
    Object value = responseBean.getValue();
    if (value instanceof List) {
      List<Object> valueList = (ArrayList<Object>) value;
      for (int i = 0; i < valueList.size(); i++) {
        Object valuObj = valueList.get(i);
        if (valuObj instanceof List) {
          List<HashMap> objListMap = (ArrayList<HashMap>) valuObj;
          Gson gson = new Gson();
          for (HashMap valueObjMap : objListMap) {
            String json = gson.toJson(valueObjMap, Map.class);
            QuestionnaireActivityStepsBean valueBean =
                gson.fromJson(json, QuestionnaireActivityStepsBean.class);
            plugInMetadataToResponses(activityMetadataBeanFromWcp, valueBean, true);
            valueResponseBeanList.add(valueBean);
          }
        } else {
          if (valuObj instanceof Map) {
            Map<String, Object> valueObjMap = (HashMap<String, Object>) valuObj;
            Gson gson = new Gson();
            String json = gson.toJson(valueObjMap, Map.class);
            QuestionnaireActivityStepsBean valueBean =
                gson.fromJson(json, QuestionnaireActivityStepsBean.class);
            plugInMetadataToResponses(activityMetadataBeanFromWcp, valueBean, true);
            valueResponseBeanList.add(valueBean);
          }
        }
      }
    }
    activityValueGroupBeanRet.setValueGroup(valueResponseBeanList);
    return activityValueGroupBeanRet;
  }

  private void plugInMetadataToResponses(
      List<QuestionnaireActivityStepsBean> activityMetadataBeanFromWcp,
      QuestionnaireActivityStepsBean responseBean,
      boolean fromGrouped) {
    List<QuestionnaireActivityStepsBean> metadataMatchList = null;

    String questionKey = responseBean.getKey();
    if (fromGrouped) {
      for (QuestionnaireActivityStepsBean stepBean : activityMetadataBeanFromWcp) {
        List<QuestionnaireActivityStepsBean> stepsBean = stepBean.getSteps();
        metadataMatchList =
            stepsBean
                .stream()
                .filter(QuestionnaireActivityStepsBeanPredicate.questionKeyMatch(questionKey))
                .collect(Collectors.<QuestionnaireActivityStepsBean>toList());
        if (metadataMatchList != null && metadataMatchList.size() > 0) {
          break;
        }
      }
    } else {
      metadataMatchList =
          activityMetadataBeanFromWcp
              .stream()
              .filter(QuestionnaireActivityStepsBeanPredicate.questionKeyMatch(questionKey))
              .collect(Collectors.<QuestionnaireActivityStepsBean>toList());
    }
    if (metadataMatchList != null) {
      for (QuestionnaireActivityStepsBean metadataMatchBean : metadataMatchList) {
        responseBean.setSkippable(metadataMatchBean.getSkippable());
        responseBean.setText(metadataMatchBean.getText());
        responseBean.setTitle(metadataMatchBean.getTitle());
      }
    }
  }

  private void saveActivityResponseData(
      ActivityResponseBean questionnaireActivityResponseBean, String rawResponseData)
      throws Exception {
    logger.entry("begin saveActivityResponseData()");
    // Add Timestamp to bean
    questionnaireActivityResponseBean.setCreatedTimestamp(
        String.valueOf(System.currentTimeMillis()));
    Map<String, Object> dataToStoreActivityResults =
        this.getHashMapForBean(questionnaireActivityResponseBean.getMetadata());
    dataToStoreActivityResults.remove(AppConstants.DATA_FIELD_KEY);

    List<QuestionnaireActivityStepsBean> questionnaireResponses =
        questionnaireActivityResponseBean.getData().getResults();
    List<Map<String, Object>> stepsList = new ArrayList<Map<String, Object>>();
    for (QuestionnaireActivityStepsBean tmpBean : questionnaireResponses) {
      Map<String, Object> dataToStoreTemp = getHashMapForBean(tmpBean);
      stepsList.add(dataToStoreTemp);
    }
    dataToStoreActivityResults.put(AppConstants.RESULTS_FIELD_KEY, stepsList);
    this.addParticipantDataToMap(questionnaireActivityResponseBean, dataToStoreActivityResults);
    if (rawResponseData != null) {
      // Store raw response data
      dataToStoreActivityResults.put(AppConstants.RAW_RESPONSE_FIELD_KEY, rawResponseData);
    }
    dataToStoreActivityResults.put(
        AppConstants.CREATED_TS_KEY, questionnaireActivityResponseBean.getCreatedTimestamp());

    String studyId = questionnaireActivityResponseBean.getMetadata().getStudyId();

    String studyCollectionName = AppUtil.makeStudyCollectionName(studyId);
    logger.debug("saveActivityResponseData() : \n Study Collection Name: " + studyCollectionName);
    responsesDao.saveActivityResponseData(
        studyId,
        studyCollectionName,
        AppConstants.ACTIVITIES_COLLECTION_NAME,
        dataToStoreActivityResults);
    logger.exit("saveActivityResponseData() - ends ");
  }

  private Map<String, Object> getMapForParticipantCollection(
      ActivityResponseBean questionnaireActivityResponseBean) {
    Map<String, Object> dataToStoreParticipantCollMap = new HashMap<String, Object>();
    dataToStoreParticipantCollMap.put(
        AppConstants.PARTICIPANT_ID_KEY, questionnaireActivityResponseBean.getParticipantId());
    dataToStoreParticipantCollMap.put(
        AppConstants.SITE_ID_KEY, questionnaireActivityResponseBean.getSiteId());
    dataToStoreParticipantCollMap.put(
        AppConstants.SHARING_CONSENT_KEY, questionnaireActivityResponseBean.getSharingConsent());
    dataToStoreParticipantCollMap.put(
        AppConstants.CREATED_TS_KEY, questionnaireActivityResponseBean.getCreatedTimestamp());
    return dataToStoreParticipantCollMap;
  }

  private void addParticipantDataToMap(
      ActivityResponseBean questionnaireActivityResponseBean,
      Map<String, Object> dataToStoreActivityResults) {

    dataToStoreActivityResults.put(
        AppConstants.PARTICIPANT_ID_KEY, questionnaireActivityResponseBean.getParticipantId());
    dataToStoreActivityResults.put(
        AppConstants.SITE_ID_KEY, questionnaireActivityResponseBean.getSiteId());
    dataToStoreActivityResults.put(
        AppConstants.SHARING_CONSENT_KEY, questionnaireActivityResponseBean.getSharingConsent());
    dataToStoreActivityResults.put(
        AppConstants.CREATED_TS_KEY, questionnaireActivityResponseBean.getCreatedTimestamp());
  }

  private String getRawJsonInputData(Object argBean) {
    Gson gson = new Gson();
    return gson.toJson(argBean);
  }

  private Map<String, Object> getHashMapForBean(Object bean) throws Exception {
    BeanInfo beanInfo;
    beanInfo = Introspector.getBeanInfo(bean.getClass());
    PropertyDescriptor[] propDescriptor = beanInfo.getPropertyDescriptors();
    Map<String, Object> dataToStore = new HashMap<>();
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    List<Map<String, Object>> stepsList = new ArrayList<Map<String, Object>>();
    for (PropertyDescriptor pd : propDescriptor) {
      String propertyName = pd.getName();
      if (!propertyName.equals(AppConstants.PROPERTY_NAME_CLASS)) {
        Method getterMethod = pd.getReadMethod();
        Object propertyValue = getterMethod.invoke(bean);
        if (!(propertyValue instanceof String)) {
          if (propertyValue instanceof ActivityValueGroupBean
              || propertyValue instanceof ActivityMetadataBean) {
            dataToStore.put(propertyName, getHashMapForBean(propertyValue));
          } else if (propertyValue instanceof List) {
            try {
              ArrayList<Object> pvalueList = (ArrayList<Object>) propertyValue;
              for (Object valueObj : pvalueList) {
                if (valueObj instanceof QuestionnaireActivityStepsBean) {
                  Map<String, Object> tempMap = getHashMapForBean(valueObj);
                  stepsList.add(tempMap);
                } else if (valueObj instanceof String) {
                  if (valueObj != null) {
                    Object tmpPropertyValue = dataToStore.get(propertyName);
                    if (tmpPropertyValue != null) {
                      String tmpPropertyValueStr = (String) tmpPropertyValue.toString();
                      if (!StringUtils.isBlank(tmpPropertyValueStr)) {
                        valueObj = tmpPropertyValueStr + AppConstants.COMMA_STR + valueObj;
                      }
                    }
                    dataToStore.put(propertyName, valueObj);
                  }
                } else {
                  if (valueObj != null) {
                    propertyValue = gson.toJson(valueObj);
                    Object tmpPropertyValue = dataToStore.get(propertyName);
                    if (tmpPropertyValue != null) {
                      String tmpPropertyValueStr = (String) tmpPropertyValue.toString();
                      if (!StringUtils.isBlank(tmpPropertyValueStr)) {
                        propertyValue =
                            tmpPropertyValueStr + AppConstants.COMMA_STR + propertyValue;
                      }
                    }
                    dataToStore.put(propertyName, propertyValue);
                  }
                }
                if (stepsList != null && !stepsList.isEmpty()) {
                  dataToStore.put(AppConstants.RESULTS_FIELD_KEY, stepsList);
                }
              }
            } catch (ClassCastException ce) {
              propertyValue = gson.toJson(propertyValue);
              dataToStore.put(propertyName, getHashMapForBean(propertyValue));
            }
          } else {
            propertyValue = gson.toJson(propertyValue);
            dataToStore.put(propertyName, propertyValue);
          }
        } else {
          if (propertyValue != null) {
            dataToStore.put(propertyName, propertyValue);
          }
        }
        logger.debug(
            "getHashMapForBean() : \n Property Name: "
                + propertyName
                + "\t Property Value : "
                + propertyValue);
      }
    }
    return dataToStore;
  }
}
