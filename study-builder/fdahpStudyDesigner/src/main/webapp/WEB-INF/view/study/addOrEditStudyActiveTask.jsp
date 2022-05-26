<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<div class="col-sm-10 col-rc white-bg p-none">

  <!--  Start top tab section-->
  <div class="right-content-head">
    <div class="text-right">
      <div class="black-md-f dis-line pull-left line34">
        <span
            class="pr-sm cur-pointer"
            onclick="goToBackPage(this);">
          <img src="../images/icons/back-b.png" class="pr-md" alt=""/></span>
        <c:if test="${actionPage eq 'add'}"> Add active task</c:if>
        <c:if test="${actionPage eq 'addEdit'}">Edit active task</c:if>
        <c:if test="${actionPage eq 'view'}">View active task <c:set
            var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span> ':''} ${not empty  sessionScope[isLive]?activeTaskBo.activeTaskVersion:''}
        </c:if>
      </div>

      <div class="dis-line form-group mb-none mr-sm">
        <button type="button" class="btn btn-default gray-btn" onclick="goToBackPage(this);">
          Cancel
        </button>
      </div>

      <div class="dis-line form-group mb-none mr-sm">
        <button type="button" class="btn btn-default gray-btn actBut" id="saveId">Save</button>
      </div>

      <div class="dis-line form-group mb-none">
        <button type="button" class="btn btn-primary blue-btn actBut" id="doneId">Done</button>
      </div>
    </div>
  </div>
  <!--  End  top tab section-->

  <!--  Start body tab section -->
  <div class="right-content-body pt-none pl-none pr-none">

    <ul class="nav nav-tabs review-tabs gray-bg" id="tabsId">
      <li class="contentClass active">
        <a data-toggle="tab" href="#content">Content</a>
      </li>
      <li class="scheduleTaskClass linkDis" disabled>
        <a data-toggle="tab"
           href="#schedule">Schedule
        </a>
      </li>
    </ul>
    <div class="tab-content pl-xlg pr-xlg">
      <!-- Content-->
      <div id="content" class="tab-pane fade in active mt-xlg">
        <div class="mt-md blue-md-f text-uppercase">Select Active task</div>
        <div class="gray-xs-f mt-md mb-sm">Choose from a list of pre-defined active tasks</div>
        <div class="col-md-4 p-none">
          <select class="selectpicker targetOption" id="targetOptionId" taskId="${activeTaskBo.id}"
                  title="Select">
            <c:forEach items="${activeTaskListBos}" var="activeTaskTypeInfo">
              <option
                  value="${activeTaskTypeInfo.activeTaskListId}" ${activeTaskBo.taskTypeId eq activeTaskTypeInfo.activeTaskListId ?'selected':''}>${activeTaskTypeInfo.taskName}</option>
            </c:forEach>
          </select>
        </div>
        <div class="clearfix"></div>
        <div class="mt-sm black-xs-f italic-txt activeText"></div>

        <div class="changeContent"></div>
      </div>
      <!-- End Content-->
      <!---  Schedule --->
      <div id="schedule" class="tab-pane fade mt-xlg"></div>
    </div>
  </div>
  <!--  End body tab section -->
</div>
<!-- End right Content here -->
<script>
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    // Fancy Scroll Bar
    var changeTabSchedule = true;

    $(".menuNav li.active").removeClass('active');
    $(".sixthTask").addClass('active');
    actionPageView();

    var typeOfActiveTask = '${activeTaskBo.taskTypeId}';
    var activeTaskInfoId = '${activeTaskBo.id}';
    if (!activeTaskInfoId) {
      activeTaskInfoId = '${activeTaskInfoId}';
    }
    var actionType = '${actionPage}';

    var selectedTask = $('.targetOption').find("option:selected").text();
    
    if(actionType == 'view'){
        $('.manuallyContainer').find('input:text').attr('disabled', 'disabled');
    }

    if (activeTaskInfoId) {
      $('.targetOption').prop('disabled', true);
      $('.targetOption').addClass('linkDis');
      $('.activeText').empty().append(
          'This task records fetal activity for a given duration of time,').append($("<br>")).append('in terms of the number of times the woman experiences kicks.');
      $('.scheduleTaskClass').prop('disabled', false);
      $('.scheduleTaskClass').removeClass('linkDis');
    } else {
      $('.actBut').hide();
    }
    if (typeOfActiveTask && activeTaskInfoId) {
      loadSelectedATask(typeOfActiveTask, activeTaskInfoId, actionType);
    }
    $(".schedule").click(function () {
      $(".all").addClass("dis-none");
      var schedule_opts = $(this).val();
      $("." + schedule_opts).removeClass("dis-none");
    });
    $("#targetOptionId").change(function () {
      console.log($(this).val());
      var taskId = $(this).val();
      if (taskId == 1) {
        $('.activeText').empty().append(
            'This task records fetal activity for a given duration of time,').append($("<br>")).append('in terms of the number of times the woman experiences kicks.');
      } else if (taskId == 2) {
        $('.activeText').text("This task measures a person's problem-solving skills.");
      } else if (taskId == 3) {
        $('.activeText').text(
            "The task collects data that can be used to assess visuospatial memory and executive function.");
      }
      var typeOfActiveTask = $(this).val();
      var activeTaskInfoId = $(this).attr('taskId');
      $('.changeContent').empty();
      $(document).find('#saveId').unbind();
      $(document).off('click', '#doneId');
      loadSelectedATask(typeOfActiveTask, activeTaskInfoId, actionType);
      $('.actBut').show();
      $('.scheduleTaskClass').prop('disabled', false);
      $('.scheduleTaskClass').removeClass('linkDis');
    });
    if (activeTaskInfoId || selectedTask) {
      loadActiveSchedule(changeTabSchedule);
    }

    function loadSelectedATask(typeOfActiveTask, activeTaskInfoId, actionType) {
      $(".changeContent").load(
          "/studybuilder/adminStudies/navigateContentActiveTask.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}",
          {
            noncache: new Date().getTime(),
            typeOfActiveTask: typeOfActiveTask,
            activeTaskInfoId: activeTaskInfoId,
            actionType: actionType
          },
          function () {
            
            resetValidation($(this).parents('form'));
            var dt = new Date();
            $('#inputClockId').datetimepicker({
              format: 'HH:mm',
              minDate: new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 00, 00),
              maxDate: new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 23, 59)
            });
            actionPageView();
            var currentPage = '${currentPage}';
            $('#currentPageId').val(currentPage);
          });

    }

    function loadActiveSchedule(changeTabSchedule) {
      if (changeTabSchedule) {
        $("#schedule").load(
            "/studybuilder/adminStudies/viewScheduledActiveTask.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}",
            {
              noncache: new Date().getTime(),
              activeTaskId: activeTaskInfoId
            }, function () {
              resetValidation($('form'));
              actionPageView();
            });
        changeTabSchedule = false;
      } else {
        resetValidation($('form'));
      }
    }

    $('#tabsId a').click(function (e) {
      e.preventDefault();
      $(this).tab('show');
    });

    // store the currently selected tab in the hash value
    $("ul.nav-tabs > li > a").on("shown.bs.tab", function (e) {
      var id = $(e.target).attr("href").substr(1);
      window.location.hash = id;
      $('#currentPageId').val(id);
    });

    // on load of the page: switch to the currently selected tab
    var hash = window.location.hash;
    $('#tabsId a[href="' + hash + '"]').tab('show');
    window.addEventListener("popstate", function (e) {
      var activeTab = $('[href="' + window.location.hash + '"]');
      if (activeTab.length) {
        activeTab.tab('show');
      } else {
        $('.nav-tabs a:first').tab('show');
      }
    });
  });

  $("#doneId").click(function () {
    var scheduletype = $('input[name="scheduleType"]:checked').val();
	$('.manually-anchor-option').each(function(customAnchorCount) {
		if ($('#xdays' + customAnchorCount).val() == '' && scheduletype == 'AnchorDate') {
	  	  $('#xdays' + customAnchorCount).parent().addClass("has-danger").addClass("has-error");
     	  $('#xdays' + customAnchorCount).parent().find(".help-block-timer").empty().append(
     	  $("<ul><li> </li></ul>").attr("class","list-unstyled").text(
     	       "Please fill out this field"));
   	  	  $('#xdays' + customAnchorCount).parent().find(".help-block-timer").show();
	  	  $('#xdays' + customAnchorCount).parent().find(".help-block").hide();
    	}
		
		if ($('#manualStartTime' + customAnchorCount).val() == '' && scheduletype == 'AnchorDate') {
	  	  $('#manualStartTime' + customAnchorCount).parent().addClass("has-danger").addClass("has-error");
     	  $('#manualStartTime' + customAnchorCount).parent().find(".help-block-timer").empty().append(
     	  $("<ul><li> </li></ul>").attr("class","list-unstyled").text(
     	       "Please fill out this field"));
     	  $('#manualStartTime' + customAnchorCount).parent().find(".help-block-timer").show();
     	  $('#manualStartTime' + customAnchorCount).parent().find(".help-block").hide();
    	}
		
		if ($('#manualEndTime' + customAnchorCount).val() == '' && scheduletype == 'AnchorDate') {
	  	  $('#manualEndTime' + customAnchorCount).parent().addClass("has-danger").addClass("has-error");
     	  $('#manualEndTime' + customAnchorCount).parent().find(".help-block-timer").empty().append(
     	  $("<ul><li> </li></ul>").attr("class","list-unstyled").text(
     	       "Please fill out this field"));
    	}
	});
	
	$('.manually-option').each(function(customCount) {
		if ($('#customTime' + customCount).val() == '' && scheduletype == 'Regular') {
		  $('#customTime' + customCount).parent().find(".help-block").show();
		  $('#customTime' + customCount).parent().find(".help-block-timer").hide();
		}
	});
  });
		
  function goToBackPage(item) {
    //window.history.back();
    <c:if test="${actionPage ne 'view'}">
    $(item).prop('disabled', true);
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
          var a = document.createElement('a');
          a.href = "/studybuilder/adminStudies/viewStudyActiveTasks.do?_S=${param._S}";
          document.body.appendChild(a).click();
        } else {
          $(item).prop('disabled', false);
        }
      }
    });
    </c:if>
    <c:if test="${actionPage eq 'view'}">
    var a = document.createElement('a');
    a.href = "/studybuilder/adminStudies/viewStudyActiveTasks.do?_S=${param._S}";
    document.body.appendChild(a).click();
    </c:if>
  }

  function actionPageView() {
    <c:if test="${actionPage eq 'view'}">
    $(document).find('input,textarea,select').prop('disabled', true);
    $(document).find('form.elaborateClass').addClass('linkDis');
    $(document).find('.actBut, .addBtnDis, .remBtnDis').remove();
    </c:if>
  }

  $(document).on('mouseenter', '.dropdown-toggle',  function () {
      $(this).removeAttr("title");
  });
  
  var actionType = '${actionPage}';
  $(".scheduleTaskClass ").click(function () {
     if(actionType == 'view'){
   		  $('.manuallyContainer').find('input:text').attr('disabled', 'disabled');
     }
  })
 
</script>