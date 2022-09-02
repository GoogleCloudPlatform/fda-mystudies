/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "studies")
@NamedQueries({
  @NamedQuery(name = "studyDetailsByStudyId", query = "from StudyDto SDTO" + " where SDTO.id =:id"),
  @NamedQuery(
      name = "getStudyIdByCustomStudyId",
      query =
          "select SDTO.id" + " from StudyDto SDTO" + " where SDTO.customStudyId =:customStudyId"),
  @NamedQuery(
      name = "getLiveStudyIdByCustomStudyId",
      query = "from StudyDto SDTO" + " where SDTO.customStudyId =:customStudyId and SDTO.live=1"),
  @NamedQuery(
      name = "getPublishedStudyByCustomId",
      query =
          "from StudyDto SDTO"
              + " where SDTO.customStudyId =:customStudyId and SDTO.status='Pre-launch(Published)'"),
  @NamedQuery(
      name = "getActivityUpdatedOrNotByStudyIdAndVersion",
      query =
          "from StudyDto SDTO"
              + " where SDTO.customStudyId =:customStudyId and ROUND(SDTO.version,1)=:version"),
})
public class StudyDto implements Serializable {

  private static final long serialVersionUID = -5843091568457462789L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "custom_study_id")
  private String customStudyId;

  @Column(name = "name")
  private String name;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "type")
  private String type;

  @Column(name = "platform")
  private String platform;

  @Column(name = "category")
  private String category;

  @Column(name = "research_sponsor")
  private String researchSponsor;

  @Column(name = "tentative_duration")
  private Integer tentativeDuration;

  @Column(name = "tentative_duration_weekmonth")
  private String tentativeDurationWeekmonth;

  @Column(name = "description")
  private String description;

  @Column(name = "enrolling_participants")
  private String enrollingParticipants;
  
  @Deprecated
  @Column(name = "retain_participant")
  private String retainParticipant;

  @Deprecated
  @Column(name = "allow_rejoin")
  private String allowRejoin;

  @Deprecated
  @Column(name = "allow_rejoin_text")
  private String allowRejoinText;

  @Column(name = "irb_review")
  private String irbReview;

  @Column(name = "inbox_email_address")
  private String inboxEmailAddress;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "status")
  private String status;

  @Column(name = "sequence_number")
  private Integer sequenceNumber;

  @Column(name = "thumbnail_image")
  private String thumbnailImage;

  @Column(name = "media_link")
  private String mediaLink;

  @Column(name = "study_website")
  private String studyWebsite;

  @Column(name = "study_tagline")
  private String studyTagline;

  @Column(name = "version")
  private Float version = 0f;

  @Column(name = "study_lunched_date")
  private String studylunchDate;

  @Column(name = "study_pre_active_flag")
  @Type(type = "yes_no")
  private boolean studyPreActiveFlag = false;

  @Column(name = "is_live")
  private Integer live = 0;

  @Column(name = "has_study_draft")
  private Integer hasStudyDraft = 0;

  @Column(name = "has_activity_draft")
  private Integer hasActivityDraft = 0;

  @Column(name = "has_consent_draft")
  private Integer hasConsentDraft = 0;

  @Column(name = "has_questionnaire_draft")
  private Integer hasQuestionnaireDraft = 0;

  @Column(name = "has_activitetask_draft")
  private Integer hasActivetaskDraft = 0;

  @Column(name = "app_id")
  private String appId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getResearchSponsor() {
    return researchSponsor;
  }

  public void setResearchSponsor(String researchSponsor) {
    this.researchSponsor = researchSponsor;
  }

  public Integer getTentativeDuration() {
    return tentativeDuration;
  }

  public void setTentativeDuration(Integer tentativeDuration) {
    this.tentativeDuration = tentativeDuration;
  }

  public String getTentativeDurationWeekmonth() {
    return tentativeDurationWeekmonth;
  }

  public void setTentativeDurationWeekmonth(String tentativeDurationWeekmonth) {
    this.tentativeDurationWeekmonth = tentativeDurationWeekmonth;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getEnrollingParticipants() {
    return enrollingParticipants;
  }

  public void setEnrollingParticipants(String enrollingParticipants) {
    this.enrollingParticipants = enrollingParticipants;
  }
  
  public String getRetainParticipant() {
    return retainParticipant;
  }

  public void setRetainParticipant(String retainParticipant) {
    this.retainParticipant = retainParticipant;
  }

  public String getAllowRejoin() {
    return allowRejoin;
  }

  public void setAllowRejoin(String allowRejoin) {
    this.allowRejoin = allowRejoin;
  }

  public String getAllowRejoinText() {
    return allowRejoinText;
  }

  public void setAllowRejoinText(String allowRejoinText) {
    this.allowRejoinText = allowRejoinText;
  }

  public String getIrbReview() {
    return irbReview;
  }

  public void setIrbReview(String irbReview) {
    this.irbReview = irbReview;
  }

  public String getInboxEmailAddress() {
    return inboxEmailAddress;
  }

  public void setInboxEmailAddress(String inboxEmailAddress) {
    this.inboxEmailAddress = inboxEmailAddress;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }

  public String getThumbnailImage() {
    return thumbnailImage;
  }

  public void setThumbnailImage(String thumbnailImage) {
    this.thumbnailImage = thumbnailImage;
  }

  public String getMediaLink() {
    return mediaLink;
  }

  public void setMediaLink(String mediaLink) {
    this.mediaLink = mediaLink;
  }

  public String getStudyWebsite() {
    return studyWebsite;
  }

  public void setStudyWebsite(String studyWebsite) {
    this.studyWebsite = studyWebsite;
  }

  public String getStudyTagline() {
    return studyTagline;
  }

  public void setStudyTagline(String studyTagline) {
    this.studyTagline = studyTagline;
  }

  public Float getVersion() {
    return version;
  }

  public void setVersion(Float version) {
    this.version = version;
  }

  public String getStudylunchDate() {
    return studylunchDate;
  }

  public void setStudylunchDate(String studylunchDate) {
    this.studylunchDate = studylunchDate;
  }

  public boolean isStudyPreActiveFlag() {
    return studyPreActiveFlag;
  }

  public void setStudyPreActiveFlag(boolean studyPreActiveFlag) {
    this.studyPreActiveFlag = studyPreActiveFlag;
  }

  public Integer getLive() {
    return live;
  }

  public void setLive(Integer live) {
    this.live = live;
  }

  public Integer getHasStudyDraft() {
    return hasStudyDraft;
  }

  public void setHasStudyDraft(Integer hasStudyDraft) {
    this.hasStudyDraft = hasStudyDraft;
  }

  public Integer getHasActivityDraft() {
    return hasActivityDraft;
  }

  public void setHasActivityDraft(Integer hasActivityDraft) {
    this.hasActivityDraft = hasActivityDraft;
  }

  public Integer getHasConsentDraft() {
    return hasConsentDraft;
  }

  public void setHasConsentDraft(Integer hasConsentDraft) {
    this.hasConsentDraft = hasConsentDraft;
  }

  public Integer getHasQuestionnaireDraft() {
    return hasQuestionnaireDraft;
  }

  public void setHasQuestionnaireDraft(Integer hasQuestionnaireDraft) {
    this.hasQuestionnaireDraft = hasQuestionnaireDraft;
  }

  public Integer getHasActivetaskDraft() {
    return hasActivetaskDraft;
  }

  public void setHasActivetaskDraft(Integer hasActivetaskDraft) {
    this.hasActivetaskDraft = hasActivetaskDraft;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }
}
