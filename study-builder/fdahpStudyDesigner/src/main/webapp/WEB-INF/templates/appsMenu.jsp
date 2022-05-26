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
/*     margin-top: 2px; */
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
          <div style="display: inline-flex;">
          <span class="study_status right-border no-border ${empty appBo.customAppId?'hide':''}
                    <c:if test="${appBo.appStatus eq 'Active'}"> post-launch_txt </c:if>
	                <c:if test="${appBo.appStatus eq 'Deactivated'}"> paused_txt </c:if>
	                <c:if test="${appBo.appStatus eq 'Draft'}"> paused_txt </c:if>
	                pr-sm"> ${appBo.appStatus} </span>
	      <c:if test="${appBo.appStatus eq 'Active'}">
          <span class="study_status right-border pr-sm pl-sm ${appBo.isAppPublished?'post-launch_txt':'pre-launch_txt'}"> <c:if test="${appBo.isAppPublished}">Published </c:if><c:if test="${not appBo.isAppPublished}">Not<span  class="visibility_hidden">_</span>published </c:if></span>
          <span class="study_status pl-sm ${(appBo.iosAppDistributed || appBo.androidAppDistributed)?'post-launch_txt':'pre-launch_txt'}"> 
          <c:choose>
              <c:when test="${appBo.iosAppDistributed && appBo.androidAppDistributed}">Distributed (2)</c:when>
              <c:when test="${appBo.iosAppDistributed || appBo.androidAppDistributed}">Distributed (1)</c:when>
          <c:when test="${not appBo.iosAppDistributed && not appBo.androidAppDistributed}">Not<span  class="visibility_hidden">_</span>distributed</c:when>
          </c:choose></span>
          </c:if>
          </div>
          <span class="version"></span>
        </div>
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
      <li class="third commonCls1">
        APP PROPERTIES
        <c:if test="${appBo.appSequenceBo.appProperties}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="fourth commonCls1">
        DEVELOPER CONFIGURATIONS
        <c:if test="${appBo.appSequenceBo.developerConfigs}">
          <span class="sprites-icons-2 tick pull-right mt-xs"></span>
        </c:if>
      </li>
      <li class="fifth commonCls1">
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
    
    <c:if test="${appBo.appStatus == 'Draft' || appBo.appStatus == 'Deactivated'}">
	 $('.no-border').removeClass("right-border");
	</c:if>



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
    <c:if test="${appBo.appSequenceBo.appInfo || appBo.appStatus == 'Active' || appBo.appStatus == 'Deactivated'}">
    $('.second').click(function () {
      a.href = "/studybuilder/adminApps/viewAppSettings.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
   
    <c:if test="${appBo.appStatus == 'Active' || appBo.appStatus == 'Deactivated'}">
    $('.third').click(function () {
      a.href = "/studybuilder/adminApps/viewAppProperties.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    $('.fourth').click(function () {
      a.href = "/studybuilder/adminApps/viewDevConfigs.do?_S=${param._S}";
      document.body.appendChild(a).click();
    });
    </c:if>
    <c:if test="${(appBo.appSequenceBo.appSettings) || appBo.appStatus == 'Active' || appBo.appStatus == 'Deactivated'}">
    $('.fifth').click(function () {
        a.href = "/studybuilder/adminApps/appActionList.do?_S=${param._S}";
        document.body.appendChild(a).click();
      });
    </c:if>
    </c:if>
    
    <c:if test="${not appBo.appSequenceBo.appInfo && appBo.appStatus != 'Active'}">
    $('.commonCls').addClass('cursor-none-without-event');
    $('.commonCls1').addClass('cursor-none-without-event');
    </c:if>
    <c:if test="${appBo.appSequenceBo.appInfo && not appBo.appSequenceBo.appSettings && appBo.appStatus != 'Active'}">
    $('.commonCls').not('.second').addClass('cursor-none-without-event');
    $('.commonCls1').addClass('cursor-none-without-event');
    </c:if>
    <c:if test="${appBo.appSequenceBo.appSettings && appBo.appStatus != 'Active'}">
    $('.commonCls1').not('.fifth').addClass('cursor-none-without-event');
    </c:if> 
    
    <c:if test="${appBo.appStatus == 'Deactivated'}">
    $('.commonCls').removeClass('cursor-none-without-event');
    $('.commonCls1').removeClass('cursor-none-without-event');
    
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
