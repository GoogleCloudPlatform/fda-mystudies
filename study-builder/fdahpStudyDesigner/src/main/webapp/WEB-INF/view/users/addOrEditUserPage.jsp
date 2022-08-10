<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<style>
.disabled {
  pointer-events: none;
  cursor: default;
}

.filter-option {
  text-transform: inherit !important;
}

.app-addbtn {
    padding: 7px 15px;
    background: #036eb7;
    margin-left: 10px;
    color: #fff;
    border-radius: 5px;
    cursor: pointer;
}
button#deleteUser {
    background: #cf0036 !important;
    border-color: #cf0036 !important;
    color: #fff;
    padding: 4px 8px;
    text-align: center;
    margin-left: 9px;
}


input::-webkit-calendar-picker-indicator {
  display: none !important;
}


</style>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mt-md mb-md">

  <!-- widgets section-->
  <div class="col-sm-12 col-md-12 col-lg-12 p-none">
    <div class="black-lg-f">
      <span class="mr-xs">
        <a href="javascript:void(0)"
           class="backOrCancelBttn"><img
            src="/studybuilder/images/icons/back-b.png" alt=""/></a>
      </span>
      <c:if test="${actionPage eq 'ADD_PAGE'}">
        Add new admin
      </c:if>
      <c:if test="${actionPage eq 'EDIT_PAGE'}">
        Edit admin details
      </c:if>
      <c:if test="${actionPage eq 'VIEW_PAGE'}">
        Admin details
      </c:if>

    </div>
    <c:if test="${actionPage eq 'EDIT_PAGE' || actionPage eq 'VIEW_PAGE'}">
      <div class="dis-line pull-right">
        <div class="form-group mb-none">
          <c:if
              test="${not empty userBO.userPassword && userBO.enabled && userBO.emailChanged eq '0'}">
            <div class="dis-inline mt-sm">
              <span class="stat">
                <span class="black-sm-f">Account status:
                  <span
                      class="gray-xs-f mb-xs pl-xs"> Active
                  </span>
                </span>
              </span>
            </div>
          </c:if>
          <c:if
              test="${not empty userBO.userPassword &&  not userBO.enabled}">
            <div class="dis-inline mt-sm">
              <span class="black-sm-f">Account status:
                <span
                    class="gray-xs-f mb-xs pl-xs"> Deactivated
                </span>
              </span>
            </div>
          </c:if>
          <c:if test="${empty userBO.userPassword}">
            <div class="dis-inline mt-sm">
              <span class="black-sm-f">Account status:
                <span
                    class="gray-xs-f mb-xs pl-xs pr-md"> Invitation sent,
                   pending activation
                </span>
              </span>
		   <c:choose>
			         <c:when test="${actionPage eq 'VIEW_PAGE'}">
			              <span class="black-sm-f resend pl-md">
			                <a  href="javascript:void(0)"  id="resendLinkId" class="disabled">Re-send
			                  activation link
			                </a>
			              </span>
			         </c:when>
			         <c:otherwise>
			           		<span class="black-sm-f resend pl-md">
			                <a  href="javascript:void(0)" id="resendLinkId" >Re-send
			                  activation link
			                </a>
			              </span>
			         </c:otherwise>
	      	 </c:choose> 
            </div>
          </c:if>
          <c:if test="${userBO.emailChanged eq '1'}">
            <div class="dis-inline mt-sm">
              <span class="black-sm-f">Account status:
                <span
                    class="gray-xs-f mb-xs pl-xs"> Pending verification
                </span>
              </span>
            </div>
          </c:if>
        </div>
      </div>
    </c:if>

  </div>

</div>

<form:form
    action="/studybuilder/adminUsersEdit/addOrUpdateUserDetails.do"
    data-toggle="validator" id="userForm" role="form" method="post"
    autocomplete="off">
  <input type="hidden" name="userId" value="${userBO.userId}">
  <input type="hidden" id="userStatus" name="enabled"
         value="${userBO.enabled}">
  <input type="hidden" id="selectedStudies" name="selectedStudies">
  <input type="hidden" id="permissionValues" name="permissionValues">
  <input type="hidden" id="permissionValuesForApp" name="permissionValuesForApp">
  <input type="hidden" id="selectedApps" name="selectedApps">
  <input type="hidden" name="ownUser" id="ownUser">
  <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
    <div class="white-bg box-space">
      <c:if
          test="${actionPage eq 'EDIT_PAGE' && not empty userBO.userPassword && userBO.emailChanged eq '0'}">
        <c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_SUPERADMIN')}">
          <div class="gray-xs-f text-weight-semibold pull-right">
            <button type="button" class="btn btn-default gray-btn"
                    id="enforcePasswordId">Enforce password change
            </button>
          </div>
        </c:if>
      </c:if>
      <div class="ed-user-layout row">
        <!-- Edit User Layout-->

        <div class="blue-md-f text-uppercase mb-md">Admin Information</div>
        <div class="col-md-12 p-none">
          <!-- form- input-->
          <div class="col-md-6 pl-none">
            <div class="gray-xs-f mb-xs">
              First name
              <span class="requiredStar"> *</span>
            </div>
            <div class="form-group">
              <input autofocus="autofocus" type="text" class="form-control"
                     name="firstName" value="${fn:escapeXml(userBO.firstName)}"
                     maxlength="50" required data-error="Please fill out this field"
                     <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if> />
              <div class="help-block with-errors red-txt"></div>
            </div>
          </div>
          <!-- form- input-->
          <div class="col-md-6 pr-none">
            <div class="gray-xs-f mb-xs">
              Last name
              <span class="requiredStar"> *</span>
            </div>
            <div class="form-group">
              <input type="text" class="form-control" name="lastName"
                     value="${fn:escapeXml(userBO.lastName)}" maxlength="50" required data-error="Please fill out this field"
                     <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if> />
              <div class="help-block with-errors red-txt"></div>
            </div>
          </div>
        </div>


        <div class="col-md-12 p-none">
          <!-- form- input-->
          <div class="col-md-6 pl-none">
            <div class="gray-xs-f mb-xs">
              Email
              <c:if test="${actionPage ne 'VIEW_PAGE'}">&nbsp;<small>(100
                characters max)</small>
              </c:if>
              <span class="requiredStar"> *</span>
            </div>
            <div class="form-group myarrow">
              <input type="text" class="form-control" id="emailId"
                     name="userEmail" value="${userBO.userEmail}"
                     oldVal="${userBO.userEmail}"
                     pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,24}$"
                     data-pattern-error="Email address is invalid" data-error="Please fill out this field" maxlength="100"
                     required
                     <c:if
                         test="${actionPage eq 'VIEW_PAGE' || (empty userBO.userPassword && not empty userBO)}">disabled</c:if> />
              <div class="help-block with-errors red-txt"></div>
            </div>
          </div>
          <!-- form- input-->
          <div class="col-md-6 pr-none">
            <div class="gray-xs-f mb-xs">
              Phone (XXX - XXX - XXXX)
            </div>
            <div class="form-group">
              <input type="text" class="form-control phoneMask"
                     name="phoneNumber" value="${userBO.phoneNumber}"
                     data-minlength="12" maxlength="12" 
                     <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if> />
              <div class="help-block with-errors red-txt"></div>
            </div>
          </div>
        </div>

        <div class="clearfix"></div>
        <!-- Assign Role Section -->
        <div class="col-md-12 p-none">
          <!-- form- input-->
          <div class="col-md-6 pl-none">
            <div class="blue-md-f mt-lg mb-md">
              Role
              <span class="requiredStar"> *</span>
              <span data-toggle="tooltip" data-placement="top" title="" class="filled-tooltip" data-original-title="Superadmin users have application-wide permissions. They can manage admins of the Study Builder and in addition, can manage app-level notifications and studies as well. Non-superadmins or 'study admins' will have permissions-based access to specific sections and studies only." aria-describedby="tooltip739612"></span>
            </div>
            <div class="form-group">
              <select id="roleId"
                      class="selectpicker <c:if test="${actionPage eq 'VIEW_PAGE'}">linkDis</c:if>"
                      name="roleId" required data-error="Please fill out this field">
                <option value="" selected disabled>- Select Role -</option>
                <c:forEach items="${roleBOList}" var="role">
                  <option ${role.roleId eq userBO.roleId ? 'selected' : ''}
                      value="${role.roleId}">${role.roleName}</option>
                </c:forEach>
              </select>
              <div class="help-block with-errors red-txt"></div>
            </div>
          </div>
          <c:if test="${actionPage ne 'ADD_PAGE'}">
            <div class="col-md-6 pl-none">
              <div class="blue-md-f text-uppercase mt-lg mb-md">&nbsp;&nbsp;&nbsp;&nbsp;Activate
                / Deactivate
              </div>
              <div class="form-group mb-none">
                <c:if
                    test="${actionPage eq 'EDIT_PAGE' || actionPage eq 'VIEW_PAGE'}">
                  <span class="ml-xs">&nbsp; <label
                      class="switch bg-transparent mt-xs"> <input
                      type="checkbox" class="switch-input"
                      value="${userBO.enabled}" id="change${userBO.userId}"
                      <c:if test="${userBO.enabled}">checked</c:if>
                      <c:if
                          test="${empty userBO.userPassword || actionPage eq 'VIEW_PAGE' || userBO.emailChanged eq '1'}">disabled</c:if>
                      onclick="activateOrDeactivateUser('${userBO.userId}');">
                    <span class="switch-label bg-transparent" data-on="On"
                          data-off="Off"></span>
                    <span class="switch-handle"></span>
                  </label>
                  </span>
                </c:if>
              </div>
            </div>
          </c:if>
        </div>

        <div class="clearfix"></div>
        <!-- Assign Permissions -->
        <div class="blue-md-f text-uppercase mt-lg perm-assign">Assigned Permissions</div>
        <div class="pull-right mb-xs">
          <span class="gray-xs-f">View only</span>
          <span
              class="gray-xs-f ml-lg">View and edit
          </span>
        </div>
        <div class="clearfix"></div>

  <!--  Manage apps div  --> 
  
        <!-- Gray Widget-->
        <div class="edit-user-list-widget">
          <span class="checkbox checkbox-inline"><input
              type="checkbox" id="inlineCheckboxApp" name="manageApps"
              <c:if test="${fn:contains(permissions,10)}">value="1" checked</c:if>
              <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
            <label for="inlineCheckboxApp"> Manage apps </label>
          </span>
          <div class="mt-lg pl-lg">
            <div class="pb-md bor-dashed">
              <span class="checkbox checkbox-inline dis-checkbox-app"><input
                  type="checkbox" id="inlineCheckbox6" class="changeView2"
                  name="addingNewApp"
                  value="${fn:contains(permissions,9)?'1':''}"
              <c:if test="${fn:contains(permissions,9)}"> checked</c:if>
                  <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                <label for="inlineCheckbox6"> Create new apps </label>
              </span>
            </div>
            <div class="mt-md mb-md addHide app-list">
              <c:if test="${actionPage ne 'VIEW_PAGE'}">
                <select
                    class="selectpicker col-md-6 p-none changeView3 <c:if test="${actionPage eq 'VIEW_PAGE'}">linkDis</c:if>"
                    title="- Select and add apps -" multiple id="multipleApps" >
                     <c:if test="${empty apps}">
                     <option value="" id="">No app records found
                     </option>
                      </c:if>
                  <c:forEach items="${apps}" var="app">
                    <option value="${app.id}"
                            id="selectApps${app.id}">${app.name}&nbsp;(${app.customAppId})
                    </option>
                  </c:forEach>
                </select>
                <span class="app-addbtn changeView3">+</span>
              </c:if>
            </div>
            <div class="addHide">
              <span
                  class="mr-lg text-weight-bold">List of assigned apps with permissions
              </span>

              <c:if test="${actionPage ne 'VIEW_PAGE'}">
                <span class="ablue removeAllApps changeView3">x Remove all</span>
              </c:if>
            </div>
            <!-- Selected App items -->
            <div class="app-selected mt-md" >
              <c:forEach items="${appBos}" var="app">
                <div class="app-selected-item selApp" id="app${app.id}">
                  <input type="hidden" class="appCls" id="${app.id}" name=""
                         value="${app.id}"
                         appTxt="${app.name}&nbsp;(${app.customAppId})"
                         <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                  <c:if test="${actionPage ne 'VIEW_PAGE'}">
                    <span class="mr-md"><img
                        src="/studybuilder/images/icons/close.png"
                        onclick="delApp('${app.id}');" alt=""/></span>
                  </c:if>
                  <span>${app.name}&nbsp;(${app.customAppId})</span>
                  <span
                      class="pull-right">
                    <span
                        class="radio radio-info radio-inline p-45 mr-xs"><input
                        type="radio" class="v3${app.id} changeView3"
                        id="v4${app.id}" name="radio${app.id}" value="0"
                        <c:if test="${not app.viewPermission}">checked</c:if>
                        <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                      <label for="v4${app.id}"></label></span>
                    <span
                        class="radio radio-inline"><input type="radio"
                                                          class="v3${app.id} changeView3"
                                                          id="v5${app.id}"
                                                          name="radio${app.id}" value="1"
                                                          <c:if
                                                              test="${app.viewPermission}">checked</c:if>
                                                          <c:if
                                                              test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                      <label for="v5${app.id}"></label>
                    </span>
                  </span>
                </div>
              </c:forEach>
            </div>
          </div>
        </div>
        
   <!--  Manage studies div  -->     
          <!-- Gray Widget-->
        <div class="edit-user-list-widget mt-xxlg">
          <span class="checkbox checkbox-inline"><input
              type="checkbox" id="inlineCheckbox4" name="manageStudies"
              <c:if test="${fn:contains(permissions,2)}">value="1" checked</c:if>
              <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
            <label for="inlineCheckbox4"> Manage studies </label>
          </span>
          <div class="mt-lg pl-lg">
            <div class="pb-md bor-dashed">
              <span class="checkbox checkbox-inline dis-checkbox-st"><input
                  type="checkbox" id="inlineCheckbox5" class="changeView1"
                  name="addingNewStudy"
                  value="${fn:contains(permissions,8)?'1':''}"
              <c:if test="${fn:contains(permissions,8)}"> checked</c:if>
                  <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                <label for="inlineCheckbox5"> Create new studies </label>
              </span>
            </div>
            <div class="mt-md study-list mb-md addHide">
              <c:if test="${actionPage ne 'VIEW_PAGE'}">
                <select
                    class="selectpicker col-md-6 p-none changeView <c:if test="${actionPage eq 'VIEW_PAGE'}">linkDis</c:if>"
                    title="- Select and add studies -" multiple id="multiple">
                  <c:forEach items="${studyBOList}" var="study">
                    <option value="${study.id}"
                            id="selectStudies${study.id}">${study.name}&nbsp;(${study.customStudyId})
                    </option>
                  </c:forEach>
                </select>
                <span class="study-addbtn changeView">+</span>
              </c:if>
            </div>
            <div class="addHide">
              <span
                  class="mr-lg text-weight-bold">List of assigned studies with permissions
              </span>

              <c:if test="${actionPage ne 'VIEW_PAGE'}">
                <span class="ablue removeAll changeView">x Remove all</span>
              </c:if>
            </div>
            <!-- Selected Study items -->
            <div class="study-selected mt-md">
              <c:forEach items="${studyBOs}" var="study">
                <div class="study-selected-item selStd" id="std${study.id}">
                  <input type="hidden" class="stdCls" id="${study.id}" name=""
                         value="${study.id}"
                         stdTxt="${study.name}&nbsp;(${study.customStudyId})"
                         <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                  <c:if test="${actionPage ne 'VIEW_PAGE'}">
                    <span class="mr-md"><img
                        src="/studybuilder/images/icons/close.png"
                        onclick="del('${study.id}');" alt=""/></span>
                  </c:if>
                  <span>${study.name}&nbsp;(${study.customStudyId})</span>
                  <span
                      class="pull-right">
                    <span
                        class="radio radio-info radio-inline p-45 mr-xs"><input
                        type="radio" class="v${study.id} changeView"
                        id="v1${study.id}" name="radio${study.id}" value="0"
                        <c:if test="${not study.viewPermission}">checked</c:if>
                        <c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                      <label for="v1${study.id}"></label></span>
                    <span
                        class="radio radio-inline"><input type="radio"
                                                          class="v${study.id} changeView"
                                                          id="v2${study.id}"
                                                          name="radio${study.id}" value="1"
                                                          <c:if
                                                              test="${study.viewPermission}">checked</c:if>
                                                          <c:if
                                                              test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
                      <label for="v2${study.id}"></label>
                    </span>
                  </span>
                </div>
              </c:forEach>
            </div>
          </div>
        </div>
        
      </div>
    </div>
  </div>
  <c:if test="${actionPage ne 'VIEW_PAGE'}">
    <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
      <div class="white-bg box-space t-bor text-right">
        <div class="dis-line text-right ml-md">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button"
                    class="btn btn-default gray-btn backOrCancelBttn">Cancel
            </button>
          </div>
          <c:if test="${actionPage eq 'ADD_PAGE'}">
            <div class="dis-line form-group mb-none">
              <button type="button" class="btn btn-primary blue-btn addUpdate">Add</button>
            </div>
          </c:if>
          <c:if test="${actionPage eq 'EDIT_PAGE'}">
            <div class="dis-line form-group mb-none">
              <button type="button" class="btn btn-primary blue-btn addUpdate">Update</button>
            </div>
            </c:if>
          <c:if test="${actionPage eq 'EDIT_PAGE' &&  not userBO.enabled}">  
            <div class="dis-line">
              <button type="button" class="btn btn-primary red-btn deleteUser" id = "deleteUser" onclick="validateAdminStatus(this);">Delete admin</button>
            </div>
          </c:if>
        </div>
      </div>
    </div>
  </c:if>
</form:form>

<form:form action="/studybuilder/adminUsersView/getUserList.do"
           id="backOrCancelBtnForm" name="backOrCancelBtnForm" method="post">
</form:form>
<script>


  $(document).ready(function () {
    $('#rowId').parent().removeClass('white-bg');
    <c:if test="${empty studyBOList && empty studyBOs}">
    $('.addHide').hide();
    </c:if>

    $('#users').addClass('active');

    $('[data-toggle="tooltip"]').tooltip();

    var isManageStudyChecked = $("#inlineCheckbox4").is(":checked");
    var isManageAppsChecked = $("#inlineCheckboxApp").is(":checked");
    if (!isManageStudyChecked) {
      $('#inlineCheckbox5').val('');
      $('#inlineCheckbox5').prop('checked', false);
      $('.changeView').prop('disabled', true);
      $('.changeView').selectpicker('refresh');
      $('.changeView1').prop('disabled', true);
    }
    
    if(!isManageAppsChecked){
    	$('#inlineCheckbox6').val('');
        $('#inlineCheckbox6').prop('checked', false);
        $('.changeView3').prop('disabled', true);
        $('.changeView3').selectpicker('refresh');
        $('.changeView2').prop('disabled', true);
    }
    
    var role = '${userBO.roleName}';
    <c:if test="${actionPage ne 'VIEW_PAGE'}">
    if (role) {
      setStudySettingByRole(role);
    }
    </c:if>

    <c:if test="${actionPage eq 'ADD_PAGE'}">
    $('.edit-user-list-widget').hide();
 	 $('.perm-assign').hide();
 	 $('.pull-right').hide();
    </c:if>
    
   <c:if test="${actionPage eq 'EDIT_PAGE'}">
   $(".selectpicker").selectpicker('deselectAll');
   var tot_items = $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").length;
   var count = $(".app-selected-item").length;
   if (count == tot_items) {
 	  $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").hide()
     $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu").append(
     	$("<li> </li>").attr("class","text-center").text("- All items are already selected -"));
   }
    </c:if> 

    <c:if test="${actionPage eq 'EDIT_PAGE' || actionPage eq 'VIEW_PAGE'}">
    if($('#roleId').find('option:selected').text() == 'Superadmin' ){
    $('.edit-user-list-widget').hide();
 	 $('.perm-assign').hide();
 	 $('.pull-right').hide();
    }
    </c:if>

   
    
    $('#roleId').on('change', function () {
      var element = $(this).find('option:selected').text();
 	 if(element != 'Superadmin' ){
    	 $('#enforcePasswordId').hide(); 
    	 }else $('#enforcePasswordId').show(); 
      setStudySettingByRole(element);
    });

   
    var countCall = 0;
    $(window).on('load', function () {
      countCall = 1;
      $('.selStd').each(function () {
        var stdTxt = $(this).find('.stdCls').attr('stdTxt');
        $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
            function () {
              var ltxt = $(this).text();
              var a = $.trim(ltxt);
              var b = $.trim(stdTxt);
              if (a == b) {
                $(this).parent().parent().hide();
              }
            });
      });
    });
    
    var countCall2 = 0;
    $(window).on('load', function () {
    	countCall2 = 1;
      $('.selApp').each(function () {
        var appTxt = $(this).find('.appCls').attr('appTxt');
        $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
            function () {
              var ltxt = $(this).text();
              var a = $.trim(ltxt);
              var b = $.trim(appTxt);
              if (a == b) {
                $(this).parent().parent().hide();
              }
            });
      });
    });


    if (countCall == 0) {
      $('.selStd').each(function () {
        var stdTxt = $(this).find('.stdCls').attr('stdTxt');
        $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
            function () {
              var ltxt = $(this).text();
              var a = $.trim(ltxt);
              var b = $.trim(stdTxt);
              if (a == b) {
                $(this).parent().parent().hide();
              }
            });
      });
    }
    
    if (countCall2 == 0) {
        $('.selApp').each(function () {
          var appTxt = $(this).find('.appCls').attr('appTxt');
          $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
              function () {
                var ltxt = $(this).text();
                var a = $.trim(ltxt);
                var b = $.trim(appTxt);
                if (a == b) {
                  $(this).parent().parent().hide();
                }
              });
        });
      }
    
    $("#emailId").blur(function () {
      var email = $('#emailId').val().toLowerCase();
      var emailCopy = $('#emailId').val();
      var oldEmail = $('#emailId').attr('oldVal');
      var isEmail;
      var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
      isEmail = regEX.test(email);
      if (isEmail && ('' === oldEmail || ('' !== oldEmail && oldEmail !== email))) {
        var csrfDetcsrfParamName = $('#csrfDet').attr('csrfParamName');
        var csrfToken = $('#csrfDet').attr('csrfToken');
        $('#emailId').parent().find(".help-block").append($("<ul <li></li></ul>").attr("class","list-unstyled"));
        if (email !== '') {
          $.ajax({
            url: "/studybuilder/isEmailValid.do?" + csrfDetcsrfParamName + "=" + csrfToken,
            type: "POST",
            datatype: "json",
            global: false,
            data: {
              email: email,
            },
            success: function getResponse(data) {
              var message = data.message;
              if ('SUCCESS' !== message) {
                $('#emailId').validator('validate');
                $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
                $('#emailId').parent().find(".help-block").empty();
              } else {
                $("body").removeClass("loading");
                $('#emailId').val('');
                $('#emailId').parent().addClass("has-danger").addClass("has-error");
                $('#emailId').parent().find(".help-block").empty();
                $('#emailId').parent().find(".help-block").append(
            	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(emailCopy + " already exists"));
              }
            }
          });
        }
      } else {
        $('#emailId').validator('validate');
        $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
        $('#emailId').parent().find(".help-block").empty();
      }
    });

    //cancel or back click
    $('.backOrCancelBttn').on('click', function () {
      $('#backOrCancelBtnForm').submit();
    });

    if ($('#inlineCheckbox1').prop("checked") == false) {
      $('.musr').prop('checked', false);
      $('.musr').prop('disabled', true);
    }

   

    if ($('#inlineCheckbox4').prop("checked") == false) {
      $('#inlineCheckbox5').prop('checked', false);
      $('#inlineCheckbox5').prop('disabled', true);
    }
    
    if ($('#inlineCheckboxApp').prop("checked") == false) {
        $('#inlineCheckbox6').prop('checked', false);
        $('#inlineCheckbox6').prop('disabled', true);
      }

    $('#inlineCheckbox1').on('click', function () {
      if ($(this).prop("checked") == true) {
        $('.musr').prop('disabled', false);
        $('#inlineRadio1').prop('checked', true);
      } else if ($(this).prop("checked") == false) {
        $('.musr').prop('checked', false);
        $('.musr').prop('disabled', true);
      }
    });

    
    $('#inlineCheckbox4').on('click', function () {
      if ($(this).prop("checked") == true) {
        $(this).val(1);
        $('.changeView').prop('disabled', false);
        $('.changeView').selectpicker('refresh');
        $('.dis-checkbox-st').removeClass('disabled', 'disabled');
        var element = $("#roleId option:selected").text();
        if (element == 'Org-level Admin') {
          $('.changeView1').prop('disabled', true);
        } else {
          $('.changeView1').prop('disabled', false);
        }
        
        $('.selStd').each(function () {
            var stdTxt = $(this).find('.stdCls').attr('stdTxt');
            $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
                function () {
                  var ltxt = $(this).text();
                  var a = $.trim(ltxt);
                  var b = $.trim(stdTxt);
                  if (a == b) {
                    $(this).parent().parent().hide();
                  }
                });
          });
       
        
      } else if ($(this).prop("checked") == false) {
        $(this).val('');
        $('#inlineCheckbox5').val('');
        $('#inlineCheckbox5').prop('checked', false);
        $('.changeView').prop('disabled', true);
        $('.changeView').selectpicker('refresh');
        $('.changeView1').prop('disabled', true);
      }
    });
    
    $('#inlineCheckboxApp').on('click', function () {
        if ($(this).prop("checked") == true) {
          $(this).val(1);
          $('.changeView3').prop('disabled', false);
          $('.changeView3').selectpicker('refresh');
          $('.dis-checkbox-app').removeClass('disabled', 'disabled');
          var element = $("#roleId option:selected").text();
          if (element == 'Org-level Admin') {
            $('.changeView2').prop('disabled', true);
          } else {
            $('.changeView2').prop('disabled', false);
          }
          
          $('.selApp').each(function () {
  	          var appTxt = $(this).find('.appCls').attr('appTxt');
  	          $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
  	              function () {
  	                var ltxt = $(this).text();
  	                var a = $.trim(ltxt);
  	                var b = $.trim(appTxt);
  	                if (a == b) {
  	                  $(this).parent().parent().hide();
  	                }
  	              });
  	        });
          
        } else if ($(this).prop("checked") == false) {
          $(this).val('');
          $('#inlineCheckbox6').val('');
          $('#inlineCheckbox6').prop('checked', false);
          $('.changeView3').prop('disabled', true);
          $('.changeView3').selectpicker('refresh');
          $('.changeView2').prop('disabled', true);
        }
      });

    $('#inlineCheckbox5').on('click', function () {
      if ($(this).prop("checked") == true) {
        $(this).val(1);
      } else if ($(this).prop("checked") == false) {
        $(this).val('');
      }
    });
    
    $('#inlineCheckbox6').on('click', function () {
        if ($(this).prop("checked") == true) {
          $(this).val(1);
           } else if ($(this).prop("checked") == false) {
          $(this).val('');
        }
          
      });
    
    // Adding selected study items
    $(".study-addbtn").click(function () {
    	var noSelected = $('#multiple :selected').length;
  if(noSelected != 0 ){
      $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li.selected").hide();

      $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function () {
        if ($(this).text() == "- All items are already selected -") {
          $(this).remove();
        }
      });
	    if ($('#inlineCheckbox4').prop("checked") == true) {
      $('#multiple :selected').each(function (i, sel) {
        var selVal = $(sel).val();
        var selTxt = DOMPurify.sanitize($(sel).text());
        var existingStudyDiv = "<div class='study-selected-item selStd' id='std" + selVal + "'>"
            + "<input type='hidden' class='stdCls' id='" + selVal + "' name='' value='" + selVal +"'"
            + "stdTxt='"+selTxt+"'>"
            + "<span class='mr-md cls cur-pointer'><img src='/studybuilder/images/icons/close.png' onclick='del(\""
            + selVal + "\");'/></span>"
            + "<span>" + selTxt + "</span>"
            + "<span class='pull-right'>"
            + "<span class='radio radio-info radio-inline p-45 mr-xs'>"
            + " <input type='radio' class='v" + selVal + " changeView' id='v1" + selVal
            + "' name='radio" + selVal + "' value='0' checked='checked'>"
            + "<label for='v1" + selVal + "'style='padding-left:8px;'></label></span>"
            + "<span class='radio radio-inline'>"
            + "<input type='radio' class='v" + selVal + " changeView' id='v2" + selVal
            + "' name='radio" + selVal + "' value='1'>"
            + " <label for='v2" + selVal + "'></label>"
            + "</span>"
            + "</span>"
            + "</div>";

        $('.study-selected').append(existingStudyDiv);
      });
       } else if ($('#inlineCheckbox4').prop("checked") == false) {
          $(this).val('');
          
        }
	if ($(".changeView").find('.dropdown-menu').is(":hidden")){
	    $('.dropdown-toggle').dropdown('toggle');
	    var show_elements_count1 = $( ".study-list .dropdown-menu ul.dropdown-menu.inner" ).find( ":visible" ).length;
	  }
  else var show_elements_count2 = $( ".study-list .dropdown-menu ul.dropdown-menu.inner" ).find( ":visible" ).length;

	//var show_elements_count = $( ".study-list .dropdown-menu ul.dropdown-menu.inner" ).find( ":visible" ).length;
      $(".selectpicker").selectpicker('deselectAll');
      var tot_items = $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").length;
      var count = $(".study-selected-item").length;
   
      if (show_elements_count1 == 0 || show_elements_count2 == 0) {
       	  $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").hide()
           $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu").append(
           	$("<li> </li>").attr("class","text-center").text("- All items are already selected -"));
         }
     
    }
     
    });
    
    
 // Adding selected app items
    $(".app-addbtn").click(function () {
    	 $('#inlineCheckbox4').prop('disabled', false);
    	 $('#inlineCheckbox5').prop('disabled', false);
      $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li.selected").hide();

      $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function () {
        if ($(this).text() == "- All items are already selected -") {
          $(this).remove();
        }
      });
	 if ($('#inlineCheckboxApp').prop("checked") == true) {
		
      $('#multipleApps :selected').each(function (i, sel) {
        var selVal = $(sel).val();
        var selTxt = DOMPurify.sanitize($(sel).text());
        var existingAppDiv = "<div class='selApp app-selected-item' id='app" + selVal + "'>"
            + "<input type='hidden' class='appCls' id='" + selVal + "' name='' value='" + selVal +"'"
            + "appTxt='"+selTxt+"'>"
            + "<span class='mr-md cls cur-pointer'><img src='/studybuilder/images/icons/close.png' onclick='delApp(\""
            + selVal + "\");'/></span>"
            + "<span>" + selTxt + "</span>"
            + "<span class='pull-right'>"
            + "<span class='radio radio-info radio-inline p-45 mr-xs'>"
            + " <input type='radio' class='v3" + selVal + " changeView3' id='v4" + selVal
            + "' name='radio" + selVal + "' value='0' checked='checked'>"
            + "<label for='v4" + selVal + "'style='padding-left:8px;'></label></span>"
            + "<span class='radio radio-inline'>"
            + "<input type='radio' class='v3" + selVal + " changeView3' id='v5" + selVal
            + "' name='radio" + selVal + "' value='1'>"
            + " <label for='v5" + selVal + "'></label>"
            + "</span>"
            + "</span>"
            + "</div>";

        $('.app-selected').append(existingAppDiv);
      });
           } else if ($('#inlineCheckboxApp').prop("checked") == false) {
      			$(this).val('');
      		}
      
      
     
      $(".selectpicker").selectpicker('deselectAll');
      var tot_items = $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").length;
      var count = $(".app-selected-item").length;
      if (count == tot_items) {
    	  $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").hide()
        $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu").append(
        	$("<li> </li>").attr("class","text-center").text("- All items are already selected -"));
      }
    });

//Removing selected study items
    $(".removeAll").click(function () {
      $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li[style],.study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").show();
      $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function () {
        if ($(this).text() == "- All items are already selected -") {
        	$(this).remove();
        }
      });
     
      $(".study-selected-item").remove();
    });
    
  //Removing selected app items
    $(".removeAllApps").click(function () {
      $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li[style],.study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").show();
      $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function () {
        if ($(this).text() == "- All items are already selected -") {
        	$(this).remove();
        }
      });
     
      $(".app-selected-item").remove();
    });

    $('.addUpdate').on('click', function () {
    	var enforce=0;
      var email = $('#emailId').val();
      var oldEmail = $('#emailId').attr('oldVal');
      var isEmail;
      var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
      isEmail = regEX.test(email);
      if (isEmail && ('' === oldEmail || ('' !== oldEmail && oldEmail !== email))) {
        var csrfDetcsrfParamName = $('#csrfDet').attr('csrfParamName');
        var csrfToken = $('#csrfDet').attr('csrfToken');
        $('#emailId').parent().find(".help-block").append($("<ul <li></li></ul>").attr("class","list-unstyled"));
        if (email !== '') {
          $("body").addClass("loading");
          $.ajax({
            url: "/studybuilder/isEmailValid.do?" + csrfDetcsrfParamName + "=" + csrfToken,
            type: "POST",
            datatype: "json",
            global: false,
            data: {
              email: email,
            },
            success: function getResponse(data) {
              var message = data.message;
              if ('SUCCESS' !== message) {
                $('#emailId').validator('validate');
                $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
                $('#emailId').parent().find(".help-block").empty();
                saveUser();
                enforce=enforce+1;
              } else {
                $("body").removeClass("loading");
                isFromValid($('.addUpdate').parents('form'));
                $('#emailId').val('');
                $('#emailId').parent().addClass("has-danger").addClass("has-error");
                $('#emailId').parent().find(".help-block").empty();
                $('#emailId').parent().find(".help-block").append(
                	$("<ul><li> </li></ul>").attr("class","list-unstyled").text(email + " already exists"));
              }
            }
          });
        }
      } else {
        $('#emailId').validator('validate');
        $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
        $('#emailId').parent().find(".help-block").empty();
        saveUser();
        enforce=enforce+1;
      }
      if(enforce==2){
     	 $('#enforcePasswordId').show();
     }
    });

    

    $('#resendLinkId').on('click', function () {
      var form = document.createElement('form');
      form.method = 'post';
      var input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'userId';
      input.value = '${userBO.userId}';
      form.appendChild(input);

      input = document.createElement('input');
      input.type = 'hidden';
      input.name = '${_csrf.parameterName}';
      input.value = '${_csrf.token}';
      form.appendChild(input);

      form.action = '/studybuilder/adminUsersEdit/resendActivateDetailsLink.do';
      document.body.appendChild(form);
      form.submit();
    });

    $('#enforcePasswordId').on('click', function () {
      bootbox.confirm({
        closeButton: false,
        message: "Are you sure you wish to enforce a password change for this admin?",
        buttons: {
          'cancel': {
            label: 'No',
          },
          'confirm': {
            label: 'Yes',
          },
        },
        callback: function (result) {
          if (result) {
            var form = document.createElement('form');
            form.method = 'post';
            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'changePassworduserId';
            input.value = '${userBO.userId}';
            form.appendChild(input);

            var input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'emailId';
            input.value = '${userBO.userEmail}';
            form.appendChild(input);

            input = document.createElement('input');
            input.type = 'hidden';
            input.name = '${_csrf.parameterName}';
            input.value = '${_csrf.token}';
            form.appendChild(input);

            form.action = '/studybuilder/adminUsersEdit/enforcePasswordChange.do';
            document.body.appendChild(form);
            form.submit();
          }
        }
      })

    });

  });

  //delete selected study
  function del(id) {
    var atxt = $('#std' + id).children().text();

    $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
        function () {
          var ltxt = $(this).text();
          var a = $.trim(ltxt);
          var b = $.trim(atxt);
          if (a == b) {
            $(this).parent().parent().show();
          }
        });

    $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function () {
      if ($(this).text() == "- All items are already selected -") {
        $(this).remove();
      }
    });

    $('#std' + id).remove();

  }
  
//delete selected App
  function delApp(id) {
	    var atxt = $('#app' + id).children().text();
	    var selApps = $(".app-selected-item").length;

	    $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(
	        function () {
	          var ltxt = $(this).text();
	          var a = $.trim(ltxt);
	          var b = $.trim(atxt);
	          if (a == b) {
	            $(this).parent().parent().show();
	          }
	        });

	    $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function () {
	      if ($(this).text() == "- All items are already selected -") {
	        $(this).remove();
	      }
	    });

	    $('#app' + id).remove();
	  }

  function activateOrDeactivateUser(userId) {
    var status = $('#change' + userId).val();
    var msgPart = "";
    if ('false' == status) {
      msgPart = "activate";
    } else {
      msgPart = "deactivate";
    }
    bootbox.confirm("Are you sure you want to " + msgPart + " this admin?", function (result) {
      if (result) {
        if (status == 'true') {
          $('#change' + userId).val(false);
          $('#userStatus').val(false);
        } else {
          $('#change' + userId).val(true);
          $('#userStatus').val(true);
        }
      } else {
        if (status == 'true') {
          $('#change' + userId).prop('checked', true);
          $('#userStatus').val(true);

        } else if (status == 'false') {
          $('#change' + userId).prop('checked', false);
          $('#userStatus').val(false);
        }
        return;
      }
    });
  }

  function saveUser() {
    $('#emailId').prop('disabled', false);
        var element = $('#roleId').find('option:selected').text();
        if( element == "Study admin"){
        if($('#inlineCheckboxApp').is(":checked") || $('#inlineCheckbox6').is(":checked") 
    		|| $('#inlineCheckbox4').is(":checked") || $('#inlineCheckbox5').is(":checked")){
        	addUser();
        }else {
            $("body").removeClass("loading");
            showErrMsg("Please assign the admin at least one permission from the permissions set shown");
        }
  }else{
	  addUser();
  }
  }

  function setStudySettingByRole(element) {
    if (element == 'Org-level Admin') {
      $('#inlineCheckbox1').prop('checked', false);
      $('.changeView1').prop('checked', false);
      $('.changeView2').prop('checked', false);
      $('.musr').prop('checked', false);
      $('.mnotf').prop('checked', false);
      $('.musr').prop('disabled', true);
      $('.mnotf').prop('disabled', true);
      $('#inlineCheckbox1').attr('disabled', true);
      $('.changeView1').prop('disabled', true);
      $('.changeView2').prop('disabled', true);
      $('#inlineCheckbox5').prop('checked', false);
      $('#inlineCheckbox5').attr('disabled', true);
      $('#inlineCheckbox6').prop('checked', false);
      $('#inlineCheckbox6').attr('disabled', true);
    } else {
      $('#inlineCheckbox1').attr('disabled', false);
      $('#inlineCheckbox5').attr('disabled', false);
      $('#inlineCheckbox6').attr('disabled', false);
    }

  }

  $(document).on('mouseenter', '.dropdown-toggle',  function () {
      $(this).removeAttr("title");
  });
  

  $('#roleId').on('change', function () {
      var element = $(this).find('option:selected').text();
      if(element == "Study admin"){ 
      var actionPage = "${actionPage}";
      	 $('.edit-user-list-widget').show();
      	 $('.perm-assign').show();
      	 $('.pull-right').show();
      	 $('#inlineCheckbox5').val('');
	     $('#inlineCheckbox5').prop('checked', false);
	     $('.dis-checkbox-st').addClass('disabled', 'disabled');
	     $('#inlineCheckbox5').prop('checked', false);
	     $('.dis-checkbox-app').addClass('disabled', 'disabled');
	     if(actionPage == 'EDIT_PAGE' && "${userBO.accessLevel}"== "SUPERADMIN"){
	    	 $('#inlineCheckbox5').prop('checked', true);
		     $('.dis-checkbox-st').removeClass('disabled', 'disabled');
		     $('.dis-checkbox-app').removeClass('disabled', 'disabled');
		     $('#inlineCheckboxApp').prop('checked', true);
		     $('#inlineCheckbox6').prop('checked', true);
		     $('#inlineCheckboxApp').val(1);
		     $('.changeView3').prop('disabled', false);
	         $('.changeView3').selectpicker('refresh');
	     }
      	        
      	var tot_study = $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").length;
        var selected_study = $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li[style]").length;
        var tot_app = $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").length;
        var selected_app = $(".app-selected-item").length;
        
        if (selected_study == tot_study) {
      	  $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").hide()
          $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu").append(
          	$("<li> </li>").attr("class","text-center").text("- All items are already selected -"));
        }
       
        if (selected_app > 0 && actionPage == 'ADD_PAGE' ) {
        	 $(".app-selected-item").remove();
        }
        
        if(  selected_app == tot_app){
        	  $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").hide()
              $(".app-list .bootstrap-select .dropdown-menu ul.dropdown-menu").append(
              	$("<li> </li>").attr("class","text-center").text("- All items are already selected -"));
        }
          } else{
        	 $('.edit-user-list-widget').hide();
           	 $('.perm-assign').hide();
           	 $('.pull-right').hide();
        }
     
    });
  
  function showErrMsg(message) {
	    $("#alertMsg").removeClass('s-box').addClass('e-box').text(message);
	    $('#alertMsg').show('10000');
	    setTimeout(hideDisplayMessage, 10000);
	  }
  
  function validateAdminStatus(obj) {
	    var buttonText = obj.id;
	    var messageText = "";
	    if (buttonText) {
	      if (buttonText == 'deleteUser') {
	        	 messageText = "Are you sure you want to delete this admin?";
	        	 bootbox.confirm({
	                 closeButton: false,
	                 message: messageText,
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
	                	   deleteUserAdmin();
	                   }
	                 }
	               });}}}
  
function deleteUserAdmin(){
	     var form = document.createElement('form');
	      form.method = 'post';
	      var input = document.createElement('input');
	      input.type = 'hidden';
	      input.name = 'userId';
	      input.value = '${userBO.userId}';
	      form.appendChild(input);
	      input = document.createElement('input');
	      input.type = 'hidden';
	      input.name = '${_csrf.parameterName}';
	      input.value = '${_csrf.token}';
	      form.appendChild(input);
	     form.action = '/studybuilder/adminUsersView/deleteUser.do';
	     document.body.appendChild(form);
	     form.submit();
	  }
  function addUser(){
	  var selectedStudies = "";
	    var permissionValues = "";
	    var permissionValuesForApp = "";
	    
	  if (isFromValid($('.addUpdate').parents('form'))) {
	      $('.selStd').each(function () {
	        var studyId = $(this).find('.stdCls').val();
	        var permissionValue = $('#std' + studyId).find('input[type=radio]:checked').val();
	        if (selectedStudies == "") {
	          selectedStudies = studyId;
	        } else {
	          selectedStudies += "," + studyId;
	        }
	        if (permissionValues == "") {
	          permissionValues = permissionValue;
	        } else {
	          permissionValues += "," + permissionValue;
	        }
	      });
	      
	      var selectedApps = "";
	      $('.selApp').each(function () {
	          var appId = $(this).find('.appCls').val();
	          var permissionValueForApp = $('#app' + appId).find('input[type=radio]:checked').val();
	          if (selectedApps == "") {
	        	  selectedApps = appId;
	          } else {
	        	  selectedApps += "," + appId;
	          }
	          if (permissionValuesForApp == "") {
	        	  permissionValuesForApp = permissionValueForApp;
	          } else {
	        	  permissionValuesForApp += "," + permissionValueForApp;
	          }
	        });
	      
	      $('#selectedStudies').val(selectedStudies);
	      $('#selectedApps').val(selectedApps);
	      $('#permissionValues').val(permissionValues);
	      $('#permissionValuesForApp').val(permissionValuesForApp);
	      <c:if test="${sessionObject.userId eq userBO.userId}">
	      $('#ownUser').val('1');
	      </c:if>
	      $('.addUpdate').parents('form').submit();
	    }else {
	        $("body").removeClass("loading");
	    }
  }
</script>

