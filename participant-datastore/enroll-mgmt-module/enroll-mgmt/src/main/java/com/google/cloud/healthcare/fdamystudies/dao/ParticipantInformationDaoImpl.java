/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantInfoRespBean;
import com.google.cloud.healthcare.fdamystudies.common.DataSharingStatus;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantInformationDaoImpl implements ParticipantInformationDao {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(ParticipantInformationDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Override
  public ParticipantInfoRespBean getParticipantInfoDetails(String particpinatId, String studyId) {
    logger.entry("Begin getParticipantDetails()");
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<ParticipantStudyEntity> participantStudyCriteria = null;
    Root<ParticipantStudyEntity> participantStudyRoot = null;
    Predicate[] predicates = new Predicate[2];
    List<ParticipantStudyEntity> participantStudyList = null;
    ParticipantStudyEntity participantEntity = null;
    ParticipantInfoRespBean participantRespBean = null;

    Session session = this.sessionFactory.getCurrentSession();
    criteriaBuilder = session.getCriteriaBuilder();
    participantStudyCriteria = criteriaBuilder.createQuery(ParticipantStudyEntity.class);
    participantStudyRoot = participantStudyCriteria.from(ParticipantStudyEntity.class);
    StudyEntity studyInfo = session.get(StudyEntity.class, studyId);
    predicates[0] = criteriaBuilder.equal(participantStudyRoot.get("participantId"), particpinatId);
    predicates[1] = criteriaBuilder.equal(participantStudyRoot.get("study"), studyInfo);
    participantStudyCriteria.select(participantStudyRoot).where(predicates);
    participantStudyList = session.createQuery(participantStudyCriteria).getResultList();
    if (!participantStudyList.isEmpty()) {
      participantEntity = participantStudyList.get(0);
      if (participantEntity != null) {
        participantRespBean = new ParticipantInfoRespBean();

        String sharing =
            StringUtils.defaultIfEmpty(
                participantEntity.getSharing(), DataSharingStatus.UNDEFINED.value());
        participantRespBean.setSharing(sharing);

        if (participantEntity.getEnrolledDate().toString() != null) {
          participantRespBean.setEnrollment(participantEntity.getEnrolledDate().toString());
        }
        if (participantEntity.getWithdrawalDate() != null) {
          if (participantEntity.getWithdrawalDate().toString() != null) {
            participantRespBean.setWithdrawal(participantEntity.getWithdrawalDate().toString());
          }
        }
      }
    }

    logger.exit("getParticipantDetails() - ends ");
    return participantRespBean;
  }
}
