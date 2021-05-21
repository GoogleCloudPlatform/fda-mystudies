<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
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
        <button type="button" class="btn btn-default gray-btn-action "
                id="exportId" onclick="exportStudy();"

                <c:choose>
                <c:when test="${not empty permission}"> disabled </c:when>
                <c:when test="${not studyPermissionBO.viewPermission}"> disabled </c:when>
                </c:choose>>Export
        </button> <span class="sprites_icon copy copy_to_clipboard" id="copy_to_clipboard" 
                        data-toggle="tooltip" data-placement="top" title="Copy to clickBoard"></span>
                        
         <div class="form-group mr-sm" style="white-space: normal; margin-top: 4px;">
       This action exports study to google cloud storage.
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
<form:form
    action="/studybuilder/adminStudies/viewBasicInfo.do?_S=${param._S}"
    name="basicInfoForm" id="basicInfoForm" method="post">
</form:form>
<script type="text/javascript">
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    $(".menuNav li").removeClass('active');
    $(".tenth").addClass('active');
    $("#createStudyId").show();
    $('.tenth').removeClass('cursor-none');
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
  
  var signedUrl = "";
  function exportStudy(){
	   var studyId = "${studyBo.id}";
	  $
      .ajax({
        url: "/studybuilder/studies/${studyBo.id}/export.do",
        type: "POST",
        datatype: "json",
        data: {
          "${_csrf.parameterName}": "${_csrf.token}",
        },
        
        success: function (data) {
            var message = data.message;
            signedUrl = data.signedUrlOfExportStudy;
            if (message == "SUCCESS") {
              $("#alertMsg").removeClass('e-box').addClass('s-box').text("Study exported successfully");
              $('#alertMsg').show();
            } else {
              var errMsg = data.errMsg;
              if (errMsg != '' && errMsg != null && typeof errMsg != 'undefined') {
                $("#alertMsg").removeClass('s-box').addClass('e-box').text(errMsg);
              } else {
                $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
              }
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
  
  
  $('.copy_to_clipboard').on('click', function () {
	$('#copy_to_clipboard').val(signedUrl);
	var copyText = document.getElementById("copy_to_clipboard");
    var textArea = document.createElement("textarea");
    textArea.value = copyText.value;
    document.body.appendChild(textArea);
    textArea.select();
    document.execCommand("Copy");
    textArea.remove();
  });
  
</script>