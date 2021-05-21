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

import com.fdahpstudydesigner.bean.StudyDetailsBean;
import com.fdahpstudydesigner.bean.StudyIdBean;
import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bean.StudyPageBean;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.Checklist;
import com.fdahpstudydesigner.bo.ComprehensionTestQuestionBo;
import com.fdahpstudydesigner.bo.ComprehensionTestResponseBo;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.ConsentMasterInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.EligibilityTestBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.ReferenceTablesBo;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPageBo;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.dao.NotificationDAO;
import com.fdahpstudydesigner.dao.StudyActiveTasksDAO;
import com.fdahpstudydesigner.dao.StudyDAO;
import com.fdahpstudydesigner.dao.StudyQuestionnaireDAO;
import com.fdahpstudydesigner.util.CustomMultipartFile;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.ImageUtility;
import com.fdahpstudydesigner.util.SessionObject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudyServiceImpl implements StudyService {

  private static XLogger logger = XLoggerFactory.getXLogger(StudyServiceImpl.class.getName());

  @Autowired private StudyDAO studyDAO;

  @Autowired private StudyQuestionnaireDAO studyQuestionnaireDAO;

  @Autowired private NotificationDAO notificationDAO;

  @Autowired private StudyActiveTasksDAO studyActiveTasksDAO;

  @Override
  public String checkActiveTaskTypeValidation(String studyId) {
    logger.entry("StudyServiceImpl - checkActiveTaskTypeValidation - Starts");
    return studyDAO.checkActiveTaskTypeValidation(studyId);
  }

  @Override
  public int comprehensionTestQuestionOrder(String studyId) {
    int count = 1;
    logger.entry("StudyServiceImpl - comprehensionTestQuestionOrder() - Starts");
    try {
      count = studyDAO.comprehensionTestQuestionOrder(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - comprehensionTestQuestionOrder() - Error", e);
    }
    logger.exit("StudyServiceImpl - comprehensionTestQuestionOrder() - Ends");
    return count;
  }

  @Override
  public int consentInfoOrder(String studyId) {
    int count = 1;
    logger.entry("StudyServiceImpl - consentInfoOrder() - Starts");
    try {
      count = studyDAO.consentInfoOrder(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - consentInfoOrder() - Error", e);
    }
    logger.exit("StudyServiceImpl - consentInfoOrder() - Ends");
    return count;
  }

  @Override
  public boolean copyliveStudyByCustomStudyId(String customStudyId, SessionObject object) {
    logger.entry("StudyServiceImpl - copyliveStudyByCustomStudyId() - Starts");
    boolean flag = false;
    try {
      flag =
          studyDAO.resetDraftStudyByCustomStudyId(
              customStudyId, FdahpStudyDesignerConstants.COPY_STUDY, object);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - copyliveStudyByCustomStudyId() - Error", e);
    }
    logger.exit("StudyServiceImpl - copyliveStudyByCustomStudyId() - Ends");
    return flag;
  }

  @Override
  public String deleteComprehensionTestQuestion(
      String questionId, String studyId, SessionObject sessionObject) {
    logger.entry("StudyServiceImpl - deleteComprehensionTestQuestion() - Starts");
    String message = null;
    try {
      message = studyDAO.deleteComprehensionTestQuestion(questionId, studyId, sessionObject);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - deleteComprehensionTestQuestion() - Error", e);
    }
    logger.exit("StudyServiceImpl - deleteComprehensionTestQuestion() - Ends");
    return message;
  }

  @Override
  public String deleteConsentInfo(
      String consentInfoId, String studyId, SessionObject sessionObject, String customStudyId) {
    logger.entry("StudyServiceImpl - deleteConsentInfo() - Starts");
    String message = null;
    try {
      message = studyDAO.deleteConsentInfo(consentInfoId, studyId, sessionObject, customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - deleteConsentInfo() - Error", e);
    }
    logger.exit("StudyServiceImpl - deleteConsentInfo() - Ends");
    return message;
  }

  @Override
  public String deleteEligibilityTestQusAnsById(
      String eligibilityTestId, String studyId, SessionObject sessionObject, String customStudyId) {
    logger.entry("StudyServiceImpl - deleteEligibilityTestQusAnsById - Starts");
    String message = FdahpStudyDesignerConstants.SUCCESS;
    try {
      message =
          studyDAO.deleteEligibilityTestQusAnsById(
              eligibilityTestId, studyId, sessionObject, customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - deleteEligibilityTestQusAnsById - Error", e);
    }
    logger.exit("StudyServiceImpl - deleteEligibilityTestQusAnsById - Ends");
    return message;
  }

  @Override
  public String deleteOverviewStudyPageById(String studyId, String pageId) {
    logger.entry("StudyServiceImpl - deleteOverviewStudyPageById() - Starts");
    String message = "";
    try {
      message = studyDAO.deleteOverviewStudyPageById(studyId, pageId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - deleteOverviewStudyPageById() - ERROR ", e);
    }
    return message;
  }

  @Override
  public String deleteResourceInfo(
      String resourceInfoId, SessionObject sesObj, String customStudyId, String studyId) {
    logger.entry("StudyServiceImpl - deleteConsentInfo() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    ResourceBO resourceBO = null;
    try {
      resourceBO = studyDAO.getResourceInfo(resourceInfoId);
      if (null != resourceBO) {
        message =
            studyDAO.deleteResourceInfo(resourceInfoId, resourceBO.isResourceVisibility(), studyId);
      }

    } catch (Exception e) {
      logger.error("StudyServiceImpl - deleteConsentInfo() - Error", e);
    }
    logger.exit("StudyServiceImpl - deleteConsentInfo() - Ends");
    return message;
  }

  @Override
  public boolean deleteStudyByCustomStudyId(String customStudyId) {
    logger.entry("StudyServiceImpl - deleteStudyByCustomStudyId() - Starts");
    boolean flag = false;
    try {
      flag = studyDAO.deleteStudyByCustomStudyId(customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - deleteStudyByCustomStudyId() - Error", e);
    }
    logger.exit("StudyServiceImpl - deleteStudyByCustomStudyId() - Ends");
    return flag;
  }

  @Override
  public List<StudyBo> getAllStudyList() {
    logger.entry("StudyServiceImpl - getAllStudyList() - Starts");
    List<StudyBo> studyBOList = null;
    try {
      studyBOList = studyDAO.getAllStudyList();
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getAllStudyList() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getAllStudyList() - Ends");
    return studyBOList;
  }

  @Override
  public Checklist getchecklistInfo(String studyId) {
    logger.entry("StudyServiceImpl - getchecklistInfo() - Starts");
    Checklist checklist = null;
    try {
      checklist = studyDAO.getchecklistInfo(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getchecklistInfo() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getchecklistInfo() - Ends");
    return checklist;
  }

  @Override
  public ComprehensionTestQuestionBo getComprehensionTestQuestionById(String questionId) {
    logger.entry("StudyServiceImpl - getComprehensionTestQuestionById() - Starts");
    ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
    try {
      comprehensionTestQuestionBo = studyDAO.getComprehensionTestQuestionById(questionId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getComprehensionTestQuestionById() - Error", e);
    }
    logger.exit("StudyServiceImpl - getComprehensionTestQuestionById() - Ends");
    return comprehensionTestQuestionBo;
  }

  @Override
  public List<ComprehensionTestQuestionBo> getComprehensionTestQuestionList(String studyId) {
    logger.entry("StudyServiceImpl - getComprehensionTestQuestionList() - Starts");
    List<ComprehensionTestQuestionBo> comprehensionTestQuestionList = null;
    try {
      comprehensionTestQuestionList = studyDAO.getComprehensionTestQuestionList(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getComprehensionTestQuestionList() - Error", e);
    }
    logger.exit("StudyServiceImpl - getComprehensionTestQuestionList() - Starts");
    return comprehensionTestQuestionList;
  }

  @Override
  public List<ComprehensionTestResponseBo> getComprehensionTestResponseList(
      String comprehensionQuestionId) {
    logger.entry("StudyServiceImpl - getComprehensionTestResponseList() - Starts");
    List<ComprehensionTestResponseBo> comprehensionTestResponseLsit = null;
    try {
      comprehensionTestResponseLsit =
          studyDAO.getComprehensionTestResponseList(comprehensionQuestionId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getComprehensionTestResponseList() - ERROR", e);
    }
    logger.exit("StudyServiceImpl - getComprehensionTestResponseList() - Starts");
    return comprehensionTestResponseLsit;
  }

  @Override
  public ConsentBo getConsentDetailsByStudyId(String studyId) {
    logger.entry("INFO: StudyServiceImpl - getConsentDetailsByStudyId() :: Starts");
    ConsentBo consentBo = null;
    try {
      consentBo = studyDAO.getConsentDetailsByStudyId(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getConsentDetailsByStudyId() :: ERROR", e);
    }
    logger.exit("INFO: StudyServiceImpl - getConsentDetailsByStudyId() :: Ends");
    return consentBo;
  }

  @Override
  public ConsentInfoBo getConsentInfoById(String consentInfoId) {
    logger.entry("StudyServiceImpl - getConsentInfoById() - Starts");
    ConsentInfoBo consentInfoBo = null;
    try {
      consentInfoBo = studyDAO.getConsentInfoById(consentInfoId);
      if (consentInfoBo != null) {
        consentInfoBo.setBriefSummary(
            consentInfoBo.getBriefSummary().replaceAll("(\\r|\\n|\\r\\n)+", "&#13;&#10;"));
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getConsentInfoById() - Error", e);
    }
    logger.exit("StudyServiceImpl - getConsentInfoById() - Ends");
    return consentInfoBo;
  }

  @Override
  public List<ConsentInfoBo> getConsentInfoDetailsListByStudyId(String studyId) {
    logger.entry("INFO: StudyServiceImpl - getConsentInfoDetailsListByStudyId() :: Starts");
    List<ConsentInfoBo> consentInfoBoList = null;
    try {
      consentInfoBoList = studyDAO.getConsentInfoDetailsListByStudyId(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getConsentInfoDetailsListByStudyId() - ERROR", e);
    }
    logger.exit("INFO: StudyServiceImpl - getConsentInfoDetailsListByStudyId() :: Ends");
    return consentInfoBoList;
  }

  @Override
  public List<ConsentInfoBo> getConsentInfoList(String studyId) {
    logger.entry("StudyServiceImpl - getConsentInfoList() - Starts");
    List<ConsentInfoBo> consentInfoList = null;
    try {
      consentInfoList = studyDAO.getConsentInfoList(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getConsentInfoList() - Error", e);
    }
    logger.exit("StudyServiceImpl - getConsentInfoList() - Ends");
    return consentInfoList;
  }

  @Override
  public List<ConsentMasterInfoBo> getConsentMasterInfoList() {
    logger.entry("StudyServiceImpl - getConsentMasterInfoList() - Starts");
    List<ConsentMasterInfoBo> consentMasterInfoList = null,
        sortedConsentMasterInfoList = new ArrayList<>();

    HashMap<String, ConsentMasterInfoBo> consentMap = new HashMap<>();
    ArrayList<String> consentTitleList = null;

    try {
      consentMasterInfoList = studyDAO.getConsentMasterInfoList();

      for (ConsentMasterInfoBo consent : consentMasterInfoList) {
        consentMap.put(consent.getTitle(), consent);
      }
      consentTitleList = new ArrayList<>(consentMap.keySet());
      Collections.sort(
          consentTitleList,
          new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
              return s1.compareToIgnoreCase(s2);
            }
          });
      for (String consentTitle : consentTitleList) {
        sortedConsentMasterInfoList.add(consentMap.get(consentTitle));
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getConsentMasterInfoList() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getConsentMasterInfoList() - Ends");
    return sortedConsentMasterInfoList;
  }

  @Override
  public StudyIdBean getLiveVersion(String customStudyId) {
    logger.entry("StudyServiceImpl - getLiveVersion() - Starts");
    StudyIdBean studyIdBean = new StudyIdBean();
    try {
      studyIdBean = studyDAO.getLiveVersion(customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getLiveVersion() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getLiveVersion() - Ends");
    return studyIdBean;
  }

  @Override
  public List<StudyPageBo> getOverviewStudyPagesById(String studyId, String userId) {
    logger.entry("StudyServiceImpl - getOverviewStudyPagesById() - Starts");
    List<StudyPageBo> studyPageBos = null;
    try {
      studyPageBos = studyDAO.getOverviewStudyPagesById(studyId, userId);
      StudyBo study = getStudyInfo(studyId);
      if ((null != studyPageBos) && !studyPageBos.isEmpty()) {
        for (StudyPageBo s : studyPageBos) {
          if (FdahpStudyDesignerUtil.isNotEmpty(s.getImagePath())) {
            // to make unique image
            String path =
                FdahpStudyDesignerConstants.STUDIES
                    + FdahpStudyDesignerConstants.PATH_SEPARATOR
                    + study.getCustomStudyId()
                    + FdahpStudyDesignerConstants.PATH_SEPARATOR
                    + FdahpStudyDesignerConstants.STUDTYPAGES
                    + FdahpStudyDesignerConstants.PATH_SEPARATOR
                    + s.getImagePath();
            s.setSignedUrl(FdahpStudyDesignerUtil.getSignedUrl(path, 12));
            if (s.getImagePath().contains("?v=")) {
              String imagePathArr[] = s.getImagePath().split("\\?");
              s.setImagePath(imagePathArr[0] + "?v=" + new Date().getTime());
            } else {
              s.setImagePath(s.getImagePath() + "?v=" + new Date().getTime());
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getOverviewStudyPagesById() - ERROR ", e);
    }
    return studyPageBos;
  }

  @Override
  public Map<String, List<ReferenceTablesBo>> getreferenceListByCategory() {
    logger.entry("StudyServiceImpl - getreferenceListByCategory() - Starts");
    HashMap<String, List<ReferenceTablesBo>> referenceMap = null;
    try {
      referenceMap = studyDAO.getreferenceListByCategory();
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyList() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getreferenceListByCategory() - Ends");
    return referenceMap;
  }

  @Override
  public ResourceBO getResourceInfo(String resourceInfoId) {
    logger.entry("StudyServiceImpl - getResourceInfo() - Starts");
    ResourceBO resourceBO = null;
    try {
      resourceBO = studyDAO.getResourceInfo(resourceInfoId);
      if (null != resourceBO) {
        resourceBO.setStartDate(
            FdahpStudyDesignerUtil.isNotEmpty(resourceBO.getStartDate())
                ? String.valueOf(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        resourceBO.getStartDate(),
                        FdahpStudyDesignerConstants.DB_SDF_DATE,
                        FdahpStudyDesignerConstants.UI_SDF_DATE))
                : "");
        resourceBO.setEndDate(
            FdahpStudyDesignerUtil.isNotEmpty(resourceBO.getEndDate())
                ? String.valueOf(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        resourceBO.getEndDate(),
                        FdahpStudyDesignerConstants.DB_SDF_DATE,
                        FdahpStudyDesignerConstants.UI_SDF_DATE))
                : "");
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getResourceInfo() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getResourceInfo() - Ends");
    return resourceBO;
  }

  @Override
  public List<ResourceBO> getResourceList(String studyId) {
    logger.entry("StudyServiceImpl - getResourceList() - Starts");
    List<ResourceBO> resourceBOList = null;
    try {
      resourceBOList = studyDAO.getResourceList(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getResourceList() - Error", e);
    }
    logger.exit("StudyServiceImpl - getResourceList() - Ends");
    return resourceBOList;
  }

  @Override
  public List<NotificationBO> getSavedNotification(String studyId) {
    logger.entry("StudyServiceImpl - notificationSaved() - Starts");
    List<NotificationBO> notificationSavedList = null;
    try {
      notificationSavedList = studyDAO.getSavedNotification(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - notificationSaved() - Error", e);
    }
    logger.exit("StudyServiceImpl - resourcesSaved() - Ends");
    return notificationSavedList;
  }

  @Override
  public StudyBo getStudyById(String studyId, String userId) {
    logger.entry("StudyServiceImpl - getStudyById() - Starts");
    StudyBo studyBo = null;
    try {
      studyBo = studyDAO.getStudyById(studyId, userId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyById() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getStudyById() - Ends");
    return studyBo;
  }

  @Override
  public EligibilityBo getStudyEligibiltyByStudyId(String studyId) {
    logger.entry("StudyServiceImpl - getStudyEligibiltyByStudyId() - Starts");
    EligibilityBo eligibilityBo = null;
    try {
      eligibilityBo = studyDAO.getStudyEligibiltyByStudyId(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyEligibiltyByStudyId() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getStudyEligibiltyByStudyId() - Ends");
    return eligibilityBo;
  }

  @Override
  public List<StudyListBean> getStudyList(String userId) {
    logger.entry("StudyServiceImpl - getStudyList() - Starts");
    List<StudyListBean> studyBos = null;
    try {
      if (StringUtils.isNotEmpty(userId)) {
        studyBos = studyDAO.getStudyList(userId);
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyList() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getStudyList() - Ends");
    return studyBos;
  }

  @Override
  public List<StudyListBean> getStudyListByUserId(String userId) {
    logger.entry("StudyServiceImpl - getStudyListByUserId() - Starts");
    List<StudyListBean> studyListBeans = null;
    try {
      studyListBeans = studyDAO.getStudyListByUserId(userId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyListByUserId() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getStudyListByUserId() - Ends");
    return studyListBeans;
  }

  @Override
  public StudyBo getStudyLiveStatusByCustomId(String customStudyId) {
    logger.entry("StudyServiceImpl - getStudyLiveStatusByCustomId() - Starts");
    StudyBo studyLive = null;
    try {
      studyLive = studyDAO.getStudyLiveStatusByCustomId(customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyLiveStatusByCustomId() - Error", e);
    }
    logger.exit("StudyServiceImpl - getStudyLiveStatusByCustomId() - Ends");
    return studyLive;
  }

  @Override
  public ResourceBO getStudyProtocol(String studyId) {
    logger.entry("StudyServiceImpl - getStudyProtocol() - Starts");
    ResourceBO studyprotocol = null;
    try {
      studyprotocol = studyDAO.getStudyProtocol(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyProtocol() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getStudyProtocol() - Ends");
    return studyprotocol;
  }

  @Override
  public String markAsCompleted(
      String studyId,
      String markCompleted,
      Boolean flag,
      SessionObject sesObj,
      String customStudyId) {
    logger.entry("StudyServiceImpl - markAsCompleted() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = studyDAO.markAsCompleted(studyId, markCompleted, flag, sesObj, customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - markAsCompleted() - Error", e);
    }
    logger.exit("StudyServiceImpl - markAsCompleted() - Ends");
    return message;
  }

  @Override
  public String markAsCompleted(
      String studyId, String markCompleted, SessionObject sesObj, String customStudyId) {
    logger.entry("StudyServiceImpl - markAsCompleted() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = studyDAO.markAsCompleted(studyId, markCompleted, true, sesObj, customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - markAsCompleted() - Error", e);
    }
    logger.exit("StudyServiceImpl - markAsCompleted() - Ends");
    return message;
  }

  @Override
  public String reOrderComprehensionTestQuestion(
      String studyId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("StudyServiceImpl - reOrderComprehensionTestQuestion() - Starts");
    String message = FdahpStudyDesignerConstants.SUCCESS;
    try {
      message = studyDAO.reOrderComprehensionTestQuestion(studyId, oldOrderNumber, newOrderNumber);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - reOrderComprehensionTestQuestion() - Error", e);
    }
    logger.exit("StudyServiceImpl - reOrderComprehensionTestQuestion - Ends");
    return message;
  }

  @Override
  public String reOrderConsentInfoList(String studyId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("StudyServiceImpl - reOrderConsentInfoList() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = studyDAO.reOrderConsentInfoList(studyId, oldOrderNumber, newOrderNumber);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - reOrderConsentInfoList() - Error", e);
    }
    logger.exit("StudyServiceImpl - reOrderConsentInfoList() - Ends");
    return message;
  }

  @Override
  public String reorderEligibilityTestQusAns(
      String eligibilityId, int oldOrderNumber, int newOrderNumber, String studyId) {
    logger.entry("StudyServiceImpl - reorderEligibilityTestQusAns - Starts");
    String message = FdahpStudyDesignerConstants.SUCCESS;
    try {
      message =
          studyDAO.reorderEligibilityTestQusAns(
              eligibilityId, oldOrderNumber, newOrderNumber, studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - reorderEligibilityTestQusAns - Error", e);
    }
    logger.exit("StudyServiceImpl - reorderEligibilityTestQusAns - Ends");
    return message;
  }

  @Override
  public String reOrderResourceList(String studyId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("StudyServiceImpl - reOrderResourceList() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = studyDAO.reOrderResourceList(studyId, oldOrderNumber, newOrderNumber);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - reOrderResourceList() - Error", e);
    }
    logger.exit("StudyServiceImpl - reOrderResourceList() - Ends");
    return message;
  }

  @Override
  public boolean resetDraftStudyByCustomStudyId(String customStudy) {
    logger.entry("StudyServiceImpl - resetDraftStudyByCustomStudyId() - Starts");
    boolean flag = false;
    try {
      SessionObject object = null;
      flag =
          studyDAO.resetDraftStudyByCustomStudyId(
              customStudy, FdahpStudyDesignerConstants.RESET_STUDY, object);
      if (flag) {
        flag = studyDAO.deleteLiveStudy(customStudy);
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - resetDraftStudyByCustomStudyId() - Error", e);
    }
    logger.exit("StudyServiceImpl - resetDraftStudyByCustomStudyId() - Ends");
    return flag;
  }

  @Override
  public int resourceOrder(String studyId) {
    int count = 1;
    logger.entry("StudyServiceImpl - resourceOrder() - Starts");
    try {
      count = studyDAO.resourceOrder(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - resourceOrder() - Error", e);
    }
    logger.exit("StudyServiceImpl - resourceOrder() - Ends");
    return count;
  }

  @Override
  public List<ResourceBO> resourcesSaved(String studyId) {
    logger.entry("StudyServiceImpl - resourcesSaved() - Starts");
    List<ResourceBO> resourceBOList = null;
    try {
      resourceBOList = studyDAO.resourcesSaved(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - resourcesSaved() - Error", e);
    }
    logger.exit("StudyServiceImpl - resourcesSaved() - Ends");
    return resourceBOList;
  }

  @Override
  public List<ResourceBO> resourcesWithAnchorDate(String studyId) {
    logger.entry("StudyServiceImpl - resourcesWithAnchorDate() - Starts");
    List<ResourceBO> resourceList = null;
    try {
      resourceList = studyDAO.resourcesWithAnchorDate(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - resourcesWithAnchorDate() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - resourcesWithAnchorDate() - Ends");
    return resourceList;
  }

  @Override
  public ConsentBo saveOrCompleteConsentReviewDetails(
      ConsentBo consentBo, SessionObject sesObj, String customStudyId) {
    logger.entry("INFO: StudyServiceImpl - saveOrCompleteConsentReviewDetails() :: Starts");
    ConsentBo updateConsentBo = null;
    try {
      if (StringUtils.isNotEmpty(consentBo.getId())) {
        updateConsentBo = studyDAO.getConsentDetailsByStudyId(consentBo.getStudyId().toString());
      } else {
        updateConsentBo = new ConsentBo();
      }

      if (consentBo.getId() != null) {
        updateConsentBo.setId(consentBo.getId());
      }

      if (consentBo.getStudyId() != null) {
        updateConsentBo.setStudyId(consentBo.getStudyId());
      }

      if (consentBo.getNeedComprehensionTest() != null) {
        updateConsentBo.setNeedComprehensionTest(consentBo.getNeedComprehensionTest());
      }
      if (consentBo.getShareDataPermissions() != null) {
        updateConsentBo.setShareDataPermissions(consentBo.getShareDataPermissions());
      }

      if (consentBo.getTitle() != null) {
        updateConsentBo.setTitle(consentBo.getTitle());
      }

      if (consentBo.getTaglineDescription() != null) {
        updateConsentBo.setTaglineDescription(consentBo.getTaglineDescription());
      }

      if (consentBo.getShortDescription() != null) {
        updateConsentBo.setShortDescription(consentBo.getShortDescription());
      }

      if (consentBo.getTitle() != null) {
        updateConsentBo.setTitle(consentBo.getTitle());
      }

      if (consentBo.getLongDescription() != null) {
        updateConsentBo.setLongDescription(consentBo.getLongDescription());
      }

      if (consentBo.getLearnMoreText() != null) {
        updateConsentBo.setLearnMoreText(consentBo.getLearnMoreText());
      }

      if (consentBo.getConsentDocType() != null) {
        updateConsentBo.setConsentDocType(consentBo.getConsentDocType());
      }

      if (consentBo.getConsentDocContent() != null) {
        updateConsentBo.setConsentDocContent(consentBo.getConsentDocContent());
      }

      if (consentBo.getAllowWithoutPermission() != null) {
        updateConsentBo.setAllowWithoutPermission(consentBo.getAllowWithoutPermission());
      }

      if (consentBo.geteConsentFirstName() != null) {
        updateConsentBo.seteConsentFirstName(consentBo.geteConsentFirstName());
      }

      if (consentBo.geteConsentLastName() != null) {
        updateConsentBo.seteConsentLastName(consentBo.geteConsentLastName());
      }

      if (consentBo.geteConsentSignature() != null) {
        updateConsentBo.seteConsentSignature(consentBo.geteConsentSignature());
      }

      if (consentBo.geteConsentAgree() != null) {
        updateConsentBo.seteConsentAgree(consentBo.geteConsentAgree());
      }

      if (consentBo.geteConsentDatetime() != null) {
        updateConsentBo.seteConsentDatetime(consentBo.geteConsentDatetime());
      }

      if (consentBo.getCreatedBy() != null) {
        updateConsentBo.setCreatedBy(consentBo.getCreatedBy());
      }

      if (consentBo.getCreatedOn() != null) {
        updateConsentBo.setCreatedOn(consentBo.getCreatedOn());
      }

      if (consentBo.getModifiedBy() != null) {
        updateConsentBo.setModifiedBy(consentBo.getModifiedBy());
      }

      if (consentBo.getModifiedOn() != null) {
        updateConsentBo.setModifiedOn(consentBo.getModifiedOn());
      }

      if (consentBo.getVersion() != null) {
        updateConsentBo.setVersion(consentBo.getVersion());
      }

      if (consentBo.getType() != null) {
        updateConsentBo.setType(consentBo.getType());
      }
      if (consentBo.getComprehensionTest() != null) {
        updateConsentBo.setComprehensionTest(consentBo.getComprehensionTest());
        updateConsentBo.setComprehensionTestMinimumScore(
            consentBo.getComprehensionTestMinimumScore());
      }

      if (consentBo.getEnrollAgain() != null) {
        updateConsentBo.setEnrollAgain(consentBo.getEnrollAgain());
      }

      updateConsentBo =
          studyDAO.saveOrCompleteConsentReviewDetails(updateConsentBo, sesObj, customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrCompleteConsentReviewDetails() :: ERROR", e);
    }
    logger.exit("INFO: StudyServiceImpl - saveOrCompleteConsentReviewDetails() :: Ends");
    return updateConsentBo;
  }

  @Override
  public String saveOrDoneChecklist(
      Checklist checklist, String actionBut, SessionObject sesObj, String customStudyId) {
    String checklistId = null;
    logger.entry("StudyServiceImpl - saveOrDoneChecklist() - Starts");
    Checklist checklistBO = null;
    StudyBo studyBo = null;
    try {
      if (checklist.getChecklistId() == null) {
        studyBo = studyDAO.getStudyById(checklist.getStudyId().toString(), sesObj.getUserId());
        checklist.setCustomStudyId(studyBo.getCustomStudyId());
        checklist.setCreatedBy(sesObj.getUserId());
        checklist.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      } else {
        checklistBO = studyDAO.getchecklistInfo(checklist.getStudyId());
        checklist.setCustomStudyId(checklistBO.getCustomStudyId());
        checklist.setCreatedBy(checklistBO.getCreatedBy());
        checklist.setCreatedOn(checklistBO.getCreatedOn());
        checklist.setModifiedBy(sesObj.getUserId());
        checklist.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
      }
      checklistId = studyDAO.saveOrDoneChecklist(checklist);
      if (StringUtils.isNotEmpty(checklistId)) {
        if ("save".equalsIgnoreCase(actionBut)) {
          studyDAO.markAsCompleted(
              checklist.getStudyId(),
              FdahpStudyDesignerConstants.CHECK_LIST,
              false,
              sesObj,
              customStudyId);
        } else if ("done".equalsIgnoreCase(actionBut)) {

          studyDAO.markAsCompleted(
              checklist.getStudyId(),
              FdahpStudyDesignerConstants.CHECK_LIST,
              true,
              sesObj,
              customStudyId);
        }
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrDoneChecklist() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - saveOrDoneChecklist() - Ends");
    return checklistId;
  }

  @Override
  public ComprehensionTestQuestionBo saveOrUpdateComprehensionTestQuestion(
      ComprehensionTestQuestionBo comprehensionTestQuestionBo) {
    logger.entry("StudyServiceImpl - getComprehensionTestResponseList() - Starts");
    ComprehensionTestQuestionBo updateComprehensionTestQuestionBo = null;
    try {
      if (comprehensionTestQuestionBo != null) {
        if (StringUtils.isNotEmpty(comprehensionTestQuestionBo.getId())) {
          updateComprehensionTestQuestionBo =
              studyDAO.getComprehensionTestQuestionById(comprehensionTestQuestionBo.getId());
        } else {
          updateComprehensionTestQuestionBo = new ComprehensionTestQuestionBo();
          updateComprehensionTestQuestionBo.setActive(true);
        }
        if (comprehensionTestQuestionBo.getStatus() != null) {
          updateComprehensionTestQuestionBo.setStatus(comprehensionTestQuestionBo.getStatus());
        }
        if (comprehensionTestQuestionBo.getQuestionText() != null) {
          updateComprehensionTestQuestionBo.setQuestionText(
              comprehensionTestQuestionBo.getQuestionText());
        }
        if (comprehensionTestQuestionBo.getStudyId() != null) {
          updateComprehensionTestQuestionBo.setStudyId(comprehensionTestQuestionBo.getStudyId());
        }
        if (comprehensionTestQuestionBo.getSequenceNo() != null) {
          updateComprehensionTestQuestionBo.setSequenceNo(
              comprehensionTestQuestionBo.getSequenceNo());
        }
        if (comprehensionTestQuestionBo.getStructureOfCorrectAns() != null) {
          updateComprehensionTestQuestionBo.setStructureOfCorrectAns(
              comprehensionTestQuestionBo.getStructureOfCorrectAns());
        }
        if (comprehensionTestQuestionBo.getCreatedOn() != null) {
          updateComprehensionTestQuestionBo.setCreatedOn(
              comprehensionTestQuestionBo.getCreatedOn());
        }
        if (comprehensionTestQuestionBo.getCreatedBy() != null) {
          updateComprehensionTestQuestionBo.setCreatedBy(
              comprehensionTestQuestionBo.getCreatedBy());
        }
        if (comprehensionTestQuestionBo.getModifiedOn() != null) {
          updateComprehensionTestQuestionBo.setModifiedOn(
              comprehensionTestQuestionBo.getModifiedOn());
        }
        if (comprehensionTestQuestionBo.getModifiedBy() != null) {
          updateComprehensionTestQuestionBo.setModifiedBy(
              comprehensionTestQuestionBo.getModifiedBy());
        }
        if ((comprehensionTestQuestionBo.getResponseList() != null)
            && !comprehensionTestQuestionBo.getResponseList().isEmpty()) {
          updateComprehensionTestQuestionBo.setResponseList(
              comprehensionTestQuestionBo.getResponseList());
        }
        updateComprehensionTestQuestionBo =
            studyDAO.saveOrUpdateComprehensionTestQuestion(updateComprehensionTestQuestionBo);
      }

    } catch (Exception e) {
      logger.error("StudyServiceImpl - getComprehensionTestResponseList() - Error", e);
    }
    logger.exit("StudyServiceImpl - getComprehensionTestResponseList() - Ends");
    return updateComprehensionTestQuestionBo;
  }

  @Override
  public ConsentInfoBo saveOrUpdateConsentInfo(
      ConsentInfoBo consentInfoBo, SessionObject sessionObject, String customStudyId) {
    logger.entry("StudyServiceImpl - saveOrUpdateConsentInfo() - Starts");
    ConsentInfoBo updateConsentInfoBo = null;
    try {
      if (consentInfoBo != null) {
        if (StringUtils.isNotEmpty(consentInfoBo.getId())) {
          updateConsentInfoBo = studyDAO.getConsentInfoById(consentInfoBo.getId());
          updateConsentInfoBo.setModifiedBy(sessionObject.getUserId());
          updateConsentInfoBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        } else {
          updateConsentInfoBo = new ConsentInfoBo();
          updateConsentInfoBo.setCreatedBy(sessionObject.getUserId());
          updateConsentInfoBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          updateConsentInfoBo.setActive(true);
        }
        if (consentInfoBo.getConsentItemType() != null) {
          updateConsentInfoBo.setConsentItemType(consentInfoBo.getConsentItemType());
        }
        if (consentInfoBo.getContentType() != null) {
          updateConsentInfoBo.setContentType(consentInfoBo.getContentType());
        }
        if (consentInfoBo.getBriefSummary() != null) {
          updateConsentInfoBo.setBriefSummary(
              consentInfoBo.getBriefSummary().replaceAll("\"", "&#34;").replaceAll("\'", "&#39;"));
        }
        if (consentInfoBo.getElaborated() != null) {
          updateConsentInfoBo.setElaborated(consentInfoBo.getElaborated());
        }
        if (consentInfoBo.getHtmlContent() != null) {
          updateConsentInfoBo.setHtmlContent(consentInfoBo.getHtmlContent());
        }
        if (consentInfoBo.getUrl() != null) {
          updateConsentInfoBo.setUrl(consentInfoBo.getUrl());
        }
        if (consentInfoBo.getVisualStep() != null) {
          updateConsentInfoBo.setVisualStep(consentInfoBo.getVisualStep());
        }
        if (consentInfoBo.getSequenceNo() != null) {
          updateConsentInfoBo.setSequenceNo(consentInfoBo.getSequenceNo());
        }
        if (consentInfoBo.getStudyId() != null) {
          updateConsentInfoBo.setStudyId(consentInfoBo.getStudyId());
        }
        if (consentInfoBo.getDisplayTitle() != null) {
          updateConsentInfoBo.setDisplayTitle(consentInfoBo.getDisplayTitle());
        }
        if (consentInfoBo.getType() != null) {
          updateConsentInfoBo.setType(consentInfoBo.getType());
        }
        if (consentInfoBo.getConsentItemTitleId() != null) {
          updateConsentInfoBo.setConsentItemTitleId(consentInfoBo.getConsentItemTitleId());
        }
        updateConsentInfoBo =
            studyDAO.saveOrUpdateConsentInfo(updateConsentInfoBo, sessionObject, customStudyId);
      }

    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrUpdateConsentInfo() - Error", e);
    }
    logger.exit("StudyServiceImpl - saveOrUpdateConsentInfo() - Ends");
    return updateConsentInfoBo;
  }

  @Override
  public String saveOrUpdateEligibilityTestQusAns(
      EligibilityTestBo eligibilityTestBo,
      String studyId,
      SessionObject sessionObject,
      String customStudyId) {
    String eligibilityTestId = null;
    logger.entry("StudyServiceImpl - saveOrUpdateEligibilityTestQusAns - Starts");
    Integer seqCount = 0;
    try {
      if (eligibilityTestBo != null) {
        if (StringUtils.isEmpty(eligibilityTestBo.getId())) {
          seqCount = studyDAO.eligibilityTestOrderCount(eligibilityTestBo.getEligibilityId());
          eligibilityTestBo.setSequenceNo(seqCount);
        }
        eligibilityTestId =
            studyDAO.saveOrUpdateEligibilityTestQusAns(
                eligibilityTestBo, studyId, sessionObject, customStudyId);
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrUpdateEligibilityTestQusAns - Error", e);
    }
    logger.entry("StudyServiceImpl - saveOrUpdateEligibilityTestQusAns - Ends");
    return eligibilityTestId;
  }

  @Override
  public String saveOrUpdateOverviewStudyPages(StudyPageBean studyPageBean, SessionObject sesObj) {
    logger.entry("StudyServiceImpl - saveOrUpdateOverviewStudyPages() - Starts");
    String message = "";
    CustomMultipartFile[] customMultipart = null;
    try {
      // Resize Image

      if (studyPageBean.getMultipartFiles() != null
          && studyPageBean.getMultipartFiles().length > 0) {
        int height = 0;
        int width = 0;

        MultipartFile[] multipartFiles = studyPageBean.getMultipartFiles();
        customMultipart = new CustomMultipartFile[multipartFiles.length];

        for (int i = 0; i < multipartFiles.length; i++) {

          MultipartFile multipartFile = multipartFiles[i];

          if (i == 0) {
            width = 750;
            height = 1334;
          } else {
            width = 750;
            height = 570;
          }

          BufferedImage newBi = ImageIO.read(new ByteArrayInputStream(multipartFile.getBytes()));
          BufferedImage resizedImage = ImageUtility.resizeImage(newBi, width, height);
          String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ImageIO.write(resizedImage, extension, baos);
          baos.flush();

          CustomMultipartFile imageResizeMultipartFile =
              new CustomMultipartFile(
                  baos.toByteArray(), multipartFile.getOriginalFilename(), extension);
          customMultipart[i] = imageResizeMultipartFile;
        }
      }

      if (customMultipart != null) {
        studyPageBean.setMultipartFiles(customMultipart);
      }

      if ((studyPageBean.getMultipartFiles() != null)
          && (studyPageBean.getMultipartFiles().length > 0)) {
        String imagePath[] = new String[studyPageBean.getImagePath().length];

        StudyBo study = getStudyInfo(studyPageBean.getStudyId());

        for (int i = 0; i < studyPageBean.getMultipartFiles().length; i++) {
          String file;
          if (!studyPageBean.getMultipartFiles()[i].isEmpty()) {
            if (FdahpStudyDesignerUtil.isNotEmpty(studyPageBean.getImagePath()[i])) {
              file =
                  studyPageBean.getImagePath()[i].replace(
                      "."
                          + studyPageBean
                              .getImagePath()[i]
                              .split("\\.")[
                              studyPageBean.getImagePath()[i].split("\\.").length - 1],
                      "");
            } else {
              file =
                  FdahpStudyDesignerUtil.getStandardFileName(
                      "STUDY_PAGE_" + i,
                      studyPageBean.getUserId() + "",
                      studyPageBean.getStudyId());
            }

            imagePath[i] =
                FdahpStudyDesignerUtil.saveImage(
                    studyPageBean.getMultipartFiles()[i],
                    file,
                    FdahpStudyDesignerConstants.STUDTYPAGES,
                    study.getCustomStudyId());

          } else {
            imagePath[i] = studyPageBean.getImagePath()[i].split("\\?")[0];
          }
        }
        studyPageBean.setImagePath(imagePath);
      }
      message = studyDAO.saveOrUpdateOverviewStudyPages(studyPageBean, sesObj);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrUpdateOverviewStudyPages() - ERROR ", e);
    }
    return message;
  }

  @Override
  public String saveOrUpdateResource(ResourceBO resourceBO, SessionObject sesObj) {
    String resourseId = null;
    logger.entry("StudyServiceImpl - saveOrUpdateResource() - Starts");
    ResourceBO resourceBO2 = null;
    String fileName = "";
    String file = "";
    NotificationBO notificationBO = null;
    StudyBo studyBo = null;
    Boolean saveNotiFlag = false;
    Boolean updateResource = false;
    try {
      studyBo = studyDAO.getStudyById(resourceBO.getStudyId().toString(), sesObj.getUserId());
      if (StringUtils.isEmpty(resourceBO.getId())) {
        resourceBO2 = new ResourceBO();
        resourceBO2.setSequenceNo(resourceBO.getSequenceNo());
        resourceBO2.setStudyId(resourceBO.getStudyId());
        resourceBO2.setCreatedBy(sesObj.getUserId());
        resourceBO2.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        resourceBO2.setStatus(true);
      } else {
        resourceBO2 = getResourceInfo(resourceBO.getId());
        resourceBO2.setModifiedBy(sesObj.getUserId());
        resourceBO2.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        updateResource = true;
      }

      resourceBO2.setTitle(null != resourceBO.getTitle() ? resourceBO.getTitle().trim() : "");
      resourceBO2.setTextOrPdf(resourceBO.isTextOrPdf());
      resourceBO2.setRichText(
          null != resourceBO.getRichText()
              ? StringEscapeUtils.escapeHtml4(resourceBO.getRichText().trim())
              : "");
      if ((resourceBO.getPdfFile() != null) && !resourceBO.getPdfFile().isEmpty()) {
        file =
            FdahpStudyDesignerUtil.getStandardFileName(
                    FilenameUtils.removeExtension(resourceBO.getPdfFile().getOriginalFilename()),
                    sesObj.getFirstName(),
                    sesObj.getLastName())
                .replaceAll("\\W+", "_");

        fileName =
            FdahpStudyDesignerUtil.saveImage(
                resourceBO.getPdfFile(),
                file,
                FdahpStudyDesignerConstants.RESOURCEPDFFILES,
                studyBo.getCustomStudyId());

        resourceBO2.setPdfUrl(fileName);
        resourceBO2.setPdfName(resourceBO.getPdfFile().getOriginalFilename());
      } else {
        resourceBO2.setPdfUrl(resourceBO.getPdfUrl());
        resourceBO2.setPdfName(resourceBO.getPdfName());
      }
      resourceBO2.setxDaysSign(resourceBO.isxDaysSign());
      resourceBO2.setyDaysSign(resourceBO.isyDaysSign());
      resourceBO2.setAnchorDateId(resourceBO.getAnchorDateId());
      resourceBO2.setResourceVisibility(resourceBO.isResourceVisibility());
      resourceBO2.setResourceType(resourceBO.isResourceType());
      resourceBO2.setResourceText(
          null != resourceBO.getResourceText() ? resourceBO.getResourceText().trim() : "");
      resourceBO2.setTimePeriodFromDays(resourceBO.getTimePeriodFromDays());
      resourceBO2.setTimePeriodToDays(resourceBO.getTimePeriodToDays());
      resourceBO2.setStartDate(
          FdahpStudyDesignerUtil.isNotEmpty(resourceBO.getStartDate())
              ? String.valueOf(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      resourceBO.getStartDate(),
                      FdahpStudyDesignerConstants.UI_SDF_DATE,
                      FdahpStudyDesignerConstants.DB_SDF_DATE))
              : null);
      resourceBO2.setEndDate(
          FdahpStudyDesignerUtil.isNotEmpty(resourceBO.getEndDate())
              ? String.valueOf(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      resourceBO.getEndDate(),
                      FdahpStudyDesignerConstants.UI_SDF_DATE,
                      FdahpStudyDesignerConstants.DB_SDF_DATE))
              : null);
      resourceBO2.setAction(resourceBO.isAction());
      resourceBO2.setStudyProtocol(resourceBO.isStudyProtocol());
      resourseId = studyDAO.saveOrUpdateResource(resourceBO2);

      if (StringUtils.isNotEmpty(resourseId)) {

        if (!resourceBO2.isStudyProtocol()) {
          studyDAO.markAsCompleted(
              resourceBO2.getStudyId(),
              FdahpStudyDesignerConstants.RESOURCE,
              false,
              sesObj,
              studyBo.getCustomStudyId());
        }
        if (resourceBO.isAction()) {
          notificationBO = studyDAO.getNotificationByResourceId(resourseId);
          String notificationText = "";
          boolean notiFlag = false;
          if (null == notificationBO && !(resourceBO2.getResourceText().equals(""))) {
            notificationBO = new NotificationBO();
            notificationBO.setStudyId(resourceBO2.getStudyId());
            notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
            if (StringUtils.isNotEmpty(studyBo.getAppId())) {
              notificationBO.setAppId(studyBo.getAppId());
            }
            notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
            notificationBO.setNotificationSubType(
                FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_RESOURCE);
            notificationBO.setNotificationScheduleType(
                FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
            notificationBO.setResourceId(resourceBO2.getId());
            notificationBO.setNotificationStatus(false);
            notificationBO.setCreatedBy(sesObj.getUserId());
            notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          } else {
            notiFlag = true;
            notificationBO.setModifiedBy(sesObj.getUserId());
            notificationBO.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          }
          if (!resourceBO2.isStudyProtocol()) {
            if (resourceBO.isResourceVisibility()) {
              saveNotiFlag = true;
              notificationText = resourceBO2.getResourceText();
            } else {
              saveNotiFlag = false;
            }
          } else {
            if (studyBo.getLiveStudyBo() != null) {
              String studyName = studyBo.getName();
              String innerText;
              if (notiFlag || (!notiFlag && updateResource)) {
                innerText = "updated";
              } else {
                innerText = "added";
              }
              saveNotiFlag = true;
              notificationText =
                  "Study Protocol information has been "
                      + innerText
                      + " for the study "
                      + studyName
                      + ". Visit the app to read it now.";
            }
          }
          notificationBO.setNotificationText(notificationText);
          if (resourceBO2.isResourceType()) {
            notificationBO.setAnchorDate(true);
            notificationBO.setxDays(resourceBO2.getTimePeriodFromDays());
          } else {
            notificationBO.setAnchorDate(false);
            notificationBO.setxDays(null);
          }
          notificationBO.setScheduleDate(null);
          notificationBO.setScheduleTime(null);
          if (saveNotiFlag) {
            studyDAO.saveResourceNotification(notificationBO, notiFlag);
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrUpdateResource() - Error", e);
    }
    logger.exit("StudyServiceImpl - saveOrUpdateResource() - Ends");
    return resourseId;
  }

  @Override
  public String saveOrUpdateStudy(StudyBo studyBo, String userId, SessionObject sessionObject) {
    logger.entry("StudyServiceImpl - saveOrUpdateStudy() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = studyDAO.saveOrUpdateStudy(studyBo, sessionObject);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrUpdateStudy() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - saveOrUpdateStudy() - Ends");
    return message;
  }

  @Override
  public String saveOrUpdateStudyEligibilty(
      EligibilityBo eligibilityBo, SessionObject sesObj, String customStudyId) {
    logger.entry("StudyServiceImpl - saveOrUpdateStudyEligibilty() - Starts");
    String result = FdahpStudyDesignerConstants.FAILURE;
    try {
      result = studyDAO.saveOrUpdateStudyEligibilty(eligibilityBo, sesObj, customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrUpdateStudyEligibilty() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - saveOrUpdateStudyEligibilty() - Ends");
    return result;
  }

  @Override
  public String saveOrUpdateStudySettings(StudyBo studyBo, SessionObject sesObj) {
    logger.entry("StudyServiceImpl - saveOrUpdateStudySettings() - Starts");
    String result = FdahpStudyDesignerConstants.FAILURE;
    try {
      result = studyDAO.saveOrUpdateStudySettings(studyBo, sesObj);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - saveOrUpdateStudySettings() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - saveOrUpdateStudySettings() - Ends");
    return result;
  }

  @Autowired
  public void setStudyDAO(StudyDAO studyDAO) {
    this.studyDAO = studyDAO;
  }

  @Override
  public String updateStudyActionOnAction(String studyId, String buttonText, SessionObject sesObj) {
    logger.entry("StudyServiceImpl - updateStudyActionOnAction() - Starts");
    String message = "";
    try {
      if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(buttonText)) {
        message = studyDAO.updateStudyActionOnAction(studyId, buttonText, sesObj);
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - validateStudyAction() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - updateStudyActionOnAction() - Ends");
    return message;
  }

  @Override
  public String validateActivityComplete(String studyId, String action) {
    logger.entry("StudyServiceImpl - validateActivityComplete() - Starts");
    String message = FdahpStudyDesignerConstants.SUCCESS;
    try {
      message = studyDAO.validateActivityComplete(studyId, action);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - validateActivityComplete() - Error", e);
    }
    logger.exit("StudyServiceImpl - validateActivityComplete() - Ends");
    return message;
  }

  @Override
  public String validateEligibilityTestKey(
      String eligibilityTestId, String shortTitle, String eligibilityId) {
    logger.entry("StudyServiceImpl - validateEligibilityTestKey - Starts");
    String message = FdahpStudyDesignerConstants.SUCCESS;
    try {
      message = studyDAO.validateEligibilityTestKey(eligibilityTestId, shortTitle, eligibilityId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - validateEligibilityTestKey - Error", e);
    }
    logger.exit("StudyServiceImpl - validateEligibilityTestKey - Ends");
    return message;
  }

  @Override
  public String validateStudyAction(String studyId, String buttonText) {
    logger.entry("StudyServiceImpl - validateStudyAction() - Starts");
    String message = "";
    try {
      message = studyDAO.validateStudyAction(studyId, buttonText);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - validateStudyAction() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - validateStudyAction() - Ends");
    return message;
  }

  @Override
  public boolean validateStudyId(String studyId) {
    logger.entry("StudyServiceImpl - validateStudyId() - Starts");
    boolean flag = false;
    try {
      flag = studyDAO.validateStudyId(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - validateStudyId() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - validateStudyId() - Ends");
    return flag;
  }

  @Override
  public List<EligibilityTestBo> viewEligibilityTestQusAnsByEligibilityId(String eligibilityId) {
    logger.entry("StudyServiceImpl - viewEligibilityTestQusAnsByEligibilityId - Starts");
    List<EligibilityTestBo> eligibilityTestBos = null;
    try {
      eligibilityTestBos = studyDAO.viewEligibilityTestQusAnsByEligibilityId(eligibilityId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - viewEligibilityTestQusAnsByEligibilityId - Error", e);
    }
    logger.exit("StudyServiceImpl - viewEligibilityTestQusAnsByEligibilityId - Ends");
    return eligibilityTestBos;
  }

  @Override
  public EligibilityTestBo viewEligibilityTestQusAnsById(String eligibilityTestId) {
    logger.entry("StudyServiceImpl - viewEligibilityTestQusAnsById - Starts");
    EligibilityTestBo eligibilityTestBo = null;
    try {
      eligibilityTestBo = studyDAO.viewEligibilityTestQusAnsById(eligibilityTestId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - viewEligibilityTestQusAnsById - Error", e);
    }
    logger.exit("StudyServiceImpl - viewEligibilityTestQusAnsById - Ends");
    return eligibilityTestBo;
  }

  @Override
  public Boolean isAnchorDateExistForEnrollment(String studyId, String customStudyId) {
    logger.entry("StudyServiceImpl - isAnchorDateExistForEnrollment - Starts");
    return studyDAO.isAnchorDateExistForEnrollment(studyId, customStudyId);
  }

  @Override
  public Boolean isAnchorDateExistForEnrollmentDraftStudy(String studyId, String customStudyId) {
    logger.entry("StudyServiceImpl - isAnchorDateExistForEnrollmentDraftStudy - Starts");
    return studyDAO.isAnchorDateExistForEnrollmentDraftStudy(studyId, customStudyId);
  }

  @Override
  public boolean validateAppId(
      String customStudyId, String appId, String studyType, String dbCustomStudyId) {
    logger.entry("StudyServiceImpl - validateAppId - Starts");
    return studyDAO.validateAppId(customStudyId, appId, studyType, dbCustomStudyId);
  }

  @Override
  public StudyPermissionBO findStudyPermissionBO(String studyId, String userId) {
    logger.entry("StudyServiceImpl - findStudyPermissionBO() - Starts");
    return studyDAO.getStudyPermissionBO(studyId, userId);
  }

  @Override
  public StudyDetailsBean getStudyByLatestVersion(String customStudyId) {
    logger.entry("StudyServiceImpl - getStudyByLatestVersion - Starts");
    StudyDetailsBean studyDetails = null;
    StudyBo studyBo = null;
    String studyCatagory = "";
    Integer eligibilityType = null;
    try {
      Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
      studyBo = studyDAO.getStudyByLatestVersion(customStudyId);
      if (studyBo != null) {
        eligibilityType = studyDAO.getEligibilityType(studyBo.getId());
        studyDetails = new StudyDetailsBean();
        studyDetails.setContactEmail(studyBo.getInboxEmailAddress());
        studyDetails.setStudyId(studyBo.getCustomStudyId());
        studyDetails.setStudyTitle(studyBo.getName());
        studyDetails.setStudyVersion(studyBo.getVersion() + "");
        if (eligibilityType != null) {
          if (eligibilityType == 3) {
            studyDetails.setStudyType("OPEN");
          } else {
            studyDetails.setStudyType("CLOSE");
          }
        }
        studyDetails.setStudyStatus(studyBo.getStatus());
        if (studyBo.getCategory() != null) {
          studyCatagory = studyDAO.getStudyCategory(studyBo.getCategory());
        }
        if (StringUtils.isNotBlank(studyCatagory)) {
          studyDetails.setStudyCategory(studyCatagory);
        }
        studyDetails.setStudyTagline(studyBo.getStudyTagLine());
        studyDetails.setStudySponsor(studyBo.getResearchSponsor());
        studyDetails.setStudyEnrolling(studyBo.getEnrollingParticipants());
        studyDetails.setAppId(studyBo.getAppId());
        studyDetails.setAppName("App Name_" + studyBo.getAppId());
        studyDetails.setAppDescription("App Desc_" + studyBo.getAppId());

        studyDetails.setLogoImageUrl(
            StringUtils.isEmpty(studyBo.getThumbnailImage())
                ? propMap.get("fda.imgDisplaydPath")
                    + propMap.get("cloud.bucket.name")
                    + propMap.get(FdahpStudyDesignerConstants.FDA_SMD_STUDY_THUMBNAIL_PATH)
                    + propMap.get(FdahpStudyDesignerConstants.STUDY_BASICINFORMATION_DEFAULT_IMAGE)
                : propMap.get("fda.imgDisplaydPath")
                    + propMap.get("cloud.bucket.name")
                    + propMap.get(FdahpStudyDesignerConstants.FDA_SMD_STUDY_THUMBNAIL_PATH)
                    + studyBo.getThumbnailImage());
      }
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getStudyByLatestVersion - Error", e);
    }
    logger.exit("StudyServiceImpl - getStudyByLatestVersion - Ends");
    return studyDetails;
  }

  @Override
  public boolean validateStudyActions(String studyId) {
    logger.entry("StudyServiceImpl - validateStudyAction() - Starts");
    boolean markAsCompleted = false;
    try {
      markAsCompleted = studyDAO.validateStudyActions(studyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - validateStudyAction() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - validateStudyAction() - Ends");
    return markAsCompleted;
  }

  public StudyBo getStudyInfo(String studyId) {
    return studyDAO.getStudy(studyId);
  }

  @Override
  public List<ConsentBo> getConsentList(String customStudyId) {
    logger.entry("StudyServiceImpl - getConsentList() - Starts");
    List<ConsentBo> consentBoList = null;
    try {
      consentBoList = studyDAO.getConsentList(customStudyId);
    } catch (Exception e) {
      logger.error("StudyServiceImpl - getConsentList() - ERROR ", e);
    }
    logger.exit("StudyServiceImpl - getConsentList() - Ends");
    return consentBoList;
  }

  @Override
  public StudyBo replicateStudy(String studyId, SessionObject sessionObject) {

    StudyBo studyBo = studyDAO.getStudy(studyId);

    EligibilityBo eligibilityBo = studyDAO.getStudyEligibiltyByStudyId(studyBo.getId());

    List<ConsentBo> consentBoList = studyDAO.getConsentList(studyBo.getId());

    List<ConsentInfoBo> consentInfoBoList = studyDAO.getConsentInfoList(studyBo.getId());

    List<ComprehensionTestQuestionBo> comprehensionTestQuestionBoList =
        studyDAO.getComprehensionTestQuestionList(studyBo.getId());

    List<QuestionnaireBo> questionnairesList =
        studyQuestionnaireDAO.getStudyQuestionnairesByStudyId(studyBo.getId());

    List<NotificationBO> notificationBOs = notificationDAO.getNotificationList(studyBo.getId());

    List<ResourceBO> resourceBOs = studyDAO.getResourceList(studyBo.getId());

    // replicating study
    studyDAO.cloneStudy(studyBo, sessionObject);
    saveActiveTaskDetails(studyBo);

    if (eligibilityBo != null) {
      studyDAO.cloneEligibility(eligibilityBo, studyBo.getId());
    }

    if (CollectionUtils.isNotEmpty(consentBoList)) {
      for (ConsentBo consentBo : consentBoList) {
        studyDAO.cloneConsent(consentBo, studyBo.getId());
      }
    }

    if (CollectionUtils.isNotEmpty(consentInfoBoList)) {
      for (ConsentInfoBo consentinfoBo : consentInfoBoList) {
        studyDAO.cloneConsentInfo(consentinfoBo, studyBo.getId());
      }
    }

    if (CollectionUtils.isNotEmpty(comprehensionTestQuestionBoList)) {
      for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
          comprehensionTestQuestionBoList) {
        comprehensionTestQuestionBo.setStudyId(studyBo.getId());
        studyDAO.cloneComprehensionTest(comprehensionTestQuestionBo, studyBo.getId());
      }
    }

    if (CollectionUtils.isNotEmpty(questionnairesList)) {
      for (QuestionnaireBo questionnaireBo : questionnairesList) {
        studyQuestionnaireDAO.cloneStudyQuestionnaire(
            questionnaireBo.getId(), studyBo.getId(), sessionObject);
      }
    }

    if (CollectionUtils.isNotEmpty(resourceBOs)) {
      for (ResourceBO resourceBO : resourceBOs) {
        resourceBO.setId(null);
        resourceBO.setStudyId(studyBo.getId());
        resourceBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        studyDAO.saveOrUpdateResource(resourceBO);
      }
    }

    if (CollectionUtils.isNotEmpty(notificationBOs)) {
      for (NotificationBO notificationBO : notificationBOs) {
        notificationBO.setNotificationId(null);
        notificationBO.setStudyId(studyBo.getId());
        notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
        notificationDAO.saveNotification(notificationBO);
      }
    }
    return studyBo;
  }

  private void saveActiveTaskDetails(StudyBo studyBo) {

    List<ActiveTaskBo> activeTaskBos =
        studyActiveTasksDAO.getStudyActiveTaskByStudyId(studyBo.getId());

    List<String> activeTaskIds = new ArrayList<>();
    List<String> activeTaskTypes = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(activeTaskBos)) {
      for (ActiveTaskBo activeTaskBo : activeTaskBos) {
        activeTaskIds.add(activeTaskBo.getId());
        activeTaskTypes.add(activeTaskBo.getTaskTypeId());
      }
    }
    List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos =
        studyActiveTasksDAO.getActiveTaskAtrributeValuesByActiveTaskId(activeTaskIds);

    List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBoList =
        studyActiveTasksDAO.getActiveTaskCustomScheduleBoList(activeTaskIds);

    List<ActiveTaskFrequencyBo> activeTaskFrequencyBoList =
        studyActiveTasksDAO.getActiveTaskFrequencyBoList(activeTaskIds);

    if (CollectionUtils.isNotEmpty(activeTaskBos)) {
      for (ActiveTaskBo activeTask : activeTaskBos) {
        String oldActiveTaskId = activeTask.getId();
        activeTask.setId(null);
        activeTask.setStudyId(studyBo.getId());
        studyDAO.saveStudyActiveTask(activeTask);

        for (ActiveTaskAtrributeValuesBo active : activeTaskAtrributeValuesBos) {
          if (active.getActiveTaskId().equals(oldActiveTaskId)) {
            active.setAttributeValueId(null);
            active.setActiveTaskId(activeTask.getId());
            studyDAO.saveActiveTaskAtrributeValuesBo(active);
          }
        }
        for (ActiveTaskCustomScheduleBo active : activeTaskCustomScheduleBoList) {
          if (active.getActiveTaskId().equals(oldActiveTaskId)) {
            active.setId(null);
            active.setActiveTaskId(activeTask.getId());
            studyDAO.saveActiveTaskCustomScheduleBo(active);
          }
        }
        for (ActiveTaskFrequencyBo active : activeTaskFrequencyBoList) {
          if (active.getActiveTaskId().equals(oldActiveTaskId)) {
            active.setId(null);
            active.setActiveTaskId(activeTask.getId());
            studyDAO.saveActiveTaskFrequencyBo(active);
          }
        }
      }
    }
  }
}
