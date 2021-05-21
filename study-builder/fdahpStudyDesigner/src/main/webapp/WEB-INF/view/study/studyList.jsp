<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<style>
#studies_list tr th {
    padding-left: 10px !important;
}
#studies_list tr td {
    padding-left: 10px !important;
}
.mr-lg {
    margin-right: 15px !important;
}
</style>

<div>
  <table id="studies_list" class="table wid100 tbl">
    <thead>
      <tr>
        <th style="display: none;" id="">
          <span class="sort"></span>
        </th>
        <th style="display: none;" id="">Live Study ID
          <span class="sort"></span>
        </th>
        <th id="">Study ID
          <span class="sort"></span>
        </th>
        <th id="">APP ID
          <span class="sort"></span>
        </th>
        <th id="">Study name
          <span class="sort"></span>
        </th>
        
        <th id="">Status
          <span class="sort"></span>
        </th>
        <th id="">Actions</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach items="${studyBos}" var="study">
         <tr class="${study.status}" >
          <td style="display: none;">${study.createdOn}</td>
          <td style="display: none;">${study.liveStudyId}</td>
          <td>${study.customStudyId}</td>

          <td>${study.appId}</td>
          <td>
            <div class="studylist-txtoverflow">${study.name}</div>
          </td>
          <td class ="studyStatus${study.customStudyId}">${study.status}</td>
          <td>
            <span class="sprites_icon preview-g mr-lg viewStudyClass" isLive=""
                  studyId="${study.id}"
                  permission="view" data-toggle="tooltip" data-placement="top"
                  title="View"></span>
            <span
                class="${(not empty study.liveStudyId)?((study.flag)?'edit-inc-draft mr-md':'edit-inc mr-md'):'edit-inc-draft mr-md'}
                        addEditStudyClass 
                    <c:choose>
						<c:when test="${not study.viewPermission}">
								cursor-none
						</c:when>
						<c:when test="${not empty study.status && (study.status eq 'Deactivated')}">
							  cursor-none
						</c:when>
					</c:choose>" data-toggle="tooltip" data-placement="top"
                title="${(not empty study.liveStudyId)?((study.flag)?'Edit draft version':'Edit'):'Edit draft version'}"
                studyId="${study.id}"></span>
           
             <span class="sprites_icon copy copyStudy mr-lg
              <c:if  test="${empty study.customStudyId}">
								cursor-none
						</c:if>" 
                   data-toggle="tooltip" data-placement="top" studyId="${study.customStudyId}"
                  title="Copy-into-new" onclick='copyStudy("${study.id}");'>
                    </span>
           <c:if test="${not empty study.liveStudyId}">
              <span class="eye-inc viewStudyClass mr-lg published" isLive="Yes"
                    studyId="${study.liveStudyId}"
                    permission="view" data-toggle="tooltip" data-placement="top"
                    title="View last published version"></span>
            </c:if>
          </td>
        </tr>
      </c:forEach>
    </tbody>
  </table>
</div>

<form:form action="/studybuilder/adminStudies/viewBasicInfo.do?_S=${param._S}"
           name="addEditStudyForm"
           id="addEditStudyForm" method="post">
  <input type="hidden" name="studyId" id="studyId" value="${studyId}"/>
</form:form>
<script>
  $(document).ready(function () {
    $('.studyClass').addClass('active');
    $('[data-toggle="tooltip"]').tooltip();

    $('.addEditStudyClass').on('click', function () {
      var form = document.createElement('form');
      form.method = 'post';
      var input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'studyId';
      input.value = $(this).attr('studyId');
      form.appendChild(input);

      input = document.createElement('input');
      input.type = 'hidden';
      input.name = '${_csrf.parameterName}';
      input.value = '${_csrf.token}';
      form.appendChild(input);

      form.action = '/studybuilder/adminStudies/viewStudyDetails.do';
      document.body.appendChild(form);
      form.submit();
    });
    


    $('.viewStudyClass').on('click', function () {
      var form = document.createElement('form');
      form.method = 'post';
      var input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'studyId';
      input.value = $(this).attr('studyId');
      form.appendChild(input);

      var input1 = document.createElement('input');
      input1.type = 'hidden';
      input1.name = 'permission';
      input1.value = $(this).attr('permission');
      form.appendChild(input1);

      var input2 = document.createElement('input');
      input2.type = 'hidden';
      input2.name = 'isLive';
      input2.value = $(this).attr('isLive');
      form.appendChild(input2);

      input = document.createElement('input');
      input.type = 'hidden';
      input.name = '${_csrf.parameterName}';
      input.value = '${_csrf.token}';
      form.appendChild(input);

      form.action = '/studybuilder/adminStudies/viewStudyDetails.do';
      document.body.appendChild(form);
      form.submit();
    });
    $("#studies_list").DataTable({
        "paging": true,
        "abColumns": [
          {"bSortable": true},
          {"bSortable": true},
          {"bSortable": true},
          {"bSortable": true},
          {"bSortable": false}
        ],
        "columnDefs": [{orderable: false, targets: [6]}],
        "order": [[0, "desc"]],
        "info": false,

        "lengthChange": false,
        language: {
          "zeroRecords": "No studies found.",
        },
        "searching": true,
        "pageLength": 10,
        "sDom": "rtip"
         })
         var oTable = $("#studies_list").DataTable() ;
    showActivatedStudies()

    oTable.draw();
 });

  $('.copyStudyClass').on('click', function () {
    var form = document.createElement('form');
    form.method = 'post';
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'customStudyId';
    input.value = $(this).attr('customStudyId');
    form.appendChild(input);

    input = document.createElement('input');
    input.type = 'hidden';
    input.name = '${_csrf.parameterName}';
    input.value = '${_csrf.token}';
    form.appendChild(input);

    form.action = '/studybuilder/adminStudies/crateNewStudy.do';
    document.body.appendChild(form);
    form.submit();
  });

  //datatable icon toggle
  $(".table thead tr th").click(function () {
    $(this).children().removeAttr('class')
    $(this).siblings().children().removeAttr('class').addClass('sort');
    if ($(this).attr('class') == 'sorting_asc') {
      $(this).children().addClass('asc');
    } else if ($(this).attr('class') == 'sorting_desc') {
      $(this).children().addClass('desc');
    } else {
      $(this).children().addClass('sort');
    }
  });
  
  function showActivatedStudies(status) {
	  var oTable = $("#studies_list").DataTable() ;
      if ($('#deactivatedBtn').is(":checked")) {
          console.log("This is checked");
          oTable
          .columns([5]) //The index of column to search
             .search('') //The RegExp search all string that not cointains USA
          .draw();

      } else {
          console.log("This is Unchecked");
         
          oTable
          .columns([5]) //The index of column to search
             .search('^(?:(?!Deactivated).)*$\r?\n?', true, false) //The RegExp search all string that not cointains USA
          .draw();
      }
     
  }
 
  function copyStudy(studyId) {
      var form = document.createElement('form');
      form.method = 'post';
      var input = document.createElement('input');
      input.type = 'hidden';
      input.name = 'studyId';
      input.value = studyId;
      form.appendChild(input);

      input = document.createElement('input');
      input.type = 'hidden';
      input.name = '${_csrf.parameterName}';
      input.value = '${_csrf.token}';
      form.appendChild(input);

      form.action = '/studybuilder/adminStudies/replicate.do';
      document.body.appendChild(form);
      form.submit();
    }

 
</script>