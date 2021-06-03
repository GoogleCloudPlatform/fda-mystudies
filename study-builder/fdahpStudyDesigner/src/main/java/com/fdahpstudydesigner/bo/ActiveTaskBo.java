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
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "active_task")
@NamedQueries({
  @NamedQuery(name = "ActiveTaskBo.findAll", query = "SELECT ATB FROM ActiveTaskBo ATB"),
  @NamedQuery(
      name = "ActiveTaskBo.getActiveTasksByByStudyId",
      query =
          "SELECT ATB FROM ActiveTaskBo ATB where ATB.active IS NOT NULL and ATB.active=1 and ATB.studyId =:studyId order by id"),
  @NamedQuery(
      name = "ActiveTaskBo.getActiveTasksByByStudyIdDone",
      query =
          "SELECT ATB FROM ActiveTaskBo ATB where ATB.active IS NOT NULL and ATB.active=1 and ATB.studyId =:studyId order by id"),
  @NamedQuery(
      name = "updateStudyActiveTaskVersion",
      query = "UPDATE ActiveTaskBo SET live=2 WHERE customStudyId=:customStudyId and live=1"),
  @NamedQuery(
      name = "updateFromActiveTAskStartDate",
      query =
          "update ActiveTaskBo SET activeTaskLifetimeStart=:activeTaskLifetimeStart where id=:id and active IS NOT NULL and active=1"),
})
public class ActiveTaskBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "action", length = 1)
  private boolean action = false;

  @Transient private String actionPage;

  @Column(name = "active")
  private Integer active = 0;

  @Transient
  private List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBo = new ArrayList<>();

  @Transient private ActiveTaskFrequencyBo activeTaskFrequenciesBo = new ActiveTaskFrequencyBo();

  @Transient private List<ActiveTaskFrequencyBo> activeTaskFrequenciesList = new ArrayList<>();

  @Column(name = "active_task_lifetime_end")
  private String activeTaskLifetimeEnd;

  @Column(name = "active_task_lifetime_start")
  private String activeTaskLifetimeStart;

  @Transient private String activeTaskVersion = "";

  @Transient private boolean activityFinished = false;

  @Transient private boolean activityStarted = false;

  @Transient private String buttonText;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_date")
  private String createdDate;

  @Column(name = "custom_study_id")
  private String customStudyId;

  @Column(name = "day_of_the_week")
  private String dayOfTheWeek;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "duration")
  private String duration;

  @Transient private String fetalCickDuration = "";

  @Column(name = "frequency")
  private String frequency;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "instruction")
  private String instruction;

  @Column(name = "is_Change")
  private Integer isChange = 0;

  @Transient private Integer isDuplicate = 0;

  @Column(name = "is_live")
  private Integer live = 0;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_date")
  private String modifiedDate;

  @Transient private String previousFrequency;

  @Column(name = "repeat_active_task")
  private Integer repeatActiveTask;

  @Column(name = "short_title")
  private String shortTitle;

  @Column(name = "study_id")
  private String studyId;

  @Transient private List<ActiveTaskAtrributeValuesBo> taskAttributeValueBos = new ArrayList<>();

  @Transient private List<ActiveTaskMasterAttributeBo> taskMasterAttributeBos = new ArrayList<>();

  @Column(name = "task_type_id")
  private String taskTypeId;

  @Column(name = "task_title")
  private String title;

  @Transient private String type;

  @Column(name = "version")
  private Float version = 0f;

  @Column(name = "schedule_type")
  private String scheduleType = "";

  @Column(name = "anchor_date_id")
  private String anchorDateId;

  @Transient private boolean versionFlag = false;

  @Transient private String activeTaskCreated = "";

  public ActiveTaskBo() {
    // Do nothing
  }

  public String getActionPage() {
    return actionPage;
  }

  public Integer getActive() {
    return active;
  }

  public List<ActiveTaskCustomScheduleBo> getActiveTaskCustomScheduleBo() {
    return activeTaskCustomScheduleBo;
  }

  public ActiveTaskFrequencyBo getActiveTaskFrequenciesBo() {
    return activeTaskFrequenciesBo;
  }

  public List<ActiveTaskFrequencyBo> getActiveTaskFrequenciesList() {
    return activeTaskFrequenciesList;
  }

  public String getActiveTaskLifetimeEnd() {
    return this.activeTaskLifetimeEnd;
  }

  public String getActiveTaskLifetimeStart() {
    return this.activeTaskLifetimeStart;
  }

  public String getActiveTaskVersion() {
    return activeTaskVersion;
  }

  public String getButtonText() {
    return buttonText;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public String getDayOfTheWeek() {
    return dayOfTheWeek;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getDuration() {
    return this.duration;
  }

  public String getFetalCickDuration() {
    return fetalCickDuration;
  }

  public String getFrequency() {
    return frequency;
  }

  public String getId() {
    return this.id;
  }

  public String getInstruction() {
    return instruction;
  }

  public Integer getIsChange() {
    return isChange;
  }

  public Integer getIsDuplicate() {
    return isDuplicate;
  }

  public Integer getLive() {
    return live;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedDate() {
    return modifiedDate;
  }

  public String getPreviousFrequency() {
    return previousFrequency;
  }

  public Integer getRepeatActiveTask() {
    return repeatActiveTask;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public String getStudyId() {
    return this.studyId;
  }

  public List<ActiveTaskAtrributeValuesBo> getTaskAttributeValueBos() {
    return taskAttributeValueBos;
  }

  public List<ActiveTaskMasterAttributeBo> getTaskMasterAttributeBos() {
    return taskMasterAttributeBos;
  }

  public String getTaskTypeId() {
    return taskTypeId;
  }

  public String getTitle() {
    return title;
  }

  public String getType() {
    return type;
  }

  public Float getVersion() {
    return version;
  }

  public boolean isAction() {
    return action;
  }

  public boolean isActivityFinished() {
    return activityFinished;
  }

  public boolean isActivityStarted() {
    return activityStarted;
  }

  public boolean isVersionFlag() {
    return versionFlag;
  }

  public void setAction(boolean action) {
    this.action = action;
  }

  public void setActionPage(String actionPage) {
    this.actionPage = actionPage;
  }

  public void setActive(Integer active) {
    this.active = active;
  }

  public void setActiveTaskCustomScheduleBo(
      List<ActiveTaskCustomScheduleBo> activeTaskCustomScheduleBo) {
    this.activeTaskCustomScheduleBo = activeTaskCustomScheduleBo;
  }

  public void setActiveTaskFrequenciesBo(ActiveTaskFrequencyBo activeTaskFrequenciesBo) {
    this.activeTaskFrequenciesBo = activeTaskFrequenciesBo;
  }

  public void setActiveTaskFrequenciesList(List<ActiveTaskFrequencyBo> activeTaskFrequenciesList) {
    this.activeTaskFrequenciesList = activeTaskFrequenciesList;
  }

  public void setActiveTaskLifetimeEnd(String activeTaskLifetimeEnd) {
    this.activeTaskLifetimeEnd = activeTaskLifetimeEnd;
  }

  public void setActiveTaskLifetimeStart(String activeTaskLifetimeStart) {
    this.activeTaskLifetimeStart = activeTaskLifetimeStart;
  }

  public void setActiveTaskVersion(String activeTaskVersion) {
    this.activeTaskVersion = activeTaskVersion;
  }

  public void setActivityFinished(boolean activityFinished) {
    this.activityFinished = activityFinished;
  }

  public void setActivityStarted(boolean activityStarted) {
    this.activityStarted = activityStarted;
  }

  public void setButtonText(String buttonText) {
    this.buttonText = buttonText;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public void setDayOfTheWeek(String dayOfTheWeek) {
    this.dayOfTheWeek = dayOfTheWeek;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public void setFetalCickDuration(String fetalCickDuration) {
    this.fetalCickDuration = fetalCickDuration;
  }

  public void setFrequency(String frequency) {
    this.frequency = frequency;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setInstruction(String instruction) {
    this.instruction = instruction;
  }

  public void setIsChange(Integer isChange) {
    this.isChange = isChange;
  }

  public void setIsDuplicate(Integer isDuplicate) {
    this.isDuplicate = isDuplicate;
  }

  public void setLive(Integer live) {
    this.live = live;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedDate(String modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public void setPreviousFrequency(String previousFrequency) {
    this.previousFrequency = previousFrequency;
  }

  public void setRepeatActiveTask(Integer repeatActiveTask) {
    this.repeatActiveTask = repeatActiveTask;
  }

  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public void setTaskAttributeValueBos(List<ActiveTaskAtrributeValuesBo> taskAttributeValueBos) {
    this.taskAttributeValueBos = taskAttributeValueBos;
  }

  public void setTaskMasterAttributeBos(List<ActiveTaskMasterAttributeBo> taskMasterAttributeBos) {
    this.taskMasterAttributeBos = taskMasterAttributeBos;
  }

  public void setTaskTypeId(String taskTypeId) {
    this.taskTypeId = taskTypeId;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setVersion(Float version) {
    this.version = version;
  }

  public void setVersionFlag(boolean versionFlag) {
    this.versionFlag = versionFlag;
  }

  public String getScheduleType() {
    return scheduleType;
  }

  public void setScheduleType(String scheduleType) {
    this.scheduleType = scheduleType;
  }

  public String getAnchorDateId() {
    return anchorDateId;
  }

  public void setAnchorDateId(String anchorDateId) {
    this.anchorDateId = anchorDateId;
  }

  public String getActiveTaskCreated() {
    return activeTaskCreated;
  }

  public void setActiveTaskCreated(String activeTaskCreated) {
    this.activeTaskCreated = activeTaskCreated;
  }
}
