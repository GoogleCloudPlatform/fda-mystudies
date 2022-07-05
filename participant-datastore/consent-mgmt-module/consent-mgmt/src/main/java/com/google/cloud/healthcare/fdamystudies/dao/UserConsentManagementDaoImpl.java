/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CONSENT_DATE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CONSENT_TYPE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DATA_SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.ENROLLED;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.IMAGE_PATH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PARTICIPANT_STUDY_ID;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PDF_PATH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PRIMARY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.SITE_ID;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.STUDY_ID;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.VERSION;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.collections4.map.HashedMap;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.google.api.services.healthcare.v1.model.Consent;
import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.common.DataSharingStatus;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentManagementAPIs;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

@Transactional
@Repository
public class UserConsentManagementDaoImpl implements UserConsentManagementDao {

  private XLogger logger = XLoggerFactory.getXLogger(UserConsentManagementDaoImpl.class.getName());

  @Autowired private SessionFactory sessionFactory;

  @Autowired StudyRepository studyRepository;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired ConsentManagementAPIs consentApis;

  @Autowired SiteRepository siteRepository;
  
  HibernateTemplate hibernateTemplate;

  @Override
  public ParticipantStudyEntity getParticipantStudies(String studyId, String userId) {
    logger.entry("Begin getParticipantStudies() ");

    Session session = this.sessionFactory.getCurrentSession();

    ParticipantStudyEntity participantStudiesEntity = null;
    CriteriaBuilder criteriaBuilder = null;

    CriteriaQuery<StudyEntity> studiesBoCriteria = null;
    Root<StudyEntity> studiesBoRoot = null;
    Predicate[] studiesBoPredicates = new Predicate[1];

    StudyEntity studyInfo = null;

    Predicate[] predicates = new Predicate[2];
    List<ParticipantStudyEntity> participantStudiesBoList = null;

    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;

    criteriaBuilder = session.getCriteriaBuilder();

    studiesBoCriteria = criteriaBuilder.createQuery(StudyEntity.class);
    studiesBoRoot = studiesBoCriteria.from(StudyEntity.class);
    studiesBoPredicates[0] = criteriaBuilder.equal(studiesBoRoot.get("id"), studyId);
    studiesBoCriteria.select(studiesBoRoot).where(studiesBoPredicates);
    List<StudyEntity> studiesBoList = session.createQuery(studiesBoCriteria).getResultList();
    CriteriaQuery<ParticipantStudyEntity> participantStudiesBoCriteria =
        criteriaBuilder.createQuery(ParticipantStudyEntity.class);
    Root<ParticipantStudyEntity> participantStudiesBoRoot =
        participantStudiesBoCriteria.from(ParticipantStudyEntity.class);

    userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
    userDetailspredicates[0] =
        criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
    userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
    userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();

    if (!userDetailsBoList.isEmpty() && !studiesBoList.isEmpty()) {
      userDetailsEntity = userDetailsBoList.get(0);
      studyInfo = studiesBoList.get(0);
      predicates[0] =
          criteriaBuilder.equal(participantStudiesBoRoot.get("userDetails"), userDetailsEntity);
      predicates[1] = criteriaBuilder.equal(participantStudiesBoRoot.get("study"), studyInfo);
      participantStudiesBoCriteria.select(participantStudiesBoRoot).where(predicates);
      participantStudiesBoList = session.createQuery(participantStudiesBoCriteria).getResultList();

      if (!participantStudiesBoList.isEmpty()) {
        participantStudiesEntity = participantStudiesBoList.get(0);
      }
    }
    logger.exit("getParticipantStudies() - Ends ");
    return participantStudiesEntity;
  }

  @Override
  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList) {
    logger.entry("Begin saveParticipantStudies()");

    Session session = this.sessionFactory.getCurrentSession();

    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    CriteriaBuilder criteriaBuilder = null;
    CriteriaUpdate<ParticipantStudyEntity> criteriaUpdate = null;
    Integer isSaved = 0;
    int isUpdated = 0;

    for (ParticipantStudyEntity participantStudies : participantStudiesList) {
      if (participantStudies.getStudy() != null) {
        criteriaBuilder = session.getCriteriaBuilder();
        criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ParticipantStudyEntity.class);
        Root<ParticipantStudyEntity> participantStudiesBoRoot =
            criteriaUpdate.from(ParticipantStudyEntity.class);
        criteriaUpdate.set("eligibility", participantStudies.getEligibility());
        criteriaUpdate.set("sharing", participantStudies.getSharing());
        criteriaUpdate.set("bookmark", participantStudies.getBookmark());
        criteriaUpdate.set("consentStatus", participantStudies.getConsentStatus());
        criteriaUpdate.set("completion", participantStudies.getCompletion());
        criteriaUpdate.set("adherence", participantStudies.getAdherence());
        criteriaUpdate.where(
            criteriaBuilder.equal(participantStudiesBoRoot.get("id"), participantStudies.getId()));
        isUpdated = session.createQuery(criteriaUpdate).executeUpdate();

      } else {
        isSaved = (Integer) session.save(participantStudies);
      }
    }

    if ((isUpdated > 0) || (isSaved > 0)) {
      message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
    }
    logger.exit("saveParticipantStudies() - Ends ");
    return message;
  }

  @Override
  public StudyConsentEntity getStudyConsent(String userId, String studyId, String consentVersion) {
    logger.entry("Begin getStudyConsent()");
    StudyConsentEntity studyConsent = null;

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyConsentEntity> criteriaQuery = null;
    Predicate[] predicates;
    List<StudyConsentEntity> studyConsentBoList = null;
    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;

    Optional<StudyEntity> optStudy = studyRepository.findById(studyId);

    criteriaBuilder = session.getCriteriaBuilder();
    userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
    userDetailspredicates[0] =
        criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
    userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
    userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetailsEntity = userDetailsBoList.get(0);
    }

    criteriaQuery = criteriaBuilder.createQuery(StudyConsentEntity.class);
    Root<StudyConsentEntity> studyConsentBoRoot = criteriaQuery.from(StudyConsentEntity.class);
    if ((consentVersion != null) && !StringUtils.isEmpty(consentVersion)) {
      predicates = new Predicate[3];
      predicates[0] =
          criteriaBuilder.equal(studyConsentBoRoot.get("userDetails"), userDetailsEntity);
      if (optStudy.isPresent()) {
        predicates[1] = criteriaBuilder.equal(studyConsentBoRoot.get("study"), optStudy.get());
      }
      predicates[2] = criteriaBuilder.equal(studyConsentBoRoot.get("version"), consentVersion);
    } else {
      predicates = new Predicate[2];
      predicates[0] =
          criteriaBuilder.equal(studyConsentBoRoot.get("userDetails"), userDetailsEntity);
      if (optStudy.isPresent()) {
        predicates[1] = criteriaBuilder.equal(studyConsentBoRoot.get("study"), optStudy.get());
      }
    }
    criteriaQuery.select(studyConsentBoRoot).where(predicates);
    if ((consentVersion != null) && !StringUtils.isEmpty(consentVersion)) {
      studyConsentBoList = session.createQuery(criteriaQuery).getResultList();
    } else {
      criteriaQuery.orderBy(criteriaBuilder.desc(studyConsentBoRoot.get("created")));
      studyConsentBoList = session.createQuery(criteriaQuery).setMaxResults(1).getResultList();
    }
    if (!studyConsentBoList.isEmpty()) {
      studyConsent = studyConsentBoList.get(0);
    }

    logger.exit("getStudyConsent() - Ends ");
    return studyConsent;
  }

  @Override
  public String saveStudyConsent(
      StudyConsentEntity studyConsent,
      ParticipantStudyEntity participantStudyEntity,
      String filePath,
      String dataSharingPath) {
    logger.entry("Begin saveStudyConsent()");

    Session session = this.sessionFactory.getCurrentSession();

    String addConsentMessage = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    String isSaved = "";

    if (null != studyConsent) {
      studyConsent.setCreated(Timestamp.from(Instant.now()));
      String flag = appConfig.getEnableConsentManagementAPI();

      if (!StringUtils.isEmpty(flag) && Boolean.valueOf(flag) && filePath != null) {
        isSaved =
            saveConsentDetailsInConsentStore(
                studyConsent, participantStudyEntity, filePath, dataSharingPath);
      } else {
        isSaved = (String) session.save(studyConsent);
      }
    }
    if (!StringUtils.isEmpty(isSaved)) {
      addConsentMessage = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
    }
    logger.exit("saveStudyConsent() - Ends ");
    return addConsentMessage;
  }

  @Override
  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId) {
    logger.entry("Begin validatedUserAppDetailsByAllApi()");

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<AppEntity> appDetailsBoCriteria = null;
    Root<AppEntity> appDetailsBoRoot = null;
    Predicate[] appDetailsPredicates = new Predicate[1];
    List<AppEntity> appDetailsList = null;
    AppEntity appEntity = null;

    AppOrgInfoBean appOrgInfoBean = new AppOrgInfoBean();
    String appInfoId = String.valueOf(0);

    criteriaBuilder = session.getCriteriaBuilder();
    if (!StringUtils.isEmpty(appId)) {
      appDetailsBoCriteria = criteriaBuilder.createQuery(AppEntity.class);
      appDetailsBoRoot = appDetailsBoCriteria.from(AppEntity.class);
      appDetailsPredicates[0] = criteriaBuilder.equal(appDetailsBoRoot.get("appId"), appId);
      appDetailsBoCriteria.select(appDetailsBoRoot).where(appDetailsPredicates);
      appDetailsList = session.createQuery(appDetailsBoCriteria).getResultList();
      if (!appDetailsList.isEmpty()) {
        appEntity = appDetailsList.get(0);
        appInfoId = appEntity.getId();
      }
    }

    appOrgInfoBean.setAppInfoId(appInfoId);

    logger.exit("getUserAppDetailsByAllApi() - Ends ");
    return appOrgInfoBean;
  }

  @Override
  public StudyEntity getStudyInfo(String customStudyId) {
    logger.entry("Begin getStudyInfo()");

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<StudyEntity> studyInfoBoCriteria = null;
    Root<StudyEntity> studyInfoBoRoot = null;
    Predicate[] studyInfoPredicates = new Predicate[1];
    List<StudyEntity> studyInfoList = null;
    StudyEntity studyEntity = null;
    criteriaBuilder = session.getCriteriaBuilder();
    if (!StringUtils.isEmpty(customStudyId)) {
      studyInfoBoCriteria = criteriaBuilder.createQuery(StudyEntity.class);
      studyInfoBoRoot = studyInfoBoCriteria.from(StudyEntity.class);
      studyInfoPredicates[0] =
          criteriaBuilder.equal(studyInfoBoRoot.get("customId"), customStudyId);
      studyInfoBoCriteria.select(studyInfoBoRoot).where(studyInfoPredicates);
      studyInfoList = session.createQuery(studyInfoBoCriteria).getResultList();
      if (!studyInfoList.isEmpty()) {
        studyEntity = studyInfoList.get(0);
      }
    }
    logger.exit("getStudyInfo() - Ends ");
    return studyEntity;
  }

  @Override
  public String getUserDetailsId(String userId) {
    logger.entry("Begin getUserDetailsId()");

    Session session = this.sessionFactory.getCurrentSession();

    CriteriaBuilder criteriaBuilder = null;
    CriteriaQuery<UserDetailsEntity> userDetailsBoCriteria = null;
    Root<UserDetailsEntity> userDetailsBoRoot = null;
    List<UserDetailsEntity> userDetailsBoList = null;
    Predicate[] userDetailspredicates = new Predicate[1];
    UserDetailsEntity userDetailsEntity = null;
    String userDetailsId = String.valueOf(0);
    criteriaBuilder = session.getCriteriaBuilder();

    userDetailsBoCriteria = criteriaBuilder.createQuery(UserDetailsEntity.class);
    userDetailsBoRoot = userDetailsBoCriteria.from(UserDetailsEntity.class);
    userDetailspredicates[0] =
        criteriaBuilder.equal(userDetailsBoRoot.get(AppConstants.KEY_USERID), userId);
    userDetailsBoCriteria.select(userDetailsBoRoot).where(userDetailspredicates);
    userDetailsBoList = session.createQuery(userDetailsBoCriteria).getResultList();
    if (!userDetailsBoList.isEmpty()) {
      userDetailsEntity = userDetailsBoList.get(0);
      userDetailsId = userDetailsEntity.getId();
    }
    logger.exit("getUserDetailsId() - Ends ");
    return userDetailsId;
  }

  /**
   * Saves Consent details in consent store
   *
   * @param studyConsent
   * @return
   * @throws Exception
   */
  private String saveConsentDetailsInConsentStore(
      StudyConsentEntity studyConsent,
      ParticipantStudyEntity participantStudyEntity,
      String filePath,
      String dataSharingImagePath) {
    logger.entry("Begin saveConsentDetailsInConsentStore()");

    String parentName =
        String.format(
            "projects/%s/locations/%s/datasets/%s/consentStores/%s",
            appConfig.getProjectId(),
            appConfig.getRegionId(),
            studyConsent.getStudy().getCustomId(),
            "CONSENT_" + studyConsent.getStudy().getCustomId());

    String primaryConsentArtifactName =
        createPrimaryRecord(studyConsent, participantStudyEntity, filePath, parentName);

    createDataSharingRecord(
        studyConsent,
        participantStudyEntity,
        dataSharingImagePath,
        parentName,
        primaryConsentArtifactName);

    /*    // dataSharing consent record
    String filter3 = "Metadata(\"" + CONSENT_TYPE + "\")=\"" + SHARING + "\"";
    List<Consent> Consents = consentApis.getListOfConsents(filter1 + " AND " + filter3, parentName);

    Map<String, String> dataSharingMetadata =
        getMetadata(studyConsent, participantStudyEntity, optSite, SHARING);
    dataSharingMetadata.put(DATA_SHARING, participantStudyEntity.getSharing());
    if (CollectionUtils.isEmpty(Consents)) {
      consentApis.createConsents(
          dataSharingMetadata,
          participantStudyEntity.getParticipantId(),
          parentName,
          consentArtifactName);
    } else {
      consentApis.updateConsents(
          dataSharingMetadata, Consents.get(0).getName(), consentArtifactName);
    }*/

    logger.exit("saveConsentDetailsInConsentStore() - Ends ");
    return primaryConsentArtifactName;
  }

  private String createPrimaryRecord(
      StudyConsentEntity studyConsent,
      ParticipantStudyEntity participantStudyEntity,
      String filePath,
      String parentName) {

    Map<String, String> artifactMetadata = new HashedMap<String, String>();
    artifactMetadata.put(PDF_PATH, studyConsent.getPdfPath());
    artifactMetadata.put(CONSENT_DATE, DateTimeUtils.format(studyConsent.getConsentDate()));
    artifactMetadata.put(DATA_SHARING, studyConsent.getSharing());
    artifactMetadata.put(STUDY_ID, studyConsent.getStudy().getCustomId());
    artifactMetadata.put(PARTICIPANT_STUDY_ID, participantStudyEntity.getId());
    artifactMetadata.put(CONSENT_TYPE, PRIMARY);
    Optional<SiteEntity> optSite =
        siteRepository.findById(studyConsent.getParticipantStudy().getSite().getId());
    if (optSite.isPresent()) {
      artifactMetadata.put(SITE_ID, optSite.get().getLocation().getCustomId());
    }

    // primary consent artifact
    String gcsUri = "gs://" + appConfig.getBucketName() + "/" + filePath;
    String consentArtifactName =
        consentApis.createConsentArtifact(
            artifactMetadata,
            participantStudyEntity.getParticipantId(),
            studyConsent.getVersion(),
            gcsUri,
            parentName);

    // primary consent record
    String filter1 = "user_id=\"" + participantStudyEntity.getParticipantId() + "\"";
    String filter2 = "Metadata(\"" + CONSENT_TYPE + "\")=\"" + PRIMARY + "\"";
    List<Consent> Consents = consentApis.getListOfConsents(filter1 + " AND " + filter2, parentName);

    Map<String, String> metadata =
        getMetadata(studyConsent, participantStudyEntity, optSite, PRIMARY);

    if (CollectionUtils.isEmpty(Consents)) {
      consentApis.createConsents(
          metadata,
          participantStudyEntity.getParticipantId(),
          parentName,
          consentArtifactName,
          "ACTIVE");
    } else {
      consentApis.updateConsents(metadata, Consents.get(0).getName(), consentArtifactName);
    }
    return consentArtifactName;
  }

  private void createDataSharingRecord(
      StudyConsentEntity studyConsent,
      ParticipantStudyEntity participantStudyEntity,
      String dataSharingImagePath,
      String parentName,
      String primaryConsentArtifactName) {

    logger.entry("Begin createDataSharingRecord()");
    if (!participantStudyEntity.getSharing().equals(DataSharingStatus.NOT_APPLICABLE.value())
        && !StringUtils.isEmpty(dataSharingImagePath)) {
      Optional<SiteEntity> optSite =
          siteRepository.findById(studyConsent.getParticipantStudy().getSite().getId());

      Map<String, String> artifactMetadata = new HashedMap<String, String>();
      artifactMetadata.put(IMAGE_PATH, dataSharingImagePath);
      artifactMetadata.put(CONSENT_DATE, DateTimeUtils.format(studyConsent.getConsentDate()));
      artifactMetadata.put(DATA_SHARING, studyConsent.getSharing());
      artifactMetadata.put(STUDY_ID, studyConsent.getStudy().getCustomId());
      artifactMetadata.put(CONSENT_TYPE, SHARING);

      if (optSite.isPresent()) {
        artifactMetadata.put(SITE_ID, optSite.get().getLocation().getCustomId());
      }

      // dataSharing consent artifact
      String gcsUri = "gs://" + appConfig.getBucketName() + "/" + dataSharingImagePath;

      String consentArtifactName =
          consentApis.createConsentArtifact(
              artifactMetadata,
              participantStudyEntity.getParticipantId(),
              studyConsent.getVersion(),
              gcsUri,
              parentName);

      // dataSharing consent record
      String filter1 = "user_id=\"" + participantStudyEntity.getParticipantId() + "\"";
      String filter2 = "Metadata(\"" + CONSENT_TYPE + "\")=\"" + SHARING + "\"";
      List<Consent> Consents =
          consentApis.getListOfConsents(filter1 + " AND " + filter2, parentName);

      Map<String, String> dataSharingMetadata =
          getMetadata(studyConsent, participantStudyEntity, optSite, SHARING);
      dataSharingMetadata.put(DATA_SHARING, participantStudyEntity.getSharing());
      dataSharingMetadata.put("PrimaryConsentArtifact", primaryConsentArtifactName);

      String state = "";
      if (participantStudyEntity.getSharing().equals(DataSharingStatus.NOT_PROVIDED.value())) {
        state = "REJECTED";
      } else if (participantStudyEntity.getSharing().equals(DataSharingStatus.PROVIDED.value())) {
        state = "ACTIVE";
      }

      if (CollectionUtils.isEmpty(Consents)) {

        consentApis.createConsents(
            dataSharingMetadata,
            participantStudyEntity.getParticipantId(),
            parentName,
            consentArtifactName,
            state);
      }
    }

    logger.exit("Exit createDataSharingRecord()");
  }

  private Map<String, String> getMetadata(
      StudyConsentEntity studyConsent,
      ParticipantStudyEntity participantStudyEntity,
      Optional<SiteEntity> optSite,
      String consentType) {
    Map<String, String> metadata = new HashedMap<String, String>();
    metadata.put(STUDY_ID, participantStudyEntity.getStudy().getCustomId());
    metadata.put(ENROLLED, participantStudyEntity.getEnrolledDate().toString());
    if (optSite.isPresent()) {
      metadata.put(SITE_ID, optSite.get().getLocation().getCustomId());
    }
    metadata.put(CONSENT_TYPE, consentType);
    metadata.put(VERSION, studyConsent.getVersion());
    return metadata;
  }

@Override
public StudyConsentEntity getExistStudyConsent(String userId, String studyId,String participanStudyId) {
	 logger.entry("begin getExistStudyConsent()");
	    Session session = null;
	    Query query = null;
	    Transaction transaction = null;
	    List<StudyConsentEntity> studyConsentEntities = null;
	    StudyConsentEntity consentEntity=null;
	    try {
	    	 session = this.sessionFactory.getCurrentSession();
	        String searchQuery =
	            "from StudyConsentEntity where study =:studyId and userDetails =:userId and participantStudy =:participanStudyId and DataSharingConsentArtifactPath IS NOT NULL ";
	        query =
	            session
	                .createQuery(searchQuery)
	                .setString("studyId", studyId)
	                .setString("userId", userId)
	                .setString("participanStudyId", participanStudyId);
	        studyConsentEntities = query.list();
	        if(studyConsentEntities!=null) {
	        	 consentEntity =studyConsentEntities.get(studyConsentEntities.size()-1);
	        }
	    } catch (Exception e) {
	        logger.error("UserconsentDAO - getExistStudyConsent() - ERROR ", e);
	      } 
	      logger.exit("getExistStudyConsent() - Ends");
	return consentEntity;
}
}
