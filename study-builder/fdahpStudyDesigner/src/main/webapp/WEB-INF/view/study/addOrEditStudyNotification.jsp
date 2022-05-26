<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date"/>
<c:set var="tz" value="America/Los_Angeles"/>

<div class="col-sm-10 col-rc white-bg p-none">
  <form:form
      action="/studybuilder/adminStudies/saveOrUpdateStudyNotification.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}"
      data-toggle="validator" role="form" id="studyNotificationFormId" method="post"
      autocomplete="off">
    <input type="hidden" name="buttonType" id="buttonType">
    <input type="hidden" name="notificationId" value="${notificationBO.notificationId}">
    <input type="hidden" name="actionPage" value="${notificationBO.actionPage}">
    <input type="hidden" name="appId" value="${appId}">
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f dis-line pull-left line34">
          <span class="pr-sm">
            <a href="javascript:void(0)" class="goToNotificationListForm"
               id="goToNotificationListForm"><img
                src="/studybuilder/images/icons/back-b.png" alt=""/></a>
          </span>
          <c:if test="${notificationBO.actionPage eq 'edit'}">Edit notification</c:if>
          <c:if test="${notificationBO.actionPage eq 'addOrCopy'}">Add notification</c:if>
          <c:if test="${notificationBO.actionPage eq 'view'}">View notification</c:if>
          <c:if test="${notificationBO.actionPage eq 'resend'}">Resend notification</c:if>
        </div>

        <div class="dis-line form-group mb-none">
          <button type="button" class="btn btn-default gray-btn goToNotificationListForm"
                  id="goToStudyListPage">Cancel
          </button>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button" class="btn btn-primary gray-btn deleteNotificationButtonHide ml-sm"
                  id="deleteStudyNotification">Delete
          </button>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button"
                  class="btn btn-default gray-btn studyNotificationButtonHide ml-sm mr-sm"
                  id="saveStudyId">Save
          </button>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button" class="btn btn-primary blue-btn studyNotificationButtonHide mr-sm"
                  id="doneStudyId">Done
          </button>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button" class="btn btn-primary blue-btn resendBuuttonAsDone mr-sm"
                  id="resendStudyId">
            Done
          </button>
        </div>
      </div>
    </div>
    <!-- End top tab section-->
    <!-- Start body tab section -->
    <div class="right-content-body">
      <!-- form- input-->
      <c:if
          test="${notificationBO.notificationSent && notificationBO.actionPage eq 'edit' && not empty notificationHistoryNoDateTime}">
        <div>
          <span>This notification has already been sent out to users and cannot be edited. To resend this notification,
            use the Resend action and choose a time for firing the notification.
          </span>
        </div>
      </c:if>

      <div class="pl-none mt-none">
        <div class="gray-xs-f mb-xs">Notification text (250 characters max)
          <span
              class="requiredStar">*
          </span>
        </div>
        <div class="form-group">
          <textarea autofocus="autofocus" class="form-control" maxlength="250" rows="5"
                    id="notificationText"
                    name="notificationText" required data-error="Please fill out this field" 
          >${notificationBO.notificationText}</textarea>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>

      <div class="mt-lg mb-none">
        <div class="form-group hideOnHover">
          <span class="radio radio-info radio-inline p-45">
            <input type="radio" id="inlineRadio1" value="notImmediate"
                   name="currentDateTime"
                   <c:if
                       test="${notificationBO.notificationScheduleType eq 'notImmediate'}">checked</c:if>
                   <c:if test="${notificationBO.actionPage eq 'addOrCopy'}">checked</c:if>>
            <label for="inlineRadio1">Schedule this notification</label>
              <span
      <fmt:formatDate value = "${date}" pattern="z" var="server_timezone"/>
          class="ml-xs sprites_v3 filled-tooltip Selectedtooltip"
          style="width: 20px;background-position: -164px -68px;"
          data-toggle="tooltip"
          data-placement="top"
          title="The notification gets delivered to mobile app users at the selected date and time as per server time zone which is ${server_timezone}.">
      </span>
          </span>
          <span class="radio radio-inline">
            <input type="radio" id="inlineRadio2" value="immediate" name="currentDateTime"
                   <c:if
                       test="${notificationBO.notificationScheduleType eq 'immediate'}">checked</c:if>
                   <c:if test="${studyBo.status ne 'Active'}">disabled</c:if>>
            <label for="inlineRadio2" data-toggle="tooltip" data-placement="top"
                   title="This option will be available once the study is launched.">Send immediately</label>
          </span>
          <div class="help-block with-errors red-txt"></div>
          <c:if test="${not empty notificationHistoryNoDateTime}">
          <div class="gray-xs-f mb-xs mt-xs">Previously sent on: </div>
            <c:forEach items="${notificationHistoryNoDateTime}" var="notificationHistory">
              <span
                  class="lastSendDateTime">${notificationHistory.notificationSentdtTime}</span>
              <br><br>
            </c:forEach>
          </c:if>
          <div class="clearfix"></div>
        </div>
      </div>

      <div class="add_notify_option mandatoryForStudyNotification">
        <div class="gray-xs-f mb-xs">Select date
          <span class="requiredStar">*</span>
        </div>
        <div class="form-group date">
          <input id='datetimepicker' type="text" class="form-control calendar datepicker resetVal" data-error="Please fill out this field" 
                 id="scheduleDate"
                 name="scheduleDate" value="${notificationBO.scheduleDate}"
                 oldValue="${notificationBO.scheduleDate}"
                 placeholder="MM/DD/YYYY" disabled/>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>

      <div class="add_notify_option mandatoryForStudyNotification">
        <div class="gray-xs-f mb-xs">Time
          <span class="requiredStar">*</span>
        </div>
        <div class="form-group">
          <input id="timepicker1" class="form-control clock timepicker resetVal" id="scheduleTime" data-error="Please fill out this field" 
                 name="scheduleTime" value="${notificationBO.scheduleTime}"
                 oldValue="${notificationBO.scheduleTime}"
                 placeholder="00:00" disabled/>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
 <div class="form-group mr-sm" style="white-space: normal; margin-top: -9px;">
 For studies that are already launched, notifications get scheduled for delivery to participants, immediately upon marking this screen as Done, not requiring an explicit 'Publish updates' action at the study level.
 </div>
    </div>
  </form:form>
  <!--  End body tab section -->
</div>

<form:form action="/studybuilder/adminStudies/viewStudyNotificationList.do?_S=${param._S}"
           id="viewStudyNotificationListPage" name="viewStudyNotificationListPage" method="post">
</form:form>

<form:form action="/studybuilder/adminStudies/studyList.do?_S=${param._S}" name="studyListPage"
           id="studyListPage"
           method="post">
</form:form>

<form:form action="/studybuilder/adminStudies/deleteStudyNotification.do?_S=${param._S}"
           id="deleteStudyNotificationForm" name="deleteStudyNotificationForm" method="post">
  <input type="hidden" name="notificationId" value="${notificationBO.notificationId}">
</form:form>
<script>
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    var appId = '${appId}';
    $(".menuNav li").removeClass('active');
    $(".eigthNotification").addClass('active');
    $("#createStudyId").show();
    $('.eigthNotification').removeClass('cursor-none');

    $('[data-toggle="tooltip"]').tooltip();

    <c:if test="${notificationBO.actionPage eq 'view'}">
    $('#studyNotificationFormId input,textarea').prop('disabled', true);
    $('.studyNotificationButtonHide').addClass('dis-none');
    $('.deleteNotificationButtonHide').addClass('dis-none');
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    $('.resendBuuttonAsDone').addClass('dis-none');
    </c:if>

    <c:if test="${notificationBO.actionPage eq 'addOrCopy'}">
    $('.deleteNotificationButtonHide').addClass('dis-none');
    $('.resendBuuttonAsDone').addClass('dis-none');
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('#datetimepicker, #timepicker1').attr('required', 'required');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    </c:if>

    <c:if test="${not notificationBO.notificationSent && notificationBO.actionPage eq 'edit' && empty notificationHistoryNoDateTime}">
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('#datetimepicker, #timepicker1').attr('required', 'required');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    $('.resendBuuttonAsDone').addClass('dis-none');
    </c:if>

    <c:if test="${notificationBO.notificationSent && notificationBO.actionPage eq 'edit' && not empty notificationHistoryNoDateTime}">
    $('[data-toggle="tooltip"]').tooltip('destroy');
    $('#studyNotificationFormId input,textarea').prop('disabled', true);
    $('.deleteNotificationButtonHide').addClass('dis-none');
    $('.studyNotificationButtonHide').addClass('dis-none');
    $('.resendBuuttonAsDone').addClass('dis-none');
    </c:if>

    <c:if test="${not notificationBO.notificationSent && notificationBO.actionPage eq 'edit'}">
    $('.deleteNotificationButtonHide').removeClass('dis-none');
    $('.resendBuuttonAsDone').addClass('dis-none');
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('#datetimepicker, #timepicker1').attr('required', 'required');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    </c:if>

    <c:if test="${not notificationBO.notificationSent && notificationBO.actionPage eq 'edit' && not empty notificationHistoryNoDateTime}">
    $('.deleteNotificationButtonHide').addClass('dis-none');
    $('#studyNotificationFormId textarea').prop('disabled', true);
    </c:if>

    <c:if test="${not notificationBO.notificationSent && notificationBO.actionPage eq 'resend'}">
    $('#studyNotificationFormId input,textarea').prop('disabled', true);
    $('#studyNotificationFormId #inlineRadio2,#inlineRadio2').prop('disabled', true);
    $('.resendBuuttonAsDone').addClass('dis-none');
    $('.deleteNotificationButtonHide').addClass('dis-none');
    $('.studyNotificationButtonHide').addClass('dis-none');
    $('[data-toggle="tooltip"]').tooltip('destroy');
    $('#doneStudyId').addClass('dis-none');
    </c:if>

    <c:if test="${notificationBO.notificationSent && notificationBO.actionPage eq 'resend'}">
    $('#studyNotificationFormId #inlineRadio1').prop('disabled', false);
    <c:if test="${studyBo.status ne 'Active'}">
    $('#studyNotificationFormId #inlineRadio2').prop('disabled', true);
    </c:if>
    <c:if test="${studyBo.status eq 'Active'}">
    $('#studyNotificationFormId #inlineRadio2').prop('disabled', false);
    </c:if>

    $('#studyNotificationFormId textarea,#datetimepicker,#timepicker1,#inlineRadio1').prop(
        'disabled', false);
    $('#studyNotificationFormId textarea').prop('disabled', true);
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').attr('required', 'required');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    $('#buttonType').val('resend');
    $('.resendBuuttonAsDone').removeClass('dis-none');
    $('#saveStudyId').addClass('dis-none');
    $('#doneStudyId').addClass('dis-none');
    $('.deleteNotificationButtonHide').addClass('dis-none');
    </c:if>

    $('.studyNotificationList').on('click', function () {
      $('.studyNotificationList').prop('disabled', true);
      $('#viewStudyNotificationListPage').submit();
    });

    $('#deleteStudyNotification').on('click', function () {
      bootbox.confirm("Are you sure you want to delete this notification?", function (result) {
        if (result) {
          $('#deleteStudyNotificationForm').submit();
        }
      });
    });

    var today, datepicker;
    <c:if test="${ empty notificationBO.scheduleDate}">
    today = new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate());
    </c:if>
    
    <c:if test="${not empty notificationBO.scheduleDate}">
    today=${notificationBO.scheduleDate};
    </c:if>
    
    $('.datepicker').datetimepicker({
      format: 'MM/DD/YYYY',
      ignoreReadonly: true,
      minDate: today,
      useCurrent: false
    }).on('dp.change change', function (e) {
      validateTime();
    });

    $('.timepicker').datetimepicker({
      format: 'h:mm a',
      minDate: 0
    }).on('dp.change change', function (e) {
      validateTime();
    });

    $(".datepicker").on("click", function (e) {
      $('.datepicker').data("DateTimePicker").minDate(serverDate());
    });

    $(".timepicker").on("click", function (e) {
      var dt = $('#datetimepicker').val();
      if (dt != '' && dt != moment(serverDate()).format("MM/DD/YYYY")) {
        $('.timepicker').data("DateTimePicker").minDate(false);
        $('.timepicker').parent().removeClass('has-error has-danger').find(
            '.help-block.with-errors').empty();
      } else {
        $('.timepicker').data("DateTimePicker").minDate(serverDateTime());
      }
    });

    $('#inlineRadio2').on('click', function () {
      $('#datetimepicker, #timepicker1').removeAttr('required');
      $("#datetimepicker, #timepicker1").parent().removeClass('has-error has-danger');
      $("#datetimepicker, #timepicker1").parent().find(".help-block").empty();
      $('.add_notify_option').addClass('dis-none');
      resetValidation('.mandatoryForStudyNotification');
    });

    $('#inlineRadio1').on('click', function () {
      $('#datetimepicker, #timepicker1').val('');
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('.add_notify_option').removeClass('dis-none');
      $('#datetimepicker, #timepicker1').attr('required', 'required');
      $('#studyNotificationFormId').find('.resetVal').each(function () {
        $(this).val($(this).attr('oldValue'));
      });
      resetValidation('.mandatoryForStudyNotification');
    });

    $("#doneStudyId").on('click', function (e) {
      $('#inlineRadio1, #inlineRadio2').attr('required', 'required');
      $('#buttonType').val('done');
      if (isFromValid('#studyNotificationFormId')) {
        if ($('#inlineRadio2').prop('checked')) {
          bootbox.confirm("Are you sure you want to send this notification immediately?",
              function (result) {
                if (result) {
                  $('#doneStudyId').prop('disabled', true);
                  $('#studyNotificationFormId').submit();
                }
              });
        } else if ($('#inlineRadio1').prop('checked')) {
          if (validateTime()) {
            $('#doneStudyId').prop('disabled', true);
            $('#studyNotificationFormId').submit();
          }
        }
      } else {
        $('#doneStudyId').prop('disabled', false);
      }
    });

    $("#resendStudyId").on('click', function (e) {
      $('#inlineRadio1, #inlineRadio2').attr('required', 'required');
      $('#buttonType').val('resend');
      if (isFromValid('#studyNotificationFormId')) {
        $('#notificationText').prop('disabled', false);
        if ($('#inlineRadio2').prop('checked')) {
          bootbox.confirm("Are you sure you want to resend this notification immediately?",
              function (result) {
                if (result) {
                  $('#resendStudyId').prop('disabled', true);
                  $('#studyNotificationFormId').submit();
                }
              });
        } else if ($('#inlineRadio1').prop('checked')) {
          if (validateTime()) {
            $('#resendStudyId').prop('disabled', true);
            $('#studyNotificationFormId').submit();
          }
        }
      } else {
        $('#resendStudyId').prop('disabled', false);
      }
    });

    $('#saveStudyId').click(function () {
      $('#datetimepicker, #timepicker1').removeAttr('required', 'required');
      $('#buttonType').val('save');
      if (isFromValid('#studyNotificationFormId')) {
        if ($('#inlineRadio2').prop('checked')) {
          bootbox.confirm("Are you sure you want to send this notification immediately?",
              function (result) {
                if (result) {
                  $('#saveStudyId').prop('disabled', true);
                  $('#studyNotificationFormId').submit();
                }
              });
        } else if ($('#inlineRadio1').prop('checked')) {
          if (validateTime()) {
            $('#saveStudyId').prop('disabled', true);
            $('#studyNotificationFormId').submit();
          }
        }
      } else {
        $('#saveStudyId').prop('disabled', false);
      }
    });

    $('.goToNotificationListForm').on('click', function () {
      <c:if test="${notificationBO.actionPage eq 'edit' || notificationBO.actionPage eq 'addOrCopy' && not notificationBO.notificationSent}">
      bootbox.confirm({
        closeButton: false,
        message: 'You are about to leave the page and any unsaved changes will be lost. Are you sure you want to proceed?',
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
            $('#viewStudyNotificationListPage').submit();
          }
        }
      });
      </c:if>
      <c:if test="${notificationBO.actionPage eq 'view' || notificationBO.actionPage eq 'edit' && notificationBO.notificationSent}">
      $('#viewStudyNotificationListPage').submit();
      </c:if>
      <c:if test="${notificationBO.actionPage eq 'resend' && not notificationBO.notificationSent}">
      $('#viewStudyNotificationListPage').submit();
      </c:if>
      <c:if test="${notificationBO.actionPage eq 'resend' && notificationBO.notificationSent}">
      bootbox.confirm({
        closeButton: false,
        message: 'You are about to leave the page and any unsaved changes will be lost. Are you sure you want to proceed?',
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
            $('#viewStudyNotificationListPage').submit();
          }
        }
      });
      </c:if>
    });

  });

  function validateTime() {
    var dt = $('#datetimepicker').val();
    var tm = $('#timepicker1').val();
    var valid = true;
    if (dt && tm) {
      dt = moment(dt, "MM/DD/YYYY").toDate();
      thisDate = moment($('.timepicker').val(), "h:mm a").toDate();
      dt.setHours(thisDate.getHours());
      dt.setMinutes(thisDate.getMinutes());
      $('.timepicker').parent().removeClass('has-error has-danger').find(
          '.help-block.with-errors').empty();
      if (dt < serverDateTime()) {
        $('.timepicker').parent().addClass('has-error has-danger').find(
            '.help-block.with-errors').empty().append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
            "Please select a time in the future"));
        valid = false;
      }
    }
    return valid;
  }
</script>