<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html class="overflow-hidden" lang="en">
  <head>
    <!-- Basic -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="refresh" content="1700">
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
    <link rel="stylesheet" href="/studybuilder/vendor/datatable/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="/studybuilder/vendor/datatable/css/jquery.dataTables.min.css">

    <!-- Your custom styles (optional) -->
    <link href="/studybuilder/css/loader.css" rel="stylesheet">

    <link rel="stylesheet" href="/studybuilder/vendor/datatable/css/rowReorder.dataTables.min.css">
    <link rel="stylesheet" href="/studybuilder/vendor/dragula/dragula.min.css">
    <link rel="stylesheet" href="/studybuilder/vendor/magnific-popup/magnific-popup.css">
    <link rel="stylesheet" href="/studybuilder/vendor/font-awesome/font-awesome.min.css">
    <link rel="stylesheet" href="/studybuilder/vendor/select2/bootstrap-select.min.css">
    <link rel="stylesheet" href="/studybuilder/vendor/animation/animate.css">

    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/studybuilder/css/layout.css">

    <!-- Theme CSS -->
    <link rel="stylesheet" href="/studybuilder/css/theme.css">
    <link rel="stylesheet" href="/studybuilder/css/style.css">
    <link rel="stylesheet" href="/studybuilder/css/sprites_icon.css">

    <!-- Head Libs -->
    <script src="/studybuilder/vendor/modernizr/modernizr.js"></script>

    <!-- Vendor -->
    <script src="/studybuilder/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/studybuilder/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/studybuilder/js/validator.min.js"></script>
    <script src="/studybuilder/vendor/animation/wow.min.js"></script>
    <script src="/studybuilder/vendor/datatable/js/jquery.dataTables.min.js"></script>
    <script src="/studybuilder/vendor/datatable/js/dataTables.rowReorder.min.js"></script>
    <script src="/studybuilder/vendor/dragula/react-dragula.min.js"></script>
    <script src="/studybuilder/vendor/magnific-popup/jquery.magnific-popup.min.js"></script>
    <script src="/studybuilder/vendor/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="/studybuilder/vendor/select2/bootstrap-select.min.js"></script>
    <script type="text/javascript" src="/studybuilder/js/loader.js"></script>

  </head>
  <body class="loading background__img" onload="noBack();" onpageshow="if (event.persisted) noBack();"
        onunload="">
    <div id="loader">
      <span></span>
    </div>
    <div id="lg-container" class="lg-container">


      <!-- new login -->
      <!-- Logo-->
      <div class="logo__space">
        <img src="images/logo/logo_landing_welcome.png" alt=""/>
      </div>
      <div class="clearfix"></div>
      <div class="login__container">


        <div class="login-box">
          <div class="lg-space-txt">
            Study Builder
          </div>
          <div class="ll__border__bottom"></div>
          <c:url value='/j_spring_security_check' var="fdaLink"/>
          <input type="hidden" id="fdaLink" value="${fdaLink}">
          <form:form id="loginForm" data-toggle="validator" role="form" action="#" name="loginForm"
                     method="post"
                     autocomplete="off">
            <div id="errMsg" class="error_msg">${errMsg}</div>
            <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
            <div class="login pt-xlg">
              <div class="mb-lg form-group">
                <input type="text" class="input-field wow_input" id="email" name="username"
                       data-pattern-error="Email address is invalid"
                       placeholder="Email address" required maxlength="100"
                       data-error="Please complete this field"
                       pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$" autofocus
                       autocomplete="off">
                <div class="help-block with-errors red-txt"></div>
              </div>
              <div class="mb-lg form-group">
                <input type="password" class="input-field wow_input" id="password"
                       placeholder="Password" required maxlength="64"
                       data-error="Please complete this field"
                       autocomplete="off" readonly onfocus="$(this).removeAttr('readonly');">
                <div class="help-block with-errors red-txt"></div>
              </div>
              <div class="mb-lg form-group">
                <button type="button" id="loginBtnId" class="btn lg-btn">SIGN IN</button>
              </div>
              <div class="pb-md pt-xs">
                <a id="forgot_pwd"
                   class="gray-link white__text"
                   href="javascript:void(0)">Forgot password?
                </a>
              </div>
            </div>
            <input type="password" name="password" id="hidePass" style="display: none;"/>
          </form:form>
          <form:form id="forgotForm" data-toggle="validator" role="form" action="forgotPassword.do"
                     method="post"
                     autocomplete="off">
            <div class="pwd dis-none">
              <div class="mb-lg">
                <h3 style="    color: #fff; padding-top: 20px;" class="mt-none">Forgot password?</h3>
                <div class="mt-md white__text">Enter your Email address to get a link to reset your
                  password
                </div>
              </div>
              <div class="mb-lg form-group">
                <input type="text" class="input-field wow_input" id="emailReg" name="email"
                       maxlength="100"
                       placeholder="Email address"
                       data-pattern-error="Email address is invalid" required maxlength="100"
                       pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$">
                <div class="help-block with-errors red-txt"></div>
              </div>
              <div class="mb-lg">
                <button type="submit" class="btn lg-btn" id="log-btn">SUBMIT</button>
              </div>
              <div class="pt-xs">
                <a id="login" class="gray-link white__text" href="javascript:void(0)">Back to sign in
                </a>
              </div>
            </div>
          </form:form>
        </div>
        <div class="clearfix"></div>
      </div>
      <div class="clearfix"></div>
      <div class="footer">
        <div>
          <span>Copyright</span>
          <span>
            <a href="/studybuilder/terms.do" class=""
               target="_blank">Terms
            </a>
          </span>
          <span>
            <a
                href="/studybuilder/privacyPolicy.do" class="" target="_blank">Privacy Policy
            </a>
          </span>
        </div>
      </div>
      <!-- new login -->
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
              <span>${masterDataBO.termsText}</span>
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
              <span>${masterDataBO.privacyPolicyText}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}"
           csrfToken="${_csrf.token}"/>
    <script src="/studybuilder/js/theme.js"></script>
    <script src="/studybuilder/js/jquery.mask.min.js"></script>
    <script src="/studybuilder/js/common.js"></script>
    <script src="/studybuilder/js/jquery.nicescroll.min.js"></script>
    <script src="/studybuilder/vendor/tinymce/tinymce.min.js"></script>
    <script src="/studybuilder/js/bootbox.min.js"></script>
    <script src="/studybuilder/js/autofill-event.js"></script>
    <script src="/studybuilder/js/ajaxRequestInterceptor.js"></script>

    <script>
      var isChanged = true;
      $(document).ready(function (e) {
        // Internet Explorer 6-11
        var isIE = false || !!document.documentMode;

        // Edge 20+
        var isEdge = !isIE && !!window.StyleMedia;
        if (isIE || isEdge) {
          $('#password').prop('readonly', false);
        }
        $.ajaxSetup({
          beforeSend: function (xhr, settings) {
            xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
          }
        });
        $('#siginNoteBtnId').click(function () {
          $('#password').removeAttr('readonly');
          if (isFromValid($(this).parents('form'))) {
            $(".askSignInCls").removeClass('hide');
          }
        });
        $('#loginForm').keypress(function (e) {
          $('#password').removeAttr('readonly');
          if (e.which == 13) {
            if (isFromValid($("#loginForm"))) {
              e.target.blur();
              $('#loginBtnId').click();
            }
          }
        });
        $("#cancelbtn").click(function () {
          $(".cs-model-box").addClass('hide');
        });
        $('.termsCls').on('click', function () {
          $('#termsModal').modal('show');
        });

        $('.privacyCls').on('click', function () {
          $('#privacyModal').modal('show');
        });

        var errMsg = '${errMsg}';
        if (errMsg.length > 0) {
          $("#errMsg").text(errMsg);
          $("#errMsg").show("fast");

          setTimeout(hideDisplayMessage, 4000);
        }
        var sucMsg = '${sucMsg}';
        if (sucMsg.length > 0) {
          $("#sucMsg").text(sucMsg);
          $("#sucMsg").show("fast");
          $("#errMsg").hide("fast");
          setTimeout(hideDisplayMessage, 4000);
        }

        // Internet Explorer 6-11
        var isIE = false || !!document.documentMode;
        // Edge 20+
        var isEdge = !isIE && !!window.StyleMedia;

        $('#email').keyup(function (event) {
          event = (event || window.event);
          if (event.keyCode == 13) {
            var isEmail = false;
            var emailAdd = $('#email').val();
            var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
            isEmail = regEX.test(emailAdd);
            if (emailAdd == '') {
              if (isIE || isEdge) {
                $('#email').parent().find(".help-block").empty().append(
                        $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Please complete this field"));
              } else {
                $('#email').parent().find(".help-block").empty().append(
                        $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Please complete this field"));
              }
            } else if (!isEmail) {
              $('#email').parent().find(".help-block").empty().append(
            		  $("<ul><li> </li></ul>").attr("class","list-unstyled").text("Email address is invalid"));
            }
          }
        });

        var wh = $(window).height();
        $('.cs-model-box>div:first-child').css('height', wh);

      });

      function hideDisplayMessage() {
        $('#sucMsg').hide();
        $('#errMsg').hide();
      }

      window.history.forward();

      function noBack() {
        window.history.forward();
      }
    </script>

  </body>
</html>