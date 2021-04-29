<%@page import="com.fdahpstudydesigner.util.SessionObject" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:url value="/j_spring_security_logout" var="logoutUrl"/>


<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none white-bg hd_con">
  <div class="md-container">
    <!-- Navigation Menu-->
    <nav class="navbar navbar-inverse">
      <div class=" display__flex__center justyfy__space__between ">
        <div class="navbar-header display__flex__center mr-lg">
          <button type="button" class="navbar-toggle" data-toggle="collapse"
                  data-target="#myNavbar">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand pt-none pb-none display__flex__center p-none pr-lg"
             href="javascript:void(0)"
             id="landingScreen"><img src="/studybuilder/images/logo/logo_innerScreens.png"/></a>
          <ul class="nav navbar-nav ml-none">
            <c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_STUDIES')}">
              <li class="studyClass">
                <a href="javascript:void(0)" id="studySection">Studies</a>
              </li>
            </c:if>
            <c:if
                test="${fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW')}">
              <li id="notification" class="">
                <a href="javascript:void(0)"
                   id="manageNotificationSection">Notifications
                </a>
              </li>
            </c:if>
            <c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_USERS_VIEW')}">
              <li id="users">
                <a href="javascript:void(0)" id="usersSection">Admins</a>
              </li>
            </c:if>
          </ul>
        </div>
        <div class="collapse navbar-collapse p-none" id="myNavbar">


          <ul class="nav navbar-nav navbar-right">
            <li id="myAccount" class="dropdown ml-lg userLi">
              <a class="dropdown-toggle blue-link" data-toggle="dropdown"
                 href="javascript:void(0)">${sessionObject.firstName} ${sessionObject.lastName}
                &nbsp;<i
                    class="fa fa-angle-down" aria-hidden="true"></i>
              </a>
              <ul class="dropdown-menu pb-none pt-none profileBox">

                <li class="linkProf">
                  <a href="javascript:void(0)"
                     class="blue-link text-weight-normal text-uppercase"
                     id="profileSection">My Account
                  </a>
                  <hr align="left" width="100%">
                  <a href="/studybuilder/sessionOut.do"
                     class="blue-link text-weight-normal text-uppercase">
                    <span>sign Out</span>
                    <span
                        class="ml-xs"><img src="/studybuilder/images/icons/logout.png"/></span>
                  </a>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>
    </nav>

  </div>
</div>

<form action="${logoutUrl}" method="post" id="logoutForm">
  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" id="logoutCsrf"/>
</form>
<form:form action="/studybuilder/adminUsersView/getUserList.do" id="userListForm"
           name="userListForm" method="post">
</form:form>
<form:form action="/studybuilder/adminNotificationView/viewNotificationList.do"
           id="manageNotificationForm"
           name="manageNotificationForm" method="post">
</form:form>
<form:form action="/studybuilder/adminDashboard/viewUserDetails.do" id="myAccountForm"
           name="myAccountForm"
           method="post">
</form:form>
<form:form action="/studybuilder/adminStudies/studyList.do" id="adminStudyDashForm"
           name="adminStudyDashForm"
           method="post">
</form:form>
<form:form action="/studybuilder/adminDashboard/viewDashBoard.do" id="landingPageForm"
           name="landingPageForm"
           method="post">
</form:form>
<script type="text/javascript">
  $(document).ready(function () {
    var a = document.createElement('a');
    $('#usersSection').on('click', function () {
      a.href = "/studybuilder/adminUsersView/getUserList.do";
      document.body.appendChild(a).click();
    });

    $('#manageNotificationSection').on('click', function () {
      a.href = "/studybuilder/adminNotificationView/viewNotificationList.do";
      document.body.appendChild(a).click();
    });

    $('#profileSection').on('click', function () {
      a.href = "/studybuilder/adminDashboard/viewUserDetails.do";
      document.body.appendChild(a).click();
    });

    $('#studySection').on('click', function () {
      a.href = "/studybuilder/adminStudies/studyList.do";
      document.body.appendChild(a).click();
    });

    $('#landingScreen').on('click', function () {
      a.href = "/studybuilder/adminDashboard/viewDashBoard.do";
      document.body.appendChild(a).click();
    });

  });

  function formSubmit() {
    document.getElementById("logoutForm").submit();
  }
</script>