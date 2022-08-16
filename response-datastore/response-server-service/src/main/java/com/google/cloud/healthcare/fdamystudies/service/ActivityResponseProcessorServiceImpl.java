/*
 * Copyright 2020-2021 Google LLC
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
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.DATE_FORMAT_RESPONSE_FHIR;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.DATE_FORMAT_RESPONSE_MOBILE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PATIENT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.QUESTIONNAIRE_RESPONSE_TYPE;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.QUESTIONNAIRE_TYPE_FHIR;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityMetadataBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityValueGroupBean;
import com.google.cloud.healthcare.fdamystudies.bean.Answer;
import com.google.cloud.healthcare.fdamystudies.bean.FHIRPatientBean;
import com.google.cloud.healthcare.fdamystudies.bean.FHIRQuestionnaireResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.Identifier;
import com.google.cloud.healthcare.fdamystudies.bean.ItemsQuestionnaireResponse;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStepsBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireEntry;
import com.google.cloud.healthcare.fdamystudies.bean.Reference;
import com.google.cloud.healthcare.fdamystudies.bean.SearchPatientFhirResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.SearchQuestionnaireFhirBean;
import com.google.cloud.healthcare.fdamystudies.bean.SearchQuestionnaireResponseFhirBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ResponseServerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ResponsesDao;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.DeIdentifyHealthcareApis;
import com.google.cloud.healthcare.fdamystudies.utils.FhirHealthcareApis;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ActivityResponseProcessorServiceImpl implements ActivityResponseProcessorService {

  private static final String FHIR_STORES = "/fhirStores/";

  private static final String DATASET_PATH = "projects/%s/locations/%s/datasets/%s";

  private static final String QUESTIONNAIRE_TYPE = "questionnaire_type";

  private static final String QUESTION_KEY = "question_key";

  private static final String SKIPPED = "skipped";

  private static final String WCP_RESULT_TYPE = "wcp_result_type";

  private static final String RESPONSE_RESULT_TYPE = "response_result_type";

  @Autowired
  @Qualifier("cloudFirestoreResponsesDaoImpl")
  private ResponsesDao responsesDao;

  @Autowired private ApplicationConfiguration appConfig;

  @Autowired private ResponseServerAuditLogHelper responseServerAuditLogHelper;

  @Autowired private FhirHealthcareApis fhirHealthcareAPIs;

  @Autowired private DeIdentifyHealthcareApis deIdentifyHealthcareApis;

  @Autowired private CommonDao commonDao;

  private XLogger logger =
      XLoggerFactory.getXLogger(ActivityResponseProcessorServiceImpl.class.getName());

  @Override
  public void saveActivityResponseDataForParticipant(
      QuestionnaireActivityStructureBean activityMetadataBeanFromWcp,
      ActivityResponseBean questionnaireActivityResponseBean,
      AuditLogEventRequest auditRequest)
      throws Exception {
    logger.info("begin saveActivityResponseDataForParticipant()");
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

      if (activityMetadataBeanFromWcp.getType().equals("task")) {
        if (CollectionUtils.isNotEmpty(questionnaireMetadata)) {
          questionnaireActivityResponseBean
              .getMetadata()
              .setActivityType(questionnaireMetadata.get(0).getResultType());
        }
      } else {
        questionnaireActivityResponseBean
            .getMetadata()
            .setActivityType(activityMetadataBeanFromWcp.getType());
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
    SearchPatientFhirResponseBean searchPatientFhirResponseBean =
        updateStatusOfPatientInFHIR(studyId, participantId);

    if (appConfig.getDiscardFhirAfterDid().equalsIgnoreCase("false")) {
      updateStatusOfPatientInDID(searchPatientFhirResponseBean, studyId);
    }
  }

  private void updateStatusOfPatientInDID(
      SearchPatientFhirResponseBean searchPatientFhirResponseBean, String studyId)
      throws ProcessResponseException {
    if (searchPatientFhirResponseBean != null
        && searchPatientFhirResponseBean.getTotal() == 1
        && appConfig.getEnableFhirManagementApi().equalsIgnoreCase("fhir&did")) {
      String srcDatasetPathforFHIR =
          String.format(DATASET_PATH, appConfig.getProjectId(), appConfig.getRegionId(), studyId);
      String datasetPathforDID =
          String.format(DATASET_PATH, appConfig.getProjectId(), appConfig.getRegionId(), studyId);

      List<String> resourceIds = new ArrayList<String>();
      resourceIds.add(
          searchPatientFhirResponseBean.getEntry().get(0).getResource().getResourceType()
              + "/"
              + searchPatientFhirResponseBean.getEntry().get(0).getResource().getId());

      deIdentifyHealthcareApis.deIdentification(
          srcDatasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId,
          datasetPathforDID + FHIR_STORES + "DID_" + studyId,
          resourceIds);
      if (appConfig.getDiscardFhirAfterDid().equalsIgnoreCase("true")) {
        String resourceNameOfPatient =
            srcDatasetPathforFHIR
                + FHIR_STORES
                + "FHIR_"
                + studyId
                + "/fhir/"
                + searchPatientFhirResponseBean.getEntry().get(0).getResource().getResourceType()
                + "/"
                + searchPatientFhirResponseBean.getEntry().get(0).getResource().getId();
        fhirHealthcareAPIs.fhirResourceDelete(resourceNameOfPatient);
      }
    }
  }

  public SearchPatientFhirResponseBean updateStatusOfPatientInFHIR(
      String studyId, String participantId) throws ProcessResponseException {
    SearchPatientFhirResponseBean searchPatientFhirResponseBean = null;
    try {
      if (appConfig.getEnableFhirManagementApi().contains("fhir")) {
        String searchJson = null;
        logger.entry("begin updateStatusOfPatientInFHIR()");

        String datasetPathforFHIR =
            String.format(DATASET_PATH, appConfig.getProjectId(), appConfig.getRegionId(), studyId);

        searchJson =
            fhirHealthcareAPIs.fhirResourceSearchPost(
                datasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId + "/fhir/" + PATIENT_TYPE,
                "identifier=" + participantId);
        searchPatientFhirResponseBean =
            new Gson().fromJson(searchJson, SearchPatientFhirResponseBean.class);

        if (searchPatientFhirResponseBean != null
            && searchPatientFhirResponseBean.getTotal() == 1) {
          final String RESOURCE_NAME =
              datasetPathforFHIR
                  + FHIR_STORES
                  + "FHIR_"
                  + studyId
                  + "/fhir/"
                  + PATIENT_TYPE
                  + "/"
                  + searchPatientFhirResponseBean.getEntry().get(0).getResource().getId();
          String data = "[{\"op\": \"replace\", \"path\": \"/active\", \"value\": false}]";
          fhirHealthcareAPIs.fhirResourcePatch(RESOURCE_NAME, data);
        }
      }
    } catch (Exception e) {
      logger.error(
          "updateStatusOfPatientInFHIR() method: Unable to update the status of Patient status in FHIR ",
          e);
    }
    logger.exit("updateStatusOfPatientInFHIR() - ends ");
    return searchPatientFhirResponseBean;
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
        if ((metadataMatchBean.getResultType().equals("numeric"))
            && metadataMatchBean.getFormat().containsKey("style")) {
          responseBean.setWcpResultType((String) metadataMatchBean.getFormat().get("style"));
        }
      }
    }
  }

  private void saveActivityResponseData(
      ActivityResponseBean questionnaireActivityResponseBean, String rawResponseData)
      throws Exception {
    logger.debug("begin saveActivityResponseData()");
    logger.debug("DiscardFHIR : \n : " + appConfig.getDiscardFhirAfterDid());
    logger.debug("EnableFHI : \n : " + appConfig.getEnableFhirManagementApi());
    // Add Timestamp to bean
    questionnaireActivityResponseBean.setCreatedTimestamp(
        String.valueOf(System.currentTimeMillis()));
    Map<String, Object> dataToStoreActivityResults =
        this.getHashMapForBean(questionnaireActivityResponseBean.getMetadata());
    dataToStoreActivityResults.remove(AppConstants.DATA_FIELD_KEY);

    List<QuestionnaireActivityStepsBean> questionnaireResponses =
        questionnaireActivityResponseBean.getData().getResults();
    List<Map<String, Object>> stepsList = new ArrayList<>();
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
    logger.info("saveActivityResponseData() : \n Study Collection Name: " + studyCollectionName);
    String fhirJson = "";
    if (appConfig.getEnableFhirManagementApi().contains("fhir")
        && appConfig.getDiscardFhirAfterDid().equalsIgnoreCase("false")) {
      fhirJson = processToFhirResponse(questionnaireActivityResponseBean);

    } else {
      responsesDao.saveActivityResponseData(
          studyId,
          studyCollectionName,
          AppConstants.ACTIVITIES_COLLECTION_NAME,
          dataToStoreActivityResults);
    }
    if (appConfig.getEnableFhirManagementApi().contains("did")) {
      logger.info(" did Enabled " + appConfig.getEnableFhirManagementApi());
      if (StringUtils.isBlank(fhirJson)) {
        fhirJson = processToFhirResponse(questionnaireActivityResponseBean);
      }
      processToDIDResponse(fhirJson, questionnaireActivityResponseBean);
      logger.info("did end");
    }
    logger.exit("saveActivityResponseData() - ends ");
  }

  public void processToDIDResponse(
      String fhirJson, ActivityResponseBean questionnaireActivityResponseBean) throws Exception {
    logger.entry("begin processToDIDResponse()");
    String studyId = null;

    try {
      if (StringUtils.isNotBlank(fhirJson)
          && appConfig.getEnableFhirManagementApi().contains("did")) {
        logger.info("did start");
        studyId = questionnaireActivityResponseBean.getMetadata().getStudyId();
        String srcDatasetPathforFHIR =
            String.format(DATASET_PATH, appConfig.getProjectId(), appConfig.getRegionId(), studyId);
        FHIRQuestionnaireResponseBean fhirQuestionnaireResponseBean =
            new Gson().fromJson(fhirJson, FHIRQuestionnaireResponseBean.class);
        String datasetPathforDID =
            String.format(DATASET_PATH, appConfig.getProjectId(), appConfig.getRegionId(), studyId);

        //  createFhirStore(datasetPathforDID, "DID_" + studyId);

        String identifierValue =
            questionnaireActivityResponseBean.getMetadata().getStudyId()
                + "@"
                + questionnaireActivityResponseBean.getSiteId()
                + "@"
                + questionnaireActivityResponseBean.getParticipantId()
                + "@"
                + questionnaireActivityResponseBean.getMetadata().getActivityId()
                + "@"
                + questionnaireActivityResponseBean.getMetadata().getActivityRunId();
        logger.info("fhirResourceSearchPost");
        String searchQuestionnaireJson =
            fhirHealthcareAPIs.fhirResourceSearchPost(
                datasetPathforDID
                    + FHIR_STORES
                    + "DID_"
                    + studyId
                    + "/fhir/"
                    + QUESTIONNAIRE_RESPONSE_TYPE,
                "identifier=" + identifierValue);

        // to avoid duplicate response submission
        SearchQuestionnaireResponseFhirBean searchPatientFhirResponseBean =
            new Gson().fromJson(searchQuestionnaireJson, SearchQuestionnaireResponseFhirBean.class);
        if (searchPatientFhirResponseBean != null && searchPatientFhirResponseBean.getTotal() > 0) {
          return;
        }

        List<String> resourceIds = new ArrayList<>();
        resourceIds.add(
            fhirQuestionnaireResponseBean.getResourceType()
                + "/"
                + fhirQuestionnaireResponseBean.getId());
        resourceIds.add(fhirQuestionnaireResponseBean.getSource().getReference());
        logger.entry("begin processToDIDResponse()" + srcDatasetPathforFHIR);
        logger.entry("begin processToDIDResponse()" + datasetPathforDID);
        logger.entry("begin deIdentification() :");
        deIdentifyHealthcareApis.deIdentification(
            srcDatasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId,
            datasetPathforDID + FHIR_STORES + "DID_" + studyId,
            resourceIds);
        logger.entry("deIdentification Created  :");
        commonDao.updateDidStatus(
            fhirQuestionnaireResponseBean.getResourceType()
                + "/"
                + fhirQuestionnaireResponseBean.getId());
        deIdentifyHealthcareApis.updateDIDResponseLocation(
            datasetPathforDID, studyId, fhirQuestionnaireResponseBean);
        if (appConfig.getDiscardFhirAfterDid().equalsIgnoreCase("true")) {
          String datasetPathforFHIR =
              String.format(
                  DATASET_PATH, appConfig.getProjectId(), appConfig.getRegionId(), studyId);
          String resourceNameForQuestionnaireResponse =
              datasetPathforFHIR
                  + FHIR_STORES
                  + "FHIR_"
                  + studyId
                  + "/fhir/"
                  + QUESTIONNAIRE_RESPONSE_TYPE
                  + "/"
                  + fhirQuestionnaireResponseBean.getId();
          fhirHealthcareAPIs.fhirResourceDelete(resourceNameForQuestionnaireResponse);
          /*String resourceNameForPatient =
              datasetPathforFHIR
                  + FHIR_STORES
                  + questionnaireActivityResponseBean.getMetadata().getStudyId()
                  + "/fhir/"
                  + fhirQuestionnaireResponseBean.getSource().getReference();
          fhirHealthcareAPIs.fhirResourceDelete(resourceNameForPatient);*/
        }
      }
    } catch (JsonSyntaxException | ProcessResponseException e) {
      logger.error(
          "ActivityResponseProcessorServiceImpl : \n processToDIDResponse:" + e.getMessage());
    }
  }

  public String processToFhirResponse(ActivityResponseBean questionnaireActivityResponseBean)
      throws Exception {

    String getFhirJson = null;
    String studyId = null;

    if (appConfig.getEnableFhirManagementApi().contains("fhir")) {
      logger.entry("begin processToFhirResponse()");

      studyId = questionnaireActivityResponseBean.getMetadata().getStudyId();
      List<ItemsQuestionnaireResponse> listOfItems = new LinkedList<>();
      String datasetPathforFHIR =
          String.format(DATASET_PATH, appConfig.getProjectId(), appConfig.getRegionId(), studyId);

      String identifierValue =
          questionnaireActivityResponseBean.getMetadata().getStudyId()
              + "@"
              + questionnaireActivityResponseBean.getSiteId()
              + "@"
              + questionnaireActivityResponseBean.getParticipantId()
              + "@"
              + questionnaireActivityResponseBean.getMetadata().getActivityId()
              + "@"
              + questionnaireActivityResponseBean.getMetadata().getActivityRunId();

      String searchQuestionnaireJson =
          fhirHealthcareAPIs.fhirResourceSearchPost(
              datasetPathforFHIR
                  + FHIR_STORES
                  + "FHIR_"
                  + questionnaireActivityResponseBean.getMetadata().getStudyId()
                  + "/fhir/"
                  + QUESTIONNAIRE_RESPONSE_TYPE,
              "identifier=" + identifierValue);

      // to avoid duplicate response submission
      SearchQuestionnaireResponseFhirBean searchQuestionFhirResponseBean =
          new Gson().fromJson(searchQuestionnaireJson, SearchQuestionnaireResponseFhirBean.class);
      if (searchQuestionFhirResponseBean != null && searchQuestionFhirResponseBean.getTotal() > 0) {
        return new Gson().toJson(searchQuestionFhirResponseBean.getEntry().get(0).getResource());
      }

      for (QuestionnaireActivityStepsBean tmpBean :
          questionnaireActivityResponseBean.getData().getResults()) {

        ItemsQuestionnaireResponse items = new ItemsQuestionnaireResponse();
        List<Answer> answerList = new LinkedList<>();
        Map<String, Object> map = new HashedMap<>();
        map.put(RESPONSE_RESULT_TYPE, tmpBean.getResultType());
        map.put(WCP_RESULT_TYPE, tmpBean.getWcpResultType());
        map.put(SKIPPED, tmpBean.getSkipped());
        map.put(QUESTIONNAIRE_TYPE, questionnaireActivityResponseBean.getData().getResultType());
        map.put(QUESTION_KEY, tmpBean.getKey());

        toFHIRFormatQuestionnaireResponse(
            tmpBean, false, items, map, answerList, questionnaireActivityResponseBean.getType());

        items.setLinkId(tmpBean.getKey());
        if (questionnaireActivityResponseBean.getType().equals("questionnaire")) {
          items.setText(
              tmpBean.getResultType().equalsIgnoreCase(AppConstants.GROUPED_FIELD_KEY)
                  ? tmpBean.getKey()
                  : tmpBean.getTitle());
        } else {
          items.setText(questionnaireActivityResponseBean.getMetadata().getName());
        }

        items.setDefinition(tmpBean.getResultType());
        listOfItems.add(items);
      }

      FHIRQuestionnaireResponseBean questFHIResponseBean = new FHIRQuestionnaireResponseBean();
      questFHIResponseBean.setResourceType("QuestionnaireResponse");
      questFHIResponseBean.setStatus("completed");

      questFHIResponseBean.setItem(listOfItems);
      logger.debug("processToFhirResponse4" + questFHIResponseBean.toString());
      String searchPostForQuestionaire =
          datasetPathforFHIR
              + FHIR_STORES
              + "FHIR_"
              + questionnaireActivityResponseBean.getMetadata().getStudyId()
              + "/fhir/"
              + QUESTIONNAIRE_TYPE_FHIR;
      String searchJson =
          fhirHealthcareAPIs.fhirResourceSearchPost(
              searchPostForQuestionaire,
              "identifier="
                  + questionnaireActivityResponseBean.getMetadata().getActivityId()
                  + "&"
                  + "version="
                  + questionnaireActivityResponseBean.getMetadata().getVersion());
      logger.debug("processToFhirResponse5" + searchJson.toString());

      SearchQuestionnaireFhirBean searchQuestionnaireFhirBean =
          new Gson().fromJson(searchJson, SearchQuestionnaireFhirBean.class);
      String resourceId = searchQuestionnaireFhirBean.getEntry().get(0).getResource().getId();
      String searchVersionHistoryJson =
          fhirHealthcareAPIs.fhirResourceGetHistory(searchPostForQuestionaire + "/" + resourceId);
      logger.debug("processToFhirResponse6" + searchVersionHistoryJson.toString());

      SearchQuestionnaireFhirBean searchVersionQuestionnaireFhirBean =
          new Gson().fromJson(searchVersionHistoryJson, SearchQuestionnaireFhirBean.class);

      QuestionnaireEntry questionnaireEntry =
          searchVersionQuestionnaireFhirBean
              .getEntry()
              .stream()
              .filter(
                  version ->
                      version
                          .getResource()
                          .getVersion()
                          .equals(questionnaireActivityResponseBean.getMetadata().getVersion()))
              .filter(status -> status.getResource().getStatus().equals("active"))
              .findAny()
              .orElse(null);
      logger.debug("processToFhirResponse7" + questionnaireEntry.toString());

      if (questionnaireEntry != null) {
        questFHIResponseBean.setQuestionnaire(
            FHIR_STORES
                + "FHIR_"
                + questionnaireActivityResponseBean.getMetadata().getStudyId()
                + "/fhir/"
                + QUESTIONNAIRE_TYPE_FHIR
                + "/"
                + resourceId
                + "/_history/"
                + questionnaireEntry.getResource().getMeta().getVersionId());
      }

      getPatientReference(
          questionnaireActivityResponseBean.getMetadata().getStudyId(),
          questionnaireActivityResponseBean.getParticipantId(),
          datasetPathforFHIR,
          questFHIResponseBean);

      if (StringUtils.isNotBlank(questionnaireActivityResponseBean.getData().getSubmittedTime())) {
        questFHIResponseBean.setAuthored(
            AppUtil.convertDateToOtherFormat(
                questionnaireActivityResponseBean.getData().getSubmittedTime(),
                AppConstants.DATE_FORMAT_RESPONSE_MOBILE,
                AppConstants.DATE_FORMAT_RESPONSE_FHIR));
      }

      Map<String, Object> identifierType = new HashedMap<>();
      identifierType.put("text", questionnaireActivityResponseBean.getMetadata().getActivityType());
      Identifier identifier = new Identifier();
      identifier.setValue(identifierValue);
      identifier.setUse("official");
      identifier.setType(identifierType);
      questFHIResponseBean.setIdentifier(identifier);

      final String DATASET_NAME =
          datasetPathforFHIR
              + FHIR_STORES
              + "FHIR_"
              + questionnaireActivityResponseBean.getMetadata().getStudyId();

      if (questFHIResponseBean != null && new Gson().toJson(questFHIResponseBean) != null) {
        logger.debug(
            "method name :processToFhirResponse()"
                + "questFHIResponseBean"
                + questFHIResponseBean.toString());
        getFhirJson =
            fhirHealthcareAPIs.fhirResourceCreate(
                DATASET_NAME, QUESTIONNAIRE_RESPONSE_TYPE, new Gson().toJson(questFHIResponseBean));
        commonDao.saveToFHIREntity(
            getFhirJson, questionnaireActivityResponseBean.getMetadata().getStudyId());
        logger.exit("processToFhirResponse() - ends ");
      } else {
        logger.exit("unable to create fhirResource- ");
      }
    }
    return getFhirJson;
  }

  private void getPatientReference(
      String studyId,
      String participantId,
      String datasetPathforFHIR,
      FHIRQuestionnaireResponseBean questFHIResponseBean)
      throws Exception {
    logger.entry("begin getPatientReference()");

    String searchJson =
        fhirHealthcareAPIs.fhirResourceSearchPost(
            datasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId + "/fhir/" + PATIENT_TYPE,
            "identifier=" + participantId);

    SearchPatientFhirResponseBean searchPatientFhirResponseBean =
        new Gson().fromJson(searchJson, SearchPatientFhirResponseBean.class);
    if (searchPatientFhirResponseBean != null && searchPatientFhirResponseBean.getTotal() == 1) {
      questFHIResponseBean.setSource(
          new Reference(
              PATIENT_TYPE
                  + "/"
                  + searchPatientFhirResponseBean.getEntry().get(0).getResource().getId(),
              PATIENT_TYPE));
    } else if (searchPatientFhirResponseBean != null
        && searchPatientFhirResponseBean.getTotal() == 0) {
      FHIRPatientBean responseBean =
          new Gson()
              .fromJson(
                  insertPatientInFHIR(datasetPathforFHIR, studyId, participantId),
                  FHIRPatientBean.class);
      questFHIResponseBean.setSource(
          new Reference(PATIENT_TYPE + "/" + responseBean.getId(), PATIENT_TYPE));
    }

    logger.exit("getPatientReference() - ends ");
  }

  public String insertPatientInFHIR(String datasetPathforFHIR, String studyId, String participantId)
      throws Exception {
    logger.entry("begin insertPatientInFHIR()");
    FHIRPatientBean fhirBean = new FHIRPatientBean();
    fhirBean.setResourceType(PATIENT_TYPE);
    fhirBean.setActive(true);
    Identifier identifier = new Identifier();
    identifier.setValue(participantId);
    List<Identifier> listOfIdentifier = new LinkedList<>();
    listOfIdentifier.add(identifier);
    fhirBean.setIdentifier(listOfIdentifier);
    String json = new Gson().toJson(fhirBean);

    logger.exit("insertPatientInFHIR() - ends ");
    return fhirHealthcareAPIs.fhirResourceCreate(
        datasetPathforFHIR + FHIR_STORES + "FHIR_" + studyId, PATIENT_TYPE, json);
  }

  @SuppressWarnings("unchecked")
  private void toFHIRFormatQuestionnaireResponse(
      Object bean,
      boolean value,
      ItemsQuestionnaireResponse items,
      Map<String, Object> map,
      List<Answer> answerList,
      String type)
      throws Exception {
    logger.entry("begin toFHIRFormatQuestionnaireResponse()");

    String responseResultType = (String) map.get(RESPONSE_RESULT_TYPE);
    boolean skipped = (boolean) map.get(SKIPPED);

    BeanInfo beanInfo;
    beanInfo = Introspector.getBeanInfo(bean.getClass());
    PropertyDescriptor[] propDescriptor = beanInfo.getPropertyDescriptors();
    Answer ans = new Answer();
    ActivityValueGroupBean acitivtyValueGroup = null;

    for (PropertyDescriptor pd : propDescriptor) {
      String propertyName = pd.getName();
      Method getterMethod = pd.getReadMethod();
      Object propertyValue = getterMethod.invoke(bean);
      if (!propertyName.equals(AppConstants.PROPERTY_NAME_CLASS)) {

        if (propertyName.equals("actvityValueGroup")) {
          acitivtyValueGroup = (ActivityValueGroupBean) propertyValue;
        }

        if (value
            || propertyName.equals("value")
            || (acitivtyValueGroup != null && !acitivtyValueGroup.getValueGroup().isEmpty())) {
          if (!(propertyValue instanceof String)) {
            if (propertyValue instanceof ActivityValueGroupBean) {
              toFHIRFormatQuestionnaireResponse(propertyValue, true, items, map, answerList, type);
            } else if (propertyValue instanceof List) {
              ArrayList<Object> pvalueList = (ArrayList<Object>) propertyValue;
              List<ItemsQuestionnaireResponse> listOfItems1 = new LinkedList<>();
              List<Answer> answer = new LinkedList<>();
              for (Object valueObj : pvalueList) {

                if (valueObj instanceof QuestionnaireActivityStepsBean) {

                  ItemsQuestionnaireResponse nestedItem = new ItemsQuestionnaireResponse();
                  nestedItem.setLinkId(((QuestionnaireActivityStepsBean) valueObj).getKey());
                  nestedItem.setText(
                      ((QuestionnaireActivityStepsBean) valueObj)
                              .getResultType()
                              .equalsIgnoreCase(AppConstants.GROUPED_FIELD_KEY)
                          ? ((QuestionnaireActivityStepsBean) valueObj).getKey()
                          : ((QuestionnaireActivityStepsBean) valueObj).getTitle());

                  nestedItem.setDefinition(
                      ((QuestionnaireActivityStepsBean) valueObj).getResultType());

                  map.put(
                      RESPONSE_RESULT_TYPE,
                      ((QuestionnaireActivityStepsBean) valueObj).getResultType());
                  map.put(
                      WCP_RESULT_TYPE,
                      ((QuestionnaireActivityStepsBean) valueObj).getWcpResultType());
                  map.put(SKIPPED, ((QuestionnaireActivityStepsBean) valueObj).getSkipped());
                  map.put(QUESTION_KEY, ((QuestionnaireActivityStepsBean) valueObj).getKey());
                  toFHIRFormatQuestionnaireResponse(valueObj, false, nestedItem, map, answer, type);

                  listOfItems1.add(nestedItem);

                } else {
                  if (valueObj instanceof String
                      && StringUtils.isNotBlank((String) valueObj)
                      && !skipped) {
                    ans = new Answer();
                    ans = fhirAnswerValue(map, valueObj, ans);
                    answer.add(ans);
                    items.setAnswer(answer);
                  }
                }
                if (type.equals("task")) {
                  Answer ansList = new Answer();
                  ansList.setItem(listOfItems1);
                  List<Answer> a1 = new LinkedList<>();
                  a1.add(ansList);
                  items.setAnswer(a1);
                } else {
                  items.setItem(listOfItems1);
                }
              }
            } else {
              if (!responseResultType.equalsIgnoreCase(AppConstants.GROUPED_FIELD_KEY)
                  && !skipped) {
                ans = fhirAnswerValue(map, propertyValue, ans);
                answerList = new LinkedList<>();
                answerList.add(ans);
                items.setAnswer(answerList);
              }
            }
          } else {
            if (!responseResultType.equalsIgnoreCase(AppConstants.GROUPED_FIELD_KEY)
                && !skipped
                && StringUtils.isNotBlank((String) propertyValue)) {
              if (map.get(QUESTION_KEY).equals("duration")) {
                map.put(WCP_RESULT_TYPE, "Integer");
                propertyValue = Double.parseDouble((String) propertyValue);
              }
              ans = fhirAnswerValue(map, propertyValue, ans);
              answerList = new LinkedList<>();
              answerList.add(ans);
              items.setAnswer(answerList);
            }
          }
        }
      }
    }
    logger.exit("toFHIRFormatQuestionnaireResponse() - ends ");
  }

  @SuppressWarnings("deprecation")
  private Answer fhirAnswerValue(Map<String, Object> map, Object propertyValue, Answer answerMap)
      throws Exception {
    logger.entry("begin fhirAnswerValue()");

    String responseResultType = (String) map.get(RESPONSE_RESULT_TYPE);
    String wcpResultType = (String) map.get(WCP_RESULT_TYPE);

    if (responseResultType.equals("continuousScale")
        || responseResultType.equals("timeInterval")
        || responseResultType.equals("height")) {

      if (propertyValue instanceof Double) {
        answerMap.setValueDecimal((Double) propertyValue);
      } else if (propertyValue instanceof Integer) {
        answerMap.setValueDecimal(new Double((Integer) propertyValue));
      }

    } else if (responseResultType.equals("numeric")) {

      if (wcpResultType.equals("Integer")) {
        if (propertyValue instanceof Double) {
          answerMap.setValueInteger(new Double((Double) propertyValue).intValue());
        } else if (propertyValue instanceof Integer) {
          answerMap.setValueInteger((Integer) propertyValue);
        }
      } else {
        if (propertyValue instanceof Double) {
          answerMap.setValueDecimal((Double) propertyValue);
        } else if (propertyValue instanceof Integer) {
          answerMap.setValueDecimal(new Double((Integer) propertyValue));
        }
      }

    } else if (responseResultType.equals("scale")) {

      if (propertyValue instanceof Double) {
        answerMap.setValueInteger(new Double((Double) propertyValue).intValue());
      } else if (propertyValue instanceof Integer) {
        answerMap.setValueInteger((Integer) propertyValue);
      }

    } else if (responseResultType.equals("boolean")) {
      answerMap.setValueBoolean((Boolean) propertyValue);
    } else if (responseResultType.equals("timeOfDay")) {
      answerMap.setValueTime((String) propertyValue);
    } else if (responseResultType.equals("date")) {

      if (StringUtils.isNotBlank((String) propertyValue)) {
        answerMap.setValueDateTime(
            AppUtil.convertDateToOtherFormat(
                (String) propertyValue, DATE_FORMAT_RESPONSE_MOBILE, DATE_FORMAT_RESPONSE_FHIR));
      }
    } else if (propertyValue instanceof String) {
      answerMap.setValueString((String) propertyValue);
    }
    logger.exit("fhirAnswerValue() - ends ");
    return answerMap;
  }

  public void createFhirStore(String datasetPath, String studyId) throws ProcessResponseException {

    try {
      fhirHealthcareAPIs.fhirStoreGet(datasetPath + FHIR_STORES + studyId);
    } catch (Exception e) {
      if (e instanceof GoogleJsonResponseException
          && ((GoogleJsonResponseException) e).getStatusCode() == 404
          && ((GoogleJsonResponseException) e).getStatusMessage().equals("Not Found")) {
        fhirHealthcareAPIs.fhirStoreCreate(datasetPath, studyId);
      }
    }
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
