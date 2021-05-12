<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<style>
  table.dataTable thead th:last-child {
    width: 100px !important;
  }
</style>
<div class="col-sm-10 col-rc white-bg p-none" id="settingId">
  <form:form
      action="/studybuilder/adminStudies/saveOrUpdateSettingAndAdmins.do?_S=${param._S}"
      data-toggle="validator" role="form" id="settingfoFormId" method="post"
      autocomplete="off">
    <input type="hidden" name="buttonText" id="buttonText">
    <input type="hidden" id="settingsstudyId" name="id"
           value="${studyBo.id}">
    <input type="hidden" id="userIds" name="userIds">
    <input type="hidden" id="permissions" name="permissions">
    <input type="hidden" id="projectLead" name="projectLead">
    
	
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          STUDY SETTINGS
          <c:set var="isLive">${_S}isLive</c:set>
            ${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</div>

        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut"
                  id="cancelId">Cancel
          </button>
        </div>
        <c:if
            test="${(empty permission) && (sessionObject.role ne 'Org-level Admin')}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn" id="saveId">Save</button>
          </div>

          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn"
                    id="completedId">Mark as completed
            </button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->


    <!-- Start body tab section -->
    <div class="right-content-body col-xs-12">
      <!-- Start Section-->
      <div class="col-md-12 p-none">
        <div class="gray-xs-f mb-sm">
          Platform(s) supported
          <span class="requiredStar"> *</span>
          <span
              class="sprites_v3 filled-tooltip" id="infoIconId"></span>
        </div>
        <div class="form-group">
          <span class="checkbox checkbox-inline p-45"><input
              class="platformClass" type="checkbox" id="inlineCheckbox1"
              name="platform" value="I"
              <c:if test="${fn:contains(studyBo.platform,'I')}">checked</c:if>
              <c:if
                  test="${not empty studyBo.liveStudyBo && fn:contains(studyBo.liveStudyBo.platform,'I') || studyBo.status eq 'Active'}">disabled</c:if>
              data-error="Please check these box if you want to proceed"
              > <label for="inlineCheckbox1"> iOS </label>
          </span>
          <span class="checkbox checkbox-inline"><input
              type="checkbox" class="platformClass" id="inlineCheckbox2"
              name="platform" value="A"
              <c:if test="${fn:contains(studyBo.platform,'A')}">checked</c:if>
              <c:if
                  test="${not empty studyBo.liveStudyBo && fn:contains(studyBo.liveStudyBo.platform,'A') || studyBo.status eq 'Active'}">disabled</c:if>
              data-error="Please check these box if you want to proceed"
              > <label for="inlineCheckbox2"> Android </label>
          </span>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <!-- End Section-->

     <!-- Start Section-->
			<div class="col-md-12 p-none">
				<div class="gray-xs-f mb-sm">
					Allow participants to enroll? <span>
            </span><span class="requiredStar"> *</span> <span
                  data-toggle="tooltip" data-placement="top"
                  title="This field can be updated after the study is launched if you wish to stop enrollment at any point during the course of the study."
                  class="filled-tooltip"></span>
				</div>
				<div class="form-group">
					<span class="radio radio-info radio-inline p-45"><input
						type="radio" id="inlineRadio1" value="Yes" 
						name="enrollingParticipants"
						<c:if test="${studyBo.enrollingParticipants eq 'Yes' || studyBo.status eq 'Pre-launch'}">checked</c:if>
						 required data-error="Please fill out this field"> <label
						for="inlineRadio1">Yes</label> </span> <span class="radio radio-inline"><input
						type="radio" id="inlineRadio2" value="No"
						name="enrollingParticipants"
						${studyBo.status eq 'Pre-launch' ?'disabled':''}
						<c:if test="${ studyBo.enrollingParticipants eq 'No' }">checked</c:if>
						 required data-error="Please fill out this field">
						<label for="inlineRadio2">No</label> </span>
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>
			<!-- End Section-->

      <!-- Start Section-->
      <div class="col-md-12 p-none">
        <div class="gray-xs-f mb-sm">
          Use participant enrollment date as anchor date?
          <span
              class="requiredStar"> *
          </span>
          <span>
            <span
                data-toggle="tooltip" data-placement="top"
                title="Select this option to distribute a questionnaire, active task or resource, N number of days after participant enrollment. N is configured in the schedule settings of that study activity or resource."
                class="filled-tooltip"></span>
          </span>
        </div>

        <div class="form-group">
          <span class="radio radio-info radio-inline p-45"><input
              type="radio" id="inlineRadio11" value="Yes"
              name="enrollmentdateAsAnchordate"
              <c:if test="${studyBo.enrollmentdateAsAnchordate}">checked</c:if>
              required data-error="Please fill out this field"> <label for="inlineRadio11">Yes</label>
          </span>
          <span class="radio radio-inline"><input type="radio"
                                                  id="inlineRadio22" value="No"
                                                  name="enrollmentdateAsAnchordate"
            ${isAnchorForEnrollmentLive?'disabled':''}
                                                  <c:if
                                                      test="${studyBo.enrollmentdateAsAnchordate eq false}">checked</c:if>
                                                  required data-error="Please fill out this field"> <label
              for="inlineRadio22">No</label>
          </span>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <!-- End Section-->
      

    </div>
    <!-- End body tab section -->

  </form:form>

</div>
<!-- End right Content here -->


<!-- Modal -->
<div class="modal fade" id="myModal" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">

      <div class="modal-header cust-hdr pt-lg">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
        <h4 class="modal-title pl-lg">
          <strong>Platform and Feature Support</strong>
        </h4>
      </div>
      <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
        <div>
          <div>
            <ul class="no-disc">
              <li><strong>1. Platform support: </strong><br/>
                <ul class="no-disc">
                  <li>Note that once the study is launched, platform support
                    cannot be revoked. However, adding support for a platform not
                    previously selected will still be possible.
                  </li>
                </ul>
              </li>
              <li>&nbsp;</li>
              <li><strong>2. Feature support on iOS and Android:</strong><br/>

                <ul class="no-disc">
                  <li>Given below is a list of features currently NOT
					available for Android as compared to iOS. Please note the same
                    in your creation of study content:
                  </li>
                  <li>i. Active tasks: Tower of hanoi, Spatial span memory
                  </li>
                </ul>
              </li>

            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script>
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    <c:if test="${empty permission && fn:contains(permissions,5)}">
   
    $('[data-toggle="tooltip"]').tooltip();
    $('#adminsId').hide();
    $('.studyAdminRowCls').each(function () {
      var userId = $(this).attr('studyUserId');
      $('#user' + userId).removeClass('checkCount').hide();
    });
   
    $('.addAdminCheckbox').on('click', function () {
      var count = 0;
      $('[name=case]:checked').each(function () {
        count++;
      });
      
    });
    </c:if>
    table = $('#studyAdminsTable').DataTable({
      "paging": false,
      "aoColumns": [
        {"width": '40%', "bSortable": false},
        {"width": '10%', "bSortable": false},
        {"width": '10%', "bSortable": false},
        {"width": '10%', "bSortable": false},
        {"width": '10%', "bSortable": false}
      ],
      "info": false,
      "lengthChange": true,
      "searching": false,
    });
    $(".menuNav li.active").removeClass('active');
    $(".menuNav li.second").addClass('active');
   
    <c:if test="${(not empty permission) || (sessionObject.role eq 'Org-level Admin')}">
    $('#settingfoFormId input,textarea,select').prop('disabled', true);
    $('#settingfoFormId').find('.elaborateClass').addClass('linkDis');
    </c:if>
    <c:if test="${!fn:contains(permissions,5)}">
    $('.radcls').prop('disabled', true);
    </c:if>
    $("#completedId").on('click', function (e) {

      if ($('.checkbox input:checked').length == 0) {
  	    $("input").attr("required", true);
      }
      var rowCount = 0;
      if (isFromValid("#settingfoFormId")) {
        rowCount = $('.leadCls').length;
        if (rowCount != 0) {
          if ($("#studyAdminsTable .leadCls:checked").length > 0) {
            $('#completedId').prop('disabled', true);
            platformTypeValidation('completed');
          } else {
            bootbox.alert({
              closeButton: false,
              message: 'Please select one of the admin as a project lead',
            });
          }
        } else {
          $('#completedId').prop('disabled', true);
          platformTypeValidation('completed');
        }
      }
    });
    
    $("#saveId").click(function () {
      platformTypeValidation('save');
    });
   
    $("[data-toggle=tooltip]").tooltip();
    $("#infoIconId").hover(function () {
      $('#myModal').modal('show');
    });
  });
 
  
  function platformTypeValidation(buttonText) {
    var platformNames = '';
    $("input:checkbox[name=platform]:checked").each(function () {
      platformNames = platformNames + $(this).val();
    });
    var liveStudy = "${studyBo.liveStudyBo}";
    if (liveStudy) {
      var platform = "${studyBo.liveStudyBo.platform}";
      if (platform.includes('A')) {
        platformNames = '';
      }
    }
    if (platformNames != '' && platformNames.includes('A')) {
      $('.actBut').prop('disabled', true);
      $("body").addClass("loading");
      $.ajax({
        url: "/studybuilder/adminStudies/studyPlatformValidationforActiveTask.do?_S=${param._S}",
        type: "POST",
        datatype: "json",
        data: {
          studyId: $('#settingsstudyId').val(),
          "${_csrf.parameterName}": "${_csrf.token}",
        },
        success: function platformValid(data, status) {
        	 var message = data.message;
             var errorMessage = data.errorMessage;
            
          $("body").removeClass("loading");
          if (message == "SUCCESS") {
            $('#completedId').removeAttr('disabled');
            bootbox.alert(errorMessage);
          } else {
            submitButton(buttonText);
          }
        },
        error: function status(data, status) {
          $("body").removeClass("loading");
        },
        complete: function () {
          $('.actBut').removeAttr('disabled');
        },
        global: false
      });
    } else {
        submitButton(buttonText);
    }
  }
  function submitButton(buttonText) {
    var isAnchorForEnrollmentDraft = '${isAnchorForEnrollmentDraft}';
    if (buttonText === 'save') {
      $('#settingfoFormId').validator('destroy');
      $("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
      $("#buttonText").val('save');
      $("#settingfoFormId").submit();
    } else {
        $("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
        $("#buttonText").val('completed');
        $("#settingfoFormId").submit();
    }
  }
  function admins() {
    var userIds = "";
    var permissions = "";
    var projectLead = "";
    $('.studyAdminRowCls').each(function () {
      var userId = $(this).attr('studyUserId');
      if (userIds == "") {
        userIds = userId;
      } else {
        userIds += "," + userId;
      }
      var permission = $(this).find('input[type=radio]:checked').val();
      if (permissions == "") {
        permissions = permission;
      } else {
        permissions += "," + permission;
      }
      if ($(this).find('#inlineRadio3' + userId).prop('checked')) {
        projectLead = userId;
      }
    });
    $('#userIds').val(userIds);
    $('#permissions').val(permissions);
    $('#projectLead').val(projectLead);
  }
  <c:if test="${empty permission && fn:contains(permissions,5)}">
  
  
  
 
  function escapeXml(unsafe) {
    return unsafe.replace(/[<>&'"]/g, function (c) {
      switch (c) {
        case '<':
          return '&lt;';
        case '>':
          return '&gt;';
        case '&':
          return '&amp;';
        case '\'':
          return '&apos;';
        case '"':
          return '&quot;';
      }
    });
  }
  </c:if>
  function showWarningForAnchor(isAnchorForEnrollmentDraft, enrollmentdateAsAnchordate) {
    if (isAnchorForEnrollmentDraft == 'true' && enrollmentdateAsAnchordate == 'No') {
      var text = "You have chosen not to use enrollment date as an anchor date. You will need to revise the schedules of 'target' activities or resources, if any, that were set up based on the enrollment date.";
      bootbox.confirm({
        closeButton: false,
        message: text,
        buttons: {
          'cancel': {
            label: 'Cancel',
          },
          'confirm': {
            label: 'OK',
          },
        },
        callback: function (valid) {
          if (valid) {
            console.log(1);
            $("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
            $("#buttonText").val('completed');
            $("#settingfoFormId").submit();
          } else {
            console.log(2);
            $('#completedId').removeAttr('disabled');
          }
        }
      });
    } else {
      $("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
      $("#buttonText").val('completed');
      $("#settingfoFormId").submit();
    }
  }
  var sucMsg = '${sucMsg}';
  if (sucMsg.length > 0) {
    showSucMsg(sucMsg);
  }

	function showSucMsg(message) {
	  $("#alertMsg").removeClass('e-box').addClass('s-box').text(message);
	  $('#alertMsg').show('5000');
	  if('${param.buttonText}' == 'completed'){
		    window.setTimeout(function(){
		        window.location.href = "/studybuilder/adminStudies/overviewStudyPages.do?_S=${param._S}";
		    }, 5000);
	  }else{
	  	setTimeout(hideDisplayMessage, 5000);
	  }
	}
</script>