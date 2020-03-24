/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import org.apache.tomcat.util.codec.binary.Base64;
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
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStatusBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

@RestController
public class UserConsentManagementController {

  @Autowired private UserConsentManagementService userConsentManagementService;

  @Autowired CommonService commonService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private FileStorageService cloudStorageService;

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
      @Context HttpServletResponse response) {
    logger.info("UserConsentManagementController updateEligibilityConsentStatus() - starts ");
    ErrorBean errorBean = null;
    StudyInfoBean studyInfoBean = null;
    Integer userDetailId = 0;
    try {
      if ((consentStatusBean != null)
          && (consentStatusBean.getConsent() != null)
          && ((consentStatusBean.getConsent().getVersion() != null)
              && (consentStatusBean.getConsent().getPdf() != null))
          && (consentStatusBean.getConsent().getStatus() != null)) {
        if ((consentStatusBean.getStudyId() != null)
            && !StringUtils.isEmpty(consentStatusBean.getStudyId())
            && (userId != null)
            && !StringUtils.isEmpty(userId)) {
          studyInfoBean =
              userConsentManagementService.getStudyInfoId(consentStatusBean.getStudyId());
          ParticipantStudiesBO participantStudies =
              userConsentManagementService.getParticipantStudies(
                  studyInfoBean.getStudyInfoId(), userId);
          if (participantStudies != null) {
            if (consentStatusBean.getEligibility() != null) {
              participantStudies.setEligibility(consentStatusBean.getEligibility());
            }
            if ((consentStatusBean.getSharing() != null)
                && !StringUtils.isEmpty(consentStatusBean.getSharing())) {
              participantStudies.setSharing(consentStatusBean.getSharing());
            }
            List<ParticipantStudiesBO> participantStudiesList =
                new ArrayList<ParticipantStudiesBO>();
            participantStudiesList.add(participantStudies);
            String message =
                userConsentManagementService.saveParticipantStudies(participantStudiesList);

            StudyConsentBO studyConsent = null;
            if ((consentStatusBean.getConsent().getVersion() != null)
                && !StringUtils.isEmpty(consentStatusBean.getConsent().getVersion())) {
              studyConsent =
                  userConsentManagementService.getStudyConsent(
                      userId,
                      studyInfoBean.getStudyInfoId(),
                      consentStatusBean.getConsent().getVersion());
              userDetailId = userConsentManagementService.getUserDetailsId(userId);
              if (studyConsent != null) {
                if ((consentStatusBean.getConsent().getVersion() != null)
                    && !StringUtils.isEmpty(consentStatusBean.getConsent().getVersion())) {
                  studyConsent.setVersion(consentStatusBean.getConsent().getVersion());
                }
                if ((consentStatusBean.getConsent().getStatus() != null)
                    && !StringUtils.isEmpty(consentStatusBean.getConsent().getStatus())) {
                  studyConsent.setStatus(consentStatusBean.getConsent().getStatus());
                }
                if ((consentStatusBean.getConsent().getPdf() != null)
                    && !StringUtils.isEmpty(consentStatusBean.getConsent().getPdf())) {
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
                  String content = consentStatusBean.getConsent().getPdf();
                  String path = cloudStorageService.saveFile(fileName, content, underDirectory);
                  studyConsent.setPdfPath(path);
                  studyConsent.setPdfStorage(1);
                }
                studyConsent.setUserId(userDetailId);
                studyConsent.setStudyInfoId(studyInfoBean.getStudyInfoId());
              } else {
                studyConsent = new StudyConsentBO();
                studyConsent.setUserId(userDetailId);
                studyConsent.setStudyInfoId(studyInfoBean.getStudyInfoId());
                studyConsent.setStatus(consentStatusBean.getConsent().getStatus());
                studyConsent.setVersion(consentStatusBean.getConsent().getVersion());
                if ((consentStatusBean.getConsent().getPdf() != null)
                    && !StringUtils.isEmpty(consentStatusBean.getConsent().getPdf())) {
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
                  String content = consentStatusBean.getConsent().getPdf();

                  String path = cloudStorageService.saveFile(fileName, content, underDirectory);
                  studyConsent.setPdfPath(path);
                  studyConsent.setPdfStorage(1);
                }
              }
              String addOrUpdateConsentMessage =
                  userConsentManagementService.saveStudyConsent(studyConsent);
              if ((addOrUpdateConsentMessage.equalsIgnoreCase(
                      MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())
                  && message.equalsIgnoreCase(
                      MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue()))) {
                errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_110.errorMessage());

                commonService.createActivityLog(
                    userId,
                    AppConstants.AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_NAME,
                    String.format(
                        AppConstants.AUDIT_EVENT_UPDATE_ELIGIBILITY_CONSENT_DESC,
                        consentStatusBean.getStudyId()));
              } else {
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
    } catch (Exception e) {
      logger.error("UserConsentManagementController updateEligibilityConsentStatus() - error ", e);
      return AppUtil.httpResponseForInternalServerError();
    }

    logger.info("UserConsentManagementController updateEligibilityConsentStatus() - ends ");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }

  /**
   * saving the consent documents into particular study folder
   *
   * @param studyConsent
   * @return
   */
  public String saveStudyConsentDocument(StudyConsentBO studyConsent) {
    String fileName = "";
    String catalinaHome = "";
    byte pdfFileInByte[] = null;
    FileOutputStream fop = null;
    File file = null;
    try {
      catalinaHome = System.getProperty("catalina.home");
      if (!StringUtils.isEmpty(studyConsent.getPdf()) || (studyConsent.getPdf().length() != 0)) {
        try {
          pdfFileInByte = Base64.decodeBase64(studyConsent.getPdf());
          fileName = "user_" + studyConsent.getUserId() + ".pdf";
          file = new File("src/main/webapp/consentDucmentPdfFiles/" + fileName);
          fop = new FileOutputStream(file);
          fop.write(pdfFileInByte);
          fop.flush();
        } catch (Exception e) {
          logger.error("SafepassageLoginDAOImpl - updateUserProfile()- error ", e);
        } finally {
          if (fop != null) {
            fop.close();
          }
        }
      }
    } catch (Exception e) {
      logger.error("FdahpUserRegWSController saveStudyConsentDocument:", e);
    }
    return fileName;
  }

  @GetMapping(value = "/consentDocument", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyConsentPDF(
      @RequestHeader("userId") String userId,
      @QueryParam("studyId") String studyId,
      @QueryParam("consentVersion") String consentVersion,
      @Context HttpServletResponse response) {
    logger.info("UserConsentManagementController getStudyConsentPDF() - starts ");
    ErrorBean errorBean = null;
    ConsentStudyResponseBean consentStudyResponseBean = null;
    StudyInfoBean studyInfoBean = null;
    try {
      if ((studyId != null)
          && !StringUtils.isEmpty(studyId)
          && (userId != null)
          && !StringUtils.isEmpty(userId)) {
        studyInfoBean = userConsentManagementService.getStudyInfoId(studyId);
        consentStudyResponseBean =
            userConsentManagementService.getStudyConsentDetails(
                userId, studyInfoBean.getStudyInfoId(), consentVersion);
        if (consentStudyResponseBean != null) {
          consentStudyResponseBean.setMessage(
              MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
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
            MyStudiesUserRegUtil.ErrorCodes.INVALID_AUTH_CODE.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.SESSION_EXPIRED_MSG.getValue(),
            response);
        errorBean =
            AppUtil.dynamicResponse(ErrorCode.EC_109.code(), ErrorCode.EC_109.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      logger.error("UserConsentManagementController getStudyConsentPDF() - error ", e);
      return AppUtil.httpResponseForInternalServerError();
    }

    logger.info("UserConsentManagementController getStudyConsentPDF() - ends ");
    return new ResponseEntity<>(consentStudyResponseBean, HttpStatus.OK);
  }
}
