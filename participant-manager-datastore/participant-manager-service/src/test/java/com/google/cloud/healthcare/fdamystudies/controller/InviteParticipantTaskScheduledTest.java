/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.INVITATION_EMAIL_SENT;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.InviteParticipantEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.InviteParticipantsEmailRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.service.SiteService;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InviteParticipantTaskScheduledTest extends BaseMockIT {

  @Autowired private TestDataHelper testDataHelper;

  @Autowired SiteService siteService;

  private UserRegAdminEntity userRegAdminEntity;
  private StudyEntity studyEntity;
  private LocationEntity locationEntity;
  private AppEntity appEntity;
  private SiteEntity siteEntity;
  private ParticipantRegistrySiteEntity participantRegistrySiteEntity;
  private ParticipantStudyEntity participantStudyEntity;
  private StudyConsentEntity studyConsentEntity;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private InviteParticipantsEmailRepository invitedParticipantsEmailRepository;

  @BeforeEach
  public void setUp() {
    locationEntity = testDataHelper.createLocation();
    userRegAdminEntity = testDataHelper.createUserRegAdminEntity();
    appEntity = testDataHelper.createAppEntity(userRegAdminEntity);
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntity = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
    participantRegistrySiteEntity =
        testDataHelper.createParticipantRegistrySite(siteEntity, studyEntity);
    participantStudyEntity =
        testDataHelper.createParticipantStudyEntity(
            siteEntity, studyEntity, participantRegistrySiteEntity);
    studyConsentEntity = testDataHelper.createStudyConsentEntity(participantStudyEntity);
  }

  @Test
  public void shouldSendEmailInvitation() throws Exception {

    studyEntity.setApp(appEntity);
    siteEntity.setStudy(studyEntity);
    participantRegistrySiteEntity.setEmail(TestDataHelper.EMAIL_VALUE);
    testDataHelper.getSiteRepository().save(siteEntity);
    testDataHelper.getParticipantRegistrySiteRepository().save(participantRegistrySiteEntity);

    InviteParticipantEntity inviteParticipantsEmail = new InviteParticipantEntity();
    inviteParticipantsEmail.setParticipantRegistrySite(participantRegistrySiteEntity.getId());
    inviteParticipantsEmail.setStudy(siteEntity.getStudy().getId());
    inviteParticipantsEmail.setAppId(appEntity.getAppId());
    inviteParticipantsEmail.setCorrelationId(IdGenerator.id());
    inviteParticipantsEmail.setAppVersion("1.0");
    inviteParticipantsEmail.setMobilePlatform("UNKOWN");

    invitedParticipantsEmailRepository.saveAndFlush(inviteParticipantsEmail);

    siteService.sendInvitationEmail();

    verify(emailSender, atLeastOnce()).send(isA(MimeMessage.class));

    List<InviteParticipantEntity> inviteParticipantsList =
        invitedParticipantsEmailRepository.findAll();

    assertTrue(inviteParticipantsList.isEmpty());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setStudyId(siteEntity.getStudyId());
    auditRequest.setAppId(siteEntity.getStudy().getAppId());
    auditRequest.setCorrelationId(IdGenerator.id());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(INVITATION_EMAIL_SENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, INVITATION_EMAIL_SENT);
  }

  @AfterEach
  public void clean() {
    testDataHelper.cleanUp();
  }
}
