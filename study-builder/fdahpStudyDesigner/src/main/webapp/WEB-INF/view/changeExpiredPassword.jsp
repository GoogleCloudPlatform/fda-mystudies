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

    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/studybuilder/css/sprites.css">
    <link rel="stylesheet" href="/studybuilder/css/layout.css">

    <!-- Theme CSS -->
    <link rel="stylesheet" href="/studybuilder/css/theme.css">
    <link rel="stylesheet" href="/studybuilder/css/jquery-password-validator.css"></link>
    <link rel="stylesheet" href="/studybuilder/css/style.css">

    <!-- Head Libs -->
    <script src="/studybuilder/vendor/modernizr/modernizr.js"></script>


    <!-- Vendor -->
    <script src="/studybuilder/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/studybuilder/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/studybuilder/js/validator.min.js"></script>
    <script src="/studybuilder/vendor/animation/wow.min.js"></script>
    <script src="/studybuilder/vendor/select2/bootstrap-select.min.js"></script>

    <script src="/studybuilder/js/jquery.password-validator.js"></script>

    <script src="/studybuilder/js/underscore-min.js"></script>
    <script src="/studybuilder/js/ajaxRequestInterceptor.js"></script>
    <script type="text/javascript" src="/studybuilder/js/loader.js"></script>
    <style type="text/css">
      .has-error .checkbox, .has-error .checkbox-inline, .has-error .control-label, .has-error .help-block, .has-error .radio, .has-error .radio-inline, .has-error.checkbox label, .has-error.checkbox-inline label, .has-error.radio label, .has-error.radio-inline label {
        color: #fff !important;
      }
      .arrowLeftSugg {
        top: 153px; !important;
      }
    </style>
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
      <div class="logo__space">
      	<a href="/studybuilder/sessionOut.do"
             class="blue-link text-weight-normal text-uppercase">
        	<img src="../images/logo/logo_landing_welcome.png" alt=""/>
        </a>
      </div>
      <div class="pwdexp__container">
        <!--container-->
        <div>
          <!-- change password box-->
          <input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}"
                 csrfToken="${_csrf.token}"/>
          <form:form id="passwordResetForm" data-toggle="validator" role="form"
                     action="/studybuilder/changePassword.do" method="post" autocomplete="off">
            <div>
              <div id="errMsg" class="error_msg">${errMsg}</div>
              <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
              <p class="white__text">Your password has expired. Please create a new password.</p>
              <div class="mb-lg form-group">
                <input type="password" class="form-control input-field wow_input" id="oldPassword"
                       name=""
                       maxlength="14" data-minlength="8" placeholder="Previous password"
                       data-error="Invalid previous password" required data-error="Please fill out this field"
                       autocomplete="off"/>
                <div class="help-block with-errors"></div>
                <input type="hidden" name="oldPassword" id="hideOldPass" data-error="Please fill out this field"/>
              </div>
              <div class="mb-lg form-group">
                <input type="password" class="form-control input-field wow_input" id="password" name=""
                       maxlength="14" data-minlength="8" placeholder="New password"
                       data-error="New password is invalid" data-error="Please fill out this field"
                       required
                       pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~])[A-Za-z\d!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~]{8,14}"
                       autocomplete="off"/>
                <div class="help-block with-errors"></div>
                <span class="arrowLeftSugg"></span>
              </div>
              <div class="mb-lg form-group">
                <input type="password" class="form-control input-field wow_input" id="cfnPassword"
                       name=""
                       maxlength="14" data-match="#password"
                       data-match-error="Passwords do not match" data-error="Please fill out this field"
                       placeholder="Confirm new password"
                       required autocomplete="off"/>
                <div class="help-block with-errors"></div>
              </div>
              <div class="mb-lg form-group">
                <button type="button" class="btn lg-btn" id="resetPasswordBut">Submit</button>
              </div>
            </div>
            <input type="password" name="newPassword" id="hidePass" style="display: none;"/>
          </form:form>
          <!-- change password box ends-->
        </div>
        <!--container-->
        <jsp:include page="../templates/copyright.jsp">
          <jsp:param name="footerClass" value="footer" />
        </jsp:include>
      
      </div>
    </div>
    <script src="/studybuilder/js/theme.js"></script>
    <script src="/studybuilder/js/common.js"></script>
    <script>
      $(document).ready(function (e) {
        addPasswordPopup();
        var errMsg = '${errMsg}';
        if (errMsg.length > 0) {
          $("#errMsg").text(errMsg);
          $("#errMsg").show("fast");
          setTimeout(hideDisplayMessage, 5000);
        }
      });

      var addPasswordPopup = function () {
        $("#password").passwordValidator({
          require: ['length', 'lower', 'upper', 'digit', 'spacial'],
          length: 8
        });
      }

      function formSubmit() {
        document.getElementById("logoutForm").submit();
      }

      window.history.forward();

      function noBack() {
        window.history.forward();
      }

      function hideDisplayMessage() {
        $('#errMsg').hide();
      }
    </script>
  </body>
</html>