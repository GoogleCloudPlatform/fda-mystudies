/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "response_sub_type_value")
@NamedQueries({
  @NamedQuery(
      name = "getQuestionSubResponse",
      query =
          "from QuestionResponseSubTypeBo QRBO where QRBO.responseTypeId=:responseTypeId and QRBO.active=1  order by QRBO.sequenceNumber "),
})
public class QuestionResponseSubTypeBo implements Serializable {

  private static final long serialVersionUID = -7853082585280415082L;

  @Column(name = "active")
  private Boolean active;

  @Column(name = "description")
  private String description;

  @Column(name = "destination_step_id")
  private String destinationStepId;

  @Column(name = "detail")
  private String detail;

  @Column(name = "exclusive")
  private String exclusive;

  @Column(name = "image")
  private String image;

  @Transient private MultipartFile imageFile;

  @Transient private Integer imageId;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "response_sub_type_value_id", updatable = false, nullable = false)
  private String responseSubTypeValueId;

  @Column(name = "response_type_id")
  private String responseTypeId;

  @Column(name = "selected_image")
  private String selectedImage;

  @Transient private MultipartFile selectImageFile;

  @Column(name = "study_version")
  private final Integer studyVersion = 1;

  @Column(name = "text")
  private String text;

  @Column(name = "value")
  private String value;

  @Transient private String signedImage;

  @Transient private String signedSelectedImage;

  @Column(name = "sequence_number")
  private Integer sequenceNumber;

  public Boolean getActive() {
    return active;
  }

  public String getDescription() {
    return description;
  }

  public String getDestinationStepId() {
    return destinationStepId;
  }

  public String getDetail() {
    return detail;
  }

  public String getExclusive() {
    return exclusive;
  }

  public String getImage() {
    return image;
  }

  public MultipartFile getImageFile() {
    return imageFile;
  }

  public Integer getImageId() {
    return imageId;
  }

  public String getResponseSubTypeValueId() {
    return responseSubTypeValueId;
  }

  public String getResponseTypeId() {
    return responseTypeId;
  }

  public String getSelectedImage() {
    return selectedImage;
  }

  public MultipartFile getSelectImageFile() {
    return selectImageFile;
  }

  public String getText() {
    return text;
  }

  public String getValue() {
    return value;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDestinationStepId(String destinationStepId) {
    this.destinationStepId = destinationStepId;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public void setExclusive(String exclusive) {
    this.exclusive = exclusive;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setImageFile(MultipartFile imageFile) {
    this.imageFile = imageFile;
  }

  public void setImageId(Integer imageId) {
    this.imageId = imageId;
  }

  public void setResponseSubTypeValueId(String responseSubTypeValueId) {
    this.responseSubTypeValueId = responseSubTypeValueId;
  }

  public void setResponseTypeId(String responseTypeId) {
    this.responseTypeId = responseTypeId;
  }

  public void setSelectedImage(String selectedImage) {
    this.selectedImage = selectedImage;
  }

  public void setSelectImageFile(MultipartFile selectImageFile) {
    this.selectImageFile = selectImageFile;
  }

  public void setText(String text) {
    this.text = text;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getSignedImage() {
    return signedImage;
  }

  public String getSignedSelectedImage() {
    return signedSelectedImage;
  }

  public void setSignedImage(String signedImage) {
    this.signedImage = signedImage;
  }

  public void setSignedSelectedImage(String signedSelectedImage) {
    this.signedSelectedImage = signedSelectedImage;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }
}
