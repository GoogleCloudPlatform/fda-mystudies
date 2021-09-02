/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.AppDetailsBean;
import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.bo.StudyBo;
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

  public List<AppsBo> getActiveApps(String userId);

  public AppsBo getAppbyCustomAppId(String customId);

  public boolean getAppPermission(String appId, String userId);

  public int getStudiesByAppId(String customAppId);

  public List<AppsBo> getAppsForStudy(String userId);

  public List<StudyBo> getStudiesAssociatedWithApps(String appIds);
}
