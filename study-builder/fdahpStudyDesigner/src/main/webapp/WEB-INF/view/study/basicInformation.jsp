<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<style>
.uploadImg{
margin-top:16px !important;
}
</style>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->

      
<div class="col-sm-10 col-rc white-bg p-none">
  <form:form
      action="/studybuilder/adminStudies/saveOrUpdateBasicInfo.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}"
      data-toggle="validator" role="form" id="basicInfoFormId" method="post"
      autocomplete="off" enctype="multipart/form-data">
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          Study Information
          <c:set var="isLive">${_S}isLive</c:set>
            ${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}
        </div>


        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
        </div>
        <c:if test="${empty permission}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn actBut"
                    id="saveId"
                    <c:if test="${not studyBo.viewPermission }">disabled</c:if>>Save
            </button>
          </div>

          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn actBut"
                    id="completedId"
                    <c:if test="${not studyBo.viewPermission }">disabled</c:if>>Mark
              as completed
            </button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->
    <input type="hidden" id="sId" value="${studyBo.id}" name="id"/>
    <input type="hidden" value="" id="buttonText" name="buttonText">
    <!-- Start body tab section -->
    <div class="right-content-body col-xs-12">

      <div class="col-md-12 p-none">
        <div class="col-md-6 pl-none">
          <div class="gray-xs-f mb-xs">
            Study ID
            <small>(15 characters max)</small>
            <span
                class="requiredStar"> *
            </span>
            <span class="filled-tooltip"
                    data-toggle="tooltip" data-placement="top"
                    data-html="true"
                    title="<span class='font24 text-weight-light pull-left'></span> A unique human-readable platform-wide identifier for the study">
            </span>
          </div>
          <div class="form-group customStudyClass">
            <input type="text" custAttType="cust" autofocus="autofocus"
                   class="form-control aq-inp studyIdCls" name="customStudyId"
                   id="customStudyId" maxlength="15"
                   value="${studyBo.customStudyId}"
                <c:if
                    test="${not empty studyBo.status && (studyBo.status == 'Active' || studyBo.status == 'Published' || studyBo.status == 'Paused' || studyBo.status == 'Deactivated')}"> disabled</c:if>
                   required data-error="Please fill out this field"/>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
        <div class="col-md-6 pr-none">
          <div class="gray-xs-f mb-xs">
            App ID
            <small>(15 characters max)</small>
            <span
                class="requiredStar"> *
            </span>
            <span>
              <span
                  data-toggle="tooltip" data-placement="top"
                  title="Enter a unique human-readable identifier corresponding to the app that this study must belong to."
                  class="filled-tooltip"></span>
            </span>
          </div>
          <div class="form-group">
            <input type="text" custAttType="cust" autofocus="autofocus"
                   class="form-control aq-inp appIdCls" name="appId" id="appId"
                   maxlength="15" value="${studyBo.appId}"
                <c:if
                    test="${not empty studyBo.status && (studyBo.status == 'Active' || studyBo.status == 'Published' || studyBo.status == 'Paused' || studyBo.status == 'Deactivated')}"> disabled</c:if>
                   required data-error="Please fill out this field"/>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
      </div>
      <!-- phase2a-sp1 -->
      <div class="col-md-12 p-none">
        <div class="col-md-6 pl-none">
          <div class="gray-xs-f mb-xs">
            Study name
            <small>(50 characters max)</small>
            <span
                class="requiredStar"> *
            </span>
            <span class="filled-tooltip"
                    data-toggle="tooltip" data-placement="top"
                    data-html="true"
                    title="<span class='font24 text-weight-light pull-left'></span> A short study name for display in the mobile app">
            </span>
          </div>
          <div class="form-group">
             <input type="text" class="form-control" name="name"
                   id="customStudyName" value="${fn:escapeXml(studyBo.name)}"
                   maxlength="50" required data-error="Please fill out this field"/>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>

        <div class="col-md-6 pr-none">
          <div class="gray-xs-f mb-xs">
            Study website
            <small>(100
              characters max)
            </small>   
           </div>
          <div class="form-group">
            <input type="text" class="form-control" id="studyWebsiteId"
                   name="studyWebsite" value="${studyBo.studyWebsite}"
                   pattern="^(http:\/\/|https:\/\/)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
                   title="Include http://" maxlength="100"
                   data-pattern-error="Please enter a valid URL" data-error="Please fill out this field"/>
            <div class="help-block with-errors red-txt"></div>
          </div> 
        </div>
      </div>
      <!--phase2a sp1-->

      <div class="col-md-12 p-none">
        <div class="gray-xs-f mb-xs">
          Descriptive study name
          <small>(150 characters max)</small>
          <span
              class="requiredStar"> *
          </span>
        </div>
        <div class="form-group">
          <input type="text" class="form-control" name="fullName"
                 value="${fn:escapeXml(studyBo.fullName)}" maxlength="150" required data-error="Please fill out this field"/>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>

      
      <div class="col-md-12 p-none">
        <div class="col-md-6 pl-none">
          <div class="gray-xs-f mb-xs">
            Study type
            <span class="requiredStar"> *</span>
            <span class="filled-tooltip"
                    data-toggle="tooltip" data-placement="top"
                    data-html="true"
                    title="<span class='font24 text-weight-light pull-left'></span> Specify if the study should be part of a 'gateway' app that can house multiple other studies or a 'standalone' app that has just this study. The app ID entered above should belong to an app that matches the selected type.">
            </span>
          </div>
          <div class="form-group">
            <span class="radio radio-info radio-inline p-45"><input
                type="radio" id="inlineRadio5"
                class="rejoin_radio studyTypeClass" name="type" value="GT"
              ${studyBo.type eq 'GT'?'checked':""} required data-error="Please fill out this field"
            <c:if
                test="${not empty studyBo.status && (studyBo.status == 'Active' || studyBo.status == 'Published' || studyBo.status == 'Paused' || studyBo.status == 'Deactivated')}">
                disabled </c:if>>
              <label for="inlineRadio5">Gateway</label>
            </span>
            <span class="radio radio-inline"><input type="radio"
                                                    id="inlineRadio6"
                                                    class="rejoin_radio studyTypeClass"
                                                    name="type"
                                                    value="SD" ${studyBo.type eq 'SD'?'checked':""}
                                                    required data-error="Please fill out this field"
            <c:if
                test="${not empty studyBo.status && (studyBo.status == 'Active' || studyBo.status == 'Published' || studyBo.status == 'Paused' || studyBo.status == 'Deactivated')}">
                                                    disabled </c:if>>
              <label for="inlineRadio6">Standalone</label>
            </span>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
        <div class="col-md-6 pr-none">
          <div class="gray-xs-f mb-xs">
            Support email (for participants)
            <small>(100
              characters max)
            </small>
            <span class="requiredStar"> *</span>
          </div>
          <div class="form-group">
            <input type="text" class="form-control" name="inboxEmailAddress"
                   value="${studyBo.inboxEmailAddress}" required maxlength="100"
                   pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$"
                   autocomplete="off" data-pattern-error="Email address is invalid" data-error="Please fill out this field"/>
            <div class="help-block with-errors red-txt"></div>
          </div>
        </div>
      </div>
		<div class="col-md-12 p-none mb-xxlg">
          <div class="col-md-6 pr-none thumbImageDIv" style="margin-left:-13px">
          <div class="gray-xs-f mb-sm">
            Study thumbnail image
            <span class="filled-tooltip"
                    data-toggle="tooltip" data-placement="top"
                    data-html="true"
                    title=" <p class='text-left'>Image requirements: The default image shown below will be used for the study list thumbnail in the mobile app. Upload an alternate image if you wish to override it</p>
					<p class='text-left'>The image must be of type .JPG or .PNG. The minimum image size required is 225 x 225. For optimum display in the mobile app, upload an image of either the minimum size or one that is proportionally larger"></p>
            </span>
      </div>
          
          <div class="thumb" style="display:inline-block; width:77px !important;">
                        <img
                           src="${defaultImageSignedUrl}"
                            class="wid100" alt=""/>

            </div>
            
            
          <div style="display:inline-block">
            <div class="thumb alternate" style=" width:77px !important;"> 
              <img
                  <c:if
                       test="${not empty studyBo.thumbnailImage}">src="${signedUrl}"
              </c:if>
                  <c:if
                      test="${empty studyBo.thumbnailImage}">src="/studybuilder/images/dummy-img.jpg" </c:if>
                  onerror="this.src='/studybuilder/images/dummy-img.jpg';"
                  class="wid100" alt=""/>
            </div>
            <div class="dis-inline">
              <span id="removeUrl" class="blue-link elaborateHide">X
                <a
                    href="javascript:void(0)"
                    class="blue-link txt-decoration-underline pl-xs">Remove
                  Image
                </a>
              </span>
              <div class="form-group mb-none mt-sm uploadImg">
                <button id="uploadImgbtn" type="button"
                        class="btn btn-default gray-btn imageButtonDis">Upload
					   
                </button>
                <span>
                  <span
                      class="help-block with-errors red-txt pos-inherit"></span>
                </span>
                <input
                    id="uploadImg" class="dis-none" type="file" name="file"
                    accept=".png, .jpg, .jpeg" onchange="readURL(this);">
                <input type="hidden" value="${studyBo.thumbnailImage}"
                       id="thumbnailImageId" name="thumbnailImage"/>

              </div>
              
              
            </div>
          </div>
         
           <div class="mt-lg" style="margin-top:-5px !important;  font-size:10px; ">
                    <div class="gray-xs-f" style="font-size:12px !important; font-weight:500 !important ; color:#4a5054 !important; display:inline-block">
                     Default image                     
            </div>
             &nbsp; &nbsp; &nbsp;&nbsp;
            <div class="gray-xs-f" style="font-size:12px !important; font-weight:500 !important ; color:#4a5054 !important; display:inline-block">
                     Alternate image                     
            </div>
            </div>
                    
        </div>
      </div>
    </div>
    <!-- End body tab section -->
  </form:form>
</div>
<!-- End right Content here -->

<script>
  $(document)
      .ready(
          function () {
        	$('.studyClass').addClass("active");
            $('#removeUrl').css("visibility", "hidden");
            var file = $('#uploadImg').val();
            var thumbnailImageId = $('#thumbnailImageId').val();
            if (file || thumbnailImageId) {
              $('#removeUrl').css("visibility", "visible");
            }

            <c:if test="${not empty permission}">
            $('#basicInfoFormId input,textarea,select').prop(
                'disabled', true);
            $('#basicInfoFormId').find('.elaborateClass').addClass(
                'linkDis');
            $('.elaborateHide').css('visibility', 'hidden');
            $('.imageButtonDis').prop('disabled', true);
            </c:if>

            var studyType = '${studyBo.type}';
            if (studyType) {
              if (studyType === 'GT') {
                $('.thumbDivClass').show();
              } else {
                $('.thumbDivClass').hide();
              }
            }

            checkRadioRequired();
            $(".rejoin_radio").click(function () {
              checkRadioRequired();
            })

            $("[data-toggle=tooltip]").tooltip();

            //summernote editor initialization
            $('#summernote')
                .summernote(
                    {
                      placeholder: '',
                      tabsize: 2,
                      height: 200,
                      toolbar: [
                        [
                          'font',
                          ['bold', 'italic']],
                        [
                          'para',
                          ['paragraph',
                            'ul', 'ol']],
                        ['font', ['underline']],
                        ['insert', ['link']],
                        ['hr'],
                        ['clear'],
                        ['cut'],
                        ['undo'],
                        ['redo'],
                        ['fontname',
                          ['fontname']],
                        ['fontsize',
                          ['fontsize']],]

                    });
            <c:if test="${not empty permission}">
            $('#summernote').summernote('disable');
            </c:if>

            // File Upload
            $("#uploadImgbtn").click(function () {
              $("#uploadImg").click();
            });

            // Removing selected file upload image
            $("#removeUrl")
                .click(
                    function () {
                      $(".thumb.alternate img")
                          .attr("src",
                              "/studybuilder/images/dummy-img.jpg");
                      $('#uploadImg').val('');
                      $('#thumbnailImageId').val('');
                      var file = $('#uploadImg').val();
                      var thumbnailImageId = $(
                          '#thumbnailImageId').val();
                      if (file || thumbnailImageId) {
                        $("#uploadImg").removeAttr(
                            'required');
                        resetValidation($("#uploadImg")
                            .parents('form'));
                        $('#removeUrl')
                            .css("visibility",
                                "visible");
                      } else {
                        
                        $('#removeUrl').css(
                            "visibility", "hidden");
                      }
                    });

            $("#completedId")
                .on(
                    'click',
                    function (e) {
                      e.preventDefault();
                      isFromValid("#basicInfoFormId")
                      if ($('#summernote').summernote(
                          'code') === '<br>' || $('#summernote').summernote(
                          'code') === '' || $('#summernote').summernote('code') === '<p><br></p>') {
                        $('#summernote').attr(
                            'required', true);
                        $('#summernote')
                            .parent()
                            .addClass(
                                'has-error has-danger')
                            .find(".help-block")
                            .empty()
                            .append(
                              $("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                                "Please fill out this field"));
                        return false;
                      } else {
                        $('#summernote').attr(
                            'required', false);
                        $('#summernote').parent()
                            .removeClass(
                                "has-danger")
                            .removeClass(
                                "has-error");
                        $('#summernote').parent().find(
                            ".help-block").html("");

                      }

                      var type = $(
                          "input[name='type']:checked")
                          .val();
                      if (type && type == 'GT') {
                        validateStudyId(
                            '',
                            function (st) {
                              if (st) {
                                var studyCount = $(
                                    '.customStudyClass')
                                    .find(
                                        '.help-block')
                                    .children().length;
                                if (parseInt(studyCount) >= 1) {
                                  return false;
                                } else {
                                  $(
                                      '.studyTypeClass,.studyIdCls,.appIdCls')
                                      .prop(
                                          'disabled',
                                          false);
                                  if (isFromValid("#basicInfoFormId"))
                                    validateAppId(
                                        '',
                                        function (
                                            valid) {
                                          if (valid) {
                                            var appCount = $(
                                                '.appIdClass')
                                                .find(
                                                    '.help-block')
                                                .children().length;
                                            if (parseInt(appCount) >= 1) {
                                              return false;
                                            } else {
                                              $(
                                                  '.studyTypeClass,.studyIdCls,.appIdCls')
                                                  .prop(
                                                      'disabled',
                                                      false);
                                              if (isFromValid("#basicInfoFormId"))
                                                var file = $(
                                                    '#uploadImg')
                                                    .val();
                                              var thumbnailImageId = $(
                                                  '#thumbnailImageId')
                                                  .val();
                                              
                                                $(
                                                    "#uploadImg")
                                                    .parent()
                                                    .find(
                                                        ".help-block")
                                                    .empty();
                                                $(
                                                    "#uploadImg")
                                                    .removeAttr(
                                                        'required');
                                                validateStudyId(
                                                    '',
                                                    function (
                                                        st) {
                                                      if (st) {
                                                        var studyCount = $(
                                                            '.customStudyClass')
                                                            .find(
                                                                '.help-block')
                                                            .children().length;
                                                        if (parseInt(studyCount) >= 1) {
                                                          return false;
                                                        } else {
                                                          validateAppId(
                                                              '',
                                                              function (
                                                                  valid) {
                                                                if (valid) {
                                                                  var appCount = $(
                                                                      '.appIdClass')
                                                                      .find(
                                                                          '.help-block')
                                                                      .children().length;
                                                                  if (parseInt(appCount) >= 1) {
                                                                    return false;
                                                                  } else {
                                                                    $(
                                                                        '.studyTypeClass,.studyIdCls,.appIdCls')
                                                                        .prop(
                                                                            'disabled',
                                                                            false);
                                                                    if (isFromValid("#basicInfoFormId")) {
                                                                    	var richText=$('#summernote').summernote('code');
                                                                    	var escaped = $('#summernote').text(richText).html();
                                                                    	$('#summernote').val(escaped);
                                                                      $(
                                                                          "#buttonText")
                                                                          .val(
                                                                              'completed');
                                                                      $(
                                                                          "#basicInfoFormId")
                                                                          .submit();
                                                                    }
                                                                  }
                                                                }
                                                              });
                                                        }
                                                      }
                                                    });
                                              
                                            }
                                          }
                                        });
                                }
                              } else {
                                isFromValid("#basicInfoFormId");
                              }
                            });
                      } else {
                        $("#uploadImg").parent().find(
                            ".help-block").empty();
                        validateStudyId(
                            '',
                            function (st) {
                              if (st) {
                                var studyCount = $(
                                    '.customStudyClass')
                                    .find(
                                        '.help-block')
                                    .children().length;
                                if (parseInt(studyCount) >= 1) {
                                  return false;
                                } else {
                                  $(
                                      '.studyTypeClass,.studyIdCls,.appIdCls')
                                      .prop(
                                          'disabled',
                                          false);
                                  if (isFromValid("#basicInfoFormId"))
                                    ;
                                  validateAppId(
                                      '',
                                      function (
                                          valid) {
                                        if (valid) {
                                          var appCount = $(
                                              '.appIdClass')
                                              .find(
                                                  '.help-block')
                                              .children().length;
                                          if (parseInt(appCount) >= 1) {
                                            return false;
                                          } else {
                                            $(
                                                '.studyTypeClass,.studyIdCls,.appIdCls')
                                                .prop(
                                                    'disabled',
                                                    false);
                                            if (isFromValid("#basicInfoFormId")) {
                                              $(
                                                  "#buttonText")
                                                  .val(
                                                      'completed');
                                              $(
                                                  "#basicInfoFormId")
                                                  .submit();
                                            }
                                          }
                                        }
                                      });
                                }
                              } else {
                                isFromValid("#basicInfoFormId");
                              }
                            });
                      }
                    });
            $("#uploadImg").on(
                'change',
                function (e) {
                  var type = $("input[name='type']:checked")
                      .val();
                  if (null != type && type != ''
                      && typeof type != 'undefined'
                      && type == 'GT') {
                    var file = $('#uploadImg').val();
                    var image=$('.thumb.alternate img').attr("src");
                    var thumbnailImageId = $(
                        '#thumbnailImageId').val();
                    if (file || thumbnailImageId) {
                        if(!image.includes("dummy")){
                      $('#removeUrl').css("visibility",
                          "visible");
                        }
                      $("#uploadImg").parent().find(
                          ".help-block").empty();
                    }
                  } else {
                    $('#removeUrl').css("visibility",
                        "visible");
                    $("#uploadImg").parent().find(
                        ".help-block").empty();
                  }
                });
            $('#saveId')
                .click(
                    function (e) {
                      $('#basicInfoFormId').validator(
                          'destroy').validator();
                      validateStudyId(
                          '',
                          function (st) {
                            if (st) {
                              var studyCount = $(
                                  '.customStudyClass')
                                  .find(
                                      '.help-block')
                                  .children().length;
                              if (parseInt(studyCount) >= 1) {
                                return false;
                              } else if (!$('#customStudyName')[0]
                                  .checkValidity()) {
                                $(
                                    "#customStudyName")
                                    .parent()
                                    .addClass(
                                        'has-error has-danger')
                                    .find(
                                        ".help-block")
                                    .empty()
                                    .append(
                                    	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                                        "This is a required field"));
                                return false;
                              } else {
                                var appId = $(
                                    '#appId')
                                    .val();
                                if (null != appId
                                    && appId != ''
                                    && typeof appId != 'undefined') {
                                  validateAppId(
                                      '',
                                      function (
                                          valid) {
                                        if (valid) {
                                          $(
                                              '.studyTypeClass,.studyIdCls,.appIdCls')
                                              .prop(
                                                  'disabled',
                                                  false);
                                          $(
                                              '#basicInfoFormId')
                                              .validator(
                                                  'destroy');
                                          $(
                                              "#buttonText")
                                              .val(
                                                  'save');
                                          var richTextVal=$('#summernote').val();
	                                      if (null != richTextVal && richTextVal != '' && typeof richTextVal != 'undefined' && richTextVal != '<p><br></p>'){
	                                    	  var richText=$('#summernote').summernote('code');
	                                    	  var escaped = $('#summernote').text(richText).html();
                                          	  $('#summernote').val(escaped);
	                                       }
                                          $(
                                              '#basicInfoFormId')
                                              .submit();
                                        } else {
                                          $(
                                              '.studyTypeClass,.studyIdCls,.appIdCls')
                                              .prop(
                                                  'disabled',
                                                  false);
                                          $(
                                              '#basicInfoFormId')
                                              .validator(
                                                  'destroy');
                                          $(
                                              "#buttonText")
                                              .val(
                                                  'save');
                                          var richTextVal=$('#summernote').val();
	                                      if (null != richTextVal && richTextVal != '' && typeof richTextVal != 'undefined' && richTextVal != '<p><br></p>'){
	                                    	  var richText=$('#summernote').summernote('code');
	                                    	  var escaped = $('#summernote').text(richText).html();
                                          	  $('#summernote').val(escaped);
	                                       }
                                          $(
                                              '#basicInfoFormId')
                                              .submit();
                                        }
                                      });
                                } else {
                                  $(
                                      '.studyTypeClass,.studyIdCls,.appIdCls')
                                      .prop(
                                          'disabled',
                                          false);
                                  $(
                                      '#basicInfoFormId')
                                      .validator(
                                          'destroy');
                                  $(
                                      "#buttonText")
                                      .val(
                                          'save');
                                  var richTextVal=$('#summernote').val();
                                  if (null != richTextVal && richTextVal != '' && typeof richTextVal != 'undefined' && richTextVal != '<p><br></p>'){
                                	  var richText=$('#summernote').summernote('code');
                                	  var escaped = $('#summernote').text(richText).html();
                                  	  $('#summernote').val(escaped);
                                   }
                                  $(
                                      '#basicInfoFormId')
                                      .submit();
                                }
                              }
                            } else {
                              var studyCount = $(
                                  '.customStudyClass')
                                  .find(
                                      '.help-block')
                                  .children().length;
                              if (parseInt(studyCount) >= 1) {
                                return false;
                              } else {
                                $(
                                    '#basicInfoFormId')
                                    .validator(
                                        'destroy')
                                    .validator();
                                if (!$('#customStudyId')[0]
                                    .checkValidity()) {
                                  $(
                                      "#customStudyId")
                                      .parent()
                                      .addClass(
                                          'has-error has-danger')
                                      .find(
                                          ".help-block")
                                      .empty()
                                      .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                                          "This is a required field"));
                                  return false;
                                }
                              }
                            }
                          });
                    });
            $(".studyIdCls").blur(function () {
              validateStudyId('', function (val) {
              });
            });
            $(".appIdCls").blur(function () {
              validateAppId('', function (val) {
              });
            });
            $("#inlineRadio6").change(function () {
              validateAppId('', function (val) {
              });
            });

          });

  // Displaying images from file upload
  function readURL(input) {
	
    if (input.files && input.files[0]) {
      const allowedExtensions =  ['jpg','png','jpeg'];
   	  const { name:fileName } = input.files[0];
   	  const fileExtension = fileName.split(".").pop().toLowerCase();
   	  if(allowedExtensions.includes(fileExtension)){  
	      var reader = new FileReader();
	
	      reader.onload = function (e) {
	    	  var image = new Image();
	    	    image.src = e.target.result;
	    	    image.onload = function() {
	    	        // access image size here 
	    	        if(this.width >=225 && this.height>=225 ){
	    	        	 $('.thumb.alternate img').attr('src', e.target.result).width(66).height(
	         	                66);
	        	       }
	    	    };
	      };
	      reader.readAsDataURL(input.files[0]);
   	  }else{
   		  $("#uploadImg")
          .parent()
          .find(".help-block")
          .empty()
          .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
              "Invalid image size or format"));
      	  $(".thumb.alternate img")
          .attr("src",
              "/studybuilder/images/dummy-img.jpg");
      	  $('#uploadImg, #thumbnailImageId').val('');
      	  $('#removeUrl').css("visibility", "hidden");
   	  }
    }
  }

  //Added for image height and width
  var _URL = window.URL || window.webkitURL;

  $("#uploadImg")
      .change(
          function (e) {
            var file, img;
            if ((file = this.files[0])) {
              img = new Image();
              img.onload = function () {  
                var ht = this.height;
                var wds = this.width;
                if (ht >= 225 && wds >= 225) {
                  $("#uploadImg").parent()
                      .find(".help-block").append('');
                  $('#removeUrl')
                      .css("visibility", "visible");
                } else {
                  $("#uploadImg")
                      .parent()
                      .find(".help-block")
                      .empty()
                      .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                          "Invalid image size or format"));
                  $(".thumb.alternate img")
                      .attr("src",
                          "/studybuilder/images/dummy-img.jpg");
                  $('#uploadImg, #thumbnailImageId').val('');
                  $('#removeUrl').css("visibility", "hidden");
                }
                var file = $('#uploadImg').val();
                var thumbnailImageId = $('#thumbnailImageId')
                    .val();
                if (file || thumbnailImageId) {
                  $("#uploadImg").removeAttr('required');
                  resetValidation($("#uploadImg").parents(
                      'form'));
                } else {
                  
                  resetValidation($("#uploadImg").parents(
                      'form'));
                }
              };
              img.onerror = function () {
                $("#uploadImg")
                    .parent()
                    .find(".help-block")
                    .empty()
                    .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                        "Invalid image size or format"));
                $('#removeUrl').css("visibility", "hidden");
                $(".thumb.alternate img").attr("src",
                    "/studybuilder/images/dummy-img.jpg");
                $('#uploadImg, #thumbnailImageId').val('');
                var file = $('#uploadImg').val();
                var thumbnailImageId = $('#thumbnailImageId')
                    .val();
                if (file || thumbnailImageId) {
                  $("#uploadImg").removeAttr('required');
                  resetValidation($("#uploadImg").parents(
                      'form'));
                } else {
                  
                  resetValidation($("#uploadImg").parents(
                      'form'));
                }
              };
              img.src = _URL.createObjectURL(file);
            }
          });
  $("#uploadImg, #thumbnailImageId").change(function () {
    var file = $('#uploadImg').val();
    var thumbnailImageId = $('#thumbnailImageId').val();
    if (file || thumbnailImageId) {
    	$('#thumbnailImageId').val("");
      $("#uploadImg").removeAttr('required');
      resetValidation($("#uploadImg").parents('form'));
    } else {
      
      resetValidation($("#uploadImg").parents('form'));
    }
  });

  function validateStudyId(item, callback) {
    var customStudyId = $("#customStudyId").val();
    var thisAttr = $("#customStudyId");
    var dbcustomStudyId = '${studyBo.customStudyId}';
    if (customStudyId != null && customStudyId != ''
        && typeof customStudyId != 'undefined') {
      if (dbcustomStudyId != customStudyId) {
        $
            .ajax({
              url: "/studybuilder/adminStudies/validateStudyId.do?_S=${param._S}",
              type: "POST",
              datatype: "json",
              data: {
                customStudyId: customStudyId,
                "${_csrf.parameterName}": "${_csrf.token}",
              },
              success: function getResponse(data) {
                var message = data.message;
                if ('SUCCESS' != message) {
                  $(thisAttr).validator('validate');
                  $(thisAttr).parent().removeClass(
                      "has-danger").removeClass(
                      "has-error");
                  $(thisAttr).parent().find(".help-block")
                      .html("");
                  callback(true);
                } else {
                  $(thisAttr).val('');
                  $(thisAttr).parent().addClass("has-danger")
                      .addClass("has-error");
                  $(thisAttr).parent().find(".help-block")
                      .empty();
                  $(thisAttr)
                      .parent()
                      .find(".help-block")
                      .empty()
                      .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                          customStudyId
                          + " has already been used in the past"));
                  callback(false);
                }
              },
              global: false
            });
      } else {
        callback(true);
      }
    } else {
      callback(false);
    }
  }

  function checkRadioRequired() {
    var rejoinRadioVal = $('input[name=type]:checked').val();
    if (rejoinRadioVal == 'GT') {
      $('.thumbDivClass').show();
      $('.thumbImageDIv').show();
      
      var file = $('#uploadImg').val();
      var thumbnailImageId = $('#thumbnailImageId').val();
      if (file || thumbnailImageId) {
        $("#uploadImg").removeAttr('required');
        resetValidation($("#uploadImg").parents('form'));
      } else {
      
        resetValidation($("#uploadImg").parents('form'));
      }
    } else {
      $('.thumbDivClass').hide();
      $('.thumbImageDIv').hide();
      $('#uploadImg').removeAttr('required', '');
      resetValidation($("#uploadImg").parents('form'));
    }
  }

  function validateAppId(item, callback) {
    var appId = $("#appId").val();
    var studyType = $('input[name=type]:checked').val();
    var thisAttr = $("#appId");
    var customStudyId = $("#customStudyId").val();
    var dbcustomStudyId = '${studyBo.customStudyId}';
    if (appId != null && appId != '' && typeof appId != 'undefined') {
      $
          .ajax({
            url: "/studybuilder/adminStudies/validateAppId.do?_S=${param._S}",
            type: "POST",
            datatype: "json",
            data: {
              customStudyId: customStudyId,
              appId: appId,
              studyType: studyType,
              dbcustomStudyId: dbcustomStudyId,
              "${_csrf.parameterName}": "${_csrf.token}",
            },
            success: function getResponse(data) {
              var message = data.message;
              if ('SUCCESS' != message) {
                $(thisAttr).validator('validate');
                $(thisAttr).parent().removeClass("has-danger")
                    .removeClass("has-error");
                $(thisAttr).parent().find(".help-block").html(
                    "");
                callback(true);
              } else {
                $(thisAttr).val('');
                $(thisAttr).parent().addClass("has-danger")
                    .addClass("has-error");
                $(thisAttr).parent().find(".help-block")
                    .empty();
                $(thisAttr)
                    .parent()
                    .find(".help-block")
                    .empty()
                    .append($("<ul><li> </li></ul>").attr("class","list-unstyled").text(
                        appId
                        + " has already been used in the past. Switch app type to 'gateway' or choose a unique App ID"))
                    .append("</br>");
                callback(false);
              }
            },
            global: false
          });
    } else {
      callback(false);
    }
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
	        window.location.href = "/studybuilder/adminStudies/viewSettingAndAdmins.do?_S=${param._S}";
	
	    }, 5000);
    }else{
    	setTimeout(hideDisplayMessage, 5000);
    }
  }
</script>
