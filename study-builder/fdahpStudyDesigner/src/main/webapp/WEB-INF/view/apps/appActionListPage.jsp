<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<style>
.export-btn {
    color: #fff;
    background: #007cba;
    border-color: #007cba !important;
    padding: 4px 20px;
    width: 230px;
    height: 38px;
}
button#exportId {
    margin-right: 7px;
}
.modal-footer {
    border-top: none !important;
}
.modal-header {
    border-bottom: none !important; 
}
.copy-version {
    width: max-content !important; 
    border-radius: 0px !important; 
    padding: 20px !important;
} 
.exportVersionModel {
    position: fixed;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-40%, -40%); 
}


</style>

<div class="col-sm-10 col-rc white-bg p-none">

  <!--  Start top tab section-->
  <div class="right-content-head">
    <div class="text-right">
      <div class="black-md-f text-uppercase dis-line pull-left line34">ACTIONS</div>
      <div class="dis-line form-group mb-none mr-sm"></div>
      <div class="dis-line form-group mb-none"></div>
    </div>
  </div>
  <!--  End  top tab section-->
  <!--  Start body tab section -->
  <div class="right-content-body">
    <div>
      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action "
                id="lunchId" onclick="validateStudyStatus(this);" style="margin-top:25px;"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${not empty studyBo.status && (studyBo.status eq 'Active' || studyBo.status eq 'Paused' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when test="${markAsCompleted eq false}">
                disabled
               </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Create
          app
        </button>
        <div class="form-group mr-sm" style="white-space: normal; margin-top:4px;">
       This action publishes the study to the mobile app making it live and open to enrollment.  
        Launching a study requires that all study sections be marked 'completed' indicating that all mandatory and intended content has been entered.
      </div>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action"
                id="updatesId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when test="${not empty studyBo.status && empty liveStudyBo && (studyBo.hasStudyDraft==0  || studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' ||
					             studyBo.status eq 'Paused' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when test="${not empty studyBo.status && not empty liveStudyBo && (studyBo.hasStudyDraft==0  || studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' ||
					             studyBo.status eq 'Paused' || studyBo.status eq 'Deactivated' || liveStudyBo.status eq 'Paused')}">
                disabled
              </c:when>
               <c:when test="${markAsCompleted eq false}">
                disabled
               </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Publish
          app
        </button>
        <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
        This action publishes updates to a study that is live. All sections need to be marked complete in order to publish updates to the study. Note that updates to the Notifications section are published immediately upon marking the section complete and do not need the use of this action.
      </div>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action "
                id="pauseId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${empty liveStudyBo && not empty studyBo.status && (studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' || studyBo.status eq 'Paused'  || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when
                  test="${not empty liveStudyBo && not empty liveStudyBo.status && (liveStudyBo.status eq 'Pre-launch' || liveStudyBo.status eq 'Pre-launch(Published)' || liveStudyBo.status eq 'Paused'  || liveStudyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Mark ios app as distributed
        </button>
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
        This action temporarily pauses the live study. Mobile app users can no longer participate in study activities until the study is resumed again.
        However, they will still be able to view the dashboard and resources for the study.
      </div>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action "
                id="resumeId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${empty liveStudyBo && not empty studyBo.status && (studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' || studyBo.status eq 'Active' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when
                  test="${not empty liveStudyBo && not empty liveStudyBo.status && (liveStudyBo.status eq 'Pre-launch' || liveStudyBo.status eq 'Pre-launch(Published)' || liveStudyBo.status eq 'Active'  || liveStudyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Mark android app as distributed
        </button>
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action resumes a paused study and brings it back to an 'active' state. Active studies can have updates published to them and can also be deactivated.
      </div>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default red-btn-action "
                id="deactivateId" onclick="validateStudyStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${not empty studyBo.status && (studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)' || studyBo.status eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Deactivate app
        </button>
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action closes out a live study and deactivates it in the mobile app. 
       Once deactivated, mobile app users will no longer be able to participate in the study. Deactivated studies cannot be reactivated.
      </div>
      </div>
  </div>
</div>

<script type="text/javascript">
  $(document).ready(function () {
 });
	   
</script>