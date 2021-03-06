<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.fdahpstudydesigner.util.SessionObject" %>

<style>
.modal-title {
    text-align: initial !important;
}
</style>

<!-- create Study Section Start -->
<div id="" class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mt-md tit_con">
  <div class="md-container">
    <div class="text-center">
      <div class="" id="alertMsg"></div>
    </div>
  </div>
</div>
<!-- create Study Section End -->

<!-- StudyList Section Start-->

<div id="studyListId" class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none" style="display: none;">
  <div class="md-container">
    <div class="col-sm-12 col-md-12 col-lg-12 p-none mb-md">
      <div class="black-lg-f">
        Configure and manage studies
      </div>
      <c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_CREATE_MANAGE_STUDIES')}">
        <div class="dis-line pull-right ml-md mt-xs">
          <div class="form-group mb-none">
          <p class="black-lg-f ml-lg deactivated_lable">
       				 Show deactivated studies
     		 </p>
          <label class="switch deactivate_switch mr-md" data-toggle="tooltip" id="lab" data-placement="top">
                    <input type="checkbox" class="switch-input deactivate_switch-input"
                            value="checked"  id="deactivatedBtn" onchange="showActivatedStudies(status)"/>
                    <span class="switch-label deactivate_switch-label" data-on="Off" data-off="On"></span>
                    <span class="switch-handle deactivate_switch-handle"></span>
                  </label>
            <button type="button" class="btn btn-primary blue-btn addEditStudy"> Create study
            </button>
              <button type="button" class="btn btn-primary blue-btn importStudy"  onclick="importStudy();"> Import </button>
          </div>
        </div>
      </c:if>
    </div>
  </div>
</div>
<!-- StudyList Section End-->

<form:form action="/studybuilder/adminStudies/viewStudyDetails.do" id="addEditStudyForm"
           name="addEditStudyForm"
           method="post">
</form:form>
<form:form action="/studybuilder/adminStudies/studyList.do" id="backOrCancelForm"
           name="backOrCancelForm" method="post">
</form:form>

<script type="text/javascript">
  $(document).ready(function () {
	  
    $('.addEditStudy').on('click', function () {
      $('#addEditStudyForm').submit();
    });

//cancel or back click
    $('.backOrCancelBtn').on('click', function () {
      $('#backOrCancelForm').submit();
    });

    <c:if test="${studyListId eq true}">
    $('#studyListId').show();
    </c:if>
    var sucMsg = "${sucMsg}";
    if (sucMsg.length > 0) {
      showSucMsg(sucMsg);
    }
    var errMsg = '${errMsg}';
    if (errMsg.length > 0) {
      showErrMsg(errMsg);
    }

    var resourceErrMsg = '${resourceErrMsg}';
    if (resourceErrMsg) {
      bootbox.alert(resourceErrMsg);
    }

    var actionSucMsg = '${actionSucMsg}';
    if (actionSucMsg) {
    	showSucMsg(actionSucMsg);
    }
  });

  function showSucMsg(message) {
    $("#alertMsg").removeClass('e-box').addClass('s-box').text(message);
    $('#alertMsg').show('5000');
    setTimeout(hideDisplayMessage, 5000);
  }

  function showErrMsg(message) {
    $("#alertMsg").removeClass('s-box').addClass('e-box').text(message);
    $('#alertMsg').show('5000');
    setTimeout(hideDisplayMessage, 5000);
  }

  function hideDisplayMessage() {
    $('#alertMsg').slideUp('5000');
  }
  
  function importStudy() {
	   var bb=  bootbox.prompt({ 
		  title: "Import a study",
		  inputType: "text",
  		  placeholder: "Enter a valid signed URL",
           dataError: "Please enter a valid URL",
           required: true,
           closeButton: false,
		    buttons: {
		          'cancel': {
		            label: 'Cancel',
		          },
		          'confirm': {
		            label: 'Submit',
		          },
		        },
		    callback: function (result) {
		    	if(result == null){
		    		return;
		    	}
		    	
		    	var decodedURL = unescape(result);
		    	var storagePath = "${sessionObject.storagePath}";
		    	if(decodedURL !=null && !(decodedURL.startsWith(storagePath) && decodedURL.includes("Expires="))){
		    		showErrMsg("Please enter a valid URL");
		           }else if(decodedURL !=null){
		        	   if(validateExpireDate(decodedURL)){
	            	    $.ajax({
	                      url: "/studybuilder/studies/import.do?_S=${param._S}",
	                      type: "POST",
	                      datatype: "json",
	                      data: {
	                        signedUrl: decodedURL,
	                        "${_csrf.parameterName}": "${_csrf.token}",
	                      },
	                      success: function emailValid(data, status) {
	                    	  message = data.message;
	                    	  if (message == "SUCCESS") {
	                    		  showSucMsg("Study imported successfully");
	                    		  window.location=window.location;
	                    		  setTimeout(hideDisplayMessage, 5000);
	                            } else if(message == "Please enter a valid URL"){
	                            	 showErrMsg(message);
	                            }else{
	                              bootbox.alert(message);
	                            }
	                          },
	                   error: function status(data, status) {
	                     $("body").removeClass("loading");
	                     showErrMsg("Import failed")
	                   }
	                 });
	              } 
		       }
		    }
		        
	  });
  }
  

  function validateExpireDate(result){

	 var index= result.search("Expires=");
     var expire = result.substring(index, result.indexOf('&', index));
	 var expireTimeStamp= expire.split("=");
	 if(expireTimeStamp[1] < Math.round(new Date().getTime()/1000)){
	    showErrMsg("The URL has expired. Please use a newly generated one.");
	    return false;
	 }
	    return true;
  }

</script>
