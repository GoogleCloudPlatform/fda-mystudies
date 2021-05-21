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

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_UPDATED_FAILED;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.dao.DashBoardAndProfileDAO;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashBoardAndProfileServiceImpl implements DashBoardAndProfileService {

  private static XLogger logger =
      XLoggerFactory.getXLogger(DashBoardAndProfileServiceImpl.class.getName());

  @Autowired private DashBoardAndProfileDAO dashBoardAndProfiledao;

  @Autowired private StudyBuilderAuditEventHelper auditLogHelper;

  @Autowired private HttpServletRequest request;

  @Override
  public MasterDataBO getMasterData(String type) {
    logger.entry("begin getMasterData()");
    MasterDataBO masterDataBO = null;
    try {
      masterDataBO = dashBoardAndProfiledao.getMasterData(type);
    } catch (Exception e) {
      logger.error("DashBoardAndProfileServiceImpl - getMasterData() - ERROR", e);
    }
    logger.exit("getMasterData() - Ends");
    return masterDataBO;
  }

  @Override
  public String isEmailValid(String email) {
    return dashBoardAndProfiledao.isEmailValid(email);
  }

  @Override
  public String updateProfileDetails(UserBO userBO, String userId, SessionObject userSession) {
    logger.entry("begin updateProfileDetails()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    StudyBuilderAuditEvent auditLogEvent = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      message = dashBoardAndProfiledao.updateProfileDetails(userBO, userId);
      if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
        auditLogEvent = USER_ACCOUNT_UPDATED;
      } else {
        auditLogEvent = USER_ACCOUNT_UPDATED_FAILED;
      }
      auditLogHelper.logEvent(auditLogEvent, auditRequest);
    } catch (Exception e) {
      logger.error("DashBoardAndProfileServiceImpl - updateProfileDetails() - Error", e);
    }
    logger.exit("updateProfileDetails - Ends");
    return message;
  }
}
