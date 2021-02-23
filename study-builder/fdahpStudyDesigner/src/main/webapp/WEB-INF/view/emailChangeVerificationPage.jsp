<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
    <link rel="stylesheet" href="vendor/boostrap/bootstrap.min.css">
    <link rel="stylesheet" href="vendor/datatable/css/dataTables.bootstrap.min.css">

    <!-- Your custom styles (optional) -->
    <link href="/studybuilder/css/loader.css" rel="stylesheet">

    <link rel="stylesheet" href="vendor/dragula/dragula.min.css">
    <link rel="stylesheet" href="vendor/magnific-popup/magnific-popup.css">
    <link rel="stylesheet" href="vendor/animation/animate.css">

    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="css/layout.css">

    <!-- Theme CSS -->
    <link rel="stylesheet" href="css/theme.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/jquery-password-validator.css"></link>

    <!-- Head Libs -->
    <script src="vendor/modernizr/modernizr.js"></script>
    
    <style>
		.hover_text_white { color:#fff !important;}
		.hover_text_white:hover { color:#fff !important;}
		.hover_text_white:focus { color:#fff !important;}
	</style>

  </head>
  <body class="loading background__img">
    <div id="loader">
      <span></span>
    </div>
    <div id="lg-container" class="lg-container">
      <div class="logo__space">
        <img src="images/logo/logo_innerScreens.png" alt=""/>
      </div>

      <div class="login__container">
        <div class="">
          <input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}"
                 csrfToken="${_csrf.token}"/>
          <form:form id="accessCodeForm" data-toggle="validator" role="form"
                     action="validateEmailChangeVerification.do"
                     method="post" autocomplete="off">

            <div id="errMsg" class="error_msg">${errMsg}</div>
            <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
            <c:if test="${isValidToken}">
              <p class="white__text">Thank you. Your email verification process is completed. 
              Please use the new email address to sign in.</p>
              <div>
              <a id="login" class="gray-link backToLogin white__text hover_text_white" href="javascript:void(0)">Back to
                Sign in
              </a>
            </div>
            </c:if>
            <c:if test="${not isValidToken}">
              <jsp:forward page="errorPage.jsp" />
            </c:if>
            <input type="hidden" name="securityToken" value="${securityToken}"/>
          </form:form>
        </div>
      </div>
      <jsp:include page="../templates/copyright.jsp">
        <jsp:param name="footerClass" value="footer" />
      </jsp:include>

    </div>
    <!-- End Login Right Section-->
    <form:form action="/studybuilder/login.do" id="backToLoginForm" name="backToLoginForm"
               method="post">
    </form:form>

    <!-- Vendor -->
    <script src="vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="vendor/boostrap/bootstrap.min.js"></script>
    <script src="vendor/animation/wow.min.js"></script>
    <script src="vendor/datatable/js/jquery.dataTables.min.js"></script>
    <script src="vendor/dragula/react-dragula.min.js"></script>
    <script src="vendor/magnific-popup/jquery.magnific-popup.min.js"></script>
    <script src="vendor/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="js/validator.min.js"></script>
    <script src="/studybuilder/js/jquery.mask.min.js"></script>
    <script src="/studybuilder/js/jquery.password-validator.js"></script>
    <script type="text/javascript" src="/studybuilder/js/loader.js"></script>
    <script type="text/javascript" src="/studybuilder/js/jquery.password-validator.js"></script>
    <script src="/studybuilder/js/underscore-min.js"></script>

    <!-- Theme Custom JS-->
    <script src="/studybuilder/js/theme.js"></script>
    <script src="/studybuilder/js/common.js"></script>
    <!--common js-->


    <script>
      $(document).ready(function (e) {

        $('#termsId').on('click', function () {
          $('#termsModal').modal('show');
        });

        $('#privacyId').on('click', function () {
          $('#privacyModal').modal('show');
        });

        addPasswordPopup();
        $('.backToLogin').on('click', function () {
          $('#accessCodeForm').submit();
        });

        $('.backToLogin1').on('click', function () {
          $('#backToLoginForm').submit();
        });
        
        var errMsg = '${errMsg}';
        var isValidToken = '${isValidToken}';
        if (isValidToken) {
          if (errMsg.length > 0) {
            $("#errMsg").text(errMsg);
            $("#errMsg").show("fast");
            setTimeout(hideDisplayMessage, 5000);
          }
        }
        var sucMsg = '${sucMsg}';
        if (isValidToken) {
          if (sucMsg.length > 0) {
            $("#sucMsg").text(sucMsg);
            $("#sucMsg").show("fast");
            $("#errMsg").hide("fast");
            setTimeout(hideDisplayMessage, 5000);
          }
        }
        $("#password").passwordValidator({
          require: ['length', 'lower', 'upper', 'digit', 'spacial'],
          length: 8
        });

      });

      function hideDisplayMessage() {
        $('#sucMsg').hide();
        $('#errMsg').hide();
      }

      window.onload = function () {
        if (typeof history.pushState === "function") {
          history.pushState("jibberish", null, null);
          window.onpopstate = function () {
            history.pushState('newjibberish', null, null);
            // Handle the back (or forward) buttons here
            // Will NOT handle refresh, use onbeforeunload for this.
          };
        } else {
          var ignoreHashChange = true;
          window.onhashchange = function () {
            if (!ignoreHashChange) {
              ignoreHashChange = true;
              window.location.hash = Math.random();
              // Detect and redirect change here
              // Works in older FF and IE9
              // * it does mess with your hash symbol (anchor?) pound sign
              // delimiter on the end of the URL
            } else {
              ignoreHashChange = false;
            }
          };
        }
        $(document).find('.md-container.white-bg ')
            .removeClass('md-container');
      }

      var addPasswordPopup = function () {
        $("#password").passwordValidator({
          // list of qualities to require
          require: ['length', 'lower', 'upper', 'digit', 'spacial'],
          // minimum length requirement
          length: 8
        });
      }
    </script>

  </body>
</html>