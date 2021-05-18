/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ImportParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantRequest;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteStatusResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.SiteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(
    tags = "Sites",
    value = "Site related api's",
    description = "Operations pertaining to Sites in participant manager")
@RestController
public class SiteController {

  private XLogger logger = XLoggerFactory.getXLogger(SiteController.class.getName());

  private static final String BEGIN_REQUEST_LOG = "%s request";

  private static final String STATUS_LOG = "status=%d ";

  @Autowired private SiteService siteService;

  @ApiOperation(value = "create a new site for a study")
  @PostMapping(
      value = "/sites",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SiteResponse> addNewSite(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody SiteRequest siteRequest,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    siteRequest.setUserId(userId);
    SiteResponse siteResponse = siteService.addSite(siteRequest, auditRequest);

    logger.exit(
        String.format(
            "status=%d and siteId=%s", siteResponse.getHttpStatusCode(), siteResponse.getSiteId()));

    return ResponseEntity.status(siteResponse.getHttpStatusCode()).body(siteResponse);
  }

  @ApiOperation(value = "add a new participant for a site")
  @PostMapping(
      value = "/sites/{siteId}/participants",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ParticipantResponse> addNewParticipant(
      @PathVariable String siteId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody ParticipantDetailRequest participant,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    participant.setSiteId(siteId);
    ParticipantResponse participantResponse =
        siteService.addNewParticipant(participant, userId, auditRequest);
    logger.exit(String.format(STATUS_LOG, participantResponse.getHttpStatusCode()));
    return ResponseEntity.status(participantResponse.getHttpStatusCode()).body(participantResponse);
  }

  @ApiOperation(value = "fetch participants related to particular site")
  @GetMapping(value = "/sites/{siteId}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ParticipantRegistryResponse> getSiteParticipant(
      @PathVariable String siteId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(name = "onboardingStatus", required = false) String onboardingStatus,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer limit,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    if (StringUtils.isNotEmpty(onboardingStatus)
        && OnboardingStatus.fromCode(onboardingStatus) == null) {
      throw new ErrorCodeException(ErrorCode.INVALID_ONBOARDING_STATUS);
    }

    ParticipantRegistryResponse participants =
        siteService.getParticipants(userId, siteId, onboardingStatus, auditRequest, page, limit);
    logger.exit(String.format(STATUS_LOG, participants.getHttpStatusCode()));
    return ResponseEntity.status(participants.getHttpStatusCode()).body(participants);
  }

  @ApiOperation(value = "Activate/Deactivate site for a study")
  @PutMapping(
      value = "/sites/{siteId}/decommission",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SiteStatusResponse> decomissionSite(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @PathVariable String siteId,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    SiteStatusResponse decomissionSiteResponse =
        siteService.toggleSiteStatus(userId, siteId, auditRequest);

    logger.exit(String.format(STATUS_LOG, decomissionSiteResponse.getHttpStatusCode()));
    return ResponseEntity.status(decomissionSiteResponse.getHttpStatusCode())
        .body(decomissionSiteResponse);
  }

  @ApiOperation(value = "fetch participant details with enrollment history")
  @GetMapping("/sites/{participantRegistrySiteId}/participant")
  public ResponseEntity<ParticipantDetailResponse> getParticipantDetails(
      @PathVariable String participantRegistrySiteId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer limit,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());

    ParticipantDetailResponse participantDetails =
        siteService.getParticipantDetails(participantRegistrySiteId, userId, page, limit);

    logger.exit(String.format(STATUS_LOG, participantDetails.getHttpStatusCode()));
    return ResponseEntity.status(participantDetails.getHttpStatusCode()).body(participantDetails);
  }

  @ApiOperation(value = "Send/Resend invitation to participants")
  @PostMapping("/sites/{siteId}/participants/invite")
  public ResponseEntity<InviteParticipantResponse> inviteParticipants(
      @Valid @RequestBody InviteParticipantRequest inviteParticipantRequest,
      @PathVariable String siteId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    inviteParticipantRequest.setSiteId(siteId);
    inviteParticipantRequest.setUserId(userId);

    InviteParticipantResponse inviteParticipantResponse =
        siteService.inviteParticipants(inviteParticipantRequest, auditRequest);

    logger.exit(String.format(STATUS_LOG, inviteParticipantResponse.getHttpStatusCode()));
    return ResponseEntity.status(inviteParticipantResponse.getHttpStatusCode())
        .body(inviteParticipantResponse);
  }

  @ApiOperation(value = "import participants from file")
  @PostMapping(
      value = "/sites/{siteId}/participants/import",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImportParticipantResponse> importParticipants(
      @PathVariable String siteId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam("file") MultipartFile inputFile,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    ImportParticipantResponse participants =
        siteService.importParticipants(userId, siteId, inputFile, auditRequest);
    logger.exit(String.format(STATUS_LOG, participants.getHttpStatusCode()));
    return ResponseEntity.status(participants.getHttpStatusCode()).body(participants);
  }

  @ApiOperation(value = "update onbording status for a participant")
  @PatchMapping("/sites/{siteId}/participants/status")
  public ResponseEntity<ParticipantStatusResponse> updateOnboardingStatus(
      @PathVariable String siteId,
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @Valid @RequestBody ParticipantStatusRequest participantStatusRequest,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    participantStatusRequest.setSiteId(siteId);
    participantStatusRequest.setUserId(userId);
    ParticipantStatusResponse response =
        siteService.updateOnboardingStatus(participantStatusRequest, auditRequest);

    logger.exit(String.format(STATUS_LOG, response.getHttpStatusCode()));
    return ResponseEntity.status(response.getHttpStatusCode()).body(response);
  }

  @ApiOperation(value = "fetch a list of sites that user have permissions")
  @GetMapping("/sites")
  public ResponseEntity<SiteDetailsResponse> getSites(
      @RequestHeader(name = USER_ID_HEADER) String userId,
      @RequestParam(defaultValue = "10") Integer limit,
      @RequestParam(defaultValue = "0") Integer offset,
      @RequestParam(required = false) String searchTerm,
      HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());

    SiteDetailsResponse siteDetails = siteService.getSites(userId, limit, offset, searchTerm);

    logger.exit(String.format(STATUS_LOG, siteDetails.getHttpStatusCode()));
    return ResponseEntity.status(siteDetails.getHttpStatusCode()).body(siteDetails);
  }
}
