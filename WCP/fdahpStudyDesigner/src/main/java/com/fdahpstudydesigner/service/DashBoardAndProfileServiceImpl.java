package com.fdahpstudydesigner.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.dao.AuditLogDAO;
import com.fdahpstudydesigner.dao.DashBoardAndProfileDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;

@Service
public class DashBoardAndProfileServiceImpl implements DashBoardAndProfileService {

  private static Logger logger = Logger.getLogger(DashBoardAndProfileServiceImpl.class);

  @Autowired private AuditLogDAO auditLogDAO;

  @Autowired private DashBoardAndProfileDAO dashBoardAndProfiledao;

  @Override
  public MasterDataBO getMasterData(String type) {
    logger.info("DashBoardAndProfileServiceImpl - getMasterData() - Starts");
    MasterDataBO masterDataBO = null;
    try {
      masterDataBO = dashBoardAndProfiledao.getMasterData(type);
    } catch (Exception e) {
      logger.error("DashBoardAndProfileServiceImpl - getMasterData() - ERROR", e);
    }
    logger.info("DashBoardAndProfileServiceImpl - getMasterData() - Ends");
    return masterDataBO;
  }

  
  @Override
  public String isEmailValid(String email) {
    return dashBoardAndProfiledao.isEmailValid(email);
  }

  
  @Override
  public String updateProfileDetails(UserBO userBO, int userId, SessionObject userSession) {
    logger.info("DashBoardAndProfileServiceImpl - updateProfileDetails - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    String activity = "";
    String activityDetail = "";
    try {
      message = dashBoardAndProfiledao.updateProfileDetails(userBO, userId);
      if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
        activity = "Profile details updated.";
        activityDetail =
            "Profile details updated successfully. (Account Details:- First Name = "
                + userBO.getFirstName()
                + ", Last Name = "
                + userBO.getLastName()
                + " ,Email = "
                + userBO.getUserEmail()
                + ")";
        // Audit log capturing the action performed
        auditLogDAO.saveToAuditLog(
            null,
            null,
            userSession,
            activity,
            activityDetail,
            "DashBoardAndProfileDAOImpl - updateProfileDetails()");
      }
    } catch (Exception e) {
      logger.error("DashBoardAndProfileServiceImpl - updateProfileDetails() - Error", e);
    }
    logger.info("DashBoardAndProfileServiceImpl - updateProfileDetails - Starts");
    return message;
  }
}
