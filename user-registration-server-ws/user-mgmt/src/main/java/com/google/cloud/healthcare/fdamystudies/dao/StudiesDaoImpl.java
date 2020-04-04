/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
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
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.LocationBo;
import com.google.cloud.healthcare.fdamystudies.model.OrgInfo;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;

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

    CriteriaQuery<OrgInfo> orgCriteria = null;
    Root<OrgInfo> orgRoot = null;
    Predicate[] orgPredicate = new Predicate[1];

    AppInfoDetailsBO appInfo = null;
    OrgInfo orgInfo = null;

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

      orgCriteria = builder.createQuery(OrgInfo.class);
      orgRoot = orgCriteria.from(OrgInfo.class);
      orgPredicate[0] = builder.equal(orgRoot.get("orgId"), studyMetadataBean.getOrgId());
      orgCriteria.select(orgRoot).where(orgPredicate);
      orgInfo = session.createQuery(orgCriteria).uniqueResult();

      if (studyInfo != null) {
        appInfo = studyInfo.getAppInfo();
        orgInfo = appInfo.getOrgInfo();

        orgInfo.setOrgId(studyMetadataBean.getOrgId());
        orgInfo.setModifiedBy(0);
        orgInfo.setModifiedDate(UserManagementUtil.getCurrentUtilDateTime());

        appInfo.setAppId(studyMetadataBean.getAppId());
        appInfo.setAppName(studyMetadataBean.getAppName());
        appInfo.setAppDescription(studyMetadataBean.getAppDescription());
        appInfo.setModifiedBy(0);
        appInfo.setModifiedDate(UserManagementUtil.getCurrentUtilDateTime());
        appInfo.setOrgInfo(orgInfo);

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

        if (orgInfo == null) {
          orgInfo = new OrgInfo();
          orgInfo.setOrgId(studyMetadataBean.getOrgId());
          orgInfo.setCreatedBy(0);
          orgInfo.setCreatedOn(UserManagementUtil.getCurrentUtilDateTime());
          session.save(orgInfo);
        }

        if (appInfo == null) {
          appInfo = new AppInfoDetailsBO();
          appInfo.setAppId(studyMetadataBean.getAppId());
          appInfo.setAppName(studyMetadataBean.getAppName());
          appInfo.setAppDescription(studyMetadataBean.getAppDescription());
          appInfo.setCreatedBy(0);
          appInfo.setCreatedOn(UserManagementUtil.getCurrentUtilDateTime());
          appInfo.setOrgInfo(orgInfo);
          session.save(appInfo);
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

  @Override
  public String withdrawFromStudy(String participantId, String studyId, boolean delete) {
    logger.info("StudiesDaoImpl withdrawFromStudy() - Ends ");
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    Transaction transaction = null;
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<StudyInfoBO> studiesBoCriteria = null;
    Root<StudyInfoBO> studiesBoRoot = null;
    Predicate[] studiesBoPredicates = new Predicate[1];
    List<StudyInfoBO> studiesBoList = null;
    StudyInfoBO studyInfo = null;

    CriteriaUpdate<ParticipantStudiesBO> criteriaUpdate = null;
    Root<ParticipantStudiesBO> participantStudiesBoRoot = null;
    List<Predicate> predicates = new ArrayList<>();
    int isUpdated = 0;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {

      transaction = session.beginTransaction();
      criteriaBuilder = session.getCriteriaBuilder();

      studiesBoCriteria = criteriaBuilder.createQuery(StudyInfoBO.class);
      studiesBoRoot = studiesBoCriteria.from(StudyInfoBO.class);
      studiesBoPredicates[0] = criteriaBuilder.equal(studiesBoRoot.get("customId"), studyId);
      studiesBoCriteria.select(studiesBoRoot).where(studiesBoPredicates);
      studiesBoList = session.createQuery(studiesBoCriteria).getResultList();
      if (!studiesBoList.isEmpty()) {
        studyInfo = studiesBoList.get(0);
        criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ParticipantStudiesBO.class);
        participantStudiesBoRoot = criteriaUpdate.from(ParticipantStudiesBO.class);
        criteriaUpdate.set("status", AppConstants.WITHDRAWN);
        //        criteriaUpdate.set("participantId", "NULL");
        predicates.add(
            criteriaBuilder.equal(participantStudiesBoRoot.get("participantId"), participantId));
        predicates.add(criteriaBuilder.equal(participantStudiesBoRoot.get("studyInfo"), studyInfo));
        criteriaUpdate.where(predicates.toArray(new Predicate[predicates.size()]));
        isUpdated = session.createQuery(criteriaUpdate).executeUpdate();
        if (isUpdated > 0) {
          message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
        }
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("StudiesDaoImpl withdrawFromStudy() - error ", e);
      if (transaction != null) {
        try {
          transaction.rollback();
        } catch (Exception e1) {
          logger.error("StudiesDaoImpl - withdrawFromStudy() - error rollback", e1);
        }
      }
    }
    logger.info("StudiesDaoImpl withdrawFromStudy() - Ends ");
    return message;
  }
}
