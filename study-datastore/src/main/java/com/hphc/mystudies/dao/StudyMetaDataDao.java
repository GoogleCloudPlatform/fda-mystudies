/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.hphc.mystudies.dao;

import com.hphc.mystudies.bean.AnchorDateBean;
import com.hphc.mystudies.bean.ComprehensionDetailsBean;
import com.hphc.mystudies.bean.ConsentBean;
import com.hphc.mystudies.bean.ConsentDetailsBean;
import com.hphc.mystudies.bean.ConsentDocumentBean;
import com.hphc.mystudies.bean.ConsentDocumentResponse;
import com.hphc.mystudies.bean.CorrectAnswersBean;
import com.hphc.mystudies.bean.EligibilityBean;
import com.hphc.mystudies.bean.EligibilityConsentResponse;
import com.hphc.mystudies.bean.GatewayInfoResourceBean;
import com.hphc.mystudies.bean.GatewayInfoResponse;
import com.hphc.mystudies.bean.InfoBean;
import com.hphc.mystudies.bean.QuestionInfoBean;
import com.hphc.mystudies.bean.QuestionnaireActivityStepsBean;
import com.hphc.mystudies.bean.ResourcesBean;
import com.hphc.mystudies.bean.ResourcesResponse;
import com.hphc.mystudies.bean.ReviewBean;
import com.hphc.mystudies.bean.SettingsBean;
import com.hphc.mystudies.bean.SharingBean;
import com.hphc.mystudies.bean.StudyBean;
import com.hphc.mystudies.bean.StudyInfoResponse;
import com.hphc.mystudies.bean.StudyResponse;
import com.hphc.mystudies.dto.ActiveTaskDto;
import com.hphc.mystudies.dto.AnchorDateTypeDto;
import com.hphc.mystudies.dto.ComprehensionTestQuestionDto;
import com.hphc.mystudies.dto.ComprehensionTestResponseDto;
import com.hphc.mystudies.dto.ConsentDto;
import com.hphc.mystudies.dto.ConsentInfoDto;
import com.hphc.mystudies.dto.ConsentMasterInfoDto;
import com.hphc.mystudies.dto.EligibilityDto;
import com.hphc.mystudies.dto.EligibilityTestDto;
import com.hphc.mystudies.dto.EnrollmentTokenDto;
import com.hphc.mystudies.dto.FormMappingDto;
import com.hphc.mystudies.dto.GatewayInfoDto;
import com.hphc.mystudies.dto.GatewayWelcomeInfoDto;
import com.hphc.mystudies.dto.QuestionnairesDto;
import com.hphc.mystudies.dto.QuestionnairesStepsDto;
import com.hphc.mystudies.dto.QuestionsDto;
import com.hphc.mystudies.dto.ReferenceTablesDto;
import com.hphc.mystudies.dto.ResourcesDto;
import com.hphc.mystudies.dto.StudyDto;
import com.hphc.mystudies.dto.StudyPageDto;
import com.hphc.mystudies.dto.StudySequenceDto;
import com.hphc.mystudies.dto.StudyVersionDto;
import com.hphc.mystudies.exception.DAOException;
import com.hphc.mystudies.util.HibernateUtil;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class StudyMetaDataDao {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(StudyMetaDataDao.class.getName());

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  @SuppressWarnings("unchecked")
  HashMap<String, String> authPropMap = StudyMetaDataUtil.getAuthorizationProperties();

  SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
  Query query = null;
  String queryString = "";

  public boolean isValidAuthorizationId(String authorization) throws DAOException {
    LOGGER.entry("begin isValidAuthorizationId()");
    boolean hasValidAuthorization = false;
    String bundleIdAndAppToken = null;
    try {
      byte[] decodedBytes = Base64.getDecoder().decode(authorization);
      bundleIdAndAppToken = new String(decodedBytes, StudyMetaDataConstants.TYPE_UTF8);
      final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
      final String bundleId = tokenizer.nextToken();
      final String appToken = tokenizer.nextToken();
      if (authPropMap.containsValue(bundleId) && authPropMap.containsValue(appToken)) {
        hasValidAuthorization = true;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidAuthorizationId() :: ERROR", e);
    }
    LOGGER.exit("StudyMetaDataOrchestration - isValidAuthorizationId() :: Ends");
    return hasValidAuthorization;
  }

  @SuppressWarnings("unchecked")
  public GatewayInfoResponse gatewayAppResourcesInfo(String authorization) throws DAOException {
    LOGGER.entry("begin gatewayAppResourcesInfo()");
    Session session = null;
    GatewayInfoResponse gatewayInfoResponse = new GatewayInfoResponse();
    GatewayInfoDto gatewayInfo = null;
    List<GatewayWelcomeInfoDto> gatewayWelcomeInfoList = null;
    List<ResourcesDto> resourcesList = null;
    String platformType = "";
    try {
      session = sessionFactory.openSession();
      gatewayInfo = (GatewayInfoDto) session.getNamedQuery("getGatewayInfo").uniqueResult();
      if (null != gatewayInfo) {

        gatewayWelcomeInfoList = session.getNamedQuery("getGatewayWelcomeInfoList").list();
        if ((null != gatewayWelcomeInfoList) && !gatewayWelcomeInfoList.isEmpty()) {
          List<InfoBean> infoBeanList = new ArrayList<>();
          for (GatewayWelcomeInfoDto gatewayWelcomeInfo : gatewayWelcomeInfoList) {

            InfoBean infoBean = new InfoBean();
            infoBean.setTitle(
                StringUtils.isEmpty(gatewayWelcomeInfo.getAppTitle())
                    ? ""
                    : gatewayWelcomeInfo.getAppTitle());
            infoBean.setImage(
                StringUtils.isEmpty(gatewayWelcomeInfo.getImagePath())
                    ? ""
                    : StudyMetaDataUtil.getSignedUrl(
                        propMap.get("cloud.bucket.name"),
                        propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH).trim()
                            + gatewayWelcomeInfo.getImagePath(),
                        StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
            infoBean.setText(
                StringUtils.isEmpty(gatewayWelcomeInfo.getDescription())
                    ? ""
                    : gatewayWelcomeInfo.getDescription());
            if (infoBeanList.isEmpty()) {
              infoBean.setType(StudyMetaDataConstants.TYPE_VIDEO);
              infoBean.setVideoLink(
                  StringUtils.isEmpty(gatewayInfo.getVideoUrl()) ? "" : gatewayInfo.getVideoUrl());
            } else {
              infoBean.setType(StudyMetaDataConstants.TYPE_TEXT);
            }
            infoBeanList.add(infoBean);
          }
          gatewayInfoResponse.setInfo(infoBeanList);
        }
      }

      /**
       * Get the platform from the provided authorization credentials and fetch based on the
       * platform
       */
      platformType =
          StudyMetaDataUtil.platformType(
              authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM);
      if (StringUtils.isNotEmpty(platformType)) {
        resourcesList =
            session
                .createQuery(
                    "from ResourcesDto RDTO"
                        + " where RDTO.studyId in ( select SDTO.id"
                        + " from StudyDto SDTO"
                        + " where SDTO.platform like concat('%', :platformType, '%')"
                        + " and SDTO.type= :type and SDTO.live=1)"
                        + " ORDER BY RDTO.sequenceNo")
                .setString(StudyMetaDataEnum.QF_TYPE.value(), StudyMetaDataConstants.STUDY_TYPE_GT)
                .setString("platformType", platformType)
                .list();
        if ((null != resourcesList) && !resourcesList.isEmpty()) {
          List<GatewayInfoResourceBean> resourceBeanList = new ArrayList<>();
          for (ResourcesDto resource : resourcesList) {

            GatewayInfoResourceBean resourceBean = new GatewayInfoResourceBean();
            resourceBean.setTitle(
                StringUtils.isEmpty(resource.getTitle()) ? "" : resource.getTitle());
            if (!resource.isTextOrPdf()) {
              resourceBean.setType(StudyMetaDataConstants.TYPE_HTML);
              resourceBean.setContent(
                  StringUtils.isEmpty(resource.getRichText()) ? "" : resource.getRichText());
            } else {
              resourceBean.setType(StudyMetaDataConstants.TYPE_PDF);
              resourceBean.setContent(
                  StringUtils.isEmpty(resource.getPdfUrl())
                      ? ""
                      : StudyMetaDataUtil.getSignedUrl(
                          propMap.get("cloud.bucket.name"),
                          propMap.get(StudyMetaDataConstants.FDA_SMD_RESOURCE_PDF_PATH).trim()
                              + resource.getPdfUrl(),
                          StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
            }

            resourceBean.setResourcesId(
                resource.getId() == null ? "" : String.valueOf(resource.getId()));
            resourceBeanList.add(resourceBean);
          }
          gatewayInfoResponse.setResources(resourceBeanList);
        }
      }

      gatewayInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - gatewayAppResourcesInfo() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("gatewayAppResourcesInfo() :: Ends");
    return gatewayInfoResponse;
  }

  @SuppressWarnings("unchecked")
  public StudyResponse studyList(String authorization, String applicationId) throws DAOException {
    LOGGER.entry("begin studyList()");
    Session session = null;
    StudyResponse studyResponse = new StudyResponse();
    List<StudyDto> studiesList = null;
    String platformType = "";
    try {
      platformType =
          StudyMetaDataUtil.platformType(
              authorization, StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM);
      if (StringUtils.isNotEmpty(platformType)) {
        session = sessionFactory.openSession();

        /** Get all configured studies from the WCP by platform supported */
        studiesList =
            session
                .createQuery(
                    "from StudyDto SDTO"
                        + " where SDTO.platform like concat('%', :platformType, '%')"
                        + " and SDTO.appId=:applicationId"
                        + " and (SDTO.status= :status OR SDTO.live=1)")
                .setString(
                    StudyMetaDataEnum.QF_STATUS.value(),
                    StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)
                .setString("platformType", platformType)
                .setString("applicationId", applicationId)
                .list();

        if ((null != studiesList) && !studiesList.isEmpty()) {
          List<StudyBean> studyBeanList = new ArrayList<>();
          for (StudyDto studyDto : studiesList) {

            StudyBean studyBean = new StudyBean();
            studyBean.setStudyVersion(
                studyDto.getVersion() == null
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : studyDto.getVersion().toString());
            studyBean.setTagline(
                StringUtils.isEmpty(studyDto.getStudyTagline()) ? "" : studyDto.getStudyTagline());

            switch (studyDto.getStatus()) {
              case StudyMetaDataConstants.STUDY_STATUS_ACTIVE:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_ACTIVE);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PAUSED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_PAUSED);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_UPCOMING);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_DEACTIVATED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_CLOSED);
                break;
              default:
                break;
            }

            studyBean.setTitle(StringUtils.isEmpty(studyDto.getName()) ? "" : studyDto.getName());
            studyBean.setLogo(
                StringUtils.isEmpty(studyDto.getThumbnailImage())
                    ? ""
                    : StudyMetaDataUtil.getSignedUrl(
                        propMap.get("cloud.bucket.name"),
                        propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH).trim()
                            + studyDto.getThumbnailImage(),
                        StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
            studyBean.setStudyId(
                StringUtils.isEmpty(studyDto.getCustomStudyId())
                    ? ""
                    : studyDto.getCustomStudyId());

            studyBean.setSponsorName(
                StringUtils.isEmpty(studyDto.getResearchSponsor())
                    ? ""
                    : studyDto.getResearchSponsor());

            if (StringUtils.isNotEmpty(studyDto.getCategory())
                && StringUtils.isNotEmpty(studyDto.getResearchSponsor())) {
              List<ReferenceTablesDto> referenceTablesList =
                  session
                      .createQuery("from ReferenceTablesDto RTDTO where RTDTO.id IN (:category)")
                      .setString("category", studyDto.getCategory())
                      .list();
              if ((null != referenceTablesList) && !referenceTablesList.isEmpty()) {
                for (ReferenceTablesDto reference : referenceTablesList) {
                  if (reference
                      .getCategory()
                      .equalsIgnoreCase(StudyMetaDataConstants.STUDY_REF_CATEGORIES)) {
                    studyBean.setCategory(
                        StringUtils.isEmpty(reference.getValue()) ? "" : reference.getValue());
                  }
                }
              }
            }

            SettingsBean settings = new SettingsBean();
            if (studyDto.getPlatform().contains(",")) {
              settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ALL);
            } else {
              switch (studyDto.getPlatform()) {
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_IOS);
                  break;
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID);
                  break;
                default:
                  break;
              }
            }

            if (StringUtils.isNotEmpty(studyDto.getEnrollingParticipants())
                && studyDto
                    .getEnrollingParticipants()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              settings.setEnrolling(true);
            } else {
              settings.setEnrolling(false);
            }
            studyBean.setSettings(settings);
            studyBeanList.add(studyBean);
          }
          studyResponse.setStudies(studyBeanList);
        }
        studyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - studyList() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("studyList() :: Ends");
    return studyResponse;
  }

  @SuppressWarnings("unchecked")
  public EligibilityConsentResponse eligibilityConsentMetadata(String studyId) throws DAOException {
    LOGGER.entry("begin eligibilityConsentMetadata()");
    Session session = null;
    EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
    EligibilityDto eligibilityDto = null;
    ConsentDto consentDto = null;
    List<ConsentInfoDto> consentInfoDtoList = null;
    List<ComprehensionTestQuestionDto> comprehensionQuestionList = null;
    List<ConsentMasterInfoDto> consentMasterInfoList = null;
    ConsentDetailsBean consent = new ConsentDetailsBean();
    StudySequenceDto studySequenceDto = null;
    StudyDto studyDto = null;
    StudyVersionDto studyVersionDto = null;
    List<EligibilityTestDto> eligibilityTestList = null;
    try {
      session = sessionFactory.openSession();

      studyDto =
          (StudyDto)
              session
                  .getNamedQuery("getLiveStudyIdByCustomStudyId")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto != null) {
        studyVersionDto =
            (StudyVersionDto)
                session
                    .getNamedQuery("getLiveVersionDetailsByCustomStudyIdAndVersion")
                    .setString(
                        StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyDto.getCustomStudyId())
                    .setFloat(StudyMetaDataEnum.QF_STUDY_VERSION.value(), studyDto.getVersion())
                    .setMaxResults(1)
                    .uniqueResult();

        studySequenceDto =
            (StudySequenceDto)
                session
                    .getNamedQuery("getStudySequenceDetailsByStudyId")
                    .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                    .uniqueResult();
        if (studySequenceDto != null) {

          if (studySequenceDto
              .getEligibility()
              .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)) {

            eligibilityDto =
                (EligibilityDto)
                    session
                        .getNamedQuery("eligibilityDtoByStudyId")
                        .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                        .uniqueResult();
            if (eligibilityDto != null) {

              EligibilityBean eligibility = new EligibilityBean();
              if (null != eligibilityDto.getEligibilityMechanism()) {
                switch (eligibilityDto.getEligibilityMechanism()) {
                  case 1:
                    eligibility.setType(StudyMetaDataConstants.TYPE_TOKEN);
                    break;
                  case 2:
                    eligibility.setType(StudyMetaDataConstants.TYPE_BOTH);
                    break;
                  case 3:
                    eligibility.setType(StudyMetaDataConstants.TYPE_TEST);
                    break;
                  default:
                    eligibility.setType("");
                    break;
                }
              }
              eligibility.setTokenTitle(
                  StringUtils.isEmpty(eligibilityDto.getInstructionalText())
                      ? ""
                      : eligibilityDto.getInstructionalText());

              eligibilityTestList =
                  session
                      .createQuery(
                          "from EligibilityTestDto ETDTO"
                              + " where ETDTO.eligibilityId=:eligId"
                              + " and ETDTO.status=true and ETDTO.active=true"
                              + " ORDER BY ETDTO.sequenceNo")
                      .setInteger("eligId", eligibilityDto.getId())
                      .list();
              if ((eligibilityTestList != null) && !eligibilityTestList.isEmpty()) {
                List<QuestionnaireActivityStepsBean> test = new ArrayList<>();

                List<HashMap<String, Object>> correctAnswers = new ArrayList<>();
                for (EligibilityTestDto eligibilityTest : eligibilityTestList) {
                  QuestionnaireActivityStepsBean questionStep =
                      new QuestionnaireActivityStepsBean();
                  questionStep.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
                  questionStep.setResultType(StudyMetaDataConstants.QUESTION_BOOLEAN);
                  questionStep.setKey(eligibilityTest.getShortTitle());
                  questionStep.setTitle(eligibilityTest.getQuestion());
                  questionStep.setText(StudyMetaDataConstants.ELIGIBILITY_TEXT);
                  questionStep.setSkippable(false);
                  questionStep.setGroupName("");
                  questionStep.setRepeatable(false);
                  questionStep.setRepeatableText("");
                  questionStep.setHealthDataKey("");
                  test.add(questionStep);

                  if (eligibilityTest.getResponseYesOption()) {
                    HashMap<String, Object> correctAnsHashMap = new HashMap<>();
                    correctAnsHashMap.put("key", eligibilityTest.getShortTitle());
                    correctAnsHashMap.put("answer", true);
                    correctAnswers.add(correctAnsHashMap);
                  }

                  if (eligibilityTest.getResponseNoOption()) {
                    HashMap<String, Object> correctAnsHashMap = new HashMap<>();
                    correctAnsHashMap.put("key", eligibilityTest.getShortTitle());
                    correctAnsHashMap.put("answer", false);
                    correctAnswers.add(correctAnsHashMap);
                  }
                }
                eligibility.setTest(test);
                eligibility.setCorrectAnswers(correctAnswers);
              }
              eligibilityConsentResponse.setEligibility(eligibility);
            }
          }

          consentDto =
              (ConsentDto)
                  session
                      .getNamedQuery("consentDetailsByCustomStudyIdAndVersion")
                      .setString(
                          StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                          studyVersionDto.getCustomStudyId())
                      .setFloat(
                          StudyMetaDataEnum.QF_VERSION.value(), studyVersionDto.getConsentVersion())
                      .uniqueResult();
          if (null != consentDto) {
            consent.setVersion(
                consentDto.getVersion() == null
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : consentDto.getVersion().toString());

            SharingBean sharingBean = new SharingBean();

            /** check whether share data permission is yes or no */
            if (StringUtils.isNotEmpty(consentDto.getShareDataPermissions())
                && consentDto
                    .getShareDataPermissions()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              sharingBean.setTitle(
                  StringUtils.isEmpty(consentDto.getTitle()) ? "" : consentDto.getTitle());
              sharingBean.setText(
                  StringUtils.isEmpty(consentDto.getTaglineDescription())
                      ? ""
                      : consentDto.getTaglineDescription());
              sharingBean.setLearnMore(
                  StringUtils.isEmpty(consentDto.getLearnMoreText())
                      ? ""
                      : consentDto.getLearnMoreText());
              sharingBean.setLongDesc(
                  StringUtils.isEmpty(consentDto.getLongDescription())
                      ? ""
                      : consentDto.getLongDescription());
              sharingBean.setShortDesc(
                  StringUtils.isEmpty(consentDto.getShortDescription())
                      ? ""
                      : consentDto.getShortDescription());
              if ((consentDto.getAllowWithoutPermission() != null)
                  && StudyMetaDataConstants.YES.equalsIgnoreCase(
                      consentDto.getAllowWithoutPermission())) {
                sharingBean.setAllowWithoutSharing(true);
              }
            }
            consent.setSharing(sharingBean);
          }

          consentMasterInfoList = session.createQuery("from ConsentMasterInfoDto CMIDTO").list();

          if (studySequenceDto
              .getConsentEduInfo()
              .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)) {

            consentInfoDtoList =
                session
                    .getNamedQuery("consentInfoDetailsByCustomStudyIdAndVersion")
                    .setString(
                        StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(),
                        studyVersionDto.getCustomStudyId())
                    .setFloat(
                        StudyMetaDataEnum.QF_VERSION.value(), studyVersionDto.getConsentVersion())
                    .list();
            if ((null != consentInfoDtoList) && !consentInfoDtoList.isEmpty()) {

              List<ConsentBean> consentBeanList = new ArrayList<>();
              for (ConsentInfoDto consentInfoDto : consentInfoDtoList) {

                ConsentBean consentBean = new ConsentBean();
                consentBean.setText(
                    StringUtils.isEmpty(consentInfoDto.getBriefSummary())
                        ? ""
                        : consentInfoDto
                            .getBriefSummary()
                            .replaceAll("&#34;", "\"")
                            .replaceAll("&#39;", "'"));
                consentBean.setTitle(
                    StringUtils.isEmpty(consentInfoDto.getDisplayTitle())
                        ? ""
                        : consentInfoDto
                            .getDisplayTitle()
                            .replaceAll("&#34;", "\"")
                            .replaceAll("&#39;", "'"));
                if (consentInfoDto.getConsentItemTitleId() != null) {
                  if ((consentMasterInfoList != null) && !consentMasterInfoList.isEmpty()) {
                    for (ConsentMasterInfoDto masterInfo : consentMasterInfoList) {
                      if (masterInfo.getId().intValue()
                          == consentInfoDto.getConsentItemTitleId().intValue()) {
                        consentBean.setType(masterInfo.getCode());
                        break;
                      }
                    }
                  }
                } else {
                  consentBean.setType(StudyMetaDataConstants.CONSENT_TYPE_CUSTOM.toLowerCase());
                }
                consentBean.setDescription("");
                consentBean.setHtml(
                    StringUtils.isEmpty(consentInfoDto.getElaborated())
                        ? ""
                        : consentInfoDto
                            .getElaborated()
                            .replaceAll("&#34;", "'")
                            .replaceAll("em>", "i>")
                            .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'"));
                consentBean.setUrl(
                    StringUtils.isEmpty(consentInfoDto.getUrl()) ? "" : consentInfoDto.getUrl());

                if (StringUtils.isNotEmpty(consentInfoDto.getVisualStep())
                    && consentInfoDto
                        .getVisualStep()
                        .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
                  consentBean.setVisualStep(true);
                } else {
                  consentBean.setVisualStep(false);
                }
                consentBeanList.add(consentBean);
              }
              consent.setVisualScreens(consentBeanList);
            }
          }

          if (studySequenceDto
                  .getComprehensionTest()
                  .equalsIgnoreCase(StudyMetaDataConstants.STUDY_SEQUENCE_Y)
              && ((consentDto != null)
                  && (consentDto.getNeedComprehensionTest() != null)
                  && consentDto
                      .getNeedComprehensionTest()
                      .equalsIgnoreCase(StudyMetaDataConstants.YES))) {

            comprehensionQuestionList =
                session
                    .getNamedQuery("comprehensionQuestionByStudyId")
                    .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                    .list();
            if ((null != comprehensionQuestionList) && !comprehensionQuestionList.isEmpty()) {
              ComprehensionDetailsBean comprehensionDetailsBean = new ComprehensionDetailsBean();
              if ((consentDto != null) && (consentDto.getComprehensionTestMinimumScore() != null)) {
                comprehensionDetailsBean.setPassScore(
                    consentDto.getComprehensionTestMinimumScore());
              } else {
                comprehensionDetailsBean.setPassScore(0);
              }

              List<QuestionnaireActivityStepsBean> comprehensionList = new ArrayList<>();
              List<CorrectAnswersBean> correctAnswerBeanList = new ArrayList<>();
              for (ComprehensionTestQuestionDto comprehensionQuestionDto :
                  comprehensionQuestionList) {
                QuestionnaireActivityStepsBean questionStep = new QuestionnaireActivityStepsBean();
                questionStep.setType(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
                questionStep.setResultType(StudyMetaDataConstants.QUESTION_TEXT_CHOICE);
                questionStep.setKey(comprehensionQuestionDto.getId().toString());
                questionStep.setTitle(comprehensionQuestionDto.getQuestionText());
                questionStep.setText(StudyMetaDataConstants.COMPREHENSION_TEXT);
                questionStep.setSkippable(false);
                questionStep.setGroupName("");
                questionStep.setRepeatable(false);
                questionStep.setRepeatableText("");
                questionStep.setHealthDataKey("");

                List<ComprehensionTestResponseDto> comprehensionTestResponseList =
                    session
                        .getNamedQuery("comprehensionQuestionResponseByCTID")
                        .setInteger("comprehensionTestQuestionId", comprehensionQuestionDto.getId())
                        .list();
                if ((comprehensionTestResponseList != null)
                    && !comprehensionTestResponseList.isEmpty()) {

                  CorrectAnswersBean correctAnswerBean = new CorrectAnswersBean();
                  Map<String, Object> questionFormat = new LinkedHashMap<>();
                  List<LinkedHashMap<String, Object>> textChoiceMapList = new ArrayList<>();
                  StringBuilder sb = new StringBuilder();

                  for (ComprehensionTestResponseDto compResp : comprehensionTestResponseList) {
                    if (compResp.getCorrectAnswer()) {
                      sb.append(
                          StringUtils.isEmpty(sb)
                              ? compResp.getResponseOption().trim()
                              : "&@##@&" + compResp.getResponseOption().trim());
                    }
                    LinkedHashMap<String, Object> textChoiceMap = new LinkedHashMap<>();
                    textChoiceMap.put(
                        "text",
                        StringUtils.isEmpty(compResp.getResponseOption().trim())
                            ? ""
                            : compResp.getResponseOption().trim());
                    textChoiceMap.put(
                        "value",
                        StringUtils.isEmpty(compResp.getResponseOption().trim())
                            ? ""
                            : compResp.getResponseOption().trim());
                    textChoiceMap.put("detail", "");
                    textChoiceMap.put("exclusive", false);
                    textChoiceMapList.add(textChoiceMap);
                  }

                  questionFormat.put("textChoices", textChoiceMapList);

                  if (comprehensionQuestionDto.getStructureOfCorrectAns()) {
                    questionFormat.put("selectionStyle", "Multiple");
                  } else {
                    questionFormat.put("selectionStyle", "Single");
                  }

                  questionStep.setFormat(questionFormat);
                  if (StringUtils.isNotEmpty(sb.toString())) {
                    correctAnswerBean.setAnswer(sb.toString().split("&@##@&"));
                  }

                  correctAnswerBean.setKey(comprehensionQuestionDto.getId().toString());
                  correctAnswerBean.setEvaluation(
                      comprehensionQuestionDto.getStructureOfCorrectAns()
                          ? StudyMetaDataConstants.COMPREHENSION_RESPONSE_STRUCTURE_ALL
                          : StudyMetaDataConstants.COMPREHENSION_RESPONSE_STRUCTURE_ANY);
                  correctAnswerBeanList.add(correctAnswerBean);
                }

                comprehensionList.add(questionStep);
              }
              comprehensionDetailsBean.setQuestions(comprehensionList);
              comprehensionDetailsBean.setCorrectAnswers(correctAnswerBeanList);
              consent.setComprehension(comprehensionDetailsBean);
            }
          }

          if (consentDto != null) {
            ReviewBean reviewBean = new ReviewBean();
            if (consentDto
                .getConsentDocType()
                .equals(StudyMetaDataConstants.CONSENT_DOC_TYPE_NEW)) {
              reviewBean.setReviewHTML(
                  StringUtils.isEmpty(consentDto.getConsentDocContent())
                      ? ""
                      : consentDto
                          .getConsentDocContent()
                          .replaceAll("&#34;", "'")
                          .replaceAll("em>", "i>")
                          .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'"));
            }
            consent.setReview(reviewBean);
          }
          eligibilityConsentResponse.setConsent(consent);

          eligibilityConsentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
        }
      } else {
        eligibilityConsentResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - eligibilityConsentMetadata() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("eligibilityConsentMetadata() :: Ends");
    return eligibilityConsentResponse;
  }

  public ConsentDocumentResponse consentDocument(
      String studyId, String consentVersion, String activityId, String activityVersion)
      throws DAOException {
    LOGGER.entry("begin consentDocument()");
    Session session = null;
    ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
    ConsentDto consent = null;
    StudyDto studyDto = null;
    StudyVersionDto studyVersionDto = null;
    String studyVersionQuery = "from StudyVersionDto SVDTO where SVDTO.customStudyId=:studyId";
    try {
      session = sessionFactory.openSession();

      studyDto =
          (StudyDto)
              session
                  .getNamedQuery("getLiveStudyIdByCustomStudyId")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto == null) {
        studyDto =
            (StudyDto)
                session
                    .getNamedQuery("getPublishedStudyByCustomId")
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .uniqueResult();
      }

      if (studyDto != null) {
        if (StringUtils.isNotEmpty(consentVersion)) {
          studyVersionQuery += " and ROUND(SVDTO.consentVersion, 1)=:consentVersion";
        } else if (StringUtils.isNotEmpty(activityId) && StringUtils.isNotEmpty(activityVersion)) {
          studyVersionQuery += " and ROUND(SVDTO.activityVersion, 1)=:activityVersion";
        }

        /** Get study version details by version identifier in descending order */
        studyVersionQuery += " ORDER BY SVDTO.versionId DESC";

        if (!studyDto
            .getStatus()
            .equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)) {
          if (StringUtils.isNotEmpty(consentVersion)) {
            studyVersionDto =
                (StudyVersionDto)
                    session
                        .createQuery(studyVersionQuery)
                        .setMaxResults(1)
                        .setString("studyId", studyId)
                        .setString("consentVersion", consentVersion)
                        .uniqueResult();
          } else if (StringUtils.isNotEmpty(activityId)
              && StringUtils.isNotEmpty(activityVersion)) {
            studyVersionDto =
                (StudyVersionDto)
                    session
                        .createQuery(studyVersionQuery)
                        .setMaxResults(1)
                        .setString("studyId", studyId)
                        .setString("activityVersion", activityVersion)
                        .uniqueResult();
          } else {
            studyVersionDto =
                (StudyVersionDto)
                    session
                        .createQuery(studyVersionQuery)
                        .setMaxResults(1)
                        .setString("studyId", studyId)
                        .uniqueResult();
          }
        } else {
          studyVersionDto = new StudyVersionDto();
          studyVersionDto.setConsentVersion(0f);
        }
        if (studyVersionDto != null) {
          if (!studyDto
              .getStatus()
              .equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)) {
            consent =
                (ConsentDto)
                    session
                        .getNamedQuery("consentDetailsByCustomStudyIdAndVersion")
                        .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                        .setFloat(
                            StudyMetaDataEnum.QF_VERSION.value(),
                            studyVersionDto.getConsentVersion())
                        .uniqueResult();
          } else {
            consent =
                (ConsentDto)
                    session
                        .getNamedQuery("consentDtoByStudyId")
                        .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                        .uniqueResult();
          }

          if (consent != null) {
            ConsentDocumentBean consentDocumentBean = new ConsentDocumentBean();
            consentDocumentBean.setType("text/html");
            consentDocumentBean.setVersion(
                (consent.getVersion() == null)
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : String.valueOf(consent.getVersion()));
            consentDocumentBean.setContent(
                StringUtils.isEmpty(consent.getConsentDocContent())
                    ? ""
                    : consent
                        .getConsentDocContent()
                        .replaceAll("&#34;", "'")
                        .replaceAll("em>", "i>")
                        .replaceAll("<a", "<a style='text-decoration:underline;color:blue;'"));
            consentDocumentResponse.setConsent(consentDocumentBean);
          }
          consentDocumentResponse.setMessage(StudyMetaDataConstants.SUCCESS);
        }
      } else {
        consentDocumentResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - consentDocument() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("consentDocument() :: Ends");
    return consentDocumentResponse;
  }

  @SuppressWarnings("unchecked")
  public ResourcesResponse resourcesForStudy(String studyId) throws DAOException {
    LOGGER.entry("begin resourcesForStudy()");
    Session session = null;
    ResourcesResponse resourcesResponse = new ResourcesResponse();
    List<ResourcesDto> resourcesDtoList = null;
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();

      studyDto =
          (StudyDto)
              session
                  .getNamedQuery("getLiveStudyIdByCustomStudyId")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto != null) {

        resourcesDtoList =
            session
                .getNamedQuery("getResourcesListByStudyId")
                .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                .list();
        if ((null != resourcesDtoList) && !resourcesDtoList.isEmpty()) {

          List<ResourcesBean> resourcesBeanList = new ArrayList<>();
          for (ResourcesDto resourcesDto : resourcesDtoList) {

            ResourcesBean resourcesBean = new ResourcesBean();
            resourcesBean.setAudience(
                resourcesDto.isResourceType()
                    ? StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_LIMITED
                    : StudyMetaDataConstants.RESOURCE_AUDIENCE_TYPE_ALL);
            resourcesBean.setTitle(
                StringUtils.isEmpty(resourcesDto.getTitle()) ? "" : resourcesDto.getTitle());
            if (!resourcesDto.isTextOrPdf()) {
              resourcesBean.setType(StudyMetaDataConstants.TYPE_TEXT);
              resourcesBean.setContent(
                  StringUtils.isEmpty(resourcesDto.getRichText())
                      ? ""
                      : resourcesDto.getRichText());
            } else {
              resourcesBean.setType(StudyMetaDataConstants.TYPE_PDF);
              resourcesBean.setContent(
                  StringUtils.isEmpty(resourcesDto.getPdfUrl())
                      ? ""
                      : StudyMetaDataUtil.getSignedUrl(
                          propMap.get("cloud.bucket.name"),
                          propMap.get(StudyMetaDataConstants.FDA_SMD_RESOURCE_PDF_PATH).trim()
                              + resourcesDto.getPdfUrl(),
                          StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
            }
            resourcesBean.setResourcesId(
                resourcesDto.getId() == null ? "" : String.valueOf(resourcesDto.getId()));

            if (!resourcesDto.isResourceVisibility()) {
              Map<String, Object> availability = new LinkedHashMap<>();
              availability.put(
                  "availableDate",
                  StringUtils.isEmpty(resourcesDto.getStartDate())
                      ? ""
                      : resourcesDto.getStartDate());
              availability.put(
                  "expiryDate",
                  StringUtils.isEmpty(resourcesDto.getEndDate()) ? "" : resourcesDto.getEndDate());

              if (resourcesDto.getTimePeriodFromDays() != null) {
                availability.put(
                    "startDays",
                    resourcesDto.isxDaysSign()
                        ? Integer.parseInt("-" + resourcesDto.getTimePeriodFromDays())
                        : resourcesDto.getTimePeriodFromDays());
              } else {
                availability.put("startDays", 0);
              }

              if (resourcesDto.getTimePeriodToDays() != null) {
                availability.put(
                    "endDays",
                    resourcesDto.isyDaysSign()
                        ? Integer.parseInt("-" + resourcesDto.getTimePeriodToDays())
                        : resourcesDto.getTimePeriodToDays());
              } else {
                availability.put("endDays", 0);
              }
              /** Phase 2a anchordate */
              if (resourcesDto.getAnchorDateId() != null) {
                availability.put(
                    "availabilityType", StudyMetaDataConstants.SCHEDULETYPE_ANCHORDATE);
                String searchQuery = "";
                searchQuery = "from AnchorDateTypeDto a where a.id=:id";
                AnchorDateTypeDto anchorDateTypeDto =
                    (AnchorDateTypeDto)
                        session
                            .createQuery(searchQuery)
                            .setParameter("id", resourcesDto.getAnchorDateId())
                            .uniqueResult();
                if (anchorDateTypeDto != null) {
                  if (!anchorDateTypeDto
                      .getName()
                      .replace(" ", "")
                      .equalsIgnoreCase(StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE)) {
                    availability.put(
                        "sourceType", StudyMetaDataConstants.ANCHOR_TYPE_ACTIVITYRESPONSE);
                    searchQuery =
                        "select s.step_short_title,qr.short_title"
                            + " from questionnaires qr,questions q, questionnaires_steps s"
                            + " where"
                            + " s.questionnaires_id=qr.id"
                            + " and s.instruction_form_id=q.id"
                            + " and s.step_type='Question'"
                            + " and qr.custom_study_id=:studyId"
                            + " and qr.schedule_type=:scheduleType"
                            + " and qr.frequency = :frequencyType"
                            + " and q.anchor_date_id=:anchorDate";
                    List<?> result =
                        session
                            .createSQLQuery(searchQuery)
                            .setString("studyId", studyId)
                            .setString("scheduleType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                            .setString(
                                "frequencyType", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                            .setInteger("anchorDate", resourcesDto.getAnchorDateId())
                            .list();
                    if ((null != result) && !result.isEmpty()) {
                      Object[] objects = (Object[]) result.get(0);
                      availability.put("sourceKey", objects[0]);
                      availability.put("sourceActivityId", objects[1]);
                      availability.put("sourceFormKey", "");
                    } else {
                      String query = "";
                      query =
                          "select q.shortTitle, qsf.stepShortTitle ,qq.shortTitle as questionnaireShort"
                              + " from QuestionsDto q,FormMappingDto fm,FormDto f,QuestionnairesStepsDto qsf,QuestionnairesDto qq"
                              + " where"
                              + " q.id=fm.questionId"
                              + " and f.formId=fm.formId"
                              + " and f.formId=qsf.instructionFormId"
                              + " and qsf.stepType='Form'"
                              + " and qsf.questionnairesId=qq.id"
                              + " and q.anchorDateId=:anchorDate"
                              + " and qq.customStudyId=:studyId"
                              + " and qq.scheduleType=:scheduleType"
                              + " and qq.frequency = :frequencyType";
                      List<?> result1 =
                          session
                              .createQuery(query)
                              .setString("studyId", studyId)
                              .setString(
                                  "scheduleType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR)
                              .setString(
                                  "frequencyType", StudyMetaDataConstants.FREQUENCY_TYPE_ONE_TIME)
                              .setInteger("anchorDate", resourcesDto.getAnchorDateId())
                              .list();
                      if ((null != result1) && !result1.isEmpty()) {
                        Object[] objects = (Object[]) result1.get(0);
                        availability.put("sourceKey", objects[0]);
                        availability.put("sourceActivityId", objects[2]);
                        availability.put("sourceFormKey", objects[1]);
                      }
                    }
                  } else {
                    availability.put(
                        "sourceType", StudyMetaDataConstants.ANCHOR_TYPE_ENROLLMENTDATE);
                    availability.put("sourceKey", "");
                    availability.put("sourceActivityId", "");
                    availability.put("sourceFormKey", "");
                  }
                }

              } else {
                availability.put("availabilityType", StudyMetaDataConstants.SCHEDULETYPE_REGULAR);
              }
              /** phase 2a anchordate */
              resourcesBean.setAvailability(availability);
            }
            resourcesBean.setNotificationText(
                StringUtils.isEmpty(resourcesDto.getResourceText())
                    ? ""
                    : resourcesDto.getResourceText());
            resourcesBeanList.add(resourcesBean);
          }
          resourcesResponse.setResources(resourcesBeanList);
        }
        resourcesResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        resourcesResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - resourcesForStudy() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("resourcesForStudy() :: Ends");
    return resourcesResponse;
  }

  @SuppressWarnings("unchecked")
  public StudyInfoResponse studyInfo(String studyId) throws DAOException {
    LOGGER.entry("begin studyInfo()");
    Session session = null;
    StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
    List<StudyPageDto> studyPageDtoList = null;
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .getNamedQuery("getLiveStudyIdByCustomStudyId")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .uniqueResult();
      if (studyDto == null) {
        studyDto =
            (StudyDto)
                session
                    .getNamedQuery("getPublishedStudyByCustomId")
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .uniqueResult();
      }

      if (studyDto != null) {

        studyInfoResponse.setStudyWebsite(
            StringUtils.isEmpty(studyDto.getStudyWebsite()) ? "" : studyDto.getStudyWebsite());

        List<InfoBean> infoList = new ArrayList<>();
        studyPageDtoList =
            session
                .getNamedQuery("studyPageDetailsByStudyId")
                .setInteger(StudyMetaDataEnum.QF_STUDY_ID.value(), studyDto.getId())
                .list();
        if ((null != studyPageDtoList) && !studyPageDtoList.isEmpty()) {
          for (StudyPageDto studyPageInfo : studyPageDtoList) {
            InfoBean info = new InfoBean();

            if (infoList.isEmpty()) {
              info.setType(StudyMetaDataConstants.TYPE_VIDEO);
              info.setVideoLink(
                  StringUtils.isEmpty(studyDto.getMediaLink()) ? "" : studyDto.getMediaLink());
            } else {
              info.setType(StudyMetaDataConstants.TYPE_TEXT);
              info.setVideoLink("");
            }

            info.setTitle(
                StringUtils.isEmpty(studyPageInfo.getTitle()) ? "" : studyPageInfo.getTitle());
            info.setImage(
                StringUtils.isEmpty(studyPageInfo.getImagePath())
                    ? ""
                    : StudyMetaDataUtil.getSignedUrl(
                        propMap.get("cloud.bucket.name"),
                        propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_PAGE_PATH).trim()
                            + studyPageInfo.getImagePath(),
                        StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
            info.setText(
                StringUtils.isEmpty(studyPageInfo.getDescription())
                    ? ""
                    : studyPageInfo.getDescription());
            infoList.add(info);
          }
        } else {

          InfoBean info = new InfoBean();

          if (infoList.isEmpty()) {
            info.setType(StudyMetaDataConstants.TYPE_VIDEO);
            info.setVideoLink(
                StringUtils.isEmpty(studyDto.getMediaLink()) ? "" : studyDto.getMediaLink());
          } else {
            info.setType(StudyMetaDataConstants.TYPE_TEXT);
            info.setVideoLink("");
          }

          info.setTitle(StringUtils.isEmpty(studyDto.getName()) ? "" : studyDto.getName());
          info.setImage(
              StringUtils.isEmpty(studyDto.getThumbnailImage())
                  ? ""
                  : StudyMetaDataUtil.getSignedUrl(
                      propMap.get("cloud.bucket.name"),
                      propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH).trim()
                          + studyDto.getThumbnailImage(),
                      StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
          info.setText(StringUtils.isEmpty(studyDto.getFullName()) ? "" : studyDto.getFullName());
          infoList.add(info);
        }
        studyInfoResponse.setInfo(infoList);

        if (!studyDto
            .getStatus()
            .equalsIgnoreCase(StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)) {
          List<QuestionnairesDto> questionnairesList =
              session
                  .createQuery(
                      "from QuestionnairesDto QDTO"
                          + " where QDTO.customStudyId=:custStudyId"
                          + " and QDTO.active=true"
                          + " and QDTO.status=true and QDTO.live=1")
                  .setString("custStudyId", studyDto.getCustomStudyId())
                  .list();
          if ((questionnairesList != null) && !questionnairesList.isEmpty()) {

            List<Integer> questionnaireIdsList = new ArrayList<>();
            Map<Integer, QuestionnairesDto> questionnaireMap = new TreeMap<>();
            Map<String, QuestionnairesStepsDto> stepsMap = new TreeMap<>();
            Map<Integer, QuestionsDto> questionsMap = null;
            Map<Integer, FormMappingDto> formMappingMap = new TreeMap<>();

            for (QuestionnairesDto questionnaire : questionnairesList) {
              questionnaireIdsList.add(questionnaire.getId());
              questionnaireMap.put(questionnaire.getId(), questionnaire);
            }

            if (!questionnaireIdsList.isEmpty()) {

              List<Integer> questionIdsList = new ArrayList<>();
              List<Integer> formIdsList = new ArrayList<>();
              List<QuestionnairesStepsDto> questionnairesStepsList =
                  session
                      .createQuery(
                          "from QuestionnairesStepsDto QSDTO"
                              + " where QSDTO.active=true and QSDTO.status=true"
                              + " and QSDTO.questionnairesId in (:questionnaireIdsList)"
                              + " and QSDTO.stepType in (:questType,:questForm)")
                      .setParameterList("questionnaireIdsList", questionnaireIdsList)
                      .setString(
                          "questType", StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)
                      .setString("questForm", StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)
                      .list();
              if ((questionnairesStepsList != null) && !questionnairesStepsList.isEmpty()) {

                for (QuestionnairesStepsDto stepsDto : questionnairesStepsList) {
                  if (stepsDto
                      .getStepType()
                      .equalsIgnoreCase(StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION)) {
                    questionIdsList.add(stepsDto.getInstructionFormId());
                    stepsMap.put(
                        stepsDto.getInstructionFormId()
                            + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION,
                        stepsDto);
                  } else {
                    formIdsList.add(stepsDto.getInstructionFormId());
                    stepsMap.put(
                        stepsDto.getInstructionFormId()
                            + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM,
                        stepsDto);
                  }
                }

                if (!questionIdsList.isEmpty()) {
                  List<QuestionsDto> questionnsList =
                      session
                          .createQuery(
                              "from QuestionsDto QDTO"
                                  + " where QDTO.active=true and QDTO.status=true"
                                  + " and QDTO.id in (:questionIdsList)"
                                  + " and QDTO.responseType=10 and QDTO.useAnchorDate=true")
                          .setMaxResults(1)
                          .setParameterList("questionIdsList", questionIdsList)
                          .list();
                  if ((questionnsList != null) && !questionnsList.isEmpty()) {

                    questionsMap = new TreeMap<>();
                    for (QuestionsDto question : questionnsList) {
                      questionsMap.put(question.getId(), question);
                    }
                  }
                }

                if ((questionsMap == null) && !formIdsList.isEmpty()) {

                  List<Integer> formQuestionsList = new ArrayList<>();
                  List<FormMappingDto> formMappingList =
                      session
                          .createQuery(
                              "from FormMappingDto FMDTO"
                                  + " where FMDTO.formId in (select FDTO.formId"
                                  + " from FormDto FDTO"
                                  + " where FDTO.formId in (:formIdsList)"
                                  + " and FDTO.active=true) and FMDTO.active=true"
                                  + " ORDER BY FMDTO.formId, FMDTO.sequenceNo")
                          .setParameterList("formIdsList", formIdsList)
                          .list();
                  if ((formMappingList != null) && !formMappingList.isEmpty()) {

                    for (FormMappingDto formMapping : formMappingList) {
                      formQuestionsList.add(formMapping.getQuestionId());
                      formMappingMap.put(formMapping.getQuestionId(), formMapping);
                    }

                    if (!formQuestionsList.isEmpty()) {
                      List<QuestionsDto> questionnsList =
                          session
                              .createQuery(
                                  "from QuestionsDto QDTO"
                                      + " where QDTO.active=true and QDTO.status=true"
                                      + " and QDTO.id in (:formQuestionsList)"
                                      + " and QDTO.responseType=10 and QDTO.useAnchorDate=true")
                              .setMaxResults(1)
                              .setParameterList("formQuestionsList", formQuestionsList)
                              .list();
                      if ((questionnsList != null) && !questionnsList.isEmpty()) {

                        questionsMap = new TreeMap<>();
                        for (QuestionsDto question : questionnsList) {
                          questionsMap.put(question.getId(), question);
                        }
                      }
                    }
                  }
                }

                if (questionsMap != null) {
                  AnchorDateBean anchorDate = new AnchorDateBean();
                  anchorDate.setType(StudyMetaDataConstants.ANCHORDATE_TYPE_QUESTION);
                  for (Map.Entry<Integer, QuestionsDto> map : questionsMap.entrySet()) {
                    QuestionsDto questionDto = map.getValue();
                    if (questionDto != null) {
                      QuestionnairesStepsDto questionnairesSteps;

                      if (StringUtils.isNotEmpty(questionDto.getShortTitle())) {
                        FormMappingDto formMapping = formMappingMap.get(questionDto.getId());
                        questionnairesSteps =
                            stepsMap.get(
                                formMapping.getFormId()
                                    + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM);
                      } else {
                        questionnairesSteps =
                            stepsMap.get(
                                questionDto.getId()
                                    + StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_QUESTION);
                      }

                      if (questionnairesSteps != null) {
                        QuestionnairesDto questionnairesDto =
                            questionnaireMap.get(questionnairesSteps.getQuestionnairesId());

                        if (questionnairesDto != null) {
                          QuestionInfoBean questionInfoBean = new QuestionInfoBean();
                          questionInfoBean.setActivityId(questionnairesDto.getShortTitle());
                          questionInfoBean.setActivityVersion(
                              questionnairesDto.getVersion().toString());

                          if (questionnairesSteps
                              .getStepType()
                              .equalsIgnoreCase(
                                  StudyMetaDataConstants.QUESTIONAIRE_STEP_TYPE_FORM)) {
                            questionInfoBean.setKey(questionDto.getShortTitle());
                          } else {
                            questionInfoBean.setKey(questionnairesSteps.getStepShortTitle());
                          }
                          anchorDate.setQuestionInfo(questionInfoBean);
                        }
                      }
                    }
                  }
                  studyInfoResponse.setAnchorDate(anchorDate);
                }
              }
            }
          }
        }
        studyInfoResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      } else {
        studyInfoResponse.setMessage(StudyMetaDataConstants.INVALID_STUDY_ID);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - studyInfo() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("studyInfo() :: Ends");
    return studyInfoResponse;
  }

  public boolean isValidStudy(String studyId) throws DAOException {
    LOGGER.entry("begin isValidStudy()");
    Session session = null;
    boolean isValidStudy = false;
    StudyDto studyDto = null;
    try {
      session = sessionFactory.openSession();
      studyDto =
          (StudyDto)
              session
                  .createQuery(
                      "from StudyDto SDTO"
                          + " where SDTO.customStudyId= :customStudyId"
                          + " ORDER BY SDTO.id DESC")
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setMaxResults(1)
                  .uniqueResult();
      isValidStudy = (studyDto == null) ? false : true;
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidStudy() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("StudyMetaDataOrchestration - isValidStudy() :: Ends");
    return isValidStudy;
  }

  public boolean isValidActivity(String activityId, String studyId, String activityVersion)
      throws DAOException {
    LOGGER.entry("begin isValidActivity()");
    Session session = null;
    boolean isValidActivity = false;
    ActiveTaskDto activeTaskDto = null;
    QuestionnairesDto questionnaireDto = null;
    try {
      session = sessionFactory.openSession();
      activeTaskDto =
          (ActiveTaskDto)
              session
                  .createQuery(
                      "from ActiveTaskDto ATDTO"
                          + " where ATDTO.shortTitle= :shortTitle"
                          + " and ROUND(ATDTO.version, 1)= :version and ATDTO.customStudyId= :customStudyId"
                          + " ORDER BY ATDTO.id DESC")
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setMaxResults(1)
                  .uniqueResult();
      isValidActivity = (activeTaskDto == null) ? false : true;

      if (!isValidActivity) {
        questionnaireDto =
            (QuestionnairesDto)
                session
                    .createQuery(
                        "from QuestionnairesDto QDTO"
                            + " where QDTO.shortTitle= :shortTitle"
                            + " and ROUND(QDTO.version, 1)= :version and QDTO.customStudyId= :customStudyId"
                            + " ORDER BY QDTO.id DESC")
                    .setString(
                        StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                        StudyMetaDataUtil.replaceSingleQuotes(activityId))
                    .setFloat(
                        StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                    .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                    .setMaxResults(1)
                    .uniqueResult();
        isValidActivity = (questionnaireDto == null) ? false : true;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isValidActivity() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("StudyMetaDataOrchestration - isValidActivity() :: Ends");
    return isValidActivity;
  }

  public boolean isActivityTypeQuestionnaire(
      String activityId, String studyId, String activityVersion) throws DAOException {
    LOGGER.entry("begin isActivityTypeQuestionnaire()");
    Session session = null;
    boolean isActivityTypeQuestionnaire = true;
    ActiveTaskDto activeTaskDto = null;
    try {
      session = sessionFactory.openSession();
      activeTaskDto =
          (ActiveTaskDto)
              session
                  .createQuery(
                      "from ActiveTaskDto ATDTO"
                          + " where ATDTO.shortTitle= :shortTitle"
                          + " and ROUND(ATDTO.version, 1)= :version and ATDTO.customStudyId= :customStudyId"
                          + " ORDER BY ATDTO.id DESC")
                  .setString(
                      StudyMetaDataEnum.QF_SHORT_TITLE.value(),
                      StudyMetaDataUtil.replaceSingleQuotes(activityId))
                  .setFloat(StudyMetaDataEnum.QF_VERSION.value(), Float.parseFloat(activityVersion))
                  .setString(StudyMetaDataEnum.QF_CUSTOM_STUDY_ID.value(), studyId)
                  .setMaxResults(1)
                  .uniqueResult();
      if (activeTaskDto != null) {
        isActivityTypeQuestionnaire = false;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("StudyMetaDataOrchestration - isActivityTypeQuestionnaire() :: Ends");
    return isActivityTypeQuestionnaire;
  }

  public String getconsentDocumentDisplayTitle(String displaytitle) throws DAOException {
    LOGGER.entry("begin getconsentDocumentDisplayTitle()");
    String consentTitle = "";
    try {
      switch (displaytitle) {
        case "overview":
          consentTitle = "Overview";
          break;
        case "dataGathering":
          consentTitle = "Data Gathering";
          break;
        case "privacy":
          consentTitle = "Privacy";
          break;
        case "dataUse":
          consentTitle = "Data Use";
          break;
        case "timeCommitment":
          consentTitle = "Time Commitment";
          break;
        case "studySurvey":
          consentTitle = "Study Survey";
          break;
        case "studyTasks":
          consentTitle = "Study Tasks";
          break;
        case "withdrawing":
          consentTitle = "Withdrawing";
          break;
        case "customService":
          consentTitle = "Custom Service";
          break;
        default:
          consentTitle = displaytitle;
          break;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - getconsentDocumentDisplayTitle() :: ERROR", e);
    }
    LOGGER.exit("getconsentDocumentDisplayTitle() :: Ends");
    return consentTitle;
  }

  public boolean isValidToken(String token) throws DAOException {
    LOGGER.entry("begin isValidToken()");
    Session session = null;
    boolean isValidStudy = false;
    EnrollmentTokenDto tokenDto = null;
    try {
      session = sessionFactory.openSession();
      tokenDto =
          (EnrollmentTokenDto)
              session
                  .createQuery(
                      "from EnrollmentTokenDto "
                          + " where enrollmentToken= :enrollmentToken"
                          + " ORDER BY id DESC")
                  .setString("enrollmentToken", token)
                  .setMaxResults(1)
                  .uniqueResult();
      if (null != tokenDto) {
        isValidStudy = true;
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - isValidToken() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("isValidToken() :: Ends");
    return isValidStudy;
  }

  @SuppressWarnings("unchecked")
  public StudyResponse study(String studyId) throws DAOException {
    LOGGER.entry("begin study()");
    Session session = null;
    StudyResponse studyResponse = new StudyResponse();
    List<StudyDto> studiesList = null;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        session = sessionFactory.openSession();

        studiesList =
            session
                .createQuery(
                    "from StudyDto SDTO"
                        + " where SDTO.customStudyId =:studyId"
                        + " and (SDTO.status= :status OR SDTO.live=1)")
                .setString("studyId", studyId)
                .setString(
                    StudyMetaDataEnum.QF_STATUS.value(),
                    StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH)
                .list();
        if ((null != studiesList) && !studiesList.isEmpty()) {
          List<StudyBean> studyBeanList = new ArrayList<>();
          for (StudyDto studyDto : studiesList) {

            StudyBean studyBean = new StudyBean();
            studyBean.setStudyVersion(
                studyDto.getVersion() == null
                    ? StudyMetaDataConstants.STUDY_DEFAULT_VERSION
                    : studyDto.getVersion().toString());
            studyBean.setTagline(
                StringUtils.isEmpty(studyDto.getStudyTagline()) ? "" : studyDto.getStudyTagline());

            switch (studyDto.getStatus()) {
              case StudyMetaDataConstants.STUDY_STATUS_ACTIVE:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_ACTIVE);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PAUSED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_PAUSED);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_PRE_PUBLISH:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_UPCOMING);
                break;
              case StudyMetaDataConstants.STUDY_STATUS_DEACTIVATED:
                studyBean.setStatus(StudyMetaDataConstants.STUDY_CLOSED);
                break;
              default:
                break;
            }

            studyBean.setTitle(StringUtils.isEmpty(studyDto.getName()) ? "" : studyDto.getName());
            studyBean.setLogo(
                StringUtils.isEmpty(studyDto.getThumbnailImage())
                    ? ""
                    : StudyMetaDataUtil.getSignedUrl(
                        propMap.get("cloud.bucket.name"),
                        propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH).trim()
                            + studyDto.getThumbnailImage(),
                        StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
            studyBean.setStudyId(
                StringUtils.isEmpty(studyDto.getCustomStudyId())
                    ? ""
                    : studyDto.getCustomStudyId());

            studyBean.setSponsorName(
                StringUtils.isEmpty(studyDto.getResearchSponsor())
                    ? ""
                    : studyDto.getResearchSponsor());

            if (StringUtils.isNotEmpty(studyDto.getCategory())
                && StringUtils.isNotEmpty(studyDto.getResearchSponsor())) {

              List<ReferenceTablesDto> referenceTablesList =
                  session
                      .createQuery("from ReferenceTablesDto RTDTO where RTDTO.id IN (:category)")
                      .setString("category", studyDto.getCategory())
                      .list();
              if ((null != referenceTablesList) && !referenceTablesList.isEmpty()) {
                for (ReferenceTablesDto reference : referenceTablesList) {
                  if (reference
                      .getCategory()
                      .equalsIgnoreCase(StudyMetaDataConstants.STUDY_REF_CATEGORIES)) {
                    studyBean.setCategory(
                        StringUtils.isEmpty(reference.getValue()) ? "" : reference.getValue());
                  }
                }
              }
            }

            SettingsBean settings = new SettingsBean();
            if (studyDto.getPlatform().contains(",")) {
              settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ALL);
            } else {
              switch (studyDto.getPlatform()) {
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_IOS);
                  break;
                case StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID:
                  settings.setPlatform(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID);
                  break;
                default:
                  break;
              }
            }

            if (StringUtils.isNotEmpty(studyDto.getEnrollingParticipants())
                && studyDto
                    .getEnrollingParticipants()
                    .equalsIgnoreCase(StudyMetaDataConstants.YES)) {
              settings.setEnrolling(true);
            } else {
              settings.setEnrolling(false);
            }
            studyBean.setSettings(settings);
            studyBeanList.add(studyBean);
          }
          studyResponse.setStudies(studyBeanList);
        }
        studyResponse.setMessage(StudyMetaDataConstants.SUCCESS);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataDao - study() :: ERROR", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    LOGGER.exit("study() :: Ends");
    return studyResponse;
  }
}
