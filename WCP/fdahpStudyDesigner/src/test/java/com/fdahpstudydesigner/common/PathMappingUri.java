/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

public enum PathMappingUri {
  ACTIVATE_OR_DEACTIVATE_USER("/adminUsersEdit/activateOrDeactivateUser.do"),

  SESSION_OUT("/sessionOut.do"),

  ACTIVE_TASK_MARK_AS_COMPLETED("/adminStudies/activeTAskMarkAsCompleted.do"),

  SAVE_OR_UPDATE_ACTIVE_TASK_CONTENT("/adminStudies/saveOrUpdateActiveTaskContent.do"),

  DELETE_ACTIVE_TASK("/adminStudies/deleteActiveTask.do"),

  VIEW_NOTIFICATION_LIST("/adminNotificationView/viewNotificationList.do"),

  SAVE_OR_UPDATE_NOTIFICATION("/adminNotificationEdit/saveOrUpdateNotification.do"),

  SAVE_OR_UPDATE_STUDY_NOTIFICATION("/adminStudies/saveOrUpdateStudyNotification.do"),

  CHANGE_PASSWORD("/adminDashboard/changePassword.do"),

  UPDATE_PROFILE_DETAILS("/adminDashboard/updateUserDetails.do"),

  VIEW_USER_DETAILS("/adminDashboard/viewUserDetails.do"),

  DELETE_QUESTION_FORM_INFO("/adminStudies/deleteFormQuestion.do"),

  SAVE_OR_UPDATE_QUETIONNAIR_SCHEDULE("/adminStudies/saveorUpdateQuestionnaireSchedule.do"),

  DELETE_QUESTIONNAIRES("/adminStudies/deleteQuestionnaire.do"),
  CONSENT_MARKED_AS_COMPLETE("/adminStudies/consentMarkAsCompleted.do"),

  NOTIFICATION_MARK_AS_COMPLETED("/adminStudies/notificationMarkAsCompleted.do"),

  QUESTIONAIRE_MARK_AS_COMPLETED("/adminStudies/questionnaireMarkAsCompleted.do"),

  RESOURCE_MARK_AS_COMPLETED("/adminStudies/resourceMarkAsCompleted.do"),

  VIEW_STUDY_DETAILS("/adminStudies/viewStudyDetails.do"),

  UPDATE_STUDY_ACTION("/adminStudies/updateStudyAction"),

  SAVE_OR_UPDATE_SETTINGS_AND_ADMINS("/adminStudies/saveOrUpdateSettingAndAdmins.do");

  private final String path;

  private PathMappingUri(String path) {
    this.path = path;
  }

  public String getPath() {
    return this.path;
  }
}
