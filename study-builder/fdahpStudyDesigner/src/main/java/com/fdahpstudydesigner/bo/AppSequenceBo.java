/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
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
      name = "getAppSequenceByAppd",
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
