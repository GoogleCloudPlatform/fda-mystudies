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

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStatusBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.UpdateEligibilityConsentBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ConsentAuditHelper;
import com.google.cloud.healthcare.fdamystudies.common.DataSharingStatus;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;
import com.google.cloud.storage.StorageException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.apache.commons.collections4.map.HashedMap;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Api(
    tags = "Consent",
    value = "consent management",
    description = "Operations pertaining to save or retrive consent document")
@RestController
public class UserConsentManagementController {

  @Autowired private UserConsentManagementService userConsentManagementService;

  @Autowired CommonService commonService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private FileStorageService cloudStorageService;

  @Autowired private ConsentAuditHelper consentAuditHelper;

  @Autowired UserDetailsRepository userDetailsRepository;

  @Autowired StudyRepository studyRepository;

  @Autowired ParticipantStudyRepository participantStudyRepository;

  private XLogger logger =
      XLoggerFactory.getXLogger(UserConsentManagementController.class.getName());

  private static final String STATUS_LOG = "status=%s";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @ApiOperation(value = "save consent document in cloud")
  @PostMapping(
      value = "/updateEligibilityConsentStatus",
      consumes = "application/json",
      produces = "application/json")
  public ResponseEntity<?> updateEligibilityConsentStatus(
      @RequestHeader("userId") String userId,
      @Valid @RequestBody ConsentStatusBean consentStatusBean,
      @Context HttpServletResponse response,
      HttpServletRequest request)
      throws Exception {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    ErrorBean errorBean = null;
    StudyEntity studyInfo = null;
    String userDetailId = String.valueOf(0);
    String consentdocumentFilepath = null;

    auditRequest.setUserId(userId);
    studyInfo = userConsentManagementService.getStudyInfo(consentStatusBean.getStudyId());
    if (studyInfo != null) {
      auditRequest.setStudyId(studyInfo.getCustomId());
      auditRequest.setStudyVersion(String.valueOf(studyInfo.getVersion()));
    }

    Optional<ParticipantStudyEntity> optParticipantStudies =
        participantStudyRepository.findByStudyIdAndSiteId(
            studyInfo.getId(), userId, consentStatusBean.getSiteId());

    if (optParticipantStudies.isPresent()) {
      ParticipantStudyEntity participantStudies = optParticipantStudies.get();
      auditRequest.setParticipantId(participantStudies.getParticipantId());
      if (consentStatusBean.getEligibility() != null) {
        participantStudies.setEligibility(consentStatusBean.getEligibility());
      }
      DataSharingStatus dataSharing = DataSharingStatus.fromValue(consentStatusBean.getSharing());
      if (dataSharing == null) {
        throw new ErrorCodeException(
            com.google.cloud.healthcare.fdamystudies.common.ErrorCode.INVALID_DATA_SHARING_STATUS);
      }
      participantStudies.setSharing(dataSharing.value());

      List<ParticipantStudyEntity> participantStudiesList = new ArrayList<ParticipantStudyEntity>();
      participantStudiesList.add(participantStudies);

      userDetailId = userConsentManagementService.getUserDetailsId(userId);
      Optional<UserDetailsEntity> optUser = userDetailsRepository.findById(userDetailId);

      StudyConsentEntity studyConsent = new StudyConsentEntity();
      if (optUser.isPresent()) {
        studyConsent.setUserDetails(optUser.get());
      }

      studyConsent.setStudy(studyInfo);
      studyConsent.setParticipantStudy(participantStudies);
      studyConsent.setConsentDate(participantStudies.getEnrolledDate());
      studyConsent.setSharing(dataSharing.value());
      studyConsent.setStatus(consentStatusBean.getConsent().getStatus());
      studyConsent.setVersion(consentStatusBean.getConsent().getVersion());
      if (!StringUtils.isEmpty(consentStatusBean.getConsent().getPdf())) {
        //        String underDirectory = userId + "/" + consentStatusBean.getStudyId();
        String underDirectory =
            studyInfo.getCustomId()
                + "/"
                + participantStudies.getParticipantId()
                + "-"
                + IdGenerator.id();
        String consentDocumentFileName =
            consentStatusBean.getConsent().getVersion()
                + "_"
                + new SimpleDateFormat("MMddyyyyHHmmss").format(new Date())
                + ".pdf";
        saveDocumentToCloudStorage(
            auditRequest, underDirectory, consentDocumentFileName, consentStatusBean, studyConsent);
        consentdocumentFilepath = underDirectory + "/" + consentDocumentFileName;
      }
      String message = userConsentManagementService.saveParticipantStudies(participantStudiesList);
      String addConsentMessage = userConsentManagementService.saveStudyConsent(studyConsent);
      if ((addConsentMessage.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())
          && message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue()))) {
        if (AppConstants.STATUS_COMPLETED.equalsIgnoreCase(
            consentStatusBean.getConsent().getStatus())) {
          Map<String, String> map = new HashedMap<>();
          map.put("consent_version", consentStatusBean.getConsent().getVersion());
          map.put("data_sharing_consent", consentStatusBean.getSharing());
          consentAuditHelper.logEvent(INFORMED_CONSENT_PROVIDED_FOR_STUDY, auditRequest, map);
        }

        errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_110.errorMessage());
      } else {
        errorBean = new ErrorBean(ErrorCode.EC_111.code(), ErrorCode.EC_111.errorMessage());
      }

    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.NO_DATA_AVAILABLE.getValue(),
          response);
      return null;
    }

    UpdateEligibilityConsentBean updateEligibilityConsentBean =
        new UpdateEligibilityConsentBean(
            errorBean.getCode(), errorBean.getMessage(), consentdocumentFilepath);
    logger.exit(String.format(STATUS_LOG, updateEligibilityConsentBean.getCode()));
    return new ResponseEntity<>(updateEligibilityConsentBean, HttpStatus.OK);
  }

  private void saveDocumentToCloudStorage(
      AuditLogEventRequest auditRequest,
      String underDirectory,
      String fileName,
      ConsentStatusBean consentStatusBean,
      StudyConsentEntity studyConsent) {
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

  @ApiOperation(value = "fetch consent document")
  @GetMapping(value = "/consentDocument", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyConsentPdf(
      @RequestHeader("userId") String userId,
      @QueryParam("studyId") String studyId,
      @QueryParam("consentVersion") String consentVersion,
      @Context HttpServletResponse response,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    ErrorBean errorBean = null;
    ConsentStudyResponseBean consentStudyResponseBean = null;
    StudyEntity studyInfo = null;

    if (!StringUtils.isEmpty(studyId)) {
      studyInfo = userConsentManagementService.getStudyInfo(studyId);
      auditRequest.setUserId(userId);
      auditRequest.setStudyId(studyInfo.getCustomId());
      auditRequest.setStudyVersion(String.valueOf(studyInfo.getVersion()));

      consentStudyResponseBean =
          userConsentManagementService.getStudyConsentDetails(
              userId, studyInfo.getId(), consentVersion, auditRequest);

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

    logger.exit(String.format(STATUS_LOG, consentStudyResponseBean.getMessage()));
    return new ResponseEntity<>(consentStudyResponseBean, HttpStatus.OK);
  }
}
