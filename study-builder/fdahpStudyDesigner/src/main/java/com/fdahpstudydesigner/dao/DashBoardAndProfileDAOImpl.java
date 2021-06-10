/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
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
    logger.entry("begin updateProfileDetails()");
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
