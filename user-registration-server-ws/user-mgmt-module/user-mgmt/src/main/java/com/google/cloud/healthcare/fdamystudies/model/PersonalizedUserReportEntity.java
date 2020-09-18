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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "personalized_user_report")
public class PersonalizedUserReportEntity implements Serializable {

  private static final long serialVersionUID = -3019529323339411129L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserDetailsEntity userDetails;

  @ManyToOne
  @JoinColumn(name = "study_info_id")
  private StudyEntity studyInfo;

  @Column(name = "report_title")
  private String reportTitle;

  // Length is an arbitrary multiple of 100 > 2^15 and < 2^16 to guarantee we
  // get the `TEXT` data type. Marking `columnDefinition = Text` is not
  // portable and doesn't work in tests.
  @Column(name = "report_content", length = 65500)
  private String reportContent;

  @Column(name = "activity_date_time")
  @CreationTimestamp
  private Timestamp creationTime;
}
