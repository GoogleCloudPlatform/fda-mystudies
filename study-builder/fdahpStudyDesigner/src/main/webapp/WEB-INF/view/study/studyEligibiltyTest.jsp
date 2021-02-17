<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<div class="col-sm-10 col-rc white-bg p-none">
  <!--  Start top tab section-->
  <form:form
      action="/studybuilder/adminStudies/saveOrUpdateStudyEligibiltyTestQusAns.do?_S=${param._S}"
      name="studyEligibiltyTestFormId" id="studyEligibiltyTestFormId" method="post"
      data-toggle="validator"
      role="form">
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          <span
              class="mr-xs cur-pointer"
              onclick="goToBackPage(this);"><img
              src="../images/icons/back-b.png" alt=""/></span>
          <c:if test="${actionTypeForQuestionPage == 'edit'}">適格性テストの編集</c:if>
          <c:if test="${actionTypeForQuestionPage == 'view'}">適格性テスト詳細 <c:set
              var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}
          </c:if>
          <c:if test="${actionTypeForQuestionPage == 'add'}">適格性テストの追加</c:if>
        </div>
        <input type="hidden" value="${actionTypeForQuestionPage}" name="actionTypeForQuestionPage">
        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn" onclick="goToBackPage(this);">
            キャンセル
          </button>
        </div>
        <c:if test="${actionTypeForQuestionPage ne 'view'}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn" id="saveId">下書き保存</button>
          </div>
          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn" id="doneId">完了</button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->
    <!-- Start body tab section -->
    <div class="right-content-body">
      <!-- form- input-->
      <input type="hidden" id="type" name="type" value="complete"/>
      <input type="hidden" name="id" value="${eligibilityTest.id}"/>
      <input type="hidden" id="eligibilityId" name="eligibilityId" value="${eligibilityId}"/>
      <input type="hidden" id="sequenceNo" name="sequenceNo" value="${eligibilityTest.sequenceNo}"/>
      <div class=" col-lg-4 col-md-5 pl-none">
        <div class="gray-xs-f mb-xs">タイトル (1 文字以上 15 文字以下)
          <span
              class="requiredStar"> *
          </span>
          <span
              class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
              title="This must be a human-readable activity identifier and unique across all activities of the study.Note that this field cannot be edited once the study is Launched."></span>
        </div>
        <div class="form-group">
          <input autofocus="autofocus" type="text" custAttType="cust"
                 class="form-control ${eligibilityTest.used ? 'cursor-none-disabled-event' : ''}"
                 name="shortTitle" id="shortTitleId"
                 value="${fn:escapeXml(eligibilityTest.shortTitle)}"
                 required="required"
                 maxlength="15" ${eligibilityTest.used ? 'readonly' : ''} />
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <div class="clearfix"></div>
      <div class="gray-xs-f mb-xs">質問内容 (1 文字以上 250 文字以下)
        <span
            class="requiredStar"> *
        </span>
      </div>
      <div class="form-group">
        <input type="text" class="form-control" required name="question" id="question"
               value="${fn:escapeXml(eligibilityTest.question)}" maxlength="250"/>
        <div class="help-block with-errors red-txt"></div>
      </div>
      <div class="clearfix"></div>
      <div class="col-lg-5 col-md-5 p-none">
        <div class="form-group col-md-12 p-none mr-md mb-none">
          <div class="gray-xs-f mb-xs col-md-6 pl-none ">回答の形式</div>
          <div class="gray-xs-f mb-xs col-md-6 pr-none">正解 / 不正解
            <span
                class="requiredStar"> *
            </span>
          </div>
        </div>
        <div class="col-md-12 p-none mr-md mb-none">
          <div class="col-md-6 pl-none">
            <input type="text" class="form-control" name="tentativeDuration" value="Yes" disabled/>
          </div>
          <div class="form-group col-md-6 pr-none">
            <select class="selectpicker elaborateClass" required title="Select"
                    name="responseYesOption"
                    id="resYesOptId" onchange="chkValidChoosedOption()">
              <option value="true" ${eligibilityTest.responseYesOption ? 'selected':''}>正解
              </option>
              <option
                  value="false" ${not empty eligibilityTest.responseYesOption && not eligibilityTest.responseYesOption ? 'selected':''}>
                不正解
              </option>
            </select>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
        <div class="col-md-12 p-none mr-md mb-none">
          <div class="col-md-6 pl-none ">
            <input type="text" class="form-control" name="tentativeDuration" value="No" disabled/>
          </div>
          <div class="form-group col-md-6 pr-none">
            <select class="selectpicker elaborateClass form-control" required title="Select"
                    name="responseNoOption" id="resNoOptId" onchange="chkValidChoosedOption()">
              <option value="true" ${eligibilityTest.responseNoOption ? 'selected':''} >
                正解
              </option>
              <option value="false" ${not empty eligibilityTest.responseNoOption && not eligibilityTest.responseNoOption ? 'selected':''}>
                不正解
              </option>
            </select>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
      </div>
      <div class="clearfix"></div>
    </div>
  </form:form>
  <!--  End body tab section -->
</div>
<!-- End right Content here -->
<script type="text/javascript">
  var isValid = false;
  var oldShortTitle = "${fn:escapeXml(eligibilityTest.shortTitle)}";
  $(document).ready(function () {

    $(".menuNav li.active").removeClass('active');
    $(".menuNav li.fourth").addClass('active');

    <c:if test="${actionTypeForQuestionPage eq 'view'}">
    $('#studyEligibiltyTestFormId input,textarea,select').prop('disabled', true);
    $('#studyEligibiltyTestFormId').find('.elaborateClass').addClass('linkDis');
    </c:if>

    $("#shortTitleId").blur(function () {
      if ($(this).val() != oldShortTitle)
        validateShortTitle(this, function (val) {

        });
    });
    $('[data-toggle="tooltip"]').tooltip();
    $("#doneId").click(function () {
      $(this).prop("disabled", true);
      validateShortTitle("#shortTitleId", function (val) {
        if (val) {
          $('#shortTitleId').prop('disabled', false);
          if (isFromValid("#studyEligibiltyTestFormId") && chkValidChoosedOption()) {
            document.studyEligibiltyTestFormId.submit();
          } else {
            $("#doneId").prop("disabled", false);
          }
        } else {
          $("#doneId").prop("disabled", false);
        }
      });
    });
    $("#saveId").click(function () {
      $(this).prop("disabled", true);
      validateShortTitle("#shortTitleId", function (val) {
        if (val) {
          if (chkValidChoosedOption()) {
            $('#studyEligibiltyTestFormId').validator('destroy');
            $('#type').val('save');
            $('#studyEligibiltyTestFormId').submit();
          } else {
            $('#saveId').prop("disabled", false);
          }
        } else {
          if ($('#shortTitleId').val()) {
            $('#shortTitleId').parent().addClass('has-error has-danger').find(
                ".help-block").empty().append(
                $("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                "This is a required field."));
          }
          $('#saveId').prop("disabled", false);
          return false;
        }
      });
    });
  });

  function validateShortTitle(item, callback) {
    var thisAttr = item;
    var shortTitle = $("#shortTitleId").val();
    if (!$('#shortTitleId').is('[readonly]')) {
      if (shortTitle) {
        $('#shortTitleId').prop('disabled', true);
        $.ajax({
          url: "/studybuilder/adminStudies/validateEligibilityTestKey.do?_S=${param._S}",
          type: "POST",
          datatype: "json",
          data: {
            shortTitle: shortTitle,
            eligibilityTestId: '${eligibilityTest.id}',
            eligibilityId: '${eligibilityId}'
          },
          beforeSend: function (xhr, settings) {
            xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
          },
          success: function (data) {
            var message = data.message;
            $('#shortTitleId').prop('disabled', false);
            if ('SUCCESS' == message) {
              $(thisAttr).validator('validate');
              $(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
              $(thisAttr).parent().find(".help-block").empty();
              oldShortTitle = shortTitle;
              callback(true);
            } else {
              $(thisAttr).val('');
              $(thisAttr).parent().addClass("has-danger").addClass("has-error");
              $(thisAttr).parent().find(".help-block").empty();
              $(thisAttr).parent().find(".help-block").append(
            	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                  shortTitle + " は既に登録済みです。"));
              callback(false);
            }
          },
          error: function () {
            $('#shortTitleId').prop('disabled', false);
          },
          global: false
        });
      } else {
        callback(false);
      }
    } else {
      callback(true);
    }
  }

  function goToBackPage(item) {
    $(item).prop('disabled', true);
    <c:if test="${actionTypeForQuestionPage ne 'view'}">
    bootbox.confirm({
      closeButton: false,
      message: '未保存の質問があります。保存をしないと編集内容が削除されてしまいます。本当にこのページを離れますか？',
      buttons: {
        'cancel': {
          label: 'キャンセル',
        },
        'confirm': {
          label: 'はい',
        },
      },
      callback: function (result) {
        if (result) {
          var a = document.createElement('a');
          a.href = "/studybuilder/adminStudies/viewStudyEligibilty.do?_S=${param._S}";
          document.body.appendChild(a).click();
        } else {
          $(item).prop('disabled', false);
        }
      }
    });
    </c:if>
    <c:if test="${actionTypeForQuestionPage eq 'view'}">
    var a = document.createElement('a');
    a.href = "/studybuilder/adminStudies/viewStudyEligibilty.do?_S=${param._S}";
    document.body.appendChild(a).click();
    </c:if>
  }

  var chkValidChoosedOption = function () {
    let resYesOptVal = $('#resYesOptId').val();
    let resNoOptVal = $('#resNoOptId').val();

    if (resYesOptVal == 'false' && resNoOptVal == 'false') {
      showErrMsg("２つの回答が不正解にすることはできません。");
      $("#resYesOptId").parents(".form-group").addClass("has-error has-danger");
      $("#resNoOptId").parents(".form-group").addClass("has-error has-danger");
      return false;
    } else {
      $("#resYesOptId").parents(".form-group").removeClass("has-error has-danger");
      $("#resNoOptId").parents(".form-group").removeClass("has-error has-danger");
      return true;
    }

  }
</script>