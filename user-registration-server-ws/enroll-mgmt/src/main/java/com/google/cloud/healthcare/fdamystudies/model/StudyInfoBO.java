package com.google.cloud.healthcare.fdamystudies.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "study_info")
public class StudyInfoBO {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer studyInfoId;

  @Column(name = "custom_id")
  private String customStudyId;

  @Column(name = "app_info_id")
  private int appInfoId;

  @Column(name = "name")
  private String studyName;

  @Column(name = "description")
  @Type(type = "text")
  private String studyDescription;

  @Column(name = "study_type")
  private String studyType;

  @Column(name = "created_on")
  private Date createdOn;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;
}
