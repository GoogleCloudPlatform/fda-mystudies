/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.response.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Entity
@Table(name = "fhir_history")
public class FHIRresponseEntity implements Serializable {

  private static final long serialVersionUID = 1005603353927628403L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "questionnaire_reference", nullable = false, length = LARGE_LENGTH)
  private String questionnaireReference;

  @Column(name = "patient_reference", nullable = false, length = LARGE_LENGTH)
  private String patientReference;

  @Column(name = "study_id", nullable = false, length = LARGE_LENGTH)
  private String studyId;

  @Column(name = "timestamp")
  @CreationTimestamp
  private Timestamp timestamp;

  @Column(name = "did_status")
  @Type(type = "yes_no")
  private Boolean didStatus = false;
}
