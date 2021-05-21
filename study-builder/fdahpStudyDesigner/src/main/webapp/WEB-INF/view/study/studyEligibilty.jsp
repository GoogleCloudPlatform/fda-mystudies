<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.fdahpstudydesigner.util.SessionObject" %>
<style>
  .cursonMove {
    cursor: move !important;
  }

  .tool-tip {
    display: inline-block;
  }

  .tool-tip [disabled] {
    pointer-events: none;
  }
</style>
<div class="col-sm-10 col-rc white-bg p-none">
  <!--  Start top tab section-->
  <form:form data-toggle="validator"
             action="/studybuilder/adminStudies/saveOrUpdateStudyEligibilty.do?_S=${param._S}"
             id="eleFormId">
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          Eligibility
          <c:set var="isLive">${_S}isLive</c:set>
            ${not empty  sessionScope[isLive] ? '<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</div>

        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
        </div>
        <c:if test="${empty permission}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn submitEle"
                    actType="save" id="saveBtn">Save
            </button>
          </div>

          <div class="dis-line form-group mb-none">
            <span id="spancomId" class="tool-tip" data-toggle="tooltip"
                  data-placement="bottom" data-original-title="">
              <button type="button" class="btn btn-primary blue-btn submitEle"
                      actType="mark" id="doneBut">Mark as completed
              </button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->
    <input type="hidden" value="${eligibility.studyId}" name="studyId"
           id="studyId"/>
    <input type="hidden" value="${eligibility.id}" name="id"/>
    <!-- Start body tab section -->
    <div class="right-content-body">
      <div class="mb-xlg form-group" id="eligibilityOptDivId">
        <div class="gray-xs-f mb-sm">
          Choose the method to be used for ascertaining participant
          eligibility
          <span>
            <span
               data-toggle="tooltip" data-placement="bottom"
               title="Selecting any method that uses an enrollment token will make the study a 'closed' study requiring the participant to have an invitiation to participate. Invitations containing enrollment tokens can be generated and sent to particpants using the Participant Manager. Studies that use only the elgiibility test are referred to as 'open' studies and any app user that passes the test, will be allowed to enroll into the study."
               class="filled-tooltip"></span>
            </span>
        </div>
        <span class="radio radio-info radio-inline p-45"><input
            type="radio" id="inlineRadio1" value="1" class="eligibilityOptCls"
            name="eligibilityMechanism" required data-error="Please fill out this field"
            <c:if test="${eligibility.eligibilityMechanism eq 1}">checked</c:if>
        <c:if test="${liveStatus}"> disabled</c:if>>
          <label for="inlineRadio1">Token validation</label>
        </span>
        <span class="radio radio-inline p-45"><input type="radio"
                                                     id="inlineRadio2" value="2"
                                                     class="eligibilityOptCls"
                                                     name="eligibilityMechanism" required data-error="Please fill out this field"
                                                     <c:if
                                                         test="${eligibility.eligibilityMechanism eq 2}">checked</c:if>
        <c:if test="${liveStatus}"> disabled</c:if>>
          <label for="inlineRadio2">Token validation and eligibility test</label>
        </span>
        <span class="radio radio-inline"><input type="radio"
                                                id="inlineRadio3" value="3"
                                                class="eligibilityOptCls"
                                                name="eligibilityMechanism" required data-error="Please fill out this field"
                                                <c:if
                                                    test="${eligibility.eligibilityMechanism eq 3}">checked</c:if>
        <c:if test="${liveStatus}"> disabled</c:if>> <label
            for="inlineRadio3">Eligibility test</label>
        </span>
        <div class="help-block with-errors red-txt"></div>
      </div>
      <div id="instructionTextDivId"
           <c:if test="${eligibility.eligibilityMechanism eq 3}">style="display: none;"</c:if>>
        <div class="blue-md-f mb-md text-uppercase">Token Validation</div>
        <div>
          <div class="gray-xs-f mb-xs">
            Instruction text
            <small>(230 characters max)</small>
            <span
                class="requiredStar">*
            </span>
            <span>
            <span
               data-toggle="tooltip" data-placement="top"
               title="This is the text that participant sees as an introductory instruction for the eligibility module of the study in the mobile app. Suggested text has been provided inline."
               class="filled-tooltip"></span>
            </span>
          </div>
          <div class="form-group elaborateClass">
            <textarea class="form-control" rows="1" id="comment"
                      maxlength="230" required data-error="Please fill out this field"
                      name="instructionalText">${eligibility.instructionalText}</textarea>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
      </div>
    </div>
    <div id="eligibilityQusDivId" 
         <c:if test="${eligibility.eligibilityMechanism eq 1}">style="display: none;"</c:if>>
      <div class="right-content-head pt-none">
        <div class="text-right">
          <div class="black-md-f  dis-line pull-left line34">Eligibility test questions
          <span>
            <span
               data-toggle="tooltip" data-placement="top"
               title="Set up questions with a Yes/No answer format for the study's eligibility test. Mark the answer(s) that if selected will let the user 'pass' the question. App users are required to pass all the questions in the test to be considered eligible to participate in the study."
               class="filled-tooltip"></span>
            </span>
          </div>
          <div class="dis-line form-group mb-none">
            <c:if test="${empty permission}">
            <span id="spanAddQaId" class="tool-tip" data-toggle="tooltip"
                  data-placement="bottom" data-original-title="">
              <button type="button" class="btn btn-primary blue-btn"
                      id="addQaId">+ Add question
              </button>
              </span>
            </c:if>
          </div>
        </div>
      </div>
      <div class="right-content-body  pt-none pb-none">
        <table id="consent_list" class="display bor-none" cellspacing="0"
               width="100%">
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
            <c:set value="true" var="chkDone"/>
            <c:forEach items="${eligibilityTestList}" var="etQusAns">
              <tr id="">
                <td>${etQusAns.sequenceNo}</td>
                <td>
                  <span class="dis-ellipsis">${etQusAns.question}</span>
                </td>
                <td>
                  <span class="sprites_icon preview-g mr-lg viewIcon"
                        data-toggle="tooltip" data-placement="top" title="View"
                        etId="${etQusAns.id}"></span>
                  <span
                      class="${etQusAns.status ? 'edit-inc' : 'edit-inc-draft mr-md'} mr-lg <c:if test="${not empty permission}"> cursor-none </c:if> editIcon"
                      data-toggle="tooltip" data-placement="top" title="Edit"
                      etId='${etQusAns.id}'></span>
                  <span
                      class="sprites_icon copy delete <c:if test="${not empty permission}"> cursor-none </c:if> deleteIcon"
                      data-toggle="tooltip" data-placement="top" title="Delete"
                      onclick="deleteEligibiltyTestQusAns('${etQusAns.id}', this);"></span>
                </td>
              </tr>
              <c:if test="${chkDone eq 'true' && not etQusAns.status}">
                <c:set value="false" var="chkDone"/>
              </c:if>
            </c:forEach>
            <c:if test="${empty eligibilityTestList}">
              <c:set value="false" var="chkDone"/>
            </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </form:form>
</div>
<form:form
    action="/studybuilder/adminStudies/viewStudyEligibiltyTestQusAns.do?_S=${param._S}"
    id="viewQAFormId"></form:form>
<script type="text/javascript">
  var viewPermission = "${permission}";
  var permission = "${permission}";
  var chkDone = ${chkDone};
  var eligibilityMechanism = '${eligibility.eligibilityMechanism}';
  console.log("viewPermission:" + viewPermission);
  var reorder = true;
  var table1;
  var emVal = $("input[name='eligibilityMechanism']:checked").val();
  var eligibilityTestSize =${eligibilityTestList.size()};
  $(document)
      .ready(
          function () {
        	$('.studyClass').addClass("active");
            $(".menuNav li.active").removeClass('active');
            $(".menuNav li.fourth").addClass('active');
            <c:if test="${not empty permission}">
            $('#eleFormId input,textarea,select').prop('disabled',
                true);
            $('#eleFormId').find('.elaborateClass').addClass(
                'linkDis');
            </c:if>

            <c:if test="${empty eligibility.id}">
            $('#addQaId').prop('disabled', true);
            $("#saveBtn").html("Next");
            $('#spanAddQaId').attr('data-original-title', 'Please click on Next to start adding questions');
            $('.viewIcon, .editIcon, .deleteIcon').addClass('cursor-none');
            </c:if>

           
            
            if (!chkDone && eligibilityMechanism != "1") {
              $('#doneBut').prop('disabled', true);
              $('#spancomId')
                  .attr(
                      'data-original-title',
                      'Please ensure individual list items are marked Done, before marking the section as Complete');
              $('[data-toggle="tooltip"]').tooltip();
            }

            if (emVal != "1") {
                if (eligibilityTestSize === 0) {
                  $("#doneBut").attr("disabled", true);
                  $('#spancomId').attr('data-original-title',
                      'Please add 1 or more questions to the test');
                }
             }
            initActions();
            $('.submitEle').click(
                function (e) {
                  e.preventDefault();
                  $('#actTy').remove();
                  $('<input />').attr('type', 'hidden').attr(
                      'name', "actionType").attr('value',
                      $(this).attr('actType')).attr('id',
                      'actTy').appendTo('#eleFormId');
                  if ($(this).attr('actType') == 'save') {
                    $('#eleFormId').validator('destroy');
                    if (${liveStatus}) {
                      var eligibilityVal = $("input[name='eligibilityMechanism']:checked").val();
                      if (eligibilityVal == 1) {
                        $("#inlineRadio1").prop("disabled", false);
                      } else if (eligibilityVal == 2) {
                        $("#inlineRadio2").prop("disabled", false);
                      } else if (eligibilityVal == 3) {
                        $("#inlineRadio3").prop("disabled", false);
                      }
                    }
                    $('#eleFormId').submit();
                  } else {
                    if (isFromValid('#eleFormId')) {
                      if (${liveStatus}) {
                        var eligibilityVal = $("input[name='eligibilityMechanism']:checked").val();
                        if (eligibilityVal == 1) {
                          $("#inlineRadio1").prop("disabled", false);
                        } else if (eligibilityVal == 2) {
                          $("#inlineRadio2").prop("disabled", false);
                        } else if (eligibilityVal == 3) {
                          $("#inlineRadio3").prop("disabled", false);
                        }
                      }
                      $('#eleFormId').submit();
                    }
                  }
                });

            if (viewPermission == 'view') {
              reorder = false;
            } else {
              reorder = true;
            }
            table1 = $('#consent_list').DataTable(
                {
                  "paging": false,
                  "info": false,
                  "filter": false,
                  language: {
                    "zeroRecords": "No content created yet",
                  },
                  rowReorder: reorder,
                  "columnDefs": [{
                    orderable: false,
                    targets: [0, 1, 2]
                  }],
                  "fnRowCallback": function (nRow, aData,
                                             iDisplayIndex, iDisplayIndexFull) {
                    if (viewPermission != 'view') {
                      $('td:eq(0)', nRow).addClass(
                          "cursonMove dd_icon");
                    }
                  }
                });

            table1
                .on(
                    'row-reorder',
                    function (e, diff, edit) {
                      var oldOrderNumber = '', newOrderNumber = '';
                      var result = 'Reorder started on row: '
                          + edit.triggerRow.data()[1]
                          + '<br>';
                      var studyId = $("#studyId").val();
                      for (var i = 0, ien = diff.length; i < ien; i++) {
                        var rowData = table1.row(
                            diff[i].node).data();
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
                        result += rowData[1]
                            + ' updated to be in position '
                            + diff[i].newData
                            + ' (was '
                            + diff[i].oldData
                            + ')<br>';
                      }

                      if (oldOrderNumber
                          && newOrderNumber) {
                        $
                            .ajax({
                              url: "/studybuilder/adminStudies/reOrderStudyEligibiltyTestQusAns.do?_S=${param._S}",
                              type: "POST",
                              datatype: "json",
                              data: {
                                eligibilityId: '${eligibility.id}',
                                oldOrderNumber: oldOrderNumber,
                                newOrderNumber: newOrderNumber,
                                "${_csrf.parameterName}": "${_csrf.token}",
                              },
                              success: function consentInfo(
                                  data) {
                                var message = data.message;
                                if (message == "SUCCESS") {
                                  $(
                                      "#alertMsg")
                                      .removeClass(
                                          'e-box')
                                      .addClass(
                                          's-box')
                                      .text(
                                          "Content items reordered");
                                  $(
                                      '#alertMsg')
                                      .show();
                                  if ($(
                                      '.fourth')
                                      .find(
                                          'span')
                                      .hasClass(
                                          'sprites-icons-2 tick pull-right mt-xs')) {
                                    $(
                                        '.fourth')
                                        .find(
                                            'span')
                                        .removeClass(
                                            'sprites-icons-2 tick pull-right mt-xs');
                                  }
                                } else {
                                  $(
                                      '#alertMsg')
                                      .show();
                                  $(
                                      "#alertMsg")
                                      .removeClass(
                                          's-box')
                                      .addClass(
                                          'e-box')
                                      .text(
                                          "Unable to reorder consent");
                                }
                                setTimeout(
                                    hideDisplayMessage,
                                    5000);
                              },
                              error: function (
                                  xhr,
                                  status,
                                  error) {
                                $("#alertMsg")
                                    .removeClass(
                                        's-box')
                                    .addClass(
                                        'e-box')
                                    .text(
                                        error);
                                setTimeout(
                                    hideDisplayMessage,
                                    5000);
                              }
                            });
                      }
                    });

            $('#eligibilityOptDivId input[type=radio]')
                .change(
                    function () {
                      if ($(this).val() != '1'
                          && eligibilityMechanism != $(
                              this).val()) {
                        $('#forceContinueMsgId').show();
                        $('#addQaId').prop('disabled', true);
                        $("#saveBtn").html("Next");
                        $('#spanAddQaId').attr('data-original-title', 'Please click on Next to start adding questions');
                        $(
                            '.viewIcon, .editIcon, .deleteIcon')
                            .addClass('cursor-none');
                        if (!chkDone && $(this).val() != '1') {
                          $('#doneBut').prop(
                              'disabled', true);
                          $('#spancomId')
                              .attr(
                                  'data-original-title',
                                  'Please ensure individual list items are marked Done, before marking the section as Complete');
                        }
                      } else {
                        $('#forceContinueMsgId').hide();
                        $('#doneBut, #addQaId').prop('disabled', false);
                        $("#saveBtn").html("Save");
                        $("#spanAddQaId").removeAttr("data-original-title");
                        $('#spancomId').attr(
                            'data-original-title',
                            '');
                        $(
                            '.viewIcon, .editIcon, .deleteIcon')
                            .removeClass(
                                'cursor-none');
                        if (!chkDone && $(this).val() != '1') {
                          $('#doneBut').prop(
                              'disabled', true);
                          $('#spancomId')
                              .attr(
                                  'data-original-title',
                                  'Please ensure individual list items are marked Done, before marking the section as Complete');
                        }
                      }
                      if ($('#inlineRadio1:checked').length > 0) {
                        $('#eligibilityQusDivId')
                            .slideUp('fast');
                        $('#instructionTextDivId')
                            .slideDown('fast');
                        $('#doneBut').prop('disabled',false);
                        $('#spancomId').removeAttr('data-original-title');
                      } else if ($('#inlineRadio3:checked').length > 0) {
                        $('#instructionTextDivId')
                            .slideUp('fast');
                        $('#eligibilityQusDivId')
                            .slideDown('fast');
                        if (!chkDone)
                          $('#doneBut').prop(
                              'disabled', true);
                      } else {
                        $('#eligibilityQusDivId')
                            .slideDown('fast');
                        $('#instructionTextDivId')
                            .slideDown('fast');
                        if (!chkDone)
                          $('#doneBut').prop(
                              'disabled', true);
                      }

                      emVal = $("input[name='eligibilityMechanism']:checked").val();
                      if (emVal != "1") {
                        if ($('#consent_list tbody tr').length == 1
                                && $('#consent_list tbody tr td').length == 1) {
                          $("#doneBut").attr("disabled", true);
                          $('#spancomId').attr('data-original-title',
                              'Please add 1 or more questions to the test');
                        }
                      }

                    })
          });


  function addOrEditOrViewQA(actionTypeForQuestionPage, eligibilityTestId) {
    var form = $('#viewQAFormId');
    var input = document.createElement("input");
    input.setAttribute('type', "hidden");
    input.setAttribute('name', 'actionTypeForQuestionPage');
    input.setAttribute('value', actionTypeForQuestionPage);
    form.append(input);

    input = document.createElement("input");
    input.setAttribute('type', "hidden");
    input.setAttribute('name', 'eligibilityTestId');
    input.setAttribute('value', eligibilityTestId);
    form.append(input);

    input = document.createElement("input");
    input.setAttribute('type', "hidden");
    input.setAttribute('name', 'eligibilityId');
    input.setAttribute('value', "${eligibility.id}");
    form.append(input);


    form.submit();
  }

  function deleteEligibiltyTestQusAns(eligibilityTestId, thisAttr) {
    var studyId = $('#studyId').val();
    bootbox
        .confirm(
            "Are you sure you want to delete this eligibility test item?",
            function (result) {
              if (result) {
                if (eligibilityTestId) {
                  $
                      .ajax({
                        url: "/studybuilder/adminStudies/deleteEligibiltyTestQusAns.do?_S=${param._S}",
                        type: "POST",
                        datatype: "json",
                        data: {
                          eligibilityTestId: eligibilityTestId,
                          eligibilityId: '${eligibility.id}',
                          studyId: studyId,
                          "${_csrf.parameterName}": "${_csrf.token}",
                        },
                        success: function deleteConsentInfo(
                            data) {
                          var status = data.message;
                          if (status == "SUCCESS") {
                            $("#alertMsg")
                                .removeClass(
                                    'e-box')
                                .addClass(
                                    's-box')
                                .text(
                                    "Question deleted successfully");
                            $('#alertMsg').show();
                            if ($('.fifthConsent')
                                .find('span')
                                .hasClass(
                                    'sprites-icons-2 tick pull-right mt-xs')) {
                              $('.fifthConsent')
                                  .find(
                                      'span')
                                  .removeClass(
                                      'sprites-icons-2 tick pull-right mt-xs');
                            }
                            reloadEligibiltyTestDataTable(data.eligibiltyTestList);
                            if ($('#consent_list tbody tr').length == 1
                                && $('#consent_list tbody tr td').length == 1) {
                              eligibilityTestSize--;
                              chkDone = false;
                              $('#doneBut').prop(
                                  'disabled',
                                  true);
                            }else if(chkDone){
                                $("#doneBut").attr("disabled", false);
                                $('#spancomId').removeAttr('data-original-title');
                              }
                          } else {
                            $("#alertMsg")
                                .removeClass(
                                    's-box')
                                .addClass(
                                    'e-box')
                                .text(
                                    "Unable to delete Question");
                            $('#alertMsg').show();
                          }
                          setTimeout(
                              hideDisplayMessage,
                              5000);
                        },
                        error: function (xhr, status,
                                         error) {
                          $("#alertMsg").removeClass(
                              's-box').addClass(
                              'e-box')
                              .text(error);
                          setTimeout(
                              hideDisplayMessage,
                              5000);
                        }
                      });
                }
              }
            });
  }

  function reloadEligibiltyTestDataTable(eligibiltyTestList) {
    $('#consent_list').DataTable().clear();
    chkDone=true;
    if (eligibiltyTestList != null && eligibiltyTestList.length > 0) {
      $
          .each(
              eligibiltyTestList,
              function (i, obj) {
                var datarow = [];
                if (typeof obj.sequenceNo === "undefined"
                    && typeof obj.sequenceNo === "undefined") {
                  datarow.push(' ');
                } else {
                  datarow.push(obj.sequenceNo);
                }
                if (typeof obj.question === "undefined"
                    && typeof obj.question === "undefined") {
                  datarow.push(' ');
                } else {
                  datarow
                      .push("<span class='dis-ellipsis' title='" + DOMPurify.sanitize(obj.question) + "'>"
                          + DOMPurify.sanitize(obj.question) + "</span>");
                }
                if(!DOMPurify.sanitize(obj.status)){
                    chkDone=false;
                }
                var actions = '<span class="sprites_icon preview-g mr-lg viewIcon" data-toggle="tooltip" data-placement="top" title="View" etId="'
                    + obj.id + '"></span> '
                    + '<span class="'
                    + (DOMPurify.sanitize(obj.status) ? "edit-inc"
                        : "edit-inc-draft")
                    + ' mr-md mr-lg  editIcon" data-toggle="tooltip" data-placement="top" title="Edit"  etId="'
                    + obj.id
                    + '"></span>'
                    + '<span class="sprites_icon copy delete deleteIcon" data-toggle="tooltip" data-placement="top" title="Delete" onclick="deleteEligibiltyTestQusAns(&#34;'
                    + obj.id + '&#34;,this)"></span> '
                     datarow.push(actions);
                $('#consent_list').DataTable().row.add(datarow);
              });
      $('#consent_list').DataTable().draw();
      initActions();
    } else {
      $('#consent_list').DataTable().draw();
      $("#doneBut").attr("disabled", true);
      $('#spancomId').attr('data-original-title',
          'Please add 1 or more questions to the test');
    }
    $('[data-toggle="tooltip"]').tooltip();
  }

  function initActions() {
    $(document).find('#addQaId').click(function () {
      addOrEditOrViewQA("add", "");
    });

    $(document).find('.viewIcon').click(function () {
      addOrEditOrViewQA("view", $(this).attr('etId'));
    });

    $(document).find('.editIcon').click(function () {
      addOrEditOrViewQA("edit", $(this).attr('etId'));
    });
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
		        window.location.href = "/studybuilder/adminStudies/consentListPage.do?_S=${param._S}";
		    }, 5000);
	  }else{
	  	setTimeout(hideDisplayMessage, 5000);
	  }
	}
</script>