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

.distributed_image{
	width: 13px;
    margin-left: 4px;
    margin-bottom: 4px;
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
                id="createAppId" onclick="validateAppStatus(this);" style="margin-top:25px;"
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
                id="publishAppId" onclick="validateAppStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
             <%--  <c:when test="${appBo.isAppPublished}">
                disabled
              </c:when> --%>
               <c:when test="${appBo.isAppPublished == true && appBo.hasAppDraft ne 1}">
                disabled
              </c:when>
              <c:when test="${not empty appBo.appSequenceBo && (appBo.appStatus ne 'Active' || not appBo.appSequenceBo.appProperties || not appBo.appSequenceBo.developerConfigs)}">
                disabled
              </c:when>
               <c:when test="${markAsCompleted eq false}">
                disabled
               </c:when>
            </c:choose>>Publish
          app
        </button>
        <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
          This action publishes or updates app properties and configurations to the Participant Datastore, that are required to run the mobile app(s). The 'Published' status for the app, displayed at the top of the left menu, is set the first time you publish the app, indicating the app can be run on the mobile device. Note that this action is enabled after the app is created and all app sections are filled out and marked 'completed'. Once used, this button remains disabled unless there are subsequent updates that need to be published.
      </div>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
      
      <div class="display__flex__center">
       <div>   <button type="button" class="btn btn-default gray-btn-action "
                id="iosDistributedId" onclick="validateAppStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              
             <%--  <c:when
                  test="${empty appBo.liveAppsBo}">
                disabled
              </c:when>
              <c:when
                  test="${not empty appBo.liveAppsBo && appBo.liveAppsBo.iosAppDistributed}">
                disabled
              </c:when> --%>
               <c:when
                  test="${not empty appBo.appStatus && (appBo.appStatus eq 'Draft'|| appBo.appStatus eq 'Deactivated')}">
                disabled
              </c:when>
              
               <c:when test="${ empty appBo.isAppPublished || appBo.isAppPublished == false}">
                disabled
              </c:when>
              <c:when
                  test="${not empty appBo.iosAppDistributed && appBo.iosAppDistributed}">
                disabled
              </c:when> 
               <c:when test="${appBo.appPlatform == 'A'}">
			  	disabled
			  </c:when>
			  <c:when test="${appBo.isAppPublished == true && appBo.hasAppDraft eq 1}">
                disabled
              </c:when>
            </c:choose>
                >Mark iOS app as distributed
        </button> </div>
      
      
        
       <div> <span class="study_status  post-launch_txt  pr-sm pl-sm empty ${not appBo.iosAppDistributed?'hide':''}"> Distributed  <img class="distributed_image"  src="/studybuilder/images/icons/check-solid.svg" > </span>  </div> 
       </div>
     
        
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
          This action helps flag the iOS app as 'distributed' (via the App Store or other means), live and made available for actual participants to use. Once the app is marked 'distributed' , key developer configurations that drive the app, get locked disallowing further editing. This action cannot be undone. The latest available app properties must be published to the backend using the 'Publish app' action before marking the app as 'distributed'.
      </div>
      </div>
       <div class="form-group mr-sm" style="white-space: normal;">
      
      <div class="display__flex__center">
       <div>   <button type="button" class="btn btn-default gray-btn-action "
                id="androidDistributedId" onclick="validateAppStatus(this);"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
              <%-- <c:when
                  test="${empty appBo.liveAppsBo}">
                disabled
              </c:when>
              <c:when
                  test="${not empty appBo.liveAppsBo && appBo.liveAppsBo.androidAppDistributed}">
                disabled
              </c:when> --%>
              <c:when
                  test="${ empty appBo.appStatus && (appBo.appStatus eq 'Draft'|| appBo.appStatus eq 'Deactivated')}">
                disabled
              </c:when>
              <c:when test="${empty appBo.isAppPublished || appBo.isAppPublished == false}">
                disabled
              </c:when>
               <c:when
                  test="${not empty appBo.androidAppDistributed && appBo.androidAppDistributed}">
                disabled
              </c:when> 
              <c:when test="${appBo.appPlatform == 'I'}">
			  	disabled
			  </c:when>
			  <c:when test="${appBo.isAppPublished == true && appBo.hasAppDraft eq 1}">
                disabled
              </c:when>
            </c:choose>
                >Mark Android app as distributed
        </button> </div>
      
      
        
       <div> <span class="study_status  post-launch_txt  pr-sm pl-sm empty ${not appBo.androidAppDistributed?'hide':''}"> Distributed <img class="distributed_image" src="/studybuilder/images/icons/check-solid.svg" > </span>  </div> 
       </div>
     
        
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action helps flag the Android app as 'distributed' (via the Play Store or other means), live and made available for actual participants to use. Once the app is marked 'distributed' , key developer configurations that drive the app, get locked disallowing further editing. This action cannot be undone. The latest available app properties must be published to the backend using the 'Publish app' action before marking the app as 'distributed'.
      </div>
      </div>

      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default red-btn-action "
                id="deactivateId" onclick="validateAppStatus(this)"
            <c:choose>
              <c:when test="${not empty permission}">
                disabled
              </c:when>
               <c:when test="${countOfStudiesAssociated ne 0}">
                disabled
               </c:when>
              <c:when
                  test="${not empty appBo.appStatus && (appBo.appStatus eq 'Deactivated' || appBo.appStatus eq 'Draft')}">
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

<form:form
    action="/studybuilder/adminApps/appList.do?_S=${param._S}"
    name="appListInfoForm" id="appListInfoForm" method="post">
</form:form>
<script type="text/javascript">
  $(document).ready(function () {
	  $('.appClass').addClass('active');
	  $(".menuNav li.active").removeClass('active');
	  $(".menuNav li.fifth").addClass('active');
	  
	  console.log("${markAsCompleted}");
	 
 });

  function validateAppStatus(obj) {
	    var buttonText = obj.id;
	    var messageText = "";
	    if (buttonText) {
	      if (buttonText == 'createAppId'
	          || buttonText == 'deactivateId'|| buttonText == 'publishAppId'|| buttonText == 'iosDistributedId'|| buttonText == 'androidDistributedId') {
	         if (buttonText == 'createAppId') {
	        	 messageText = "You are attempting to create a new app record. Are you sure you wish to proceed?";
	        } else if (buttonText == 'deactivateId') {
	            messageText = "You are attempting to deactivate the app record. This cannot be undone. Are you sure you wish to proceed?";
	        } else if (buttonText == 'publishAppId') {
	            messageText = "You are attempting to publish or update app properties and configurations. Are you sure you wish to proceed?";
	        } else if (buttonText == 'iosDistributedId') {
	            messageText = "You are attempting to mark the iOS app as 'distributed'. This action cannot be undone. Are you sure you wish to proceed?";
	        } else if (buttonText == 'androidDistributedId') {
	            messageText = "You are attempting to mark the Android app as 'distributed'. This action cannot be undone. Are you sure you wish to proceed?";
	        }
	        bootbox.confirm({
	          closeButton: false,
	          message: messageText,
	          buttons: {
	            'cancel': {
	              label: 'Cancel',
	            },
	            'confirm': {
	              label: 'OK',
	            },
	          },
	          callback: function (result) {
	            if (result) {
	               updateAppsByAction(buttonText); 
	            }
	          }
	        });
	      }
	    }

	  }
  
  function updateAppsByAction(buttonText) {
	  
	    if (buttonText) {
	      var appId = "${appBo.id}";
	      var customAppId = "${appBo.customAppId}";
	      $
	          .ajax({
	            url: "/studybuilder/adminApps/updateAppAction.do?_S=${param._S}",
	            type: "POST",
	            datatype: "json",
	            data: {
	              buttonText: buttonText,
	              appId: appId,
	              customAppId: customAppId,
	              "${_csrf.parameterName}": "${_csrf.token}",
	            },
	            success: function updateAction(data, status) {
	              var message = data.message;
	              
	                $('#appListInfoForm').submit();
	            },
	            error: function status(data, status) {
	              $("body").removeClass("loading");
	            },
	            complete: function () {
	              $('.actBut').removeAttr('disabled');
	            }
	          });
	    }
	  }
 
</script>
