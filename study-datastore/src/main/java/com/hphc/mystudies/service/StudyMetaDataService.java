/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hphc.mystudies.service;

import com.hphc.mystudies.bean.ActiveTaskActivityMetaDataResponse;
import com.hphc.mystudies.bean.ActivityResponse;
import com.hphc.mystudies.bean.AppUpdatesResponse;
import com.hphc.mystudies.bean.AppVersionInfoBean;
import com.hphc.mystudies.bean.ConsentDocumentResponse;
import com.hphc.mystudies.bean.EligibilityConsentResponse;
import com.hphc.mystudies.bean.EnrollmentTokenResponse;
import com.hphc.mystudies.bean.ErrorResponse;
import com.hphc.mystudies.bean.GatewayInfoResponse;
import com.hphc.mystudies.bean.InfoBean;
import com.hphc.mystudies.bean.NotificationsResponse;
import com.hphc.mystudies.bean.QuestionnaireActivityMetaDataResponse;
import com.hphc.mystudies.bean.ResourcesResponse;
import com.hphc.mystudies.bean.StudyBean;
import com.hphc.mystudies.bean.StudyDashboardResponse;
import com.hphc.mystudies.bean.StudyInfoResponse;
import com.hphc.mystudies.bean.StudyResponse;
import com.hphc.mystudies.bean.StudyUpdatesResponse;
import com.hphc.mystudies.bean.TermsPolicyResponse;
import com.hphc.mystudies.exception.ErrorCodes;
import com.hphc.mystudies.integration.ActivityMetaDataOrchestration;
import com.hphc.mystudies.integration.AppMetaDataOrchestration;
import com.hphc.mystudies.integration.DashboardMetaDataOrchestration;
import com.hphc.mystudies.integration.StudyMetaDataOrchestration;
import com.hphc.mystudies.util.StudyMetaDataConstants;
import com.hphc.mystudies.util.StudyMetaDataEnum;
import com.hphc.mystudies.util.StudyMetaDataUtil;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

@Path("/")
public class StudyMetaDataService {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(StudyMetaDataService.class.getName());

  @SuppressWarnings("unchecked")
  HashMap<String, String> propMap = StudyMetaDataUtil.getAppProperties();

  StudyMetaDataOrchestration studyMetaDataOrchestration = new StudyMetaDataOrchestration();
  ActivityMetaDataOrchestration activityMetaDataOrchestration = new ActivityMetaDataOrchestration();
  DashboardMetaDataOrchestration dashboardMetaDataOrchestration =
      new DashboardMetaDataOrchestration();
  AppMetaDataOrchestration appMetaDataOrchestration = new AppMetaDataOrchestration();

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("gatewayInfo")
  public Object gatewayAppResourcesInfo(
      @HeaderParam("Authorization") String authorization,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin gatewayAppResourcesInfo()");
    GatewayInfoResponse gatewayInfo = new GatewayInfoResponse();
    try {
      gatewayInfo = studyMetaDataOrchestration.gatewayAppResourcesInfo(authorization);
      if (!gatewayInfo.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
        return Response.status(Response.Status.NOT_FOUND)
            .entity(StudyMetaDataConstants.NO_RECORD)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - gatewayAppResourcesInfo() :: ERROR ", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("gatewayAppResourcesInfo() :: Ends");
    return gatewayInfo;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("studyList")
  public Object studyList(
      @HeaderParam("Authorization") String authorization,
      @HeaderParam("applicationId") String applicationId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin studyList()");
    StudyResponse studyResponse = new StudyResponse();
    try {
      if (!StringUtils.isEmpty(authorization) && !StringUtils.isEmpty(applicationId)) {
        studyResponse = studyMetaDataOrchestration.studyList(authorization, applicationId);
        if (!studyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        } else {
          List<StudyBean> studyBeanInfo = studyResponse.getStudies();

          if (!studyBeanInfo.isEmpty()) {
            for (StudyBean studyBeanObject : studyBeanInfo) {
              String logo = studyBeanObject.getLogo();
              if (logo == null || logo.isEmpty()) {
                studyBeanObject.setLogo(
                    StudyMetaDataUtil.getSignedUrl(
                        propMap.get("cloud.bucket.name"),
                        propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH).trim()
                            + propMap.get(
                                StudyMetaDataConstants.STUDY_BASICINFORMATION_DEFAULT_IMAGE),
                        StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
              }
            }
          }
        }
      } else {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - studyList() :: ERROR ", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("studyList() :: Ends");
    return studyResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("eligibilityConsent")
  public Object eligibilityConsentMetadata(
      @QueryParam("studyId") String studyId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin eligibilityConsentMetadata()");
    EligibilityConsentResponse eligibilityConsentResponse = new EligibilityConsentResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        eligibilityConsentResponse = studyMetaDataOrchestration.eligibilityConsentMetadata(studyId);
        if (!eligibilityConsentResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - eligibilityConsentMetadata() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("eligibilityConsentMetadata() :: Ends");
    return eligibilityConsentResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("consentDocument")
  public Object consentDocument(
      @QueryParam("studyId") String studyId,
      @QueryParam("consentVersion") String consentVersion,
      @QueryParam("activityId") String activityId,
      @QueryParam("activityVersion") String activityVersion,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin resourcesForStudy()");
    ConsentDocumentResponse consentDocumentResponse = new ConsentDocumentResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        consentDocumentResponse =
            studyMetaDataOrchestration.consentDocument(
                studyId, consentVersion, activityId, activityVersion);
        if (!consentDocumentResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - resourcesForStudy() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("resourcesForStudy() :: Ends");
    return consentDocumentResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("resources")
  public Object resourcesForStudy(
      @QueryParam("studyId") String studyId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin resourcesForStudy()");
    ResourcesResponse resourcesResponse = new ResourcesResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        resourcesResponse = studyMetaDataOrchestration.resourcesForStudy(studyId);
        if (!resourcesResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - resourcesForStudy() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("resourcesForStudy() :: Ends");
    return resourcesResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("studyInfo")
  public Object studyInfo(
      @QueryParam("studyId") String studyId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin studyInfo()");
    StudyInfoResponse studyInfoResponse = new StudyInfoResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        studyInfoResponse = studyMetaDataOrchestration.studyInfo(studyId);
        if (!studyInfoResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }

      List<InfoBean> infoBeans = studyInfoResponse.getInfo();

      int count = 0;

      for (InfoBean infoBean : infoBeans) {
        if (infoBean.getImage() == null || infoBean.getImage().equals("")) {
          if (count == 0) {
            infoBean.setImage(
                StudyMetaDataUtil.getSignedUrl(
                    propMap.get("cloud.bucket.name"),
                    propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH).trim()
                        + propMap.get(StudyMetaDataConstants.STUDY_DEFAULT_IMAGE),
                    StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
          } else {
            infoBean.setImage(
                StudyMetaDataUtil.getSignedUrl(
                    propMap.get("cloud.bucket.name"),
                    propMap.get(StudyMetaDataConstants.FDA_SMD_STUDY_THUMBNAIL_PATH).trim()
                        + propMap.get(StudyMetaDataConstants.STUDY_PAGE2_DEFAULT_IMAGE),
                    StudyMetaDataConstants.SIGNED_URL_DURATION_IN_HOURS));
          }
        }
        count++;
      }

    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - studyInfo() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("studyInfo() :: Ends");
    return studyInfoResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("activityList")
  public Object studyActivityList(
      @HeaderParam("Authorization") String authorization,
      @QueryParam("studyId") String studyId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin studyActivityList()");
    ActivityResponse activityResponse = new ActivityResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        activityResponse = activityMetaDataOrchestration.studyActivityList(studyId, authorization);
        if (!activityResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - studyActivityList() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("studyActivityList() :: Ends");
    return activityResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("activity")
  public Object studyActivityMetadata(
      @QueryParam("studyId") String studyId,
      @QueryParam("activityId") String activityId,
      @QueryParam("activityVersion") String activityVersion,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin studyActivityMetadata()");
    QuestionnaireActivityMetaDataResponse questionnaireActivityMetaDataResponse =
        new QuestionnaireActivityMetaDataResponse();
    ActiveTaskActivityMetaDataResponse activeTaskActivityMetaDataResponse =
        new ActiveTaskActivityMetaDataResponse();
    Boolean isValidFlag = false;
    Boolean isActivityTypeQuestionnaire = false;
    try {
      if (StringUtils.isNotEmpty(studyId)
          && StringUtils.isNotEmpty(activityId)
          && StringUtils.isNotEmpty(activityVersion)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        isValidFlag =
            studyMetaDataOrchestration.isValidActivity(activityId, studyId, activityVersion);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_ACTIVITY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_ACTIVITY_ID)
              .build();
        }

        isActivityTypeQuestionnaire =
            studyMetaDataOrchestration.isActivityTypeQuestionnaire(
                activityId, studyId, activityVersion);
        if (!isActivityTypeQuestionnaire) {
          activeTaskActivityMetaDataResponse =
              activityMetaDataOrchestration.studyActiveTaskActivityMetadata(
                  studyId, activityId, activityVersion);
          if (!activeTaskActivityMetaDataResponse
              .getMessage()
              .equals(StudyMetaDataConstants.SUCCESS)) {
            StudyMetaDataUtil.getFailureResponse(
                ErrorCodes.STATUS_103,
                ErrorCodes.NO_DATA,
                StudyMetaDataConstants.FAILURE,
                response);
            return Response.status(Response.Status.NO_CONTENT)
                .entity(StudyMetaDataConstants.NO_RECORD)
                .build();
          }
          return activeTaskActivityMetaDataResponse;
        } else {
          questionnaireActivityMetaDataResponse =
              activityMetaDataOrchestration.studyQuestionnaireActivityMetadata(
                  studyId, activityId, activityVersion);
          if (!questionnaireActivityMetaDataResponse
              .getMessage()
              .equals(StudyMetaDataConstants.SUCCESS)) {
            StudyMetaDataUtil.getFailureResponse(
                ErrorCodes.STATUS_103,
                ErrorCodes.NO_DATA,
                StudyMetaDataConstants.FAILURE,
                response);
            return Response.status(Response.Status.NO_CONTENT)
                .entity(StudyMetaDataConstants.NO_RECORD)
                .build();
          }
          return questionnaireActivityMetaDataResponse;
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }

    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - studyActivityMetadata() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("studyDashboard")
  public Object studyDashboardInfo(
      @QueryParam("studyId") String studyId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin studyDashboardInfo()");
    StudyDashboardResponse studyDashboardResponse = new StudyDashboardResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(studyId)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        studyDashboardResponse = dashboardMetaDataOrchestration.studyDashboardInfo(studyId);
        if (!studyDashboardResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - studyDashboardInfo() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("studyDashboardInfo() :: Ends");
    return studyDashboardResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("termsPolicy")
  public Object termsPolicy(
      @Context ServletContext context, @Context HttpServletResponse response) {
    LOGGER.entry("begin termsPolicy()");
    TermsPolicyResponse termsPolicyResponse = new TermsPolicyResponse();
    try {
      termsPolicyResponse = appMetaDataOrchestration.termsPolicy();
      if (!termsPolicyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
        return Response.status(Response.Status.NO_CONTENT)
            .entity(StudyMetaDataConstants.NO_RECORD)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - termsPolicy() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("termsPolicy() :: Ends");
    return termsPolicyResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("notifications")
  public Object notifications(
      @QueryParam("skip") String skip,
      @HeaderParam("Authorization") String authorization,
      @HeaderParam("applicationId") String appId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin notifications()");
    NotificationsResponse notificationsResponse = new NotificationsResponse();
    try {
      if (StringUtils.isNotEmpty(skip)) {
        notificationsResponse = appMetaDataOrchestration.notifications(skip, authorization, appId);
        if (!notificationsResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - notifications() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("notifications() :: Ends");
    return notificationsResponse;
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("appUpdates")
  public Object appUpdates(
      @QueryParam("appVersion") String appVersion,
      @HeaderParam("Authorization") String authorization,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin appUpdates()");
    AppUpdatesResponse appUpdatesResponse = new AppUpdatesResponse();
    try {
      if (StringUtils.isNotEmpty(appVersion) && StringUtils.isNotEmpty(authorization)) {
        appUpdatesResponse = appMetaDataOrchestration.appUpdates(appVersion, authorization);
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.UNKNOWN,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("appUpdates() :: Ends");
    return appUpdatesResponse;
  }

  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Path("studyUpdates")
  public Object studyUpdates(
      @QueryParam("studyId") String studyId,
      @QueryParam("studyVersion") String studyVersion,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin studyUpdates()");
    StudyUpdatesResponse studyUpdatesResponse = new StudyUpdatesResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(studyVersion)) {
        isValidFlag = studyMetaDataOrchestration.isValidStudy(studyId);
        if (!isValidFlag) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_STUDY_ID,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_STUDY_ID)
              .build();
        }

        studyUpdatesResponse = appMetaDataOrchestration.studyUpdates(studyId, studyVersion);
        if (!studyUpdatesResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.UNKNOWN,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - appUpdates() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("studyUpdates() :: Ends");
    return studyUpdatesResponse;
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("updateAppVersion")
  public Object updateAppVersionDetails(
      String params, @Context ServletContext context, @Context HttpServletResponse response) {
    LOGGER.entry("begin updateAppVersionDetails()");
    String updateAppVersionResponse = "OOPS! Something went wrong.";
    try {
      JSONObject serviceJson = new JSONObject(params);
      String forceUpdate = serviceJson.getString(StudyMetaDataEnum.RP_FORCE_UPDATE.value());
      String osType = serviceJson.getString(StudyMetaDataEnum.RP_OS_TYPE.value());
      String appVersion = serviceJson.getString(StudyMetaDataEnum.RP_APP_VERSION.value());
      String bundleId = serviceJson.getString(StudyMetaDataEnum.RP_BUNDLE_IDENTIFIER.value());
      String customStudyId = serviceJson.getString(StudyMetaDataEnum.RP_STUDY_IDENTIFIER.value());
      String message = serviceJson.getString(StudyMetaDataEnum.RP_MESSAGE.value());
      if (StringUtils.isNotEmpty(forceUpdate)
          && StringUtils.isNotEmpty(osType)
          && StringUtils.isNotEmpty(appVersion)
          && StringUtils.isNotEmpty(bundleId)
          && StringUtils.isNotEmpty(message)) {
        if (Integer.parseInt(forceUpdate) > 1) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.UNKNOWN,
              StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
              response);
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(StudyMetaDataConstants.INVALID_INPUT)
              .build();
        }

        if (!osType.equals(StudyMetaDataConstants.STUDY_PLATFORM_IOS)
            && !osType.equals(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.UNKNOWN,
              StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
              response);
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(StudyMetaDataConstants.INVALID_INPUT)
              .build();
        }

        if (Float.parseFloat(appVersion) < 1) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.UNKNOWN,
              StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
              response);
          return Response.status(Response.Status.BAD_REQUEST)
              .entity(StudyMetaDataConstants.INVALID_INPUT)
              .build();
        }

        updateAppVersionResponse =
            appMetaDataOrchestration.updateAppVersionDetails(
                forceUpdate, osType, appVersion, bundleId, customStudyId, message);
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.UNKNOWN,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - updateAppVersionDetails() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("updateAppVersionDetails() :: Ends");
    return updateAppVersionResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("validateEnrollmentToken")
  public Object validateEnrollmentToken(
      @QueryParam("token") String token,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin validateEnrollmentToken()");
    EnrollmentTokenResponse enrollmentTokenResponse = new EnrollmentTokenResponse();
    Boolean isValidFlag = false;
    try {
      if (StringUtils.isNotEmpty(token)) {
        isValidFlag = studyMetaDataOrchestration.isValidToken(token);
        if (isValidFlag) {
          enrollmentTokenResponse.setMessage(StudyMetaDataConstants.SUCCESS);
        } else {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_102,
              ErrorCodes.INVALID_INPUT,
              StudyMetaDataConstants.INVALID_ENROLLMENT_TOKEN,
              response);
          return Response.status(Response.Status.NOT_FOUND)
              .entity(StudyMetaDataConstants.INVALID_ENROLLMENT_TOKEN)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - validateEnrollmentToken() :: ERROR", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("validateEnrollmentToken() :: Ends");
    return enrollmentTokenResponse;
  }

  @GET
  @Path("healthCheck")
  public String healthCheck() {
    return "200 OK!";
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("study")
  public Object study(
      @QueryParam("studyId") String studyId,
      @Context ServletContext context,
      @Context HttpServletResponse response) {
    LOGGER.entry("begin study()");
    StudyResponse studyResponse = new StudyResponse();
    try {
      studyResponse = studyMetaDataOrchestration.study(studyId);
      if (!studyResponse.getMessage().equals(StudyMetaDataConstants.SUCCESS)) {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
        return Response.status(Response.Status.NOT_FOUND)
            .entity(StudyMetaDataConstants.NO_RECORD)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataService - study() :: ERROR ", e);
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("study() :: Ends");
    return studyResponse;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("versionInfo")
  public Object getAppVersionInfo(
      @HeaderParam("applicationId") String appId, @Context HttpServletResponse response) {
    AppVersionInfoBean appVersionInfoBean = null;
    LOGGER.entry("begin getAppVersionInfo()");

    if (StringUtils.isBlank(appId)) {
      StudyMetaDataUtil.getFailureResponse(
          ErrorCodes.STATUS_102,
          ErrorCodes.UNKNOWN,
          StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
          response);
      return Response.status(Response.Status.NOT_FOUND)
          .entity(StudyMetaDataConstants.INVALID_INPUT)
          .build();
    }

    try {
      appVersionInfoBean = appMetaDataOrchestration.getAppVersionInfo(appId);
      if (appVersionInfoBean == null) {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_103, ErrorCodes.NO_DATA, StudyMetaDataConstants.FAILURE, response);
        return Response.status(Response.Status.NOT_FOUND)
            .entity(StudyMetaDataConstants.NO_RECORD)
            .build();
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataService - getAppVersionInfo()", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("getAppVersionInfo() :: ends");
    return appVersionInfoBean;
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("activityResponce")
  public Object storeJsonResponseFile(
      String params,
      @HeaderParam("Authorization") String authorization,
      @Context ServletContext context,
      @Context HttpServletResponse response)
      throws Exception {
    LOGGER.entry("begin storeJsonResponseFile()");
    ErrorResponse errorResponse = new ErrorResponse();
    try {

      if (StringUtils.isNotEmpty(params)) {

        errorResponse = appMetaDataOrchestration.storeResponseActivitiesTemp(params);

        if (!errorResponse.getError().getStatus().equals(StudyMetaDataConstants.SUCCESS)) {
          StudyMetaDataUtil.getFailureResponse(
              ErrorCodes.STATUS_104, ErrorCodes.UNKNOWN, StudyMetaDataConstants.FAILURE, response);
          return Response.status(Response.Status.NO_CONTENT)
              .entity(StudyMetaDataConstants.NO_RECORD)
              .build();
        }
      } else {
        StudyMetaDataUtil.getFailureResponse(
            ErrorCodes.STATUS_102,
            ErrorCodes.INVALID_INPUT,
            StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG,
            response);
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(StudyMetaDataConstants.INVALID_INPUT_ERROR_MSG)
            .build();
      }
    } catch (JSONException e) {
      LOGGER.error("ERROR: StudyMetaDataService - storeJsonResponseFile()", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataService - storeJsonResponseFile()", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(StudyMetaDataConstants.FAILURE)
          .build();
    }
    LOGGER.exit("storeJsonResponseFile() :: ends");
    return errorResponse;
  }
}
