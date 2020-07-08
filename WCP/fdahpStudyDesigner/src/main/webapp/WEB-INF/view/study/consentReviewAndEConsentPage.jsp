<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<div class="col-sm-10 col-rc white-bg p-none">
  <!--  Start top tab section-->
  <form:form
      action="/studybuilder/adminStudies/studyList.do?_S=${param._S}"
      name="cancelConsentReviewFormId" id="cancelConsentReviewFormId"
      method="POST" role="form">
    <input type="hidden" id="studyId" name="studyId" value="${studyId}">
    <input type="hidden" id="consentId" name="consentId"
           value="${consentBo.id}">
  </form:form>
  <form:form
      action="/studybuilder/adminStudies/saveConsentReviewAndEConsentInfo.do?_S=${param._S}"
      name="consentReviewFormId" id="consentReviewFormId" method="post"
      role="form">
    <input type="hidden" id="studyId" name="studyId" value="${studyId}">
    <input type="hidden" id="consentId" name="consentId"
           value="${consentBo.id}">
    <input type="hidden" id="consentBo" name="consentBo"
           value="${consentBo}">
    <input type="hidden" id="typeOfCensent" name="typeOfCensent"
           value="${consentBo.consentDocType}">
    <!-- End body tab section -->
    <div>
      <!--  Start top tab section-->
      <div class="right-content-head" style="z-index: 999;">
        <div class="text-right">
          <div class="black-md-f text-uppercase dis-line pull-left line34">
            Review and E-Consent Steps
            <c:set var="isLive">${_S}isLive</c:set>
              ${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</div>
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn"
                    onclick="goToBackPage(this);">Cancel
            </button>
          </div>
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn" id="saveId">Save</button>
          </div>
          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn" id="doneId">Mark
              as Completed
            </button>
          </div>
        </div>
      </div>
      <!--  End  top tab section-->
      <!--  Start body tab section -->
      <div class="right-content-body pt-none pl-none">
        <ul class="nav nav-tabs review-tabs">
          <li class="shareData active">
            <a data-toggle="tab"
               href="#menu1">Data-sharing Permission
            </a>
          </li>
          <li class="consentReview">
            <a data-toggle="tab" href="#menu2">Consent
              Document for Review
            </a>
          </li>
          <li class="econsentForm">
            <a data-toggle="tab" href="#menu3">E-Consent
              Form
            </a>
          </li>
        </ul>
        <div class="tab-content pl-xlg pr-xlg">
          <input type="hidden" id="version" name="version"
                 value="${consentBo.version}">
          <div id="menu1" class="tab-pane fade in active">
            <div class="mt-lg">
              <div class="gray-xs-f mb-sm">Enable data-sharing permission
                step for this study? (This will let participants choose whether
                they want to allow their data to be shared with 3rd parties)
              </div>
              <div class="col-md-12 pl-none">
                <div class="form-group custom-form">
                  <span class="radio radio-info radio-inline p-45"><input
                      type="radio" id="shareDataPermissionsYes" value="Yes"
                      name="shareDataPermissions"
                    ${consentBo.shareDataPermissions eq 'Yes' ? 'checked' : ''}>
                    <label for="shareDataPermissionsYes">Yes</label>
                  </span>
                  <span class="radio radio-inline"><input type="radio"
                                                          id="shareDataPermissionsNo"
                                                          value="No"
                                                          name="shareDataPermissions"
                    ${empty consentBo.shareDataPermissions || consentBo.shareDataPermissions eq 'No' ? 'checked' : ''}>
                    <label for="shareDataPermissionsNo">No</label>
                  </span>
                </div>
              </div>
              <div
                  class="<c:if test="${consentBo.shareDataPermissions eq 'No'}">ct_panel</c:if>"
                  id="rootContainer">
                <div class="col-md-12 p-none">
                  <div class="gray-xs-f mb-xs">
                    Screen Title
                    <small>(250 characters max)</small>
                    <span
                        class="requiredStar">*
                    </span>
                    E.g. Sharing Options
                  </div>
                  <div class="form-group custom-form">
                    <input type="text" class="form-control requiredClass"
                           placeholde="" id="titleId" name="title"
                           value="${consentBo.title}" maxlength="250"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-md-12 p-none">
                  <div class="gray-xs-f mb-xs">
                    Brief description
                    <small>(500 characters max)</small>
                    <span
                        class="requiredStar">*
                    </span>
                    E.g. &lt;Institution&gt; and
                    its partners will receive your study data from your
                    participation in the study. Please indicate if you permit to
                    share your data more broadly to other parties for research.
                  </div>
                  <div class="form-group custom-form">
                    <input type="text" class="form-control requiredClass"
                           placeholder="" maxlength="250" name="taglineDescription"
                           id="taglineDescriptionId"
                           value="${consentBo.taglineDescription}"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-md-12 p-none">
                  <div class="gray-xs-f mb-xs">
                    Option 1: Share my data with &lt;institution&gt; and qualified
                    researchers worldwide.
                    <small>(250 characters max)</small>
                    <span
                        class="requiredStar">*
                    </span>
                  </div>
                  <div class="form-group custom-form">
                    <input type="text" class="form-control requiredClass"
                           placeholder="" maxlength="250" name="shortDescription"
                           id="shortDescriptionId" value="${consentBo.shortDescription}"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-md-12 p-none">
                  <div class="gray-xs-f mb-xs">
                    Option 2: Only share my data with &lt;institution and its
                    partners&gt;
                    <small>(500 characters max)</small>
                    <span
                        class="requiredStar">*
                    </span>
                  </div>
                  <div class="form-group custom-form">
                    <textarea class="form-control requiredClass" rows="5"
                              maxlength="500" placeholder="" name="longDescription"
                              id="longDescriptionId">${consentBo.longDescription}</textarea>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-md-12 p-none">
                  <div class="gray-xs-f mb-xs">
                    Explanatory text that can be provided in a 'Learn More'
                    section
                    <span class="requiredStar">*</span>
                  </div>
                  <div class="form-group">
                    <textarea id="learnMoreTextId" name="learnMoreText"
                              required>${consentBo.learnMoreText}</textarea>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </div>
                <div class="col-md-12 pl-none mt-lg mb-xlg">
                  <a class="preview__text" href="javascript:void()"
                     data-toggle="modal" onclick="previewDataSharing();"><img
                      class="mr-xs" src="../images/icons/eye-icn.png" alt="">
                    <span>See Screen Preview</span>
                  </a>
                </div>
              </div>
            </div>
            <div class="clearfix"></div>


          </div>
          <div id="menu2" class="tab-pane fade">
            <div class="mt-lg">
              <div class="gray-xs-f mb-sm">Select a method of creation
                for the consent document:
              </div>
              <div class="form-group mb-none">
                <div id="consentDocTypeDivId">
                  <span class="radio radio-info radio-inline p-45"><input
                      type="radio" id="inlineRadio1" value="Auto"
                      name="consentDocType" required
                      data-error="Please choose consent document type"
                    ${consentBo.consentDocType=='Auto'?'checked':''}> <label
                      for="inlineRadio1">Automatic creation</label>
                  </span>
                  <span class="radio radio-inline"><input type="radio"
                                                          id="inlineRadio2" value="New"
                                                          name="consentDocType" required
                                                          data-error="Please choose consent document type"
                    ${consentBo.consentDocType=='New'?'checked':''}> <label
                      for="inlineRadio2">Manual content entry</label>
                  </span>
                  <div class="help-block with-errors red-txt"></div>
                </div>
              </div>
            </div>
            <div class="italic-txt">
              <div id="autoCreateHelpTextDiv" style="display: block;">
                <small class="inst">This is a preview of the Consent
                  Document to depict how it gets created by the ResearchKit /
                  ResearchStack frameworks on the mobile app. Consent Items
                  (title and long description portions) are concatenated to
                  automatically create the Consent Document. The mobile app also
                  generates a Consent Document PDF with participant first name,
                  last name, signature and date, time of providing consent, as
                  captured on the app.
                </small>
              </div>
              <div id="newDocumentHelpTextDiv" style="display: none;">
                <small class="inst">Choose this option if you wish to
                  provide separate content for the Consent Document instead of
                  using the auto-generated Consent Document. Note that in this
                  case, the mobile app will not be able to add user-specific
                  details such as first name, last name, signature and date/time
                  of providing consent, to the PDF that it generates for the
                  Consent Document.
                </small>
              </div>
            </div>
            <div class="mt-xlg">
              <div class="blue-lg-f text-uppercase">
                CONSENT DOCUMENT
                <span id="requiredStarId" class="requiredStar">*</span>
              </div>
              <div class="mt-lg">
                <div class="cont_doc" id="autoCreateDivId"
                     style="display: block;">
                  <div style="height: 900px;">
                    <div id="autoConsentDocumentDivId"></div>
                  </div>
                </div>
                <div class="cont_editor">
                  <div id="newDivId" style="display: none;">
                    <div class="form-group ">
                      <textarea class="" rows="8" id="newDocumentDivId"
                                name="newDocumentDivId">${consentBo.consentDocContent}</textarea>
                      <div class="help-block with-errors red-txt"></div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="mt-xlg dis-inline" id="autoCreateDivId01"
                 style="display: block;">
              <div class="sign">Participant's First Name</div>
              <div class="sign">Last Name</div>
              <div class="sign">Signature</div>
              <div class="sign">Date</div>
              <div class="sign">Time</div>
            </div>
          </div>
          <div id="menu3" class="tab-pane fade">
            <div class="mt-xlg text-weight-semibold">The mobile app
              captures the following from the user as part of the e-Consent
              process for the study in the app:
            </div>
            <div class="mt-lg">
              <ul class="list-style-image">
                <li>Explicit confirmation of consent document review and
                  agreement
                  <small>(250 characters max)</small>
                  <span
                      class="requiredStar">*
                  </span>
                  <span
                      class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
                      title="Text message shown to the prospect participant on the app, to confirm Review of and Agreement to the Consent Document."></span>
                  <div class="form-group mt-sm mb-none">
                    <input type="text" class="form-control" placeholder=""
                           required name="aggrementOfTheConsent"
                           id="aggrementOfTheConsentId"
                           value="${consentBo.aggrementOfTheConsent}" maxlength="250"/>
                    <div class="help-block with-errors red-txt"></div>
                  </div>
                </li>

                <li>First Name</li>
                <li>Last Name</li>
                <li>E-signature</li>
                <li>Date and Time of providing Consent</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- End body tab section -->

    <!-- End right Content here -->
  </form:form>
  <div class="modal fade" id="myModal" tabindex="-1" role="dialog"
       aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-sm">
      <div class="">
        <div class="pp__img__container">
          <img src="../images/preview_phone.png" alt="Norway"
               style="width: 100%;">
          <div class="pp__top">
            <div id="cancelButtonId" class="pl-lg pr-lg"
                 style="display: none;">
              <button class="float__right cancel__close mb-sm"
                      data-dismiss="modal">Cancel
              </button>
            </div>
            <div id="doneButtonId" class="pl-lg pr-lg" style="display: none;">
              <button class="float__right cancel__close"
                      onclick="previewDataSharing();">Done
              </button>
            </div>
            <div class="clearfix"></div>
            <div class="pl-md pr-md">
              <div id="wrapper__">
                <div class="scrollbar__" id="style-2">
                  <div class="force-overflow__">
                    <!--1st modal Start -->
                    <div class="pp__title" id="titleModalId">- NA -</div>
                    <div class="pp__tagline" id="tagLineDescriptionModalId">-
                      NA -
                    </div>
                    <div class="pp__learnmore">
                      <a href="javascript:void(0)" data-toggle="modal"
                         onclick="previewLearnMore();">Learn more
                      </a>
                    </div>
                    <div class="pp__ul mt-xlg">
                      <div class="panel-group overview-panel" id="accordion">
                        <div class="panel panel-default">
                          <div class="panel-heading">
                            <div class="panel-title">
                              <a data-toggle="collapse" data-parent="#accordion"
                                 href="#collapse1" aria-expanded="true">
                                <div class="text-left dis-inline pull-left">
                                  <span class="ellipsis__">dis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis
                                    dis-ellipsis dis-ellipsis dis-ellipsis
                                  </span>
                                </div>
                                <div class="text-right dis-inline pull-right">
                                  <span class="glyphicon glyphicon-chevron-right"></span>
                                </div>
                                <div class="clearfix"></div>
                              </a>
                            </div>
                          </div>
                          <div id="collapse1" class="panel-collapse collapse">
                            <div class="panel-body">kfjdf;ljhdlfhjd;lhjb
                              dskfdsjfnhslkdnghlkdsfglkd bfdskjfbkjd
                            </div>
                          </div>
                        </div>
                      </div>
                      <div class="panel-group overview-panel" id="accordion1">
                        <div class="panel panel-default">
                          <div class="panel-heading">
                            <div class="panel-title">
                              <a data-toggle="collapse" data-parent="#accordion1"
                                 href="#collapse2" aria-expanded="true">
                                <div class="text-left dis-inline pull-left">
                                  <span class="ellipsis__">ronalin sahoo
                                    ejrerhewuirew ronalinefewf
                                  </span>
                                </div>
                                <div class="text-right dis-inline pull-right">
                                  <span class="glyphicon glyphicon-chevron-right"></span>
                                </div>
                                <div class="clearfix"></div>
                              </a>
                            </div>
                          </div>
                          <div id="collapse2" class="panel-collapse collapse">
                            <div class="panel-body">kfjdf;ljhdlfhjd;lhjb
                              dskfdsjfnhslkdnghlkdsfglkd bfdskjfbkjd
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <!-- 1st Modal  End-->
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- End right Content here -->
<script type="text/javascript">
  $(document).ready(function () {
    //check the type of page action(view/edit)
    newLearnMoreConsentDocument();
    if ('${permission}' == 'view') {
      $('input[name="consentDocType"]').attr('disabled', 'disabled');
      $('#consentReviewFormId input').prop('disabled', true);
      $('#longDescriptionId').prop('disabled', true);
      $('#newDivId .elaborateClass').addClass('linkDis');
      $('#saveId,#doneId').hide();
    }

    //auto select if consent Id is empty

    var consentId = "${consentBo.consentDocType}";
    if (consentId == null || consentId == '' || typeof consentId === 'undefined') {
      if (null != "${consentInfoList}" && "${consentInfoList}" != '' && "${consentInfoList}"
          !== undefined) {
        $("#inlineRadio1").attr('checked', true);
        $("#version").val('1.0');
      } else {
        $("#inlineRadio2").attr('checked', true);
      }
    }

    //active li
    $(".menuNav li").removeClass('active');
    $(".fifthConsentReview").addClass('active');
    $("#createStudyId").show();
    consentDocumentDivType();
    //check the consent type
    $("#consentDocTypeDivId").on('change', function () {
      consentDocumentDivType();
    });
    var shareDataPermissions = '${consentBo.shareDataPermissions}';
    resetValues(shareDataPermissions);
    $('input[name="shareDataPermissions"]').change(function () {
      var shareDataPermissions = '${consentBo.shareDataPermissions}';
      var value = $(this).val();
      if (value == 'Yes') {
        $('#rootContainer input').attr('required', true);
        $('#learnMoreTextId').attr('required', true);
        $('.requiredClass').attr('required', true);
        $('#longDescriptionId').attr('required', true);
        newLearnMoreConsentDocument();
        $("#rootContainer").show();
      } else {
        $('#rootContainer input').attr('required', false);
        $('#longDescriptionId').attr('required', false);
        $('.requiredClass').attr('required', false);
        $('#learnMoreTextId').attr('required', false);
        $("#rootContainer").hide();
      }
    });
    //go back to consentList page
    $("#saveId,#doneId").on('click', function () {
      var id = this.id;
      var valid = true;
      if ($("#typeOfCensent").val() == "New") {
        valid = maxLenValEditor();
      }
      if (valid) {
        if (id == "saveId") {
          $("#consentReviewFormId").parents("form").validator("destroy");
          $("#consentReviewFormId").parents("form").validator();
          saveConsentReviewAndEConsentInfo("saveId");
        } else if (id == "doneId") {
          var isvalid = true;
          var retainTxt = '${studyBo.retainParticipant}';
          if ($('#shareDataPermissionsYes').is(":checked")) {
            isvalid = maxLenLearnMoreEditor();
          }
          isFromValid("#consentReviewFormId");
          if ($('.custom-form').find('.has-error.has-danger').length < 1 && $('#menu3').find(
              '.has-error.has-danger').length < 1 && isvalid) {
            var message = "";
            var alertType = "";
            if (retainTxt != null && retainTxt != '' && typeof retainTxt != 'undefined') {
              if (retainTxt == 'Yes') {
                alertType = "retained";
              } else if (retainTxt == 'No') {
                alertType = "deleted";
              } else {
                alertType = "retained or deleted as per participant choice";
              }
              message = "You have a setting that needs study data to be " + alertType
                  + " if the participant withdraws from the study. Please ensure you have worded Consent Terms in accordance with this. Click OK to proceed with completing this section or Cancel if you wish to make changes.";
            }
            bootbox.confirm({
              closeButton: false,
              message: message,
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
                  var consentDocumentType = $('input[name="consentDocType"]:checked').val();
                  if (consentDocumentType == "Auto") {
                    saveConsentReviewAndEConsentInfo("doneId");
                  } else {
                    var content = $('#newDocumentDivId').summernote('code');
                    if (content != null && content != '' && typeof content != 'undefined' && content
                        != '<p><br></p>') {
                      saveConsentReviewAndEConsentInfo("doneId");
                    } else {
                      $("#newDocumentDivId").parent().find(".help-block").empty();
                      $("#newDocumentDivId").parent().addClass('has-danger has-error').find(
                          ".help-block").append(
                          '<ul class="list-unstyled"><li>Please fill out this field.</li></ul>');
                    }
                  }

                } else {
                  $("#doneId").prop('disabled', false);
                }
              }
            });
          } else {
            var slaCount = $('.custom-form').find('.has-error.has-danger').length;
            var qlaCount = $('#menu2').find('.has-error.has-danger').length;
            var rlaCount = $('#menu3').find('.has-error.has-danger').length;
            if (parseInt(slaCount) >= 1 || $('#learnMoreTextId').parent().find(".help-block").html()
                != '') {
              $('.shareData a').tab('show');
            } else if (parseInt(qlaCount) >= 1 || $('#newDocumentDivId').parent().find(
                ".help-block").html() != '') {
              $('.consentReview a').tab('show');
            } else if (parseInt(rlaCount) >= 1) {
              $('.econsentForm a').tab('show');
            }
          }
        }
      } else {
        var slaCount = $('.custom-form').find('.has-error.has-danger').length;
        var qlaCount = $('#menu2').find('.has-error.has-danger').length;
        var rlaCount = $('#menu3').find('.has-error.has-danger').length;
        if (parseInt(slaCount) >= 1) {
          $('.shareData a').tab('show');
        } else if (parseInt(qlaCount) >= 1 || $('#newDocumentDivId').parent().find(
            ".help-block").html() != '') {
          $('.consentReview a').tab('show');
        } else if (parseInt(rlaCount) >= 1) {
          $('.econsentForm a').tab('show');
        }
      }
    });

    function resetValues(shareDataPermissions) {
      if (shareDataPermissions == '' || shareDataPermissions == 'No') {
        $('#rootContainer input').val('');
        $('#allowWithoutPermissionYes').val("Yes");
        $('#allowWithoutPermissionNo').val("No");
        $('#allowWithoutPermissionYes').prop("checked", true);
        $('#learnMoreTextId').summernote('reset');
        $('#learnMoreTextId').attr("required", false);
        $('#longDescriptionId').val('');
        $('.requiredClass').attr('required', false);
        $("#rootContainer").hide();
      } else {
        $('.requiredClass').attr('required', true);
        $('#learnMoreTextId').attr('required', true);
      }
    }

    //consent doc type div
    function consentDocumentDivType() {

      if ($("#inlineRadio1").is(":checked")) {
        $("#autoCreateDivId").show();
        $("#autoCreateDivId01").show();
        $('#newDocumentDivId').attr("required", false);
        $("#newDivId").hide();
        $("#typeOfCensent").val("Auto");
        $("#autoCreateHelpTextDiv").show();
        $("#newDocumentHelpTextDiv").hide();
        $('#requiredStarId').hide();
        autoCreateConsentDocument();
      } else {
        $("#newDivId").show();
        $("#autoCreateDivId").hide();
        $("#autoCreateDivId01").hide();
        $("#typeOfCensent").val("New");
        $("#autoCreateHelpTextDiv").hide();
        $("#newDocumentHelpTextDiv").show();
        $('#requiredStarId').show();
        $('#newDocumentDivId').attr("required", true);
        createNewConsentDocument();
      }
    }

    //check the consentinfo list
    function autoCreateConsentDocument() {
      var consentDocumentDivContent = "";
      $("#autoConsentDocumentDivId").empty();
      if (null != "${consentInfoList}" && "${consentInfoList}" != '' && "${consentInfoList}"
          !== undefined) {
        if ($("#inlineRadio1").is(":checked")) {
          <c:forEach items="${consentInfoList}" varStatus="i" var="consentInfo">
          consentDocumentDivContent += "<span style='font-size:18px;'><strong>"
              + "${consentInfo.displayTitle}"
              + "</strong></span><br/>"
              + "<span style='display: block; overflow-wrap: break-word; width: 100%;'>"
              + "${consentInfo.elaborated}"
              + "</span><br/>";
          </c:forEach>
        }
      }
      $("#autoConsentDocumentDivId").append(consentDocumentDivContent);
      $("#newDocumentDivId").val('');

    }

    function createNewConsentDocument() {
      $('#newDocumentDivId').summernote({
        placeholder: '',
        tabsize: 2,
        height: 200,
        toolbar: [
          ['font', ['bold', 'italic']],
          ['para', ['paragraph', 'ul', 'ol']],
          ['font', ['underline']],
          ['insert', ['link']],
          ['hr'],
          ['clear'],
          ['cut'],
          ['undo'],
          ['redo'],
          ['fontname', ['fontname']],
          ['fontsize', ['fontsize']],
        ]
      });
      <c:if test="${permission eq 'view'}">
      $('#newDocumentDivId').summernote('disable');
      </c:if>

    }

    function newLearnMoreConsentDocument() {
      $('#learnMoreTextId').summernote({
        placeholder: '',
        tabsize: 2,
        height: 200,
        toolbar: [
          ['font', ['bold', 'italic']],
          ['para', ['paragraph', 'ul', 'ol']],
          ['font', ['underline']],
          ['insert', ['link']],
          ['hr'],
          ['clear'],
          ['cut'],
          ['undo'],
          ['redo'],
          ['fontname', ['fontname']],
          ['fontsize', ['fontsize']],
        ],
      });
      <c:if test="${permission eq 'view'}">
      $('#learnMoreTextId').summernote('disable');
      </c:if>
    }

    //save review and E-consent data
    function saveConsentReviewAndEConsentInfo(item) {
      var consentInfo = new Object();
      var consentId = $("#consentId").val();
      var studyId = $("#studyId").val();
      var agreementCB = $("#agreementCB").val();
      var fNameCB = $("#fNameCB").val();
      var lNameCB = $("#lNameCB").val();
      var eSignCB = $("#eSignCB").val();
      var dateTimeCB = $("#dateTimeCB").val();
      var consentDocumentContent = "";
      var consentDocType = $('input[name="consentDocType"]:checked').val();

      var shareDataPermissionsTxt = $('input[name="shareDataPermissions"]:checked').val();
      var title_txt = $("#titleId").val();
      var tagline_description = $("#taglineDescriptionId").val();
      var short_description = $("#shortDescriptionId").val();
      var long_description = $("#longDescriptionId").val();
      var learn_more_text = $('#learnMoreTextId').summernote('code');
      learn_more_text = replaceSpecialCharacters(learn_more_text);
      var allow_Permission = $('input[name="allowWithoutPermission"]:checked').val();
      var aggrement_of_theconsent = $("#aggrementOfTheConsentId").val();

      if (consentDocType == "New") {
        consentDocumentContent = $('#newDocumentDivId').summernote('code');
        consentDocumentContent = replaceSpecialCharacters(consentDocumentContent);
      }

      if (item == "doneId") {
        consentInfo.type = "completed";
      } else {
        consentInfo.type = "save";
      }
      if (null != consentId) {
        consentInfo.id = consentId;
      }
      if (null != studyId) {
        consentInfo.studyId = studyId;
      }
      if (null != consentDocType) {
        consentInfo.consentDocType = consentDocType;
      }
      if (null != consentDocumentContent) {
        consentInfo.consentDocContent = consentDocumentContent;
      }
      if (null != agreementCB) {
        consentInfo.eConsentAgree = agreementCB;
      }
      if (null != fNameCB) {
        consentInfo.eConsentFirstName = fNameCB;
      }
      if (null != lNameCB) {
        consentInfo.eConsentLastName = lNameCB;
      }
      if (null != eSignCB) {
        consentInfo.eConsentSignature = eSignCB;
      }
      if (null != dateTimeCB) {
        consentInfo.eConsentDatetime = dateTimeCB;
      }

      if (null != shareDataPermissionsTxt) {
        consentInfo.shareDataPermissions = shareDataPermissionsTxt;
      }
      if (null != title_txt) {
        consentInfo.title = title_txt;
      }
      if (null != tagline_description) {
        consentInfo.taglineDescription = tagline_description;
      }
      if (null != short_description) {
        consentInfo.shortDescription = short_description;
      }
      if (null != long_description) {
        consentInfo.longDescription = long_description;
      }
      if (null != learn_more_text) {
        consentInfo.learnMoreText = learn_more_text;
      }
      if (null != allow_Permission) {
        consentInfo.allowWithoutPermission = allow_Permission;
      }
      if (null != aggrement_of_theconsent) {
        consentInfo.aggrementOfTheConsent = aggrement_of_theconsent;
      }
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
          var jsonobj = eval(data);
          var message = jsonobj.message;
          $("#alertMsg").html('');
          if (message == "SUCCESS") {
            var consentId = jsonobj.consentId;
            var studyId = jsonobj.studyId;
            $("#consentId").val(consentId);
            $("#studyId").val(studyId);
            var consentDocumentType = $('input[name="consentDocType"]:checked').val();
            $("#newDocumentDivId").val('');
            if (consentDocumentType == "New") {
              $("#newDocumentDivId").val(consentDocumentContent);
              $('#newDocumentDivId').summernote('');
              $('#newDocumentDivId').summernote('consentDocumentContent');
            }
            if (item == "doneId") {
              var a = document.createElement('a');
              a.href = "/studybuilder/adminStudies/consentReviewMarkAsCompleted.do?_S=${param._S}";
              document.body.appendChild(a).click();
            } else {
              $("#alertMsg").removeClass('e-box').addClass('s-box').html("Content saved as draft.");
              $(item).prop('disabled', false);
              $('#alertMsg').show();
              if ($('.fifthConsentReview').find('span').hasClass(
                  'sprites-icons-2 tick pull-right mt-xs')) {
                $('.fifthConsentReview').find('span').removeClass(
                    'sprites-icons-2 tick pull-right mt-xs');
              }
            }
          } else {
            $("#alertMsg").removeClass('s-box').addClass('e-box').html("Something went Wrong");
            $('#alertMsg').show();
          }
          setTimeout(hideDisplayMessage, 4000);
        },
        global: false
      });
    }
  });

  function goToBackPage(item) {
    <c:if test="${permission ne 'view'}">
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
          a.href = "/studybuilder/adminStudies/consentListPage.do?_S=${param._S}";
          document.body.appendChild(a).click();
        } else {
          $(item).prop('disabled', false);
        }
      }
    });
    </c:if>
    <c:if test="${permission eq 'view'}">
    var a = document.createElement('a');
    a.href = "/studybuilder/adminStudies/consentListPage.do?_S=${param._S}";
    document.body.appendChild(a).click();
    </c:if>
  }

  function maxLenValEditor() {
    var isValideditor = true;
    var valueEditor = $('#newDocumentDivId').summernote('code');
    if (valueEditor !== "<p><br></p>") {
      if (valueEditor != '' && $.trim(valueEditor.replace(/(<([^>]+)>)/ig, "")).length > 70000) {
        if (isValideditor) {
          isValideditor = false;
        }
        $('#newDocumentDivId').parent().addClass('has-danger has-error').find(
            ".help-block").empty().append(
            '<ul class="list-unstyled"><li>Maximum 70000 characters are allowed.</li></ul>');
      } else {
        $('#newDocumentDivId').parent().removeClass("has-danger").removeClass("has-error");
        $('#newDocumentDivId').parent().find(".help-block").html("");
      }
    } else {
      isValideditor = false;
      $('#newDocumentDivId').attr('required', true);
      $('#newDocumentDivId').parent().addClass('has-danger has-error').find(
          ".help-block").empty().append(
          '<ul class="list-unstyled"><li>Please fill out this field.</li></ul>');

    }

    return isValideditor;
  }

  function maxLenLearnMoreEditor() {
    var isValid = true;
    var value = $('#learnMoreTextId').summernote('code');
    if (value != '<p><br></p>') {
      if (value != '' && $.trim(value.replace(/(<([^>]+)>)/ig, "")).length > 70000) {
        if (isValid) {
          isValid = false;
        }
        $('#learnMoreTextId').parent().addClass('has-danger has-error').find(
            ".help-block").empty().append(
            '<ul class="list-unstyled"><li>Maximum 70000 characters are allowed.</li></ul>');
      } else {
        $('#learnMoreTextId').parent().removeClass("has-danger").removeClass("has-error");
        $('#learnMoreTextId').parent().find(".help-block").html("");
      }
    } else {
      isValid = false;
      $('#learnMoreTextId').attr('required', true);
      $('#learnMoreTextId').parent().addClass('has-danger has-error').find(
          ".help-block").empty().append(
          '<ul class="list-unstyled"><li>Please fill out this field.</li></ul>');

    }

    return isValid;
  }

  function previewDataSharing() {
    var titleText = $("#titleId").val();
    var tagline_description = $("#taglineDescriptionId").val();
    var short_description = $("#shortDescriptionId").val();
    var long_descriptionId = $("#longDescriptionId").val();
    $('.force-overflow__').html('');
    var data = '<div class="pp__title" id="titleModalId">';
    if (titleText != '' && titleText != null && typeof titleText != 'undefined') {
      data += titleText + '</div>';
    } else {
      data += ' -NA-</div>';
    }
    data += '<div class="pp__tagline" id="tagLineDescriptionModalId">';
    if (tagline_description != '' && tagline_description != null && typeof tagline_description
        != 'undefined') {
      data += tagline_description + '</div>';
    } else {
      data += ' -NA-</div>';
    }

    data += '<div class="pp__learnMore"><a href="javascript:void(0)" data-toggle="modal" onclick="previewLearnMore();">Learn more</a>'
        + '</div>'
        + '<div class="pp__ul mt-xlg">';
    if (short_description != '' && short_description != null && typeof short_description
        != 'undefined') {
      data += '<div class="panel-group overview-panel" id="accordion">'
          + '<div class="panel panel-default">'
          + '<div class="panel-heading">'
          + '<div class="panel-title" style="font-weight: bold;">'
          + '<a data-toggle="collapse" data-parent="#accordion" href="#collapse1" aria-expanded="true">'
          + '<div class="text-left dis-inline pull-left">'
          + '<span class="ellipsis__">' + 'Share my data with ' + short_description
          + ' and qualified researchers worldwide' + '</span>'
          + '</div>'
          + '<div class="text-right dis-inline pull-right"><span class="glyphicon glyphicon-chevron-right"></span>'
          + '</div><div class="clearfix"></div></a></div></div>'
          + '<div id="collapse1" class="panel-collapse collapse"><div class="panel-body">'
          + 'Share my data with ' + short_description + ' and qualified researchers worldwide'
          + '</div></div></div></div>';

    } else {
      data += '<ul class=""><li id="shortDescriptionModalId" style="font-weight: bold;"> - NA - </li></ul>';
    }

    if (long_descriptionId != '' && long_descriptionId != null && typeof long_descriptionId
        != 'undefined') {
      data += '<div class="panel-group overview-panel" id="accordion1">'
          + '<div class="panel panel-default">'
          + '<div class="panel-heading">'
          + '<div class="panel-title" style="font-weight: bold;">'
          + '<a data-toggle="collapse" data-parent="#accordion1" href="#collapse2" aria-expanded="true">'
          + '<div class="text-left dis-inline pull-left">'
          + '<span class="ellipsis__">' + 'Only share my data with ' + long_descriptionId
          + '</span>'
          + '</div>'
          + '<div class="text-right dis-inline pull-right"><span class="glyphicon glyphicon-chevron-right"></span>'
          + '</div><div class="clearfix"></div></a></div></div>'
          + '<div id="collapse2" class="panel-collapse collapse"><div class="panel-body">'
          + 'Only share my data with ' + long_descriptionId + '</div></div></div></div>';
    } else {
      data += '<ul class=""><li id="longDescriptionModalId" style="font-weight: bold;"> - NA - </li></ul>';
    }
    data += '</div>';

    $('.force-overflow__').html(data);
    $('.scrollbar__').scrollTop(0);
    colapseUpAndDown();
    $('#cancelButtonId').show();
    $('#doneButtonId').hide();
    $("#myModal").modal('show');
  }

  function previewLearnMore() {
    $('#cancelButtonId').hide();
    $('#doneButtonId').show();
    $('.force-overflow__').html('');
    var learn_more_desc = $('learnMoreTextId').summernote('code');
    var data = '<div class="pp__title">Learn more</div>'
        + '<div class="pp__ul mt-xlg">';
    if (learn_more_desc != ' ' && learn_more_desc != '' && learn_more_desc != null
        && typeof learn_more_desc != 'undefined') {
      data += '<div class="panel-group overview-panel" id="accordion1">'
          + '<div class="panel panel-default">'
          + '<div class="panel-heading">'
          + '<div class="panel-title" style="font-weight: bold;">'
          + '<a data-toggle="collapse" data-parent="#accordion1" href="#collapse2" aria-expanded="true">'
          + '<div class="text-left dis-inline pull-left">'
          + '<span class="ellipsis__">' + learn_more_desc + '</span>'
          + '</div>'
          + '<div class="text-right dis-inline pull-right"><span class="glyphicon glyphicon-chevron-right"></span>'
          + '</div><div class="clearfix"></div></a></div></div>'
          + '<div id="collapse2" class="panel-collapse collapse"><div class="panel-body">'
          + learn_more_desc + '</div></div></div></div>';
    } else {
      data += '<ul class=""><li id="learnMoreDescId" style="font-weight: bold;"> - NA - </li></ul>';
    }
    data += '</div>';
    $('.force-overflow__').html(data);
    $('.scrollbar__').scrollTop(0);
    colapseUpAndDown();
  }

  $(document).on('show.bs.collapse', '.collapse', function () {
    $('.collapse').not(this).collapse('hide').removeClass('in');
  });

  function colapseUpAndDown() {
    $('.collapse').on('shown.bs.collapse', function () {
      $(this).parent().find(".glyphicon-chevron-right").removeClass(
          "glyphicon-chevron-right").addClass("glyphicon-chevron-down");
    }).on('hidden.bs.collapse', function () {
      $(this).parent().find(".glyphicon-chevron-down").removeClass(
          "glyphicon-chevron-down").addClass("glyphicon-chevron-right");
    });
  }
</script>