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
.modal-footer {
    border-top: none !important;
}
.modal-header {
    border-bottom: none !important; 
}
.copy-version {
    width: max-content !important; 
    border-radius: 0px !important; 
    padding: 20px !important;
} 
.copyVersionModel {
    position: fixed;
    top: 50% !important;
    left: 50% !important;
    transform: translate(-40%, -40%); 
}

  .select-sup_text { font-size: 14px;
    line-height: 16px;
    color: #7c868d;
    font-weight: 500;
    padding-left:3%;
  }
  
  .select-sub_text { padding-left:25px;}
  .select_drop_parent {
  	position: absolute;
    display: contents;
    }
     .custom_checkbox_dropdown { 
   background: #fff!important;
    min-width: 200px !important;
     width: 268px !important;
    max-height: 300px;
    overflow-y: scroll;
      }
      
    .custom_checkbox_dropdown > li >a {
    padding: 0px 20px;
    
}
.pl-7 {
padding-left: 7px;
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
              <c:choose>
						<c:when test="${not study.viewPermission}">
							  cursor-none
						</c:when>
						<c:when test="${not empty study.status && (study.status eq 'Deactivated')}">
							  cursor-none
						</c:when>
						<c:when test="${empty study.customStudyId}">
						      cursor-none
						</c:when>
						<c:when test="${not fn:contains(sessionObject.userPermissions,'ROLE_CREATE_MANAGE_STUDIES')}"> 
						      cursor-none
						</c:when>
			  </c:choose>"
             
                   data-toggle="tooltip" data-placement="top" studyId="${study.customStudyId}"
                  title="Copy-into-new" onclick='copyStudy("${study.id}" , "${study.liveStudyId}" ,
                   ${(not empty study.liveStudyId)?((study.flag)? true : false): false});'>
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
<div class="modal fade copyVersionModel" id="copyVersionModel" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content copy-version">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">Select the study version to be copied:</h4>
            </div>
            <div class="modal-body">
               
                      <span class="radio radio-info radio-inline p-40 ">
                          <input type="radio" id="workingVersion" class="workingVersion copyVersion"  value="workingVersion" name="copy">
                          <label for="workingVersion">Copy working version</label>
                      </span>
                      <span class="radio radio-inline ">
                          <input type="radio" id="publishedVersion" class="publishedVersion copyVersion"  value="publishedVersion" name="copy">
                          <label for="publishedVersion">Copy last published version</label>
                     </span>
                     
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default gray-btn" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary blue-btn" id="submit" onclick='copyVersion()' disabled >Submit</button>
                 <input type="hidden" name="draftVersion" id="draftVersion" value=""/>
                 <input type="hidden" name="lastpublish" id="lastpublish" value=""/>
            </div>
        </div>
    </div>
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
    
    $('.fcheckbox').on('change', function(e){
 	      var searchTerms = []
 	      $.each($('.fcheckbox'), function(i,elem){
 	        if($(elem).prop('checked')){
 	          searchTerms.push("^" + $(this).val() + "$")
 	        }
 	      })
 	      oTable.column(3).search(searchTerms.join('|'), true, false, true).draw();
 	    });
    var searchTerms = []
     $.each($('.fcheckbox'), function(i,elem){
       if($(elem).prop('checked')){
         searchTerms.push("^" + $(this).val() + "$")
       }
     })
     oTable.column(3).search(searchTerms.join('|'), true, false, true).draw();
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
 
   function copyStudy(studyId, lastpublishStudyId, flag) {
	  if(flag){
		  $('#draftVersion').val(studyId);
		  $('#lastpublish').val(lastpublishStudyId);
		  $("input[type=radio][name=copy]").prop('checked', false);
		  $("#submit").attr("disabled", "disabled");
	      $('#copyVersionModel').modal('show');
	  }else{
		  copyAction(studyId, "workingVersion");
	  }
    }  
    

   function copyVersion() {
	var copy_opts = $("input[name='copy']:checked").val();
	var studyId = (copy_opts == 'publishedVersion') ? $('#lastpublish').val() : $('#draftVersion').val();
	copyAction(studyId, copy_opts);
   }
   
   var radioButton = $("input:radio");
   radioButton.change(function () {
       if (radioButton.filter(':checked').length > 0) {
           $("#submit").removeAttr("disabled");
       } else {
           $("#submit").attr("disabled", "disabled");
       }
   });
   
   function copyAction(studyId, copyVersion){
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
	     
	     input = document.createElement('input');
	     input.type = 'hidden';
	     input.name = 'copyVersion';
	     input.value = copyVersion;
	     form.appendChild(input);

	     form.action = '/studybuilder/adminStudies/replicate.do';
	     document.body.appendChild(form);
	     form.submit(); 
   }

   $(document).ready(function () {

		  $('body').on("click", ".dropdown-menu", function (e) {
			    $(this).parent().is(".open") && e.stopPropagation();
			});

			$('.selectall').click(function() {
			    if ($(this).is(':checked')) {
			        $('.option').prop('checked', true);
			        var total = $('input[name="options[]"]:checked').length;
			        $(".dropdown-text").html('All apps');
			        $(".select-text").html(' Deselect');
			    } else {
			        $('.option').prop('checked', false);
			        var total = $('input[name="options[]"]:checked').length;
			        $(".dropdown-text").html('Filter by apps');
			        $(".select-text").html(' Select');
			    }
			});

			$("input[type='checkbox'].justone").change(function(){
			    var a = $("input[type='checkbox'].justone");
			    if(a.length == a.filter(":checked").length){
			        $('.selectall').prop('checked', true);
			        $(".select-text").html(' Deselect');
			    }
			    else {
			        $('.selectall').prop('checked', false);
			        $(".select-text").html(' Select');
			    }
			  var total = $('input[name="options[]"]:checked').length;
			  if(total == 0){
				  $(".dropdown-text").html('Filter by apps');
			  }else{
				  $(".dropdown-text").html(total + ' app(s)');
			  }
			  
			});
		  
			  <c:if test="${not empty appId}">
		    	$(".dropdown-text").html(1 + ' app(s)');
		    </c:if>
		    
		});
  
</script>