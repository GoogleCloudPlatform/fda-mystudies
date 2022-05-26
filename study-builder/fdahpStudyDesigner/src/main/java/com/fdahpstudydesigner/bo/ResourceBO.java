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

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "resources")
@NamedQueries({
  @NamedQuery(
      name = "getResourceInfo",
      query = "SELECT RBO FROM ResourceBO RBO WHERE RBO.id =:resourceInfoId"),
})
public class ResourceBO implements Serializable {

  private static final long serialVersionUID = -4548349227102496191L;

  @Column(name = "action", length = 1)
  private boolean action;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "end_date")
  private String endDate;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Transient private MultipartFile pdfFile;

  @Column(name = "pdf_name")
  private String pdfName;

  @Column(name = "pdf_url")
  private String pdfUrl;

  @Column(name = "resource_text")
  private String resourceText;

  @Column(name = "resource_type", length = 1)
  private boolean resourceType;

  @Column(name = "resource_visibility", length = 1)
  private boolean resourceVisibility;

  @Column(name = "rich_text")
  private String richText;

  @Column(name = "sequence_no")
  private Integer sequenceNo = 0;

  @Column(name = "start_date")
  private String startDate;

  @Column(name = "status", length = 1)
  private boolean status;

  @Column(name = "study_id")
  private String studyId;

  @Column(name = "study_protocol", length = 1)
  private boolean studyProtocol;

  @Column(name = "text_or_pdf", length = 1)
  private boolean textOrPdf;

  @Column(name = "time_period_from_days")
  private Integer timePeriodFromDays;

  @Column(name = "time_period_to_days")
  private Integer timePeriodToDays;

  @Column(name = "title")
  private String title;

  @Column(name = "x_days_sign", length = 1)
  private boolean xDaysSign = false;

  @Column(name = "y_days_sign", length = 1)
  private boolean yDaysSign = false;

  @Column(name = "anchor_date_id")
  private String anchorDateId;

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public String getEndDate() {
    return endDate;
  }

  public String getId() {
    return id;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public MultipartFile getPdfFile() {
    return pdfFile;
  }

  public String getPdfName() {
    return pdfName;
  }

  public String getPdfUrl() {
    return pdfUrl;
  }

  public String getResourceText() {
    return resourceText;
  }

  public String getRichText() {
    return richText;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getStudyId() {
    return studyId;
  }

  public Integer getTimePeriodFromDays() {
    return timePeriodFromDays;
  }

  public Integer getTimePeriodToDays() {
    return timePeriodToDays;
  }

  public String getTitle() {
    return title;
  }

  public boolean isAction() {
    return action;
  }

  public boolean isResourceType() {
    return resourceType;
  }

  public boolean isResourceVisibility() {
    return resourceVisibility;
  }

  public boolean isStatus() {
    return status;
  }

  public boolean isStudyProtocol() {
    return studyProtocol;
  }

  public boolean isTextOrPdf() {
    return textOrPdf;
  }

  public boolean isxDaysSign() {
    return xDaysSign;
  }

  public boolean isyDaysSign() {
    return yDaysSign;
  }

  public void setAction(boolean action) {
    this.action = action;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setPdfFile(MultipartFile pdfFile) {
    this.pdfFile = pdfFile;
  }

  public void setPdfName(String pdfName) {
    this.pdfName = pdfName;
  }

  public void setPdfUrl(String pdfUrl) {
    this.pdfUrl = pdfUrl;
  }

  public void setResourceText(String resourceText) {
    this.resourceText = resourceText;
  }

  public void setResourceType(boolean resourceType) {
    this.resourceType = resourceType;
  }

  public void setResourceVisibility(boolean resourceVisibility) {
    this.resourceVisibility = resourceVisibility;
  }

  public void setRichText(String richText) {
    this.richText = richText;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public void setStudyProtocol(boolean studyProtocol) {
    this.studyProtocol = studyProtocol;
  }

  public void setTextOrPdf(boolean textOrPdf) {
    this.textOrPdf = textOrPdf;
  }

  public void setTimePeriodFromDays(Integer timePeriodFromDays) {
    this.timePeriodFromDays = timePeriodFromDays;
  }

  public void setTimePeriodToDays(Integer timePeriodToDays) {
    this.timePeriodToDays = timePeriodToDays;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setxDaysSign(boolean xDaysSign) {
    this.xDaysSign = xDaysSign;
  }

  public void setyDaysSign(boolean yDaysSign) {
    this.yDaysSign = yDaysSign;
  }

  public String getAnchorDateId() {
    return anchorDateId;
  }

  public void setAnchorDateId(String anchorDateId) {
    this.anchorDateId = anchorDateId;
  }
}
