/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.INFORMED_CONSENT_PROVIDED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.SIGNED_CONSENT_DOCUMENT_SAVED;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.SIGNED_CONSENT_DOCUMENT_SAVE_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.STUDY_ENROLLMENT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.USER_ENROLLED_INTO_STUDY;

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStatusBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ConsentAuditHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.consent.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.consent.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;
import com.google.cloud.storage.StorageException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserConsentManagementController {

  @Autowired private UserConsentManagementService userConsentManagementService;

  @Autowired CommonService commonService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private FileStorageService cloudStorageService;

  @Autowired private ConsentAuditHelper consentAuditHelper;

  private static final Logger logger =
      LoggerFactory.getLogger(UserConsentManagementController.class);

  @RequestMapping(value = "/ping")
  public String ping() {
    logger.info(" UserConsentManagementController - ping()  ");
    return "Mystudies UserRegistration Webservice Started !!!";
  }

  @PostMapping(
      value = "/updateEligibilityConsentStatus",
      consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<?> updateEligibilityConsentStatus(
      @RequestHeader("userId") String userId,
      @RequestBody ConsentStatusBean consentStatusBean,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    logger.info("UserConsentManagementController updateEligibilityConsentStatus() - starts ");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    ErrorBean errorBean = null;
    StudyInfoBean studyInfoBean = null;
    Integer userDetailId = 0;

    if ((consentStatusBean != null)
        && (consentStatusBean.getConsent() != null)
        && ((consentStatusBean.getConsent().getVersion() != null)
            && (consentStatusBean.getConsent().getPdf() != null))
        && (consentStatusBean.getConsent().getStatus() != null)) {

      if (!StringUtils.isEmpty(consentStatusBean.getStudyId()) && !StringUtils.isEmpty(userId)) {
        auditRequest.setUserId(userId);
        auditRequest.setStudyId(consentStatusBean.getStudyId());

        studyInfoBean = userConsentManagementService.getStudyInfoId(consentStatusBean.getStudyId());
        ParticipantStudiesBO participantStudies =
            userConsentManagementService.getParticipantStudies(
                studyInfoBean.getStudyInfoId(), userId);
        if (participantStudies != null) {
          if (consentStatusBean.getEligibility() != null) {
            participantStudies.setEligibility(consentStatusBean.getEligibility());
          }
          if (!StringUtils.isEmpty(consentStatusBean.getSharing())) {
            participantStudies.setSharing(consentStatusBean.getSharing());
          }
          List<ParticipantStudiesBO> participantStudiesList = new ArrayList<ParticipantStudiesBO>();
          participantStudiesList.add(participantStudies);
          String message =
              userConsentManagementService.saveParticipantStudies(participantStudiesList);

          StudyConsentBO studyConsent = null;
          if (!StringUtils.isEmpty(consentStatusBean.getConsent().getVersion())) {
            studyConsent =
                userConsentManagementService.getStudyConsent(
                    userId,
                    studyInfoBean.getStudyInfoId(),
                    consentStatusBean.getConsent().getVersion());
            userDetailId = userConsentManagementService.getUserDetailsId(userId);
            if (studyConsent != null) {
              if (!StringUtils.isEmpty(consentStatusBean.getConsent().getVersion())) {
                studyConsent.setVersion(consentStatusBean.getConsent().getVersion());
              }
              if (!StringUtils.isEmpty(consentStatusBean.getConsent().getStatus())) {
                studyConsent.setStatus(consentStatusBean.getConsent().getStatus());
              }
              if (!StringUtils.isEmpty(consentStatusBean.getConsent().getPdf())) {
                String underDirectory = userId + "/" + consentStatusBean.getStudyId();
                String fileName =
                    userId
                        + "_"
                        + consentStatusBean.getStudyId()
                        + "_"
                        + consentStatusBean.getConsent().getVersion()
                        + "_"
                        + new SimpleDateFormat("MMddyyyyHHmmss").format(new Date())
                        + ".pdf";

                saveDocumentToCloudStorage(
                    auditRequest, underDirectory, fileName, consentStatusBean, studyConsent);
              }
              studyConsent.setUserId(userDetailId);
              studyConsent.setStudyInfoId(studyInfoBean.getStudyInfoId());
            } else {
              studyConsent = new StudyConsentBO();
              studyConsent.setUserId(userDetailId);
              studyConsent.setStudyInfoId(studyInfoBean.getStudyInfoId());
              studyConsent.setStatus(consentStatusBean.getConsent().getStatus());
              studyConsent.setVersion(consentStatusBean.getConsent().getVersion());
              if (!StringUtils.isEmpty(consentStatusBean.getConsent().getPdf())) {
                String underDirectory = userId + "/" + consentStatusBean.getStudyId();
                String fileName =
                    userId
                        + "_"
                        + consentStatusBean.getStudyId()
                        + "_"
                        + consentStatusBean.getConsent().getVersion()
                        + "_"
                        + new SimpleDateFormat("MMddyyyyHHmmss").format(new Date())
                        + ".pdf";

                saveDocumentToCloudStorage(
                    auditRequest, underDirectory, fileName, consentStatusBean, studyConsent);
              }
            }
            String addOrUpdateConsentMessage =
                userConsentManagementService.saveStudyConsent(studyConsent);
            if ((addOrUpdateConsentMessage.equalsIgnoreCase(
                    MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())
                && message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue()))) {
              if (AppConstants.STATUS_COMPLETED.equals(
                  consentStatusBean.getConsent().getStatus())) {
                Map<String, String> map = new HashedMap<>();
                map.put("consent_version", consentStatusBean.getConsent().getVersion());
                map.put("data_sharing_consent", consentStatusBean.getSharing());
                consentAuditHelper.logEvent(INFORMED_CONSENT_PROVIDED_FOR_STUDY, auditRequest, map);
              }

              consentAuditHelper.logEvent(USER_ENROLLED_INTO_STUDY, auditRequest);
              errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_110.errorMessage());
            } else {
              consentAuditHelper.logEvent(STUDY_ENROLLMENT_FAILED, auditRequest);
              errorBean = new ErrorBean(ErrorCode.EC_111.code(), ErrorCode.EC_111.errorMessage());
            }
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.CONSENT_VERSION_REQUIRED.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.CONSENT_VERSION_REQUIRED.getValue(),
                response);
            return null;
          }

        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
              response);
          return null;
        }

      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        return null;
      }
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      return null;
    }

    logger.info("UserConsentManagementController updateEligibilityConsentStatus() - ends ");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  private void saveDocumentToCloudStorage(
      AuditLogEventRequest auditRequest,
      String underDirectory,
      String fileName,
      ConsentStatusBean consentStatusBean,
      StudyConsentBO studyConsent) {
    Map<String, String> map = new HashedMap<>();
    map.put("file_name", fileName);
    map.put("consent_version", consentStatusBean.getConsent().getVersion());
    map.put("directory_name", appConfig.getBucketName());
    try {
      String path =
          cloudStorageService.saveFile(
              fileName, consentStatusBean.getConsent().getPdf(), underDirectory);
      studyConsent.setPdfPath(path);
      studyConsent.setPdfStorage(1);
      consentAuditHelper.logEvent(SIGNED_CONSENT_DOCUMENT_SAVED, auditRequest, map);
    } catch (StorageException e) {
      consentAuditHelper.logEvent(SIGNED_CONSENT_DOCUMENT_SAVE_FAILED, auditRequest, map);
    }
  }

  @GetMapping(value = "/consentDocument", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyConsentPdf(
      @RequestHeader("userId") String userId,
      @QueryParam("studyId") String studyId,
      @QueryParam("consentVersion") String consentVersion,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.info("UserConsentManagementController getStudyConsentPDF() - starts ");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    ErrorBean errorBean = null;
    ConsentStudyResponseBean consentStudyResponseBean = null;
    StudyInfoBean studyInfoBean = null;

    if (!StringUtils.isEmpty(studyId) && !StringUtils.isEmpty(userId)) {
      studyInfoBean = userConsentManagementService.getStudyInfoId(studyId);
      consentStudyResponseBean =
          userConsentManagementService.getStudyConsentDetails(
              userId, studyInfoBean.getStudyInfoId(), consentVersion, auditRequest);
      auditRequest.setUserId(userId);
      auditRequest.setStudyId(studyId);

      if (consentStudyResponseBean.getConsent().getContent() != null) {
        consentStudyResponseBean.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
      } else {
        consentAuditHelper.logEvent(
            READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT, auditRequest);
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
            response);
        return null;
      }
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_AUTH_CODE.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.SESSION_EXPIRED_MSG.getValue(),
          response);
      errorBean = AppUtil.dynamicResponse(ErrorCode.EC_109.code(), ErrorCode.EC_109.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.NOT_FOUND);
    }

    logger.info("UserConsentManagementController getStudyConsentPDF() - ends ");
    return new ResponseEntity<>(consentStudyResponseBean, HttpStatus.OK);
  }
}
