/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "personalized_user_report")
@ConditionalOnProperty(
    value = "participant.datastore.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class PersonalizedUserReportEntity implements Serializable {

  private static final long serialVersionUID = -3019529323339411129L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserDetailsEntity userDetails;

  @ManyToOne
  @JoinColumn(name = "study_info_id")
  private StudyEntity studyInfo;

  @Column(name = "report_title", length = ColumnConstraints.MEDIUM_LENGTH)
  private String reportTitle;

  // Length is an arbitrary multiple of 100 > 2^15 and < 2^16 to guarantee we
  // get the `TEXT` data type. Marking `columnDefinition = Text` is not
  // portable and doesn't work in tests.
  @Column(name = "report_content")
  @Type(type = "text")
  private String reportContent;

  @Column(name = "activity_date_time")
  @CreationTimestamp
  private Timestamp creationTime;
}
