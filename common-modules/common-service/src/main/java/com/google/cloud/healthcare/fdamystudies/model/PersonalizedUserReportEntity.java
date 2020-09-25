/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.MEDIUM_LENGTH;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "personalized_user_report")
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

  @Column(name = "report_title", length = MEDIUM_LENGTH)
  private String reportTitle;

  @Column(name = "report_content")
  @Type(type = "text")
  private String reportContent;

  @Column(name = "activity_date_time")
  @CreationTimestamp
  private Timestamp creationTime;
}
