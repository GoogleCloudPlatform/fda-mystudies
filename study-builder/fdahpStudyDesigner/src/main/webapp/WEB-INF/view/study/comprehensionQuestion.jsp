<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<div class="col-sm-10 col-rc white-bg p-none">
  <!--  Start top tab section-->
  <form:form
      action="/studybuilder/adminStudies/saveOrUpdateComprehensionTestQuestion.do?_S=${param._S}&${_csrf.parameterName}=${_csrf.token}"
      name="comprehensionFormId" id="comprehensionFormId" method="post" role="form">
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f dis-line pull-left line34">
          <span class="pr-sm cur-pointer"
                onclick="goToBackPage(this);"><img
              src="../images/icons/back-b.png" alt=""/></span>
          <c:if test="${empty comprehensionQuestionBo.id}">Add comprehension test question</c:if>
          <c:if
              test="${not empty comprehensionQuestionBo.id && actionPage eq 'addEdit'}">Edit comprehension test question</c:if>
          <c:if
              test="${not empty comprehensionQuestionBo.id && actionPage eq 'view'}">View comprehension test question<c:set
              var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}
          </c:if>
        </div>
        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn" onclick="goToBackPage(this);">
            Cancel
          </button>
        </div>
        <div class="dis-line form-group mb-none mr-sm ">
          <button type="button" class="btn btn-default gray-btn TestQuestionButtonHide" id="saveId">
            Save
          </button>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button" class="btn btn-primary blue-btn TestQuestionButtonHide" id="doneId">
            Done
          </button>
        </div>
      </div>
    </div>
    <!-- End top tab section-->
    <!-- Start body tab section -->
    <div class="right-content-body pt-none pb-none">
      <input type="hidden" id="id" name="id" value="${comprehensionQuestionBo.id}">
      <c:if test="${not empty comprehensionQuestionBo.id}">
        <input type="hidden" id="studyId" name="studyId" value="${comprehensionQuestionBo.studyId}">
      </c:if>
      <c:if test="${empty comprehensionQuestionBo.id}">
        <input type="hidden" id="studyId" name="studyId" value="${studyId}">
      </c:if>
      <div>
        <div class="gray-xs-f mb-xs mt-md">Question (300 characters max)
          <span
              class="requiredStar">*
          </span>
        </div>
        <div class="form-group">
          <input type="text" class="form-control" name="questionText" id="questionText" required data-error="Please fill out this field"
                 value="${comprehensionQuestionBo.questionText}" maxlength="300"/>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <!-- Answer option section-->
      <div class="col-md-11 col-lg-12 p-none">
        <!-- Bending Answer options -->
        <div class="unitDivParent">
          <c:if test="${fn:length(comprehensionQuestionBo.responseList) eq 0}">
            <div class="col-md-12 p-none">
              <div class='col-md-6 pl-none'>
                <div class="gray-xs-f mb-xs">Answer options (150 characters max)
                  <span
                      class="requiredStar">*
                  </span>
                </div>
              </div>
              <div class='col-md-3'>
                <div class="gray-xs-f mb-xs">Is this a 'correct' answer?
                  <span class="requiredStar">*</span>
                  <span>
                    <span
                      data-toggle="tooltip" data-placement="top"
                      title="Choose Yes if the answer option qualifies as one of the 'correct' answers to the question."
                      class="filled-tooltip"></span>
                  </span> 
                </div>
              </div>

              <div class="col-md-3">
                <div class="gray-xs-f mb-xs">&nbsp;</div>
              </div>
              <div class="clearfix"></div>
            </div>
            <div class="ans-opts col-md-12 p-none" id="0">
              <div class='col-md-6 pl-none'>
                <div class='form-group'>
                  <input type='text' class='form-control responseOptionClass'
                         name="responseList[0].responseOption" id="responseOptionId0" required data-error="Please fill out this field"
                         maxlength="150" onblur="validateForUniqueValue(this,function(){});"
                         onkeypress="resetValue(this);"/>
                  <div class='help-block with-errors red-txt'></div>
                </div>
              </div>
              <div class='col-md-3'>
                <div class="form-group">
                  <select class='selectpicker wid100' name="responseList[0].correctAnswer"
                          id="correctAnswerId0" required data-error='Please choose one option'>
                    <option value=''>Select</option>
                    <option value="true">Yes</option>
                    <option value="false">No</option>
                  </select>
                  <div class='help-block with-errors red-txt'></div>
                </div>
              </div>
              <div class="col-md-3 pl-none">
                <div class="clearfix"></div>
                <div class="mt-xs formgroup">
                  <span class="addBtnDis addbtn mr-sm align-span-center"
                        onclick='addAns();'>+
                  </span>
                  <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center"
                        onclick='removeAns(this);'></span>
                </div>
              </div>
            </div>
            <div class="ans-opts col-md-12 p-none" id="1">
              <div class='col-md-6 pl-none'>
                <div class='form-group'>
                  <input type='text' class='form-control' name="responseList[1].responseOption"
                         id="responseOptionId1" required maxlength="150" data-error="Please fill out this field"
                         onblur="validateForUniqueValue(this,function(){});"
                         onkeypress="resetValue(this);"/>
                  <div class='help-block with-errors red-txt'></div>
                </div>
              </div>
              <div class='col-md-3'>
                <div class="form-group">
                  <select class='selectpicker wid100' name="responseList[1].correctAnswer"
                          id="correctAnswerId1" required data-error='Please choose one option'>
                    <option value=''>Select</option>
                    <option value="true">Yes</option>
                    <option value="false">No</option>
                  </select>
                  <div class='help-block with-errors red-txt'></div>
                </div>
              </div>
              <div class="col-md-3 pl-none">
                <div class="clearfix"></div>
                <div class="mt-xs formgroup">
                  <span class="addBtnDis addbtn mr-sm align-span-center"
                        onclick='addAns();'>+
                  </span>
                  <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center"
                        onclick='removeAns(this);'></span>
                </div>
              </div>
            </div>
          </c:if>
          <c:if test="${fn:length(comprehensionQuestionBo.responseList) gt 0}">
            <div class="col-md-12 p-none">
              <div class='col-md-6 pl-none'>
                <div class="gray-xs-f mb-xs">Answer options (150 characters max)
                  <span
                      class="requiredStar">*
                  </span>
                </div>
              </div>
              <div class='col-md-2'>
                <div class="gray-xs-f mb-xs">Correct Answer
                  <span class="requiredStar">*</span>
                </div>
              </div>

              <div class="col-md-4">
                <div class="gray-xs-f mb-xs">&nbsp;</div>
              </div>
              <div class="clearfix"></div>
            </div>
            <c:forEach items="${comprehensionQuestionBo.responseList}" var="responseBo"
                       varStatus="responseBoVar">
              <div class="ans-opts col-md-12 p-none" id="${responseBoVar.index}">
                <div class='col-md-6 pl-none'>
                  <div class='form-group'>
                    <input type='text' class='form-control'
                           name="responseList[${responseBoVar.index}].responseOption"
                           id="responseOptionId${responseBoVar.index}"
                           value="${responseBo.responseOption}" required maxlength="150"
                           onblur="validateForUniqueValue(this,function(){});"
                           onkeypress="resetValue(this);"/>
                    <div class='help-block with-errors red-txt'></div>
                  </div>
                </div>
                <div class='col-md-3'>
                  <div class="form-group">
                    <select class='selectpicker wid100' required data-error="Please fill out this field"
                            data-error='Please choose one option'
                            name="responseList[${responseBoVar.index}].correctAnswer"
                            id="correctAnswerId${responseBoVar.index}">
                      <option value=''>Select</option>
                      <option value="true" ${responseBo.correctAnswer ? 'selected':''}>Yes
                      </option>
                      <option value="false" ${responseBo.correctAnswer eq false ? 'selected':''}>
                        No
                      </option>
                    </select>
                    <div class='help-block with-errors red-txt'></div>
                  </div>
                </div>
                <div class="col-md-3 pl-none">
                  <div class="clearfix"></div>
                  <div class="mt-xs formgroup">
                    <span class="addBtnDis study-addbtn ml-none" onclick='addAns();'>+</span>
                    <span
                        class="delete vertical-align-middle remBtnDis hide pl-md align-span-center"
                        onclick='removeAns(this);'></span>
                  </div>
                </div>
              </div>
            </c:forEach>
          </c:if>
        </div>
      </div>
      <div class="clearfix"></div>

      <div>
      <div class="gray-xs-f mb-sm">Consider the question to be correctly answered if the app user responds with
          <span
              class="requiredStar">*
          </span>
        </div>
        <div class="form-group">
          <span class="radio radio-info radio-inline p-45">
            <input type="radio" id="inlineRadio1" value="false"
                   name="structureOfCorrectAns" ${!comprehensionQuestionBo.structureOfCorrectAns ? 'checked' : ''}>
             <label for="inlineRadio1">any of the answer options marked 'correct' above</label>
          </span>
          <span class="radio radio-inline p-45">
            <input type="radio" id="inlineRadio2" value="true"
                   name="structureOfCorrectAns" ${empty comprehensionQuestionBo.structureOfCorrectAns || comprehensionQuestionBo.structureOfCorrectAns ? 'checked' : ''}>
            <label for="inlineRadio2">all the answer options marked 'correct' above</label>
          </span>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
    </div>
  </form:form>
  <!--  End body tab section -->
</div>
<!-- End right Content here -->
<script type="text/javascript">
  $(document).ready(function () {
	$('.studyClass').addClass("active");
	$(".menuNav li").removeClass('active');
    $(".fifthComre").addClass('active');
    <c:if test="${actionPage eq 'view'}">
    $('#comprehensionFormId input,textarea,select').prop('disabled', true);
    $('.TestQuestionButtonHide').hide();
    $('.addBtnDis, .remBtnDis').addClass('dis-none');
    </c:if>
    $("#doneId").on("click", function () {
      if (isFromValid("#comprehensionFormId") && validateCorrectAnswers()) {
        validateForUniqueValue('', function (val) {
          if (val) {
            $("#comprehensionFormId").submit();
          }
        });
      }
    });
    $("#saveId").on("click", function () {
      $(".right-content-body").parents("form").validator("destroy");
      $(".right-content-body").parents("form").validator();
      saveComrehensionTestQuestion();
    });
    
    if ($('.ans-opts').length > 2) {
      $(".remBtnDis").removeClass("hide");
      $(".remBtnDis").css("pointer-events", "auto");
    } else {
      $(".remBtnDis").addClass("hide");
      $(".remBtnDis").css("pointer-events", "none");
    }
    if (${actionPage == 'view'}) {
  	  $('.ans-opts').find(".remBtnDis").css("pointer-events", "none");
    }
  });
  var ansCount = $(".ans-opts").length;

  function addAns() {
    ansCount = parseInt(ansCount) + 1;
    var newAns = "<div class='ans-opts col-md-12 p-none' id='" + ansCount
        + "'><div class='col-md-6 pl-none'>"
        + "<div class='form-group'>"
        + "<input type='text' class='form-control' required data-error='Please fill out this field' name='responseList[" + ansCount
        + "].responseOption' id='responseOptionId" + ansCount
        + "'  maxlength='150' onblur='validateForUniqueValue(this,function(){});' onkeypress='resetValue(this);'/>"
        + "<div class='help-block with-errors red-txt'></div>"
        + "</div>"
        + "</div>"
        + "<div class='col-md-3'><div class='form-group'>"
        + "<select class='selectpicker' required data-error='Please choose one option' name='responseList["
        + ansCount + "].correctAnswer' id='correctAnswerId" + ansCount + "'>"
        + "<option value=''>Select</option>"
        + "<option value='true'>Yes</option>"
        + "<option value='false'>No</option>"
        + "</select>"
        + "<div class='help-block with-errors red-txt'></div>"
        + "</div>"
        + "</div>"
        + "<div class='col-md-3 pl-none'>"
        + "	<div class='clearfix'></div>"
        + "	<div class='mt-xs form-group'> "
        + "		<span id='ans-btn' class='addBtnDis addbtn mr-sm align-span-center' onclick='addAns();'>+</span>"
        + "		<span class='delete vertical-align-middle remBtnDis hide pl-md align-span-center' onclick='removeAns(this);'></span>"
        + "    </div> "
        + "</div>"
        + " </div>"
        + "</div></div>";
    $(".ans-opts:last").after(newAns);
    $(".ans-opts").parents("form").validator("destroy");
    $(".ans-opts").parents("form").validator();
    if ($('.ans-opts').length > 1) {
      $(".remBtnDis").removeClass("hide");

    } else {
      $('.unitDivParent').find(".remBtnDis").addClass("hide");

    }
    $('.selectpicker').selectpicker('refresh');
    $('#' + ansCount).find('input:first').focus();
    if ($('.ans-opts').length > 2) {
        $(".remBtnDis").removeClass("hide");
        $(".remBtnDis").css("pointer-events", "auto");
    } else {
        $(".remBtnDis").addClass("hide");
        $(".remBtnDis").css("pointer-events", "none");
    }
  }

  function removeAns(param) {
    $(param).parents(".ans-opts").remove();
    $(".ans-opts").parents("form").validator("destroy");
    $(".ans-opts").parents("form").validator();
    
    if ($('.ans-opts').length > 2) {
        $(".remBtnDis").removeClass("hide");
        $(".remBtnDis").css("pointer-events", "auto");
    } else {
        $(".remBtnDis").addClass("hide");
        $(".remBtnDis").css("pointer-events", "none");
    }
  }

  function goToBackPage(item) {
    <c:if test="${actionPage ne 'view'}">
    $(item).prop('disabled', true);
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
          a.href = "/studybuilder/adminStudies/comprehensionQuestionList.do?_S=${param._S}";
          document.body.appendChild(a).click();
        } else {
          $(item).prop('disabled', false);
        }
      }
    });
    </c:if>
    <c:if test="${actionPage eq 'view'}">
    var a = document.createElement('a');
    a.href = "/studybuilder/adminStudies/comprehensionQuestionList.do?_S=${param._S}";
    document.body.appendChild(a).click();
    </c:if>
  }

  function saveComrehensionTestQuestion() {
    var comprehensionTestQuestion = new Object();
    var testQuestionId = $("#id").val();
    var studyId = $("#studyId").val();
    var questiontext = $("#questionText").val();
    var structureOfCorrectTxt = $('input[name="structureOfCorrectAns"]:checked').val();
    var questionResponseArray = new Array();
    $('.ans-opts').each(function () {
      var testQuestionResponse = new Object();
      var id = $(this).attr("id");
      var responseOption = $("#responseOptionId" + id).val();
      var correctAnswer = $("#correctAnswerId" + id).val();
      testQuestionResponse.responseOption = responseOption;
      testQuestionResponse.correctAnswer = correctAnswer;
      testQuestionResponse.comprehensionTestQuestionId = testQuestionId;

      questionResponseArray.push(testQuestionResponse);
    });
    comprehensionTestQuestion.id = testQuestionId;
    comprehensionTestQuestion.studyId = studyId;
    comprehensionTestQuestion.questionText = questiontext;
    comprehensionTestQuestion.structureOfCorrectAns = structureOfCorrectTxt;
    comprehensionTestQuestion.responseList = questionResponseArray;
    var formData = new FormData();
    if (studyId != null && studyId != '' && typeof studyId != 'undefined' &&
        questiontext != null && questiontext != '' && typeof questiontext != 'undefined') {
      formData.append("comprehenstionQuestionInfo", JSON.stringify(comprehensionTestQuestion));
      $.ajax({
        url: "/studybuilder/adminStudies/saveComprehensionTestQuestion.do?_S=${param._S}",
        type: "POST",
        datatype: "json",
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function (xhr, settings) {
          xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
        },
        success: function (data) {
          var message = data.message;
          if (message == "SUCCESS") {
            var questionId = data.questionId;
            $("#id").val(questionId);
            $("#alertMsg").removeClass('e-box').addClass('s-box').text("Content saved as draft");
            $('#alertMsg').show();
          } else {
            var errMsg = data.errMsg;
            if (errMsg != '' && errMsg != null && typeof errMsg != 'undefined') {
              $("#alertMsg").removeClass('s-box').addClass('e-box').text(errMsg);
            } else {
              $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
            }
          }
          setTimeout(hideDisplayMessage, 5000);
        },
        error: function (xhr, status, error) {
          $(item).prop('disabled', false);
          $('#alertMsg').show();
          $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
          setTimeout(hideDisplayMessage, 5000);
        }
      });
    } else {
      $('#questionText').validator('destroy').validator();
      if (!$('#questionText')[0].checkValidity()) {
        $("#questionText").parent().addClass('has-error has-danger').find(
            ".help-block").empty().append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
            "This is a required field"));
      }
    }
  }

  function validateCorrectAnswers() {
    var questionResponseArray = new Array();
    $('.ans-opts').each(function () {
      var id = $(this).attr("id");
      var correctAnswer = $("#correctAnswerId" + id).val();
      questionResponseArray.push(correctAnswer);
    });
    if (questionResponseArray.indexOf("true") != -1) {
      return true;
    } else {
      $('#alertMsg').show();
      $("#alertMsg").removeClass('s-box').addClass('e-box').text(
          "Please mark at least one of the answer options as the correct answer.");
      setTimeout(hideDisplayMessage, 5000);
      return false;
    }
  }

  function validateForUniqueValue(item, callback) {
    var isValid = true;
    var valueArray = new Array();
    $('.ans-opts').each(function () {
      var id = $(this).attr("id");
      var diaplay_value = $("#responseOptionId" + id).val();
      $("#responseOptionId" + id).parent().removeClass("has-danger").removeClass("has-error");
      $("#responseOptionId" + id).parent().find(".help-block").empty();
      if (diaplay_value != '') {
        if (valueArray.indexOf(diaplay_value.toLowerCase()) != -1) {
          isValid = false;

          $("#responseOptionId" + id).parent().addClass("has-danger").addClass("has-error");
          $("#responseOptionId" + id).parent().find(".help-block").empty();
          $("#responseOptionId" + id).parent().find(".help-block").append(
        	 $("<ul><li> </li></ul>").attr("class","list-unstyled").text("The value should be unique "));
        } else
          valueArray.push(diaplay_value.toLowerCase());
      } else {
        $("#responseOptionId" + id).parent().addClass("has-danger").addClass("has-error");
        $("#responseOptionId" + id).parent().find(".help-block").empty();
      }

    });
    callback(isValid);
  }

  function resetValue(item) {
    $(item).parent().addClass("has-danger").addClass("has-error");
    $(item).parent().find(".help-block").empty();
  }

  $(document).on('mouseenter', '.dropdown-toggle',  function () {
      $(this).removeAttr("title");
  });
</script>
