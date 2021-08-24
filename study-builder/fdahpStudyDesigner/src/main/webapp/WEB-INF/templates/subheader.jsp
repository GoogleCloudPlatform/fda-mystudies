<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.fdahpstudydesigner.util.SessionObject" %>


<style>
.modal-title {
    text-align: initial !important;
}

  .select-sup_text { font-size: 14px;
    line-height: 16px;
    color: #7c868d;
    font-weight: 500;
    padding-left:3%;
  }
  
  .select-sub_text { padding-left:13%;}
  .select_drop_parent {
  	position: absolute;
    display: contents;
    }
      .custom_checkbox_dropdown { 
      background: #d9e1e9;
      left: 20px;
      }
      
    .custom_checkbox_dropdown > li >a {
    padding: 0px 20px;
    
}

.dropdown_chk_box {
 position: absolute;
display: inline-block;
}

.dropdown_chk_box button { 
width:200px;
text-align: left;
color: #95a2ab;
}

.dropdown_chk_box button .caret {
    float: right !important;
    margin-top: 8px !important;
}


.dropdown_chk_box .btn-default.active.focus,
 .dropdown_chk_box .btn-default.active:focus, 
 .dropdown_chk_box .btn-default.active:hover,
  .dropdown_chk_box .btn-default:active.focus,
   .dropdown_chk_box .btn-default:active:focus,
    .dropdown_chk_box .btn-default:active:hover, 
    .open>.dropdown-toggle.btn-default.focus,
     .open>.dropdown-toggle.btn-default:focus, 
     .open>.dropdown-toggle.btn-default:hover, 
     .bootstrap-select .dropdown-toggle:focus {
     color: #95a2ab;
     }


.custom_checkbox_dropdown > li >a:hover {
    color: #2d2926 !important;
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
      
      
        <div class="dropdown dropdown_chk_box pl-lg">
      <button class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown">
        <span class="dropdown-text">-- Select apps --</span>
        <span class="caret"></span>
      </button>
      <ul class="dropdown-menu custom_checkbox_dropdown">
        <li>
          <a href="#">
            <label>
              <input type="checkbox" class="selectall" />
              <span class="select-sub_text"> All</span> apps </label>
          </a>
        </li>
        <li class="divider"></li>
        <li>
          <a class="option-link" href="#">
            <label>
              <input name='options[]' type="checkbox" class="option justone" value='Option 1 ' /> 
              <div class="select_drop_parent"> 
 <span class="select-sup_text"> APP94789 | Standalone</span> 
  <div class="clearfix"></div> 
  <span class="select-sub_text"> Arthritis Research APP </span> </div> </label>
 </label>
          </a>
        </li>
        <li>
          <a href="#">
            <label>
              <input name='options[]' type="checkbox" class="option justone" value='Option 2 ' /> 
               <div class="select_drop_parent"> 
              <span class="select-sup_text"> APP94789 | Standalone</span>
 <div class="clearfix"></div>
  <span class="select-sub_text"> COVID19 Research APP</span> </div>
               </label>
          </a>
        </li>
        <li>
          <a href="#">
            <label>
              <input name='options[]' type="checkbox" class="option justone" value='Option 3 ' /> 
               <div class="select_drop_parent"> 
               <span class="select-sup_text"> APP94789 | Standalone</span> 
                <div class="clearfix"></div> 
                <span class="select-sub_text"> COVID19 Research APP</span>
                </div>
              </label>
          </a>
        </li>
      </ul>
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
    
    var sucMsgAppActions = '${sucMsgAppActions}';
    if (sucMsgAppActions) {
    	showSucMsg(sucMsgAppActions);
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
