/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@Table(name = "participant_study_info")
public class ParticipantStudiesBO {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "participant_study_info_id")
  private Integer participantStudyInfoId;

  @Column(name = "participant_id", unique = true)
  private String participantId;

  @ManyToOne
  @JoinColumn(name = "participant_registry_site_id", insertable = false, updatable = true)
  private ParticipantRegistrySite participantRegistrySite;

  @ManyToOne
  @JoinColumn(name = "study_info_id", insertable = false, updatable = false)
  private StudyInfoBO studyInfo;

  @ManyToOne
  @JoinColumn(name = "site_id", insertable = false, updatable = false)
  private SiteBo siteBo;

  @ManyToOne
  @JoinColumn(name = "user_details_id", insertable = false, updatable = false)
  private UserDetails userDetails;

  @Column(name = "consent_status", columnDefinition = "TINYINT(1)")
  private Boolean consentStatus = false;

  @Column(name = "status")
  private String status;

  @Column(name = "bookmark", columnDefinition = "TINYINT(1)")
  private Boolean bookmark = false;

  @Column(name = "eligbibility", columnDefinition = "TINYINT(1)")
  private Boolean eligbibility = false;

  @Column(name = "enrolled_date")
  private Date enrolledDate;

  @Column(name = "sharing")
  private String sharing;

  @Column(name = "completion")
  private Integer completion;

  @Column(name = "adherence")
  private Integer adherence;

  @Column(name = "withdrawal_date")
  private LocalDateTime withdrawalDate;
}
