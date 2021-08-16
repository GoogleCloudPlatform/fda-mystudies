<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<style>
  table.dataTable thead th:last-child {
    width: 100px !important;
  }
</style>
<div class="col-sm-10 col-rc white-bg p-none" id="settingId">
  <form:form
      action="/studybuilder/adminApps/saveOrUpdateAppSettingAndAdmins.do?_S=${param._S}"
      data-toggle="validator" role="form" id="settingFormId" method="post"
      autocomplete="off">
     <input type="hidden" name="buttonText" id="buttonText">
     <input type="hidden" id="settingsAppId" name="id"
           value="${appBo.id}">
	
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          APP SETTINGS
          <c:set var="isLive">${_S}isLive</c:set>
        </div>

        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut"
                  id="cancelId">Cancel
          </button>
        </div>
        <c:if test="${empty permission}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn" id="saveId">Save</button>
          </div>

          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn"
                    id="completedId">Mark as completed
            </button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->


    <!-- Start body tab section -->
    <div class="right-content-body col-xs-12">
    
         <!-- Start Section-->
			<div class="col-md-12 p-none">
				<div class="gray-xs-f mb-sm">
					App Type <span>
            </span><span class="requiredStar"> *</span> <span
                  data-toggle="tooltip" data-placement="top"
                  title="This field can be updated after the study is launched if you wish to stop enrollment at any point during the course of the study."
                  class="filled-tooltip"></span>
				</div>
				<div class="form-group">
					<span class="radio radio-info radio-inline p-45"><input
						type="radio" id="inlineRadio1" value="GT" class="appTypeClass"
						${appBo.type eq 'GT'?'checked':""} name="type"
						 required data-error="Please fill out this field"> <label
						for="inlineRadio1">Gateway</label> </span> <span class="radio radio-inline"><input
						type="radio" id="inlineRadio2" value="SD" class="appTypeClass"
						name="type"
						${appBo.type eq 'SD'?'checked':""}
						 required data-error="Please fill out this field">
						<label for="inlineRadio2">Standalone</label> </span>
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>
			<!-- End Section-->
			
      <!-- Start Section-->
      <div class="col-md-12 p-none">
        <div class="gray-xs-f mb-sm">
          Platform(s) supported
          <span class="requiredStar"> *</span>
          <span
              class="sprites_v3 filled-tooltip" id="infoIconId"></span>
        </div>
        <div class="form-group">
          <span class="checkbox checkbox-inline p-45"><input
              class="platformClass" type="checkbox" id="inlineCheckbox1"
              name="appPlatform" value="I"
              <c:if test="${fn:contains(appBo.appPlatform,'I')}">checked</c:if>
              <%-- <c:if
                  test="${not empty studyBo.liveStudyBo && fn:contains(studyBo.liveStudyBo.platform,'I') || studyBo.status eq 'Active'}">disabled</c:if> --%>
              data-error="Please check these box if you want to proceed"
              > <label for="inlineCheckbox1"> iOS </label>
          </span>
          <span class="checkbox checkbox-inline"><input
              type="checkbox" class="platformClass" id="inlineCheckbox2"
              name="appPlatform" value="A"
              <c:if test="${fn:contains(appBo.appPlatform,'A')}">checked</c:if>
              <%-- <c:if
                  test="${not empty studyBo.liveStudyBo && fn:contains(studyBo.liveStudyBo.platform,'A') || studyBo.status eq 'Active'}">disabled</c:if> --%>
              data-error="Please check these box if you want to proceed"
              > <label for="inlineCheckbox2"> Android </label>
          </span>
          <div class="help-block with-errors red-txt"></div>
        </div>
      </div>
      <!-- End Section-->


    </div>
    <!-- End body tab section -->

  </form:form>

</div>
<!-- End right Content here -->


<!-- Modal -->
<div class="modal fade" id="myModal" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">

      <div class="modal-header cust-hdr pt-lg">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
        <h4 class="modal-title pl-lg">
          <strong>Platform and Feature Support</strong>
        </h4>
      </div>
      <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
        <div>
          <div>
            <ul class="no-disc">
              <li><strong>1. Platform support: </strong><br/>
                <ul class="no-disc">
                  <li>Note that once the study is launched, platform support
                    cannot be revoked. However, adding support for a platform not
                    previously selected will still be possible.
                  </li>
                </ul>
              </li>
              <li>&nbsp;</li>
              <li><strong>2. Feature support on iOS and Android:</strong><br/>

                <ul class="no-disc">
                  <li>Given below is a list of features currently NOT
					available for Android as compared to iOS. Please note the same
                    in your creation of study content:
                  </li>
                  <li>i. Active tasks: Tower of hanoi, Spatial span memory
                  </li>
                </ul>
              </li>

            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script>
$(document).ready( function () {
	  <c:if test="${not empty permission}">
	     $('#settingFormId input').prop('disabled', true);
	  </c:if>
        
	});
	
$('#saveId').click(
        function (e) {
         $('#settingFormId').validator('destroy');
          $("#buttonText").val('save');
          $("#settingFormId").submit()
        });

$('#completedId').click(
    function (e) {
      if ($('.checkbox input:checked').length == 0) {
    	    $("input").attr("required", true);
        }
     if( isFromValid("#settingFormId")){
    	 $("#buttonText").val('completed');
    	 $("#settingFormId").submit();
     }
      
    
    });
  
</script>