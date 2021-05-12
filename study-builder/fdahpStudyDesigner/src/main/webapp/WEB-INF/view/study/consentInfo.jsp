<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<div class="col-sm-10 col-rc white-bg p-none">
  <!--  Start top tab section-->
  <form:form
      action="/studybuilder/adminStudies/saveOrUpdateConsentInfo.do?_S=${param._S}&${_csrf.parameterName}=${_csrf.token}"
      name="consentInfoFormId" id="consentInfoFormId" method="post"
      data-toggle="validator" role="form" autocomplete="off">
    <input type="hidden" id="id" name="id" value="${consentInfoBo.id}">
    <c:if test="${not empty consentInfoBo.id}">
      <input type="hidden" id="studyId" name="studyId"
             value="${consentInfoBo.studyId}">
    </c:if>
    <c:if test="${empty consentInfoBo.id}">
      <input type="hidden" id="studyId" name="studyId" value="${studyId}">
    </c:if>
    <input type="hidden" id="elaborated" name="elaborated" value=""/>
    <input type="hidden" id="type" name="type" value="complete"/>
    <div class="right-content-head" style="z-index: 999;">
      <div class="text-right">
        <div class="black-md-f dis-line pull-left line34">
          <span class="pr-sm cur-pointer" onclick="goToBackPage(this);">
            <img src="../images/icons/back-b.png" alt=""/>
          </span>
          <c:if test="${empty consentInfoBo.id}"> Add consent section</c:if>
          <c:if
              test="${not empty consentInfoBo.id && actionPage eq 'addEdit'}">Edit consent section</c:if>
          <c:if test="${not empty consentInfoBo.id && actionPage eq 'view'}">View consent section
            <c:set
                var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}
          </c:if>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button" class="btn btn-default gray-btn"
                  onclick="goToBackPage(this);">Cancel
          </button>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button"
                  class="btn btn-default gray-btn ConsentButtonHide ml-sm mr-sm"
                  onclick="saveConsentInfo(this);">Save
          </button>
        </div>
        <div class="dis-line form-group mb-none">
          <button type="button"
                  class="btn btn-primary blue-btn ConsentButtonHide" id="doneId">Done
          </button>
        </div>
      </div>
    </div>
    <!-- End top tab section-->
    <!-- Start body tab section -->
    <div class="right-content-body">
      <div class="gray-xs-f mb-xs">
        Consent section type
        <span class="requiredStar">*</span>
      </div>
      <div class="mt-sm form-group">
        <span class="radio radio-info radio-inline p-45"><input
            type="radio" id="inlineRadio1" value="ResearchKit/ResearchStack"
            name="consentItemType" required data-error="Please choose type"
          ${empty consentInfoBo.consentItemType  || consentInfoBo.consentItemType=='ResearchKit/ResearchStack' ?'checked':''}>
          <label for="inlineRadio1">Default options</label>
        </span>
        <span class="radio radio-inline p-45"><input type="radio"
                                                     id="inlineRadio2" value="Custom"
                                                     name="consentItemType" required
                                                     data-error="Please choose type"
          ${consentInfoBo.consentItemType=='Custom'?'checked':''}> <label
            for="inlineRadio2">Custom</label>
        </span>
        <div class="help-block with-errors red-txt"></div>
      </div>
      <div id="titleContainer">
        <div class="gray-xs-f mb-xs">
          Topic
          <span class="requiredStar">*</span>
        </div>
        <div class="col-md-5 p-none form-group elaborateClass consentTitle">
          <select class="selectpicker" id="consentItemTitleId"
                  name="consentItemTitleId" required
                  data-error="Please select a topic">
            <option value="">Select</option>
            <c:forEach items="${consentMasterInfoList}" var="consentMaster">
              <option value="${consentMaster.id}"
                ${consentInfoBo.consentItemTitleId eq consentMaster.id  ? 'selected' : ''}>${consentMaster.title}</option>
            </c:forEach>
          </select>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <div class="clearfix"></div>
      <input type="hidden" id="displayTitleTemp" name="displayTitleTemp"
             value="${consentInfoBo.displayTitle}"> <input type="hidden"
                                                           id="briefSummaryTemp"
                                                           name="briefSummaryTemp"
                                                           value="${consentInfoBo.briefSummary}">
      <textarea name="hide" id="elaboratedTemp"
                style="display: none;">${consentInfoBo.elaborated}</textarea>
      <div id="displayTitleId">
        <div class="gray-xs-f mb-xs">
          Title
          <small>(75 characters max)</small>
          <span class="requiredStar">*</span>
        </div>
        <div class="form-group">
          <input autofocus="autofocus" type="text" id="displayTitle"
                 class="form-control" name="displayTitle" required data-error="Please fill out this field"
                 value="${fn:escapeXml(consentInfoBo.displayTitle)}" maxlength="75">
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <div>
        <div class="gray-xs-f mb-xs">
          Brief summary
          <small>(500 characters max)</small>
          <span
              class="requiredStar">*
          </span>
        </div>
        <div class="form-group">
          <textarea class="form-control" rows="7" id="briefSummary"
                    name="briefSummary" required data-error="Please fill out this field"
                    maxlength="500">${consentInfoBo.briefSummary}</textarea>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <div class="clearfix"></div>
      <div>
        <div class="gray-xs-f mb-xs">
          Long description (appears in a 'Learn more' section, and also in the study's consent document if you choose to use the auto-created one)
          <span
              class="requiredStar">*
          </span>
        </div>
        <div class="form-group">
          <textarea class="" rows="8" id="elaboratedRTE" name="elaboratedRTE" data-error="Please fill out this field"
                    required>${consentInfoBo.elaborated}</textarea>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <div class="clearfix"></div>
      <div>
        <div class="gray-xs-f mb-xs">
          Show as a visual step?
          <span class="requiredStar">*</span>
          <span
              class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
              title="Visual steps are app screens that provide information on specific consent topics, and which are presented to the user before they are asked to review the entire consent document. A visual step for a consent section is created using the title and brief summary, and it also has a link to the long description."></span>
        </div>
        <div class="form-group visualStepDiv">
          <span class="radio radio-info radio-inline p-45"><input
              class="" type="radio" id="inlineRadio3" value="Yes"
              name="visualStep" required
              data-error="Please select one of the above options"
            ${consentInfoBo.visualStep=='Yes'?'checked':''}> <label
              for="inlineRadio3">Yes</label>
          </span>
          <span class="radio radio-inline p-45"><input class=""
                                                       type="radio" id="inlineRadio4"
                                                       value="No"
                                                       name="visualStep"
                                                       required
                                                       data-error="Please select one of the above options"
            ${consentInfoBo.visualStep=='No'?'checked':''}> <label
              for="inlineRadio4">No</label>
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
    // Fancy Scroll Bar
    $('.studyClass').addClass("active");
    <c:if test="${actionPage eq 'view'}">
    $('#consentInfoFormId input,textarea').prop('disabled', true);
    $('#consentInfoFormId .elaborateClass').addClass('linkDis');
    $('.ConsentButtonHide').hide();
    </c:if>

    if ('${consentInfoBo.id}' == '') {
      $("#displayTitleId").hide();
    }
    $(".menuNav li").removeClass('active');
    $(".fifthConsent").addClass('active');
    $("#createStudyId").show();
    //load the list of titles when the page loads
    consentInfoDetails();
    initSummernoteEditor();
    //get the selected consent type on change
    $('input[name="consentItemType"]').change(function () {
      $('.visualStepDiv').find(".help-block").empty();
      resetValidation($("#consentInfoFormId"));

      if (this.value == 'Custom') {
        $("#displayTitleId").show();
        $("#titleContainer").hide();
        $("#consentItemTitleId").prop('required', false);
      } else {

        consentInfoDetails();
        $("#consentItemTitleId").prop('required', true);
        $("#titleContainer").show();
      }
      addDefaultData();

    });

    $("#consentItemTitleId").change(function () {
      var titleText = this.options[this.selectedIndex].text;
      resetValidation($("#consentInfoFormId"));

      $(".consentTitle").parent().removeClass('has-error has-danger');
      $(".consentTitle").parent().find(".help-block").empty();
      $("#displayTitle").parent().removeClass('has-error has-danger');
      $("#displayTitle").parent().find(".help-block").empty();
      if (titleText != null && titleText != '' && typeof titleText != 'undefined') {
        $("#displayTitleId").show();
        if (titleText != 'Select') {
          $("#displayTitle").val(titleText);
        } else {
          $("#displayTitle").val('');
        }
      }
    });

    if ('${consentInfoBo.consentItemType}' == 'Custom') {
      $("#titleContainer").hide();
      $("#consentItemTitleId").prop('required', false);
    } else {
      $("#titleContainer").show();
      $("#consentItemTitleId").prop('required', true);
    }
    addDefaultData();


  });

  <c:if test="${actionPage eq 'view'}">
  $('#consentInfoFormId input,textarea').prop('disabled',
      true);
  $('#consentInfoFormId .elaborateClass').addClass(
      'linkDis');
  $('.ConsentButtonHide').hide();
  </c:if>

  if ('${consentInfoBo.id}' == '') {
    $("#displayTitleId").hide();
  }
  $(".menuNav li").removeClass('active');
  $(".fifthConsent").addClass('active');
  $("#createStudyId").show();
  //load the list of titles when the page loads
  consentInfoDetails();
  initSummernoteEditor();
  //get the selected consent type on change
  $('input[name="consentItemType"]').change(
      function () {
        $('.visualStepDiv').find(".help-block")
            .empty();
        resetValidation($("#consentInfoFormId"));

        if (this.value == 'Custom') {
          $("#displayTitleId").show();
          $("#titleContainer").hide();
          $("#consentItemTitleId").prop(
              'required', false);
        } else {

          consentInfoDetails();
          $("#consentItemTitleId").prop(
              'required', true);
          $("#titleContainer").show();
        }
        addDefaultData();

      });

  $("#consentItemTitleId")
      .change(
          function () {
            var titleText = this.options[this.selectedIndex].text;
            resetValidation($("#consentInfoFormId"));

            $(".consentTitle")
                .parent()
                .removeClass(
                    'has-error has-danger');
            $(".consentTitle").parent().find(
                ".help-block").empty();
            $("#displayTitle")
                .parent()
                .removeClass(
                    'has-error has-danger');
            $("#displayTitle").parent().find(
                ".help-block").empty();
            if (titleText != null
                && titleText != ''
                && typeof titleText != 'undefined') {
              $("#displayTitleId").show();
              if (titleText != 'Select') {
                $("#displayTitle").val(
                    titleText);
              } else {
                $("#displayTitle").val('');
              }
            }
          });

  if ('${consentInfoBo.consentItemType}' == 'Custom') {
    $("#titleContainer").hide();
    $("#consentItemTitleId").prop('required', false);
  } else {
    $("#titleContainer").show();
    $("#consentItemTitleId").prop('required', true);
  }
  //submit the form
  $("#doneId")
      .on(
          'click',
          function () {
            $("#doneId").prop('disabled', true);
            $('#elaboratedRTE').summernote(
            'code');
            valid = maxLenValEditor();
            if (isFromValid("#consentInfoFormId")
                && valid) {
              var visualStepData = '';

              visualStepData = $(
                  'input[name=visualStep]:checked')
                  .val();
              if (visualStepData != ''
                  && visualStepData != null
                  && typeof visualStepData != 'undefined') {

                var elaboratedContent = $(
                    '#elaboratedRTE')
                    .summernote('code');
                elaboratedContent = replaceSpecialCharacters(elaboratedContent);
                var briefSummaryText = replaceSpecialCharacters($(
                    "#briefSummary")
                    .val());
                $("#elaborated").val(
                    elaboratedContent);
                $("#briefSummary").val(
                    briefSummaryText);
                var displayTitleText = $(
                    "#displayTitle")
                    .val();
                displayTitleText = replaceSpecialCharacters(displayTitleText);
                $("#displayTitle").val(
                    displayTitleText);
                $("#consentInfoFormId")
                    .submit();

              } else {
                $('.visualStepDiv')
                    .addClass(
                        'has-error has-danger');
                $('.visualStepDiv')
                    .find(".help-block")
                    .empty()
                    .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                        "Please select one of the above options"));
                $("#doneId").prop(
                    'disabled', false);
              }
            } else {
              $("#doneId").prop('disabled',
                  false);
            }
          });

  function saveConsentInfo(item) {
    var consentInfo = new Object();
    var consentInfoId = $("#id").val();
    var study_id = $("#studyId").val();
    var consentType = $('input[name="consentItemType"]:checked').val();
    var consentitemtitleid = $("#consentItemTitleId").val();
    var displayTitleText = $("#displayTitle").val();
    displayTitleText = replaceSpecialCharacters(displayTitleText);
    var briefSummaryText = $("#briefSummary").val();
    briefSummaryText = replaceSpecialCharacters(briefSummaryText);
    var elaboratedText = $('#elaboratedRTE').summernote('code');
    if (elaboratedText != '<p><br></p>') {
      elaboratedText = replaceSpecialCharacters(elaboratedText);
    }

    var visual_step = $('input[name="visualStep"]:checked').val();

    if ((study_id != null && study_id != '' && typeof study_id != 'undefined')
        && (displayTitleText != null && displayTitleText != '' && typeof displayTitleText
            != 'undefined')) {
      $(item).prop('disabled', true);
      if (null != consentInfoId) {
        consentInfo.id = consentInfoId;
      }
      consentInfo.studyId = study_id;
      if (null != consentType) {
        consentInfo.consentItemType = consentType;
      }
      if (null != consentitemtitleid) {
        consentInfo.consentItemTitleId = consentitemtitleid;
      }
      if (null != briefSummaryText) {
        consentInfo.briefSummary = briefSummaryText;
      }
      if (null != elaboratedText) {
        consentInfo.elaborated = elaboratedText;
      }
      if (null != visual_step) {
        consentInfo.visualStep = visual_step;
      }
      if (null != displayTitleText) {
        consentInfo.displayTitle = displayTitleText;
      }
      consentInfo.type = "save";

      var data = JSON.stringify(consentInfo);
      $
          .ajax({
            url: "/studybuilder/adminStudies/saveConsentInfo.do?_S=${param._S}",
            type: "POST",
            datatype: "json",
            data: {
              consentInfo: data
            },
            beforeSend: function (xhr, settings) {
              xhr.setRequestHeader("X-CSRF-TOKEN",
                  "${_csrf.token}");
            },
            success: function (data) {
              var message = data.message;
              if (message == "SUCCESS") {
                var consentInfoId = data.consentInfoId;
                $("#id").val(consentInfoId);
                $("#alertMsg").removeClass('e-box').addClass(
                    's-box')
                    .text("Content saved as draft");
                $(item).prop('disabled', false);
                $('#alertMsg').show();
              } else {
                $("#alertMsg").removeClass('s-box').addClass(
                    'e-box').text("Something went Wrong");
                $('#alertMsg').show();
              }
              setTimeout(hideDisplayMessage, 5000);
            },
            error: function (xhr, status, error) {
              $(item).prop('disabled', false);
              $('#alertMsg').show();
              $("#alertMsg").removeClass('s-box').addClass(
                  'e-box').text("Something went Wrong");
              setTimeout(hideDisplayMessage, 5000);
            }
          });
    } else {
      $(item).prop('disabled', false);
      if (valid) {
        $(".consentTitle").parent().addClass('has-error has-danger');
        $(".consentTitle")
            .parent()
            .find(".help-block")
            .empty()
            .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                "This is a required field"));
        setTimeout(hideDisplayMessage, 5000);
      }

    }
  }

  function goToBackPage(item) {
    <c:if test="${actionPage ne 'view'}">
    $(item).prop('disabled', true);
    bootbox
        .confirm({
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
              a.href = "/studybuilder/adminStudies/consentListPage.do?_S=${param._S}";
              document.body.appendChild(a).click();
            } else {
              $(item).prop('disabled', false);
            }
          }
        });
    </c:if>
    <c:if test="${actionPage eq 'view'}">
    var a = document.createElement('a');
    a.href = "/studybuilder/adminStudies/consentListPage.do?_S=${param._S}";
    document.body.appendChild(a).click();
    </c:if>
  }

  //remove the default vallues from the fields when the consent type is changed
  function addDefaultData() {
    var consentInfoId = $("#id").val();
    $("#displayTitle").val('');
    $("#briefSummary").val('');
    $("#elaborated").val('');
    $("#elaboratedRTE").summernote('reset');
    $("#inlineRadio3").prop('checked', false);
    $("#inlineRadio4").prop('checked', false);
    $('#elaboratedRTE').parent().find(".help-block").html("");
    if (consentInfoId != null && consentInfoId != ''
        && typeof consentInfoId != 'undefined') {
      var consentType = "${consentInfoBo.consentItemType}";
      var actualValue = $("input[name='consentItemType']:checked").val();
      if (consentType == actualValue) {

        var elaboratedText = $("#elaboratedTemp").val();
        $('#elaboratedRTE').summernote('code', elaboratedText);

        var displayTitle = $("#displayTitleTemp").val();
        var briefSummary = $("#briefSummaryTemp").val();
        $("#displayTitle").val(displayTitle);
        $("#briefSummary").val(briefSummary);
        var visualStep = "${consentInfoBo.visualStep}";
        if (visualStep == "Yes") {
          $("#inlineRadio3").prop('checked', true);
        } else if (visualStep == "No") {
          $("#inlineRadio4").prop('checked', true);
        }
        initSummernoteEditor();
      }
    }
  }

  function consentInfoDetails() {
    if (typeof "${consentInfoList}" != 'undefined') {
      var selectedTitle = document.getElementById('consentItemTitleId');
      var actualOption = "${consentInfoBo.consentItemTitleId}";
      for (var i = 0; i < selectedTitle.length; i++) {
        if (actualOption == selectedTitle.options[i].value) {
          $('#consentItemTitleId :nth-child(' + (i + 1) + ')').prop(
              'selected', true).trigger('change');
        }
        <c:forEach items="${consentInfoList}" var="consentInfo">
        if ('${consentInfo.consentItemTitleId}' != ''
            && '${consentInfo.consentItemTitleId}' != null) {
          if ('${consentInfo.consentItemTitleId}' == selectedTitle.options[i].value
              && '${consentInfo.consentItemTitleId}' != '${consentInfoBo.consentItemTitleId}' && '${consentInfo.consentItemType}' != 'Custom') {
            $(
                "select option[value="
                + selectedTitle.options[i].value + "]")
                .attr("disabled", "disabled");
            $('.selectpicker').selectpicker('refresh');
          }
        }

        </c:forEach>
      }
    }
  }

  function initSummernoteEditor() {
    $('#elaboratedRTE').summernote(
        {

          placeholder: '',
          tabsize: 2,
          height: 200,
          toolbar: [['font', ['bold', 'italic']],
            ['para', ['paragraph', 'ul', 'ol']],
            ['font', ['underline']],
            ['insert', ['link']], ['hr'], ['clear'],
            ['cut'],
            ['undo'], ['redo'],
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
          ]
        });
  }

  <c:if test="${actionPage eq 'view'}">
  $(
      '#elaboratedRTE'
  ).summernote('disable');
  </c:if>

  function maxLenValEditor() {
    var isValid = true;
    var value = $('#elaboratedRTE').summernote('code');
    if (value == '<br>' || value == '<p><br></p>') {
    	value = '';
    }
    
    if (value != '') {
      if ($.trim(value.replace(/(<([^>]+)>)/ig, "")).length > 15000) {
        if (isValid) {
          isValid = false;
        }
        $('#elaboratedRTE').parent().addClass('has-error-cust').find(".help-block").empty().append(
        	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
            "Maximum 15000 characters are allowed"));

      } else {
        $('#elaboratedRTE').parent().removeClass("has-danger")
            .removeClass("has-error");
        $('#elaboratedRTE').parent().find(".help-block").empty();
      }
    } else {
      isValid = false;
      $('#elaboratedRTE')
          .parent()
          .addClass('has-error has-danger')
          .find(".help-block")
          .empty()
          .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
              "Please fill out this field"));

    }
    return isValid;
  }

  $(document).on('mouseenter', '.dropdown-toggle',  function () {
      $(this).removeAttr("title");
  });
</script>