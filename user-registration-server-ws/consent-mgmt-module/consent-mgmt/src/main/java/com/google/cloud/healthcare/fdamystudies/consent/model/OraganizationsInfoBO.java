package com.google.cloud.healthcare.fdamystudies.consent.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

@Setter
@Getter
@ToString
@Entity
@Table(name = "org_info")
public class OraganizationsInfoBO {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private int orgInfoId;

  @Column(name = "custom_id")
  private String customOrgId;

  @Column(name = "name")
  private String orgName;

  @Column(name = "description")
  @Type(type = "text")
  private String orgDescription;

  @Column(name = "type")
  private String orgType;

  @Column(name = "created_on")
  private Date createdOn;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;
}
