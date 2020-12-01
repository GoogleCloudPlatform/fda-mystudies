/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.ACCOUNT_UPDATE_EMAIL_SENT;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.NEW_USER_INVITATION_EMAIL_SENT;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.EmailTemplate;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.UserAccountEmailSchedulerTaskEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserAccountEmailSchedulerTaskRepository;
import com.google.cloud.healthcare.fdamystudies.service.ManageUserService;
import java.util.List;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AdminUsersAccountScheduledTaskTest extends BaseMockIT {

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private ManageUserService manageUserService;

  @Autowired private UserAccountEmailSchedulerTaskRepository addNewAdminEmailServiceRepository;

  @BeforeEach
  public void setUp() {}

  @Test
  public void shouldSendEmailInvitationForNewAdmin() throws Exception {

    UserRegAdminEntity admin = testDataHelper.createNonSuperAdmin();
    admin.setSecurityCode(IdGenerator.id());
    testDataHelper.getUserRegAdminRepository().saveAndFlush(admin);

    UserAccountEmailSchedulerTaskEntity adminRecordToSendEmail =
        new UserAccountEmailSchedulerTaskEntity();
    adminRecordToSendEmail.setUserId(admin.getId());
    adminRecordToSendEmail.setAppId("GCPMS001");
    adminRecordToSendEmail.setAppVersion("1.0");
    adminRecordToSendEmail.setCorrelationId(IdGenerator.id());
    adminRecordToSendEmail.setSource("PARTICIPANT MANAGER");
    adminRecordToSendEmail.setMobilePlatform("Unknown");
    adminRecordToSendEmail.setEmailTemplateType(
        EmailTemplate.ACCOUNT_CREATED_EMAIL_TEMPLATE.getTemplate());

    addNewAdminEmailServiceRepository.saveAndFlush(adminRecordToSendEmail);

    manageUserService.sendUserEmail();

    verify(emailSender, atLeastOnce()).send(isA(MimeMessage.class));

    List<UserAccountEmailSchedulerTaskEntity> invitedAdmins =
        addNewAdminEmailServiceRepository.findAll();

    assertTrue(invitedAdmins.isEmpty());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    //    auditRequest.setUserId(admin.getId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(NEW_USER_INVITATION_EMAIL_SENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, NEW_USER_INVITATION_EMAIL_SENT);
  }

  @Test
  public void shouldSendEmailInvitationForUpdateAdmin() throws Exception {

    UserRegAdminEntity admin = testDataHelper.createNonSuperAdmin();
    admin.setSecurityCode(IdGenerator.id());
    testDataHelper.getUserRegAdminRepository().saveAndFlush(admin);

    UserAccountEmailSchedulerTaskEntity adminRecordToSendEmail =
        new UserAccountEmailSchedulerTaskEntity();
    adminRecordToSendEmail.setUserId(admin.getId());
    adminRecordToSendEmail.setAppId("GCPMS001");
    adminRecordToSendEmail.setAppVersion("1.0");
    adminRecordToSendEmail.setCorrelationId(IdGenerator.id());
    adminRecordToSendEmail.setSource("PARTICIPANT MANAGER");
    adminRecordToSendEmail.setMobilePlatform("Unknown");
    adminRecordToSendEmail.setEmailTemplateType(
        EmailTemplate.ACCOUNT_UPDATED_EMAIL_TEMPLATE.getTemplate());

    addNewAdminEmailServiceRepository.saveAndFlush(adminRecordToSendEmail);

    manageUserService.sendUserEmail();

    verify(emailSender, atLeastOnce()).send(isA(MimeMessage.class));

    List<UserAccountEmailSchedulerTaskEntity> invitedAdmins =
        addNewAdminEmailServiceRepository.findAll();

    assertTrue(invitedAdmins.isEmpty());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    //    auditRequest.setUserId(admin.getId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ACCOUNT_UPDATE_EMAIL_SENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, ACCOUNT_UPDATE_EMAIL_SENT);
  }

  @AfterEach
  public void clean() {
    testDataHelper.cleanUp();
  }
}
