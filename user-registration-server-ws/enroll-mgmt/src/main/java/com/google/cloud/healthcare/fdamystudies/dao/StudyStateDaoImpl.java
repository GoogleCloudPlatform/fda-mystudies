package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@Repository
public class StudyStateDaoImpl implements StudyStateDao {

  private static final Logger logger = LoggerFactory.getLogger(StudyStateDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public List<ParticipantStudiesBO> getParticipantStudiesList(String userId) {
    logger.info("StudyStateDaoImpl getParticipantStudiesList() - Starts ");
    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<ParticipantStudiesBO> criteriaQuery = null;
    Root<ParticipantStudiesBO> root = null;
    Predicate[] predicates = new Predicate[1];
    List<ParticipantStudiesBO> participantStudiesList = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      criteriaBuilder = session.getCriteriaBuilder();
      criteriaQuery = criteriaBuilder.createQuery(ParticipantStudiesBO.class);
      root = criteriaQuery.from(ParticipantStudiesBO.class);
      predicates[0] = criteriaBuilder.equal(root.get(AppConstants.KEY_USERID), userId);
      criteriaQuery.select(root).where(predicates);
      participantStudiesList = session.createQuery(criteriaQuery).getResultList();
    } catch (Exception e) {
      logger.error("StudyStateDaoImpl getParticipantStudiesList() - error ", e);
    }
    logger.info("StudyStateDaoImpl getParticipantStudiesList() - Ends ");
    return participantStudiesList;
  }

  @Override
  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList) {
    logger.info("StudyStateDaoImpl saveParticipantStudies() - Starts ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    boolean isUpdated = false;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      for (ParticipantStudiesBO participantStudies : participantStudiesList) {
        session.saveOrUpdate(participantStudies);
        isUpdated = true;
      }

      if (isUpdated && !participantStudiesList.isEmpty()) {
        message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("StudyStateDaoImpl saveParticipantStudies() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("StudyStateDaoImpl - saveParticipantStudies() - error rollback", e1);
        }
      }
    }
    logger.info("StudyStateDaoImpl saveParticipantStudies() - Ends ");
    return message;
  }
}
