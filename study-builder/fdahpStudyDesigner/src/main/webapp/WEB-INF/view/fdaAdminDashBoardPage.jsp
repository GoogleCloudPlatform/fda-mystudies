<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.fdahpstudydesigner.util.SessionObject" %>
<!DOCTYPE html>
<html class="overflow-hidden" lang="">
  <head>

    <!-- Basic -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>Study Builder</title>

    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">

    <!-- Favicon -->
    <link rel="shortcut icon" href="/studybuilder/images/icons/FAV_Icon.png" type="image/x-icon"/>
    <link rel="apple-touch-icon" href="/studybuilder/images/icons/FAV_Icon.png">

    <!-- Mobile Metas -->
    <meta name="viewport"
          content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">

    <!-- Web Fonts  -->
    <link href="https://fonts.googleapis.com/css?family=Roboto:300,400" rel="stylesheet">

    <!-- Vendor CSS -->
    <link rel="stylesheet" href="/studybuilder/vendor/boostrap/bootstrap.min.css">

    <!-- Your custom styles (optional) -->
    <link href="/studybuilder/css/loader.css" rel="stylesheet">

    <link rel="stylesheet" href="/studybuilder/vendor/datatable/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="/studybuilder/vendor/dragula/dragula.min.css">
    <link rel="stylesheet" href="/studybuilder/vendor/magnific-popup/magnific-popup.css">
    <link rel="stylesheet" href="/studybuilder/vendor/animation/animate.css">

    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/studybuilder/css/sprites.css">
    <link rel="stylesheet" href="/studybuilder/css/layout.css">

    <!-- Theme CSS -->
    <link rel="stylesheet" href="/studybuilder/css/theme.css">
    <link rel="stylesheet" href="/studybuilder/css/style.css">

    <!-- Head Libs -->
    <script src="/studybuilder/vendor/modernizr/modernizr.js"></script>
  </head>
  <body class="loading background__img" onload="noBack();" onpageshow="if (event.persisted) noBack();"
        onunload="">
    <div id="loader">
      <span></span>
    </div>
    <form:form action="" name="studyListForm" id="studyListForm" method="post">
    </form:form>
    <c:url value="/j_spring_security_logout" var="logoutUrl"/>
    <form action="${logoutUrl}" method="post" id="logoutForm">
      <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>
    <div id="lg-container" class="lg-container">

      <div class="logout">
        <div class="dis-line pull-right ml-md line34">
          <a href="/studybuilder/sessionOut.do"
             class="blue-link text-weight-normal text-uppercase">
            <span class="white__text">sign Out</span>
          </a>
        </div>
      </div>
      <div class="logo__space">
        <img src="../images/logo/logo_landing_welcome.png" alt=""/>
      </div>
      <div class="landing__container">
        <!--container-->
        <div class="landing__content">
          <div class="manage-content-parent">
            <div class="lg-space-center">
              <div class="lg-space-txt">
                Study Builder
              </div>
              <div class="ll__border__bottom"></div>
              <div class="lg-space-title">
                <span>Welcome,</span>
                <span>${sessionObject.firstName}</span>
              </div>
              <div class='lg-icons'>
                <ul class="lg-icons-list">
                  <li class="studyListId">
                    <a class='' href='javascript:void(0)'>
                      <img class="mt-xlg" src="../images/icons/studies-w.png" alt="">
                    </a>
                    <div class='studyList'>Studies<br>
                      <span>&nbsp;</span>
                    </div>
                  </li>
                  <li class="linkDis hide">
                    <a class='repository-g' href='javascript:void(0)'></a>
                    <div>Repository</div>
                  </li>
                  <li class="notificationListId">
                    <a class='' href='javascript:void(0)'>
                      <img class="mt-xlg" src="../images/icons/notifications-w.png" alt="">
                    </a>
                    <div class='studyList'>Notifications<br>
                      <span>&nbsp;</span>
                    </div>
                  </li>
                  <li class="userListId">
                    <a class='' href='javascript:void(0)'>
                      <img class="mt-xlg" src="../images/icons/user-w.png" alt="">
                    </a>
                    <div>Users<br>
                      <span>&nbsp;</span>
                    </div>
                  </li>
                  <li class="myAccountId">
                    <a class='' href='javascript:void(0)'>
                      <img class="mt-xlg" src="../images/icons/account-w.png" alt="">
                    </a>
                    <div>My Account<br>
                      <span>&nbsp;</span>
                    </div>
                  </li>
                </ul>
              </div>
            </div>
            <div class="clearfix"></div>
          </div>
        </div>
        <div class="footer">
          <span>Copyright</span>
          <span>
            <a href="/studybuilder/terms.do" id="" target="_blank">Terms</a>
          </span>
          <span>
            <a href="/studybuilder/privacyPolicy.do" id="" target="_blank">Privacy Policy</a>
          </span>
        </div>
        <!--container-->
      </div>

    </div>

    <!-- Modal -->
    <div class="modal fade" id="termsModal" role="dialog">
      <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">

          <div class="modal-header cust-hdr">
            <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
          </div>
          <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
            <div>
              <div class="mt-md mb-md"><u><strong>Terms</strong></u></div>
              <span>${sessionObject.termsText}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal fade" id="privacyModal" role="dialog">
      <div class="modal-dialog modal-lg">
        <!-- Modal content-->
        <div class="modal-content">

          <div class="modal-header cust-hdr">
            <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
          </div>
          <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
            <div>
              <div class="mt-md mb-md"><u><strong>Privacy Policy</strong></u></div>
              <span>${sessionObject.privacyPolicyText}</span>
            </div>
          </div>
        </div>
      </div>
    </div>


    <!-- Vendor -->
    <script src="/studybuilder/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/studybuilder/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/studybuilder/vendor/animation/wow.min.js"></script>
    <script src="/studybuilder/vendor/datatable/js/jquery.dataTables.min.js"></script>
    <script src="/studybuilder/vendor/dragula/react-dragula.min.js"></script>
    <script src="/studybuilder/vendor/magnific-popup/jquery.magnific-popup.min.js"></script>
    <script src="/studybuilder/vendor/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="/studybuilder/js/jquery.mask.min.js"></script>

    <script type="text/javascript" src="/studybuilder/js/loader.js"></script>

    <!-- Theme Custom JS-->
    <script src="/studybuilder/js/theme.js"></script>
    <script src="/studybuilder/js/common.js"></script>

    <script>
      function formSubmit() {
        document.getElementById("logoutForm").submit();
      }

      $(document).ready(function (e) {

        $('#termsId').on('click', function () {
          $('#termsModal').modal('show');
        });

        $('#privacyId').on('click', function () {
          $('#privacyModal').modal('show');
        });

        <c:if test="${not fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_STUDIES')}">
        $(".studyListId").addClass('cursor-none');
        $(".studyListId").unbind();
        </c:if>
        <c:if test="${not fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_USERS_VIEW')}">
        $(".userListId").addClass('cursor-none');
        $(".userListId").unbind();
        </c:if>
        <c:if test="${not fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW')}">
        $(".notificationListId").addClass('cursor-none');
        $(".notificationListId").unbind();
        </c:if>

        $(".studyListId").click(function () {
          document.studyListForm.action = "/studybuilder/adminStudies/studyList.do";
          document.studyListForm.submit();
        });

        $(".userListId").click(function () {
          document.studyListForm.action = "/studybuilder/adminUsersView/getUserList.do";
          document.studyListForm.submit();
        });

        $(".notificationListId").click(function () {
          document.studyListForm.action = "/studybuilder/adminNotificationView/viewNotificationList.do";
          document.studyListForm.submit();
        });
        $(".myAccountId").click(function () {
          document.studyListForm.action = "/studybuilder/adminDashboard/viewUserDetails.do";
          document.studyListForm.submit();
        });
        if ('${sessionScope.sessionObject}' != '') {
          setTimeout(function () {
            window.location.href = '/studybuilder/errorRedirect.do?error=timeOut';
          }, 1000 * 60 * 31);
        }
      });
      <c:if test="${param.action eq 'landing'}">
      </c:if>
      window.history.forward();

      function noBack() {
        window.history.forward();
      }
    </script>
  </body>
</html>