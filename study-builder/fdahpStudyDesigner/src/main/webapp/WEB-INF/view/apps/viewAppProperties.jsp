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
      action="/studybuilder/adminApps/saveOrUpdateAppProperties.do?_S=${param._S}"
      data-toggle="validator" role="form" id="appPropertiesFormId" method="post"
      autocomplete="off">
    <input type="hidden" name="buttonText" id="buttonText">
    <input type="hidden" id="appPropertiesId" name="id"  value="${appBo.id}">
    
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          APP PROPERTIES
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
                                <div class="gray-xs-f mb-xs">Feedback email  
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter an email to receive feedback submitted by app users. Feedback can be provided anonymously by users via the in-app feedback form."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.feedbackEmailAddress}" name="feedbackEmailAddress" 
                                    required data-error="Please fill out this field" maxlength="100"
                  					pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,24}$"
                   					autocomplete="off" data-pattern-error="Email address is invalid"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">'Contact Us' email 
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter an email to receive 'Contact Us' forms submitted by app users. Note that app users provide their email as part of this form."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.contactEmailAddress}" name="contactEmailAddress" 
                                    required data-error="Please fill out this field" maxlength="100"
                  					pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,24}$"
                   					autocomplete="off" data-pattern-error="Email address is invalid"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                             <div class="col-md-6 pl-none mt-lg">
                                <div class="gray-xs-f mb-xs">App support email  
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter a support email that app users can write to for any assistance they need. This email appears in app account related emails sent to app users."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appSupportEmailAddress}" name="appSupportEmailAddress" 
                                    required data-error="Please fill out this field" maxlength="100"
                  					pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,24}$"
                   					autocomplete="off" data-pattern-error="Email address is invalid"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-lg">App 'Terms' URL
                                <small>(250 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter a URL for the app's Terms"></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appTermsUrl}" name="appTermsUrl" 
                                    pattern="^(http:\/\/|https:\/\/)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
                   					title="Include http://" maxlength="250"
                  					data-pattern-error="Please enter a valid URL" required data-error="Please fill out this field"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none mt-lg">
                                <div class="gray-xs-f mb-xs">App Privacy Policy URL  
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter a URL for the app's Privacy Policy"></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appPrivacyUrl}" name="appPrivacyUrl" 
                                    pattern="^(http:\/\/|https:\/\/)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
                   					title="Include http://" maxlength="100"
                  					data-pattern-error="Please enter a valid URL" required data-error="Please fill out this field"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-lg">Organization name
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the name of the organization offering the app. This is typically used in app account related emails sent to app users."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.organizationName}" name="organizationName" required data-error="Please fill out this field" maxlength="100"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none mt-lg">
                                <div class="gray-xs-f mb-xs">App Store URL 
                                <small>(250 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the App Store URL from where the app can be downloaded for iPhone users"></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appStoreUrl}" name="appStoreUrl" id="appStoreUrlId"
                                    pattern="^(http:\/\/|https:\/\/)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
                   					title="Include http://" maxlength="250" 
                  					data-pattern-error="Please enter a valid URL" required data-error="Please fill out this field"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-lg">Play Store URL
                                <small>(250 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the Play Store URL from where the app can be downloaded for Android users"></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.playStoreUrl}" name="playStoreUrl" id="playStoreUrlId"
                                    pattern="^(http:\/\/|https:\/\/)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
                   					title="Include http://" maxlength="250" 
                  					data-pattern-error="Please enter a valid URL" required data-error="Please fill out this field"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
							 <div class="col-md-6 pl-none mt-lg">
                                <div class="gray-xs-f mb-xs">'From' email for outgoing app emails 
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter a 'sender' email for automated emails that go out to app users. This email must be an alias of the primary sender email that is configured in the Secret Manager as part of the platform deployment process. Contact your IT admin for this information. If an alias is not possible to obtain, use the same email here."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.fromEmailAddress}" name="fromEmailAddress" 
                                    required data-error="Please fill out this field" maxlength="100"
                  					pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,24}$"
                   					autocomplete="off" data-pattern-error="Email address is invalid"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-lg">App website
                                <small>(100 characters max)</small>
                                <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="Enter the URL of a website that describes your app, if you have one."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appWebsiteUrl}" name="appWebsiteUrl"
                                    pattern="^(http:\/\/|https:\/\/)[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
                   					title="Include http://" maxlength="100" 
                  					data-pattern-error="Please enter a valid URL" required data-error="Please fill out this field"/>
                                    <div class="help-block with-errors red-txt"></div>
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
	  $(".menuNav li.third").addClass('active');
	  
	  <c:if test="${not empty permission}">
	     $('#appPropertiesFormId input').prop('disabled', true);
	     </c:if>
	     
	     <c:if test="${empty permission}">
	    
		     <c:if test = "${appBo.appPlatform == 'I,A'} || ${appBo.appPlatform == 'A,I'}">
		     $('#appStoreUrlId').prop('disabled', false);
		     $('#playStoreUrlId').prop('disabled', false);
		     </c:if>
	     	<c:if test="${appBo.appPlatform == 'I'}">
	     	 $('#playStoreUrlId').prop('required',false);
	     	$('#playStoreUrlId').prop('disabled', true);
	     	$('#appStoreUrlId').prop('disabled', false);
	     	</c:if>
	     	<c:if test="${appBo.appPlatform == 'A'}">
	    	 $('#appStoreUrlId').prop('required',false);
	    	$('#appStoreUrlId').prop('disabled', true);
	    	$('#playStoreUrlId').prop('disabled', false);
	    	</c:if>
	     </c:if>
	});
  
  
  $('#saveId').click(
	        function (e) {
	        	
	          $('#appPropertiesFormId').validator('destroy');
	          $("#buttonText").val('save');
	          $("#appPropertiesFormId").submit()
	        });
  
  $('#completedId').click(
	        function (e) {
	        	
	        	 if( isFromValid("#appPropertiesFormId")){
	            	 $("#buttonText").val('completed');
	            	 $("#appPropertiesFormId").submit();
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
		        window.location.href = "/studybuilder/adminApps/viewDevConfigs.do?_S=${param._S}";
		
		    }, 5000);
	}else{
		setTimeout(hideDisplayMessage, 5000);
	}
 }
</script>