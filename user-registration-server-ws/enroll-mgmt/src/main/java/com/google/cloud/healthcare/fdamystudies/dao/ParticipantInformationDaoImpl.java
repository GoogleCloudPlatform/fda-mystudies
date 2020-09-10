/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantInfoRespBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantInformationDaoImpl implements ParticipantInformationDao {

  private static final Logger logger = LoggerFactory.getLogger(ParticipantInformationDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public ParticipantInfoRespBean getParticipantInfoDetails(String particpinatId, Integer studyId) {
    logger.info("ParticipantInformationDaoImpl getParticipantDetails() - starts ");
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<ParticipantStudiesBO> participantBoCriteria = null;
    Root<ParticipantStudiesBO> participantBoRoot = null;
    Predicate[] predicates = new Predicate[2];
    List<ParticipantStudiesBO> participantBoList = null;
    ParticipantStudiesBO participantBo = null;
    ParticipantInfoRespBean participantRespBean = null;

    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      participantBoCriteria = criteriaBuilder.createQuery(ParticipantStudiesBO.class);
      participantBoRoot = participantBoCriteria.from(ParticipantStudiesBO.class);
      StudyInfoBO studyInfo = session.get(StudyInfoBO.class, studyId);
      predicates[0] = criteriaBuilder.equal(participantBoRoot.get("participantId"), particpinatId);
      predicates[1] = criteriaBuilder.equal(participantBoRoot.get("studyInfo"), studyInfo);
      participantBoCriteria.select(participantBoRoot).where(predicates);
      participantBoList = session.createQuery(participantBoCriteria).getResultList();
      if (!participantBoList.isEmpty()) {
        participantBo = participantBoList.get(0);
        if (participantBo != null) {
          participantRespBean = new ParticipantInfoRespBean();
          if (participantBo.getSharing() != null) {
            participantRespBean.setSharing(participantBo.getSharing());
          }
          if (participantBo.getEnrolledDate().toString() != null) {
            participantRespBean.setEnrollment(participantBo.getEnrolledDate().toString());
          }
          if (participantBo.getWithdrawalDate() != null) {
            if (participantBo.getWithdrawalDate().toString() != null) {
              participantRespBean.setWithdrawal(participantBo.getWithdrawalDate().toString());
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("ParticipantInformationDaoImpl getParticipantDetails() - error ", e);
    }
    logger.info("ParticipantInformationDaoImpl getParticipantDetails() - ends ");
    return participantRespBean;
  }
}
