<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@page buffer="8192kb" autoFlush="true" %>
<style>
  <!--
  .sorting, .sorting_asc, .sorting_desc {
    background: none !important;
  }

  -->
  #app_Wide_table_list tr td {
    padding-left: 20px !important;
    }
    #app_Wide_table_list tr th {
    padding-left: 20px !important;
}


</style>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mb-md">
  <div>
    <!-- widgets section-->
    <div class="col-sm-12 col-md-12 col-lg-12 p-none">
      <div class="black-lg-f">
        Manage apps
      </div>
      <c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_SUPERADMIN') || 
      fn:contains(sessionObject.userPermissions,'ROLE_CREATE_MANAGE_APPS')}">
      <div class="dis-line pull-right ml-md">
          <div class="form-group mb-none mt-xs">
            <button type="button" class="btn btn-primary blue-btn applistDetailsToEdit"
                    actionType="add">
             Create new
            </button>
          </div>
      </div>
      </c:if>
      
    </div>
  </div>
  <div class="clearfix"></div>
</div>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
  <div class="white-bg">
    <div>
      <table id="app_Wide_table_list" class="table wid100 tbl">
        <thead>
         <tr>
                <th id="">APP ID <span class="sort"></span></th>
                <th id="">APP NAME <span class="sort"></span></th>
                <th id="">TYPE <span class="sort"></span></th>
                <th id="">STATUS<span class="sort"></span></th>
                <th id="" class="linkDis text-right" style="padding-right: 8% !important; "  >Actions</th>
              </tr>
        </thead>
        <tbody>
       <c:forEach items="${appBos}" var="app">
          <tr>
                <td>${app.customAppId}</td>
                <td>${app.name}</td>
                <td><c:if test="${app.type == 'GT'}">Gateway</c:if><c:if test="${app.type == 'SD'}">Standalone</c:if></td>
                <td>${app.appStatus}</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg viewAppClass"
                  appId="${app.id}"
                  permission="view" data-toggle="tooltip" data-placement="top" title="View"></span>
                    <span class="${(not empty app.liveAppId)?((app.flag)?'edit-inc-draft mr-md':'edit-inc mr-md'):((app.createFlag)?'edit-inc mr-md':'edit-inc-draft mr-md')}
                    addEditAppClass 
                    <c:choose>
						<c:when test="${not app.viewPermission}">
								cursor-none
						</c:when>
						<c:when test="${not empty app.appStatus && (app.appStatus eq 'Deactivated')}">
							  cursor-none
						</c:when>
					</c:choose>" data-toggle="tooltip" data-placement="top"
                	title="${(not empty app.liveAppId)?((app.flag)?'Edit draft version':'Edit'):((app.createFlag)?'Edit':'Edit draft version')}"
                	appId="${app.id}"></span>
                    <span class="sprites_icon  mr-lg <c:if test="${app.appStatus eq 'Draft'}">
                    cursor-none</c:if>
                    <c:choose>
                        <c:when test="${fn:contains(sessionObject.userPermissions,'ROLE_SUPERADMIN') && app.studiesCount gt 0}">viewStudiesClass</c:when>
                        <c:when test="${not fn:contains(sessionObject.userPermissions,'ROLE_SUPERADMIN') && (not empty app.studyPermissionCount && app.studyPermissionCount gt 0)}">viewStudiesClass</c:when> 
                    </c:choose>" data-toggle="tooltip" data-placement="top" title="${app.studiesCount} associated studies" appId="${app.customAppId}">
                    <img class="<c:if test="${app.studiesCount eq 0 || (not empty app.studyPermissionCount && app.studyPermissionCount eq 0) || app.appStatus eq 'Draft'}">
								cursor-none
						</c:if>" src="../images/icons/file-list-line.svg" >
                    </span>
                    
                  </td>        
              </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>


<!--  applist page  -->

   

<div class="clearfix"></div>
<form:form action="/studybuilder/adminApps/viewAppDetails.do" id="addEditAppsForm"
           name="addEditAppsForm"
           method="post">
</form:form>


<script>
  $(document).ready(function () {
	$('.appClass').addClass('active');
	$('[data-toggle="tooltip"]').tooltip();
    $('.applistDetailsToEdit').on('click', function () {
      $('#addEditAppsForm').submit();
    });
	    
	    
	$('#rowId').parent().removeClass('white-bg');
	  
    $('#app_Wide_table_list').DataTable({
      "paging": true,
      "order": [],
      "columnDefs": [{orderable: false, orderable: false}],
      "info": false,
      "lengthChange": false,
      language: {
        "zeroRecords": "No content created yet",
      },
      "searching": false,
      "pageLength": 10,
    });
    $(document).on('click', '.viewAppClass', function(){
        var form = document.createElement('form');
        form.method = 'post';
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'appId';
        input.value = $(this).attr('appId');
        form.appendChild(input);

        var input1 = document.createElement('input');
        input1.type = 'hidden';
        input1.name = 'permission';
        input1.value = $(this).attr('permission');
        form.appendChild(input1);

        input = document.createElement('input');
        input.type = 'hidden';
        input.name = '${_csrf.parameterName}';
        input.value = '${_csrf.token}';
        form.appendChild(input);

        form.action = '/studybuilder/adminApps/viewAppDetails.do';
        document.body.appendChild(form);
        form.submit();
      });
    
    $(document).on('click', '.addEditAppClass', function(){
        var form = document.createElement('form');
        form.method = 'post';
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'appId';
        input.value = $(this).attr('appId');
        form.appendChild(input);

        input = document.createElement('input');
        input.type = 'hidden';
        input.name = '${_csrf.parameterName}';
        input.value = '${_csrf.token}';
        form.appendChild(input);

        form.action = '/studybuilder/adminApps/viewAppDetails.do';
        document.body.appendChild(form);
        form.submit();
      });
    
    $(document).on('click', '.viewStudiesClass', function(){
        var form = document.createElement('form');
        form.method = 'post';
        var input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'appId';
        input.value = $(this).attr('appId');
        form.appendChild(input);

        input = document.createElement('input');
        input.type = 'hidden';
        input.name = '${_csrf.parameterName}';
        input.value = '${_csrf.token}';
        form.appendChild(input);

        form.action = '/studybuilder/adminStudies/studyList.do';
        document.body.appendChild(form);
        form.submit();
      });

  });
</script>
