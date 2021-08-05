<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.fdahpstudydesigner.util.SessionObject" %>

<!-- Start left Content here -->
<!-- ============================================================== -->
<div class="col-sm-2 col-lc p-none">
  <div class="left-content-container">
    <ul class="menuNav">
      <li>
        <div class="tit_wrapper" data-toggle="tooltip" data-placement="top"
             title="${fn:escapeXml(not empty studyBo.name?studyBo.name:'Create Apps')}">${not empty studyBo.name?studyBo.name:'Create Apps'}</div>
        <div class="mb-lg ${empty studyBo.status?'hide':''}">
          <span class="study_status
	                <c:if test="${studyBo.status eq 'Pre-launch' || studyBo.status eq 'Pre-launch(Published)'}">
	                    pre-launch_txt
	                </c:if>
	                <c:if test="${studyBo.status eq 'Active'}">
	                    active_txt
	                </c:if>
	                <c:if test="${studyBo.status eq 'Paused'}">
	                    paused_txt
	                </c:if>
	                <c:if test="${studyBo.status eq 'Deactivated'}">
	                    deactivated_txt
	                </c:if>
	                ">${studyBo.status}</span>
          <c:set var="isLive">${_S}isLive</c:set>
          <span
              class="version">${not empty  sessionScope[isLive]?studyBo.studyVersionBo.studyLVersion:''}</span>
        </div>
      </li>
      <li class="first active">
        APP INFORMATION
        <c:if test="${studyBo.studySequenceBo.basicInfo}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="second commonCls">
        APP SETTINGS
        <c:if test="${studyBo.studySequenceBo.settingAdmins}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="third commonCls">
        APP PROPERTIES
        <c:if test="${studyBo.studySequenceBo.overView}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="fourth commonCls">
        DEVELOPER CONFIGURATIONS
        <c:if test="${studyBo.studySequenceBo.eligibility}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="tenth commonCls">
        ACTIONS
      </li>
    </ul>
  </div>
</div>

<!-- End left Content here -->
<script type="text/javascript">
  $(document).ready(function () {
    $("#rowId").addClass('lc-gray-bg');
    $('#createStudyId').show();
    $("#myNavbar li.studyClass").addClass('active');
    $('[data-toggle="tooltip"]').tooltip();

    $('.cancelBut').click(function () {
      <c:if test="${empty permission}">
      $('.cancelBut').prop('disabled', true);
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
            a.href = "/studybuilder/adminStudies/studyList.do";
            document.body.appendChild(a).click();
          } else {
            $('.cancelBut').prop('disabled', false);
          }
        }
      });
      </c:if>
      <c:if test="${not empty permission}">
      var a = document.createElement('a');
      a.href = "/studybuilder/adminStudies/studyList.do";
      document.body.appendChild(a).click();
      </c:if>
    });

    var a = document.createElement('a');
    $('.first').click(function () {
      a.href = "/studybuilder/adminStudies/viewBasicInfo.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });

    <c:if test="${(not empty studyBo.studySequenceBo && studyBo.studySequenceBo.basicInfo) || (not empty studyBo.status && studyBo.status ne 'Pre-launch')}">
    $('.second').click(function () {
      a.href = "/studybuilder/adminStudies/viewSettingAndAdmins.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    <c:if test="${studyBo.studySequenceBo.settingAdmins || studyBo.status ne 'Pre-launch'}">
    $('.third').click(function () {
      a.href = "/studybuilder/adminStudies/overviewStudyPages.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.fourth').click(function () {
      a.href = "/studybuilder/adminStudies/viewStudyEligibilty.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.fifth').click(function () {
      a.href = "/studybuilder/adminStudies/consentListPage.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.fifthConsent').click(function () {
      a.href = "/studybuilder/adminStudies/consentListPage.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.fifthComre').click(function () {
      a.href = "/studybuilder/adminStudies/comprehensionQuestionList.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.fifthConsentReview').click(function () {
      a.href = "/studybuilder/adminStudies/consentReview.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.sixth , .sixthQuestionnaires').click(function () {
      a.href = "/studybuilder/adminStudies/viewStudyQuestionnaires.do?_S=${param._S}";
      document.body.appendChild(a).click();

    });
    $('.sixthTask').click(function () {
      a.href = "/studybuilder/adminStudies/viewStudyActiveTasks.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });

    $('.eight').click(function () {
      a.href = "/studybuilder/adminStudies/getResourceList.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.eighthResources').click(function () {
      $('.eighthResources').addClass('cursor-none');
      a.href = "/studybuilder/adminStudies/getResourceList.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.eigthNotification').click(function () {
      $('.eigthNotification').addClass('cursor-none');
      a.href = "/studybuilder/adminStudies/viewStudyNotificationList.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.tenth').click(function () {
      $('.tenth').addClass('cursor-none');
      a.href = "/studybuilder/adminStudies/actionList.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    </c:if>
    </c:if>
    <c:if test="${(empty studyBo.status || studyBo.status eq 'Pre-launch') && ((empty studyBo.studySequenceBo) || not studyBo.studySequenceBo.basicInfo)}">
    $('.commonCls').addClass('cursor-none-without-event');
    </c:if>
    <c:if test="${(empty studyBo.status ||studyBo.status eq 'Pre-launch') && studyBo.studySequenceBo.basicInfo && not studyBo.studySequenceBo.settingAdmins}">
    $('.commonCls').not('.second').addClass('cursor-none-without-event');
    </c:if>
    $(window).on('load resize', function () {

      rtime1 = new Date();
      if (timeout1 === false) {
        timeout1 = true;
        setTimeout(resizeend1, delta1);
      }

    });
  });
  //Internet Explorer 6-11
  var isIE = /*@cc_on!@*/false || !!document.documentMode;

  // Edge 20+
  var isEdge = !isIE && !!window.StyleMedia;
  if (isIE || isEdge) {
    $(window).on('load resize', function () {

      rtime1 = new Date();
      if (timeout1 === false) {
        timeout1 = true;
        setTimeout(resizeend1, delta1);
      }

    });
  }
  var rtime1;
  var timeout1 = false;
  var delta1 = 200;

  function resizeend1() {
    if (new Date() - rtime1 < delta1) {
      setTimeout(resizeend1, delta1);
    } else {
      timeout1 = false;
      slideUpStudyMenu();
    }
  }

  function slideUpStudyMenu() {
    $(".slideUp.active").ScrollTo();
  }
  
</script>
