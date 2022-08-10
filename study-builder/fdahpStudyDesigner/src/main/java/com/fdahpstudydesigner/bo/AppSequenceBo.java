/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Entity
@Table(name = "app_sequence")
@NamedQueries({
  @NamedQuery(
      name = "getAppSequenceById",
      query = " From AppSequenceBo SSBO WHERE SSBO.appSequenceId =:appSequenceId"),
  @NamedQuery(
      name = "getAppSequenceByAppId",
      query = " From AppSequenceBo SSBO WHERE SSBO.appId =:appId")
})
public class AppSequenceBo implements Serializable {

  private static final long serialVersionUID = 3573683893623838475L;

  @Column(name = "app_info")
  @Type(type = "yes_no")
  private boolean appInfo = false;

  @Column(name = "app_settings")
  @Type(type = "yes_no")
  private boolean appSettings = false;

  @Column(name = "app_properties")
  @Type(type = "yes_no")
  private boolean appProperties = false;

  @Column(name = "developer_configs")
  @Type(type = "yes_no")
  private boolean developerConfigs = false;

  @Column(name = "actions")
  @Type(type = "yes_no")
  private boolean actions = false;

  @Column(name = "app_check_list")
  @Type(type = "yes_no")
  private boolean appCheckList = false;

  @Column(name = "app_miscellaneous_branding")
  @Type(type = "yes_no")
  private boolean appMiscellaneousBranding = false;

  @Column(name = "app_dashboard_chart")
  @Type(type = "yes_no")
  private boolean appDashboardChart = false;

  @Column(name = "app_dashboard_stats")
  @Type(type = "yes_no")
  private boolean appDashboardStats = false;

  @Column(name = "app_id")
  private String appId;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "app_sequence_id", updatable = false, nullable = false)
  private String appSequenceId;
}
