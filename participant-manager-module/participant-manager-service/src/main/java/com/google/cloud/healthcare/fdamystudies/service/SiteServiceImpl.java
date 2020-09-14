/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ACTIVE_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CLOSE_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DEFAULT_PERCENTAGE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_REGEX;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ENROLLED_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.STATUS_ACTIVE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.YET_TO_ENROLL;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.YET_TO_JOIN;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.ENROLLMENT_TARGET_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.INVITATION_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORTED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_EMAIL_ADDED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_DISABLED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_EMAIL_RESENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.PARTICIPANT_INVITATION_ENABLED;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_ACTIVATED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_DECOMMISSIONED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.SITE_PARTICIPANT_REGISTRY_VIEWED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ConsentHistory;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.Enrollment;
import com.google.cloud.healthcare.fdamystudies.beans.ImportParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantRequest;
import com.google.cloud.healthcare.fdamystudies.beans.InviteParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteDetails;
import com.google.cloud.healthcare.fdamystudies.beans.SiteDetailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateTargetEnrollmentResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.SiteStatus;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.ParticipantMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.SiteMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.StudyMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteCount;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.LocationRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyConsentRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SiteServiceImpl implements SiteService {

  private static final int EMAIL_ADDRESS_COLUMN = 1;

  private XLogger logger = XLoggerFactory.getXLogger(SiteServiceImpl.class.getName());

  @Autowired private SiteRepository siteRepository;

  @Autowired private LocationRepository locationRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private StudyPermissionRepository studyPermissionRepository;

  @Autowired private AppPermissionRepository appPermissionRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private StudyConsentRepository studyConsentRepository;

  @Autowired private AppPropertyConfig appPropertyConfig;

  @Autowired private EmailService emailService;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Override
  @Transactional
  public SiteResponse addSite(SiteRequest siteRequest) {
    logger.entry("begin addSite()");
    boolean canEdit = isEditPermissionAllowed(siteRequest.getStudyId(), siteRequest.getUserId());

    if (!canEdit) {
      throw new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED);
    }

    List<SiteEntity> sitesList =
        siteRepository.findByLocationIdAndStudyId(
            siteRequest.getLocationId(), siteRequest.getStudyId());
    if (CollectionUtils.isNotEmpty(sitesList)) {
      sitesList.get(0);
      throw new ErrorCodeException(ErrorCode.SITE_EXISTS);
    }

    Optional<StudyEntity> optStudyEntity = studyRepository.findById(siteRequest.getStudyId());
    if (OPEN_STUDY.equalsIgnoreCase(optStudyEntity.get().getType())) {
      throw new ErrorCodeException(ErrorCode.CANNOT_ADD_SITE_FOR_OPEN_STUDY);
    }

    SiteResponse siteResponse =
        saveSiteWithSitePermissions(
            siteRequest.getStudyId(), siteRequest.getLocationId(), siteRequest.getUserId());
    logger.exit(
        String.format(
            "Site %s added to locationId=%s and studyId=%s",
            siteResponse.getSiteId(), siteRequest.getLocationId(), siteRequest.getStudyId()));
    return new SiteResponse(siteResponse.getSiteId(), MessageCode.ADD_SITE_SUCCESS);
  }

  private SiteResponse saveSiteWithSitePermissions(
      String studyId, String locationId, String userId) {
    logger.entry("saveSiteWithStudyPermission()");

    List<StudyPermissionEntity> userStudypermissionList =
        studyPermissionRepository.findByStudyId(studyId);

    SiteEntity site = new SiteEntity();
    Optional<StudyEntity> studyInfo = studyRepository.findById(studyId);
    if (studyInfo.isPresent()) {
      site.setStudy(studyInfo.get());
    }
    Optional<LocationEntity> location = locationRepository.findById(locationId);
    if (location.isPresent()) {
      site.setLocation(location.get());
    }
    site.setCreatedBy(userId);
    site.setStatus(SiteStatus.ACTIVE.value());
    addSitePermissions(userId, userStudypermissionList, site);
    site = siteRepository.save(site);

    logger.exit(
        String.format(
            "saved siteId=%s with %d site permissions",
            site.getId(), site.getSitePermissions().size()));
    return SiteMapper.toSiteResponse(site);
  }

  private void addSitePermissions(
      String userId, List<StudyPermissionEntity> userStudypermissionList, SiteEntity site) {
    for (StudyPermissionEntity studyPermission : userStudypermissionList) {
      Permission editPermission =
          studyPermission.getUrAdminUser().getId().equals(userId)
              ? Permission.EDIT
              : studyPermission.getEdit();
      SitePermissionEntity sitePermission = new SitePermissionEntity();
      sitePermission.setUrAdminUser(studyPermission.getUrAdminUser());
      sitePermission.setStudy(studyPermission.getStudy());
      sitePermission.setApp(studyPermission.getApp());
      sitePermission.setCanEdit(editPermission);
      sitePermission.setCreatedBy(userId);
      site.addSitePermissionEntity(sitePermission);
    }
  }

  @Override
  @Transactional
  public ParticipantResponse addNewParticipant(
      ParticipantDetailRequest participant, String userId, AuditLogEventRequest auditRequest) {
    logger.entry("begin addNewParticipant()");

    Optional<SiteEntity> optSite = siteRepository.findById(participant.getSiteId());

    if (!optSite.isPresent() || !optSite.get().getStatus().equals(ACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    SiteEntity site = optSite.get();
    ErrorCode errorCode = validationForAddNewParticipant(participant, userId, site);
    if (errorCode != null) {
      throw new ErrorCodeException(errorCode);
    }

    ParticipantRegistrySiteEntity participantRegistrySite =
        ParticipantMapper.fromParticipantRequest(participant, site);
    participantRegistrySite.setCreatedBy(userId);
    participantRegistrySite =
        participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);
    ParticipantResponse response =
        new ParticipantResponse(
            MessageCode.ADD_PARTICIPANT_SUCCESS, participantRegistrySite.getId());

    auditRequest.setAppId(site.getStudy().getAppId());
    auditRequest.setStudyId(site.getStudyId());
    auditRequest.setUserId(userId);
    auditRequest.setSiteId(site.getId());
    auditRequest.setParticipantId(participantRegistrySite.getId());

    Map<String, String> map = Collections.singletonMap("site_id", site.getId());
    participantManagerHelper.logEvent(PARTICIPANT_EMAIL_ADDED, auditRequest, map);

    logger.exit(String.format("participantRegistrySiteId=%s", participantRegistrySite.getId()));
    return response;
  }

  private ErrorCode validationForAddNewParticipant(
      ParticipantDetailRequest participant, String userId, SiteEntity site) {

    Optional<SitePermissionEntity> optSitePermission =
        sitePermissionRepository.findByUserIdAndSiteId(userId, participant.getSiteId());

    if (!optSitePermission.isPresent()
        || !optSitePermission.get().getCanEdit().equals(Permission.EDIT)) {
      return ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED;
    }

    if (site.getStudy() != null && OPEN_STUDY.equals(site.getStudy().getType())) {
      return ErrorCode.OPEN_STUDY;
    }

    Optional<ParticipantRegistrySiteEntity> registry =
        participantRegistrySiteRepository.findByStudyIdAndEmail(
            site.getStudy().getId(), participant.getEmail());

    if (registry.isPresent()) {
      ParticipantRegistrySiteEntity participantRegistrySite = registry.get();
      Optional<ParticipantStudyEntity> participantStudy =
          participantStudyRepository.findByParticipantRegistrySiteId(
              participantRegistrySite.getId());

      if (participantStudy.isPresent()) {
        return ErrorCode.EMAIL_EXISTS;
      }
    }
    return null;
  }

  @Override
  public ParticipantRegistryResponse getParticipants(
      String userId, String siteId, String onboardingStatus, AuditLogEventRequest auditRequest) {
    logger.info("getParticipants()");
    Optional<SiteEntity> optSite = siteRepository.findById(siteId);

    if (!optSite.isPresent()) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_FOUND);
    }

    Optional<SitePermissionEntity> optSitePermission =
        sitePermissionRepository.findByUserIdAndSiteId(userId, siteId);

    if (!optSitePermission.isPresent()
        || Permission.NO_PERMISSION
            == Permission.fromValue(optSitePermission.get().getCanEdit().value())) {
      throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
    }

    ParticipantRegistryDetail participantRegistryDetail =
        ParticipantMapper.fromSite(optSite.get(), optSitePermission.get(), siteId);
    Map<String, Long> statusWithCountMap = getOnboardingStatusWithCount(siteId);
    participantRegistryDetail.setCountByStatus(statusWithCountMap);

    List<ParticipantRegistrySiteEntity> participantRegistrySites = null;
    if (StringUtils.isEmpty(onboardingStatus)) {
      participantRegistrySites = participantRegistrySiteRepository.findBySiteId(siteId);
    } else {
      participantRegistrySites =
          (List<ParticipantRegistrySiteEntity>)
              CollectionUtils.emptyIfNull(
                  participantRegistrySiteRepository.findBySiteIdAndStatus(
                      siteId, onboardingStatus));
    }

    addRegistryParticipants(participantRegistryDetail, participantRegistrySites);

    ParticipantRegistryResponse participantRegistryResponse =
        new ParticipantRegistryResponse(
            MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS, participantRegistryDetail);

    auditRequest.setSiteId(siteId);
    auditRequest.setStudyId(optSite.get().getStudyId());
    auditRequest.setAppId(optSite.get().getStudy().getAppId());
    auditRequest.setUserId(userId);

    Map<String, String> map = Collections.singletonMap("site_id", siteId);
    participantManagerHelper.logEvent(SITE_PARTICIPANT_REGISTRY_VIEWED, auditRequest, map);

    logger.exit(String.format("message=%s", participantRegistryResponse.getMessage()));
    return participantRegistryResponse;
  }

  private Map<String, Long> getOnboardingStatusWithCount(String siteId) {
    List<ParticipantRegistrySiteCount> statusCount =
        (List<ParticipantRegistrySiteCount>)
            CollectionUtils.emptyIfNull(
                participantRegistrySiteRepository.findStatusCountBySiteId(siteId));

    Map<String, Long> statusWithCountMap = new HashMap<>();
    for (OnboardingStatus onboardingStatus : OnboardingStatus.values()) {
      statusWithCountMap.put(onboardingStatus.getCode(), (long) 0);
    }

    long total = 0;
    for (ParticipantRegistrySiteCount count : statusCount) {
      total += count.getCount();
      statusWithCountMap.put(count.getOnboardingStatus(), count.getCount());
    }

    statusWithCountMap.put(OnboardingStatus.ALL.getCode(), total);
    return statusWithCountMap;
  }

  private void addRegistryParticipants(
      ParticipantRegistryDetail participantRegistryDetail,
      List<ParticipantRegistrySiteEntity> participantRegistrySites) {
    List<String> registryIds =
        CollectionUtils.emptyIfNull(participantRegistrySites)
            .stream()
            .map(ParticipantRegistrySiteEntity::getId)
            .collect(Collectors.toList());

    List<ParticipantStudyEntity> participantStudies = new ArrayList<>();
    // Check not empty for Ids to avoid SQLSyntaxErrorException
    if (CollectionUtils.isNotEmpty(registryIds)) {
      participantStudies =
          (List<ParticipantStudyEntity>)
              CollectionUtils.emptyIfNull(
                  participantStudyRepository.findParticipantsByParticipantRegistrySite(
                      registryIds));
    }

    for (ParticipantRegistrySiteEntity participantRegistrySite : participantRegistrySites) {
      ParticipantDetail participant = new ParticipantDetail();
      participant =
          ParticipantMapper.toParticipantDetails(
              participantStudies, participantRegistrySite, participant);
      participantRegistryDetail.getRegistryParticipants().add(participant);
    }
  }

  private boolean isEditPermissionAllowed(String studyId, String userId) {
    logger.entry("isEditPermissionAllowed()");
    Optional<StudyPermissionEntity> optStudyPermissionEntity =
        studyPermissionRepository.findByStudyIdAndUserId(studyId, userId);
    if (optStudyPermissionEntity.isPresent()) {
      StudyPermissionEntity studyPermission = optStudyPermissionEntity.get();
      String appInfoId = studyPermission.getApp().getId();
      Optional<AppPermissionEntity> optAppPermissionEntity =
          appPermissionRepository.findByUserIdAndAppId(userId, appInfoId);
      if (optAppPermissionEntity.isPresent()) {
        AppPermissionEntity appPermission = optAppPermissionEntity.get();
        logger.exit(String.format("editValue=%d", Permission.EDIT.value()));
        if (studyPermission.getEdit() == Permission.EDIT
            || appPermission.getEdit() == Permission.EDIT) {
          return true;
        }
      }
    }
    logger.exit("default permission is view, return false");
    return false;
  }

  @Override
  @Transactional
  public SiteStatusResponse toggleSiteStatus(
      String userId, String siteId, AuditLogEventRequest auditRequest) {
    logger.entry("toggleSiteStatus()");

    validateDecommissionSiteRequest(userId, siteId);

    Optional<SiteEntity> optSiteEntity = siteRepository.findById(siteId);
    auditRequest.setUserId(userId);
    auditRequest.setSiteId(siteId);
    auditRequest.setStudyId(optSiteEntity.get().getStudyId());

    Map<String, String> map = Collections.singletonMap("site_id", siteId);

    SiteEntity site = optSiteEntity.get();
    if (SiteStatus.DEACTIVE == SiteStatus.fromValue(site.getStatus())) {
      site.setStatus(SiteStatus.ACTIVE.value());
      site = siteRepository.saveAndFlush(site);

      participantManagerHelper.logEvent(SITE_ACTIVATED_FOR_STUDY, auditRequest, map);

      logger.exit(String.format(" Site status changed to ACTIVE for siteId=%s", site.getId()));
      return new SiteStatusResponse(
          site.getId(), site.getStatus(), MessageCode.RECOMMISSION_SITE_SUCCESS);
    }

    site.setStatus(SiteStatus.DEACTIVE.value());
    siteRepository.saveAndFlush(site);
    updateSitePermissions(siteId);

    participantManagerHelper.logEvent(SITE_DECOMMISSIONED_FOR_STUDY, auditRequest, map);

    logger.exit(String.format("Site status changed to DEACTIVE for siteId=%s", site.getId()));
    return new SiteStatusResponse(
        site.getId(), site.getStatus(), MessageCode.DECOMMISSION_SITE_SUCCESS);
  }

  private void validateDecommissionSiteRequest(String userId, String siteId) {
    Optional<SitePermissionEntity> optSitePermission =
        sitePermissionRepository.findByUserIdAndSiteId(userId, siteId);
    if (!optSitePermission.isPresent()) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_FOUND);
    }

    SitePermissionEntity sitePermission = optSitePermission.get();
    if (OPEN.equalsIgnoreCase(sitePermission.getStudy().getType())) {
      throw new ErrorCodeException(ErrorCode.CANNOT_DECOMMISSION_SITE_FOR_OPEN_STUDY);
    }

    String studyId = sitePermission.getStudy().getId();
    boolean canEdit = isEditPermissionAllowed(studyId, userId);
    if (!canEdit) {
      throw new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED);
    }

    List<String> status = Arrays.asList(ENROLLED_STATUS, STATUS_ACTIVE);
    Optional<Long> optParticipantStudyCount =
        participantStudyRepository.findByStudyIdAndStatus(status, studyId);

    if (optParticipantStudyCount.isPresent() && optParticipantStudyCount.get() > 0) {
      throw new ErrorCodeException(ErrorCode.CANNOT_DECOMMISSION_SITE_FOR_ENROLLED_ACTIVE_STATUS);
    }

    return;
  }

  private void updateSitePermissions(String siteId) {

    List<SitePermissionEntity> sitePermissions =
        (List<SitePermissionEntity>)
            CollectionUtils.emptyIfNull(sitePermissionRepository.findBySiteId(siteId));

    List<String> studyIds =
        sitePermissions
            .stream()
            .distinct()
            .map(studyId -> studyId.getStudy().getId())
            .collect(Collectors.toList());

    List<String> siteAdminIds =
        sitePermissions
            .stream()
            .distinct()
            .map(urAdminId -> urAdminId.getUrAdminUser().getId())
            .collect(Collectors.toList());

    List<StudyPermissionEntity> studyPermissions =
        (List<StudyPermissionEntity>)
            CollectionUtils.emptyIfNull(
                studyPermissionRepository.findByByUserIdsAndStudyIds(siteAdminIds, studyIds));

    List<String> studyAdminIds =
        studyPermissions
            .stream()
            .distinct()
            .map(studyAdminId -> studyAdminId.getUrAdminUser().getId())
            .collect(Collectors.toList());

    for (SitePermissionEntity sitePermission : sitePermissions) {
      if (studyAdminIds.contains(sitePermission.getUrAdminUser().getId())) {
        sitePermission.setCanEdit(Permission.VIEW);
        sitePermissionRepository.saveAndFlush(sitePermission);
      } else {
        sitePermissionRepository.delete(sitePermission);
      }
    }
    deactivateYetToEnrollParticipants(siteId);
  }

  private void deactivateYetToEnrollParticipants(String siteId) {
    List<ParticipantStudyEntity> participantStudies =
        (List<ParticipantStudyEntity>)
            CollectionUtils.emptyIfNull(
                participantStudyRepository.findBySiteIdAndStatus(siteId, YET_TO_JOIN));

    List<String> participantRegistrySiteIds =
        participantStudies
            .stream()
            .distinct()
            .map(participantStudy -> participantStudy.getParticipantRegistrySite().getId())
            .collect(Collectors.toList());

    List<ParticipantRegistrySiteEntity> participantRegistrySites =
        participantRegistrySiteRepository.findAllById(participantRegistrySiteIds);

    for (ParticipantRegistrySiteEntity participantRegistrySite :
        CollectionUtils.emptyIfNull(participantRegistrySites)) {
      participantRegistrySite.setOnboardingStatus(OnboardingStatus.DISABLED.getCode());
      participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public ParticipantDetailResponse getParticipantDetails(
      String participantRegistrySiteId, String userId) {
    logger.entry("begin getParticipantDetails()");

    Optional<ParticipantRegistrySiteEntity> optParticipantRegistry =
        participantRegistrySiteRepository.findById(participantRegistrySiteId);

    ErrorCode errorCode = validateParticipantDetailsRequest(optParticipantRegistry, userId);
    if (errorCode != null) {
      throw new ErrorCodeException(errorCode);
    }

    ParticipantDetail participantDetail =
        ParticipantMapper.toParticipantDetailsResponse(optParticipantRegistry.get());
    List<ParticipantStudyEntity> participantsEnrollments =
        participantStudyRepository.findParticipantsEnrollment(participantRegistrySiteId);

    if (CollectionUtils.isEmpty(participantsEnrollments)) {
      Enrollment enrollment = new Enrollment(null, "-", YET_TO_ENROLL, "-");
      participantDetail.getEnrollments().add(enrollment);
    } else {
      ParticipantMapper.addEnrollments(participantDetail, participantsEnrollments);
      List<String> participantStudyIds =
          participantsEnrollments
              .stream()
              .map(ParticipantStudyEntity::getId)
              .collect(Collectors.toList());

      List<StudyConsentEntity> studyConsents =
          studyConsentRepository.findByParticipantRegistrySiteId(participantStudyIds);

      List<ConsentHistory> consentHistories =
          studyConsents.stream().map(ConsentMapper::toConsentHistory).collect(Collectors.toList());
      participantDetail.getConsentHistory().addAll(consentHistories);
    }

    logger.exit(
        String.format(
            "total enrollments=%d, and consentHistories=%d",
            participantDetail.getEnrollments().size(),
            participantDetail.getConsentHistory().size()));

    return new ParticipantDetailResponse(
        MessageCode.GET_PARTICIPANT_DETAILS_SUCCESS, participantDetail);
  }

  private ErrorCode validateParticipantDetailsRequest(
      Optional<ParticipantRegistrySiteEntity> optParticipantRegistry, String userId) {
    if (!optParticipantRegistry.isPresent()) {
      logger.exit(ErrorCode.PARTICIPANT_REGISTRY_SITE_NOT_FOUND);
      return ErrorCode.PARTICIPANT_REGISTRY_SITE_NOT_FOUND;
    }

    Optional<SitePermissionEntity> sitePermission =
        sitePermissionRepository.findSitePermissionByUserIdAndSiteId(
            userId, optParticipantRegistry.get().getSite().getId());
    if (!sitePermission.isPresent()) {
      logger.exit(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
      return ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED;
    }
    return null;
  }

  @Override
  @Transactional
  public InviteParticipantResponse inviteParticipants(
      InviteParticipantRequest inviteParticipantRequest, AuditLogEventRequest auditRequest) {
    logger.entry("begin inviteParticipants()");

    Optional<SiteEntity> optSiteEntity =
        siteRepository.findById(inviteParticipantRequest.getSiteId());

    if (!optSiteEntity.isPresent() || !ACTIVE_STATUS.equals(optSiteEntity.get().getStatus())) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    Optional<SitePermissionEntity> optSitePermissionEntity =
        sitePermissionRepository.findSitePermissionByUserIdAndSiteId(
            inviteParticipantRequest.getUserId(), inviteParticipantRequest.getSiteId());
    if (!optSitePermissionEntity.isPresent()
        || Permission.EDIT
            != Permission.fromValue(optSitePermissionEntity.get().getCanEdit().value())) {
      throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
    }

    List<ParticipantRegistrySiteEntity> participantsList =
        participantRegistrySiteRepository.findByIds(inviteParticipantRequest.getIds());
    SiteEntity siteEntity = optSiteEntity.get();
    auditRequest.setUserId(inviteParticipantRequest.getUserId());
    List<ParticipantRegistrySiteEntity> invitedParticipants =
        findEligibleParticipantsAndSendInviteEmail(participantsList, siteEntity, auditRequest);

    participantRegistrySiteRepository.saveAll(invitedParticipants);

    participantsList.removeAll(invitedParticipants);
    List<String> failedParticipantIds =
        participantsList
            .stream()
            .map(ParticipantRegistrySiteEntity::getId)
            .collect(Collectors.toList());

    List<String> invitedParticipantIds =
        invitedParticipants
            .stream()
            .map(ParticipantRegistrySiteEntity::getId)
            .collect(Collectors.toList());

    logger.exit(
        String.format(
            "%d email invitations sent and %d failed",
            invitedParticipantIds.size(), failedParticipantIds.size()));
    return new InviteParticipantResponse(
        MessageCode.PARTICIPANTS_INVITED_SUCCESS, invitedParticipantIds, failedParticipantIds);
  }

  private List<ParticipantRegistrySiteEntity> findEligibleParticipantsAndSendInviteEmail(
      List<ParticipantRegistrySiteEntity> participants,
      SiteEntity siteEntity,
      AuditLogEventRequest auditRequest) {
    List<ParticipantRegistrySiteEntity> invitedParticipants = new ArrayList<>();
    for (ParticipantRegistrySiteEntity participantRegistrySiteEntity : participants) {
      OnboardingStatus onboardingStatus =
          OnboardingStatus.fromCode(participantRegistrySiteEntity.getOnboardingStatus());
      if (OnboardingStatus.DISABLED == onboardingStatus
          || OnboardingStatus.ENROLLED == onboardingStatus) {
        continue;
      }

      String token = RandomStringUtils.randomAlphanumeric(8);
      participantRegistrySiteEntity.setEnrollmentToken(token);
      participantRegistrySiteEntity.setInvitationDate(new Timestamp(Instant.now().toEpochMilli()));

      String status = onboardingStatus.getStatus();
      if (OnboardingStatus.NEW == onboardingStatus) {
        participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.INVITED.getCode());
      }

      participantRegistrySiteEntity.setInvitationCount(
          participantRegistrySiteEntity.getInvitationCount() + 1);

      participantRegistrySiteEntity.setEnrollmentTokenExpiry(
          new Timestamp(
              Instant.now()
                  .plus(appPropertyConfig.getEnrollmentTokenExpiryInHours(), ChronoUnit.HOURS)
                  .toEpochMilli()));
      EmailResponse emailResponse = sendInvitationEmail(participantRegistrySiteEntity, siteEntity);

      Map<String, String> map = Collections.singletonMap("site_id", siteEntity.getId());
      auditRequest.setSiteId(siteEntity.getId());
      auditRequest.setStudyId(siteEntity.getStudyId());
      auditRequest.setAppId(siteEntity.getStudy().getAppId());

      if (MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER
          .getMessage()
          .equals(emailResponse.getMessage())) {
        invitedParticipants.add(participantRegistrySiteEntity);
        // Audit logging
        ParticipantManagerEvent participantManagerEvent =
            OnboardingStatus.NEW.getStatus().equals(status)
                ? INVITATION_EMAIL_SENT
                : PARTICIPANT_INVITATION_EMAIL_RESENT;
        participantManagerHelper.logEvent(participantManagerEvent, auditRequest, map);
      }
    }

    return invitedParticipants;
  }

  private EmailResponse sendInvitationEmail(
      ParticipantRegistrySiteEntity participantRegistrySiteEntity, SiteEntity siteEntity) {
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("study name", siteEntity.getStudy().getName());
    templateArgs.put("org name", appPropertyConfig.getOrgName());
    templateArgs.put("enrolment token", participantRegistrySiteEntity.getEnrollmentToken());
    templateArgs.put("contact email address", appPropertyConfig.getFromEmailAddress());
    EmailRequest emailRequest =
        new EmailRequest(
            appPropertyConfig.getFromEmailAddress(),
            new String[] {participantRegistrySiteEntity.getEmail()},
            null,
            null,
            appPropertyConfig.getParticipantInviteSubject(),
            appPropertyConfig.getParticipantInviteBody(),
            templateArgs);
    return emailService.sendMimeMail(emailRequest);
  }

  @Override
  @Transactional
  public ImportParticipantResponse importParticipants(
      String userId,
      String siteId,
      MultipartFile multipartFile,
      AuditLogEventRequest auditRequest) {
    logger.entry("begin importParticipants()");

    // Validate site type, status and access permission
    Optional<SiteEntity> optSite = siteRepository.findById(siteId);

    if (!optSite.isPresent() || !optSite.get().getStatus().equals(ACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    SiteEntity siteEntity = optSite.get();
    auditRequest.setSiteId(siteId);
    auditRequest.setUserId(userId);
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());

    Map<String, String> map = Collections.singletonMap("site_id", siteEntity.getId());

    if (siteEntity.getStudy() != null && OPEN_STUDY.equals(siteEntity.getStudy().getType())) {
      participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
      throw new ErrorCodeException(ErrorCode.OPEN_STUDY);
    }

    Optional<SitePermissionEntity> optSitePermission =
        sitePermissionRepository.findSitePermissionByUserIdAndSiteId(userId, siteId);

    if (!optSitePermission.isPresent()
        || !optSitePermission.get().getCanEdit().value().equals(Permission.EDIT.value())) {
      participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
      throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
    }

    // iterate and save valid email id's
    try (Workbook workbook =
        WorkbookFactory.create(new BufferedInputStream(multipartFile.getInputStream()))) {

      Sheet sheet = workbook.getSheetAt(0);
      Row row = sheet.getRow(0);
      String columnName = row.getCell(EMAIL_ADDRESS_COLUMN).getStringCellValue();
      if (!"Email Address".equalsIgnoreCase(columnName)) {
        participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
        throw new ErrorCodeException(ErrorCode.DOCUMENT_NOT_IN_PRESCRIBED_FORMAT);
      }

      Iterator<Row> rows = sheet.rowIterator();
      Set<String> invalidEmails = new HashSet<>();
      Set<String> validEmails = new HashSet<>();

      // Skip headers row
      rows.next();
      while (rows.hasNext()) {
        Row r = rows.next();

        String email = r.getCell(EMAIL_ADDRESS_COLUMN).getStringCellValue();
        if (StringUtils.isBlank(email) || !Pattern.matches(EMAIL_REGEX, email)) {
          invalidEmails.add(email);
          continue;
        }
        validEmails.add(email);
      }

      ImportParticipantResponse importParticipantResponse =
          saveImportParticipant(validEmails, userId, siteEntity);
      importParticipantResponse.getInvalidEmails().addAll(invalidEmails);

      ParticipantManagerEvent participantManagerEvent =
          importParticipantResponse.getInvalidEmails().isEmpty()
                  && importParticipantResponse.getDuplicateEmails().isEmpty()
              ? PARTICIPANTS_EMAIL_LIST_IMPORTED
              : PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED;
      participantManagerHelper.logEvent(participantManagerEvent, auditRequest, map);

      return importParticipantResponse;
    } catch (EncryptedDocumentException | IOException | InvalidFormatException e) {
      participantManagerHelper.logEvent(PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED, auditRequest, map);
      throw new ErrorCodeException(ErrorCode.FAILED_TO_IMPORT_PARTICIPANTS);
    }
  }

  private ImportParticipantResponse saveImportParticipant(
      Set<String> emails, String userId, SiteEntity siteEntity) {

    List<ParticipantRegistrySiteEntity> participantRegistrySiteEntities =
        (List<ParticipantRegistrySiteEntity>)
            CollectionUtils.emptyIfNull(
                participantRegistrySiteRepository.findByStudyIdAndEmails(
                    siteEntity.getStudy().getId(), emails));

    List<String> participantRegistryEmails =
        (List<String>)
            CollectionUtils.emptyIfNull(
                participantRegistrySiteEntities
                    .stream()
                    .distinct()
                    .map(ParticipantRegistrySiteEntity::getEmail)
                    .collect(Collectors.toList()));

    List<String> newEmails =
        (List<String>)
            CollectionUtils.removeAll(new ArrayList<String>(emails), participantRegistryEmails);

    List<ParticipantDetail> savedParticipants = new ArrayList<>();
    for (String email : newEmails) {
      ParticipantDetail participantDetail = new ParticipantDetail();
      participantDetail.setEmail(email);
      ParticipantRegistrySiteEntity participantRegistrySite =
          ParticipantMapper.fromParticipantDetail(participantDetail, siteEntity);
      participantRegistrySite.setCreatedBy(userId);
      participantRegistrySite =
          participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);
      participantDetail.setId(participantRegistrySite.getId());
      savedParticipants.add(participantDetail);
    }

    logger.exit(
        String.format(
            "%d duplicates email found and %d new emails saved",
            participantRegistryEmails.size(), newEmails.size()));
    return new ImportParticipantResponse(
        MessageCode.IMPORT_PARTICIPANT_SUCCESS, savedParticipants, participantRegistryEmails);
  }

  @Override
  @Transactional
  public ParticipantStatusResponse updateOnboardingStatus(
      ParticipantStatusRequest participantStatusRequest, AuditLogEventRequest auditRequest) {
    logger.entry("begin updateOnboardingStatus()");

    Optional<SiteEntity> optSite = siteRepository.findById(participantStatusRequest.getSiteId());

    if (!optSite.isPresent() || !optSite.get().getStatus().equals(ACTIVE_STATUS)) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_EXIST_OR_INACTIVE);
    }

    Optional<SitePermissionEntity> optSitePermission =
        sitePermissionRepository.findByUserIdAndSiteId(
            participantStatusRequest.getUserId(), participantStatusRequest.getSiteId());

    if (!optSitePermission.isPresent()
        || !optSitePermission.get().getCanEdit().value().equals(Permission.EDIT.value())) {
      throw new ErrorCodeException(ErrorCode.MANAGE_SITE_PERMISSION_ACCESS_DENIED);
    }

    OnboardingStatus onboardingStatus =
        OnboardingStatus.fromCode(participantStatusRequest.getStatus());
    if (onboardingStatus == null) {
      throw new ErrorCodeException(ErrorCode.INVALID_ONBOARDING_STATUS);
    }

    participantRegistrySiteRepository.updateOnboardingStatus(
        participantStatusRequest.getStatus(), participantStatusRequest.getIds());
    SiteEntity site = optSite.get();
    auditRequest.setSiteId(site.getId());
    auditRequest.setUserId(participantStatusRequest.getUserId());
    auditRequest.setStudyId(site.getStudyId());
    auditRequest.setAppId(site.getStudy().getAppId());

    Map<String, String> map = Collections.singletonMap("site_id", optSite.get().getId());
    if (participantStatusRequest.getStatus().equals(OnboardingStatus.DISABLED.getCode())) {
      participantManagerHelper.logEvent(PARTICIPANT_INVITATION_DISABLED, auditRequest, map);
    } else if (participantStatusRequest.getStatus().equals(OnboardingStatus.NEW.getCode())) {
      participantManagerHelper.logEvent(PARTICIPANT_INVITATION_ENABLED, auditRequest, map);
    }
    logger.exit(
        String.format(
            "Onboarding status changed to %s for %d participants in Site %s",
            participantStatusRequest.getStatus(),
            participantStatusRequest.getIds().size(),
            participantStatusRequest.getSiteId()));
    return new ParticipantStatusResponse(MessageCode.UPDATE_STATUS_SUCCESS);
  }

  @Override
  @Transactional(readOnly = true)
  public SiteDetailsResponse getSites(String userId) {
    logger.entry("getSites(userId)");

    List<SitePermissionEntity> sitePermissions =
        sitePermissionRepository.findSitePermissionByUserId(userId);
    if (CollectionUtils.isEmpty(sitePermissions)) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_FOUND);
    }

    List<String> siteIds =
        sitePermissions
            .stream()
            .map(s -> s.getSite().getId())
            .distinct()
            .collect(Collectors.toList());

    Map<String, Long> invitedCountBySiteIdMap = getInvitedCountBySiteId(siteIds);

    Map<String, Long> enrolledCountBySiteIdMap = getEnrolledCountBySiteId(siteIds);

    Map<StudyEntity, List<SitePermissionEntity>> sitePermissionsByStudy =
        sitePermissions.stream().collect(Collectors.groupingBy(SitePermissionEntity::getStudy));

    Map<String, StudyPermissionEntity> studyPermissionsByStudyInfoId =
        getStudyPermissionsByStudyId(userId, sitePermissions);

    List<StudyDetails> studies = new ArrayList<>();
    for (Map.Entry<StudyEntity, List<SitePermissionEntity>> entry :
        sitePermissionsByStudy.entrySet()) {
      StudyEntity study = entry.getKey();
      StudyDetails studyDetail = StudyMapper.toStudyDetails(studyPermissionsByStudyInfoId, study);

      addSites(invitedCountBySiteIdMap, enrolledCountBySiteIdMap, study, studyDetail);
      studyDetail.setSitesCount((long) studyDetail.getSites().size());

      studies.add(studyDetail);
    }

    logger.exit(String.format("%d studies found", studies.size()));
    return new SiteDetailsResponse(studies, MessageCode.GET_SITES_SUCCESS);
  }

  private Map<String, StudyPermissionEntity> getStudyPermissionsByStudyId(
      String userId, List<SitePermissionEntity> sitePermissions) {
    List<String> usersStudyIds =
        sitePermissions
            .stream()
            .distinct()
            .map(studyEntity -> studyEntity.getStudy().getId())
            .collect(Collectors.toList());

    List<StudyPermissionEntity> studyPermissions =
        studyPermissionRepository.findByStudyIds(usersStudyIds, userId);

    Map<String, StudyPermissionEntity> studyPermissionsByStudyInfoId = new HashMap<>();
    if (CollectionUtils.isNotEmpty(studyPermissions)) {
      studyPermissionsByStudyInfoId =
          studyPermissions
              .stream()
              .collect(Collectors.toMap(e -> e.getStudy().getId(), Function.identity()));
    }
    return studyPermissionsByStudyInfoId;
  }

  private Map<String, Long> getInvitedCountBySiteId(List<String> usersSiteIds) {
    List<ParticipantRegistrySiteEntity> participantRegistry =
        participantRegistrySiteRepository.findBySiteIds(usersSiteIds);

    return participantRegistry
        .stream()
        .collect(
            Collectors.groupingBy(
                e -> e.getSite().getId(),
                Collectors.summingLong(ParticipantRegistrySiteEntity::getInvitationCount)));
  }

  private Map<String, Long> getEnrolledCountBySiteId(List<String> usersSiteIds) {
    List<ParticipantStudyEntity> participantsEnrollments =
        participantStudyRepository.findBySiteIds(usersSiteIds);

    return participantsEnrollments
        .stream()
        .collect(Collectors.groupingBy(e -> e.getSite().getId(), Collectors.counting()));
  }

  private void addSites(
      Map<String, Long> invitedCountBySiteIdMap,
      Map<String, Long> enrolledCountBySiteIdMap,
      StudyEntity study,
      StudyDetails studyDetail) {
    for (SiteEntity siteEntity : study.getSites()) {
      Long invitedCount = invitedCountBySiteIdMap.get(siteEntity.getId());
      Long enrolledCount = enrolledCountBySiteIdMap.get(siteEntity.getId());

      SiteDetails site = new SiteDetails();
      site.setId(siteEntity.getId());
      site.setName(siteEntity.getLocation().getName());
      site.setEnrolled(enrolledCount);

      Double percentage;
      String studyType = study.getType();
      if (studyType.equals(OPEN_STUDY) && siteEntity.getTargetEnrollment() != null) {
        site.setInvited(Long.valueOf(siteEntity.getTargetEnrollment()));
      } else if (studyType.equals(CLOSE_STUDY)) {
        site.setInvited(invitedCount);
      }

      if (site.getInvited() != null && site.getEnrolled() != null) {
        if (site.getInvited() != 0 && site.getInvited() >= site.getEnrolled()) {
          percentage =
              (Double.valueOf(site.getEnrolled()) * 100) / Double.valueOf(site.getInvited());
          site.setEnrollmentPercentage(percentage);
        } else if (site.getInvited() != 0
            && site.getEnrolled() >= site.getInvited()
            && studyType.equals(OPEN_STUDY)) {
          site.setEnrollmentPercentage(DEFAULT_PERCENTAGE);
        }
      }
      studyDetail.getSites().add(site);
    }
  }

  @Override
  @Transactional
  public UpdateTargetEnrollmentResponse updateTargetEnrollment(
      UpdateTargetEnrollmentRequest enrollmentRequest, AuditLogEventRequest auditRequest) {
    logger.entry("updateTargetEnrollment()");

    Optional<StudyPermissionEntity> optStudyPermission =
        studyPermissionRepository.findByStudyIdAndUserId(
            enrollmentRequest.getStudyId(), enrollmentRequest.getUserId());

    StudyPermissionEntity studyPermission = optStudyPermission.get();
    if (!optStudyPermission.isPresent() || Permission.VIEW == studyPermission.getEdit()) {
      throw new ErrorCodeException(ErrorCode.STUDY_PERMISSION_ACCESS_DENIED);
    }

    if (CLOSE_STUDY.equalsIgnoreCase(studyPermission.getStudy().getType())) {
      throw new ErrorCodeException(ErrorCode.CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_CLOSE_STUDY);
    }

    Optional<SiteEntity> optSiteEntity =
        siteRepository.findByStudyId(enrollmentRequest.getStudyId());
    if (!optSiteEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.SITE_NOT_FOUND);
    }

    SiteEntity site = optSiteEntity.get();
    if (SiteStatus.DEACTIVE == SiteStatus.fromValue(site.getStatus())) {
      throw new ErrorCodeException(
          ErrorCode.CANNOT_UPDATE_ENROLLMENT_TARGET_FOR_DECOMMISSIONED_SITE);
    }

    site.setTargetEnrollment(enrollmentRequest.getTargetEnrollment());
    siteRepository.saveAndFlush(site);

    auditRequest.setUserId(enrollmentRequest.getUserId());
    auditRequest.setStudyId(enrollmentRequest.getStudyId());
    auditRequest.setSiteId(site.getId());

    Map<String, String> map = Collections.singletonMap("site_id", site.getId());
    participantManagerHelper.logEvent(ENROLLMENT_TARGET_UPDATED, auditRequest, map);

    logger.exit(
        String.format(
            "target enrollment changed to %d for siteId=%s",
            site.getTargetEnrollment(), site.getId()));
    return new UpdateTargetEnrollmentResponse(
        site.getId(), MessageCode.TARGET_ENROLLMENT_UPDATE_SUCCESS);
  }
}
