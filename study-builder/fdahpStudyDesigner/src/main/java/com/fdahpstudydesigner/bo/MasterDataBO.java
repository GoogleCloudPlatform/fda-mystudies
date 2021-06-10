/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "master_data")
@NamedQueries({
  @NamedQuery(
      name = "getMasterDataByType",
      query = "select MDBO from MasterDataBO MDBO where MDBO.type =:type"),
})
public class MasterDataBO {

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "privacy_policy_text")
  private String privacyPolicyText;

  @Column(name = "terms_text")
  private String termsText;

  @Column(name = "type")
  private String type;

  public String getId() {
    return id;
  }

  public String getPrivacyPolicyText() {
    return privacyPolicyText;
  }

  public String getTermsText() {
    return termsText;
  }

  public String getType() {
    return type;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setPrivacyPolicyText(String privacyPolicyText) {
    this.privacyPolicyText = privacyPolicyText;
  }

  public void setTermsText(String termsText) {
    this.termsText = termsText;
  }

  public void setType(String type) {
    this.type = type;
  }
}
