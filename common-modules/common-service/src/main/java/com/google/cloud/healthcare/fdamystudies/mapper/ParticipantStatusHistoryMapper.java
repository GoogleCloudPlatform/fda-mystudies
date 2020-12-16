/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantEnrollmentHistoryEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.sql.Timestamp;
import java.time.Instant;

public class ParticipantStatusHistoryMapper {

  private ParticipantStatusHistoryMapper() {}

  public static ParticipantEnrollmentHistoryEntity toParticipantStatusHistoryEntity(
      ParticipantRegistrySiteEntity participantRegistrySiteEntity,
      EnrollmentStatus enrollment,
      UserDetailsEntity userDetails) {
    ParticipantEnrollmentHistoryEntity participantStatusEntity =
        new ParticipantEnrollmentHistoryEntity();
    participantStatusEntity.setStatus(enrollment.getStatus());
    participantStatusEntity.setApp(participantRegistrySiteEntity.getStudy().getApp());
    participantStatusEntity.setSite(participantRegistrySiteEntity.getSite());
    participantStatusEntity.setStudy(participantRegistrySiteEntity.getStudy());
    participantStatusEntity.setParticipantRegistrySite(participantRegistrySiteEntity);
    participantStatusEntity.setUserDetails(userDetails);
    Timestamp now = new Timestamp(Instant.now().toEpochMilli());
    if (EnrollmentStatus.ENROLLED.equals(enrollment)) {
      participantStatusEntity.setEnrolledDate(now);
    }
    return participantStatusEntity;
  }
}
