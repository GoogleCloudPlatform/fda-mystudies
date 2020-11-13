/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@ToString
@Setter
@Getter
@Entity
@Table(name = "invite_participants")
public class InviteParticipantEntity implements Serializable {

  private static final long serialVersionUID = 8610289975376774137L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "study_info_id")
  private String study;

  @Column(name = "participant_registry_site_id")
  private String participantRegistrySite;

  @Column(name = "status", columnDefinition = "TINYINT(1) default 0")
  private boolean status;
}
