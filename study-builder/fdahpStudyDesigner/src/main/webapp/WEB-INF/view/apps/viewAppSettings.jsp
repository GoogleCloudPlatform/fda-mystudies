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
					App type <span>
            </span><span class="requiredStar"> *</span> <span
                  data-toggle="tooltip" data-placement="top"
                  title="A gateway app is one that holds multiple studies in it whereas a standalone app has a single study in it. App type cannot be edited once the app record is created."
                  class="filled-tooltip"></span>
				</div>
				<div class="form-group">
					<span class="radio radio-info radio-inline p-45"><input
						type="radio" id="inlineRadio1" value="GT" class="appTypeClass"
						${appBo.type eq 'GT'?'checked':""} name="type"
						 required data-error="Please fill out this field"
						 <c:if
	                    test="${not empty appBo.appStatus && (appBo.appStatus == 'Active' || appBo.appStatus == 'Deactivated')}"> disabled</c:if>> 
						 <label for="inlineRadio1">Gateway</label> </span> 
						 <span class="radio radio-inline"><input
						  type="radio" id="inlineRadio2" value="SD" class="appTypeClass"
						  name="type"
						  ${appBo.type eq 'SD'?'checked':""}
						 required data-error="Please fill out this field"
						 <c:if
	                    test="${not empty appBo.appStatus && (appBo.appStatus == 'Active' || appBo.appStatus == 'Deactivated')}"> disabled</c:if>>
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
                  data-toggle="tooltip" data-placement="top"
                  title="Choose the mobile platform(s) that you want to make the app available for. This cannot be edited once the app record is created."
                  class="filled-tooltip"></span>
        </div>
        <div class="form-group">
          <span class="checkbox checkbox-inline p-45"><input
              class="platformClass" type="checkbox" id="inlineCheckbox1"
              name="appPlatform" value="I"
              <c:if test="${fn:contains(appBo.appPlatform,'I')}">checked</c:if>
              <c:if
	                    test="${not empty appBo.appStatus && (appBo.appStatus == 'Active' || appBo.appStatus == 'Deactivated')}"> disabled</c:if>
              data-error="Please check these box if you want to proceed"
              > <label for="inlineCheckbox1"> iOS </label>
          </span>
          <span class="checkbox checkbox-inline"><input
              type="checkbox" class="platformClass" id="inlineCheckbox2"
              name="appPlatform" value="A"
              <c:if test="${fn:contains(appBo.appPlatform,'A')}">checked</c:if>
              <c:if
	             test="${not empty appBo.appStatus && (appBo.appStatus == 'Active' || appBo.appStatus == 'Deactivated')}"> disabled</c:if>
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
<script>
$(document).ready( function () {
	$('.appClass').addClass('active');
	$(".menuNav li.active").removeClass('active');
    $(".menuNav li.second").addClass('active');
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
      var count = "${countOfStudies}";
      if($('.checkbox input:checked').length >= 1 && document.getElementById('inlineRadio2').checked == true && count > 1) {
		  bootbox.alert("This app seems to be linked to multiple studies. Please select the 'gateway' app type to proceed.") ;
    }else{
	    if( isFromValid("#settingFormId")){
	   	 $("#buttonText").val('completed');
	   	 $("#settingFormId").submit();
	    }
     }
    });
    
	var sucMsg = '${sucMsg}';
	if (sucMsg.length > 0) {
	  showSucMsg(sucMsg);
	}
	
	function showSucMsg(message) {
	$("#alertMsg").removeClass('e-box').addClass('s-box').text(message);
	$('#alertMsg').show('5000');
	if('${param.buttonText}' == 'completed'){
		var appStatus = "${appBo.appStatus}"
		if(appStatus != 'Active' ){
		    window.setTimeout(function(){
		        window.location.href = "/studybuilder/adminApps/appActionList.do?_S=${param._S}";
		
		    }, 5000);
		}else{
			  window.setTimeout(function(){
			        window.location.href = "/studybuilder/adminApps/viewAppProperties.do?_S=${param._S}";
			
			    }, 5000);
		}
	}else{
		setTimeout(hideDisplayMessage, 5000);
	}
}
  
</script>