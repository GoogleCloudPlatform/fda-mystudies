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
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Launch
          study
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
          updates
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
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Pause
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
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Resume
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
                <c:if test="${not studyPermissionBO.viewPermission}">disabled</c:if>>Deactivate
        </button>
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action closes out a live study and deactivates it in the mobile app. 
       Once deactivated, mobile app users will no longer be able to participate in the study. Deactivated studies cannot be reactivated.
      </div>
      </div>
      
      <div class="form-group mr-sm" style="white-space: normal;">
        <button type="button" class="btn btn-default export-btn "
                id="exportId" onclick="exportStudy();"

                <c:choose>
                <c:when test="${not empty permission}"> disabled </c:when>
                <c:when test="${not studyPermissionBO.viewPermission}"> disabled </c:when>
                </c:choose>>Export
        </button> <span class="sprites_icon copy copy_to_clipboard " id="copy_to_clipboard" 
                        data-toggle="tooltip" data-placement="top" data-original-title=""></span>
                        <span class=" copy_to_clipboard " id="copy_to_clipboard"
                         >Copy signed URL</span>
                        
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action exports the study into a cloud storage location and generates a signed URL that can then be copied into the Study Builder you want to import the study into.  
       The URL is valid for ${signedUrlExpiryTime} hours and can only be used to import the study into compatible Study Builder applications (running  platform release version ${releaseVersion} or higher).
      </div>
      </div>

  </div>
</div>
<div class="modal fade exportVersionModel" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"     >
    <div class="modal-dialog">
        <div class="modal-content copy-version">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Select the study version to be exported:</h4>
            </div>
            <div class="modal-body">
               
                      <span class="radio radio-info radio-inline p-40 ">
                          <input type="radio" id="workingVersion" class="workingVersion copyVersion"  value="workingVersion" name="copy">
                          <label for="workingVersion">Export working version</label>
                      </span>
                      <span class="radio radio-inline ">
                          <input type="radio" id="publishedVersion" class="publishedVersion copyVersion"  value="publishedVersion" name="copy">
                          <label for="publishedVersion">Export last published version</label>
                     </span>
                     
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default gray-btn" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary blue-btn" id="submit" onclick='copyVersion()' disabled >Submit</button>
                 <input type="hidden" name="copyVersion" id="copyVersion" value=""/>
                 <input type="hidden" name="lastpublish" id="lastpublish" value=""/>
            </div>
        </div>
    </div>
</div>
<form:form
    action="/studybuilder/adminStudies/updateStudyAction.do?_S=${param._S}"
    name="actionInfoForm" id="actionInfoForm" method="post">
  <input type="hidden" name="studyId" id="studyId" value="${studyBo.id}"/>
  <input type="hidden" name="buttonText" id="buttonText" value=""/>
</form:form>
<form:form
    action="/studybuilder/adminStudies/studyList.do?_S=${param._S}"
    name="studyListInfoForm" id="studyListInfoForm" method="post">
</form:form>

<script type="text/javascript">
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    $(".menuNav li").removeClass('active');
    $(".tenth").addClass('active');
    $("#createStudyId").show();
    $('.tenth').removeClass('cursor-none');
    
    if("${exportSignedUrl}" == null || "${exportSignedUrl}" == ""){
    	$('.copy_to_clipboard').addClass('cursor-none');
    }
    else if(!validateExpireDate("${exportSignedUrl}")){
    	$('.copy_to_clipboard').addClass('cursor-none');
    }else{
        $('.copy_to_clipboard').removeClass('cursor-none');
    }
  });

  function validateStudyStatus(obj) {
    var buttonText = obj.id;
    var messageText = "";
    if (buttonText) {
      if (buttonText == 'pauseId'
          || buttonText == 'deactivateId') {
         if (buttonText == 'pauseId') {
        	 messageText = "You are attempting to pause the study. Are you sure you wish to proceed?";
        } else if (buttonText == 'deactivateId') {
            messageText = "You are attempting to deactivate the study. Are you sure you wish to proceed?";
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
              updateStudyByAction(buttonText);
            }
          }
        });
      } else {
        $
            .ajax({
              url: "/studybuilder/adminStudies/validateStudyAction.do?_S=${param._S}",
              type: "POST",
              datatype: "json",
              data: {
                buttonText: buttonText,
                "${_csrf.parameterName}": "${_csrf.token}",
              },
              success: function emailValid(data, status) {
                var message = data.message;
                var checkListMessage = data.checkListMessage;
                var checkFailureMessage = data.checkFailureMessage;
                if (message == "SUCCESS") {
                	showBootBoxMessage(buttonText,
                            messageText);
                } else {
                	  bootbox.alert(message) ;
                }
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

  }

  function showErrMsg1(message) {
    $("#alertMsg").removeClass('s-box').addClass('e-box').text(message);
    $('#alertMsg').show('10000');
    setTimeout(hideDisplayMessage, 10000);
  }

  function showBootBoxMessage(buttonText, messageText) {
    if (buttonText == 'resumeId') {
    	 messageText = "You are attempting to resume the study. Are you sure you wish to proceed?";
    } else if (buttonText == 'lunchId') {
    	 messageText = "You are attempting to launch the study. Are you sure you wish to proceed?";
    } else if (buttonText == 'updatesId') {
    	 messageText = "You are attempting to publish updates to a live study. Are you sure you wish to proceed?";
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
          updateStudyByAction(buttonText);
        }
      }
    })
  }

  function updateStudyByAction(buttonText) {
	  
    if (buttonText) {
      var studyId = "${studyBo.id}";
      $
          .ajax({
            url: "/studybuilder/adminStudies/updateStudyAction.do?_S=${param._S}",
            type: "POST",
            datatype: "json",
            data: {
              buttonText: buttonText,
              studyId: studyId,
              "${_csrf.parameterName}": "${_csrf.token}",
            },
            success: function updateAction(data, status) {
              var message = data.message;
              if (message == "SUCCESS") {
                if (buttonText == 'deactivateId'
                    || buttonText == 'lunchId'
                    || buttonText == 'updatesId') {
                  $('#studyListInfoForm').submit();
                } else {
                  document.studyListInfoForm.action = "/studybuilder/adminStudies/actionList.do?_S=${param._S}";
                  document.studyListInfoForm.submit();
                }
              } else {
                $('#studyListInfoForm').submit();
              }
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
  
  function exportStudy(){
	   if ("${liveStudyBo}" != ""  ? (("${studyBo.hasStudyDraft}" == "1")? true : false ) : false){
	      $('#myModal').modal('show');
		 }else{
			 $('#copyVersion').val("workingVersion");
			 exportStudyToCloud("${studyBo.id}");
		 }
	
 }
   function copyVersion() {
	   var buttonValue = $("input[name='copy']:checked").val();
	   $('#copyVersion').val(buttonValue);
		var studyId = (buttonValue == 'publishedVersion') ? "${liveStudyBo.id}" : "${studyBo.id}";
		exportStudyToCloud(studyId);
  }
  
  
  $('.copy_to_clipboard').on('click', function () {
	var signedUrl = "${exportSignedUrl}";
	$('#copy_to_clipboard').val(signedUrl);
	var copyText = document.getElementById("copy_to_clipboard");
    var textArea = document.createElement("textarea");
    textArea.value = copyText.value;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand("Copy");
    textArea.remove();
    $('#alertMsg').show();
    $("#alertMsg").removeClass('e-box').addClass('s-box').text("URL copied to clipboard");
    setTimeout(hideDisplayMessage, 5000);
  });

  var expireTime = "";
  function validateExpireDate(result){
	    var decodedURL = unescape(result);
		 var urlArray= decodedURL.split("&");
		 var expireTimeStamp= urlArray[1].split("=");
		 expireTime = expireTimeStamp[1];
		  if(expireTimeStamp[1] < Math.round(new Date().getTime()/1000)){
			  return false;
		  }
		  return true;
	  }
  
 $('.copy_to_clipboard').on('mouseover', function () {
	  var urlExpireTime =  ${signedUrlExpiryTime};
	  var lastGeneratedTimestamp = expireTime-(urlExpireTime*3600);
	  var lastGeneratedTime = new Date(lastGeneratedTimestamp*1000).toLocaleString([], { hour12: true});
	 $('#copy_to_clipboard').attr("title", "Last generated on " + lastGeneratedTime );
	}); 
 
 function exportStudyToCloud(studyId) {
	   $('.copy_to_clipboard').addClass('cursor-none');
	   var studyId = studyId;
	   var exportURL = "/studybuilder/studies/"+studyId+"/export.do";
	  $
      .ajax({
        url: exportURL,
        type: "POST",
        datatype: "json",
        data: {
          "${_csrf.parameterName}": "${_csrf.token}",
          "copyVersion" : $('#copyVersion').val(),
        },
        
        success: function (data) {
            var message = data.message;
            debugger
            if (message == "SUCCESS") {
            	 $('#myModal').modal('hide');
              $("#alertMsg").removeClass('e-box').addClass('s-box').text("Study exported successfully");
              $('#alertMsg').show();
             
              setTimeout(function () {
            	  window.location=window.location;
            	  $('.copy_to_clipboard').removeClass('cursor-none');
                 
                }, 5000);
            } else {
            	showErrMsg1("Export failed. Please try again later.");
            }
            setTimeout(hideDisplayMessage, 5000);
          },
          error: function (xhr, status, error) {
            $(item).prop('disabled', false);
            $('#alertMsg').show();
            $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
            setTimeout(hideDisplayMessage, 5000);
          }
        });  
 }
 
 var radioButton = $("input:radio");
 radioButton.change(function () {
     if (radioButton.filter(':checked').length > 0) {
         $("#submit").removeAttr("disabled");
     } else {
         $("#submit").attr("disabled", "disabled");
     }
 });
	   
</script>