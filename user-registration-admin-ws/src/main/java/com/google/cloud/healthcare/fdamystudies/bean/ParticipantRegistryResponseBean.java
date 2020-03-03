/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonInclude(Include.NON_NULL)
@Component
@Scope(value = "prototype")
@ToString
@Getter
@Setter
public class ParticipantRegistryResponseBean {

  private Integer studyId = 0;
  private String customStudyId = "";
  private String studyName = "";
  private Integer appId = 0;
  private String customAppId = "";
  private String appName = "";
  private Integer siteId;
  private String customLocationId = "";
  private String locationName = "";
  private String locationStatus = "";
  private Integer sitePermission;
  private List<ParticipantBean> registryParticipants = new ArrayList<>();
  private Map<String, Integer> countByStatus;
}
