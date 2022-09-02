<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date"/>
<c:set var="tz" value="America/Los_Angeles"/>

<style>
.bootstrap-select>.dropdown-toggle {
    width: 274px !important;
}
.bootstrap-select.btn-group .dropdown-menu {
    min-width: 274px !important;
    }
</style>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mt-md mb-md">
  <!-- widgets section-->
  <div class="col-sm-12 col-md-12 col-lg-12 p-none">
    <div class="black-lg-f">
      <span class="mr-xs">
        <a href="javascript:void(0)" class="backOrCancelBtnOfNotification">
          <img src="/studybuilder/images/icons/back-b.png" alt=""/></a>
      </span>
      <c:if
          test="${notificationBO.actionPage eq 'addOrCopy' || notificationBO eq null}">Create notification</c:if>
      <c:if test="${notificationBO.actionPage eq 'edit'}">Edit notification</c:if>
      <c:if test="${notificationBO.actionPage eq 'view'}">View notification</c:if>
      <c:if test="${notificationBO.actionPage eq 'resend'}">Resend notification</c:if>
    </div>
  </div>
</div>
<form:form
    action="/studybuilder/adminNotificationEdit/saveOrUpdateNotification.do?${_csrf.parameterName}=${_csrf.token}"
    data-toggle="validator" role="form" id="appNotificationFormId" method="post" autocomplete="off">
  <input type="hidden" name="buttonType" id="buttonType">
  <input type="hidden" name="notificationId" value="${notificationBO.notificationId}">

  <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
    <div class="col-md-12 p-none">
      <div class="box-space white-bg">

        <!-- form- input-->
        <c:if test="${notificationBO.notificationSent && notificationBO.actionPage eq 'edit'}">
          <div>
            <span>This notification has already been sent out to users and cannot be edited. To resend this
              notification, use the Resend action and choose a time for firing the notification.
            </span>
          </div>
        </c:if>
        <div class="pl-none">
          <div class="gray-xs-f mb-xs mt-xs">Notification text (250 characters max)
            <span
                class="requiredStar">*
            </span>
          </div>
          <div class="form-group">
            <textarea autofocus="autofocus" class="form-control" maxlength="250" rows="5"
                      id="notificationText" name="notificationText" required data-error="Please fill out this field" 
            >${notificationBO.notificationText}</textarea>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>

        <div class="mt-xlg mb-lg">
          <div class="form-group">
            <span class="radio radio-info radio-inline p-45">
              <input type="radio" id="inlineRadio1" value="notImmediate"
                     name="currentDateTime"
                     <c:if
                         test="${notificationBO.notificationScheduleType eq 'notImmediate'}">checked</c:if>
                     <c:if
                         test="${notificationBO.actionPage eq 'addOrCopy'}">checked</c:if>>
              <label for="inlineRadio1">Schedule this notification
              <span
               <fmt:formatDate value = "${date}" pattern="z" var="server_timezone"/>
          class="ml-xs sprites_v3 filled-tooltip Selectedtooltip"
          style="width: 20px;background-position: -164px -68px;"
          data-toggle="tooltip"
          data-placement="top"
          title="The notification gets delivered to mobile app users at the selected date and time as per server time zone which is ${server_timezone}."></span>
              </label>
            </span>
            <span class="radio radio-inline">
              <input type="radio" id="inlineRadio2" value="immediate"
                     name="currentDateTime"
                     <c:if
                         test="${notificationBO.notificationScheduleType eq 'immediate'}">checked</c:if>>
              <label for="inlineRadio2">Send immediately</label>
            </span>
            <div class="help-block with-errors red-txt"></div>
            <div class="clearfix"></div>
          </div>
        </div>


        <div class="add_notify_option mandatoryForAppNotification">
          <div class="gray-xs-f mb-xs">Date
            <span class="requiredStar">*</span>
          </div>
          <div class="form-group date">
            <input id='datetimepicker' type="text" class="form-control calendar datepicker resetVal" data-error="Please fill out this field" 
                   name="scheduleDate" value="${notificationBO.scheduleDate}"
                   oldValue="${notificationBO.scheduleDate}"
                   placeholder="MM/DD/YYYY" disabled/>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>

        <div class="add_notify_option mandatoryForAppNotification">
          <div class="gray-xs-f mb-xs">Time <c:if test="${notificationBO.actionPage ne 'view'}">
          </c:if>
            <span class="requiredStar">*</span>
          </div>
          <div class="form-group">
            <input id="timepicker1" class="form-control clock timepicker resetVal" data-error="Please fill out this field" 
                   name="scheduleTime"
                   value="${notificationBO.scheduleTime}" oldValue="${notificationBO.scheduleTime}"
                   placeholder="00:00" disabled/>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>

     <div class=" ">
          <div class="form-group">
            <div class="gray-xs-f mb-xs">App to which the notification must be sent
            <span class="requiredStar">*</span></div>
			<c:choose>
               <c:when test="${notificationBO.actionPage eq 'view' || notificationBO.actionPage eq 'resend'}">
                 <input type="text" id="notificationAppId"
                    value="${notificationBO.appId}" disabled/>
             </c:when>
             <c:otherwise>
             <select id="appId" class="selectpicker" name="appId" required data-error="Please fill out this field">
              <option value=''>Select app ID</option>
              <c:forEach items="${gatewayAppList}" var="app">
                <option
                    value="${app}" ${notificationBO.appId eq app ? 'selected' : ''}>${app}</option>
              </c:forEach>
              </select>
            </c:otherwise>
            </c:choose>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>

           <c:if test="${not empty notificationHistoryNoDateTime}">
             <div class="gray-xs-f mb-xs mt-xs">Previously sent on: </div>
              <c:forEach items="${notificationHistoryNoDateTime}" var="notificationHistory">
                <span
                    class="lastSendDateTime">${notificationHistory.notificationSentdtTime}</span>
                <br><br>
              </c:forEach>
            </c:if>
            
      </div>
    </div>
  </div>
  <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
    <div class="white-bg box-space t-bor text-right">
      <div class="dis-line text-right ml-md">

        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn backOrCancelBtnOfNotification">
            Cancel
          </button>
        </div>
        <c:if test="${empty notificationBO || notificationBO.actionPage eq 'addOrCopy'}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-primary blue-btn addNotification"
                    id="immiSaveButton">
              Create
            </button>
          </div>
        </c:if>
        <c:if
            test="${not empty notificationBO && not notificationBO.notificationSent && notificationBO.actionPage eq 'edit' && empty notificationHistoryNoDateTime}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-primary blue-btn deleteButtonHide"
                    id="deleteNotification">
              Delete
            </button>
          </div>
        </c:if>
        <c:if
            test="${not empty notificationBO && not notificationBO.notificationSent && notificationBO.actionPage eq 'edit'}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-primary blue-btn updateNotification"
                    id="immiUpdateButton">
              Update
            </button>
          </div>
        </c:if>
        <c:if
            test="${not empty notificationBO && notificationBO.notificationSent && notificationBO.actionPage eq 'resend'}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-primary blue-btn resendNotification"
                    id="immiResendButton">
              Resend
            </button>
          </div>
        </c:if>
      </div>
    </div>
  </div>
</form:form>
<form:form action="/studybuilder/adminNotificationView/viewNotificationList.do"
           id="notificationBackOrCancelBtnForm"
           name="notificationBackOrCancelBtnForm" method="post">
</form:form>
<form:form action="/studybuilder/adminNotificationEdit/deleteNotification.do"
           id="deleteNotificationForm"
           name="deleteNotificationForm" method="post">
  <input type="hidden" name="notificationId" value="${notificationBO.notificationId}">
</form:form>
<script>
  $(document).ready(function () {
	$('[data-toggle="tooltip"]').tooltip();
    $('#rowId').parent().removeClass('white-bg');
    $("#notification").addClass("active");

    <c:if test="${notificationBO.notificationSent || notificationBO.actionPage eq 'view'}">
    $('#appNotificationFormId input,textarea').prop('disabled', true);
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    </c:if>

    <c:if test="${not notificationBO.notificationSent && notificationBO.actionPage eq 'resend'}">
    $('#appNotificationFormId input,textarea').prop('disabled', true);
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    </c:if>

    <c:if test="${notificationBO.actionPage eq 'addOrCopy'}">
    $('#inlineRadio1').prop('checked', 'checked');
    $('.deleteButtonHide').addClass('dis-none');
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('#datetimepicker, #timepicker1').attr('required', 'required');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
    }
    </c:if>

    <c:if test="${not notificationBO.notificationSent && notificationBO.actionPage eq 'edit' && not empty notificationHistoryNoDateTime}">
    $('#appNotificationFormId textarea').prop('disabled', true);
    $('.deleteButtonHide').addClass('dis-none');
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('#datetimepicker, #timepicker1').attr('required', 'required');
      $('#immiUpdateButton').text('Update');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
      $('#immiUpdateButton').text('Send');
    }
    </c:if>

    <c:if test="${not notificationBO.notificationSent && notificationBO.actionPage eq 'edit' && empty notificationHistoryNoDateTime}">
    $('.deleteButtonHide').removeClass('dis-none');
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('#datetimepicker, #timepicker1').attr('required', 'required');
      $('#immiUpdateButton').text('Update');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
      $('#immiUpdateButton').text('Send');
    }
    </c:if>


    <c:if test="${notificationBO.notificationSent && notificationBO.actionPage eq 'resend'}">
    $('#appNotificationFormId #inlineRadio1,#inlineRadio2').prop('disabled', false);
    $('#appNotificationFormId input').prop('disabled', false);
    $('#appNotificationFormId textarea').prop('disabled', true);
    if ($('#inlineRadio1').prop('checked')) {
      $('#datetimepicker, #timepicker1').attr('required', 'required');
      $('#immiResendButton').text('Save');
    }
    if ($('#inlineRadio2').prop('checked')) {
      $('.add_notify_option').addClass('dis-none');
      $('#datetimepicker, #timepicker1').removeAttr('required');
      $('#immiResendButton').text('Resend');
    }
    $('#buttonType').val('resend');
    $('#notificationAppId').prop('disabled', true);
    </c:if>

    $('#inlineRadio2').on('click', function () {
      $('#datetimepicker, #timepicker1').removeAttr('required');
      $("#datetimepicker, #timepicker1").parent().removeClass('has-error has-danger');
      $("#datetimepicker, #timepicker1").parent().find(".help-block").empty();
      $('.add_notify_option').addClass('dis-none');
      resetValidation('.mandatoryForAppNotification');
      $('.addNotification').prop('disabled', false);
      $('#immiSaveButton').text('Send');
      $('#immiUpdateButton').text('Send');
      $('#immiResendButton').text('Resend');
    });

    $('#inlineRadio1').on('click', function () {
      $('#datetimepicker, #timepicker1').val('');
      $('#datetimepicker, #timepicker1').prop('disabled', false);
      $('.add_notify_option').removeClass('dis-none');
      $('#datetimepicker, #timepicker1').attr('required', 'required');
      $('#appNotificationFormId').find('.resetVal').each(function () {
        $(this).val($(this).attr('oldValue'));
      });
      resetValidation('.mandatoryForAppNotification');
      $('#immiSaveButton').text('Create');
      $('#immiUpdateButton').text('Update');
      $('#immiResendButton').text('Save');
    });

    $('.backOrCancelBtnOfNotification').on('click', function () {
      $('.backOrCancelBtnOfNotification').prop('disabled', true);
      $('#notificationBackOrCancelBtnForm').submit();
    });

    $('.addNotification').on('click', function () {
      $('#buttonType').val('add');
      if (isFromValid('#appNotificationFormId')) {
        if ($('#inlineRadio2').prop('checked')) {
          bootbox.confirm("Are you sure you want to send this notification immediately?",
              function (result) {
                if (result) {
                  $('.updateNotification').prop('disabled', true);
                  $('#appNotificationFormId').submit();
                }
              });
        } else if ($('#inlineRadio1').prop('checked')) {
          if (validateTime()) {
            $('.updateNotification').prop('disabled', true);
            $('#appNotificationFormId').submit();
          }
        }
      } else {
        $('.addNotification').prop('disabled', false);
      }
    });

    $('.updateNotification').on('click', function () {
      $('#buttonType').val('update');
      if (isFromValid('#appNotificationFormId')) {
        if ($('#inlineRadio2').prop('checked')) {
          bootbox.confirm("Are you sure you want to send this notification immediately?",
              function (result) {
                if (result) {
                  $('.updateNotification').prop('disabled', true);
                  $('#appNotificationFormId').submit();
                }
              });
        } else if ($('#inlineRadio1').prop('checked')) {
          if (validateTime()) {
            $('.updateNotification').prop('disabled', true);
            $('#appNotificationFormId').submit();
          }
        }
      } else {
        $('.updateNotification').prop('disabled', false);
      }
    });

    $('.resendNotification').on('click', function () {
      $('#buttonType').val('resend');
      if (isFromValid('#appNotificationFormId')) {
        $('#notificationText').prop('disabled', false);
        if ($('#inlineRadio2').prop('checked')) {
          bootbox.confirm("Are you sure you want to resend this notification immediately?",
              function (result) {
                if (result) {
                  $('.updateNotification').prop('disabled', true);
                  $('#appNotificationFormId').submit();
                }
              });
        } else if ($('#inlineRadio1').prop('checked')) {
          if (validateTime()) {
            $('.updateNotification').prop('disabled', true);
            $('#appNotificationFormId').submit();
          }
        }
      } else {
        $('.updateNotification').prop('disabled', false);
      }
    });

    $('#deleteNotification').on('click', function () {
      bootbox.confirm("Are you sure you want to delete this notification?", function (result) {
        if (result) {
          $('#deleteNotificationForm').submit();
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
      format: 'h:mm a'
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
      } else {
        $('.timepicker').data("DateTimePicker").minDate(serverDateTime());
      }
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
      if (dt < serverDateTime()) {
        $('#timepicker1').val('');
        $('.timepicker').parent().addClass('has-error has-danger').find('.help-block.with-errors')
            .empty().append(
            	$("<ul><li> </li></ul>").attr("class","list-unstyled").text("Please select a time in the future"));
        valid = false;
      } else {
        $('.timepicker').parent().removeClass('has-error has-danger').find(
            '.help-block.with-errors').empty();
      }
    }
    return valid;
  }

  $(document).on('mouseenter', '.dropdown-toggle',  function () {
      $(this).removeAttr("title");
  });
</script>
