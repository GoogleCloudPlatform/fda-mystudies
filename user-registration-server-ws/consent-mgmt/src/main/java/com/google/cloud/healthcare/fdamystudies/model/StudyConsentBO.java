package com.google.cloud.healthcare.fdamystudies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

  // represents whether pdf content is stored in db=0 or gcp=1
  @Column(name = "pdfStorage", nullable = false, columnDefinition = "TINYINT")
  private int pdfStorage;

  /*  @Column(name = "application_id")
  private String applicationId;

  @Column(name = "org_id")
  private String orgId;*/

  @Column(name = "_ts")
  private String _ts;

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

  public String get_ts() {
    return _ts;
  }

  public void set_ts(String _ts) {
    this._ts = _ts;
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
}
