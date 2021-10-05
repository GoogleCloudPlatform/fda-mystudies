/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.AppDetailsBean;
import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;

public interface AppService {

  public List<AppListBean> getAppList(String userId);

  public AppsBo getAppById(String appId, String userId);

  public boolean validateAppId(String appId);

  public String saveOrUpdateApp(AppsBo appsBo, SessionObject sessionObject);

  public String saveOrUpdateAppSettings(AppsBo appsBo, SessionObject sessionObject);

  public String updateAppAction(
      String appId, String buttonText, SessionObject sesObj, AuditLogEventRequest auditRequest);

  public AppDetailsBean getAppDetailsBean(String customAppId);

  public String saveOrUpdateAppProperties(AppsBo appsBo, SessionObject sessionObject);

  public boolean validateAppActions(String appId);

  public List<AppsBo> getAllApps();

  public String saveOrUpdateAppDeveloperConfig(AppsBo appsBo, SessionObject sessionObject);

  public List<AppsBo> getApps(String userId);

  public AppsBo getAppbyCustomAppId(String customId);

  public boolean getAppPermission(String appId, String userId);

  public int getStudiesByAppId(String customAppId);

  public List<AppsBo> getAppsForStudy(String userId);

  public int getStudiesCountByAppId(String customAppId);
}
