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
                id="lunchId" onclick="" style="margin-top:25px;"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <c:when
                  test="${not empty appBo.appStatus && (appBo.appStatus eq 'Active'|| appBo.appStatus eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when test="${markAsCompleted eq false}">
                disabled
               </c:when>
            </c:choose>>Create
          app
        </button>
        <div class="form-group mr-sm" style="white-space: normal; margin-top:4px;">
       This action creates a new app record in the system. Creating an app record requires that the App Information and App Settings be marked complete. 
       Note that certain fields (such as app ID, type and platforms supported) cannot be edited once the app record is created. 
      </div>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default gray-btn-action"
                id="updatesId" onclick=""
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <%-- <c:when test="${not empty appBo.appStatus && appBo.appStatus eq 'Deactivated'}">
                disabled
              </c:when> --%>
              <c:when test="${not empty appBo.appSequenceBo && appBo.appSequenceBo.actions eq false}">
                disabled
              </c:when>
               <c:when test="${markAsCompleted eq false}">
                disabled
               </c:when>
            </c:choose>>Publish
          app
        </button>
        <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
        This action publishes (or updates) app properties and configurations to the Participant Datastore thereby making them available for use by the mobile app. 
        Publishing an app requires that all sections be marked 'completed'. Note that it is essential for the app to have a 'Published' status for it to be usable on the mobile device. 
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
        This action helps flag the iOS app as distributed (via the App Store or other means), live and made available for actual participants to use. 
        Once the app is marked 'distrbuted' , key developer configurations that drive the app, get locked disallowing further editing. This action cannot be undone. 
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
                  test="${empty liveAppBo && not empty appBo.appStatus && (appBo.appStatus eq 'Active' || appBo.appStatus eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Mark android app as distributed
        </button>
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action helps flag the Android app as distributed (via the Play Store or other means), live and made available for actual participants to use. 
       Once the app is marked 'distrbuted' , key developer configurations that drive the app, get locked disallowing further editing. This action cannot be undone. 
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
                  test="${not empty appBo.appStatus && (appBo.appStatus eq 'Active' || appBo.appStatus eq 'Deactivated')}">
                disabled
              </c:when>
            </c:choose>>Deactivate app
        </button>
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action deactivates an active app record in the system if there are no active studies mapped to it. Once deactivated, the app cannot be used for new studies. 
       Active app user accounts get automatically disabled as well (leverage the app notifications section to notify your app users in advance about impending deactivation). Note that deactivated apps cannot be reactivated.
      </div>
      </div>
  </div>
</div>

<script type="text/javascript">
  $(document).ready(function () {
	  $('.appClass').addClass('active');
 });
	   
</script>