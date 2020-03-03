/**
 * package com.UR.webAppWS.controller; =======
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentDocumentBean;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.EnableDisableParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.InviteParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantDetailsBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantRegistryResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantResponseBean;
import com.google.cloud.healthcare.fdamystudies.service.SitesService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;

@RestController
public class SitesController {

  private static final Logger logger = LoggerFactory.getLogger(SitesController.class);

  @Autowired private SitesService sitesService;

  @GetMapping("/sites")
  public ResponseEntity<?> getSites(@RequestHeader("userId") Integer userId) {
    logger.info(" SiteController - getSites():starts");
    DashboardBean dashboardBean;
    ErrorBean errorBean = null;
    if (null != userId && userId != 0) {
      try {
        dashboardBean = sitesService.getSites(userId);
        if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
          return new ResponseEntity<>(dashboardBean.getStudies(), HttpStatus.OK);
        } else if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_814.code()) {
          return new ResponseEntity<>(dashboardBean.getError(), HttpStatus.BAD_REQUEST);
        } else if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_500.code()) {
          return new ResponseEntity<>(dashboardBean.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } catch (Exception e) {
        logger.info("SiteController - getSites() : error", e);
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    logger.info("SiteController - getSites() : ends");

    return new ResponseEntity<>(dashboardBean.getStudies(), HttpStatus.OK);
  }

  @GetMapping(value = "/sites/{siteId}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getSiteParticipant(
      @PathVariable("siteId") Integer siteId,
      @RequestHeader("userId") String userId,
      @RequestParam(name = "onboardingStatus", defaultValue = "all") String onboardingStatus) {
    logger.info("SitesController - getSiteParticipant - starts");
    ParticipantRegistryResponseBean participants = null;
    try {
      if (!URWebAppWSConstants.ONBOARDING_STATUS_ALL.equalsIgnoreCase(onboardingStatus)) {
        onboardingStatus = onboardingStatus.substring(0, 1).toUpperCase();
      }

      participants = sitesService.getParticipants(userId, siteId, onboardingStatus);
    } catch (Exception e) {
      ErrorBean errorBean = new ErrorBean(500, e.getMessage());
      logger.error("SitesController - getSiteParticipant - error", e);
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.info("SitesController - getSiteParticipant - ends");
    return new ResponseEntity<>(participants, HttpStatus.OK);
  }

  @PostMapping(
      value = "/sites/{siteId}/participants",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addNewParticipant(
      @RequestBody ParticipantBean participant,
      @PathVariable("siteId") Integer siteId,
      @RequestHeader(value = "userId", required = false) Integer userId,
      HttpServletRequest servletRequest) {
    try {
      logger.info("SitesController - addNewParticipant - starts");
      if (StringUtils.isBlank(participant.getEmail()) || userId == null || siteId == null) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_987.code(),
                ErrorCode.EC_987.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_987.errorMessage());
        logger.info("SitesController - addNewParticipant - ends");
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      } else if (!Pattern.matches(AppConstants.EMAIL_REGEX, participant.getEmail())) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_53.code(),
                ErrorCode.EC_53.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_53.errorMessage());
        logger.info("SitesController - addNewParticipant - ends");
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      participant.setSiteId(siteId);
      sitesService.addNewParticipant(participant, siteId, userId);
      if (participant.getErrorBean() != null) {
        ErrorBean errorBean = participant.getErrorBean();
        HttpStatus httpStatus = null;
        if (ErrorCode.EC_862.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ErrorCode.EC_863.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.FORBIDDEN;
        } else if (ErrorCode.EC_864.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else {
          httpStatus = HttpStatus.BAD_REQUEST;
        }
        logger.info("SitesController - addNewParticipant - ends");
        return new ResponseEntity<>(errorBean, httpStatus);
      }
    } catch (Exception e) {
      ErrorBean errorBean = new ErrorBean(500, e.getMessage());
      logger.error("SitesController - addNewParticipant - ends", e);
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.info("SitesController - addNewParticipant - ends");
    return new ResponseEntity<>(participant, HttpStatus.OK);
  }

  @PostMapping(
      value = "/sites/{siteId}/participants/import",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> importParticipants(
      @PathVariable("siteId") Integer siteId,
      @RequestHeader("userId") Integer userId,
      @RequestParam("file") MultipartFile multipartFile,
      HttpServletRequest servletRequest) {
    try {
      logger.info("SitesController - importParticipants - starts");
      if (multipartFile == null || multipartFile.isEmpty() || userId == null || siteId == null) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_777.code(),
                ErrorCode.EC_777.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_777.errorMessage());
        logger.info("SitesController - importParticipants - ends");
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      try (Workbook workbook =
          WorkbookFactory.create(new BufferedInputStream(multipartFile.getInputStream()))) {
        ParticipantResponseBean respBean = new ParticipantResponseBean();
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        String columnName = row.getCell(1).getStringCellValue();
        List<ParticipantBean> participants = new LinkedList<>();
        if ("Email Address".equalsIgnoreCase(columnName)) {
          Iterator<Row> it = sheet.rowIterator();
          Set<String> invalidEmails = new HashSet<>();
          while (it.hasNext()) {
            Row r = it.next();
            if (r.getRowNum() == 0) {
              continue;
            }
            String email = null;
            try {
              email = r.getCell(1).getStringCellValue();
              if (!StringUtils.isBlank(email) && Pattern.matches(AppConstants.EMAIL_REGEX, email)) {
                ParticipantBean participant = new ParticipantBean();
                participant.setEmail(email);
                participant.setSiteId(siteId);
                participants.add(participant);
              } else {
                invalidEmails.add(email);
              }
            } catch (Exception e) {
              invalidEmails.add(email);
              continue;
            }
          }
          respBean.setParticipants(participants);
          respBean.setInvalidEmails(invalidEmails);
          sitesService.addNewParticipant(respBean, siteId, userId);
          if (respBean.getErrorBean() != null) {
            ErrorBean errorBean = respBean.getErrorBean();
            HttpStatus httpStatus = null;
            if (ErrorCode.EC_863.code() == errorBean.getApp_error_code()) {
              httpStatus = HttpStatus.FORBIDDEN;
            } else if (ErrorCode.EC_865.code() == errorBean.getApp_error_code()) {
              httpStatus = HttpStatus.BAD_REQUEST;
            } else if (ErrorCode.EC_913.code() == errorBean.getApp_error_code()) {
              httpStatus = HttpStatus.BAD_REQUEST;
            } else {
              httpStatus = HttpStatus.BAD_REQUEST;
            }
            logger.info("SitesController - importParticipants - ends");
            return new ResponseEntity<>(errorBean, httpStatus);
          } else {

            respBean.setParticipants(null);
            logger.info("SitesController - importParticipants - ends");
            return new ResponseEntity<>(respBean, HttpStatus.OK);
          }
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_866.code(),
                  ErrorCode.EC_866.errorMessage(),
                  URWebAppWSConstants.ERROR,
                  ErrorCode.EC_866.errorMessage());
          logger.info("SitesController - importParticipants - ends");
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
      } catch (NullPointerException | IllegalArgumentException | IOException e) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_866.code(),
                ErrorCode.EC_866.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_866.errorMessage());
        logger.error("SitesController - importParticipants - error", e);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("SitesController - importParticipants - error", e);
      return new ResponseEntity<>(
          new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage()),
          HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/sites/{participantRegistrySite}/participant")
  public ResponseEntity<?> getParticipantDetails(
      @PathVariable("participantRegistrySite") Integer participantRegistrySiteId,
      @RequestHeader("userId") String userId) {
    logger.info("GetParticipantDetailsController.getParticipantDetails()...Started");
    ErrorBean errorBean = null;
    ParticipantDetailsBean participantDetails;
    if (participantRegistrySiteId != 0 && participantRegistrySiteId != null) {
      try {
        participantDetails =
            sitesService.getParticipantDetails(participantRegistrySiteId, Integer.valueOf(userId));
        if (participantDetails.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
          return new ResponseEntity<>(participantDetails, HttpStatus.OK);
        } else if (participantDetails.getError().getApp_error_code() == ErrorCode.EC_963.code()) {
          return new ResponseEntity<>(participantDetails.getError(), HttpStatus.BAD_REQUEST);
        } else if (participantDetails.getError().getApp_error_code() == ErrorCode.EC_500.code()) {
          return new ResponseEntity<>(
              participantDetails.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } catch (Exception e) {
        logger.info("GetParticipantDetailsController.getParticipantDetails()...Ended: (ERROR) ", e);
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      logger.info("GetParticipantDetailsController.getParticipantDetails()...Ended");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(participantDetails, HttpStatus.OK);
  }

  @PostMapping("/sites/{siteId}/participants/invite")
  public ResponseEntity<?> inviteParticipants(
      @RequestBody InviteParticipantBean inviteParticipantBean,
      @PathVariable("siteId") Integer siteId,
      @RequestHeader(value = "userId", required = false) Integer userId,
      HttpServletRequest servletRequest) {
    try {
      logger.info("SitesController - inviteParticipants - starts");
      if (userId == null || siteId == null) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_987.code(),
                ErrorCode.EC_987.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_987.errorMessage());
        logger.info("SitesController - inviteParticipants - ends");
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      } else {
        sitesService.inviteParticipants(inviteParticipantBean, siteId, userId);
        if (inviteParticipantBean.getErrorBean() != null) {
          ErrorBean errorBean = inviteParticipantBean.getErrorBean();
          HttpStatus httpStatus = null;
          if (ErrorCode.EC_863.code() == errorBean.getApp_error_code()) {
            httpStatus = HttpStatus.FORBIDDEN;
          } else if (ErrorCode.EC_865.code() == errorBean.getApp_error_code()) {
            httpStatus = HttpStatus.BAD_REQUEST;
          } else {
            httpStatus = HttpStatus.BAD_REQUEST;
          }
          logger.info("SitesController - inviteParticipants - ends");
          return new ResponseEntity<>(errorBean, httpStatus);
        } else {
          logger.info("SitesController - inviteParticipants - ends");
          return new ResponseEntity<>(inviteParticipantBean, HttpStatus.OK);
        }
      }
    } catch (Exception e) {
      ErrorBean errorBean = new ErrorBean(500, e.getMessage());
      logger.error("SitesController - inviteParticipants - error", e);
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/sites/{siteId}/participants/activate")
  public ResponseEntity<?> updateOnboardingStatus(
      @PathVariable("siteId") Integer siteId,
      @RequestHeader(value = "userId", required = false) Integer userId,
      @RequestBody EnableDisableParticipantBean bean) {
    logger.info("SitesController - updateOnboardingStatus - starts");
    if (userId == null || siteId == null) {
      ErrorBean errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_987.code(),
              ErrorCode.EC_987.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_987.errorMessage());
      logger.info("SitesController - updateOnboardingStatus - ends");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    try {
      sitesService.updateOnboardingStatus(bean, siteId, userId);
      if (bean.getErrorBean() != null) {
        ErrorBean errorBean = bean.getErrorBean();
        HttpStatus httpStatus = null;
        if (ErrorCode.EC_863.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.FORBIDDEN;
        } else if (ErrorCode.EC_865.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else {
          httpStatus = HttpStatus.BAD_REQUEST;
        }
        logger.info("SitesController - updateOnboardingStatus - ends");
        return new ResponseEntity<>(errorBean, httpStatus);
      } else {
        logger.info("SitesController - updateOnboardingStatus - ends");
        return new ResponseEntity<>(bean.getSuccessBean(), HttpStatus.OK);
      }
    } catch (Exception e) {
      ErrorBean errorBean = new ErrorBean(500, e.getMessage());
      logger.error("SitesController - updateOnboardingStatus - error", e);
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/sites/{consentId}/consentDocument")
  public ResponseEntity<?> getConsentDocument(
      @PathVariable("consentId") Integer consentId, @RequestHeader("userId") String userId) {
    logger.info("GetParticipantDetailsController.getConsentDocument() -start");
    ErrorBean errorBean = null;
    ConsentDocumentBean consentDocumentBean;
    if (consentId != 0 && consentId != null) {
      try {
        consentDocumentBean = sitesService.getConsentDocument(consentId, Integer.valueOf(userId));

        if (consentDocumentBean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
          return new ResponseEntity<>(consentDocumentBean, HttpStatus.OK);
        } else if (consentDocumentBean.getError().getApp_error_code() == ErrorCode.EC_964.code()) {
          return new ResponseEntity<>(consentDocumentBean.getError(), HttpStatus.BAD_REQUEST);
        } else if (consentDocumentBean.getError().getApp_error_code() == ErrorCode.EC_500.code()) {
          return new ResponseEntity<>(
              consentDocumentBean.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } catch (Exception e) {
        logger.info("GetParticipantDetailsController getConsentDocument() -error ", e);
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      logger.info("GetParticipantDetailsController getConsentDocument() -Ended");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(consentDocumentBean, HttpStatus.OK);
  }
}
