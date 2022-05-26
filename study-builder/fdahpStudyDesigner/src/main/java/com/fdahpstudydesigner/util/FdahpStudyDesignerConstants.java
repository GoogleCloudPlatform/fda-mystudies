/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.fdahpstudydesigner.util;

public class FdahpStudyDesignerConstants {

  public static final String ACTION = "action";

  public static final String ACTION_DEACTIVATE = "deactivateId";
  public static final String ACTION_DEACTIVATE_SUCCESS_MSG =
      "The study has been successfully deactivated";

  public static final String ACTION_LUNCH = "lunchId";
  public static final String ACTION_LUNCH_SUCCESS_MSG =
      "The study has been launched and is now available to participants in the mobile applications";
  public static final String ACTION_ON = "actionOn";
  public static final String ACTION_PAGE = "actionPage";
  public static final String ACTION_PAUSE = "pauseId";
  public static final String ACTION_PAUSE_SUCCESS_MSG = "The study has been paused";
  // Action buttons
  public static final String ACTION_PUBLISH = "publishId";
  // Action success Messages
  public static final String ACTION_PUBLISH_SUCCESS_MSG =
      "The study has been published as an upcoming study.";
  public static final String ACTION_RESUME = "resumeId";
  public static final String ACTION_RESUME_SUCCESS_MSG = "The study has been resumed";

  public static final String ACTION_SUC_MSG = "actionSucMsg";
  public static final String ACTION_TYPE = "actionType";

  public static final String ACTION_TYPE_COMPLETE = "complete";
  // action type
  public static final String ACTION_TYPE_SAVE = "save";
  public static final String ACTION_TYPE_UPDATE = "update";
  public static final String ACTION_UNPUBLISH = "unpublishId";
  public static final String ACTION_UNPUBLISH_SUCCESS_MSG =
      "The study has been removed from the list of upcoming studies.";
  public static final String ACTION_UPDATES = "updatesId";
  public static final String ACTION_UPDATES_SUCCESS_MSG = "Study updates have been published";

  public static final String ACTIVE = "active";
  public static final int ACTIVE_STATUS = 1;

  public static final String ACTIVE_TASK_STUDY_ID = "activetaskStudyId";
  public static final String ACTIVEANDQUESSIONAIREEMPTY_ERROR_MSG =
      "The study must have at least one questionnaire or active task added before attempting this action.";

  public static final String ACTIVETASK_DATE_ERROR_MSG =
      "One or more of the study's activities are scheduled for dates that are in the past. Please configure activity schedules to have upcoming dates and try again.";
  public static final String ACTIVETASK_LIST = "activeTaskList";
  public static final String ACTIVITY_MESSAGE = "activityMsg";
  public static final String ACTIVITY_STUDY_ID = "activityStudyId";
  public static final String ACTIVITY_TYPE_ACTIVETASK = "Activetask";
  public static final String ACTIVITY_TYPE_QUESTIONNAIRE = "Questionnaire";
  public static final String ADD_PAGE = "ADD_PAGE";

  public static final String ADDORCOPY = "addOrCopy";
  public static final String ALLCHARACTERS = "allcharacters";
  public static final String ALLOW = "allow";
  public static final String ALPHABETS = "alphabets";

  public static final String ALPHABETSANDNUMBERS = "alphabetsandnumbers";

  public static final String ANDROID = "A";
  public static final String APPLICATION_JSON = "application/json";
  public static final String BASICINFO_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String BUTTON_TEXT = "buttonText";
  public static final String CHECK_LIST = "checkList";
  public static final String CHECKLIST_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String CHKREFRESHFLAG = "chkRefreshflag";
  public static final String COMPLETE_STUDY_SUCCESS_MESSAGE = "complete.study.success.message";

  public static final String COMPLETED_BUTTON = "completed";
  public static final String COMPREHENSION_LIST_PAGE = "comprehensionListPage";
  public static final String COMPREHENSION_QUESTION_ID = "comprehensionQuestionId";

  public static final String COMPREHENSION_TEST = "comprehenstionTest";
  public static final String COMPREHENSIONTEST_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String CONESENT = "consent";

  public static final String CONESENT_REVIEW = "consentreview";
  public static final String CONSENT_ID = "consentId";
  public static final String CONSENT_INFO_ID = "consentInfoId";
  public static final String CONSENT_INFO_LIST = "consentInfoList";

  public static final String CONSENT_INFO_LIST_PAGE = "consentInfoListPage";
  public static final String CONSENT_INFO_PAGE = "consentInfoPage";
  public static final String CONSENT_STUDY_ID = "consentStudyId";
  public static final String CONSENT_TYPE_CUSTOM = "Custom";
  // Consent related constants
  public static final String CONSENT_TYPE_RESEARCHKIT = "ResearchKit";
  public static final String CONSENTEDUINFO_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String CONTENT = "content";
  public static final String COPY_STUDY = "copy";
  public static final String COPY_STUDY_FAILURE_MSG =
      "An error occurred and the copy operation could not be completed.";
  public static final String COPY_STUDY_SUCCESS_MSG =
      "A new study has been created by replicating the existing study.";

  public static final String CURRENT_PAGE = "currentPage";
  public static final String CUSTOM_STUDY_ID = "customStudyId";
  public static final String DATE = "Date";

  public static final String DATE_TIME = "Date-Time";
  public static final String DAYS_OF_THE_CURRENT_MONTH = "Days of the current month";

  // task time range options
  public static final String DAYS_OF_THE_CURRENT_WEEK = "Days of the current week";
  public static final String DB_SDF_DATE = "yyyy-MM-dd";

  public static final String DB_SDF_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String DB_SDF_TIME = "HH:mm:ss";
  public static final String DEACTIVE = "deactive";

  public static final int DEACTIVE_STATUS = 0;
  public static final String DEFAULT = "default";
  // Study permission del Falg
  public static final Integer DEL_STUDY_PERMISSION_ACTIVE = 1;

  public static final Integer DEL_STUDY_PERMISSION_INACTIVE = 0;
  public static final String DISALLOW = "disallow";
  public static final String DRAFT_ACTIVETASK = "draftActivetask";
  public static final String DRAFT_ACTIVITY = "draftActivity";
  public static final String DRAFT_CONSCENT = "draftConscent";

  // Update version draft update flag
  public static final String DRAFT_STUDY = "draftStudy";

  public static final String ECONSENT_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String EDIT_PAGE = "EDIT_PAGE";
  public static final String ELIGIBILITY_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";

  public static final String ELIGIBILITY_ID = "eligibilityId";

  public static final Integer ELIGIBILITY_TEST = 3;
  public static final String ELIGIBILITY_TOKEN_TEXT_DEFAULT =
      "Participating in this study requires an invitation. If you have received one, please enter the token provided.";
  public static final String ENCRYPT_SALT = "StudieGatewayApp";
  public static final String ERR_MSG = "errMsg";
  public static final String FAILURE = "FAILURE";
  public static final String FAILURE_UPDATE_STUDY_MESSAGE =
      "Sorry, a technical error occurred and the update(s) could not be published.";
  public static final String FDA_ENV_LOCAL = "local";
  public static final String FDA_ENV_PROD = "prod";
  public static final String FDA_ENV_UATL = "uat";

  public static final String FDA_SALT = "BTCSoft";
  public static final String FETAL_KICK_COUNTER = "Fetal kick counter";

  public static final String FORM_ID = "formId";
  public static final String FORM_STEP = "Form";
  public static final String FORM_STEP_IMAGE = "FormImage";
  public static final String FORM_STEP_SELECTEDIMAGE = "FormSelectedImage";
  public static final String FORMSTEP_ACTIVITY = "Form step";
  public static final String FORMSTEP_DELETED = "Form step deleted from questionnaire";
  public static final String FORMSTEP_DONE = "Form step marked complete";
  public static final String FORMSTEP_QUESTION_ACTIVITY = "Form step question";

  public static final String FORMSTEP_QUESTION_DELETED = "Question deleted from form step";
  public static final String FORMSTEP_QUESTION_DONE = "Question step marked complete";
  public static final String FORMSTEP_QUESTION_SAVED = "Question step content saved";
  public static final String DRAFT_QUESTIONNAIRE = "draftQuestionnaire";

  public static final String FORMSTEP_SAVED = "Form step content saved";
  public static final String FREQUENCY_TYPE_DAILY = "Daily";
  public static final String FREQUENCY_TYPE_MANUALLY_SCHEDULE = "Manually Schedule";
  public static final String FREQUENCY_TYPE_MONTHLY = "Monthly";
  // questionaire frequency schedule
  public static final String FREQUENCY_TYPE_ONE_TIME = "One time";
  public static final String FREQUENCY_TYPE_WEEKLY = "Weekly";

  public static final String FREQUENCY_TYPE_WITHIN_A_DAY = "Within a day";
  public static final String GATEWAYLEVEL = "Gateway level";

  public static final String GET_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
  public static final String HR_SDF_TIME = "HH";
  public static final Integer ID_VALIDATION_AND_ELIGIBILITY_TEST = 2;

  // Study eligibility mechanism
  public static final Integer ID_VALIDATION_ONLY = 1;

  public static final String IMG_DEFAULT = "imgDefault";
  public static final String IMP_VALUE = "impValue";
  public static final String INPUT_TIME = "HH:mm:ss";
  public static final String INSTRUCTION_ACTIVITY = "Instruction step";
  public static final String INSTRUCTION_ADDED_SUCCESSFULLY =
      "Instruction step added to questionnaire";
  public static final String INSTRUCTION_DONE = "Instruction step marked complete";
  public static final String INSTRUCTION_ID = "instructionId";

  public static final String INSTRUCTION_SAVED = "Instruction step content saved";
  public static final String INSTRUCTION_STEP = "Instruction";
  public static final String INSTRUCTION_UPDATED_SUCCESSFULLY = "Instruction step updated";
  public static final String INSTRUCTIONSTEP_DELETED = "Instruction step deleted";
  public static final String IOS = "I";
  public static final String IS_LIVE = "isLive";
  public static final String IS_STUDY_PROTOCOL = "isstudyProtocol";
  public static final String LOGOUPLOAD = "LOGOUPLOAD";
  public static final String LOGOUT_LOGIN_USER = "logout_login_user";
  public static final String LUNCH_CHECKLIST_ERROR_MSG =
      "The Checklist section does not have all items marked as completed. Do you want to proceed?";
  public static final String LUNCH_ENROLLMENT_ERROR_MSG =
      "set \"Allow participants to enroll?\" to \"Yes\"";
  public static final String MAILFAILURE = "MAILFAILURE";
  public static final String MARK_AS_COMPLETE_DONE_ERROR_MSG =
      "One or more items in this section need to be marked as complete to proceed with this action.";
  public static final String MESSAGE = "message";
  public static final String MONTHS_OF_THE_CURRENT_YEAR = "Months of the current year";
  public static final String MULTIPLE_TIMES_A_DAY = "24 hours of current day";
  public static final String NEW_ORDER_NUMBER = "newOrderNumber";
  public static final String NO = "No";
  public static final String NOTIFICATION = "notification";
  public static final String NOTIFICATION_ACTIVETASK_TEXT =
      "A new activity $shortTitle has been added for the study: $customId.";
  public static final String NOTIFICATION_ACTIVETASK_TEXT_PUBLISH =
      "1 or more activities have been added for the study: $customId.";
  public static final String NOTIFICATION_DEACTIVATE_TEXT =
      "The study $customId has been closed. We thank you for your participation.";
  public static final String NOTIFICATION_ERROR_MSG =
      "One or more of the study's notifications are scheduled for dates that are in the past. Please configure notification schedules to have upcoming dates and try again.";
  public static final String NOTIFICATION_GT = "GT";
  public static final String NOTIFICATION_IMMEDIATE = "immediate";

  public static final String NOTIFICATION_NOT_VIEWED = "N";
  public static final String NOTIFICATION_NOTIMMEDIATE = "notImmediate";
  public static final String NOTIFICATION_PAUSE_TEXT =
      "The study $customId has been paused. We will notify you when it is resumed.";
  public static final String NOTIFICATION_RESUME_TEXT =
      "The study $customId has been resumed. Visit the study to start participating in activities again.";
  public static final String NOTIFICATION_ST = "ST";
  public static final String NOTIFICATION_SUBTYPE_ACTIVITY = "Activity";
  public static final String NOTIFICATION_SUBTYPE_ANNOUNCEMENT = "Announcement";
  public static final String NOTIFICATION_SUBTYPE_RESOURCE = "Resource";

  public static final String NOTIFICATION_UPCOMING_OR_ACTIVE_TEXT =
      "A new study is available in the app.";
  public static final String NOTIFICATION_VIEWED = "Y";
  // Notification flags
  public static final String NOTIFICATIONID = "notificationId";
  public static final String NUMBERS = "numbers";
  public static final String OLD_ORDER_NUMBER = "oldOrderNumber";
  public static final String OVERVIEW_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";

  public static final String OVERVIEW_STUDY_PAGE = "overviewStudyPage";
  public static final String PASS_FAIL_ACTIVITY_DEATILS_MESSAGE = "Invalid password";

  public static final String PASS_FAIL_ACTIVITY_MESSAGE = "Password is not valid";
  public static final String PENDING_TO_DEACTIVATE = "pending";
  public static final String PERMISSION = "permission";

  public static final String PLATFORM_ACTIVETASK_ERROR_MSG_ANDROID =
      "One or more active tasks that you have selected are not supported by the mobile platform that is configured in the Settings section.";
  public static final String PLATFORM_ERROR_MSG_ANDROID =
      "One or more questionnaires have questions with response types that are not supported by the mobile platform that is configured in the Settings section.";
  public static final String PRE_PUBLISH_ENROLLMENT_ERROR_MSG =
      "You must set \"Allow participants to enroll?\" to \"No\" in the Settings section to publish your study as an upcoming study.";
  public static final String PUBLISH_ENROLLMENT_ERROR_MSG =
      "You must set \"Allow participants to enroll?\" to \"Yes\" in the Settings section to publish updates to your study.";
  public static final String PUBLISH_UPDATE_CHECKLIST_ERROR_MSG =
      "The Checklist section does not have all items marked complete. Do you want to proceed?";

  public static final String PW_DATE_FORMAT = "MMddyy";
  public static final String QUESTION_ID = "questionId";
  public static final String QUESTION_STEP = "Question";
  public static final String QUESTION_STEP_IMAGE = "QuestionImage";
  public static final String QUESTION_STEP_SELECTEDIMAGE = "QuestionSelectedImage";

  public static final String QUESTIONNAIRE = "questionnaire";
  public static final String QUESTIONNAIRE_ACTIVITY = "Questionnaire";
  public static final String QUESTIONNAIRE_CREATED = "Questionnaire created";
  public static final String QUESTIONNAIRE_DELETED = "Questionnaire deleted";
  public static final String QUESTIONNAIRE_LIST = "questionnaireList";
  public static final String QUESTIONNAIRELIST_MARKED_AS_COMPLETED =
      "This section has been marked as complete";
  public static final String QUESTIONNARIE_STUDY_ID = "questionnarieStudyId";
  public static final String QUESTIONNARIES_ERROR_MSG =
      "One or more of the study's activities are scheduled for dates that are in the past. Please configure activity schedules to have upcoming dates and try again.";
  public static final String QUESTIONSTEP_ACTIVITY = "Question step";
  public static final String QUESTIONSTEP_DELETED = "Question step deleted";
  public static final String QUESTIONSTEP_DONE = "Question step marked complete";
  public static final String QUESTIONSTEP_SAVED = "Question step saved";
  public static final String REDIRECT_SESSION_PARAM_NAME = "sessionUserId=";
  public static final String REFERENCE_TYPE_CATEGORIES = "Categories";
  public static final String REFERENCE_TYPE_RESEARCH_SPONSORS = "Research sponsors";
  public static final String REQUIRED_DATE_TIME = "MM/dd/yyyy HH:mm";
  public static final String REQUIRED_DATE_TIME_FOR_DATE_DIFF = "MM/dd/yyyy HH:mm:ss";
  public static final String REQUIRED_TIME = "HH:mm";
  public static final String RESEND = "resend";
  public static final String RESET_STUDY = "reset";
  public static final String RESOURCE = "resource";
  public static final String RESOURCE_ANCHOR_ERROR_MSG =
      "One or more resources has a period of visibility that uses anchor date. However, there is no anchor date set for the study yet. Please visit the Questionnaires section, select a question for the anchor date and then try again.";
  // Action Failure message
  public static final String RESOURCE_DATE_ERROR_MSG =
      "One or more study resources has a visibility period with a start date that has already occurred. Please adjust the date and try again.";
  public static final String RESOURCE_INFO_ID = "resourceInfoId";
  public static final String RESOURCEPDFFILES = "studyResources";
  public static final String RESOURCES_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String RESUME_CHECKLIST_ERROR_MSG =
      "The Checklist section does not have all items marked complete. Do you want to proceed?";
  public static final Integer ROLE_CREATE_MANAGE_STUDIES = 8;
  public static final Integer ROLE_MANAGE_REPO = 3;
  public static final Integer ROLE_MANAGE_STUDIES = 2;
  public static final Integer ROLE_MANAGE_USERS_EDIT = 5;
  public static final Integer ROLE_MANAGE_USERS_VIEW = 7;
  public static final Integer ROLE_SUPERADMIN = 1;
  public static final String RUN_BASED = "Run-based";
  // Button Name
  public static final String SAVE_BUTTON = "save";
  public static final String SAVE_STUDY_SUCCESS_MESSAGE = "save.study.success.message";
  public static final String SCHEDULE = "schedule";
  public static final String SCHEDULE_ERROR_MSG =
      "Activity run duration must be greater than fetal kick record time duration.";

  public static final String SD_DATE_FORMAT = "yyyy-MM-dd";
  public static final String SDF_FILE_NAME_TIMESTAMP = "MMddyyyyHHmmss";

  public static final String SDF_TIME = "h:mm a";
  public static final String SELECTEDNOTIFICATIONPAST = "SELECTEDNOTIFICATIONPAST";
  // steps
  public static final Integer SEQUENCE_NO_1 = 1;
  public static final Integer SEQUENCE_NO_10 = 10;
  public static final Integer SEQUENCE_NO_2 = 2;
  public static final Integer SEQUENCE_NO_3 = 3;
  public static final Integer SEQUENCE_NO_4 = 4;
  public static final Integer SEQUENCE_NO_5 = 5;
  public static final Integer SEQUENCE_NO_6 = 6;
  public static final Integer SEQUENCE_NO_7 = 7;
  public static final Integer SEQUENCE_NO_8 = 8;
  public static final Integer SEQUENCE_NO_9 = 9;

  public static final String SERVER_TIME_ZONE = "America/New_York";
  public static final String SESSION_OBJECT = "sessionObject";
  public static final String SETTING_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String SHORT_NAME_STATISTIC = "identifierNameStat";
  // Active task attribute name
  public static final String SHORT_TITLE = "shortTitle";

  public static final String SPATIAL_SPAN_MEMORY = "Spatial span memory";
  public static final String SPECIALCHARACTERS = "specialcharacters";
  public static final String STAT_INFO_LIST = "statisticsInfoList";
  public static final boolean STATUS_ACTIVE = true;

  public static final boolean STATUS_EXPIRED = false;
  // Folder Name
  public static final String STUDTYLOGO = "studylogo";
  public static final String STUDTYPAGES = "studypages";
  public static final String STUDY_ACTIVE = "Active";

  public static final String STUDY_BO = "studyBo";
  public static final String STUDY_DEACTIVATED = "Deactivated";
  public static final String STUDY_EVENT = "studyEvent";
  public static final String STUDY_ID = "studyId";

  public static final String STUDY_LIST_BY_ID = "StudyBo.getStudiesById";
  public static final String STUDY_PAUSED = "Paused";
  // supported ios
  public static final String STUDY_PLATFORM_TYPE_ANDROID = "A"; // platform
  // supported
  // android
  public static final String STUDY_PLATFORM_TYPE_IOS = "I"; // platform

  // supported both ios and android
  public static final String STUDY_PLATFORM_TYPE_IOS_ANDROID = "I,A";

  // Study Status
  public static final String STUDY_PRE_LAUNCH = "Pre-launch";
  public static final String STUDY_PRE_PUBLISH = "Pre-launch(Published)";
  public static final String STUDY_PROTOCOL = "studyProtocol";
  public static final String STUDY_SEQUENCE_BY_ID = "getStudySequenceByStudyId";

  public static final String STUDY_TYPE_GT = "GT"; // study type gateway

  public static final String STUDY_TYPE_SD = "SD"; // study type standalone
  public static final String STUDYEXCACTIVETASK_ERROR_MSG =
      "One or more of the study's sections are not marked as completed. Please complete all the sections and try again.";
  public static final String STUDYEXCQUESTIONNARIES_ERROR_MSG =
      "Required sections are not marked as Completed";
  public static final String STUDYLEVEL = "Study level";
  public static final String SUC_MSG = "sucMsg";
  public static final String SUCCESS = "SUCCESS";

  public static final String TOWER_OF_HANOI = "Tower of hanoi";
  public static final String UI_DISPLAY_DATE = "EEE, MMM dd, yyyy";
  public static final String UI_SDF_DATE = "MM/dd/yyyy";
  public static final String UI_SDF_DATE_FORMAT = "dd/MM/yyyy";

  public static final String UI_SDF_DATE_TIME = "MM-dd-yyyy HH:mm";
  public static final String UI_SDF_DATE_TIME_AMPM = "MM-dd-yyyy h:mm a";

  public static final String UI_SDF_TIME = "HH:mm";
  public static final String UNABLE_TO_MARK_AS_COMPLETE =
      "The section could not be marked complete.Please try again.";
  public static final String USER_EMAIL_FAIL_ACTIVITY_DEATILS_MESSAGE = "Invalid email";
  public static final String USER_EMAIL_FAIL_ACTIVITY_MESSAGE = "Invalid email";
  public static final String USER_LOCKED_ACTIVITY_DEATILS_MESSAGE =
      "User account for user &name has been temporarily locked";
  // Audit log messages
  public static final String USER_LOCKED_ACTIVITY_MESSAGE = "User account temporarily locked";
  public static final String VIEW_PAGE = "VIEW_PAGE";
  public static final String VIEW_SETTING_AND_ADMINS = "viewSettingAndAdmins";
  public static final String WARNING = "WARNING";
  public static final String WEEKS_OF_THE_CURRENT_MONTH = "Weeks of the current month";

  public static final String YES = "Yes";

  public static final String ANCHOR_TYPE_ENROLLMENTDATE = "Enrollment date";
  public static final String SCHEDULETYPE_REGULAR = "Regular";
  public static final String SCHEDULETYPE_ANCHORDATE = "AnchorDate";
  public static final String ANCHOR_ERROR_MSG =
      "One or more activity or resources has a period of visibility that uses anchor date. However, there is no anchor date set for the study yet. Please visit the Questionnaires section, select a question for the anchor date and then try again.";

  public static final String ROLE_SUPERADMIN_NAME = "ROLE_SUPERADMIN";
  public static final String ROLE_MANAGE_USERS_EDIT_NAME = "ROLE_MANAGE_USERS_EDIT";
  public static final String ROLE_MANAGE_USERS_VIEW_NAME = "ROLE_MANAGE_USERS_VIEW";

  public static final String FDA_SMD_STUDY_THUMBNAIL_PATH = "fda.smd.study.thumbnailPath";
  public static final String FDA_SMD_STUDY_PAGE_PATH = "fda.smd.study.pagePath";

  public static final String STUDY_BASICINFORMATION_DEFAULT_IMAGE =
      "study.basicInformation.defaultImage";

  public static final String ADD = "add";

  public static final String SUPER_ADMIN_PERMISSIONS =
      "ROLE_MANAGE_STUDIES,ROLE_CREATE_MANAGE_STUDIES,ROLE_SUPERADMIN,ROLE_MANAGE_USERS_EDIT,ROLE_MANAGE_USERS_VIEW,ROLE_MANAGE_APPS,ROLE_CREATE_MANAGE_APPS";

  public static final String PATH_SEPARATOR = "/";

  public static final String STUDIES = "studies";

  public static final String DEFAULT_IMAGES = "defaultImages";

  public static final String EXPORT_SUCCESS_MSG = "Study exported successfully";

  public static final String IMPORT_SUCCESS_MSG = "Study imported successfully";

  public static final String STUDY_REPLICATTE_SUCCESS_MSG = "Study replicated successfully";

  public static final String IMPORT_FAILURE_MSG = "Import failed";

  public static final String EXPORT_FAILURE_MSG = "Export failed. Please try again later.";

  public static final String STUDY_REPLICATTE_FAILURE_MSG =
      "Study replication failed. Please try again later.";

  public static final String INVALID_URL = "Please enter a valid URL";

  public static final String FAILURE_EXPORT_STUDY_MESSAGE =
      "Sorry, a technical error occurred and the study could not be exported.";

  public static final String IMPORT_FAILED_DUE_TO_ANOMOLIES_DETECTED_IN_FILLE =
      "Import failed due to anomalies detected in the exported study file. Please try again with a new signed URL generated at the point of export in the origin Study Builder.";

  public static final String IMPORT_FAILED_DUE_TO_INCOMPATIBLE_VERSION =
      "Import failed. Studies can only be imported from compatible Study Builder applications (running a platform release version of 2.0.6 or higher, and lower than or equal to the destination platform version  which is";

  public static final String IMPORT_FAILED_DUE_TO_ALREADY_USED_URL =
      "The URL has already been used to import a study into this Study Builder. Use the copy-into-new action to replicate the study or use a new URL generated at the point of study export.";

  public static final String STUDY_PRE_LUNCH_VERSION = "0.0";

  public static final String NA = "NA";

  public static final String DEVICE_ANDROID = "android";

  public static final String DEVICE_IOS = "ios";

  public static final String PUBLISHED_VERSION = "publishedVersion";

  public static final String WORKING_VERSION = "workingVersion";

  public static final String APP_ID = "appId";
  public static final String APP_BO = "appBo";

  public static final String CUSTOM_APP_ID = "customAppId";

  public static final String APP_DEACTIVATED = "Deactivated";

  public static final String FAILURE_APP_MESSAGE =
      "Sorry, a technical error occurred and the update(s) could not be published.";

  public static final String FAILURE_DEACTIVATE_APP_MESSAGE =
      "Sorry, a technical error occurred and App deactivation failed";

  public static final Integer ROLE_MANAGE_APPS = 10;

  public static final Integer ROLE_CREATE_MANAGE_APPS = 9;

  public static final String VIEW_ASSOCIATED_STUDIES_MESSAGE =
      "Studies seen in the list are based on the permissions you have in the Study Builder";

  private FdahpStudyDesignerConstants() {
    // Do nothing
  }
}
