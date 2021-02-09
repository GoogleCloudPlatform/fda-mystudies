<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html class="overflow-hidden" lang="ja">
  <head>
    <!-- Basic -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>Study Builder</title>

    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">

    <!-- Favicon -->
    <link rel="shortcut icon"
          href="/studybuilder/images/icons/FAV_Icon.png" type="image/x-icon"/>
    <link rel="apple-touch-icon"
          href="/studybuilder/images/icons/FAV_Icon.png">

    <!-- Mobile Metas -->
    <meta name="viewport"
          content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">

    <!-- Web Fonts  -->
    <link href="https://fonts.googleapis.com/css?family=Roboto:300,400"
          rel="stylesheet">

    <!-- Vendor CSS -->
    <link rel="stylesheet"
          href="/studybuilder/vendor/boostrap/bootstrap.min.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/datatable/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/datatable/css/jquery.dataTables.min.css">

    <!-- Your custom styles (optional) -->
    <link href="/studybuilder/css/loader.css" rel="stylesheet">

    <link rel="stylesheet"
          href="/studybuilder/vendor/datatable/css/rowReorder.dataTables.min.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/dragula/dragula.min.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/magnific-popup/magnific-popup.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/font-awesome/font-awesome.min.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/select2/bootstrap-select.min.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/select2/bootstrap-multiselect.css">
    <link rel="stylesheet"
          href="/studybuilder/vendor/animation/animate.css">

    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/studybuilder/css/layout.css">

    <!-- Theme CSS -->
    <link rel="stylesheet" href="/studybuilder/css/theme.css">
    <link rel="stylesheet" href="/studybuilder/css/style.css">
    <link rel="stylesheet"
          href="/studybuilder/css/jquery-password-validator.css"></link>
    <link rel="stylesheet" href="/studybuilder/css/sprites_icon.css">

    <!-- Head Libs -->
    <script src="/studybuilder/vendor/modernizr/modernizr.js"></script>

    <!-- Vendor -->
    <script src="/studybuilder/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/studybuilder/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/studybuilder/js/validator.min.js"></script>
    <script src="/studybuilder/vendor/animation/wow.min.js"></script>
    <script
        src="/studybuilder/vendor/datatable/js/jquery.dataTables.min.js"></script>
    <script
        src="/studybuilder/vendor/datatable/js/dataTables.rowReorder.min.js"></script>
    <script src="/studybuilder/vendor/dragula/react-dragula.min.js"></script>
    <script
        src="/studybuilder/vendor/magnific-popup/jquery.magnific-popup.min.js"></script>
    <script
        src="/studybuilder/vendor/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="/studybuilder/vendor/select2/bootstrap-select.min.js"></script>


    <script src="/studybuilder/js/jquery.password-validator.js"></script>

    <script src="/studybuilder/js/underscore-min.js"></script>
    <script type="text/javascript" src="/studybuilder/js/loader.js"></script>

  </head>
  <body class="loading background__img">
    <div id="loader">
      <span></span>
    </div>
    <div id="lg-container" class="lg-container">

      <div class="pt-xlg pl-xlg">
        <img src="images/logo/logo_landing_welcome.png" alt=""/>
      </div>
      <div class="signup__container">
        <!--container-->
        <div>
          <input type="hidden" id="csrfDet"
                 csrfParamName="${_csrf.parameterName}" csrfToken="${_csrf.token}"/>
          <div class=" col-xs-12" id="alignCenter">
            <!--lg-register-center  -->
            <form:form id="signUpForm" data-toggle="validator" role="form"
                       action="addPassword.do" method="post" autocomplete="off">

              <div id="errMsg" class="error_msg">${errMsg}</div>
              <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
              <c:if test="${isValidToken}">
                <p class="col-xs-12  text-center boxcenter mb-xlg white__text">
                  ${orgName} サービスのアカウント登録を完了するには、メールに記載されている
                  アクセスコードを使用して、パスワードを設定してください。</p>
                <div class=" col-md-6 boxcenter">
                <div class="col-xs-6">
                  <div class="mb-lg form-group">
                    <input type="text" class="input-field wow_input" id=""
                           name="lastName" placeholder="姓"
                           value="${fn:escapeXml(userBO.lastName)}" maxlength="50"
                           required autocomplete="off"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>

                <div class="col-xs-6">
                  <div class="mb-lg form-group">
                    <input type="text" class="input-field wow_input" id=""
                           name="firstName" placeholder="名"
                           value="${fn:escapeXml(userBO.firstName)}" maxlength="50"
                           required autocomplete="off"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>

                <div class="col-xs-6">
                  <div class="mb-lg form-group">
                    <input type="text"
                           class="input-field wow_input validateUserEmail"
                           name="userEmail" placeholder="メールアドレス"
                           value="${userBO.userEmail}" oldVal="${userBO.userEmail}"
                           pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$"
                           data-pattern-error="メールアドレスの入力が正しくありません。" maxlength="100"
                           required readonly="readonly" autocomplete="off"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-xs-6">
                  <div class="mb-lg form-group">
                    <input type="text" class="input-field wow_input phoneMask"
                           id="" name="phoneNumber" placeholder="電話番号（ハイフン付き）"
                           value="${userBO.phoneNumber}" data-minlength="12"
                           maxlength="12" required autocomplete="off"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-xs-12">
                  <div class="mb-lg form-group">
                    <input autofocus="autofocus" type="text"
                           class="input-field wow_input" id="" name="accessCode"
                           maxlength="6" placeholder="アクセスコード"
                           data-error="アクセスコードが正しくありません。" required
                           autocomplete="off"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-xs-6">
                  <div class="mb-lg form-group">
                    <input type="password" class="input-field wow_input"
                           id="password" maxlength="64" data-minlength="8"
                           placeholder="パスワード" required
                           pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~])[A-Za-z\d!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~]{8,64}"
                           autocomplete="off" data-error="半角英数字（大小）、記号を含めて入力して下さい。"/>
                    <div class="help-block with-errors red-txt"></div>
                    <span class="arrowLeftSugg"></span>

                  </div>
                </div>
                <div class="col-xs-6">
                  <div class="mb-lg form-group">
                    <input type="password" class="input-field wow_input"
                           id="cfnPassword" name="" maxlength="64"
                           data-match="#password"
                           data-match-error="パスワードの入力が一致しません。"
                           placeholder="パスワード（確認）" required autocomplete="off"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-xs-12">
                  <div class="mb-lg form-group">
                    <span class="checkbox checkbox-inline"><input
                        type="checkbox" id="inlineCheckbox" value="option1"
                        required="required"> <label for="inlineCheckbox">
                      <span class="white__text">次に定める
                        <a
                            href="/studybuilder/terms.do"
                            class="grey__text" target="_blank">規約
                        </a>
                        、および
                        <a
                            href="/studybuilder/privacyPolicy.do"
                            class="grey__text" target="_blank">個人情報保護方針
                        </a>
                        に同意します。
                      </span>
                    </label>
                    </span>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="clearfix"></div>
                <div
                    class="mb-lg form-group text-center col-md-4 col-lg-4 boxcenter">
                  <button type="button" class="btn lg-btn" id="signPasswordBut">登録</button>
                </div>
              </c:if>
              <c:if test="${not isValidToken}">
                <p class="passwordExp text-center">
                  <i class="fa fa-exclamation-circle" aria-hidden="true"></i>
                  パスワードリセットのリンクの有効期限が切れています。
                </p>
              </c:if>
              </div>
              <input type="hidden" name="securityToken" value="${securityToken}"/>
              <input type="password" name="password" id="hidePass"
                     style="display: none;"/>
            </form:form>
          </div>
          <!--container-->
          <jsp:include page="../templates/copyright.jsp">
           <jsp:param name="footerClass" value="footer" />
          </jsp:include>
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
                <div class="mt-md mb-md">
                  <u><strong>規約</strong></u>
                </div>
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
                <div class="mt-md mb-md">
                  <u><strong>個人情報保護方針</strong></u>
                </div>
                <span>${masterDataBO.privacyPolicyText}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <form:form action="/studybuilder/login.do" id="backToLoginForm"
                 name="backToLoginForm" method="post">
      </form:form>

      <script src="/studybuilder/js/theme.js"></script>
      <script src="/studybuilder/js/jquery.mask.min.js"></script>
      <script src="/studybuilder/js/common.js"></script>
      <script src="/studybuilder/js/jquery.nicescroll.min.js"></script>
      <script src="/studybuilder/vendor/tinymce/tinymce.min.js"></script>
      <script src="/studybuilder/js/bootbox.min.js"></script>


      <script>
        $(document).ready(function (e) {

          var w = $(window).height();
          var b = $("#alignCenter").innerHeight();
          var a = (w - b) / 2;
          $("#alignCenter").css("margin-top", a);

          $('.terms').on('click', function () {
            $('#termsModal').modal('show');
          });

          $('.privacy').on('click', function () {
            $('#privacyModal').modal('show');
          });

          addPasswordPopup();
          $('.backToLogin').on('click', function () {
            $('#backToLoginForm').submit();
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