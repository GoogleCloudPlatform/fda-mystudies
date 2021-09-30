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
      action="/studybuilder/adminApps/saveOrUpdateAppDeveloperConfig.do?_S=${param._S}"
      data-toggle="validator" role="form" id="developerConfigFormId" method="post"
      autocomplete="off">
    
	 <input type="hidden" name="buttonText" id="buttonText">
     <input type="hidden" id="settingsAppId" name="id"
           value="${appBo.id}">
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          DEVELOPER CONFIGURATIONS
          <c:set var="isLive">${_S}isLive</c:set>
         </div>

        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut"
                  id="cancelId">Cancel
          </button>
        </div>
        <c:if
            test="${empty permission}">
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

                         <div class="row">
                            <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs mt-md">Android Bundle ID 
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the Bundle ID for your Android app. Note that you cannot update this field once you have marked the Android app as distributed."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control android" value= "${appBo.androidBundleId}" name="androidBundleId" required data-error="Please fill out this field" maxlength="100"
                                    <c:if test="${appBo.androidAppDistributed}"> disabled</c:if>/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-md">Android Server Key 
                                <small>(255 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the server key needed to push notifications to the Android app. Note that you cannot update this field once you have marked the Android app as distributed."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control android" value= "${appBo.androidServerKey}" name="androidServerKey" required data-error="Please fill out this field" maxlength="255"
                                    <c:if test="${appBo.androidAppDistributed}"> disabled</c:if>/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                             <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs mt-md">iOS Bundle ID 
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the Bundle ID for your iOS app. Note that you cannot update this field once you have marked the iOS app as distributed."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control ios" value= "${appBo.iosBundleId}" name="iosBundleId" required data-error="Please fill out this field" maxlength="100"
                                    <c:if test="${appBo.iosAppDistributed}"> disabled</c:if>/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-md">iOS Server Key
                                <small>(255 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the server key needed to push notifications to the iOS app. Note that you cannot update this field once you have marked the iOS app as distributed."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control ios" value= "${appBo.iosServerKey}" name="iosServerKey" required data-error="Please fill out this field" maxlength="255"
                                    <c:if test="${appBo.iosAppDistributed}"> disabled</c:if>/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            <div class="blue-md-f mb-md mt-md text-uppercase"> APP VERSION INFORMATION </div>
                <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">Latest XCode app version (for iOS app) 
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the Xcode version applicable to the latest build of the iOS app that is available to users. Only digits and the . character are allowed in this field."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control ios" value= "${appBo.iosXCodeAppVersion}" name="iosXCodeAppVersion" 
                                    required data-error="Please fill out this field"  pattern="^\d+(\.\d+)*$" data-pattern-error="Please enter a valid App version" maxlength="100"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Latest app build version (for iOS app)<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the app build version corresponding to the latest iOS app that is available to users. Only integers are allowed in this field."></span></div>
                                <div class="form-group mb-none">
                                    <input type="number" min="0" onkeypress="return isNumber(event)" class="form-control ios" value= "${appBo.iosAppBuildVersion}" name="iosAppBuildVersion" required data-error="Please fill out this field"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-12 pl-none">
                                <div class="gray-xs-f mb-xs mt-md">Force upgrade for iOS users? <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="If 'Yes' is selected, iOS app users are forced to upgrade their app before they can conitnue using it."></span></div>
                               <div class="mt-md mb-md">
                         <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="iosForceUpgradeYesId" class="ios" value="1" name="iosForceUpgrade" required data-error="Please fill out this field" />
                            <label for="iosForceUpgradeYesId">Yes</label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" id="iosForceUpgradeNoId" class="ios" value="0" name="iosForceUpgrade"  required data-error="Please fill out this field" checked="checked"/>
                            <label for="iosForceUpgradeNoId">NO</label>
                        </span>
                    </div>
                            </div>
                             <div class="clearfix"></div>

                           <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs"> Latest app version code (for Android app) <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the app version corresponding to the latest Android app that is available to users. Only integers are allowed in this field."></span></div>
                                <div class="form-group mb-none">
                                    <input type="number" min="0" onkeypress="return isNumber(event)" class="form-control android" value= "${appBo.versionInfoBO.android}" name="androidAppBuildVersion" required data-error="Please fill out this field"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                             <div class="clearfix"></div>
                            
                              <div class="col-md-12 pl-none">
                                <div class="gray-xs-f mb-xs mt-md">Force upgrade for Android users? <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="If 'Yes' is selected, Android app users are forced to upgrade their app before they can conitnue using it."></span></div>
                               <div class="mt-md mb-md">
                         <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="androidForceUpgradeYesId" class="android" value="1" name="androidForceUpgrade"  required data-error="Please fill out this field"/>
                            <label for="androidForceUpgradeYesId">Yes</label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" id="androidForceUpgradeNoId" class="android" value="0" name="androidForceUpgrade"  required data-error="Please fill out this field"  checked="checked"/>
                            <label for="androidForceUpgradeNoId">NO</label>
                        </span>
                    </div>
                            </div>
                           
                        </div>
                        
			<!-- End Section-->
			
			


    </div>
    <!-- End body tab section -->

  </form:form>

</div>
<!-- End right Content here -->
<script>
  $(document).ready(function () {
	  $('.appClass').addClass('active');
	  $(".menuNav li.active").removeClass('active');
	  $(".menuNav li.fourth").addClass('active');
	  <c:if test="${not empty permission}">
	     $('#developerConfigFormId input').prop('disabled', true);
	  </c:if>
	  
	  <c:if test="${empty permission}">
	    
	    <c:if test = "${appBo.appPlatform == 'I,A'} || ${appBo.appPlatform == 'A,I'}">
		     $('.android').prop('disabled', false);
		     $('.ios').prop('disabled', false);
	    </c:if>
	  	<c:if test="${appBo.appPlatform == 'I'}">
		  	$('.android').prop('required',false);
		  	$('.android').prop('disabled', true);
	  	</c:if>
	  	<c:if test="${appBo.appPlatform == 'A'}">
		 	 $('.ios').prop('required',false);
		 	$('.ios').prop('disabled', true);
	 	</c:if>
  	 </c:if>
	  
	  <c:if test="${appBo.versionInfoBO.iosForceUpgrade eq false}">
	  		$('#iosForceUpgradeYesId').prop('checked', false);
	  		$('#iosForceUpgradeNoId').prop('checked', true);
	  </c:if>
	  <c:if test="${appBo.versionInfoBO.iosForceUpgrade eq true}">
	  		$('#iosForceUpgradeYesId').prop('checked', true);
			$('#iosForceUpgradeNoId').prop('checked', false);
	  </c:if>
	  
	  <c:if test="${appBo.versionInfoBO.androidForceUpgrade eq false}">
	  		$('#androidForceUpgradeYesId').prop('checked', false);
			$('#androidForceUpgradeNoId').prop('checked', true);
	  </c:if>
	  <c:if test="${appBo.versionInfoBO.androidForceUpgrade eq true}">
	  		$('#androidForceUpgradeYesId').prop('checked', true);
			$('#androidForceUpgradeNoId').prop('checked', false);
	  </c:if>
	  
	  
	  $('#saveId').click(
		        function (e) {
		        	
		          $('#developerConfigFormId').validator('destroy');
		          $("#buttonText").val('save');
		          $("#developerConfigFormId").submit()
		        });
	  
	  $('#completedId').click(
		        function (e) {
		        	if ($('.radio input:checked').length == 0) {
		          	    $("input").attr("required", true);
		              }
		        	
		        	 if( isFromValid("#developerConfigFormId")){
		            	 $("#buttonText").val('completed');
		            	 $("#developerConfigFormId").submit();
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
			    window.setTimeout(function(){
			        window.location.href = "/studybuilder/adminApps/appActionList.do?_S=${param._S}";
			
			    }, 5000);
		}else{
			setTimeout(hideDisplayMessage, 5000);
		}
		
	}
	});
  function isNumber(evt) {
	    evt = (evt) ? evt : window.event;
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
	      return false;
	    }
	    return true;
	  }
</script>