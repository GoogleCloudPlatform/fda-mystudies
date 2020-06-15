/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
 
package com.google.cloud.healthcare.fdamystudies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "study_consent")
public class StudyConsentBO {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "study_consent_id")
  private Integer id;

  @Column(name = "study_info_id")
  private Integer studyInfoId;

  @Column(name = "user_details_id")
  private Integer userId;

  @Column(name = "status")
  private String status;

  @Column(name = "version")
  private String version;

  @Column(name = "pdf")
  private String pdf;

  @Column(name = "pdfpath")
  private String pdfPath;

  @ManyToOne
  @JoinColumn(name = "participant_study_id", insertable = false, updatable = true)
  private ParticipantStudiesBO participantStudiesBO;

  // represents whether pdf content is stored in db=0 or gcp=1
  @Column(name = "pdfStorage", nullable = false, columnDefinition = "TINYINT")
  private int pdfStorage;

  @Column(name = "_ts")
  private String ts;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getPdf() {
    return pdf;
  }

  public void setPdf(String pdf) {
    this.pdf = pdf;
  }

  public String getPdfPath() {
    return pdfPath;
  }

  public void setPdfPath(String pdfPath) {
    this.pdfPath = pdfPath;
  }

  public int getPdfStorage() {
    return pdfStorage;
  }

  public void setPdfStorage(int pdfStorage) {
    this.pdfStorage = pdfStorage;
  }

  public String getTs() {
    return ts;
  }

  public void setTs(String ts) {
    this.ts = ts;
  }

  public Integer getStudyInfoId() {
    return studyInfoId;
  }

  public void setStudyInfoId(Integer studyInfoId) {
    this.studyInfoId = studyInfoId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public ParticipantStudiesBO getParticipantStudiesBO() {
    return participantStudiesBO;
  }

  public void setParticipantStudiesBO(ParticipantStudiesBO participantStudiesBO) {
    this.participantStudiesBO = participantStudiesBO;
  }
}
