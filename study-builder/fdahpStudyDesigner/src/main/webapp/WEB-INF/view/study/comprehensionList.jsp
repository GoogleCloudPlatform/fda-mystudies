<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<style>
  .tool-tip {
    display: inline-block;
  }

  .tool-tip [disabled] {
    pointer-events: none;
  }
</style>
<script type="text/javascript">
  function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
      return false;
    }
    return true;
  }

  var sucMsg = '${sucMsg}';
  if (sucMsg.length > 0) {
    showSucMsg(sucMsg);
  }

	function showSucMsg(message) {
	  $("#alertMsg").removeClass('e-box').addClass('s-box').text(message);
	  $('#alertMsg').show('5000');
	  if('${param.buttonText}' == 'completed'){
	  window.setTimeout(function(){
		window.location.href = "/studybuilder/adminStudies/consentReview.do?_S=${param._S}";
		    }, 5000);
	  }else{
	    	setTimeout(hideDisplayMessage, 5000);
	  }
	}
</script>
<!-- Start right Content here -->
<!-- ============================================================== -->
<form:form action="/studybuilder/adminStudies/consentReview.do?_S=${param._S}"
           name="comprehensionInfoForm"
           id="comprehensionInfoForm" method="post">
  <div class="col-sm-10 col-rc white-bg p-none">
    <!--  Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">COMPREHENSION TEST</div>
        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
        </div>
        <c:if test="${empty permission}">
          <div class="dis-line form-group mb-none mr-sm TestQuestionButtonHide">
            <button type="button" class="btn btn-default gray-btn" id="saveId">Save</button>
          </div>
          <div class="dis-line form-group mb-none">
            <span class="tool-tip" data-toggle="tooltip" data-placement="bottom" id="helpNote"
                <c:if
                    test="${!markAsComplete && consentBo.needComprehensionTest eq 'Yes'}"> title="Please ensure individual list items on this page are marked Done before attempting to mark this section as Complete." </c:if> >
              <button type="button" class="btn btn-primary blue-btn" id="markAsCompleteBtnId"
                      <c:if
                          test="${!markAsComplete && consentBo.needComprehensionTest eq 'Yes'}">disabled</c:if>
                      onclick="markAsCompleted();">Mark as completed
              </button>
            </span>
          </div>
        </c:if>
      </div>
    </div>
    <!--  End  top tab section-->
    <div class="right-content-head">
      <div class="mb-xlg" id="displayTitleId">
        <div class="gray-xs-f mb-xs">Add a comprehension test?
          <span>
            <span
               data-toggle="tooltip" data-placement="top"
               title="This will present a set of questions to the app user to gauge their understanding of the study based on their review of the consent sections."
               class="filled-tooltip"></span>
            </span>
          <span class="ct_panel"
                id="addHelpNote">
          </span>
        </div>
        <div class="form-group col-md-5 p-none">
          <span class="radio radio-info radio-inline p-45">
            <input type="radio" id="comprehensionTestYes" value="Yes"
                   name="needComprehensionTest" ${consentBo.needComprehensionTest eq 'Yes' ? 'checked' : ''}>
            <label for="comprehensionTestYes">Yes</label>
          </span>
          <span class="radio radio-inline">
            <input type="radio" id="comprehensionTestNo" value="No"
                   name="needComprehensionTest" ${empty consentBo.needComprehensionTest || consentBo.needComprehensionTest eq 'No' ? 'checked' : ''}>
            <label for="comprehensionTestNo">No</label>
          </span>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
    </div>
    <!--  Start body tab section -->
    <div
        class="pt-none pb-none<c:if test="${empty consentBo.needComprehensionTest || consentBo.needComprehensionTest eq 'No'}">ct_panel</c:if>"
        id="mainContainer">
        <div class="right-content-head">
        <div class="text-right">
          <div class="black-md-f dis-line pull-left line34">Comprehension test questions
          </div>
          <div class="dis-line form-group mb-none">
            <c:if test="${empty permission}">
            <span id="spanAddQaId" class="tool-tip" data-toggle="tooltip"
                  data-placement="bottom" data-original-title="">
              <button type="button" class="btn btn-primary blue-btn"
                      id="addQuestionId"
                            onclick="addComphernsionQuestionPage();">+ Add question
              </button>
              </span>
            </c:if>
          </div>
        </div>
      </div>
      <div class="right-content-body">
        <table id="comprehension_list" class="display bor-none" cellspacing="0" width="100%">
          <thead>
            <tr>
              <th id="">
                <span class="marL10">#</span>
              </th>
              <th id="">Questions</th>
              <th id="">Actions</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach items="${comprehensionTestQuestionList}" var="comprehensionTestQuestion">
              <tr id="${comprehensionTestQuestion.id}">
                <td>${comprehensionTestQuestion.sequenceNo}</td>
                <td>
                  <div class="dis-ellipsis">${fn:escapeXml(comprehensionTestQuestion.questionText)}</div>
                </td>
                <td>
                  <span class="sprites_icon preview-g mr-lg" data-toggle="tooltip"
                        data-placement="top"
                        title="View"
                        onclick="viewComprehensionQuestion(${comprehensionTestQuestion.id});"></span>
                  <span
                      class="${comprehensionTestQuestion.status?'edit-inc':'edit-inc-draft mr-md'} mr-lg <c:if test="${not empty permission}"> cursor-none </c:if>"
                      data-toggle="tooltip" data-placement="top" title="Edit"
                      onclick="editComprehensionQuestion(${comprehensionTestQuestion.id});"></span>
                  <span
                      class="sprites_icon copy delete <c:if test="${not empty permission}"> cursor-none </c:if>"
                      data-toggle="tooltip" data-placement="top" title="Delete"
                      onclick="deleteComprehensionQuestion(${comprehensionTestQuestion.id});"></span>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>

      <div class="right-content-body mt-xlg" id="displayTitleId">
        <div class="gray-xs-f mb-xs" id="minScoreText">Minimum score needed to pass the test</div>
        <div class="form-group col-md-5 p-none scoreClass">
          <input type="text" id="comprehensionTestMinimumScore" class="form-control"
                 name="comprehensionTestMinimumScore"
                 value="${consentBo.comprehensionTestMinimumScore}"
                 maxlength="3" onkeypress="return isNumber(event)"  Style="width:250px">
          <div class="help-block with-errors red-txt"></div>
        </div>
        <input type="hidden" name="consentId" id="consentId" value="${consentBo.id}"/>
      </div>
    </div>
    <!--  End body tab section -->
  </div>
  <input type="hidden" name="studyId" id="studyId" value="${studyId}"/>
  <c:if test="${not empty consentBo.id}">
    <input type="hidden" id="studyId" name="studyId" value="${consentBo.studyId}">
  </c:if>
  <c:if test="${empty consentBo.id}">
    <input type="hidden" id="studyId" name="studyId" value="${studyId}">
  </c:if>
</form:form>
<form:form action="/studybuilder/adminStudies/comprehensionQuestionPage.do?_S=${param._S}"
           name="comprehenstionQuestionForm" id="comprehenstionQuestionForm" method="post">
  <input type="hidden" name="comprehensionQuestionId" id="comprehensionQuestionId" value="">
  <input type="hidden" name="actionType" id="actionType">
  <input type="hidden" name="studyId" id="studyId" value="${studyId}"/>
</form:form>
<!-- End right Content here -->
<script type="text/javascript">
var markAsComplete = "${markAsComplete}"
  $(document).ready(function () {
	var mainContainerDivision = document.getElementById("comprehensionTestNo").checked;
   if(mainContainerDivision==true){
		var mainContainerDivision = $('#mainContainer').hide();		   
	 }
	$('.studyClass').addClass("active");
    $(".menuNav li").removeClass('active');
    $(".fifthComre").addClass('active');
    $("#createStudyId").show();
    <c:if test="${permission eq 'view'}">
    $('#comprehensionInfoForm input,textarea,select').prop('disabled', true);
    $('.TestQuestionButtonHide').hide();
    $('.addBtnDis, .remBtnDis').addClass('dis-none');
    </c:if>
    $('input[name="needComprehensionTest"]').change(function () {
      var val = $(this).val();
      $("#addQuestionId").attr("disabled", true);
      if (val == "Yes") {
    	$("#saveId").html("Next");
        $("#comprehensionTestMinimumScore, #minScoreText").hide();
        $('#spanAddQaId').attr('data-original-title', 'Please click on Next to start adding questions');
        $("#mainContainer").show();
        if ($('#comprehension_list tbody tr').length == 1
                && $('#comprehension_list tbody tr td').length == 1) {
       $("#markAsCompleteBtnId").attr("disabled", true);
       $('#helpNote').attr('data-original-title','Please add 1 or more questions to the test');
        }
     
        if (markAsComplete == "false") {
          $("#markAsCompleteBtnId").attr("disabled", true);
          $("#helpNote").attr('data-original-title',
              'Please ensure individual list items on this page are marked Done before attempting to mark this section as Complete.');
          $('[data-toggle="tooltip"]').tooltip();
        }
        if (document.getElementById("addQuestionId") != null && document.getElementById(
            "addQuestionId").disabled) {
          $("#addHelpNote").show();
        }
      } else {
    	$("#saveId").html("Save");
        $("#comprehensionTestMinimumScore").val('');
        $("#mainContainer").hide();
        $("#addHelpNote").hide();
        if (document.getElementById("markAsCompleteBtnId") != null && document.getElementById(
            "markAsCompleteBtnId").disabled) {
          $("#markAsCompleteBtnId").attr("disabled", false);
          $("#helpNote").attr('data-original-title', '');
        }
      }
    });
    var needComprehensionTestTxt = $('input[name="needComprehensionTest"]:checked').val();
    if (needComprehensionTestTxt == "Yes" && ${comprehensionTestQuestionList.size()} == 0) {
        $("#markAsCompleteBtnId").attr("disabled", true);
        $('#helpNote').attr('data-original-title','Please add 1 or more questions to the test');
    }
    var viewPermission = "${permission}";

    var reorder = true;
    if (viewPermission == 'view') {
      reorder = false;
    } else {
      reorder = true;
    }
    var table1 = $('#comprehension_list').DataTable({
      "paging": false,
      "info": false,
      "filter": false,
      language: {
        "zeroRecords": "No content created yet",
      },
      rowReorder: reorder,
      "columnDefs": [{orderable: false, targets: [0, 1]}],
      "fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        if (viewPermission != 'view') {
          $('td:eq(0)', nRow).addClass("cursonMove dd_icon");
        }
      }
    });
    table1.on('row-reorder', function (e, diff, edit) {
      var oldOrderNumber = '', newOrderNumber = '';
      var result = 'Reorder started on row: ' + edit.triggerRow.data()[1] + '<br>';
      var studyId = $("#studyId").val();
      for (var i = 0, ien = diff.length; i < ien; i++) {
        var rowData = table1.row(diff[i].node).data();
        var r1;
        if (i == 0) {
          r1 = rowData[0];
        }
        if (i == 1) {
          if (r1 > rowData[0]) {
            oldOrderNumber = diff[0].oldData;
            newOrderNumber = diff[0].newData;
          } else {
            oldOrderNumber = diff[diff.length - 1].oldData;
            newOrderNumber = diff[diff.length - 1].newData;
          }

        }
        result += rowData[1] + ' updated to be in position ' +
            diff[i].newData + ' (was ' + diff[i].oldData + ')<br>';
      }

      if (oldOrderNumber !== undefined && oldOrderNumber != null && oldOrderNumber != ""
          && newOrderNumber !== undefined && newOrderNumber != null && newOrderNumber != "") {
        $.ajax({
          url: "/studybuilder/adminStudies/reOrderComprehensionTestQuestion.do?_S=${param._S}",
          type: "POST",
          datatype: "json",
          data: {
            studyId: studyId,
            oldOrderNumber: oldOrderNumber,
            newOrderNumber: newOrderNumber,
            "${_csrf.parameterName}": "${_csrf.token}",
          },
          success: function consentInfo(data) {
            var status = data.message;
            if (status == "SUCCESS") {
              $('#alertMsg').show();
              $("#alertMsg").removeClass('e-box').addClass('s-box').text(
                  "Content items reordered");
              if ($('.fifthComre').find('span').hasClass('sprites-icons-2 tick pull-right mt-xs')) {
                $('.fifthComre').find('span').removeClass('sprites-icons-2 tick pull-right mt-xs');
              }
            } else {
              $('#alertMsg').show();
              $("#alertMsg").removeClass('s-box').addClass('e-box').text(
                  "Unable to reorder consent");
            }
            setTimeout(hideDisplayMessage, 5000);
          },
          error: function (xhr, status, error) {
            $("#alertMsg").removeClass('s-box').addClass('e-box').text(error);
            setTimeout(hideDisplayMessage, 5000);
          }
        });
      }
    });
    $("#comprehensionTestMinimumScore").keyup(function () {
      $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
    });
    $("#comprehensionTestMinimumScore").blur(function () {
      $("#comprehensionTestMinimumScore").parent().removeClass("has-danger").removeClass(
          "has-error");
      $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
      var value = $(this).val();
      var questionCount = $("#comprehension_list").find("tbody").find("tr").length;
      if (value != '' && value != null && (value == 0 || parseInt(value) > parseInt(
          questionCount))) {

        $("#comprehensionTestMinimumScore").parent().addClass("has-danger").addClass("has-error");
        $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
        $("#comprehensionTestMinimumScore").parent().find(".help-block").append(
        	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
            "The score should be less than or equal to the number of questions and greater than 0"));
      } else {
        $("#comprehensionTestMinimumScore").parent().removeClass("has-danger").removeClass(
            "has-error");
        $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
      }
    });
    $("#saveId").click(function () {
      $("#comprehensionTestMinimumScore").trigger('blur');
      $("#comprehensionTestMinimumScore").parents("form").validator("destroy");
      $("#comprehensionTestMinimumScore").parents("form").validator();
      $("#comprehensionTestMinimumScore").parent().removeClass("has-danger").removeClass(
          "has-error");
      $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
      saveConsent('save');
    });
    if (document.getElementById("markAsCompleteBtnId") != null && document.getElementById(
        "markAsCompleteBtnId").disabled) {
      $('[data-toggle="tooltip"]').tooltip();
    }
  });

  function deleteComprehensionQuestion(questionId) {
    bootbox.confirm("Are you sure you want to delete this question?", function (result) {
      if (result) {
        var studyId = $("#studyId").val();
        if (questionId != '' && questionId != null && typeof questionId != 'undefined') {
          $.ajax({
            url: "/studybuilder/adminStudies/deleteComprehensionQuestion.do?_S=${param._S}",
            type: "POST",
            datatype: "json",
            data: {
              comprehensionQuestionId: questionId,
              studyId: studyId,
              "${_csrf.parameterName}": "${_csrf.token}",
            },
            success: function deleteConsentInfo(data) {
              var status = data.message;
              if (status == "SUCCESS") {
                $("#alertMsg").removeClass('e-box').addClass('s-box').text(
                    "Question deleted successfully");
                $('#alertMsg').show();
                reloadData(studyId);
              } else {
                $("#alertMsg").removeClass('s-box').addClass('e-box').text(
                    "Unable to delete Question");
                $('#alertMsg').show();
              }
              setTimeout(hideDisplayMessage, 5000);
            },
            error: function (xhr, status, error) {
              $("#alertMsg").removeClass('s-box').addClass('e-box').text(error);
              setTimeout(hideDisplayMessage, 5000);
            }
          });
        }
      }
    });
  }

  function reloadData(studyId) {
    $.ajax({
      url: "/studybuilder/adminStudies/reloadComprehensionQuestionListPage.do?_S=${param._S}",
      type: "POST",
      datatype: "json",
      data: {
        studyId: studyId,
        "${_csrf.parameterName}": "${_csrf.token}",
      },
      success: function status(data, status) {
        var message = data.message;
        if (message == "SUCCESS") {
          reloadComprehensionQuestionDataTable(data.comprehensionTestQuestionList);
          if ($('#comprehension_list tbody tr').length == 1
                  && $('#comprehension_list tbody tr td').length == 1) {
        	  $("#markAsCompleteBtnId").attr("disabled", true);
              $('#helpNote').attr('data-original-title','Please add 1 or more questions to the test');
          }else if(markAsComplete == "true" ){
        	        $("#markAsCompleteBtnId").attr("disabled", false);
        	        $('#helpNote').removeAttr('data-original-title');
        	      }
        }
      },
      error: function status(data, status) {

      },
    });
  }

  function addComphernsionQuestionPage() {
    $("#comprehensionQuestionId").val('');
    $("#actionType").val('addEdit');
    $("#comprehenstionQuestionForm").submit();
  }

  function editComprehensionQuestion(testQuestionId) {
    if (testQuestionId != null && testQuestionId != '' && typeof testQuestionId != 'undefined') {
      $("#comprehensionQuestionId").val(testQuestionId);
      $("#actionType").val('addEdit');
      $("#comprehenstionQuestionForm").submit();
    }
  }

  function viewComprehensionQuestion(testQuestionId) {
    if (testQuestionId != null && testQuestionId != '' && typeof testQuestionId != 'undefined') {
      $("#comprehensionQuestionId").val(testQuestionId);
      $("#actionType").val('view');
      $("#comprehenstionQuestionForm").submit();
    }
  }

  function reloadComprehensionQuestionDataTable(comprehensionTestQuestionList) {
	  markAsComplete="true";
    $('#comprehension_list').DataTable().clear();
    if (typeof comprehensionTestQuestionList != 'undefined' && comprehensionTestQuestionList != null
        && comprehensionTestQuestionList.length > 0) {
      $.each(comprehensionTestQuestionList, function (i, obj) {
        var datarow = [];
        if (typeof obj.sequenceNo === "undefined" && typeof obj.sequenceNo === "undefined") {
          datarow.push(' ');
        } else {
          datarow.push(obj.sequenceNo);
        }
        if (typeof obj.questionText === "undefined" && typeof obj.questionText === "undefined") {
          datarow.push(' ');
        } else {
          datarow.push("<div class='dis-ellipsis'>" + DOMPurify.sanitize(obj.questionText) + "</div>");
        }
        var actions='';
        var objStatus=(typeof obj.status ? 'edit-inc' : 'edit-inc-draft mr-md');
        if( obj.status===true){
         actions = "<span class='sprites_icon preview-g mr-lg' data-toggle='tooltip' data-placement='top' title='View' onclick='viewComprehensionQuestion("
            + parseInt(obj.id) + ");'></span>"
            + "<span class='sprites_icon mr-lg edit-inc' data-toggle='tooltip' data-placement='top' title='Edit' onclick='editComprehensionQuestion(" + parseInt(obj.id)
            + ");'>"
            + "</span><span class='sprites_icon copy delete' data-toggle='tooltip' data-placement='top' title='Delete' onclick='deleteComprehensionQuestion("
            + parseInt(obj.id) + ");'>"
            + "</span>";
        }else{
        	    actions = "<span class='sprites_icon preview-g mr-lg' data-toggle='tooltip' data-placement='top' title='View' onclick='viewComprehensionQuestion("
                   + parseInt(obj.id) + ");'></span>"
                   + "<span class='sprites_icon mr-lg edit-inc-draft mr-md' data-toggle='tooltip' data-placement='top' title='Edit' onclick='editComprehensionQuestion(" + parseInt(obj.id)
                   + ");'>"
                   + "</span><span class='sprites_icon copy delete' data-toggle='tooltip' data-placement='top' title='Delete' onclick='deleteComprehensionQuestion("
                   + parseInt(obj.id) + ");'>"
                   + "</span>";
        	    markAsComplete="false";
            }
        datarow.push(actions);
        $('#comprehension_list').DataTable().row.add(datarow);
      });
      $('#comprehension_list').DataTable().draw();
    } else {
      $('#comprehension_list').DataTable().draw();
        $("#markAsCompleteBtnId").attr("disabled", true);
        $('#helpNote').attr('data-original-title','Please add 1 or more questions to the test');
    }
    if ($('.fifthComre').find('span').hasClass('sprites-icons-2 tick pull-right mt-xs')) {
      $('.fifthComre').find('span').removeClass('sprites-icons-2 tick pull-right mt-xs');
    }
    $('[data-toggle="tooltip"]').tooltip();
  }

  function markAsCompleted() {
    var table = $('#comprehension_list').DataTable();
    var minimumScore = $("#comprehensionTestMinimumScore").val();
    var needComprehensionTestTxt = $('input[name="needComprehensionTest"]:checked').val();
    if (needComprehensionTestTxt == "Yes") {

      if (!table.data().count()) {
        $('#alertMsg').show();
        $("#alertMsg").removeClass('s-box').addClass('e-box').text("Add at least one question");
        setTimeout(hideDisplayMessage, 5000);
      } else if (isFromValid("#comprehensionInfoForm")) {
        saveConsent("Done");
      }
    } else {
      if (isFromValid("#comprehensionInfoForm")) {
        saveConsent("Done");
      }
    }
  }

  function saveConsent(type) {
    var consentId = $("#consentId").val();
    var minimumScore = $("#comprehensionTestMinimumScore").val();
    var needComprehensionTestTxt = $('input[name="needComprehensionTest"]:checked').val();
    var studyId = $("#studyId").val();
    var minScoreFlag = true;
    if (studyId != null && studyId != '' && typeof studyId != 'undefined' &&
        needComprehensionTestTxt != null && needComprehensionTestTxt != ''
        && typeof needComprehensionTestTxt != 'undefined') {
      if (type == "save") {
        $("body").addClass("loading");
      }
      var consentInfo = new Object();
      if (consentId != null && consentId != '' && typeof consentId != 'undefined') {
        consentInfo.id = consentId;
      }
      consentInfo.studyId = studyId;
      consentInfo.comprehensionTestMinimumScore = minimumScore;
      consentInfo.needComprehensionTest = needComprehensionTestTxt;
      if (type == "save") {
        consentInfo.comprehensionTest = "save";
      } else {
        consentInfo.comprehensionTest = "done";
      }

      $("#comprehensionTestMinimumScore").parent().removeClass("has-danger").removeClass(
          "has-error");
      $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
      var value = $('#comprehensionTestMinimumScore').val();
      var questionCount = $("#comprehension_list").find("tbody").find("tr").length;
      if (value != '' && value != null && (value == 0 || parseInt(value) > parseInt(
          questionCount))) {
        minScoreFlag = false;

        $("#comprehensionTestMinimumScore").parent().addClass("has-danger").addClass("has-error");
        $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
        $("#comprehensionTestMinimumScore").parent().find(".help-block").append(
        	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
            "The score should be less than or equal to the number of questions and greater than 0"));
      } else {
        $("#comprehensionTestMinimumScore").parent().removeClass("has-danger").removeClass(
            "has-error");
        $("#comprehensionTestMinimumScore").parent().find(".help-block").empty();
      }
      if (minScoreFlag) {
        var data = JSON.stringify(consentInfo);
        $.ajax({
          url: "/studybuilder/adminStudies/saveConsentReviewAndEConsentInfo.do?_S=${param._S}",
          type: "POST",
          datatype: "json",
          data: {consentInfo: data},
          beforeSend: function (xhr, settings) {
            xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
          },
          success: function (data) {
            var message = data.message;
            if (message == "SUCCESS") {
              var consentId = data.consentId;

              $("#consentId").val(consentId);
              $("#addQuestionId").attr("disabled", false);
              $("#comprehensionTestMinimumScore, #minScoreText").show();
              $("#spanAddQaId").removeAttr("data-original-title");
              $("#saveId").html("Save");
              $("#addHelpNote").hide();
              if (type != "save") {
                document.comprehensionInfoForm.action = "/studybuilder/adminStudies/comprehensionTestMarkAsCompleted.do?_S=${param._S}";
                document.comprehensionInfoForm.submit();
              } else {
                $("body").removeClass("loading");
                $("#alertMsg").removeClass('e-box').addClass('s-box').text(
                    "Content saved as draft");
                $('#alertMsg').show();
                if ($('.fifthComre').find('span').hasClass(
                    'sprites-icons-2 tick pull-right mt-xs')) {
                  $('.fifthComre').find('span').removeClass(
                      'sprites-icons-2 tick pull-right mt-xs');
                }
              }
            } else {
              $("body").removeClass("loading");
              $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
              $('#alertMsg').show();
            }
            setTimeout(hideDisplayMessage, 5000);
          },
          error: function (xhr, status, error) {
            $("body").removeClass("loading");
            $('#alertMsg').show();
            $("#alertMsg").removeClass('s-box').addClass('e-box').text("Something went Wrong");
            setTimeout(hideDisplayMessage, 5000);
          },
          global: false,
        });
      } else {
        $("body").removeClass("loading");
      }
    }
  }
</script>