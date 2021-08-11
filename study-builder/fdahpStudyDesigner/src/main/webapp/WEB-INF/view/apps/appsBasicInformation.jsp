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
      action="/studybuilder/adminApps/saveOrUpdateAppInfo.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}"
      data-toggle="validator" role="form" id="appsBasicInfoFormId" method="post"
      autocomplete="off" enctype="multipart/form-data">
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          APP INFORMATION
          <c:set var="isLive">${_S}isLive</c:set>
        </div>


        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
        </div>
        <c:if test="${empty permission}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn actBut"
                    id="saveId"
                    >Save
            </button>
          </div>

          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn actBut"
                    id="completedId"
                    >Mark
              as completed
            </button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->
    <input type="hidden" id="sId" value="${appBo.id}" name="id"/>
    <input type="hidden" value="" id="buttonText" name="buttonText">
    <!-- Start body tab section -->
    <div class="right-content-body col-xs-12">

              <div class="row">
                <div class="col-md-6 pl-none">
                  <div class="gray-xs-f mb-xs">App ID  <span class="requiredStar"> *</span>
                  <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" 
                  title="The Tooltip plugin is small pop-up box that appears when the user moves."></span>
                  </div>
                  <div class="form-group mb-none">
                    <input type="text" custAttType="cust" autofocus="autofocus"
	                   class="form-control aq-inp appIdCls" name="appId" id="appId"
	                   maxlength="15" value="${appBo.customAppId}"
	                <%-- <c:if
	                    test="${not empty appsBo.status && (appsBo.status == 'Active' || appsBo.status == 'Published' || appsBo.status == 'Paused' || appsBo.status == 'Deactivated')}"> disabled</c:if> --%>
	                   required data-error="Please fill out this field"/>
                    <div class="help-block with-errors red-txt"></div>
                </div>
              </div>

              <div class="col-md-6">
                   <div class="gray-xs-f mb-xs">App Name <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                   <div class="form-group mb-none">
                   <input type="text" class="form-control" name="name"
                   id="appName" value="${fn:escapeXml(appBo.name)}"
                    required data-error="Please fill out this field"/>
                   <div class="help-block with-errors red-txt"></div>
                   </div>
               </div>
               
              <div class="clearfix"></div>
             
          </div>

    <!-- End body tab section -->
  </form:form>
</div>
<!-- End right Content here -->

<script>
 $(document).ready( function () {
	 $('.appClass').addClass('active');
	 <c:if test="${not empty permission}">
     $('#appsBasicInfoFormId input').prop(
         'disabled', true);
     </c:if>
    $('#removeUrl').css("visibility", "hidden");
    
    $('#saveId').click(
        function (e) {
          $('#appsBasicInfoFormId').validator(
              'destroy').validator();
          validateAppId(
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
                  } else if (!$('#appName')[0]
                      .checkValidity()) {
                    $(
                        "#appName")
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
                                  '#appsBasicInfoFormId')
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
                                  '#appsBasicInfoFormId')
                                  .submit();
                            } else {
                              $(
                                  '.studyTypeClass,.studyIdCls,.appIdCls')
                                  .prop(
                                      'disabled',
                                      false);
                              $(
                                  '#appsBasicInfoFormId')
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
                                  '#appsBasicInfoFormId')
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
                          '#appsBasicInfoFormId')
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
                          '#appsBasicInfoFormId')
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
                        '#appsBasicInfoFormId')
                        .validator(
                            'destroy')
                        .validator();
                    if (!$('#appId')[0]
                        .checkValidity()) {
                      $(
                          "#appId")
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
 });
 
function validateAppId(item, callback) {
    var appId = $("#appsId").val();
    var thisAttr = $("#appsId");
    var dbAppId = '${appsBo.id}';
    if (appId != null && appId != ''
        && typeof appId != 'undefined') {
      if (dbAppId != appId) {
        $
            .ajax({
              url: "/studybuilder/adminApps/validateAppId.do?_S=${param._S}",
              type: "POST",
              datatype: "json",
              data: {
                appId: appId,
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
                          appId
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

 
 
</script>
