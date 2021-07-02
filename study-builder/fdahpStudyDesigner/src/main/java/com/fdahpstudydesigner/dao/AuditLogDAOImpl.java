/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
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
