/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.usermgmt.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Setter
@Getter
@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "user_institution")
public class UserInstitution {
  @Id
  @Column(name = "user_institution_id", unique = true)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userInstitutionId;

  @NotNull
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_details_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private UserDetailsBO user;

  @Column(name = "institution_id")
  private String institutionId;
}
