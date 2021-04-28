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
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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
public class StudiesDaoImpl implements StudiesDao {

  private static final XLogger logger = XLoggerFactory.getXLogger(StudiesDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Override
  public ErrorBean saveStudyMetadata(StudyMetadataBean studyMetadataBean) {
    logger.entry("Begin saveStudyMetadata()");
    CriteriaBuilder builder = null;
    CriteriaQuery<StudyEntity> studyCriteria = null;
    Root<StudyEntity> studyRoot = null;
    Predicate[] studyPredicate = new Predicate[1];
    StudyEntity studyInfo = null;

    CriteriaQuery<AppEntity> appCriteria = null;
    Root<AppEntity> appRoot = null;
    Predicate[] appPredicate = new Predicate[1];

    AppEntity appInfo = null;

    ErrorBean errorBean = null;
    Session session = this.sessionFactory.getCurrentSession();
    builder = session.getCriteriaBuilder();
    studyCriteria = builder.createQuery(StudyEntity.class);
    studyRoot = studyCriteria.from(StudyEntity.class);
    studyPredicate[0] = builder.equal(studyRoot.get("customId"), studyMetadataBean.getStudyId());
    studyCriteria.select(studyRoot).where(studyPredicate);
    studyInfo = session.createQuery(studyCriteria).uniqueResult();

    appCriteria = builder.createQuery(AppEntity.class);
    appRoot = appCriteria.from(AppEntity.class);
    appPredicate[0] = builder.equal(appRoot.get("appId"), studyMetadataBean.getAppId());
    appCriteria.select(appRoot).where(appPredicate);
    appInfo = session.createQuery(appCriteria).uniqueResult();

    if (studyInfo != null) {

      appInfo.setAppId(studyMetadataBean.getAppId());
      appInfo.setAppName(studyMetadataBean.getAppName());
      appInfo.setAppDescription(studyMetadataBean.getAppDescription());
      appInfo.setModifiedBy(String.valueOf(0));
      appInfo.setModified(Timestamp.from(Instant.now()));

      studyInfo.setCustomId(studyMetadataBean.getStudyId());
      studyInfo.setName(studyMetadataBean.getStudyTitle());
      studyInfo.setVersion(Float.valueOf(studyMetadataBean.getStudyVersion()));
      studyInfo.setType(studyMetadataBean.getStudyType());
      studyInfo.setStatus(studyMetadataBean.getStudyStatus());
      studyInfo.setCategory(studyMetadataBean.getStudyCategory());
      studyInfo.setTagline(studyMetadataBean.getStudyTagline());
      studyInfo.setSponsor(studyMetadataBean.getStudySponsor());
      studyInfo.setEnrolling(studyMetadataBean.getStudyEnrolling());
      studyInfo.setApp(appInfo);
      studyInfo.setModifiedBy(String.valueOf(0));
      studyInfo.setModified(Timestamp.from(Instant.now()));
      studyInfo.setLogoImageUrl(studyMetadataBean.getLogoImageUrl());
      studyInfo.setContactEmail(studyMetadataBean.getContactEmail());
      session.update(studyInfo);
    } else {
      List<AppPermissionEntity> appPermissionList = new ArrayList<>();
      if (appInfo == null) {
        appInfo = new AppEntity();
        appInfo.setAppId(studyMetadataBean.getAppId());
        appInfo.setAppName(studyMetadataBean.getAppName());
        appInfo.setAppDescription(studyMetadataBean.getAppDescription());
        appInfo.setCreatedBy(String.valueOf(0));
        appInfo.setCreated(Timestamp.from(Instant.now()));
        session.save(appInfo);
      } else {
        CriteriaQuery<AppPermissionEntity> appPermissionCriteria =
            builder.createQuery(AppPermissionEntity.class);
        Root<AppPermissionEntity> appPermissionRoot =
            appPermissionCriteria.from(AppPermissionEntity.class);
        Predicate[] appPermissionPredicate = new Predicate[1];
        appPermissionPredicate[0] = builder.equal(appPermissionRoot.get("app"), appInfo);
        appPermissionCriteria.select(appPermissionRoot).where(appPermissionPredicate);
        appPermissionList = session.createQuery(appPermissionCriteria).getResultList();
      }

      studyInfo = new StudyEntity();
      studyInfo.setCustomId(studyMetadataBean.getStudyId());
      studyInfo.setName(studyMetadataBean.getStudyTitle());
      studyInfo.setVersion(Float.valueOf(studyMetadataBean.getStudyVersion()));
      studyInfo.setType(studyMetadataBean.getStudyType());
      studyInfo.setStatus(studyMetadataBean.getStudyStatus());
      studyInfo.setCategory(studyMetadataBean.getStudyCategory());
      studyInfo.setTagline(studyMetadataBean.getStudyTagline());
      studyInfo.setSponsor(studyMetadataBean.getStudySponsor());
      studyInfo.setEnrolling(studyMetadataBean.getStudyEnrolling());
      studyInfo.setApp(appInfo);
      studyInfo.setCreatedBy(String.valueOf(0));
      studyInfo.setCreated(Timestamp.from(Instant.now()));
      studyInfo.setLogoImageUrl(studyMetadataBean.getLogoImageUrl());
      studyInfo.setContactEmail(studyMetadataBean.getContactEmail());
      String generatedStudyid = (String) session.save(studyInfo);

      for (AppPermissionEntity appPermission : appPermissionList) {
        StudyPermissionEntity studyPermission = new StudyPermissionEntity();
        studyPermission.setApp(appInfo);
        studyPermission.setStudy(studyInfo);
        studyPermission.setUrAdminUser(appPermission.getUrAdminUser());
        studyPermission.setEdit(appPermission.getEdit());
        studyPermission.setCreated(Timestamp.from(Instant.now()));
        studyPermission.setCreatedBy(appPermission.getCreatedBy());
        session.save(studyPermission);
      }

      if (!StringUtils.isBlank(studyMetadataBean.getStudyType())
          && studyMetadataBean.getStudyType().equals(AppConstants.OPEN_STUDY)) {
        LocationEntity defaultLocation =
            (LocationEntity)
                session.createQuery("from LocationEntity where isDefault='Y'").getSingleResult();
        if (defaultLocation != null) {
          StudyEntity studyInfoCreated = session.get(StudyEntity.class, generatedStudyid);
          SiteEntity site = new SiteEntity();
          site.setStudy(studyInfoCreated);
          site.setLocation(defaultLocation);
          site.setCreatedBy(String.valueOf(0));
          site.setStatus(1);
          site.setTargetEnrollment(0);
          session.save(site);

          for (AppPermissionEntity appPermission : appPermissionList) {
            SitePermissionEntity sitePermission = new SitePermissionEntity();
            sitePermission.setApp(appInfo);
            sitePermission.setStudy(studyInfo);
            sitePermission.setSite(site);
            sitePermission.setUrAdminUser(appPermission.getUrAdminUser());
            sitePermission.setCanEdit(appPermission.getEdit());
            sitePermission.setCreated(Timestamp.from(Instant.now()));
            sitePermission.setCreatedBy(appPermission.getCreatedBy());
            session.save(sitePermission);
          }
        }
      }
    }

    errorBean = new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage());
    logger.exit("saveStudyMetadata() : ends");
    return errorBean;
  }
}
