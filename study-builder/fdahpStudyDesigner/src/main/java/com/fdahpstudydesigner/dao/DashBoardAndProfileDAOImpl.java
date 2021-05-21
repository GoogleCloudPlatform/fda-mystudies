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

import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashBoardAndProfileDAOImpl implements DashBoardAndProfileDAO {

  private static XLogger logger =
      XLoggerFactory.getXLogger(DashBoardAndProfileDAOImpl.class.getName());

  HibernateTemplate hibernateTemplate;

  private Transaction transaction = null;

  @Override
  public MasterDataBO getMasterData(String type) {
    logger.entry("begin getMasterData()");
    Session session = null;
    MasterDataBO masterDataBO = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getMasterDataByType").setString("type", type);
      masterDataBO = (MasterDataBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("DashBoardAndProfileDAOImpl - getMasterData() - ERROR", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getMasterData() - Ends");
    return masterDataBO;
  }

  @Override
  public String isEmailValid(String email) {
    logger.entry("begin isEmailValid()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    String queryString = null;
    Query query = null;
    UserBO user = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      queryString = "FROM UserBO where userEmail = :email";
      query = session.createQuery(queryString).setParameter("email", email);
      user = (UserBO) query.uniqueResult();
      if (null != user) {
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
    } catch (Exception e) {
      logger.error("DashBoardAndProfileDAOImpl - isEmailValid() - ERROR " + e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("isEmailValid() - Ends");
    return message;
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Override
  public String updateProfileDetails(UserBO userBO, String userId) {
    logger.info("DashBoardAndProfileDAOImpl - updateProfileDetails() - Starts");

    Session session = null;
    Query query = null;
    String queryString = "";
    String message = FdahpStudyDesignerConstants.FAILURE;
    UserBO updatedUserBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      /*-------------------------Update FDA Admin-----------------------*/
      queryString = "from UserBO UBO where UBO.userId = :userId";
      query = session.createQuery(queryString).setParameter("userId", userId);
      updatedUserBo = (UserBO) query.uniqueResult();
      if (updatedUserBo != null) {
        updatedUserBo.setFirstName(
            null != userBO.getFirstName().trim() ? userBO.getFirstName().trim() : "");
        updatedUserBo.setLastName(
            null != userBO.getLastName().trim() ? userBO.getLastName().trim() : "");
        updatedUserBo.setUserEmail(
            null != userBO.getUserEmail().trim() ? userBO.getUserEmail().trim() : "");
        updatedUserBo.setPhoneNumber(
            null != userBO.getPhoneNumber().trim() ? userBO.getPhoneNumber().trim() : "");
        updatedUserBo.setModifiedBy(null != userBO.getModifiedBy() ? userBO.getModifiedBy() : null);
        updatedUserBo.setModifiedOn(null != userBO.getModifiedOn() ? userBO.getModifiedOn() : "");
        session.update(updatedUserBo);
      }
      transaction.commit();
      message = FdahpStudyDesignerConstants.SUCCESS;
    } catch (Exception e) {
      transaction.rollback();
      logger.error("DashBoardAndProfileDAOImpl - updateProfileDetails - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("updateProfileDetails - Ends");
    return message;
  }
}
