/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AppPermission;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.LocationBo;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.StudyPermission;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserRegAdminUser;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.Permission;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class StudiesDaoImpl implements StudiesDao {

  private static final Logger logger = LoggerFactory.getLogger(StudiesDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Override
  public ErrorBean saveStudyMetadata(StudyMetadataBean studyMetadataBean) {
    logger.info("StudiesDaoImpl - saveStudyMetadata() : Starts");
    Transaction transaction = null;
    CriteriaBuilder builder = null;
    CriteriaQuery<StudyInfoBO> studyCriteria = null;
    Root<StudyInfoBO> studyRoot = null;
    Predicate[] studyPredicate = new Predicate[1];
    StudyInfoBO studyInfo = null;

    CriteriaQuery<AppInfoDetailsBO> appCriteria = null;
    Root<AppInfoDetailsBO> appRoot = null;
    Predicate[] appPredicate = new Predicate[1];

    AppInfoDetailsBO appInfo = null;

    UserRegAdminUser superAdminUser;

    ErrorBean errorBean = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      transaction = session.beginTransaction();
      builder = session.getCriteriaBuilder();
      studyCriteria = builder.createQuery(StudyInfoBO.class);
      studyRoot = studyCriteria.from(StudyInfoBO.class);
      studyPredicate[0] = builder.equal(studyRoot.get("customId"), studyMetadataBean.getStudyId());
      studyCriteria.select(studyRoot).where(studyPredicate);
      studyInfo = session.createQuery(studyCriteria).uniqueResult();

      appCriteria = builder.createQuery(AppInfoDetailsBO.class);
      appRoot = appCriteria.from(AppInfoDetailsBO.class);
      appPredicate[0] = builder.equal(appRoot.get("appId"), studyMetadataBean.getAppId());
      appCriteria.select(appRoot).where(appPredicate);
      appInfo = session.createQuery(appCriteria).uniqueResult();

      CriteriaQuery<UserRegAdminUser> urAdminUserCriteria =
          builder.createQuery(UserRegAdminUser.class);
      Root<UserRegAdminUser> urAdminUserRoot = urAdminUserCriteria.from(UserRegAdminUser.class);
      Predicate[] urAdminUserPredicate = new Predicate[1];
      urAdminUserPredicate[0] = builder.equal(urAdminUserRoot.get("superAdmin"), true);
      urAdminUserCriteria.select(urAdminUserRoot).where(urAdminUserPredicate);
      superAdminUser = session.createQuery(urAdminUserCriteria).uniqueResult();

      if (studyInfo != null) {
        appInfo = studyInfo.getAppInfo();

        appInfo.setAppId(studyMetadataBean.getAppId());
        appInfo.setAppName(studyMetadataBean.getAppName());
        appInfo.setAppDescription(studyMetadataBean.getAppDescription());
        appInfo.setModifiedBy(0);
        appInfo.setModifiedDate(UserManagementUtil.getCurrentUtilDateTime());

        studyInfo.setCustomId(studyMetadataBean.getStudyId());
        studyInfo.setName(studyMetadataBean.getStudyTitle());
        studyInfo.setVersion(Float.valueOf(studyMetadataBean.getStudyVersion()));
        studyInfo.setType(studyMetadataBean.getStudyType());
        studyInfo.setStatus(studyMetadataBean.getStudyStatus());
        studyInfo.setCategory(studyMetadataBean.getStudyCategory());
        studyInfo.setTagline(studyMetadataBean.getStudyTagline());
        studyInfo.setSponsor(studyMetadataBean.getStudySponsor());
        studyInfo.setEnrolling(studyMetadataBean.getStudyEnrolling());
        studyInfo.setAppInfo(appInfo);
        studyInfo.setModifiedBy(0);
        studyInfo.setModifiedDate(UserManagementUtil.getCurrentUtilDateTime());
        session.update(studyInfo);
        if (studyInfo.getStatus().equalsIgnoreCase("Deactivated")) {
          decommisionSiteFromStudy(session, studyInfo.getId());
        }
      } else {

        if (appInfo == null) {
          appInfo = new AppInfoDetailsBO();
          appInfo.setAppId(studyMetadataBean.getAppId());
          appInfo.setAppName(studyMetadataBean.getAppName());
          appInfo.setAppDescription(studyMetadataBean.getAppDescription());
          appInfo.setCreatedBy(0);
          appInfo.setCreatedOn(UserManagementUtil.getCurrentUtilDateTime());
          session.save(appInfo);

          AppPermission appPermission = new AppPermission();
          appPermission.setAppInfo(appInfo);
          appPermission.setUrAdminUser(superAdminUser);
          appPermission.setEdit(Permission.READ_EDIT.value());
          appPermission.setCreated(UserManagementUtil.getCurrentUtilDateTime());
          appPermission.setCreatedBy(superAdminUser.getId());
          session.save(appPermission);
        }

        studyInfo = new StudyInfoBO();
        studyInfo.setCustomId(studyMetadataBean.getStudyId());
        studyInfo.setName(studyMetadataBean.getStudyTitle());
        studyInfo.setVersion(Float.valueOf(studyMetadataBean.getStudyVersion()));
        studyInfo.setType(studyMetadataBean.getStudyType());
        studyInfo.setStatus(studyMetadataBean.getStudyStatus());
        studyInfo.setCategory(studyMetadataBean.getStudyCategory());
        studyInfo.setTagline(studyMetadataBean.getStudyTagline());
        studyInfo.setSponsor(studyMetadataBean.getStudySponsor());
        studyInfo.setEnrolling(studyMetadataBean.getStudyEnrolling());
        studyInfo.setAppInfo(appInfo);
        studyInfo.setCreatedBy(0);
        studyInfo.setCreatedOn(UserManagementUtil.getCurrentUtilDateTime());
        int generatedStudyid = (int) session.save(studyInfo);

        StudyPermission studyPermission = new StudyPermission();
        studyPermission.setAppInfo(appInfo);
        studyPermission.setStudyInfo(studyInfo);
        studyPermission.setUrAdminUser(superAdminUser);
        studyPermission.setEdit(Permission.READ_EDIT.value());
        studyPermission.setCreated(UserManagementUtil.getCurrentUtilDateTime());
        studyPermission.setCreatedBy(superAdminUser.getId());
        session.save(studyPermission);

        if (!StringUtils.isBlank(studyMetadataBean.getStudyType())
            && studyMetadataBean.getStudyType().equals(AppConstants.OPEN_STUDY)) {
          LocationBo defaultLocation =
              (LocationBo)
                  session.createQuery("from LocationBo where isdefault='Y'").getSingleResult();
          if (defaultLocation != null) {
            StudyInfoBO studyInfoCreated = session.get(StudyInfoBO.class, generatedStudyid);
            SiteBo siteBO = new SiteBo();
            siteBO.setStudyInfo(studyInfoCreated);
            siteBO.setLocations(defaultLocation);
            siteBO.setCreatedBy(0);
            siteBO.setStatus(1);
            siteBO.setTargetEnrollment(0);
            session.save(siteBO);
          }
        }
      }
      errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
      transaction.commit();
    } catch (Exception e) {
      logger.error("StudiesDaoImpl - saveStudyMetadata() : error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("StudiesDaoImpl - saveStudyMetadata() rollback- error", e1);
        }
      }
      return new ErrorBean(ErrorCode.EC_500.code(), ErrorCode.EC_500.errorMessage());
    }
    logger.info("StudiesDaoImpl - saveStudyMetadata() : ends");
    return errorBean;
  }

  private void decommisionSiteFromStudy(Session session, Integer studyId) {
    logger.info("StudiesDaoImpl - decommisionSiteFromStudy() : Starts");
    CriteriaBuilder builder = null;
    CriteriaQuery<SiteBo> siteCriteria = null;
    Root<SiteBo> siteRoot = null;
    Predicate[] sitePredicate = new Predicate[1];
    List<SiteBo> siteList = null;
    try {
      builder = session.getCriteriaBuilder();
      siteCriteria = builder.createQuery(SiteBo.class);
      siteRoot = siteCriteria.from(SiteBo.class);
      sitePredicate[0] = builder.equal(siteRoot.get("studyInfo"), studyId);
      siteCriteria.select(siteRoot).where(sitePredicate);
      siteList = session.createQuery(siteCriteria).getResultList();
      if (!siteList.isEmpty()) {
        for (SiteBo site : siteList) {
          site.setStatus(0);
          site.setModifiedBy(0);
          site.setModifiedDate(UserManagementUtil.getCurrentUtilDateTime());
          session.update(site);
        }
      }
    } catch (Exception e) {
      logger.error("StudiesDaoImpl - decommisionSiteFromStudy() : error ", e);
    }
    logger.info("StudiesDaoImpl - decommisionSiteFromStudy() : ends");
  }
}
