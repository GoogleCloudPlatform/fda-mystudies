/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditLogDAOImpl implements AuditLogDAO {

  private static XLogger logger = XLoggerFactory.getXLogger(AuditLogDAOImpl.class.getName());

  HibernateTemplate hibernateTemplate;

  @Override
  public String updateDraftToEditedStatus(
      Session session, Transaction transaction, String userId, String actionType, String studyId) {
    logger.info("AuditLogDAOImpl - updateDraftToEditedStatus() - Starts");

    String message = FdahpStudyDesignerConstants.FAILURE;
    Session newSession = null;
    String queryString;
    String draftColumn = null;
    try {
      if (session == null) {
        newSession = hibernateTemplate.getSessionFactory().openSession();
        transaction = newSession.beginTransaction();
      }
      if ((userId != null) && (studyId != null)) {
        if ((actionType != null) && (FdahpStudyDesignerConstants.DRAFT_STUDY).equals(actionType)) {
          draftColumn = "hasStudyDraft = 1";
        } else if ((actionType != null)
            && (FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE).equals(actionType)) {
          draftColumn = "hasQuestionnaireDraft = 1, hasStudyDraft = 1 ";
        } else if ((actionType != null)
            && (FdahpStudyDesignerConstants.DRAFT_ACTIVETASK).equals(actionType)) {
          draftColumn = "hasActivetaskDraft = 1, hasStudyDraft = 1 ";
        } else if ((actionType != null)
            && (FdahpStudyDesignerConstants.DRAFT_CONSCENT).equals(actionType)) {
          draftColumn =
              "hasConsentDraft = 1, hasActivetaskDraft = 1, hasQuestionnaireDraft=1, hasStudyDraft = 1";
        }
        queryString =
            "Update StudyBo set "
                + draftColumn
                + " , modifiedBy = :userId"
                + " , modifiedOn = now() where id = :studyId";
        if (newSession != null) {
          newSession
              .createQuery(queryString)
              .setParameter("userId", userId)
              .setParameter("studyId", studyId)
              .executeUpdate();
        } else {
          session
              .createQuery(queryString)
              .setParameter("userId", userId)
              .setParameter("studyId", studyId)
              .executeUpdate();
        }
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
      if (session == null) {
        transaction.commit();
      }

    } catch (Exception e) {
      if ((session == null) && (null != transaction)) {
        transaction.rollback();
      }
      logger.error("AuditLogDAOImpl - updateDraftToEditedStatus - ERROR", e);
    } finally {
      if (null != newSession) {
        newSession.close();
      }
    }
    logger.exit("updateDraftToEditedStatus - Ends");
    return message;
  }
}
