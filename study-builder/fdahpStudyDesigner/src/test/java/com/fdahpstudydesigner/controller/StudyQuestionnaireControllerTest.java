/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTIONNAIRE_DELETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTION_STEP_IN_FORM_DELETED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.PathMappingUri;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.HashMap;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class StudyQuestionnaireControllerTest extends BaseMockIT {

  @Test
  public void shouldDeleteStudyQuestionInForm() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.DELETE_QUESTION_FORM_INFO.getPath())
                .headers(headers)
                .param("formId", "58")
                .param("questionId", "85199")
                .param("stepShortTitle", "short title")
                .param("questionnairesId", "1")
                .sessionAttr(CUSTOM_STUDY_ID_ATTR_NAME, "OpenStudy003")
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_QUESTION_STEP_IN_FORM_DELETED);
  }

  @Test
  public void shouldDeleteQuestionnaire() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(PathMappingUri.DELETE_QUESTIONNAIRES.getPath())
                .headers(headers)
                .param("studyId", "678574")
                .param("questionnaireId", "3")
                .sessionAttr(CUSTOM_STUDY_ID_ATTR_NAME, "OpenStudy002")
                .sessionAttrs(getSessionAttributes()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(STUDY_QUESTIONNAIRE_DELETED);
  }

  @Test
  public void shouldSaveOrUpdateStudyActiveTask() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject sessionObj = getSessionObject();
    sessionObj.setUserId("1");
    HashMap<String, Object> sessionAttributes = new HashMap<String, Object>();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, sessionObj);

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_QUETIONNAIR_SCHEDULE.getPath())
            .headers(headers)
            .sessionAttr(CUSTOM_STUDY_ID_ATTR_NAME, "OpenStudy003")
            .sessionAttr(STUDY_ID_ATTR_NAME, "678599")
            .sessionAttrs(sessionAttributes);

    QuestionnaireBo questionnaireBo = new QuestionnaireBo();
    questionnaireBo.setId("2");
    questionnaireBo.setQuestionnaireCustomScheduleBo(null);
    questionnaireBo.setQuestionnairesFrequenciesBo(null);
    questionnaireBo.setQuestionnairesFrequenciesList(null);

    addParams(requestBuilder, questionnaireBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewStudyQuestionnaires.do"));

    verifyAuditEventCall(STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE);
  }

  @Test
  public void shouldReturnStudyActiveTaskMarkAsCompleted() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    MockHttpServletRequestBuilder requestBuilder =
        post(PathMappingUri.SAVE_OR_UPDATE_QUETIONNAIR_SCHEDULE.getPath())
            .param("questionnairesId", "85199")
            .sessionAttr(STUDY_ID_ATTR_NAME, "678574")
            .headers(headers)
            .sessionAttr(CUSTOM_STUDY_ID_ATTR_NAME, "OpenStudy003")
            .sessionAttrs(getSessionAttributes());

    QuestionnaireBo questionnaireBo = new QuestionnaireBo();
    questionnaireBo.setQuestionnaireCustomScheduleBo(null);
    questionnaireBo.setQuestionnairesFrequenciesBo(null);
    questionnaireBo.setQuestionnairesFrequenciesList(null);

    addParams(requestBuilder, questionnaireBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:/adminStudies/viewStudyQuestionnaires.do"));

    verifyAuditEventCall(STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE);
  }
}
