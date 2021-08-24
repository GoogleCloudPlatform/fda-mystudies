<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.fdahpstudydesigner.util.SessionObject" %>

<style>
.left-content-container ul li:first-child {
	height: 85px !important;
}
.left-content-container ul li {
    width: 101% !important;
}
.post-launch_txt { color: #5ec456; }
.right-border { 
	border-right: 1px solid #95a2ab;
    height: 15px;
    position: absolute;
    margin-top: 2px;
}
</style>

<!-- Start left Content here -->
<!-- ============================================================== -->
<div class="col-sm-2 col-lc p-none">
  <div class="left-content-container">
    <ul class="menuNav">
      <li>
        <div class="tit_wrapper" data-toggle="tooltip" data-placement="top"
             title="${fn:escapeXml(not empty appBo.name?appBo.name:'Create Apps')}">${not empty appBo.name?appBo.name:'Create Apps'}</div>
             
         <div class="mb-lg ">
          <span class="study_status ${empty appBo.customAppId?'hide':''}">${appBo.customAppId}</span>
           <div class="clearfix"></div>
          <div>
          <span class="study_status 
                    <c:if test="${appBo.appStatus eq 'Active'}"> post-launch_txt </c:if>
	                <c:if test="${appBo.appStatus eq 'Deactivated'}"> paused_txt </c:if>
	                 <c:if test="${appBo.appStatus eq 'Draft'}"> paused_txt </c:if>
	                pr-sm"> ${appBo.appStatus} </span><span class="right-border"></span>
          <span class="study_status  post-launch_txt  pr-sm pl-sm"> Published </span> <span class="right-border"></span>
          <span class="study_status  post-launch_txt pr-sm pl-sm"> Distributed (1) </span>
          </div>
          <span class="version"></span>
        </div>

         <%-- <div class="mb-lg ${empty appBo.customAppId?'hide':''}"><span class="study_status">${appBo.customAppId}</span></div>
        <div class="mb-lg ${empty appBo.appsStatus?'hide':''}">
          <span class="study_status
	                <c:if test="${appBo.appsStatus eq 'Active'}">

	                    active_txt
	                </c:if>
	                <c:if test="${appBo.appsStatus eq 'Inactive'}">
	                    paused_txt
	                </c:if>
	                ">${appBo.appsStatus}</span><span class="study_status">|</span>
	                <span class="study_status active_txt"><c:if test="${appBo.isAppPublished}">Published</c:if>
	                <c:if test="${empty appBo.isAppPublished}">Not published</c:if></span><span class="study_status">|</span>
	                <span class="study_status active_txt"><c:if test="${appBo.iosAppDistributed eq 1|| appBo.androidAppDistributed  eq 1}">Distributed</c:if>
	                <c:if test="${appBo.iosAppDistributed eq 0|| appBo.androidAppDistributed  eq 0}">Not distributed</c:if></span>

          <c:set var="isLive">${_S}isLive</c:set>
          <span
              class="version">${not empty  sessionScope[isLive]?studyBo.studyVersionBo.studyLVersion:''}</span>
        </div> --%>
      </li>
      <li class="first active">
        APP INFORMATION
        <c:if test="${appBo.appSequenceBo.appInfo}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="second commonCls">
        APP SETTINGS
        <c:if test="${appBo.appSequenceBo.appSettings}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span> 
        </c:if>
      </li>
      <li class="third commonCls">
        APP PROPERTIES
        <c:if test="${appBo.appSequenceBo.appProperties}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="fourth commonCls">
        DEVELOPER CONFIGURATIONS
        <c:if test="${appBo.appSequenceBo.developerConfigs}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="fifth commonCls">
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
            a.href = "/studybuilder/adminApps/appList.do";
            document.body.appendChild(a).click();
          } else {
            $('.cancelBut').prop('disabled', false);
          }
        }
      });
      </c:if>
      <c:if test="${not empty permission}">
      var a = document.createElement('a');
      a.href = "/studybuilder/adminApps/appList.do";
      document.body.appendChild(a).click();
      </c:if>
    });

    var a = document.createElement('a');
    $('.first').click(function () {
      a.href = "/studybuilder/adminApps/viewAppsInfo.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    <c:if test="${appBo.appSequenceBo.appInfo}">
    $('.second').click(function () {
      a.href = "/studybuilder/adminApps/viewAppSettings.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    <c:if test="${appBo.appSequenceBo.appInfo && appBo.appSequenceBo.appSettings && appBo.appSequenceBo.actions}">
    $('.third').click(function () {
      a.href = "/studybuilder/adminApps/viewAppProperties.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.fourth').click(function () {
      a.href = "/studybuilder/adminApps/viewDevConfigs.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    </c:if>
    <c:if test="${appBo.appSequenceBo.appSettings}">
    $('.fifth').click(function () {
        a.href = "/studybuilder/adminApps/appActionList.do?_S=${param._S}";
        document.body.appendChild(a).click();
      });
    </c:if>
    </c:if>
    <c:if test="${appBo.appStatus eq 'Draft' && (not appBo.appSequenceBo.appInfo)}">
    $('.commonCls').addClass('cursor-none-without-event');
    </c:if>
    <c:if test="${appBo.appSequenceBo.appInfo && not appBo.appSequenceBo.appSettings}">
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
