<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date"/>
<c:set var="tz" value="America/Los_Angeles"/>
<style>
  .time-opts .addBtnDis {
    display: none;
  }

  .time-opts:last-child .addBtnDis {
    display: inline-block;
  }

  .manually-option .addBtnDis {
    display: none;
  }

  .manually-option:last-child .addBtnDis {
    display: inline-block;
  }

  .manually-anchor-option .addBtnDis {
    display: none;
  }

  .manually-anchor-option:last-child .addBtnDis {
    display: inline-block;
  }

  .dis_inlinetop {
    display: inline-block;
    vertical-align: top;
  }

  .help-block ul {
    width: 155px;
   /* font-size: 10 px !important; */
  }
.tool-tip {
    display: inline-block;
  }

  .tool-tip [disabled] {
    pointer-events: none;
  }
   
   .help-block-txt ul {
      width: max-content !important;
    }
  

 
</style>
<div class="gray-xs-f mb-sm">Active task schedule type</div>
<div class="pb-lg ">
  <span class="radio radio-info radio-inline p-40">
    <input type="radio" id="schedule1" class="typeofschedule" scheduletype="Regular"
           value="Regular"
           name="scheduleType" ${empty activeTaskBo.scheduleType  || activeTaskBo.scheduleType=='Regular' ?'checked':''} ${(activeTaskBo.isDuplicate > 0)?'disabled' : ''}
    <c:if
        test="${empty anchorTypeList || fn:length(anchorTypeList) le 1}">'disabled'</c:if>>
    <label for="schedule1">Regular</label>
  </span>
  <span class="radio radio-inline p-40">
    <input type="radio" id="schedule2" class="typeofschedule" scheduletype="AnchorDate"
           value="AnchorDate"
           name="scheduleType" ${isAnchorQuestionnaire?'disabled':''} ${activeTaskBo.scheduleType=='AnchorDate' ?'checked':''} ${(activeTaskBo.isDuplicate > 0)?'disabled' : ''}
           <c:if test="${empty anchorTypeList}">disabled</c:if> >
    <label for="schedule2">Anchor date based</label>
  </span>
</div>
<!-- Anchor date type -->
<form:form action="" name="anchorFormId" id="anchorFormId" method="post" role="form"
           data-toggle="validator">
  <div class="anchortypeclass" style="display:none;">
      <div class="gray-xs-f mb-sm">Select anchor date type</div>
      <div class="clearfix"></div>
      <div class="col-md-5 col-lg-5 p-none">
        <div class="form-group">
          <select id="anchorDateId"
                  class="selectpicker ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                  required data-error="Please fill out this field" name="anchorDateId">
            <option value=''>Select</option>
            <c:forEach items="${anchorTypeList}" var="anchorTypeInfo">
              <option value="${anchorTypeInfo.id}"
                      name="${anchorTypeInfo.name}" ${activeTaskBo.anchorDateId eq anchorTypeInfo.id ? 'selected' : ''}>${anchorTypeInfo.name}</option>
            </c:forEach>
          </select>
          <div class="help-block with-errors red-txt help-block-txt"></div>
        </div>
      </div>
      <div class="clearfix"></div>
  </div>
</form:form>
<!-- Ancor date type -->
<div class="gray-xs-f mb-sm">Active task scheduling options</div>
<div class="pb-lg b-bor">
  <span class="radio radio-info radio-inline p-40">
    <input type="radio" id="oneTimeRadio1" class="schedule" frequencytype="oneTime"
           value="One time"
           name="frequency" ${empty activeTaskBo.frequency  || activeTaskBo.frequency=='One time' ?'checked':''} ${(activeTaskBo.isDuplicate > 0)?'disabled' : ''} >
    <label for="oneTimeRadio1">One time</label>
  </span>
  <span class="radio radio-inline p-40">
    <input type="radio" id="dailyRadio2" class="schedule" frequencytype="daily" value="Daily"
           name="frequency" ${activeTaskBo.frequency=='Daily' ?'checked':''} ${(activeTaskBo.isDuplicate > 0) ?'disabled' : ''}>
    <label for="dailyRadio2">Daily</label>
  </span>
  <span class="radio radio-inline p-40">
    <input type="radio" id="weeklyRadio3" class="schedule" frequencytype="week" value="Weekly"
           name="frequency" ${activeTaskBo.frequency=='Weekly' ?'checked':''} ${(activeTaskBo.isDuplicate > 0) ?'disabled' : ''}>
    <label for="weeklyRadio3">Weekly</label>
  </span>
  <span class="radio radio-inline p-40">
    <input type="radio" id="monthlyRadio4" class="schedule" frequencytype="month" value="Monthly"
           name="frequency" ${activeTaskBo.frequency=='Monthly' ?'checked':''} ${(activeTaskBo.isDuplicate > 0) ?'disabled' : ''}>
    <label for="monthlyRadio4">Monthly</label>
  </span>
  <span class="radio radio-inline p-40">
    <input type="radio" id="manuallyRadio5" class="schedule" frequencytype="manually"
           value="Manually Schedule"
           name="frequency" ${activeTaskBo.frequency=='Manually Schedule' ?'checked':''} ${(activeTaskBo.isDuplicate > 0)?'disabled' : ''}>
    <label for="manuallyRadio5">Custom schedule</label>
  </span>
</div>
<!-- One time Section-->
<form:form action="/studybuilder/adminStudies/saveOrUpdateActiveTaskSchedule.do?_S=${param._S}"
           name="oneTimeFormId"
           id="oneTimeFormId" method="post" role="form">
  <input type="hidden" name="frequency" id="frequencyId" value="${activeTaskBo.frequency}">
  <input type="hidden" name="previousFrequency" id="previousFrequency"
         value="${activeTaskBo.frequency}">
  <input type="hidden" name="id" id="activeTaskId" class="activeTaskIdClass"
         value="${activeTaskBo.id}">
  <input type="hidden" name="type" id="type" value="schedule">
  <input type="hidden" name="studyId" id="studyId"
         value="${not empty activeTaskBo.studyId ? activeTaskBo.studyId : studyBo.id}">
  <div class="oneTime all mt-lg">
    <div class="gray-xs-f mb-sm">Date/Time of launch (pick one)
      <span class="requiredStar"> *</span>
      <span
      <fmt:formatDate value = "${date}" pattern="z" var="server_timezone"/>
          class="ml-xs sprites_v3 filled-tooltip Selectedtooltip"
          data-toggle="tooltip"
          data-placement="bottom"
          title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
      </span>
    </div>
    <div class="mt-sm">
      <span class="checkbox checkbox-inline">
        <input type="hidden" name="activeTaskFrequenciesBo.id" id="oneTimeFreId"
               value="${activeTaskBo.activeTaskFrequenciesBo.id}">
        <input type="checkbox" id="isLaunchStudy" name="activeTaskFrequenciesBo.isLaunchStudy"
               value="true" ${activeTaskBo.activeTaskFrequenciesBo.isLaunchStudy ?'checked':''} ${(activeTaskBo.isDuplicate > 0)?'disabled' : ''}>
        <label for="isLaunchStudy"> Launch with study</label>
      </span>
      <div class="onetimeanchorClass mt-sm" style="display: none">
        <!-- Anchordate start -->
        <div class="opacity06">
          OR
        </div>
        <!-- Anchordate start-->
        <div class="mt-none resetDate">
          <div>
            <span class="pr-md">Anchor date</span>
            <span>
              <select
                  class="signDropDown selectpicker sign-box ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                  title="Select" name="activeTaskFrequenciesBo.xDaysSign" id="onetimeXSign">
                <option
                    value="0" ${not activeTaskBo.activeTaskFrequenciesBo.xDaysSign ?'selected':''} ${studyBo.enrollmentdateAsAnchordate?'selected':''}>
                  +
                </option>
                <option
                    value="1" ${activeTaskBo.activeTaskFrequenciesBo.xDaysSign ?'selected':''}>-
                </option>
              </select>
            </span>
            <!--  selectpicker -->
            <span class="form-group m-none dis-inline vertical-align-middle">
              <c:choose>
                <c:when test="${activeTaskBo.activeTaskFrequenciesBo.isLaunchStudy}">
                  <input id="onetimexdaysId" type="text"
                         class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm  ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                         placeholder="X" name="activeTaskFrequenciesBo.timePeriodFromDays"
                         value="" <c:if
                      test="${activeTaskBo.activeTaskFrequenciesBo.isLaunchStudy }"> disabled </c:if>
                         maxlength="3" pattern="[0-9]+"
                         data-pattern-error="Please enter valid number" data-error="Please fill out this field"/>
                </c:when>
                <c:otherwise>
                  <input id="onetimexdaysId" type="text"
                         class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                         placeholder="X" name="activeTaskFrequenciesBo.timePeriodFromDays"
                         value="${activeTaskBo.activeTaskFrequenciesBo.timePeriodFromDays}"
                      <c:if
                          test="${activeTaskBo.activeTaskFrequenciesBo.isLaunchStudy }"> disabled </c:if>
                         maxlength="3" pattern="[0-9]+"
                         data-pattern-error="Please enter valid number" data-error="Please fill out this field"/>
                </c:otherwise>
              </c:choose>
              <span class="help-block with-errors red-txt"></span>
            </span>
            <span class="mb-sm pr-md">
              <span class="light-txt opacity06"> days</span>
            </span>
            <span class="form-group m-none dis-inline vertical-align-middle pr-md">
              <input id="selectTime" type="text"
                     class="mt-sm form-control clock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                     name="activeTaskFrequenciesBo.frequencyTime" data-error="Please fill out this field" 
                     value="${activeTaskBo.activeTaskFrequenciesBo.frequencyTime}"  <c:if
                  test="${activeTaskBo.activeTaskFrequenciesBo.isLaunchStudy}"> disabled </c:if>
                     placeholder="Select time"/>
              <span class='help-block with-errors red-txt'></span>
            </span>
          </div>
        </div>
        <!-- Anchordate End -->
      </div>
      <div class="mt-md form-group regularClass">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <input id="chooseDate" type="text"
                 class="mt-sm form-control calendar ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 name="activeTaskFrequenciesBo.frequencyDate" placeholder="Choose date"
                 value="${activeTaskBo.activeTaskFrequenciesBo.frequencyDate}" required data-error="Please fill out this field" <c:if
              test="${activeTaskBo.activeTaskFrequenciesBo.isLaunchStudy}"> disabled </c:if> />
          <span class='help-block with-errors red-txt'></span>
        </span>
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <input id="selectTime1" type="text"
                 class="mt-sm form-control clock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 name="activeTaskFrequenciesBo.frequencyTime"
                 value="${activeTaskBo.activeTaskFrequenciesBo.frequencyTime}" required  data-error="Please fill out this field" <c:if
              test="${activeTaskBo.activeTaskFrequenciesBo.isLaunchStudy}"> disabled </c:if>
                 placeholder="Select time"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
    </div>
    <div class="gray-xs-f mb-sm mt-md">Lifetime of the run and of the task (pick one)
      <span
          class="requiredStar"> *
      </span>
    </div>
    <div class="mt-sm">
      <span class="checkbox checkbox-inline">
        <input type="checkbox" id="isStudyLifeTime" name="activeTaskFrequenciesBo.isStudyLifeTime"
               value="true" ${activeTaskBo.activeTaskFrequenciesBo.isStudyLifeTime ?'checked':''} ${(activeTaskBo.isDuplicate > 0)?'disabled' : ''}
               required="required" data-error="Please fill out this field">
        <label for="isStudyLifeTime"> Study lifetime</label>
      </span>
      <div class="mt-md form-group regularClass">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <c:choose>
            <c:when test="${activeTaskBo.activeTaskFrequenciesBo.isStudyLifeTime}"><input
                id="chooseEndDate"
                type="text"
                class="form-control calendar ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                name="activeTaskLifetimeEnd"
                placeholder="Choose end date"
                required data-error="Please fill out this field" <c:if
                test="${activeTaskBo.activeTaskFrequenciesBo.isStudyLifeTime }"> disabled </c:if>
                value=""/></c:when>
            <c:otherwise><input id="chooseEndDate" type="text"
                                class="form-control calendar ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                                name="activeTaskLifetimeEnd" placeholder="Choose end date"
                                required data-error="Please fill out this field" <c:if
                test="${activeTaskBo.activeTaskFrequenciesBo.isStudyLifeTime }"> disabled </c:if>
                                value="${activeTaskBo.activeTaskLifetimeEnd}"/></c:otherwise>
          </c:choose>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
      <div class="onetimeanchorClass mt-sm" style="display: none">
        <div class="opacity06">
          OR
        </div>
        <!-- Anchordate start-->
        <div class="mt-none resetDate">
          <div>
            <span class="pr-md">Anchor date</span>
            <span>
              <select
                  class="signDropDown selectpicker sign-box ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                  title="Select" name="activeTaskFrequenciesBo.yDaysSign"
                  id="onetimeYSign">
                <option
                    value="0" ${not activeTaskBo.activeTaskFrequenciesBo.yDaysSign ?'selected':''}>+
                </option>
                <option
                    value="1" ${activeTaskBo.activeTaskFrequenciesBo.yDaysSign ?'selected':''}>-
                </option>
              </select>
            </span>
            <!--  selectpicker -->
            <span class="form-group m-none dis-inline vertical-align-middle">
              <c:choose>
                <c:when test="${activeTaskBo.activeTaskFrequenciesBo.isStudyLifeTime}">
                  <input id="onetimeydaysId" type="text"
                         class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm  ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                         placeholder="Y" name="activeTaskFrequenciesBo.timePeriodToDays"
                         value="" <c:if
                      test="${activeTaskBo.activeTaskFrequenciesBo.isStudyLifeTime }"> disabled </c:if>
                         maxlength="3" pattern="[0-9]+"
                         data-pattern-error="Please enter valid number" data-error="Please fill out this field"/>
                </c:when>
                <c:otherwise>
                  <input id="onetimeydaysId" type="text"
                         class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm  ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                         placeholder="Y" name="activeTaskFrequenciesBo.timePeriodToDays"
                         value="${activeTaskBo.activeTaskFrequenciesBo.timePeriodToDays}"
                      <c:if
                          test="${activeTaskBo.activeTaskFrequenciesBo.isStudyLifeTime}"> disabled </c:if>
                         maxlength="3" pattern="[0-9]+"
                         data-pattern-error="Please enter valid number" data-error="Please fill out this field"/>
                </c:otherwise>
              </c:choose>
              <span class="help-block with-errors red-txt"></span>
            </span>
            <span class="mb-sm pr-md">
              <span class="light-txt opacity06"> days</span>
            </span>
          </div>
        </div>
      </div>
      <!-- Anchordate End -->
    </div>
  </div>
</form:form>
<!-- Daily Section-->
<form:form action="/studybuilder/adminStudies/saveOrUpdateActiveTaskSchedule.do?_S=${param._S}"
           name="dailyFormId"
           id="dailyFormId" method="post" role="form">
  <input type="hidden" name="frequency" id="dailyFrequencyId" value="${activeTaskBo.frequency}">
  <input type="hidden" name="previousFrequency" id="previousFrequency"
         value="${activeTaskBo.frequency}">
  <input type="hidden" name="id" id="id" value="${activeTaskBo.id}">
  <input type="hidden" name="studyId" id="studyId"
         value="${not empty activeTaskBo.studyId ? activeTaskBo.studyId : studyBo.id}">
  <input type="hidden" name="type" id="type" value="schedule">
  <input type="hidden" name="fetalCickDuration" value="">
  <input type="hidden" name="id" class="activeTaskIdClass" value="${activeTaskBo.id}">
  <div class="daily all mt-lg dis-none">

    <div class="mt-md">
      <div class="dailyStartCls col-md-3 pl-none">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <span class="gray-xs-f">Start date
            <span class="requiredStar"> *</span>
            <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
                  data-placement="bottom"
                  title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed."></span>
          </span>
          <br/>
          <input id="startDate" type="text"
                 class="form-control mt-sm calendar ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 placeholder="Choose date" required data-error="Please fill out this field" name="activeTaskLifetimeStart"
                 value="${activeTaskBo.activeTaskLifetimeStart}"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
      <!-- Anchordate start-->
      <div class="dailyanchorDiv col-md-4 pl-none" style="display:none;">
        <div class=" resetDate">
          <div>
            <span class="form-group m-none dis-inline vertical-align-middle pr-md">
              <span class="gray-xs-f">Start date
                <span class="requiredStar">*</span>
              </span>
              <br/>
              <span class="pr-md">Anchor date</span>
              <span>
                <select
                    class="signDropDown selectpicker sign-box ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                    title="Select" name="activeTaskFrequenciesList[0].xDaysSign"
                    id="dailyXSign">
                  <option
                      value="0" ${(fn:length(activeTaskBo.activeTaskFrequenciesList) gt 0) && not activeTaskBo.activeTaskFrequenciesList[0].xDaysSign ?'selected':''}>
                    +
                  </option>
                  <option
                      value="1" ${(fn:length(activeTaskBo.activeTaskFrequenciesList) gt 0) && activeTaskBo.activeTaskFrequenciesList[0].xDaysSign ?'selected':''}>
                    -
                  </option>
                </select>
              </span>
              <span class="form-group m-none dis-inline vertical-align-middle">
                <input id="dailyxdaysId" type="text"
                       class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm  ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                       placeholder="X"
                       name="activeTaskFrequenciesList[0].timePeriodFromDays"
                       value="${(fn:length(activeTaskBo.activeTaskFrequenciesList) gt 0)?activeTaskBo.activeTaskFrequenciesList[0].timePeriodFromDays:''}"
                       maxlength="3" pattern="[0-9]+"
                       data-pattern-error="Please enter valid number" data-error="Please fill out this field"/>
                <span class="help-block with-errors red-txt"></span>
              </span>
              <span class="mb-sm pr-md">
                <span class="light-txt opacity06"> days</span>
              </span>
            </span>
          </div>
        </div>
      </div>
      <!-- Anchordate End -->
      <div class="col-md-6 pr-none">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <span class="gray-xs-f">Number of times to repeat the task
            <span
                class="requiredStar"> *
            </span>
          </span>
          <br/>
          <input id="days" type="text"
                 class="form-control mt-sm numChk ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 name="repeatActiveTask" placeholder="No of times" required
                 value="${activeTaskBo.repeatActiveTask}"
                 onkeypress="return isNumber(event, this)" data-error="Please fill out this field"
                 pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$"
                 data-pattern-error="Please enter valid number" maxlength="3"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
      <div class="clearfix"></div>
    </div>
    <div class="mt-md">
      <div class="gray-xs-f mb-xs">End date
        <span class="ml-xs sprites_v3 filled-tooltip Selectedtooltip"
              data-toggle="tooltip"
              data-placement="bottom"
              title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
        </span>
      </div>
      <div class="black-xs-f"
           id="endDateId">${not empty activeTaskBo.activeTaskLifetimeEnd ? activeTaskBo.activeTaskLifetimeEnd :'NA'}</div>
      <input type="hidden" name="activeTaskLifetimeEnd" id="studyDailyLifetimeEnd"
             value="${activeTaskBo.activeTaskLifetimeEnd}">
    </div>

    <div class="mt-md">
      <div class="gray-xs-f mb-sm">Time(s) of the day for daily occurrence
        <span class="requiredStar"> *</span>
        <span class="ml-xs sprites_v3 filled-tooltip Selectedtooltip" data-toggle="tooltip"
              data-placement="bottom" id="helpNote"
              title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed."></span>
      </div>
      <div class="dailyContainer">
        <c:if test="${fn:length(activeTaskBo.activeTaskFrequenciesList) eq 0}">
          <div class="time-opts mt-md dailyTimeDiv" id="0">
            <span class="form-group m-none dis-inline vertical-align-middle pr-md">
              <input id="time0" type="text" name="activeTaskFrequenciesList[0].frequencyTime" required data-error="Please fill out this field"
                     class="form-control clock dailyClock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                     placeholder="Time" onclick='timep(this.id);'/>
              <span class='help-block with-errors red-txt'></span>
            </span>
            <span
                class="addBtnDis addbtn mr-sm align-span-center ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                onclick='addTime();'>+
            </span>
          </div>
        </c:if>
        <c:if test="${fn:length(activeTaskBo.activeTaskFrequenciesList) gt 0}">
          <c:forEach items="${activeTaskBo.activeTaskFrequenciesList}" var="activeTasksFrequencies"
                     varStatus="frequeincesVar">
            <div class="time-opts mt-md dailyTimeDiv" id="${frequeincesVar.index}">
              <input type="hidden" name="activeTaskFrequenciesList[${frequeincesVar.index}].id"
                     value="${activeTasksFrequencies.id}">
              <span class="form-group m-none dis-inline vertical-align-middle pr-md">
                <input id="time${frequeincesVar.index}" type="text"
                       name="activeTaskFrequenciesList[${frequeincesVar.index}].frequencyTime" required data-error="Please fill out this field"
                       class="form-control clock dailyClock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                       placeholder="Time" onclick='timep(this.id);'
                       value="${activeTasksFrequencies.frequencyTime}"/>
                <span class='help-block with-errors red-txt'></span>
              </span>
              <span
                  class="addBtnDis addbtn mr-sm align-span-center ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                  onclick='addTime();'>+
              </span>
              <span
                  class="delete vertical-align-middle remBtnDis hide pl-md align-span-center ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                  onclick='removeTime(this);'></span>
            </div>
          </c:forEach>
        </c:if>
      </div>
    </div>

    <div class="mt-lg">
      <div class="gray-xs-f mb-xs">Lifetime of each run</div>
      <div class="black-xs-f">Until the next run comes up</div>
    </div>
    <div class="mt-lg">
      <div class="gray-xs-f mb-xs">Lifetime of the task</div>
      <div class="black-xs-f" id="lifeTimeId">${activeTaskBo.activeTaskLifetimeStart}
        - ${activeTaskBo.activeTaskLifetimeEnd}</div>
    </div>
  </div>
</form:form>
<!-- Weekly Section-->
<form:form action="/studybuilder/adminStudies/saveOrUpdateActiveTaskSchedule.do?_S=${param._S}"
           name="weeklyFormId"
           id="weeklyFormId" method="post" role="form">
  <input type="hidden" name="frequency" id="weeklyfrequencyId">
  <input type="hidden" name="previousFrequency" id="previousFrequency"
         value="${activeTaskBo.frequency}">
  <input type="hidden" name="id" id="id" class="activeTaskIdClass" value="${activeTaskBo.id}">
  <input type="hidden" name="studyId" id="studyId"
         value="${not empty activeTaskBo.studyId ? activeTaskBo.studyId : studyBo.id}">
  <input type="hidden" name="activeTaskFrequenciesBo.id" id="weeklyFreId"
         value="${activeTaskBo.activeTaskFrequenciesBo.id}">
  <input type="hidden" name="type" id="type" value="schedule">
  <div class="week all mt-lg dis-none">
    <div id="weekDaysId">
      <span class="gray-xs-f">Day/Time (of the week)
        <span class="requiredStar"> *</span>
        <span
            class="ml-xs sprites_v3 filled-tooltip"
            data-toggle="tooltip"
            data-placement="bottom"
            title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
        </span>
      </span>
      <br/>
      <div class=" form-group m-none dis-inline vertical-align-middle pr-md">
        <span class="">
          <select id="startDateWeekly"
                  class="form-control mt-sm ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                  name="dayOfTheWeek" required data-error="Please select an item in the list">
            <option value=''>Select</option>
            <option
                value='Sunday' ${activeTaskBo.dayOfTheWeek eq 'Sunday' ? 'selected':''}>Sunday
            </option>
            <option
                value='Monday' ${activeTaskBo.dayOfTheWeek eq 'Monday' ?'selected':''}>Monday
            </option>
            <option
                value='Tuesday' ${activeTaskBo.dayOfTheWeek eq 'Tuesday' ?'selected':''}>Tuesday
            </option>
            <option value='Wednesday' ${activeTaskBo.dayOfTheWeek eq 'Wednesday' ?'selected':''}>Wednesday</option>
            <option value='Thursday' ${activeTaskBo.dayOfTheWeek eq 'Thursday' ?'selected':''}>Thursday</option>
            <option
                value='Friday' ${activeTaskBo.dayOfTheWeek eq 'Friday' ?'selected':''}>Friday
            </option>
            <option value='Saturday' ${activeTaskBo.dayOfTheWeek eq 'Saturday' ?'selected':''}>Saturday</option>
          </select>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
      <span class="form-group m-none dis-inline vertical-align-middle pr-md ml-lg">
        <input id="selectWeeklyTime" type="text"
               class="form-control  mt-sm clock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
               required data-error="Please fill out this field"
               onclick="timep(this.id)" placeholder="Time"
               name="activeTaskFrequenciesBo.frequencyTime" 
               value="${activeTaskBo.activeTaskFrequenciesBo.frequencyTime}"/>
        <span class='help-block with-errors red-txt'></span>
      </span>
    </div>
    <div class="mt-md">
      <span class="weeklyStartCls  col-md-3 pl-none">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <span class="gray-xs-f">Start date
            <span class="requiredStar"> *</span>
            <span
                class="ml-xs sprites_v3 filled-tooltip"
                data-toggle="tooltip"
                data-placement="bottom"
                title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
            </span>
          </span>
          <br/>
          <input id="startWeeklyDate" type="text"
                 class="form-control mt-sm calendar ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 required data-error="Please fill out this field"
                 name="activeTaskLifetimeStart" placeholder="Choose date"
                 value="${activeTaskBo.activeTaskLifetimeStart}" readonly="readonly"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </span>
      <!-- Anchordate start-->
      <div class="weeklyanchorDiv pl-none" style="display:none;">
        <div class=" resetDate dis_inlinetop p-none">
          <div>
            <span class="form-group m-none dis-inline vertical-align-middle pr-md">
              <span class="gray-xs-f">Start date
                <span class="requiredStar">*</span>
              </span>
              <br/>
              <span class="pr-md">Anchor date</span>
              <span>
                <select
                    class="signDropDown selectpicker sign-box ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                    title="Select" name="activeTaskFrequenciesBo.xDaysSign" id="weeklyXSign">
                  <option
                      value="0" ${not activeTaskBo.activeTaskFrequenciesBo.xDaysSign ?'selected':''}>+
                  </option>
                  <option
                      value="1" ${activeTaskBo.activeTaskFrequenciesBo.xDaysSign ?'selected':''}>-
                  </option>
                </select>
              </span>
              <span class="form-group m-none dis-inline vertical-align-middle">
                <input id="weeklyxdaysId" type="text"
                       class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                       placeholder="X" name="activeTaskFrequenciesBo.timePeriodFromDays"
                       value="${activeTaskBo.activeTaskFrequenciesBo.timePeriodFromDays}"
                       maxlength="3" pattern="[0-9]+"
                       data-pattern-error="Please enter valid number" data-error="Please fill out this field"/>
                <span class="help-block with-errors red-txt"></span>
              </span>
              <span class="mb-sm pr-md">
                <span class="light-txt opacity06"> days</span>
              </span>
            </span>
          </div>
        </div>
        <div class="dis_inlinetop p-none">
          <span class="gray-xs-f">Time
            <span class="requiredStar">*</span>
            <br/></span>
          <span class="form-group m-none dis-inline vertical-align-middle pr-md">
            <input id="selectWeeklyTimeAnchor" type="text"
                   class="form-control mt-sm clock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                   required data-error="Please fill out this field" onclick="timep(this.id)" placeholder="Time"
                   name="activeTaskFrequenciesBo.frequencyTime"
                   value="${activeTaskBo.activeTaskFrequenciesBo.frequencyTime}"/>
            <span class='help-block with-errors red-txt'></span>
          </span>
        </div>
        <div class="dis_inlinetop">
          <span class="form-group m-none dis-inline vertical-align-middle pr-md">
            <span class="gray-xs-f">Number of times to repeat the task
              <span
                  class="requiredStar"> *
              </span>
            </span>
            <br/>
            <input id="weeksAnchor" type="text"
                   class="form-control mt-sm numChk ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                   name="repeatActiveTask" placeholder="No of times"
                   value="${activeTaskBo.repeatActiveTask}" required
                   onkeypress="return isNumber(event, this)" data-error="Please fill out this field"
                   pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$"
                   data-pattern-error="Please enter valid number" maxlength="3"/>
            <span class='help-block with-errors red-txt'></span>
          </span>
        </div>
      </div>

      <!-- Anchordate End -->
      <div class="col-md-4 p-none weeklyRegular">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <span class="gray-xs-f">Number of times to repeat the task
            <span
                class="requiredStar"> *
            </span>
          </span>
          <br/>
          <input id="weeks" type="text"
                 class="form-control mt-sm numChk ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 name="repeatActiveTask" placeholder="No of times"
                 value="${activeTaskBo.repeatActiveTask}" required
                 onkeypress="return isNumber(event, this)" data-error="Please fill out this field"
                 pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$"
                 data-pattern-error="Please enter valid number" maxlength="3"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
      <div class="clearfix"></div>
    </div>
    <div class="mt-md">
      <div class="gray-xs-f mb-xs">End date
        <span
            class="ml-xs sprites_v3 filled-tooltip Selectedtooltip"
            data-toggle="tooltip"
            data-placement="bottom"
            title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
        </span>
      </div>
      <div class="black-xs-f"
           id="weekEndDate">${not empty activeTaskBo.activeTaskLifetimeEnd ? activeTaskBo.activeTaskLifetimeEnd :'NA'}</div>
      <input type="hidden" name="activeTaskLifetimeEnd" id="studyWeeklyLifetimeEnd"
             value="${activeTaskBo.activeTaskLifetimeEnd}">
    </div>
    <div class="mt-lg">
      <div class="gray-xs-f mb-xs">Lifetime of each run</div>
      <div class="black-xs-f">Until the next run comes up</div>
    </div>
    <div class="mt-lg">
      <div class="gray-xs-f mb-xs">Lifetime of the task</div>
      <div class="black-xs-f" id="weekLifeTimeEnd">${activeTaskBo.activeTaskLifetimeStart}
        - ${activeTaskBo.activeTaskLifetimeEnd}</div>
    </div>
  </div>
</form:form>
<!-- Monthly Section-->
<form:form action="/studybuilder/adminStudies/saveOrUpdateActiveTaskSchedule.do?_S=${param._S}"
           name="monthlyFormId"
           id="monthlyFormId" method="post" role="form">
  <input type="hidden" name="frequency" id="monthlyfrequencyId" value="${activeTaskBo.frequency}">
  <input type="hidden" name="previousFrequency" id="previousFrequency"
         value="${activeTaskBo.frequency}">
  <input type="hidden" name="id" id="id" class="activeTaskIdClass" value="${activeTaskBo.id}">
  <input type="hidden" name="studyId" id="studyId"
         value="${not empty activeTaskBo.studyId ? activeTaskBo.studyId : studyBo.id}">
  <input type="hidden" name="activeTaskFrequenciesBo.id" id="monthFreId"
         value="${activeTaskBo.activeTaskFrequenciesBo.id}">
  <input type="hidden" name="type" id="type" value="schedule">
  <div class="month all mt-lg dis-none">
    <div id="monthlyDateId">
      <span class="gray-xs-f">Select date/time (of the month)
        <span
            class="requiredStar"> *
        </span>
        <span
            class="ml-xs sprites_v3 filled-tooltip"
            data-toggle="tooltip"
            data-placement="bottom"
            title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
        </span>
      </span>
      <br/>
      <span class="monthlyStartCls form-group m-none dis-inline vertical-align-middle pr-md">
        <span class="">
          <input id="startDateMonthly" type="text"
                 class="form-control mt-sm calendar ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 required data-error="Please fill out this field"
                 placeholder="Choose date" name="activeTaskFrequenciesBo.frequencyDate"
                 value="${activeTaskBo.activeTaskFrequenciesBo.frequencyDate}"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </span>
      <span class="form-group m-none dis-inline vertical-align-middle pr-md">
        <input id="selectMonthlyTime" type="text"
               class="form-control mt-sm clock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
               required data-error="Please fill out this field"
               onclick="timep(this.id)" placeholder="Time"
               name="activeTaskFrequenciesBo.frequencyTime"
               value="${activeTaskBo.activeTaskFrequenciesBo.frequencyTime}"/>
        <span class='help-block with-errors red-txt'></span>
      </span>
      <div class="gray-xs-f mt-xs mb-lg italic-txt text-weight-light">If the selected date is not
        available in a
        month, the last day of the month will be used instead
      </div>
    </div>
    <div class="mt-xs">
      <div class="monthlyStartCls  col-md-3 pl-none">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <span class="gray-xs-f">Start date
            <span class="requiredStar"> *</span>
            <span
                class="ml-xs sprites_v3 filled-tooltip"
                data-toggle="tooltip"
                data-placement="bottom"
                title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
            </span>
          </span>
          <br/>
          <input id="pickStartDate" type="text"
                 class="form-control mt-sm calendar ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 placeholder="Choose start date" required data-error="Please fill out this field" name="activeTaskLifetimeStart"
                 value="${activeTaskBo.activeTaskLifetimeStart}" readonly="readonly"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
      <!-- Anchordate start-->
      <div class="monthlyanchorDiv pl-none" style="display:none;">
        <div class="dis_inlinetop resetDate">
          <div>
            <span class="form-group m-none dis-inline vertical-align-middle pr-md">
              <span class="gray-xs-f">Start date
                <span
                    class="requiredStar">*
                </span>
              </span>
              <br/>
              <span class="pr-md">Anchor date</span>
              <span>
                <select
                    class="signDropDown selectpicker sign-box ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                    title="Select" name="activeTaskFrequenciesBo.xDaysSign"
                    id="monthlyXSign">
                  <option
                      value="0" ${not activeTaskBo.activeTaskFrequenciesBo.xDaysSign ?'selected':''}>+
                  </option>
                  <option
                      value="1" ${activeTaskBo.activeTaskFrequenciesBo.xDaysSign ?'selected':''}>-
                  </option>
                </select>
              </span>
              <span class="form-group m-none dis-inline vertical-align-middle">
                <input id="monthlyxdaysId" type="text"
                       class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                       placeholder="X"
                       name="activeTaskFrequenciesBo.timePeriodFromDays"
                       value="${activeTaskBo.activeTaskFrequenciesBo.timePeriodFromDays}"
                       maxlength="3" pattern="[0-9]+"
                       data-pattern-error="Please enter valid number" data-error="Please fill out this field"/>
                <span class="help-block with-errors red-txt"></span>
              </span>
              <span class="mb-sm pr-md">
                <span class="light-txt opacity06"> days</span>
              </span>
            </span>
          </div>
        </div>

        <div class="dis_inlinetop">
          <span class="gray-xs-f">Time
            <span class="requiredStar">*</span>
          </span>
          <br/>
          <span class="form-group m-none dis-inline vertical-align-middle pr-md">
            <input id="selectMonthlyTimeAnchor" type="text"
                   class="form-control mt-sm clock ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                   required data-error="Please fill out this field" onclick="timep(this.id)" placeholder="Time"
                   name="activeTaskFrequenciesBo.frequencyTime"
                   value="${activeTaskBo.activeTaskFrequenciesBo.frequencyTime}"/>
            <span class='help-block with-errors red-txt'></span>
          </span>
        </div>
        <div class="dis_inlinetop">
          <span class="form-group m-none dis-inline vertical-align-middle pr-md">
            <span class="gray-xs-f">Number of times to repeat the task
              <span
                  class="requiredStar"> *
              </span>
            </span>
            <br/>
            <input id="monthsAnchor" type="text"
                   class="form-control mt-sm numChk ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                   name="repeatActiveTask" placeholder="No of times" required
                   value="${activeTaskBo.repeatActiveTask}"
                   onkeypress="return isNumber(event, this)" data-error="Please fill out this field"
                   pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$"
                   data-pattern-error="Please enter valid number" maxlength="3"/>
            <span class='help-block with-errors red-txt'></span>
          </span>
        </div>

      </div>

      <!-- Anchordate End -->
      <div id="activeMonthlyRegular" class="dis_inlinetop">
        <span class="form-group m-none dis-inline vertical-align-middle pr-md">
          <span class="gray-xs-f">Number of times to repeat the task
            <span
                class="requiredStar"> *
            </span>
          </span>
          <br/>
          <input id="months" type="text"
                 class="form-control mt-sm numChk ${(activeTaskBo.isDuplicate > 0)?'cursor-none' : ''}"
                 name="repeatActiveTask" placeholder="No of times" required
                 value="${activeTaskBo.repeatActiveTask}" onkeypress="return isNumber(event, this)" data-error="Please fill out this field"
                 pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$"
                 data-pattern-error="Please enter valid number" maxlength="3"/>
          <span class='help-block with-errors red-txt'></span>
        </span>
      </div>
      <div class="clearfix"></div>
    </div>
    <div class="mt-md col-md-12 p-none">
      <div class="gray-xs-f mb-xs">End date
        <span
            class="ml-xs sprites_v3 filled-tooltip Selectedtooltip"
            data-toggle="tooltip"
            data-placement="bottom"
            title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
        </span>
      </div>
      <div class="black-xs-f"
           id="monthEndDate">${not empty activeTaskBo.activeTaskLifetimeEnd ? activeTaskBo.activeTaskLifetimeEnd :'NA'}</div>
      <input type="hidden" name="activeTaskLifetimeEnd" id="studyMonthlyLifetimeEnd"
             value="${activeTaskBo.activeTaskLifetimeEnd}">
    </div>
    <div class="mt-lg col-md-12 p-none">
      <div class="gray-xs-f mb-xs">Lifetime of each run</div>
      <div class="black-xs-f">Until the next run comes up</div>
    </div>
    <div class="mt-lg col-md-12 p-none">
      <div class="gray-xs-f mb-xs">Lifetime of the task</div>
      <div class="black-xs-f" id="monthLifeTimeDate">${activeTaskBo.activeTaskLifetimeStart}
        - ${activeTaskBo.activeTaskLifetimeEnd}</div>
    </div>
  </div>
</form:form>
<!-- Manually Section-->
<form:form action="/studybuilder/adminStudies/saveOrUpdateActiveTaskSchedule.do?_S=${param._S}"
           name="customFormId"
           id="customFormId" method="post" role="form">
  <input type="hidden" name="id" id="id" value="${activeTaskBo.id}">
  <input type="hidden" name="studyId" id="studyId"
         value="${not empty activeTaskBo.studyId ? activeTaskBo.studyId : studyBo.id}">
  <input type="hidden" name="frequency" id="customfrequencyId" value="${activeTaskBo.frequency}">
  <input type="hidden" name="previousFrequency" id="previousFrequency"
         value="${activeTaskBo.frequency}">
  <input type="hidden" name="type" id="type" value="schedule">
  <div class="manually all mt-lg dis-none">
  
    <div class="gray-xs-f mb-sm">Select a date range and a start and end time for each run of the activity
      <span class="requiredStar"> *</span>
      <span
         class="ml-xs sprites_v3 filled-tooltip Selectedtooltip"
         data-toggle="tooltip"
         data-placement="bottom"
         title="Selected dates and times will work as per the mobile device time. Selections of dates or times in the past (as per the server timezone which is ${server_timezone}) is not allowed.">
       </span>
    </div>
    <div class="manuallyContainer">
      <c:if test="${fn:length(activeTaskBo.activeTaskCustomScheduleBo) eq 0}">
        <div class="manually-option mb-md form-group" id="0">
          <input type="hidden" name="activeTaskCustomScheduleBo[0].activeTaskId" id="activeTaskId"
                 class="activeTaskIdClass" value="${activeTaskBo.id}">
                 
          <span class="form-group dis-inline vertical-align-middle pr-md">
            <input id="StartDate0" type="text" count='0'
                   class="form-control calendar customCalnder cusStrDate"
                   name="activeTaskCustomScheduleBo[0].frequencyStartDate" value=""
                   placeholder="Start date"
                   onclick='customStartDate(this.id,0);' required data-error="Please fill out this field"/>
            <span class='help-block with-errors red-txt'></span>
            </span>
            
            <span
                      class="form-group  dis-inline vertical-align-middle pr-md">
                    <input id="customStartTime0" type="text" count='0'
                           class="form-control clock cusTime startTime"
                           name="activeTaskCustomScheduleBo[0].frequencyStartTime"
                           placeholder="Start time" onclick='timep(this.id);' disabled required data-error="Please fill out this field"/>
                    <span class='help-block with-errors red-txt'></span>
                 
          </span>
          <span class="gray-xs-f mb-sm pr-md align-span-center">
            to
          </span>
          <span class="form-group dis-inline vertical-align-middle pr-md">
            <input id="EndDate0" type="text" count='0'
                   class="form-control calendar customCalnder cusEndDate"
                   name="activeTaskCustomScheduleBo[0].frequencyEndDate" placeholder="End date"
                   onclick='customEndDate(this.id,0);' required data-error="Please fill out this field"/>
            <span class='help-block with-errors red-txt'></span>
            </span>            
           
          <span class="form-group dis-inline vertical-align-middle pr-md">
            <input id="customTime0" type="text" count='0' class="form-control clock endTime"
                   name="activeTaskCustomScheduleBo[0].frequencyEndTime" placeholder="End time"
                   onclick='timep(this.id);'
                   disabled required data-error="Please fill out this field"/>
            <span class='help-block with-errors red-txt'></span>
          </span>
          <span class="addBtnDis addbtn mr-sm align-span-center" onclick='addDate();'>+</span>
             <span id="delete"
                  class="sprites_icon delete vertical-align-middle remBtnDis hide align-span-center ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                  onclick="removeDate(this);"></span>
          
        </div>
      </c:if>
      <c:if test="${fn:length(activeTaskBo.activeTaskCustomScheduleBo) gt 0}">
        <c:forEach items="${activeTaskBo.activeTaskCustomScheduleBo}"
                   var="activeTaskCustomScheduleBo"
                   varStatus="customVar">
          <div class="manually-option mb-md form-group" id="RegDate${customVar.index}">
            <input type="hidden" name="activeTaskCustomScheduleBo[${customVar.index}].id" id="id"
                   value="${activeTaskCustomScheduleBo.id}">
            <input type="hidden" name="activeTaskCustomScheduleBo[${customVar.index}].used"
                   id="isUsed${customVar.index}" value="${activeTaskCustomScheduleBo.used}">
            <input type="hidden" name="activeTaskCustomScheduleBo[${customVar.index}].activeTaskId"
                   id="activeTaskId" value="${activeTaskCustomScheduleBo.activeTaskId}">
            <span class="form-group  dis-inline vertical-align-middle pr-md">
              <input id="StartDate${customVar.index}" type="text" count='${customVar.index}'
                     class="form-control calendar cusStrDate ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                     name="activeTaskCustomScheduleBo[${customVar.index}].frequencyStartDate"
                     value="${activeTaskCustomScheduleBo.frequencyStartDate}" placeholder="Start date"
                     onclick='customStartDate(this.id,${customVar.index});' required data-error="Please fill out this field"/>
              <span class='help-block with-errors red-txt'></span>
            </span>
            
            
            <span class="form-group  dis-inline vertical-align-middle pr-md">
              <input id="customStartTime${customVar.index}" type="text" count='${customVar.index}'
                     class="form-control clock startTime cusTime ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                     name="activeTaskCustomScheduleBo[${customVar.index}].frequencyStartTime"
                     value="${activeTaskCustomScheduleBo.frequencyStartTime}" placeholder="Start time"
                     onclick='timep(this.id);'
                     required data-error="Please fill out this field"/>
              <span class='help-block with-errors red-txt'></span>
            </span>
            
            
            <span class="gray-xs-f mb-sm pr-md align-span-center">
              to
            </span>
            <span class="form-group  dis-inline vertical-align-middle pr-md">
              <input id="EndDate${customVar.index}" type="text" count='${customVar.index}'
                     class="form-control calendar cusEndDate ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                     name="activeTaskCustomScheduleBo[${customVar.index}].frequencyEndDate"
                     value="${activeTaskCustomScheduleBo.frequencyEndDate}" placeholder="End date"
                     onclick='customEndDate(this.id,${customVar.index});' required data-error="Please fill out this field"/>
              <span class='help-block with-errors red-txt'></span>
            </span>
            <span class="form-group  dis-inline vertical-align-middle pr-md">
              <input id="customTime${customVar.index}" type="text" count='${customVar.index}'
                     class="form-control clock cusTime endTime  ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                     name="activeTaskCustomScheduleBo[${customVar.index}].frequencyEndTime"
                     value="${activeTaskCustomScheduleBo.frequencyEndTime}" placeholder="End time"
                     onclick='timep(this.id);'
                     required data-error="Please fill out this field"/>
              <span class='help-block with-errors red-txt'></span>
            </span>
            <span id="AddButton"
                  class="addbtn addBtnDis align-span-center mr-md " onclick="addDate();">+</span>
            <span id="delete"
                  class="sprites_icon delete vertical-align-middle remBtnDis hide align-span-center ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                  onclick="removeDate(this);"></span>
          </div>
        </c:forEach>
      </c:if>
    </div>
    <!-- anchor start-->
    <div class="manuallyAnchorContainer" style="display:none;">
      <c:if
          test="${fn:length(activeTaskBo.activeTaskCustomScheduleBo) eq 0}">
        <div class="manually-anchor-option mb-md form-group" id="0">
          <input type="hidden" name="activeTaskCustomScheduleBo[0].activeTaskId" id="activeTaskId"
                 class="activeTaskIdClass" value="${activeTaskBo.id}">
            
          <span class="mb-sm pr-md">
            <span class="light-txt opacity06">
              Anchor date
            </span>
          </span>
          <span>
            <select class="signDropDown selectpicker sign-box"
                        count='0' title="Select"
                        name="activeTaskCustomScheduleBo[0].xDaysSign" id="xSign0">
            <option value="0"
              ${not activeTaskCustomScheduleBo.xDaysSign ?'selected':''}>+
            </option>
            <option value="1"
              ${activeTaskCustomScheduleBo.xDaysSign ?'selected':''}>-
            </option>
            </select>
          </span>
          <span class="form-group m-none dis-inline vertical-align-middle">
            <input id="xdays0" type="text"
                   class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave xdays daysMask mt-sm resetAncDate"
                   count='0' placeholder="X"
                   name="activeTaskCustomScheduleBo[0].timePeriodFromDays"
                   value="${activeTaskCustomScheduleBo.timePeriodFromDays}"
                   maxlength="3" required data-error="Please fill out this field" pattern="[0-9]+"
                   data-pattern-error="Please enter valid number"/>
            <span
                class="help-block with-errors red-txt"></span>
          </span>
          <span class="mb-sm pr-md">
            <span
                class="light-txt opacity06"> days
              </span>

                <span class="form-group  dis-inline vertical-align-middle pr-md"
                style="margin-bottom: -13px"><input id="manualStartTime0"
                                                    type="text" class="form-control clock"
                                                    name="activeTaskCustomScheduleBo[0].frequencyStartTime" data-error="Please fill out this field"
                                                    value="${activeTaskCustomScheduleBo.frequencyStartTime}"
                                                    placeholder="Start time" required/>
            <span
                class='help-block with-errors red-txt'></span>
          </span>

          <span
                class="light-txt opacity06">
              <span>
                  to
              </span>
              Anchor
              Date
            </span>
          </span>
          <span>
            <select class="signDropDown selectpicker sign-box"
                        count='0' title="Select"
                        name="activeTaskCustomScheduleBo[0].yDaysSign" id="ySign0">
            <option value="0"
              ${not activeTaskCustomScheduleBo.yDaysSign ?'selected':''}>+
            </option>
            <option value="1"
              ${activeTaskCustomScheduleBo.yDaysSign ?'selected':''}>-
            </option>
            </select>
          </span>
          <span class="form-group m-none dis-inline vertical-align-middle">
            <input id="ydays0" type="text"
                   class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave ydays daysMask mt-sm resetAncDate"
                   count='0' placeholder="Y"
                   name="activeTaskCustomScheduleBo[0].timePeriodToDays"
                   value="${activeTaskCustomScheduleBo.timePeriodToDays}"
                   maxlength="3" pattern="[0-9]+"
                   data-pattern-error="Please enter valid number" data-error="Please fill out this field" required/>
            <span
                class="help-block with-errors red-txt"></span>
          </span>
          <span class="mb-sm pr-md">
            <span
                class="light-txt opacity06"> days
            </span>
          </span>
          
              
                
            
          <span class="form-group  dis-inline vertical-align-middle pr-md"
                style="margin-bottom: -13px"><input id="manualEndTime0"
                                                    type="text" class="form-control clock"
                                                    name="activeTaskCustomScheduleBo[0].frequencyEndTime" data-error="Please fill out this field" 
                                                    value="${activeTaskCustomScheduleBo.frequencyEndTime}"
                                                    placeholder="End time" required/>
            <span
                class='help-block with-errors red-txt'></span>
          </span>
          <span class="addbtn addBtnDis dis-inline vertical-align-middle "
                onclick="addDateAnchor(0);">+
          </span>
             <span id="deleteAncchor"
                  class="sprites_icon delete vertical-align-middle remBtnDis hide align-span-center ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                  onclick="removeDateAnchor(this);"></span>
        </div>
      </c:if>
      <c:if
          test="${fn:length(activeTaskBo.activeTaskCustomScheduleBo) gt 0}">
        <c:forEach items="${activeTaskBo.activeTaskCustomScheduleBo}"
                   var="activeTaskCustomScheduleBo" varStatus="customVar">
          <div class="manually-anchor-option mb-md form-group"
               id="AnchorDate${customVar.index}">
            <input type="hidden" name="activeTaskCustomScheduleBo[${customVar.index}].id" id="id" data-error="Please fill out this field" 
                   value="${activeTaskCustomScheduleBo.id}">
            <input type="hidden" name="activeTaskCustomScheduleBo[${customVar.index}].used"  data-error="Please fill out this field" 
                   id="isUsed${customVar.index}" value="${activeTaskCustomScheduleBo.used}">
            <input type="hidden" name="activeTaskCustomScheduleBo[${customVar.index}].activeTaskId" data-error="Please fill out this field" 
                   id="activeTaskId" value="${activeTaskCustomScheduleBo.activeTaskId}">
            <span class="mb-sm pr-md">
              <span
                  class="light-txt opacity06"> Anchor date
              </span>
            </span>
            <span>
              <select
                class="signDropDown selectpicker sign-box ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                count='${customVar.index}' title="Select" data-error="Please select an item in the list"
                name="activeTaskCustomScheduleBo[${customVar.index}].xDaysSign"
                id="xSign${customVar.index}">
                <option value="0"
                  ${not activeTaskCustomScheduleBo.xDaysSign ?'selected':''}>+
                </option>
                <option value="1"
                  ${activeTaskCustomScheduleBo.xDaysSign ?'selected':''}>-
                </option>
              </select>
            </span>
            <span class="form-group m-none dis-inline vertical-align-middle">
              <input id="xdays${customVar.index}" type="text"
                     class="form-control wid70 disRadBtn1 disBtn1 remove_required remReqOnSave xdays daysMask mt-sm resetAncDate xancorText ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                     count='${customVar.index}' placeholder="X"
                     name="activeTaskCustomScheduleBo[${customVar.index}].timePeriodFromDays"
                     value="${activeTaskCustomScheduleBo.timePeriodFromDays}"
                     maxlength="3" required pattern="[0-9]+" data-error="Please fill out this field"
                     data-pattern-error="Please enter valid number" data-type='xancorText'/>
              <span
                  class="help-block with-errors red-txt"></span>
            </span>
            <span class="mb-sm pr-md">
              <span
                  class="light-txt opacity06"> days
                  </span>

                <span class="form-group  dis-inline vertical-align-middle pr-md"
                style="margin-bottom: -13px"><input id="manualStartTime${customVar.index}"
                                                    type="text" class="form-control clock"
                                                    name="activeTaskCustomScheduleBo[0].frequencyStartTime" 
                                                    value="${activeTaskCustomScheduleBo.frequencyStartTime}"
                                                    placeholder="Start time" required data-error="Please fill out this field"/>
            <span
                class='help-block with-errors red-txt'></span>
          </span>

          <span
                class="light-txt opacity06">
                <span
                    style="padding-right: 5px; padding-left: 5px">to
                </span>
                Anchor
                Date
              </span>
            </span>
            <span>
              <select
                class="signDropDown selectpicker sign-box ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                count='${customVar.index}' title="Select" data-error="Please select an item in the list"
                name="activeTaskCustomScheduleBo[${customVar.index}].yDaysSign"
                id="ySign${customVar.index}">
              <option value="0"
                ${not activeTaskCustomScheduleBo.yDaysSign ?'selected':''}>+
              </option>
              <option value="1"
                ${activeTaskCustomScheduleBo.yDaysSign ?'selected':''}>-
              </option>
             </select>
            </span>
            <span class="form-group m-none dis-inline vertical-align-middle">
              <input id="ydays${customVar.index}" type="text"
                     class="form-control wid70 disRadBtn1 disBtn1 remove_required remReqOnSave ydays daysMask mt-sm resetAncDate yancorText ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                     count='${customVar.index}' placeholder="Y"
                     name="activeTaskCustomScheduleBo[${customVar.index}].timePeriodToDays"
                     value="${activeTaskCustomScheduleBo.timePeriodToDays}"
                     maxlength="3" pattern="[0-9]+"
                     data-pattern-error="Please enter valid number" required data-error="Please fill out this field"
                     data-type='yancorText'/>
              <span
                  class="help-block with-errors red-txt"></span>
            </span>
            <span class="mb-sm pr-md">
              <span
                  class="light-txt opacity06"> days
              </span>
            </span>
            <span class="form-group  dis-inline vertical-align-middle pr-md"
                  style="margin-bottom: -13px"><input
                id="manualEndTime${customVar.index}" type="text"
                class="form-control remove_required clock ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                name="activeTaskCustomScheduleBo[${customVar.index}].frequencyEndTime"
                value="${activeTaskCustomScheduleBo.frequencyEndTime}"
                placeholder="End time" required data-error="Please fill out this field"/>
              <span
                  class='help-block with-errors red-txt'></span>
            </span>
            <span class="addbtn addBtnDis align-span-center mr-md "
                  onclick="addDateAnchor(${customVar.index});">+
            </span>
            <span id="deleteAncchor"
                  class="sprites_icon delete vertical-align-middle remBtnDis hide align-span-center ${activeTaskCustomScheduleBo.used ?'cursor-none' : ''}"
                  onclick="removeDateAnchor(this);"></span>
          </div>
        </c:forEach>
      </c:if>
    </div>
    <!-- anchor end-->
    <div class="mt-md">
      <div class="gray-xs-f mb-xs">Lifetime of each run</div>
      <div class="black-xs-f">Each run begins at the selected time on the start date and expires at the same time on the end date</div>
    </div>
  </div>
</form:form>
<script type="text/javascript">
  var count = 0;
  var customCount = 0;

  var frequencey = "${activeTaskBo.frequency}";
  customCount = '${customCount}';
  customCount = '${customCount}'==='' ? '0' : '${customCount}';
  count = '${count}'
  var isValidManuallySchedule = true;
  var multiTimeVal = true;
  var scheduletype = "${activeTaskBo.scheduleType}";
  if (scheduletype != '' && scheduletype != null && typeof scheduletype != 'undefined') {
    scheduletype = $('input[name="scheduleType"]:checked').val();
  }
  var customAnchorCount = 0;
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    $(".remBtnDis").addClass("hide");

    $('.selectpicker').selectpicker('refresh');
    $('[data-toggle="tooltip"]').tooltip();

    if ($('.dailyTimeDiv').length == 1) {
	   	 $('.dailyTimeDiv').find(".delete").css("visibility", "hidden");
	     }
   if($('.manually-option').length == 1){
 	  $('.manually-option').find(".delete").css("visibility", "hidden");
   }

  if($('.manually-anchor-option').length == 1){
	  $('.manually-anchor-option').find(".delete").css("visibility", "hidden");
  }
    if ($("#schedule2").prop("checked")) {
    	 var schedule_opts = $("input[name='frequency']:checked").val();
    	 
    	  $("#weekDaysId").hide();
        $("#weekDaysId").find('input:text').removeAttr('required', true);
        $(".weeklyRegular").hide();
        $(".weeklyRegular").removeAttr('required');

        $("#monthlyDateId").hide();
        $("#monthlyDateId").find('input:text').removeAttr('required', true);
        $(".monthlyRegular").hide();
        $(".monthlyRegular").removeAttr('required');

        $("#activeMonthlyRegular").hide();
        $("#months").removeAttr('required');

        localStorage.setItem("IsActiveAnchorDateSelected", "true");
        localStorage.setItem("IsActiveRegularSelected", "false");

        if (schedule_opts == 'One time') {
          $(".onetimeanchorClass").show();
          $(".onetimeanchorClass").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Daily') {
          $(".dailyanchorDiv").show();
          $(".dailyanchorDiv").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Weekly') {
          $(".weeklyanchorDiv").show();
          $(".weeklyanchorDiv").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Monthly') {
          $(".monthlyanchorDiv").show();
          $(".monthlyanchorDiv").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Manually Schedule') {
          $(".manuallyAnchorContainer").show();
          $(".manuallyAnchorContainer").find('input:text').attr('required', true);
        }
        $('.regularClass').hide();
        $('.regularClass').find('input:text').removeAttr('required');
        $('.anchortypeclass').show();
        $('.anchortypeclass').find('input:select').attr('required', true);
        $('.selectpicker').selectpicker('refresh');
        $('.dailyStartCls').hide();
        $('.dailyStartCls').find('input:text').removeAttr('required');
        $('.weeklyStartCls').hide();
        $('.weeklyStartCls').find('input:text,select').removeAttr('required');
        $('.monthlyStartCls').hide();
        $('.monthlyStartCls').find('input:text').removeAttr('required');
        $(".manuallyContainer").hide();
        $(".manuallyContainer").find('input:text').removeAttr('required');
        $(".Selectedtooltip").hide();

    } else {
    	localStorage.setItem("IsActiveAnchorDateSelected", "false");
        localStorage.setItem("IsActiveRegularSelected", "true");

        $("#weekDaysId").show();
        $("#weekDaysId").attr('required', true);

        $(".weeklyRegular").show();
        $(".weeklyRegular").attr('required');

        $("#monthlyDateId").show();
        $("#monthlyDateId").attr('required', true);

        $("#activeMonthlyRegular").show();
        $("#months").attr('required', true);

        $(".onetimeanchorClass").hide();
        $('.onetimeanchorClass').find('input:text').removeAttr('required');
        $('.regularClass').show();
        $('.regularClass').find('input:text').attr('required', true);

        $('.dailyStartCls').show();
        $('.dailyStartCls').find('input:text').attr('required', true);
        $(".dailyanchorDiv").hide();
        $(".dailyanchorDiv").find('input:text').removeAttr('required', true);

        $('.weeklyStartCls').show();
        $('.weeklyStartCls').find('input:text,select').attr('required', true);
        $(".weeklyanchorDiv").hide();
        $(".weeklyanchorDiv").find('input:text').removeAttr('required', true);

        $('.monthlyStartCls').show();
        $('.monthlyStartCls').find('input:text').attr('required', true);
        $(".monthlyanchorDiv").hide();
        $(".monthlyanchorDiv").find('input:text').removeAttr('required', true);

        $('.manuallyContainer').show();
        $('.manuallyContainer').find('input:text').attr('required', true);
        $(".manuallyAnchorContainer").hide();
        $(".manuallyAnchorContainer").find('input:text').removeAttr('required', true);
        $('.anchortypeclass').hide();
        $('.anchortypeclass').removeAttr('required');
        $("#anchorDateId").val("");
        $(".Selectedtooltip").show();
    }
    $(".typeofschedule").change(function () {
      var scheduletype = $(this).attr('scheduletype');
      $('#isLaunchStudy').prop('checked', false);
      $('#isStudyLifeTime').prop('checked', false);
      $("#chooseDate").attr("disabled", false);
      $("#selectTime1").attr("disabled", false);
      $("#chooseEndDate").attr("disabled", false);
      $("#onetimexdaysId").prop('disabled', false);
      $("#selectTime").attr("disabled", false);
      $("#onetimeydaysId").prop('disabled', false);
      var schedule_opts = $("input[name='frequency']:checked").val();
      if (scheduletype == 'AnchorDate') {

        $("#weekDaysId").hide();
        $("#weekDaysId").find('input:text').removeAttr('required', true);
        $(".weeklyRegular").hide();
        $(".weeklyRegular").removeAttr('required');

        $("#monthlyDateId").hide();
        $("#monthlyDateId").find('input:text').removeAttr('required', true);
        $(".monthlyRegular").hide();
        $(".monthlyRegular").removeAttr('required');

        $("#activeMonthlyRegular").hide();
        $("#months").removeAttr('required');

        localStorage.setItem("IsActiveAnchorDateSelected", "true");
        localStorage.setItem("IsActiveRegularSelected", "false");

        if (schedule_opts == 'One time') {
          $(".onetimeanchorClass").show();
          $(".onetimeanchorClass").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Daily') {
          $(".dailyanchorDiv").show();
          $(".dailyanchorDiv").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Weekly') {
          $(".weeklyanchorDiv").show();
          $(".weeklyanchorDiv").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Monthly') {
          $(".monthlyanchorDiv").show();
          $(".monthlyanchorDiv").find('input:text').attr('required', true);
        }
        if (schedule_opts == 'Manually Schedule') {
          $(".manuallyAnchorContainer").show();
          $(".manuallyAnchorContainer").find('input:text').attr('required', true);
        }
        $('.regularClass').hide();
        $('.regularClass').find('input:text').removeAttr('required');
        $('.anchortypeclass').show();
        $('.anchortypeclass').find('input:select').attr('required', true);
        $('.selectpicker').selectpicker('refresh');
        $('.dailyStartCls').hide();
        $('.dailyStartCls').find('input:text').removeAttr('required');
        $('.weeklyStartCls').hide();
        $('.weeklyStartCls').find('input:text,select').removeAttr('required');
        $('.monthlyStartCls').hide();
        $('.monthlyStartCls').find('input:text').removeAttr('required');
        $(".manuallyContainer").hide();
        $(".manuallyContainer").find('input:text').removeAttr('required');
        $(".Selectedtooltip").hide();

        $($('.manually-anchor-option').get().reverse()).each(function () {
           var id = $(this).attr("id");
           var count12 = $("#"+id).find(".xdays").attr("count");
           if($('#'+id).find('#xdays'+count12).val()=="" && $('.manually-anchor-option').filter(function() {
               return $(this).css('display') !== 'none';
           }).length !== 1){
         
                   $("#"+id).remove();
                   $("#"+id).find('input:text').removeAttr('required', true);

                   $("#AddButton").show();
                   $("#AddButton").attr('required', true);
               }else {
                     $("#AddButton").hide();
                     $("#AddButton").attr('required', false);
                 }
        });
        
        if( $('.manually-anchor-option').filter(function() {
            return $(this).css('display') !== 'none';
        }).length == 1){
         $("#AddButton").show();
       $('.manually-anchor-option').find(".delete").css("visibility", "hidden");
           }
        
      } else {
        localStorage.setItem("IsActiveAnchorDateSelected", "false");
        localStorage.setItem("IsActiveRegularSelected", "true");

        $("#weekDaysId").show();
        $("#weekDaysId").find('input:text,select').attr('required', true);

        $(".weeklyRegular").show();
        $(".weeklyRegular").find('input:text').attr('required', true);
   
        $("#monthlyDateId").show();
       $("#monthlyDateId").find('input:text,select').attr('required', true);

        $(".monthlyRegular").show();
        $(".monthlyRegular").attr('required', true);
        
        $("#activeMonthlyRegular").show();
        $("#months").attr('required', true);

        $(".onetimeanchorClass").hide();
        $('.onetimeanchorClass').find('input:text').removeAttr('required');
        $('.regularClass').show();
        $('.regularClass').find('input:text').attr('required', true);

        $('.dailyStartCls').show();
        $('.dailyStartCls').find('input:text').attr('required', true);
        $(".dailyanchorDiv").hide();
        $(".dailyanchorDiv").find('input:text').removeAttr('required', true);

        $('.weeklyStartCls').show();
        $('.weeklyStartCls').find('input:text,select').attr('required', true);
        $(".weeklyanchorDiv").hide();
        $(".weeklyanchorDiv").find('input:text').removeAttr('required', true);

        $('.monthlyStartCls').show();
        $('.monthlyStartCls').find('input:text').attr('required', true);
        $(".monthlyanchorDiv").hide();
        $(".monthlyanchorDiv").find('input:text').removeAttr('required', true);

        $('.manuallyContainer').show();
        $('.manuallyContainer').find('input:text').attr('required', true);
        $(".manuallyAnchorContainer").hide();
        $(".manuallyAnchorContainer").find('input:text').removeAttr('required', true);
        $('.anchortypeclass').hide();
        $('.anchortypeclass').removeAttr('required');
        $("#anchorDateId").val("");
        $(".Selectedtooltip").show();

        $('.manually-option').each(function () {
           var id = $(this).attr("id");
           var count12 = $("#"+id).find(".cusStrDate").attr("count");
           if($('#'+id).find('#StartDate'+count12).val()=="" && $('.manually-option').filter(function() {
               return $(this).css('display') !== 'none';
           }).length !== 1){
                 
                   $("#"+id).remove();
                   $("#"+id).find('input:text').removeAttr('required', true);

                   $("#AddButton").show();
                   $("#AddButton").attr('required', true);
               }else {
                     $("#AddButton").hide();
                     $("#AddButton").attr('required', false);
                 }
        });
       
        if( $('.manually-option').filter(function() {
        	return $(this).css('display') !== 'none'; }).length == 1){
    	     $("#AddButton").show();
             $('.manually-option').find(".delete").css("visibility", "hidden");
           }
        
        if($('.manually-option').filter(function() {return $(this).css('display') !== 'none';}).length !== 1 ){
            $('.manually-option').find('#AddButton').first().hide();
        }
       
      }

      if (schedule_opts == 'One time') {
        $("#chooseDate").val('');
        $("#selectTime1").val('');
        $("#chooseEndDate").val('');
        $("#isLaunchStudy").val('');
        $("#isStudyLifeTime").val('');
        $("#selectTime").val('');
        $('#onetimexdaysId').val('');
        $('#onetimeydaysId').val('');
        var frequency_txt = "${activeTaskBo.frequency}";
        if (frequency_txt != '' && frequency_txt != null && typeof frequency_txt != 'undefined') {
          $("#previousFrequency").val(frequency_txt);
        }
      }
    });
    $("#onetimexdaysId, #onetimeydaysId").on('blur', function () {
      chkDaysValid(false);
    });

    $('.signDropDown').on('change', function () {
      chkDaysValid(false);
    });
    checkDateRange();
    $('#monthEndDate').bind('contentchanged', function () {
      chkEndDateWithDate($('#months'), $('#monthEndDate'));
    });
    $('#endDateId').bind('contentchanged', function () {
      chkEndDateWithDate($('#days'), $('#endDateId'));
    });
    $('#weekEndDate').bind('contentchanged', function () {
      chkEndDateWithDate($('#weeks'), $('#weekEndDate'));
    });
    customStartDate('StartDate' + customCount, customCount);
    customEndDate('EndDate' + customCount, customCount);
    if ($('.time-opts').length > 1) {
      $('.dailyContainer').find(".remBtnDis").removeClass("hide");
    } else {
      $('.dailyContainer').find(".remBtnDis").addClass("hide");
    }
    if ($('.manually-option').length > 1) {
      $('.manuallyContainer').find(".remBtnDis").removeClass("hide");
    } else {
      $('.manuallyContainer').find(".remBtnDis").addClass("hide");
    }
    $(".schedule").change(function () {
      $(".all").addClass("dis-none");
      var schedule_opts = $(this).attr('frequencytype');
      var val = $(this).val();
      console.log("val:" + val);
      $("." + schedule_opts).removeClass("dis-none");
      resetValidation($("#oneTimeFormId"));
      resetValidation($("#customFormId"));
      resetValidation($("#dailyFormId"));
      resetValidation($("#weeklyFormId"));
      resetValidation($("#monthlyFormId"));
      console.log("frequencey:" + frequencey);
      if ((frequencey != null && frequencey != "" && typeof frequencey != 'undefined')) {
        if (frequencey != val) {
          if (val == 'One time') {
            $("#chooseDate").val('');
            $("#selectTime").val('');
            $("#selectTime1").val('');
            $("#chooseEndDate").val('');
            $("#oneTimeFreId").val('');
            $("#isLaunchStudy").val('');
            $("#isStudyLifeTime").val('');
            var frequency_txt = "${activeTaskBo.frequency}";
            if (frequency_txt != '' && frequency_txt != null && typeof frequency_txt
                != 'undefined') {
              $("#previousFrequency").val(frequency_txt);
            }
            $('#onetimexdaysId').val('');
            $('#onetimeydaysId').val('');
          } else if (val == 'Manually Schedule') {
            $('.manually').find('input:text').val('');
            isValidManuallySchedule = true;
            $('.manually-option:not(:first)').find('.remBtnDis').click();
            $('.manually-option').find('input').val('');
            $('.manually-option').find('.startTime').prop('disabled', true);
            $('.manually-option').find('.endTime').prop('disabled', true);
            
            $('.manually-anchor-option:not(:first)').find('.remBtnDis').click();
            $('.manually-anchor-option').find('input').val('');
            $('.manually-anchor-option').find('.startTime').prop('disabled', true);
            $('.manually-anchor-option').find('.endTime').prop('disabled', true);
          } else if (val == 'Daily') {
            $("#startDate").val('');
            $("#days").val('');
            $("#endDateId").text('NA');
            $("#lifeTimeId").text('-');
            $('.dailyClock').val('');
            $('.dailyClock:not(:first)').parent().parent().remove();
            multiTimeVal = true;
            $('#dailyxdaysId').val('');
          } else if (val == 'Weekly') {
            $("#startDateWeekly").val('');
            $("#weeklyFreId").val('');
            $("#activeTaskFrequenciesBo.frequencyTime").val('');
            $("#startWeeklyDate").val('');
            $("#weeks").val('');
            $("#weekEndDate").text('NA');
            $("#weekLifeTimeEnd").text('-');
            $("#selectWeeklyTime").val('');
            $('#weeklyxdaysId').val('');
          } else if (val == 'Monthly') {
            $("#monthFreId").val('');
            $("#selectMonthlyTime").val('');
            $("#pickStartDate").val('');
            $("#months").val('');
            $("#monthEndDate").text('NA');
            $("#monthLifeTimeDate").text('-');
            $('#monthlyxdaysId').val('');
          }
        }
      } else {
        $('.oneTime').find('input:text').val('');
        $(".daily").find('input:text').val('');
        $(".week").find('input:text').val('');
        $("#startDateWeekly").val('');
        $(".month").find('input:text').val('');
        $('.manually').find('input:text').val('');
        $("#isLaunchStudy").val('');
        $("#isStudyLifeTime").val('');
        $("#monthEndDate").text('NA');
        $("#monthLifeTimeDate").text('-');
        $("#weekEndDate").text('NA');
        $("#weekLifeTimeEnd").text('-');
        $("#endDateId").text('NA');
        $("#lifeTimeId").text('-');
        $('.manually-option:not(:first)').find('.remBtnDis').click();
        $('.manually-option').find('input').val('');
        $('.dailyClock').val('');
        $('.dailyClock:not(:first)').parent().parent().remove();
        $('.manually-option').find('.startTime').prop('disabled', true);
        $('.manually-anchor-option').find('.startTime').prop('disabled', true);
         $('.manually-option').find('.endTime').prop('disabled', true);
        $('.manually-anchor-option').find('.endTime').prop('disabled', true);
      }
      var flag = 'schedule';
      setFrequencyVal(flag);
      //AnchorDate start
      var scheduletype = $('input[name="scheduleType"]:checked').val();
      if (scheduletype == 'AnchorDate') {
    	  
        var element = $('#anchorDateId').find('option:selected').text();
        setAnchorDropdown(val, element);
        if (val == 'One time') {
          $(".onetimeanchorClass").show();
          $(".onetimeanchorClass").find('input:text').attr('required', true);
        }
        if (val == 'Daily') {
          $(".dailyanchorDiv").show();
          $(".dailyanchorDiv").find('input:text').attr('required', true);
        }
        if (val == 'Weekly') {
          $(".weeklyanchorDiv").show();
          $(".weeklyanchorDiv").find('input:text').attr('required', true);
        }
        if (val == 'Monthly') {
          $(".monthlyanchorDiv").show();
          $(".monthlyanchorDiv").find('input:text').attr('required', true);
        }
        if (val == 'Manually Schedule') {
          $(".manuallyAnchorContainer").show();
          $(".manuallyAnchorContainer").find('input:text').attr('required', true);
        }
        $('.regularClass').hide();
        $('.regularClass').find('input:text').removeAttr('required');
        $('.anchortypeclass').show();
        $('.anchortypeclass').find('input:select').attr('required', true);
        $('.selectpicker').selectpicker('refresh');
        $('.dailyStartCls').hide();
        $('.dailyStartCls').find('input:text').removeAttr('required');
        $('.weeklyStartCls').hide();
        $('.weeklyStartCls').find('input:text,select').removeAttr('required');
        $('.monthlyStartCls').hide();
        $('.monthlyStartCls').find('input:text').removeAttr('required');
        $(".manuallyContainer").hide();
        $(".manuallyContainer").find('input:text').removeAttr('required');
      } else {
        $(".onetimeanchorClass").hide();
        $('.onetimeanchorClass').find('input:text').removeAttr('required');
        $('.regularClass').show();
        $('.regularClass').find('input:text').attr('required', true);

        $('.dailyStartCls').show();
        $('.dailyStartCls').find('input:text').attr('required', true);
        $(".dailyanchorDiv").hide();
        $(".dailyanchorDiv").find('input:text').removeAttr('required', true);

        $('.weeklyStartCls').show();
        $('.weeklyStartCls').find('input:text,select').attr('required', true);
        $(".weeklyanchorDiv").hide();
        $(".weeklyanchorDiv").find('input:text').removeAttr('required', true);

        $('.monthlyStartCls').show();
        $('.monthlyStartCls').find('input:text').attr('required', true);
        $(".monthlyanchorDiv").hide();
        $(".monthlyanchorDiv").find('input:text').removeAttr('required', true);

        $('.manuallyContainer').show();
        $('.manuallyContainer').find('input:text').attr('required', true);
        $(".manuallyAnchorContainer").hide();
        $(".manuallyAnchorContainer").find('input:text').removeAttr('required', true);
        $('.anchortypeclass').hide();
        $('.anchortypeclass').removeAttr('required');
        $("#anchorDateId").val("");

      }
      //AnchorDate type end
    });
    if (frequencey != null && frequencey != "" && typeof frequencey != 'undefined') {
      $(".all").addClass("dis-none");
      if (frequencey == 'One time') {
        $(".oneTime").removeClass("dis-none");
      } else if (frequencey == 'Manually Schedule') {
        $(".manually").removeClass("dis-none");
      } else if (frequencey == 'Daily') {
        $(".daily").removeClass("dis-none");
      } else if (frequencey == 'Weekly') {
        $(".week").removeClass("dis-none");
      } else if (frequencey == 'Monthly') {
        $(".month").removeClass("dis-none");
      }
      var scheduletype = $('input[name="scheduleType"]:checked').val();
      if (scheduletype != '' && scheduletype != null && typeof scheduletype != 'undefined'
          && scheduletype == 'AnchorDate') {
        if (frequencey == 'One time') {
          $(".onetimeanchorClass").show();
          $('#chooseDate').removeAttr('required');
          $("#selectTime1").removeAttr('required');
          $(".onetimeanchorClass").find('input:text').attr('required', true);
          $('.anchortypeclass').find('input:select').attr('required', true);
          $('.selectpicker').selectpicker('refresh');
        }
        if (frequencey == 'Daily') {
          $(".dailyanchorDiv").show();
          $(".dailyanchorDiv").find('input:text').attr('required', true);
        }
        if (frequencey == 'Weekly') {
          $(".weeklyanchorDiv").show();
          $(".weeklyanchorDiv").find('input:text').attr('required', true);
        }
        if (frequencey == 'Monthly') {
          $(".monthlyanchorDiv").show();
          $(".monthlyanchorDiv").find('input:text').attr('required', true);
        }
        if (frequencey == 'Manually Schedule') {
          $(".manuallyAnchorContainer").show();
          $(".manuallyAnchorContainer").find('input:text').attr('required', true);
        }
        $('.regularClass').hide();
        $('.regularClass').find('input:text').removeAttr('required');
        $('.anchortypeclass').show();
        $('.anchortypeclass').find('input:text').attr('required', true);
        $('.dailyStartCls').hide();
        $('.dailyStartCls').find('input:text').removeAttr('required');
        $('.weeklyStartCls').hide();
        $('.weeklyStartCls').find('input:text,select').removeAttr('required');
        $('.monthlyStartCls').hide();
        $('.monthlyStartCls').find('input:text').removeAttr('required');
        $(".manuallyContainer").hide();
        $(".manuallyContainer").find('input:text').removeAttr('required');
      }

    } else {
      $(".onetimeanchorClass").hide();
      $('.onetimeanchorClass').find('input:text').removeAttr('required');
      $('.regularClass').show();
      $('.regularClass').find('input:text').attr('required', true);

      $('.dailyStartCls').show();
      $('.dailyStartCls').find('input:text').attr('required', true);
      $(".dailyanchorDiv").hide();
      $(".dailyanchorDiv").find('input:text').removeAttr('required', true);

      $('.weeklyStartCls').show();
      $('.weeklyStartCls').find('input:text,select').attr('required', true);
      $(".weeklyanchorDiv").hide();
      $(".weeklyanchorDiv").find('input:text').removeAttr('required', true);

      $('.monthlyStartCls').show();
      $('.monthlyStartCls').find('input:text').attr('required', true);
      $(".monthlyanchorDiv").hide();
      $(".monthlyanchorDiv").find('input:text').removeAttr('required', true);

      $('.manuallyContainer').show();
      $('.manuallyContainer').find('input:text').attr('required', true);
      $(".manuallyAnchorContainer").hide();
      $(".manuallyAnchorContainer").find('input:text,select').removeAttr('required', true);
      $('.anchortypeclass').hide();
      $('.anchortypeclass').removeAttr('required');
    }

    $('#chooseDate').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      minDate: serverDate(),
      useCurrent: false
    })
        .on("dp.change", function (e) {
          if (e.date._d)
            $("#chooseEndDate").data("DateTimePicker").clear().minDate(new Date(e.date._d));
          else
            $("#chooseEndDate").data("DateTimePicker").minDate(serverDate());
        });

    $(document).on('change dp.change ', '.dailyClock', function () {

      $('.dailyContainer').find('.dailyTimeDiv').each(function () {
        var chkVal = true;
        var thisDailyTimeDiv = $(this);
        var thisAttr = $(this).find('.dailyClock');
        $('.dailyContainer').find('.dailyTimeDiv').each(function () {
          if (!thisDailyTimeDiv.is($(this)) && $(this).find('.dailyClock').val()) {
            if ($(this).find('.dailyClock').val() == thisAttr.val()) {
              if (chkVal)
                chkVal = false;
            }
          }
        });
        if (!chkVal) {
          var test =thisAttr.parents('.dailyTimeDiv').find('.dailyClock').parent().find(".help-block").find("ul").length;
          if(test === 0){
          	thisAttr.parents('.dailyTimeDiv').find('.dailyClock').parent().find(".help-block").append(
              	$("<ul><li> </li></ul>").attr("class","list-unstyled").attr("style","white-space:nowrap").text("Please select a time that has not yet added"));
          }
        } else {
          thisAttr.parents('.dailyTimeDiv').find('.dailyClock').parent().find(".help-block").empty();
        }
      });
      var a = 0;
      $('.dailyContainer').find('.dailyTimeDiv').each(function () {
        if ($(this).find('.dailyClock').parent().find('.help-block.with-errors').children().length
            > 0) {
          a++;
        }
      });
      multiTimeVal = !(a > 0);
    });

    $('#chooseEndDate').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      minDate: serverDate(),
      useCurrent: false,
    });

    $('#startDate').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      useCurrent: false,
    }).on("dp.change", function (e) {
      var startDate = $("#startDate").val();
      var days = $("#days").val();
      var endDate = ''
      if (startDate && days && days > 0) {
        var dt = new Date(startDate);
        dt.setDate(dt.getDate() + Number(days) - 1);
        endDate = formatDate(dt);
      } else {
        startDate = '';
        endDate = '';
      }
      $("#studyDailyLifetimeEnd").val(endDate);
      $("#lifeTimeId").text(startDate + ' - ' + endDate);
      $("#endDateId").text(endDate);
    }).on("dp.show", function (e) {
      $('#startDate').data("DateTimePicker").minDate(serverDate());
    });
    $('#startDateMonthly').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      useCurrent: false,
    }).on("dp.show", function (e) {
      $('#startDateMonthly').data("DateTimePicker").minDate(serverDate());
    }).on("dp.change", function (e) {
      if (e.date._d != $('#pickStartDate').data("DateTimePicker").date()) {
        $('#pickStartDate').val('');
      }
      var dateArr = [];
      for (var i = new Date(e.date._d).getFullYear(); i < 2108; i++) {
        for (var j = 0; j < 12; j++) {
          var allowedDate = new Date(i, j, new Date(e.date._d).getDate());
          if (allowedDate.getMonth() !== j) {
            allowedDate = new Date(i, j + 1, 0);
          }
          dateArr.push(allowedDate);
        }
      }
      $('#pickStartDate').data("DateTimePicker").enabledDates(dateArr);
    });

    $(".clock").not('.cursor-none').datetimepicker({
      format: 'h:mm a',
      useCurrent: false,
    });
    $(document).on('dp.change', '.cusStrDate', function (e) {
      if (e.date._d) {
        var nxtDate = moment(new Date(e.date._d)).add(1, 'days');
      }
      if (!$(this).parents('.manually-option').find('.cusEndDate').data("DateTimePicker")) {
        customEndDate($(this).parents('.manually-option').find('.cusEndDate').attr('id'), 0);
      }
      if (nxtDate)
        $(this).parents('.manually-option').find('.cusEndDate').val('').data(
            "DateTimePicker").minDate(nxtDate);
    });
    $(document).on('dp.change change', '.cusStrDate, .cusEndDate', function () {
    	if ($(this).parents('.manually-option').find('.cusStrDate').val()) {
            $(this).parents('.manually-option').find('.startTime').prop('disabled', false);
          }else if($(this).parents('.manually-option').find('.cusEndDate').val()){
                  $(this).parents('.manually-option').find('.endTime').prop('disabled', false);
          
      } else {
    	  $(this).parents('.manually-option').find('.startTime').prop('disabled', true);
          $(this).parents('.manually-option').find('.endTime').prop('disabled', true);
      }
      resetValidation($(this).parents('form'));
    });

    $('#pickStartDate').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',

      useCurrent: false,
      ignoreReadonly: true
    }).on("dp.change", function (e) {
      var pickStartDate = $("#pickStartDate").val();
      var months = $("#months").val();
      $('#pickStartDate').attr("readonly", true);
      if ((pickStartDate != null && pickStartDate != '' && typeof pickStartDate != 'undefined')
          && (months != null && months != '' && typeof months != 'undefined')) {
        var dt = new Date(pickStartDate);
        endDate = moment(moment(dt).add(Number(months), 'M')).format("MM/DD/YYYY");
        $("#studyMonthlyLifetimeEnd").val(endDate);
        $("#monthEndDate").text(endDate);
        $("#monthLifeTimeDate").text(pickStartDate + ' - ' + endDate);
      }
    }).on("click", function (e) {
      $('#pickStartDate').data("DateTimePicker").minDate(serverDate());
    });
    $('#startWeeklyDate').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      useCurrent: false,
      ignoreReadonly: true
    }).on("dp.change", function (e) {
      var weeklyDate = $("#startWeeklyDate").val();
      var weeks = $("#weeks").val();
      console.log("weeklyDate:" + weeklyDate);
      console.log("weeks:" + weeks);
      $('#startWeeklyDate').attr("readonly", true);
      if ((weeklyDate != null && weeklyDate != '' && typeof weeklyDate != 'undefined') && (weeks
          != null && weeks != '' && typeof weeks != 'undefined')) {
        var dt = new Date(weeklyDate);
        var weekcount = Number(weeks) * 7;
        console.log(weekcount)
        dt.setDate(dt.getDate() + Number(weekcount));
        endDate = formatDate(dt);
        $("#studyWeeklyLifetimeEnd").val(endDate);
        $("#weekEndDate").text(endDate);
        $("#weekLifeTimeEnd").text(weeklyDate + ' - ' + endDate);
      }
    }).on("click", function (e) {
      $('#startWeeklyDate').data("DateTimePicker").minDate(serverDate());
    });
    $('.customCalnder').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      minDate: serverDate(),
      useCurrent: false,
    });
    var daysOfWeek = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
    $("#startDateWeekly").on('change', function () {
      var weekDay = $("#startDateWeekly").val();
      var weeks = [];
      for (var i = 0; i < daysOfWeek.length; i++) {
        if (weekDay != daysOfWeek[i]) {
          weeks.push(i);
        }
      }
      $('#startWeeklyDate').data("DateTimePicker").destroy();
      $('#startWeeklyDate').not('.cursor-none, :disabled').datetimepicker({
        format: 'MM/DD/YYYY',
        minDate: serverDate(),
        daysOfWeekDisabled: weeks,
        useCurrent: false,
        ignoreReadonly: true
      }).on("dp.change", function (e) {
        var weeklyDate = $("#startWeeklyDate").val();
        var weeks = $("#weeks").val();
        if ((weeklyDate != null && weeklyDate != '' && typeof weeklyDate != 'undefined') && (weeks
            != null && weeks != '' && typeof weeks != 'undefined')) {
          var dt = new Date(weeklyDate);
          var weekcount = Number(weeks) * 7;
          console.log(weekcount)
          dt.setDate(dt.getDate() + Number(weekcount));
          endDate = formatDate(dt);
          $("#studyWeeklyLifetimeEnd").val(endDate);
          $("#weekEndDate").text(endDate);
          $("#weekLifeTimeEnd").text(weeklyDate + ' - ' + endDate);
        }
      });
      $('#startWeeklyDate').val('');
    });

    $("#days").on('change', function () {
      console.log("change");
      var startDate = $("#startDate").val();
      var days = $("#days").val();
      var endDate = ''
      if (startDate && days && days > 0) {
        var dt = new Date(startDate);
        dt.setDate(dt.getDate() + Number(days) - 1);
        endDate = formatDate(dt);
      } else {
        startDate = '';
        endDate = '';
      }
      $("#studyDailyLifetimeEnd").val(endDate).trigger('contentchanged');
      $("#lifeTimeId").text(startDate + ' - ' + endDate);
      $("#endDateId").text(endDate);
    })

    $("#weeks").on('change', function () {
      var weeklyDate = $("#startWeeklyDate").val();
      var weeks = $("#weeks").val();
      console.log("weeklyDate:" + weeklyDate);
      console.log("weeks:" + weeks);
      if ((weeklyDate != null && weeklyDate != '' && typeof weeklyDate != 'undefined') && (weeks
          != null && weeks != '' && typeof weeks != 'undefined')) {
        var dt = new Date(weeklyDate);
        var weekcount = Number(weeks) * 7;
        console.log(weekcount)
        dt.setDate(dt.getDate() + Number(weekcount));
        endDate = formatDate(dt);
        $("#studyWeeklyLifetimeEnd").val(endDate);
        $("#weekEndDate").text(endDate).trigger('contentchanged');
        $("#weekLifeTimeEnd").text(weeklyDate + ' - ' + endDate);
      }
    });
    $("#months").on('change', function () {
      var pickStartDate = $("#pickStartDate").val();
      var months = $("#months").val();
      if ((pickStartDate != null && pickStartDate != '' && typeof pickStartDate != 'undefined')
          && (months != null && months != '' && typeof months != 'undefined')) {
        var dt = new Date(pickStartDate);
        var monthCount = Number(months) * 30;
        endDate = moment(moment(dt).add(Number(months), 'M')).format("MM/DD/YYYY");
        $("#studyMonthlyLifetimeEnd").val(endDate);
        $("#monthEndDate").text(endDate).trigger('contentchanged');
        $("#monthLifeTimeDate").text(pickStartDate + ' - ' + endDate);
      }
    });
    $("#isLaunchStudy").change(function () {
      var scheduletype = $('input[name="scheduleType"]:checked').val();
      if (!$("#isLaunchStudy").is(':checked')) {
        if (scheduletype != '' && scheduletype != null && typeof scheduletype != 'undefined'
            && scheduletype == 'AnchorDate') {
          $("#onetimexdaysId").prop('disabled', false);
          $("#selectTime").attr("disabled", false);
          $("#selectTime").required = true;
          $("#onetimexdaysId").required = true;
        } else {
          $("#chooseDate").attr("disabled", false);
          $("#selectTime1").attr("disabled", false);
          $("#chooseDate").required = false;
          $("#selectTime1").required = false;
          $('#chooseDate').datetimepicker({
            format: 'MM/DD/YYYY',
            minDate: serverDate(),
            useCurrent: false,
          })
              .on("dp.change", function (e) {
                if (e.date._d)
                  $("#chooseEndDate").data("DateTimePicker").clear().minDate(new Date(e.date._d));
                else
                  $("#chooseEndDate").data("DateTimePicker").minDate(serverDate());
              });
        }
      } else {
        if (scheduletype == 'AnchorDate') {
          $("#selectTime").attr("disabled", true);
          $("#selectTime").required = false;
          $("#onetimexdaysId").prop('disabled', true);
          $("#onetimexdaysId").required = false;
        } else {
          $("#chooseDate").attr("disabled", true);
          $("#selectTime1").attr("disabled", true);
          $("#chooseDate").required = true;
          $("#selectTime1").required = true;
        }
      }
      resetValidation($(this).parents('form'));
    });
    $("#isStudyLifeTime").change(function () {
      var scheduletype = $('input[name="scheduleType"]:checked').val();
      if (!$("#isStudyLifeTime").is(':checked')) {
        if (scheduletype != '' && scheduletype != null && typeof scheduletype != 'undefined'
            && scheduletype == 'AnchorDate') {
          $("#onetimeydaysId").prop('disabled', false);
          $('#onetimeydaysId').parent().removeClass('has-error has-danger').find(
              ".help-block").empty();
          resetValidation($('#onetimeydaysId').parents('form'));
        } else {
          $("#chooseEndDate").attr("disabled", false);
          $("#chooseEndDate").required = false;
          $('#chooseEndDate').datetimepicker({
            format: 'MM/DD/YYYY',
            minDate: serverDate(),
            useCurrent: false,
          });
          $("#chooseEndDate").val('');
        }
      } else {
        if (scheduletype == 'AnchorDate') {
          $("#onetimeydaysId").prop('disabled', true);
        } else {
          $("#chooseEndDate").attr("disabled", true);
          $("#chooseEndDate").required = true;
          $("#chooseEndDate").val('');
        }
      }
      resetValidation($(this).parents('form'));
    });
    disablePastTime('#selectWeeklyTime', '#startWeeklyDate');
    disablePastTime('#selectMonthlyTime', '#startDateMonthly');
    disablePastTime('#selectTime1', '#chooseDate');

    $(document).on('click change dp.change', '.cusStrDate, .startTime', function (e) {
        if ($(this).is('.startTime') && !$(this).prop('disabled')) {
        disablePastTime('#' + $(this).attr('id'),
            '#' + $(this).parents('.manually-option').find('.cusStrDate').attr('id'));
      }
        if ($(this).is('.endTime') && !$(this).prop('disabled')) {
            disablePastTime('#' + $(this).attr('id'),
                '#' + $(this).parents('.manually-option').find('.cusStrDate').attr('id'));
          }
          if ($(this).is('.cusStrDate') && !$(this).parents('.manually-option').find('.startTime').prop(
          'disabled')) {
        	  disablePastTime('#' + $(this).parents('.manually-option').find('.startTime').attr('id'),
            '#' + $(this).attr('id'));
      }
    });
    $(document).on('click change dp.change', '.dailyClock, #startDate', function (e) {
      var dt = $('#startDate').val();
      var date = new Date();
      var day = date.getDate() >= 10 ? date.getDate() : ('0' + date.getDate());
      var month = (date.getMonth() + 1) >= 10 ? (date.getMonth() + 1) : ('0' + (date.getMonth()
          + 1));
      var today = moment(serverDate()).format("MM/DD/YYYY");
      $('.time-opts').each(function () {
        var id = $(this).attr("id");
        var timeId = '#time' + id;
        $(timeId).data("DateTimePicker").minDate(false);
        if (dt) {
          if (dt != today) {
            $(timeId).data("DateTimePicker").minDate(false);
          } else {
            $(timeId).data("DateTimePicker").minDate(serverDateTime());
          }
          if ($(timeId).val() && dt == today && moment($(timeId).val(), 'h:mm a').toDate()
              < serverDateTime()) {
            $(timeId).val('');
          }
        } else {
          $(timeId).data("DateTimePicker").minDate(false);
        }
      });
    });
    $('#anchorDateId').change(function () {
      var frequency_text = $('input[name="frequency"]:checked').val();
      var element = $(this).find('option:selected').text();
      setAnchorDropdown(frequency_text, element);
    });

  });

  function disablePastTime(timeId, dateId) {
    $(document).on('click change dp.change', timeId + ', ' + dateId, function () {
      var dt = $(dateId).val();
      var date = new Date();
      var day = date.getDate() >= 10 ? date.getDate() : ('0' + date.getDate());
      var month = (date.getMonth() + 1) >= 10 ? (date.getMonth() + 1) : ('0' + (date.getMonth()
          + 1));
      var today = moment(serverDate()).format("MM/DD/YYYY");
      if (dt) {
        if (dt != today) {
          $(timeId).data("DateTimePicker").minDate(false);
        } else {
          $(timeId).data("DateTimePicker").minDate(serverDateTime());
        }
        if ($(timeId).val() && dt == today && moment($(timeId).val(), 'h:mm a').toDate()
            < serverDateTime()) {
          $(timeId).val('');
        }
      } else {
        $(timeId).data("DateTimePicker").minDate(false);
      }
    });
  }

  function formatDate(date) {
    var d = new Date(date),
        month = '' + (d.getMonth() + 1),
        day = '' + d.getDate(),
        year = d.getFullYear();

    if (month.length < 2) month = '0' + month;
    if (day.length < 2) day = '0' + day;

    return [month, day, year].join('/');
  }

  function addTime() {
	  $('.dailyTimeDiv').find(".delete ").css("visibility", "visible");
    count = count + 1;
    var newTime = "<div class='time-opts mt-md dailyTimeDiv' id=" + count + ">" +
        "  <span class='form-group m-none dis-inline vertical-align-middle pr-md'>" +
        "  <input id='time" + count + "' type='text' required name='activeTaskFrequenciesList["
        + count
        + "].frequencyTime' placeholder='Time' class='form-control clock dailyClock' data-error='Please fill out this field'  placeholder='00:00' onclick='timep(this.id);'/>"
        +
        "<span class='help-block with-errors red-txt'></span>" +
        " </span>" +
        "  <span class='addBtnDis addbtn mr-sm align-span-center' onclick='addTime();'>+</span>" +
        " <span class='delete vertical-align-middle remBtnDis hide pl-md align-span-center' onclick='removeTime(this);'></span>"
        +
        "</div>";
    $(".time-opts:last").after(newTime);
    $(".time-opts").parents("form").validator("destroy");
    $(".time-opts").parents("form").validator();
    if ($('.time-opts').length > 1) {
      $(".remBtnDis").removeClass("hide");
    } else {
      $(".remBtnDis").addClass("hide");
    }
    timep('time' + count);
    $('#time' + count).val("");
    var flag = 'schedule';
    setFrequencyVal(flag);
    $('#' + count).find('input:first').focus();
  }

  function removeTime(param) {
    $(param).parents(".time-opts").remove();
    $(".time-opts").parents("form").validator("destroy");
    $(".time-opts").parents("form").validator();
    if ($('.time-opts').length > 1) {
      $(".remBtnDis").removeClass("hide");
    } else if ($('.dailyTimeDiv').length == 1) {
	   	 $('.dailyTimeDiv').find(".delete").css("visibility", "hidden");
    } else {
      $(".remBtnDis").addClass("hide");
    }
    $(document).find('.dailyClock').trigger('dp.change');
    var flag = 'schedule';
    setFrequencyVal(flag);
  }

  function addDate() {
	$('.manually-option').find(".delete").css("visibility", "visible");
    customCount =  parseInt(customCount) + 1;
    
    $("#AddButton").hide();
    $("#AddButton").attr('required', false);
    
    var newDateCon = "<div class='manually-option mb-md form-group' id='RegDate" + customCount + "'>"
        + "  <span class='form-group dis-inline vertical-align-middle pr-md'>"
        + "  <input id='StartDate" + customCount + "' type='text' count='" + customCount
        + "' required name='activeTaskCustomScheduleBo[" + customCount
        + "].frequencyStartDate' class='form-control calendar customCalnder cusStrDate' data-error='Please fill out this field' placeholder='Start date' onclick='customStartDate(this.id,"
        + customCount + ");'/>"
        + "	<span class='help-block with-errors red-txt'></span>"
        + "  </span>"
        
        + "  <span class='form-group dis-inline vertical-align-middle pr-md'>"
        + "  <input id='customStartTime" + customCount + "' type='text' count='" + customCount
        + "' required name='activeTaskCustomScheduleBo[" + customCount
        + "].frequencyStartTime' class='form-control clock customTime startTime cusTime' data-error='Please fill out this field' placeholder='Start time' onclick='timep(this.id);' disabled/>"
        + "<span class='help-block with-errors red-txt'></span>"
        + "  </span>"
        
        + "  <span class='gray-xs-f mb-sm pr-md align-span-center'>"
        + "  to "
        + "  </span>"
        + "  <span class='form-group dis-inline vertical-align-middle pr-md'>"
        + "  <input id='EndDate" + customCount + "' type='text' count='" + customCount
        + "' required name='activeTaskCustomScheduleBo[" + customCount
        + "].frequencyEndDate' class='form-control calendar customCalnder cusEndDate' data-error='Please fill out this field' placeholder='End date' onclick='customEndDate(this.id,"
        + customCount + ");'/>"
        + "<span class='help-block with-errors red-txt'></span>"
        + "  </span>"
        + "  <span class='form-group dis-inline vertical-align-middle pr-md'>"
        + "  <input id='customTime" + customCount + "' type='text' count='" + customCount
        + "' required name='activeTaskCustomScheduleBo[" + customCount
        + "].frequencyEndTime' class='form-control clock customTime endTime' data-error='Please fill out this field'  placeholder='End time' onclick='timep(this.id);' disabled/>"
        + "<span class='help-block with-errors red-txt'></span>"
        + "  </span>"
        + "  <span class='addbtn addBtnDis align-span-center mr-md' onclick='addDate();'>+</span>"
        + "  <span id='delete' class='sprites_icon delete vertical-align-middle remBtnDis hide align-span-center' onclick='removeDate(this);'></span>"
        + "</div>";

        if ($('.manually-option').length > 1) {
            $('.manuallyContainer').find(".remBtnDis").removeClass("hide");
          } else {
            $('.manuallyContainer').find(".remBtnDis").addClass("hide");
          }
        
    $(".manually-option:last").after(newDateCon);
    $(".manually-option").parents("form").validator("destroy");
    $(".manually-option").parents("form").validator();
   
    customStartDate('StartDate' + customCount, customCount);
    customEndDate('EndDate' + customCount, customCount);
    timep('customStartTime' + customCount);
    timep('customTime' + customCount);

    $('#customStartTime' + customCount).val("");
    $('#customTime' + customCount).val("");
    
    $('#customTime' + customCount).val("");
  }

  function removeDate(param) {
    $(param).parents(".manually-option").remove();
	if($('.manually-option').length == 1){
	    	 $('.manually-option').find(".delete").css("visibility", "hidden");
	    	 $('#AddButton').show();
	}
    $(".manually-option").parents("form").validator("destroy");
    $(".manually-option").parents("form").validator();
    if ($('.manually-option').length > 1) {
      $('.manuallyContainer').find(".remBtnDis").removeClass("hide");
    } else if ( $('.manually-option').length == 1 ) {
   	 $('.manually-option').find(".delete").css("visibility", "hidden");
    } else {
      $('.manuallyContainer').find(".remBtnDis").addClass("hide");
    }
    
    if( $('.manually-option').filter(function() {
        return $(this).css('display') !== 'none';}).length == 1){
      $('.manually-option').find(".delete").css("visibility", "hidden");
    }
    
    $(document).find('.startTime').trigger('dp.change');
  }

  function timep(item) {
    $('#' + item).not('.cursor-none').datetimepicker({
      format: 'h:mm a',
      useCurrent: false,
    });
  }

  function customStartDate(id, count) {
	  $('.manually-option').find('.startTime').prop('disabled', false);
    $('.cusStrDate').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      minDate: serverDate(),
      useCurrent: false,
    }).on("dp.change", function (e) {
      $("#" + id).parent().removeClass("has-danger").removeClass("has-error");
      $("#" + id).parent().find(".help-block").empty();
      $("#EndDate" + count).parent().removeClass("has-danger").removeClass("has-error");
      $("#EndDate" + count).parent().find(".help-block").empty();
      var startDate = $("#" + id).val();
      var endDate = $("#EndDate" + count).val();
      if (startDate != '' && endDate != '' && toJSDate(startDate) > toJSDate(endDate)) {
        $("#" + id).parent().addClass("has-danger").addClass("has-error");
        $("#" + id).parent().find(".help-block").text(
        '<ul class="list-unstyled"><li>Start Date and Time Should not be greater than End Date and Time</li></ul>');
      } else {
        $("#activeTaskId").parent().removeClass("has-danger").removeClass("has-error");
        $("#activeTaskId").parent().find(".help-block").empty();
        $("#EndDate" + count).parent().removeClass("has-danger").removeClass("has-error");
        $("#EndDate" + count).parent().find(".help-block").empty();

      }
    });
  }

  function customEndDate(id, count) {
    $('.manually-option').find('.endTime').prop('disabled', false);
    $('.cusEndDate').not('.cursor-none, :disabled').datetimepicker({
      format: 'MM/DD/YYYY',
      minDate: serverDate(),
      useCurrent: false,
    }).on("dp.change", function (e) {
      $('#' + id).parent().removeClass("has-danger").removeClass("has-error");
      $('#' + id).parent().find(".help-block").empty();
      $("#StartDate" + count).parent().removeClass("has-danger").removeClass("has-error");
      $("#StartDate" + count).parent().find(".help-block").empty();
      var startDate = $("#StartDate" + count).val();
      var endDate = $('#' + id).val();
      if (startDate != '' && endDate != '' && toJSDate(startDate) > toJSDate(endDate)) {
        $('#' + id).parent().addClass("has-danger").addClass("has-error");
        $('#' + id).parent().find(".help-block").empty().append(
            	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                      "End Date and Time Should not be less than Start Date and Time"));
      } else {
        $('#' + id).parent().removeClass("has-danger").removeClass("has-error");
        $('#' + id).parent().find(".help-block").empty();
        $("#StartDate" + count).parent().removeClass("has-danger").removeClass("has-error");
        $("#StartDate" + count).parent().find(".help-block").empty();
      }
    });
  }

  function toJSDate(dateTime) {
    if (dateTime != null && dateTime != '' && typeof dateTime != 'undefined') {
      var date = dateTime.split("/");
      return new Date(date[2], (date[0] - 1), date[1]);
    }
  }

  function isNumber(evt, thisAttr) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if ((charCode < 48 && charCode > 57) || (charCode >= 65 && charCode <= 90) || (charCode >= 97
        && charCode <= 122)) {
      return false;
    }
    if ((!$(thisAttr).val()) && charCode == 48) {
      return false;
    }
    return true;
  }

  function saveActiveTask(item, actType, callback) {
    var id = $("#activeTaskId").val();
    var study_id = $("#studyId").val();
    var title_text = $("#title").val();
    var frequency_text = $('input[name="frequency"]:checked').val();
    var schedule_text = $('input[name="scheduleType"]:checked').val();
    var previous_frequency = $("#previousFrequency").val();
    var isFormValid = true;

    var study_lifetime_end = '';
    var study_lifetime_start = ''
    var repeat_active_task = ''
    var type_text = $("#type").val();
    var scheduletype = document.querySelector('input[name="scheduleType"]:checked').value;

    var activeTask = new Object();
    var anchorDateId = $("#anchorDateId option:selected").val();
    if (anchorDateId != null && anchorDateId != '' && typeof anchorDateId != 'undefined') {
      activeTask.anchorDateId = anchorDateId;
    }
    if (id != null && id != '' && typeof id != 'undefined') {
      activeTask.id = id;
    }
    if (frequency_text != null && frequency_text != '' && typeof frequency_text != 'undefined') {
      activeTask.frequency = frequency_text;
    }
    if (study_id != null && study_id != '' && typeof study_id != 'undefined') {
      activeTask.studyId = study_id;
    }
    if (title_text != null && title_text != '' && typeof title_text != 'undefined') {
      activeTask.title = title_text;
    }
    if (previous_frequency != null && previous_frequency != '' && typeof previous_frequency
        != 'undefined') {
      activeTask.previousFrequency = previous_frequency;
    } else {
      activeTask.previousFrequency = frequency_text;
    }
    if (type_text != null && type_text != '' && typeof type_text != 'undefined') {
      activeTask.type = type_text;
    }
    if (schedule_text != null && schedule_text != '' && typeof schedule_text != 'undefined') {
      activeTask.scheduleType = schedule_text;
    }

    var activeTaskFrequencey = new Object();

    if (frequency_text == 'One time') {

      var frequence_id = $("#oneTimeFreId").val();
      var frequency_date = $("#chooseDate").val();
      var freQuence_time = $("#selectTime1").val();
      if ($('#isLaunchStudy').is(':checked')) {
        var isLaunch_study = true;
      }
      if ($('#isStudyLifeTime').is(':checked')) {
        var isStudy_lifeTime = true;
      }

      study_lifetime_end = $("#chooseEndDate").val();
      if (study_lifetime_end != null && study_lifetime_end != '' && typeof study_lifetime_end
          != 'undefined') {
        activeTask.activeTaskLifetimeEnd = study_lifetime_end;
      }
      if (frequence_id != null && frequence_id != '' && typeof frequence_id != 'undefined') {
        activeTaskFrequencey.id = frequence_id;
      }
      if (frequency_date != null && frequency_date != '' && typeof frequency_date != 'undefined') {
        activeTaskFrequencey.frequencyDate = frequency_date;
        activeTask.activeTaskLifetimeStart = frequency_date;
      }
      if (freQuence_time != null && freQuence_time != '' && typeof freQuence_time != 'undefined') {
        activeTaskFrequencey.frequencyTime = freQuence_time;
      }
      if (isLaunch_study != null && isLaunch_study != '' && typeof isLaunch_study != 'undefined') {
        activeTaskFrequencey.isLaunchStudy = isLaunch_study;
      }
      if (isStudy_lifeTime != null && isStudy_lifeTime != '' && typeof isStudy_lifeTime
          != 'undefined') {
        activeTaskFrequencey.isStudyLifeTime = isStudy_lifeTime;
      }
      if (id != null && id != '' && typeof id != 'undefined') {
        activeTaskFrequencey.activeTaskId = id;
      }
      if (scheduletype == 'AnchorDate') {
        var onetimeXSign = $('#onetimeXSign').val();
        var onetimeXSignVal = $('#onetimexdaysId').val();
        var onetimeYSign = $('#onetimeYSign').val();
        var onetimeYSignVal = $('#onetimeydaysId').val();
        if (onetimeXSign != null && onetimeXSign != '' && typeof onetimeXSign != 'undefined') {
          var xval = true;
          if (onetimeXSign == '0')
            xval = false;
          activeTaskFrequencey.xDaysSign = xval;
        }
        if (onetimeXSignVal != null && onetimeXSignVal != '' && typeof onetimeXSignVal
            != 'undefined') {
          activeTaskFrequencey.timePeriodFromDays = onetimeXSignVal;
        }
        if (onetimeYSign != null && onetimeYSign != '' && typeof onetimeYSign != 'undefined') {
          var yval = true;
          if (onetimeYSign == '0')
            yval = false;
          activeTaskFrequencey.yDaysSign = yval;
        }
        if (onetimeYSignVal != null && onetimeYSignVal != '' && typeof onetimeYSignVal
            != 'undefined') {
          activeTaskFrequencey.timePeriodToDays = onetimeYSignVal;
        }
        var freQuence_time = $("#selectTime").val();
        if (freQuence_time != null && freQuence_time != '' && typeof freQuence_time
            != 'undefined') {
          activeTaskFrequencey.frequencyTime = freQuence_time;
        }

        if ($('#isLaunchStudy').is(':checked')) {
          activeTaskFrequencey.timePeriodFromDays = null;
          activeTaskFrequencey.xDaysSign = true;
          activeTaskFrequencey.frequencyTime = null;
        }
        if ($('#isStudyLifeTime').is(':checked')) {
          activeTaskFrequencey.timePeriodToDays = null;
          activeTaskFrequencey.yDaysSign = true;
        }
        activeTaskFrequencey.frequencyDate = null;
      } else {
        activeTask.anchorDateId = null;
        activeTaskFrequencey.timePeriodFromDays = null;
        activeTaskFrequencey.xDaysSign = true;
        activeTaskFrequencey.timePeriodToDays = null;
        activeTaskFrequencey.yDaysSign = true;
      }
      activeTask.activeTaskFrequenciesBo = activeTaskFrequencey;
      isFormValid = validateTime($("#chooseDate").not('.cursor-none, :disabled'),
          $("#selectTime").not('.cursor-none, :disabled'));
    } else if (frequency_text == 'Manually Schedule') {
      var customArray = new Array();
      isFormValid = isValidManuallySchedule;
      if (scheduletype == 'AnchorDate') {
        $('.manually-anchor-option').each(function () {
          var activeTaskCustomFrequencey = new Object();
          activeTaskCustomFrequencey.activeTaskId = id;
          var id = $(this).attr("id").replace('AnchorDate','');
          var xSign = $('#xSign' + id).val();
          var xSignVal = $('#xdays' + id).val();
          var ySign = $('#ySign' + id).val();
          var ySignVal = $('#ydays' + id).val();
          var manualStartTime = $("#manualStartTime" + id).val();
          var manualEndTime = $("#manualEndTime" + id).val();
          var isUsed = $("#isUsed" + id).val();

          activeTaskCustomFrequencey.frequencyStartDate = null;
          activeTaskCustomFrequencey.frequencyEndDate = null;
          if (manualStartTime != null && manualStartTime != '' && typeof manualStartTime != 'undefined') {
              activeTaskCustomFrequencey.frequencyStartTime = manualStartTime;
            }
            if (manualEndTime != null && manualEndTime != '' && typeof manualEndTime != 'undefined') {
              activeTaskCustomFrequencey.frequencyEndTime = manualEndTime;
          }
          if (isUsed) {
            activeTaskCustomFrequencey.used = isUsed;
          }
          if (xSign != null && xSign != '' && typeof xSign != 'undefined') {
            var xval = true;
            if (xSign == '0')
              xval = false;
            activeTaskCustomFrequencey.xDaysSign = xval;
          }
          if (xSignVal != null && xSignVal != '' && typeof xSignVal != 'undefined') {
            activeTaskCustomFrequencey.timePeriodFromDays = xSignVal;
          }
          if (ySign != null && ySign != '' && typeof ySign != 'undefined') {
            var yval = true;
            if (ySign == '0')
              yval = false;
            activeTaskCustomFrequencey.yDaysSign = yval;
          }
          if (ySignVal != null && ySignVal != '' && typeof ySignVal != 'undefined') {
            activeTaskCustomFrequencey.timePeriodToDays = ySignVal;
          }
          customArray.push(activeTaskCustomFrequencey)
        })
        activeTask.activeTaskCustomScheduleBo = customArray;
      } else {
        $('.manually-option').each(function () {
          var activeTaskCustomFrequencey = new Object();
          activeTaskCustomFrequencey.activeTaskId = id;
          var id = $(this).attr("id").replace('RegDate','');
          var startdate = $("#StartDate" + id).val();
          var enddate = $("#EndDate" + id).val();
          var startTime = $("#customStartTime" + id).val();
          var endTime = $("#customTime" + id).val();
          var isUsed = $("#isUsed" + id).val();
          if (startdate != null && startdate != '' && typeof startdate != 'undefined') {
            activeTaskCustomFrequencey.frequencyStartDate = startdate;
          }
          if (enddate != null && enddate != '' && typeof enddate != 'undefined') {
            activeTaskCustomFrequencey.frequencyEndDate = enddate;
          }
          if (startTime != null && startTime != '' && typeof startTime != 'undefined') {
              activeTaskCustomFrequencey.frequencyStartTime = startTime;
            }
            
            if (endTime != null && endTime != '' && typeof endTime != 'undefined') {
              activeTaskCustomFrequencey.frequencyEndTime = endTime;
          }
          if (isUsed) {
            activeTaskCustomFrequencey.used = isUsed;
          }
          customArray.push(activeTaskCustomFrequencey)
        })
        activeTask.activeTaskCustomScheduleBo = customArray;
        if (isValidManuallySchedule) {
          $(document).find('.manually-option').each(function () {
            var returnFlag = validateTime(
                $(this).find(".cusStrDate").not('.cursor-none, :disabled'),
                $(this).find(".startTime").not('.cursor-none, :disabled'));
            if (isFormValid) {
              isFormValid = returnFlag;
            }
          });
        }
      }
    } else if (frequency_text == 'Daily') {
      isFormValid = multiTimeVal;
      var frequenceArray = new Array();
      study_lifetime_start = $("#startDate").val();
      repeat_active_task = $("#days").val();
      study_lifetime_end = $("#endDateId").text();

      var dailyXSign = $('#dailyXSign').val();
      var dailyXSignVal = $('#dailyxdaysId').val();
      //1st record dailyxsign need to store
      var count = 0;
      $('.time-opts').each(function () {
        var activeTaskFrequencey = new Object();
        var id = $(this).attr("id").replace('AnchorDate','');
        var frequence_time = $('#time' + id).val();
        if (frequence_time != null && frequence_time != '' && typeof frequence_time
            != 'undefined') {
          activeTaskFrequencey.frequencyTime = frequence_time;
        }

        if (dailyXSign != null && dailyXSign != '' && typeof dailyXSign != 'undefined' && count
            == 0) {
          var xval = true;
          if (dailyXSign == '0')
            xval = false;
          activeTaskFrequencey.xDaysSign = xval;
          if (dailyXSignVal != null && dailyXSignVal != '' && typeof dailyXSignVal != 'undefined') {
            activeTaskFrequencey.timePeriodFromDays = dailyXSignVal;
            activeTaskFrequencey.timePeriodToDays = null;
            activeTaskFrequencey.yDaysSign = true;
          } else {
            activeTaskFrequencey.timePeriodFromDays = null;
            activeTaskFrequencey.xDaysSign = true;
            activeTaskFrequencey.timePeriodToDays = null;
            activeTaskFrequencey.yDaysSign = true;
          }
          count = 1;
        }
        frequenceArray.push(activeTaskFrequencey);
      });
      activeTask.activeTaskFrequenciesList = frequenceArray;
      if (study_lifetime_start != null && study_lifetime_start != '' && typeof study_lifetime_start
          != 'undefined') {
        activeTask.activeTaskLifetimeStart = study_lifetime_start;
      }
      if (study_lifetime_end != null && study_lifetime_end != '' && typeof study_lifetime_end
          != 'undefined') {
        activeTask.activeTaskLifetimeEnd = study_lifetime_end;
      }
      if (repeat_active_task != null && repeat_active_task != '' && typeof repeat_active_task
          != 'undefined') {
        activeTask.repeatActiveTask = repeat_active_task;
      }
      activeTask.activeTaskFrequenciesBo = activeTaskFrequencey;

      if (multiTimeVal && $('#dailyFormId').find('.numChk').val() && $('#dailyFormId').find(
          '.numChk').val() == 0 || !validateTime(
          $(document).find("#startDate").not('.cursor-none, :disabled'),
          $(document).find(".dailyClock").not('.cursor-none, :disabled'))) {
        isFormValid = false;
      }
    } else if (frequency_text == 'Weekly') {

      var frequence_id = $("#weeklyFreId").val();
      study_lifetime_start = $("#startWeeklyDate").val();
      var frequence_time = $("#selectWeeklyTime").val();
      var frequence_time_anchor = $("#selectWeeklyTimeAnchor").val();
      var dayOftheweek = $("#startDateWeekly").val();
      repeat_active_task = $("#weeks").val();
      repeat_active_task_anchor = $("#weeksAnchor").val();
      study_lifetime_end = $("#weekEndDate").text();
      var weeklyXSign = $('#weeklyXSign').val();
      var weeklyXSignVal = $('#weeklyxdaysId').val();

      if (dayOftheweek != null && dayOftheweek != '' && typeof dayOftheweek != 'undefined') {
        activeTask.dayOfTheWeek = dayOftheweek;
      }
      if (study_lifetime_start != null && study_lifetime_start != '' && typeof study_lifetime_start
          != 'undefined') {
        activeTask.activeTaskLifetimeStart = study_lifetime_start;
      }
      if (study_lifetime_end != null && study_lifetime_end != '' && typeof study_lifetime_end
          != 'undefined') {
        activeTask.activeTaskLifetimeEnd = study_lifetime_end;
      }
      if (repeat_active_task != null && repeat_active_task != '' && typeof repeat_active_task
          != 'undefined') {
        activeTask.repeatActiveTask = repeat_active_task;
      }
      if (repeat_active_task_anchor != null && repeat_active_task_anchor != ''
          && typeof repeat_active_task_anchor != 'undefined') {
        activeTask.repeatActiveTask = repeat_active_task_anchor;
      }
      if (id != null && id != '' && typeof id != 'undefined') {
        activeTaskFrequencey.activeTaskId = id;
      }
      if (frequence_id != null && frequence_id != '' && typeof frequence_id != 'undefined') {
        activeTaskFrequencey.id = frequence_id;
      }
      if (frequence_time != null && frequence_time != '' && typeof frequence_time != 'undefined') {
        activeTaskFrequencey.frequencyTime = frequence_time;
      }
      if (frequence_time_anchor != null && frequence_time_anchor != ''
          && typeof frequence_time_anchor != 'undefined' && activeTask.scheduleType == 'AnchorDate') {
        activeTaskFrequencey.frequencyTime = frequence_time_anchor;
      }
      if (weeklyXSign != null && weeklyXSign != '' && typeof weeklyXSign != 'undefined') {
        var xval = true;
        if (weeklyXSign == '0')
          xval = false;
        activeTaskFrequencey.xDaysSign = xval;
      }
      if (weeklyXSignVal != null && weeklyXSignVal != '' && typeof weeklyXSignVal != 'undefined') {
        activeTaskFrequencey.timePeriodFromDays = weeklyXSignVal;
        activeTaskFrequencey.timePeriodToDays = null;
        activeTaskFrequencey.yDaysSign = true;
      } else {
        activeTaskFrequencey.timePeriodFromDays = null;
        activeTaskFrequencey.xDaysSign = true;
        activeTaskFrequencey.timePeriodToDays = null;
        activeTaskFrequencey.yDaysSign = true;
      }
      activeTask.activeTaskFrequenciesBo = activeTaskFrequencey;
      if ($('#weeklyFormId').find('.numChk').val() && $('#weeklyFormId').find('.numChk').val() == 0
          || !validateTime($(document).find("#startWeeklyDate").not('.cursor-none, :disabled'),
              $(document).find("#selectWeeklyTime").not('.cursor-none, :disabled'))) {
        isFormValid = false;
      }
    } else if (frequency_text == 'Monthly') {

      var frequence_id = $("#monthFreId").val();
      var frequencydate = $("#startDateMonthly").val();
      var frequencetime = $("#selectMonthlyTime").val();
      var frequencetime_anchor = $("#selectMonthlyTimeAnchor").val();
      study_lifetime_start = $("#pickStartDate").val();
      repeat_active_task = $("#months").val();
      repeat_active_task_anchor = $("#monthsAnchor").val();
      study_lifetime_end = $("#monthEndDate").text();
      var monthlyXSign = $('#monthlyXSign').val();
      var monthlyXSignVal = $('#monthlyxdaysId').val();

      if (study_lifetime_start != null && study_lifetime_start != '' && typeof study_lifetime_start
          != 'undefined') {
        activeTask.activeTaskLifetimeStart = study_lifetime_start;
      }
      if (study_lifetime_end != null && study_lifetime_end != '' && typeof study_lifetime_end
          != 'undefined') {
        activeTask.activeTaskLifetimeEnd = study_lifetime_end;
      }
      if (repeat_active_task != null && repeat_active_task != '' && typeof repeat_active_task
          != 'undefined') {
        activeTask.repeatActiveTask = repeat_active_task;
      }
      if (repeat_active_task_anchor != null && repeat_active_task_anchor != ''
          && typeof repeat_active_task_anchor != 'undefined') {
        activeTask.repeatActiveTask = repeat_active_task_anchor;
      }
      if (id != null && id != '' && typeof id != 'undefined') {
        activeTaskFrequencey.activeTaskId = id;
      }
      if (frequence_id != null && frequence_id != '' && typeof frequence_id != 'undefined') {
        activeTaskFrequencey.id = frequence_id;
      }
      if (frequencydate != null && frequencydate != '' && typeof frequencydate != 'undefined') {
        activeTaskFrequencey.frequencyDate = frequencydate;
      }
      if (frequencetime != null && frequencetime != '' && typeof frequencetime != 'undefined') {
        activeTaskFrequencey.frequencyTime = frequencetime;
      }

      if (frequencetime_anchor != null && frequencetime_anchor != '' && typeof frequencetime_anchor
          != 'undefined' && activeTask.scheduleType == 'AnchorDate') {
        activeTaskFrequencey.frequencyTime = frequencetime_anchor;
      }

      if (monthlyXSign != null && monthlyXSign != '' && typeof monthlyXSign != 'undefined') {
        var xval = true;
        if (monthlyXSign == '0')
          xval = false;
        activeTaskFrequencey.xDaysSign = xval;
      }
      if (monthlyXSignVal != null && monthlyXSignVal != '' && typeof monthlyXSignVal
          != 'undefined') {
        activeTaskFrequencey.timePeriodFromDays = monthlyXSignVal;
        activeTaskFrequencey.timePeriodToDays = null;
        activeTaskFrequencey.yDaysSign = true;
      } else {
        activeTaskFrequencey.timePeriodFromDays = null;
        activeTaskFrequencey.xDaysSign = true;
        activeTaskFrequencey.timePeriodToDays = null;
        activeTaskFrequencey.yDaysSign = true;
      }
      activeTask.activeTaskFrequenciesBo = activeTaskFrequencey;
      if ($('#monthlyFormId').find('.numChk').val() && $('#monthlyFormId').find('.numChk').val()
          == 0 || !validateTime(
              $(document).find("#startDateMonthly").not('.cursor-none, :disabled'),
              $(document).find("#selectMonthlyTime").not('.cursor-none, :disabled'))) {
        isFormValid = false;
      }
    }
    console.log("activeTask:" + JSON.stringify(activeTask));
    var data = JSON.stringify(activeTask);
    $(item).prop('disabled', true);
    if (study_id && isFormValid) {
      if (actType !== 'save') {
        console.log("inside schedule");
        var activetaskType = $('#targetOptionId').val();
        console.log("activetaskType::" + activetaskType);
        $("body").addClass("loading");
        $.ajax({
          url: "/studybuilder/adminStudies/saveActiveTaskSchedule.do?_S=${param._S}",
          type: "POST",
          datatype: "json",
          data: {activeTaskScheduleInfo: data},
          beforeSend: function (xhr, settings) {
            xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
          },
          success: function (data) {
            var message = data.message;
            if (message == "SUCCESS") {
              var activeTaskId = data.activeTaskId;
              var activeTaskFrequenceId = data.activeTaskFrequenceId;
              
              var activeTaskCreated=data.activeTaskCreated
              $("#activeTaskId, #taskId,#taskContentId,.activeTaskIdClass").val(activeTaskId);
              $("#activeTaskCreated").val(activeTaskCreated);
              $("#previousFrequency").val(frequency_text);
              if (frequency_text == 'One time') {
                $("#oneTimeFreId").val(activeTaskFrequenceId);
              } else if (frequency_text == 'Weekly') {
                $("#weeklyFreId").val(activeTaskFrequenceId);
              } else if (frequency_text == 'Monthly') {
                $("#monthFreId").val(activeTaskFrequenceId);
              }
              frequencey = frequency_text;
              if (callback)
                callback(true);
            } else {
              if (callback)
                callback(false);
            }
          },
          error: function (xhr, status, error) {
            $("body").removeClass("loading");
            if (callback)
              callback(false);
          },
          complete: function () {
            $(item).prop('disabled', false);
          },
          global: false
        });
      } else {
        $("body").addClass("loading");
        $.ajax({
          url: "/studybuilder/adminStudies/saveActiveTaskSchedule.do?_S=${param._S}",
          type: "POST",
          datatype: "json",
          data: {activeTaskScheduleInfo: data},
          beforeSend: function (xhr, settings) {
            xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
          },
          success: function (data) {
            var message = data.message;
            if (message == "SUCCESS") {
              var activeTaskId = data.activeTaskId;
              var activeTaskFrequenceId = data.activeTaskFrequenceId;
              var activeTaskCreated=data.activeTaskCreated
              $("#activeTaskId, #taskId,#taskContentId,.activeTaskIdClass").val(activeTaskId);
              $("#activeTaskCreated").val(activeTaskCreated);
              $("#previousFrequency").val(frequency_text);
              if (frequency_text == 'One time') {
                $("#oneTimeFreId").val(activeTaskFrequenceId);
              } else if (frequency_text == 'Weekly') {
                $("#weeklyFreId").val(activeTaskFrequenceId);
              } else if (frequency_text == 'Monthly') {
                $("#monthFreId").val(activeTaskFrequenceId);
              }
              frequencey = frequency_text;
              if (callback)
                callback(true);
            } else {
              if (callback)
                callback(false);
            }
          },
          error: function (xhr, status, error) {
            $("body").removeClass("loading");
            if (callback)
              callback(false);
          },
          complete: function () {
            $(item).prop('disabled', false);
          },
          global: false
        });
      }
    } else {
      $("body").removeClass("loading");
      $(item).prop('disabled', false);
      if (callback)
        callback(false);
    }
  }

  function checkDateRange() {
	  $(document).on('dp.change change', '.cusStrDate, .cusEndDate, .startTime, .endTime', function (e) {
	      var chkVal = true;
	      var chkValFromDate=true;
	      var chkValToDate = true;
	      if ($(this).parents('.manually-option').find('.cusStrDate').val() && $(this).parents('.manually-option').find(
	          '.startTime').val()) {
	        var thisAttr = this;
	        $(this).parents('.manuallyContainer').find('.manually-option').each(function () {
	          if ((!$(thisAttr).parents('.manually-option').is($(this))) && $(this).find(
	              '.cusStrDate').val() && $(this).find('.cusEndDate').val() && $(this).find(
	              '.startTime').val()) {
	            
				
				 var fromDate = moment($(this).find('.cusStrDate').val(), "MM/DD/YYYY").toDate();
	              var startTime = moment($(this).find('.startTime').val(), "HH:mm").toDate()
	              fromDate.setHours(startTime.getHours());
	              fromDate.setMinutes(startTime.getMinutes());
	              var toDate = moment($(this).find('.cusEndDate').val(), "MM/DD/YYYY").toDate();
	              var endTime = moment($(this).find('.endTime').val(), "HH:mm").toDate()
	              toDate.setHours(endTime.getHours());
	              toDate.setMinutes(endTime.getMinutes());
	              var thisFromDate = moment(
	                  $(thisAttr).parents('.manually-option').find('.cusStrDate').val(),
	                  "MM/DD/YYYY").toDate();
	              var thisStartTime = moment($(thisAttr).parents('.manually-option').find('.startTime').val(),
	                  "HH:mm").toDate()
	              thisFromDate.setHours(thisStartTime.getHours());
	              thisFromDate.setMinutes(thisStartTime.getMinutes());
	              var thisToDate = moment(
	                  $(thisAttr).parents('.manually-option').find('.cusEndDate').val(),
	                  "MM/DD/YYYY").toDate();
	              var thisEndTime = moment($(thisAttr).parents('.manually-option').find('.endTime').val(),
	              "HH:mm").toDate()
	              thisToDate.setHours(thisEndTime.getHours());
	              thisToDate.setMinutes(thisEndTime.getMinutes());
				  
				  
				  
	            if (chkValFromDate && chkValToDate){
	            	 chkValFromDate = !(thisFromDate >= fromDate && thisFromDate <= toDate) 
	            	 chkValToDate = !(thisToDate >= fromDate && thisToDate <= toDate);
	            }
	             
	          }
	        });
	      }
	       if (!chkValFromDate) {
	          $(thisAttr).parents('.manually-option').find('.startTime').parent().addClass(
	              'has-error has-danger').find(".help-block").removeClass('with-errors').empty().append(
	              	$("<ul><li> </li></ul>").attr("class","list-unstyled").attr("style","font-size: 10px;").text(
	                     "Please ensure that the runs created do not have any overlapping time period."));
	        } 
	      else if (!chkValToDate) {
	        $(thisAttr).parents('.manually-option').find('.endTime').parent().addClass(
	              'has-error has-danger').find(".help-block").removeClass('with-errors').empty().append(
	              	$("<ul><li> </li></ul>").attr("class","list-unstyled").attr("style","font-size: 10px;").text(
	                     "Please ensure that the runs created do not have any overlapping time period."));
	        } else {
	        $(thisAttr).parents('.manually-option').find('.startTime').parent().removeClass(
	            'has-error has-danger').find(".help-block").addClass('with-errors').empty();
	        $(thisAttr).parents('.manually-option').find('.endTime').parent().removeClass(
	        'has-error has-danger').find(".help-block").addClass('with-errors').empty(); 
	      }
	      var a = 0;
	      $('.manuallyContainer').find('.manually-option').each(function () {
	        if ($(this).find('.cusStartTime').parent().find('.help-block').children().length > 0) {
	          a++;
	          $(this).find('.cusStartTime').val('');
	        }
	      });
	      var b = 0;
	      $('.manuallyContainer').find('.manually-option').each(function () {
	          if ($(this).find('.endTime').parent().find('.help-block').children().length > 0) {
	            b++;
	            $(this).find('.endTime').val('');
	          }
	        });
	      isValidManuallySchedule = !(a > 0 &&  b>0);
	    });
	    return isValidManuallySchedule;
	  }
  
  function doneActiveTask(item, actType, callback) {
    var frequency = $('input[name="frequency"]:checked').val();
    console.log("frequency:" + frequency);
    var scheduletype = document.querySelector('input[name="scheduleType"]:checked').value;
    var anchorForm = true;
    var onetimeForm = true;
    var valForm = false;
    var anchorDateForm = true;
    if (actType !== 'save') {
      if (scheduletype == 'AnchorDate') {
        if (!isFromValid("#anchorFormId"))
          anchorForm = false;
      }
      if (frequency == 'One time') {
        $("#frequencyId").val(frequency);
        if (isFromValid("#oneTimeFormId")) {
          valForm = true;
        }
        var x = $("#onetimexdaysId").val();
        var y = $("#onetimeydaysId").val();
        if (x != null && x != '' && typeof x != 'undefined'
            && y != null && y != '' && typeof y != 'undefined') {
          onetimeForm = chkDaysValid(true);
        }
        if (scheduletype == 'AnchorDate') {
          if ($('#isLaunchStudy').is(':checked') && $('#isStudyLifeTime').is(':checked')) {
            anchorDateForm = false;
          }
        }
      } else if (frequency == 'Manually Schedule') {
        $("#customfrequencyId").val(frequency);
        if (isFromValid("#customFormId")) {
          valForm = true;
        }
      } else if (frequency == 'Daily') {
        $("#dailyFrequencyId").val(frequency);
        if (isFromValid("#dailyFormId")) {
          valForm = true;
        }
      } else if (frequency == 'Weekly') {
        $("#weeklyfrequencyId").val(frequency);
        if (isFromValid("#weeklyFormId")) {
          valForm = true;
        }
      } else if (frequency == 'Monthly') {
        $("#monthlyfrequencyId").val(frequency);
        if (isFromValid("#monthlyFormId")) {
          valForm = true;
        }
      }
    } else {
      valForm = true;
    }
    if (valForm && anchorForm && onetimeForm) {
      if (anchorDateForm) {
        saveActiveTask(item, actType, function (val) {
          if (!val) {
            $('.scheduleTaskClass a').tab('show');
          }
          callback(val);
        });
      } else {
        showErrMsg("Please choose anchor date for date/time of the launch");
        $('.scheduleTaskClass a').tab('show');
        if (callback)
          callback(false);
      }
    } else {
      showErrMsg("Please fill in all mandatory fields");
      $('.scheduleTaskClass a').tab('show');
      if (callback)
        callback(false);
    }
  }

  function setFrequencyVal(flag) {
    var frequencyType = $('input[name=frequency]:checked').val();
    if (frequencyType) {
      $('.activeaddToChartText').hide();
      if (frequencyType == 'One time') {
        $('.chartSection').hide();

        $('.addLineChartBlock_number_of_kicks_recorded_fetal').css("display", "none");
        $('.addLineChartBlock_number_of_kicks_recorded_fetal').find('.requireClass').prop(
            'required', false);
        $('#number_of_kicks_recorded_fetal_chart_id').val(false);
        $('#number_of_kicks_recorded_fetal_chart_id').prop('checked', false);

        $('.addLineChartBlock_number_of_moves_tower').css("display", "none");
        $('.addLineChartBlock_number_of_moves_tower').find('.requireClass').prop('required', false);
        $('#number_of_moves_tower_chart_id').val(false);
        $('#number_of_moves_tower_chart_id').prop('checked', false);

        $('.addLineChartBlock_Score_spatial').css("display", "none");
        $('.addLineChartBlock_Score_spatial').find('.requireClass').prop('required', false);
        $('#Score_spatial_chart_id').val(false);
        $('#Score_spatial_chart_id').prop('checked', false);

        $('.addLineChartBlock_Number_of_Games_spatial').css("display", "none");
        $('.addLineChartBlock_Number_of_Games_spatial').find('.requireClass').prop('required',
            false);
        $('#Number_of_Games_spatial_chart_id').val(false);
        $('#Number_of_Games_spatial_chart_id').prop('checked', false);

        $('.addLineChartBlock_Number_of_Failures_spatial').css("display", "none");
        $('.addLineChartBlock_Number_of_Failures_spatial').find('.requireClass').prop('required',
            false);
        $('#Number_of_Failures_spatial_chart_id').val(false);
        $('#Number_of_Failures_spatial_chart_id').prop('checked', false);
      } else {
        $('.chartSection').show();
        if (frequencyType == 'Daily') {
          var dailyTimeLength = $('.dailyContainer').find('.dailyTimeDiv').length;
          if (dailyTimeLength == 1) {
            $("#chartId,#chartId1,#chartId2").append(
                "<option value='Days of the current week'>Days of the current week</option>");
            $("#chartId,#chartId1,#chartId2").append(
                "<option value='Days of the current month'>Days of the current month</option>");
          } else {
            $("#chartId,#chartId1,#chartId2").append(
                "<option value='24 hours of current day'>24 hours of current day</option>");
          }
        }
        if (frequencyType == 'Weekly') {
          $("#chartId,#chartId1,#chartId2").append(
              "<option value='Weeks of the current month'>Weeks of the current month</option>");
        }
        if (frequencyType == 'Monthly') {
          $("#chartId,#chartId1,#chartId2").append(
              "<option value='Months of the current year'>Months of the current year</option>");
        }
        if (frequencyType == 'Manually Schedule') {
          $("#chartId,#chartId1,#chartId2").append("<option value='Run-based'>Run-based</option>");
          $('.activeaddToChartText').show();
          $('.activeaddToChartText').text(
              'A max of x runs will be displayed in each view of the chart.')
        }
      }
      $('#chartId,#chartId1,#chartId2').selectpicker('refresh');
    }
  }

  function validateTime(dateRef, timeRef) {
    var tm = $('#timepicker1').val();
    var dt;
    var valid = true;
    dateRef.each(function () {
      dt = dateRef.val();
      if (dt) {
        dt = moment(dt, "MM/DD/YYYY").toDate();
        if (dt < serverDate()) {
          $(this).parent().addClass('has-error has-danger');
          $(this).data("DateTimePicker").clear();
        } else {
          $(this).parent().removeClass('has-error has-danger').find('.help-block.with-errors').empty();
        }
        timeRef.each(function () {
          if ($(this).val()) {
            thisDate = moment($(this).val(), "h:mm a").toDate();
            dt.setHours(thisDate.getHours());
            dt.setMinutes(thisDate.getMinutes());
            if (dt < serverDateTime()) {
              $(this).data("DateTimePicker").clear();
              $(this).parent().addClass('has-error has-danger');
              if (valid)
                valid = false;
            } else {
            }
          }
        });
      }
    });
    return valid;
  }

  function validateCustTime(dateRef, timeRef) {
    var dt;
    var valid = true;
    dateRef.each(function () {
      dt = dateRef.val();
      if (dt) {
        dt = moment(dt, "MM/DD/YYYY").toDate();
        if (dt < serverDate()) {
          $(this).parent().addClass('has-error has-danger');
          $(this).data("DateTimePicker").clear();
        } else {
          $(this).parent().removeClass('has-error has-danger').find('.help-block.with-errors').empty();
        }
        timeRef.each(function () {
          if ($(this).val()) {
            thisDate = moment($(this).val(), "h:mm a").toDate();
            dt.setHours(thisDate.getHours());
            dt.setMinutes(thisDate.getMinutes());
            if (dt < serverDateTime()) {
              $(this).data("DateTimePicker").clear();
              $(this).parent().addClass('has-error has-danger');
              if (valid)
                valid = false;
            } else {
            }
          }
        });
      }
    });
    return valid;
  }

  var updateLogoutCsrf = function () {
    $('#logoutCsrf').val('${_csrf.token}');
    $('#logoutCsrf').prop('name', '${_csrf.parameterName}');
  }
  var chkEndDateWithDate = function (couterRef, endDateRef) {
    var dt = endDateRef.text();
    var valid = true;
    if (dt && (couterRef.val() !== 0)) {
      if (moment(dt, "MM/DD/YYYY").toDate() < serverDateTime()) {
        couterRef.parent().addClass('has-error has-danger').find('.help-block.with-errors').empty().append(
            	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                "Please ensure the End Date/Time is greater than current date/time"));
        valid = false;
      } else {
        couterRef.parent().removeClass('has-error has-danger').find('.help-block.with-errors').empty();
      }
    }
    return valid;
  }

  function chkDaysValid(clickDone) {
    var x = $("#onetimexdaysId").val();
    var y = $("#onetimeydaysId").val();
    var xSign = $('#onetimeXSign').val();
    var ySign = $('#onetimeYSign').val();
    if (xSign === '0') {
      x = "+" + x;
    } else if (xSign === '1') {
      x = "-" + x;
    }
    if (ySign === '0') {
      y = "+" + y;
    } else if (ySign === '1') {
      y = "-" + y;
    }
    var valid = true;
    if (y && x) {
      if (parseInt(x) >= parseInt(y)) {
        if (clickDone && isFromValid($('#onetimeydaysId').parents('form')))
          $('#onetimeydaysId').focus();
        $('#onetimeydaysId').parent().addClass('has-error has-danger').find(
            ".help-block").empty().append(
            $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Y should be greater than X"));
        valid = false;
      } else {
        $('#onetimeydaysId').parent().removeClass('has-error has-danger').find(".help-block").empty();
        resetValidation($('#onetimeydaysId').parents('form'));
      }
    }
    return valid;
  }

  function addDateAnchor(customCountIndex) {
	$('.manually-anchor-option').find(".delete").css("visibility", "visible");
	customAnchorCount = customCountIndex + 1;
    var newDateCon = "<div class='manually-anchor-option mb-md form-group' id='AnchorDate" + customAnchorCount
        + "'>"
        + "<span class='mb-sm pr-md'><span class='light-txt opacity06'> Anchor date </span></span>"
        + "<span class='mr-xs'><select class='signDropDown selectpicker sign-box' count='" + customAnchorCount
        + "' title='Select' name='activeTaskCustomScheduleBo[" + customAnchorCount
        + "].xDaysSign' id='xSign" + customAnchorCount + "'>"
        + "<option value='0' selected>+</option><option value='1'>-</option>"
        + "</select></span>"
        + "<span class='form-group m-none dis-inline vertical-align-middle'>"
        + "<input id='xdays" + customAnchorCount
        + "' type='text' class='form-control wid70 disRadBtn1 disBtn1 remReqOnSave xdays daysMask mt-sm resetAncDate xancorText'"
        + "count='" + customAnchorCount + "' placeholder='X' name='activeTaskCustomScheduleBo["
        + customAnchorCount + "].timePeriodFromDays'"
        + "maxlength='3' required pattern='[0-9]+' data-pattern-error='Please enter valid number' data-error='Please fill out this field' data-type='xancorText'/><span class='help-block with-errors red-txt'></span>"
        + "</span>"
		+ "<span class='mb-sm pr-md'><span class='light-txt opacity06'> days</span>" 
        
        + "<span class='form-group  dis-inline vertical-align-middle pr-md' style='margin-bottom: -13px'>"
        + "<input id='manualStartTime" + customAnchorCount + "' type='text' count='" + customAnchorCount
        + "' class='form-control clock' name='activeTaskCustomScheduleBo[" + customAnchorCount
        + "].frequencyStartTime' placeholder='Start time' required data-error='Please fill out this field' />"
        + "<span class='help-block with-errors red-txt'></span>"
        + "</span>"
        
        +"<span class='light-txt opacity06'>"
        +"<span style='padding-right:5px;padding-left:5px'>to </span>  Anchor date </span></span>"
        + "<span class='mr-xs'><select class='signDropDown selectpicker sign-box' count='" + customAnchorCount
        + "' title='Select' name='activeTaskCustomScheduleBo[" + customAnchorCount
        + "].yDaysSign' id='ySign" + customAnchorCount + "'>"
        + "<option value='0' selected>+</option><option value='1'>-</option>"
        + "</select></span>"
        + "<span class='form-group m-none dis-inline vertical-align-middle'>"
        + "<input id='ydays" + customAnchorCount
        + "' type='text' class='form-control wid70 disRadBtn1 disBtn1 remReqOnSave ydays daysMask mt-sm resetAncDate yancorText' count='"
        + customAnchorCount + "' placeholder='Y'"
        + "name='activeTaskCustomScheduleBo[" + customAnchorCount
        + "].timePeriodToDays' maxlength='3' required pattern='[0-9]+' data-pattern-error='Please enter valid number' data-error='Please fill out this field' data-type='yancorText'/><span class='help-block with-errors red-txt'></span>"
        + "</span>"
        + "<span class='mb-sm pr-md'><span class='light-txt opacity06'> days </span></span>"
        + "<span class='form-group  dis-inline vertical-align-middle pr-md' style='margin-bottom: -13px'>"
        + "<input id='manualEndTime" + customAnchorCount + "' type='text' count='" + customAnchorCount
        + "' class='form-control clock' name='activeTaskCustomScheduleBo[" + customAnchorCount
        + "].frequencyEndTime' placeholder='End time' required data-error='Please fill out this field' />"
        + "<span class='help-block with-errors red-txt'></span>"
        + "</span>"
        + "<span class='addbtn addBtnDis align-span-center mr-md' onclick='addDateAnchor(customAnchorCount);'>+</span>"
        + "<span id='deleteAncchor' class='sprites_icon delete vertical-align-middle remBtnDis hide align-span-center' onclick='removeDateAnchor(this);'></span>"
        + "</div>";

    $(".manually-anchor-option:last").after(newDateCon);
    $(".manually-anchor-option").parents("form").validator("destroy");
    $(".manually-anchor-option").parents("form").validator();
    if ($('.manually-anchor-option').length > 1) {
      $('.manuallyAnchorContainer').find(".remBtnDis").removeClass("hide");
      if ($('#anchorDateId').find('option:selected').text() == 'Enrollment date') {
        setAnchorDropdown('Manually Schedule', 'Enrollment date');
      }
    } else {
      $('.manuallyAnchorContainer').find(".remBtnDis").addClass("hide");
    }
    timep('manualStartTime' + customAnchorCount);
    timep('manualEndTime' + customAnchorCount);
    
    $('#' + customAnchorCount).find('input:first').focus();
    $('.selectpicker').selectpicker('refresh');
  }

  function removeDateAnchor(param) {
    $(param).parents(".manually-anchor-option").remove();
    $(".manually-anchor-option").parents("form").validator("destroy");
    $(".manually-anchor-option").parents("form").validator();
    if ($('.manually-anchor-option').length > 1) {
      $('.manuallyAnchorContainer').find(".remBtnDis").removeClass("hide");
    } else if ( $('.manually-anchor-option').length == 1 ) {
   	 $('.manually-anchor-option').find(".delete").css("visibility", "hidden");
    } else {
      $('.manuallyAnchorContainer').find(".remBtnDis").addClass("hide");
    }
  }

  function setAnchorDropdown(frequency_text, anchorType) {
    if (anchorType == 'Enrollment date') {
      if (frequency_text == 'One time') {
        $('#onetimeXSign').children('option').remove();
        $('#onetimeXSign').append("<option value='0' selected>+</option>");
        $('#onetimeYSign').children('option').remove();
        $('#onetimeYSign').append("<option value='0' selected>+</option>");
      }
      if (frequency_text == 'Manually Schedule') {
        $('.manually-anchor-option').each(function () {
          var id = $(this).attr("id").replace('AnchorDate','');
          $("#xSign" + id).children('option').remove();
          $("#ySign" + id).children('option').remove();
          $("#xSign" + id).append("<option value='0' selected>+</option>");
          $("#ySign" + id).append("<option value='0' selected>+</option>");
        });
      }
      if (frequency_text == 'Daily') {
        $('#dailyXSign').children('option').remove();
        $('#dailyXSign').append("<option value='0' selected>+</option>");
      }
      if (frequency_text == 'Weekly') {
        $('#weeklyXSign').children('option').remove();
        $('#weeklyXSign').append("<option value='0' selected>+</option>");
      }
      if (frequency_text == 'Monthly') {
        $('#monthlyXSign').children('option').remove();
        $('#monthlyXSign').append("<option value='0' selected>+</option>");
      }
      $('.selectpicker').selectpicker('refresh');
    } else {
      if (frequency_text == 'One time') {
        $('#onetimeXSign').children('option').remove();
        $('#onetimeXSign').append(
            "<option value='0' selected>+</option><option value='1'>-</option>");
        $('#onetimeYSign').children('option').remove();
        $('#onetimeYSign').append(
            "<option value='0' selected>+</option><option value='1'>-</option>");
      }
      if (frequency_text == 'Manually Schedule') {
        $('.manually-anchor-option').each(function () {
          var id = $(this).attr("id").replace('AnchorDate','');
          $("#xSign" + id).children('option').remove();
          $("#ySign" + id).children('option').remove();
          $("#xSign" + id).append(
              "<option value='0' selected>+</option><option value='1'>-</option>");
          $("#ySign" + id).append(
              "<option value='0' selected>+</option><option value='1'>-</option>");
        });
      }
      if (frequency_text == 'Daily') {
        $('#dailyXSign').children('option').remove();
        $('#dailyXSign').append(
            "<option value='0' selected>+</option><option value='1'>-</option>");
      }
      if (frequency_text == 'Weekly') {
        $('#weeklyXSign').children('option').remove();
        $('#weeklyXSign').append(
            "<option value='0' selected>+</option><option value='1'>-</option>");
      }
      if (frequency_text == 'Monthly') {
        $('#monthlyXSign').children('option').remove();
        $('#monthlyXSign').append(
            "<option value='0' selected>+</option><option value='1'>-</option>");
      }
      $('.selectpicker').selectpicker('refresh');
    }
  }

  //# sourceURL=filename.js

  $(document).ready(function () {

    jQuery(document).on("keyup", ".xdays", function () {

      var xday = $(this).val()
      var parentId = $(this).parent().parent().attr("id").replace('AnchorDate','');
      var parent_id = parseInt(parentId);
      var xsign = $("#xSign" + parent_id).val() === "0" ? "+" : "-";
      var xdayValue = parseInt(xsign + "" + xday);
      var yday = $("#ydays" + parent_id).val();
      var ysign = $("#ySign" + parent_id).val() === "0" ? "+" : "-";
      var ydayValue = parseInt(ysign + "" + yday);

      if (parent_id === "0") {

        if (ydayValue !== "") {
          if (xdayValue >= ydayValue) {
            $(this).addClass("red-border");
            $("#ydays" + parent_id).addClass("red-border");
            $("#ydays" + parent_id).parent().addClass('has-error has-danger').find(
                ".help-block").empty().append(
                $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Y should be greater than X"));
            $(".addbtn").addClass("not-allowed");
          } else {
            $(this).removeClass("red-border");
            $("#ydays" + parent_id).removeClass("red-border");
            $("#ydays" + parent_id).parent().removeClass('has-error has-danger').find(
                ".help-block").empty();
            $(".addbtn").removeClass("not-allowed");
          }
        }

      } else {

        var pre_parent_id = $("#AnchorDate" + parent_id).prev().attr("id");
        if (pre_parent_id && pre_parent_id.indexOf("AnchorDate") >= 0) {
          pre_parent_id = pre_parent_id.replace('AnchorDate','');
        }

        var pre_parent = parseInt(pre_parent_id);
        var pyday = $("#ydays" + pre_parent).val();
        var pysign = $("#ySign" + parent_id).val() === "0" ? "+" : "-";
        var pydayValue = parseInt(pysign + "" + pyday);

        if (xdayValue <= pydayValue) {
          $(this).addClass("red-border");
          $("#ydays" + pre_parent).addClass("red-border");
          $(this).parent().addClass('has-error has-danger').find(".help-block").empty().append(
        	$("<ul><li> </li></ul>").attr("class","list-unstyled").text("X should be less than Y of the current row and greater than Y of the previous row"));
          $(".addbtn").addClass("not-allowed");
        } else {
          $(this).removeClass("red-border");
          $("#ydays" + pre_parent).removeClass("red-border");
          $(this).parent().removeClass('has-error has-danger').find(".help-block").empty();
          $(".addbtn").addClass("not-allowed");
          if (ydayValue !== "") {
            if (xdayValue >= ydayValue) {
              $(this).addClass("red-border");
              $("#ydays" + parent_id).addClass("red-border");
              $("#ydays" + parent_id).parent().addClass('has-error has-danger').find(
                  ".help-block").empty().append(
                $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Y should be greater than X"));
              $(".addbtn").addClass("not-allowed");
            } else {
              $(this).removeClass("red-border");
              $("#ydays" + parent_id).removeClass("red-border");
              $("#ydays" + parent_id).parent().removeClass('has-error has-danger').find(
                  ".help-block").empty();
              $(".addbtn").removeClass("not-allowed");
            }
          }
        }

      }

    });

    jQuery(document).on("change", ".xdays", function () {
      $(this).parent().parent().siblings().removeClass("current");
      $(this).parent().parent().addClass("current");

      $(".current").nextAll().remove();
      
      if( $('.manually-anchor-option').filter(function() {
    	    return $(this).css('display') !== 'none';}).length == 1){
    	 $("#AddButton").show();
    	 $('.manually-anchor-option').find(".delete").css("visibility", "hidden");
      }
    });

    jQuery(document).on("keyup", ".ydays", function () {

      var parent_id = $(this).parent().parent().attr("id").replace('AnchorDate','');
      var xsign = $("#xSign" + parent_id).val() === "0" ? "+" : "-";
      var xday = $("#xdays" + parent_id).val();
      var xdayValue = parseInt(xsign + "" + xday);
      var yday = $("#ydays" + parent_id).val();
      var ysign = $("#ySign" + parent_id).val() === "0" ? "+" : "-";
      var ydayValue = parseInt(ysign + "" + yday);

      if (ydayValue <= xdayValue) {
        $(this).addClass("red-border");
        $("#xdays" + parent_id).addClass("red-border");
        $("#ydays" + parent_id).parent().addClass('has-error has-danger').find(
            ".help-block").empty().append(
            $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Y should be greater than X"));
        $(this).parent().parent().siblings().removeClass("current");
        $(this).parent().parent().addClass("current");
        $(".current").nextAll().remove();
        $(".addbtn").addClass("not-allowed");
      } else {
        $(this).removeClass("red-border");
        $("#xdays" + parent_id).removeClass("red-border");
        $("#ydays" + parent_id).parent().removeClass('has-error has-danger').find(
            ".help-block").empty();
        $(".addbtn").removeClass("not-allowed");
      }

    });

    jQuery(document).on("change", ".ydays", function () {
      $(this).parent().parent().siblings().removeClass("current");
      $(this).parent().parent().addClass("current");
      $(".current").nextAll().remove();
      
      if( $('.manually-anchor-option').filter(function() {
    	    return $(this).css('display') !== 'none';}).length == 1){
    	 $("#AddButton").show();
    	 $('.manually-anchor-option').find(".delete").css("visibility", "hidden");
      }

    });

    jQuery(document).on("change", ".sign-box select", function () {
      var parent_id = $(this).attr("count");
      var signValue = $("#xSign" + parent_id).val();

      var xsign = signValue === "0" ? "+" : "-";
      var xday = $("#xdays" + parent_id).val();
      var xdayValue = parseInt(xsign + "" + xday);

      var yday = $("#ydays" + parent_id).val();
      var ysign = $("#ySign" + parent_id).val() === "0" ? "+" : "-";
      var ydayValue = parseInt(ysign + "" + yday);

      if (ydayValue <= xdayValue) {
        $("#xdays" + parent_id).addClass("red-border");
        $("#ydays" + parent_id).addClass("red-border");
        $("#ydays" + parent_id).parent().addClass('has-error has-danger').find(
            ".help-block").empty().append(
           $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Y should be greater than X"));
        $(".addbtn").addClass("not-allowed");
      } else {
        $("#xdays" + parent_id).removeClass("red-border");
        $("#ydays" + parent_id).removeClass("red-border");
        $("#ydays" + parent_id).parent().removeClass('has-error has-danger').find(
            ".help-block").empty();
        $(".addbtn").removeClass("not-allowed");
      }

      if ($('.manually-anchor-option').length > 1) {
        var pre_parent = $("#" + parent_id).prev().attr("id").replace('AnchorDate','');
        var pyday = $("#ydays" + pre_parent).val();
        var pysign = $("#ySign" + parent_id).val() === "0" ? "+" : "-";
        var pydayValue = parseInt(pysign + "" + pyday);

        if (xdayValue <= pydayValue) {
          $(this).addClass("red-border");
          $("#ydays" + pre_parent).addClass("red-border");
          $("#xdays" + parent_id).parent().addClass('has-error has-danger').find(
              ".help-block").empty().append(
             $("<ul><li> </li></ul>").attr("class","list-unstyled").text("X should be less than Y of the current row and greater than Y of the previous row"));
          $(".addbtn").addClass("not-allowed");
        } else {
          $(this).removeClass("red-border");
          $("#ydays" + pre_parent).removeClass("red-border");
          $("#xdays" + parent_id).parent().removeClass('has-error has-danger').find(
              ".help-block").empty();
          $(".addbtn").addClass("not-allowed");
        }
      }

      $(this).parent().parent().parent().siblings().removeClass("current");
      $(this).parent().parent().parent().addClass("current");

      $(".current").nextAll().remove();

    });
  });
</script>