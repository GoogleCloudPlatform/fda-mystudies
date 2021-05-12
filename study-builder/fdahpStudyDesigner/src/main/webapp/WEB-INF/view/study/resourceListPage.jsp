<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<style>
  .cursonMove {
    cursor: move !important;
  }

  .sepimgClass {
    position: relative;
  }

  .tool-tip {
    display: inline-block;
  }

  .tool-tip [disabled] {
    pointer-events: none;
  }
</style>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
<div class="col-sm-10 col-rc white-bg p-none">
  <!--  Start top tab section-->
  <div class="right-content-head">
    <div class="text-right">
      <div class="black-md-f text-uppercase dis-line pull-left line34">RESOURCES <c:set
          var="isLive">${_S}isLive</c:set>
        ${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</div>
      <div class="dis-line form-group mb-none mr-sm">
        <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
      </div>
      <c:if test="${empty permission}">
        <div class="dis-line form-group mb-none">
          <span class="tool-tip" data-toggle="tooltip" data-placement="bottom" id="spanMarkAsComp"
          data-original-title="">
            <button type="button" class="btn btn-primary blue-btn"
                    id="markAsComp" onclick="markAsCompleted();"
                    <c:if test="${fn:length(resourcesSavedList) ne 0}">disabled</c:if>>
              Mark as completed
            </button>
          </span>
        </div>
      </c:if>
    </div>
  </div>
  <!--  End  top tab section-->
  <!--  Start body tab section -->
  <div class="right-content-body pt-none pb-none">
    <div>
      <table id="resource_list" class="display bor-none tbl_rightalign"
             cellspacing="0" width="100%">
        <thead>
          <tr>
            <th id="">
              <span class="marL10">#</span>
            </th>
            <th id="">RESOURCE TITLE</th>
            <th class="text-right" id=""><c:if test="${empty permission}">
              <div class="dis-line form-group mb-none">
                <button type="button" id="addResourceId"
                        class="btn btn-primary blue-btn" onclick="addResource();">Add resource
                </button>
              </div>
            </c:if></th>

          </tr>
        </thead>
        <tbody>
          <c:forEach items="${resourceBOList}" var="resourceInfo">
            <tr id="row${resourceInfo.id}">
              <td>${resourceInfo.sequenceNo}</td>
              <td class="wid50">${resourceInfo.title}</td>
              <td class="wid50 text-right">
                <span class="sprites_icon preview-g mr-lg"
                      data-toggle="tooltip"
                      data-placement="top" title="View" id="viewRes"
                      onclick="viewResourceInfo(${resourceInfo.id});"></span>
                <span
                    class="${resourceInfo.action?'edit-inc':'edit-inc-draft mr-md'} mr-lg <c:if test="${not empty permission}"> cursor-none </c:if>"
                    data-toggle="tooltip" data-placement="top" title="Edit" id="editRes"
                    onclick="editResourceInfo(${resourceInfo.id});"></span>
                <span
                    class="sprites_icon copy delete <c:if test="${not empty permission}"> cursor-none </c:if>"
                    data-toggle="tooltip" data-placement="top" title="Delete" id="delRes"
                    onclick="deleteResourceInfo(${resourceInfo.id});"></span>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  <!--  End body tab section -->
</div>
<!-- End right Content here -->
<form:form
    action="/studybuilder/adminStudies/addOrEditResource.do?_S=${param._S}"
    name="resourceInfoForm" id="resourceInfoForm" method="post">
  <input type="hidden" name="resourceInfoId" id="resourceInfoId" value="">
  <input type="hidden" name="isstudyProtocol" id="isstudyProtocol" value="">
  <input type="hidden" name="actionOn" id="actionOn" value="">
</form:form>
<form:form
    action="/studybuilder/adminStudies/resourceMarkAsCompleted.do?_S=${param._S}"
    name="resourceMarkAsCompletedForm" id="resourceMarkAsCompletedForm"
    method="post">
  <input type="hidden" name="studyId" id="studyId" value="${studyBo.id}"/>
</form:form>
<script type="text/javascript">
  var dataTable;
  $(document).ready(function () {
	$('.studyClass').addClass("active");
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-toggle="tooltip"]').tooltip();

    // Fancy Scroll Bar
    $(".menuNav li").removeClass('active');
    $(".eighthResources").addClass('active');
    $("#createStudyId").show();
    $('.eighthResources').removeClass('cursor-none');
    var viewPermission = "${permission}";
    var permission = "${permission}";
    console.log("viewPermission:" + viewPermission);

    var reorder = true;
    if (viewPermission == 'view') {
      reorder = false;
    } else {
      reorder = true;
    }
  
    var resourcesSavedListSize = ${resourcesSavedList.size()};
    if(resourcesSavedListSize > 0){
    	 $('#spanMarkAsComp')
         .attr(
             'data-original-title',
             'Please ensure individual list items are marked Done, before marking the section as Complete');
        }

    var dataTable = $('#resource_list').DataTable({
      "paging": false,
      "info": false,
      "filter": false,
      language: {
        "zeroRecords": "No content created yet",
      },
      rowReorder: reorder,
      "columnDefs": [{orderable: false, targets: [0, 1, 2]}],
      "fnRowCallback": function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
        if (viewPermission != 'view') {
          $('td:eq(0)', nRow).addClass("cursonMove dd_icon");
        }
      }
    });

    dataTable.on('row-reorder', function (e, diff, edit) {
      var oldOrderNumber = '', newOrderNumber = '';
      var result = 'Reorder started on row: ' + edit.triggerRow.data()[1] + '<br>';
      var studyId = $("#studyId").val();
      for (var i = 0, ien = diff.length; i < ien; i++) {
        var rowData = dataTable.row(diff[i].node).data();
        var r1;
        if (i == 0) {
          r1 = rowData[0];
        }
        if (i == 1) {
          if (parseInt(r1) > parseInt(rowData[0])) {
            oldOrderNumber = diff[0].oldData;
            newOrderNumber = diff[0].newData;
          } else {
            oldOrderNumber = diff[diff.length - 1].oldData;
            newOrderNumber = diff[diff.length - 1].newData;
          }

        }
        result += rowData[1] + ' updated to be in position ' +
            diff[i].newData + ' (was ' + diff[i].oldData + ')<br>';
      }

      if (oldOrderNumber !== undefined && oldOrderNumber != null && oldOrderNumber != ""
          && newOrderNumber !== undefined && newOrderNumber != null && newOrderNumber != "") {
        $.ajax({
          url: "/studybuilder/adminStudies/reOrderResourceList.do?_S=${param._S}",
          type: "POST",
          datatype: "json",
          data: {
            studyId: studyId,
            oldOrderNumber: oldOrderNumber,
            newOrderNumber: newOrderNumber,
            "${_csrf.parameterName}": "${_csrf.token}",
          },
          success: function consentInfo(data) {
            var message = data.message;
            if (message == "SUCCESS") {
              reloadResourceDataTable(data.resourceList, null);
              $('#alertMsg').show();
              $("#alertMsg").removeClass('e-box').addClass('s-box').text(
                  "Content items reordered");
              if ($('.eighthResources').find('span').hasClass(
                  'sprites-icons-2 tick pull-right mt-xs')) {
                $('.eighthResources').find('span').removeClass(
                    'sprites-icons-2 tick pull-right mt-xs');
              }
            } else {
              $('#alertMsg').show();
              $("#alertMsg").removeClass('s-box').addClass('e-box').text(
                  "Unable to reorder consent");
            }
            setTimeout(hideDisplayMessage, 5000);
          },
          error: function (xhr, status, error) {
            $("#alertMsg").removeClass('s-box').addClass('e-box').text(error);
            setTimeout(hideDisplayMessage, 5000);
          }
        });
      }
    });

  });

  function deleteResourceInfo(resourceInfoId) {
    $('#delRes').addClass('cursor-none');
    bootbox.confirm("Are you sure you want to delete this resource?", function (result) {
      if (result) {
        var studyId = $("#studyId").val();
        console.log("delete:" + studyId);
        if (resourceInfoId != '' && resourceInfoId != null && typeof resourceInfoId
            != 'undefined') {
          $.ajax({
            url: "/studybuilder/adminStudies/deleteResourceInfo.do?_S=${param._S}",
            type: "POST",
            datatype: "json",
            data: {
              resourceInfoId: resourceInfoId,
              studyId: '${studyId}',
              "${_csrf.parameterName}": "${_csrf.token}",
            },
            success: function deleteConsentInfo(data) {
              var status = data.message;
              var resourceSaved = data.resourceSaved;
              if (status == "SUCCESS") {
                if (resourceSaved) {
                  $('#markAsComp').prop('disabled', true);
                  $('#spanMarkAsComp')
                  .attr(
                      'data-original-title',
                      'Please ensure individual list items are marked Done, before marking the section as Complete');
                  $('[data-toggle="tooltip"]').tooltip();
                } else {
                  $('#markAsComp').prop('disabled', false);
                  $('#spanMarkAsComp').removeAttr('data-original-title');
                }
                $("#alertMsg").removeClass('e-box').addClass('s-box').text(
                    "Resource deleted successfully");
                $('#alertMsg').show();
                reloadData(studyId);
              } else {
                $("#alertMsg").removeClass('s-box').addClass('e-box').text(
                    "Unable to delete resource");
                $('#alertMsg').show();
              }
              setTimeout(hideDisplayMessage, 5000);
            },
            error: function (xhr, status, error) {
              $("#alertMsg").removeClass('s-box').addClass('e-box').text(error);
              setTimeout(hideDisplayMessage, 5000);
            }
          });
        }
      }
    });
    $('#delRes').removeClass('cursor-none');
  }

  function reloadData(studyId) {
    $.ajax({
      url: "/studybuilder/adminStudies/reloadResourceListPage.do?_S=${param._S}",
      type: "POST",
      datatype: "json",
      data: {
        studyId: studyId,
        "${_csrf.parameterName}": "${_csrf.token}",
      },
      success: function status(data, status) {
        var message = data.message;
        var markAsComplete = data.markAsComplete;
        if (message == "SUCCESS") {
          reloadResourceDataTable(data.resourceList, markAsComplete);
        }
      },
      error: function status(data, status) {

      },
    });
  }

  function reloadResourceDataTable(resourceList, markAsComplete) {
    $('#resource_list').DataTable().clear();
    if (typeof resourceList != 'undefined' && resourceList != null && resourceList.length > 0) {
      $.each(resourceList, function (i, obj) {
        var datarow = [];
        console.log("obj.studyProtocol:" + obj.studyProtocol);
        if (!obj.studyProtocol) {
          if (typeof obj.sequenceNo === "undefined" && typeof obj.sequenceNo === "undefined") {
            datarow.push(' ');
          } else {
            datarow.push(obj.sequenceNo);
          }
          if (typeof obj.title === "undefined" && typeof obj.title === "undefined") {
            datarow.push(' ');
          } else {
            datarow.push(obj.title);
          }
          var actions = "<span class='sprites_icon preview-g mr-lg' data-toggle='tooltip' data-placement='top' title='View' onclick='viewResourceInfo("
              + parseInt(obj.id) + ");'></span>";
          if (obj.action) {
            actions += "<span class='sprites_icon edit-g mr-lg' data-toggle='tooltip' data-placement='top' title='Edit' onclick='editResourceInfo(" + parseInt(obj.id)
                + ");'></span>"
          } else {
            actions += "<span class='sprites_icon edit-inc-draft mr-lg' data-toggle='tooltip' data-placement='top' title='Edit' onclick='editResourceInfo("
                + parseInt(obj.id) + ");'></span>";
          }
          actions += "<span class='sprites_icon copy delete' data-toggle='tooltip' data-placement='top' title='Delete' onclick='deleteResourceInfo(" + parseInt(obj.id)
              + ");'></span>";
          datarow.push(actions);
          $('#resource_list').DataTable().row.add(datarow);

        }
      });
      if (typeof markAsComplete != 'undefined' && markAsComplete != null && markAsComplete == '') {
        $("#markAsComp").attr("disabled", false);
      }
      $('#resource_list').DataTable().draw();
      $('#resource_list tr').each(function () {
        $(this).find('td:nth-child(2)').addClass("wid50");
        $(this).find('td:last').addClass("wid50 text-right");
      });
    } else {
      $('#resource_list').DataTable().draw();
    }
    $('[data-toggle="tooltip"]').tooltip();
  }

  function addStudyProtocol(studyProResId) {
    $('#studyProtocolId').prop('disabled', true);
    if (studyProResId != null && studyProResId != '' && typeof studyProResId != 'undefined') {
      $("#resourceInfoId").val(studyProResId);
      $("#actionOn").val("edit");
    } else {
      $("#resourceInfoId").val('');
      $("#actionOn").val("add");
    }
    $("#isstudyProtocol").val('isstudyProtocol');
    $("#resourceInfoForm").submit();
  }

  function addResource() {
    $('#addResourceId').prop('disabled', true);
    $("#resourceInfoId").val('');
    $("#actionOn").val('add');
    $("#resourceInfoForm").submit();
  }

  function editResourceInfo(resourceInfoId) {
    if (resourceInfoId != null && resourceInfoId != '' && typeof resourceInfoId != 'undefined') {
      $('#editRes').addClass('cursor-none');
      $("#resourceInfoId").val(resourceInfoId);
      $("#actionOn").val('edit');
      $("#resourceInfoForm").submit();
    }
  }

  function viewResourceInfo(resourceInfoId) {
    if (resourceInfoId != null && resourceInfoId != '' && typeof resourceInfoId != 'undefined') {
      $('#viewRes').addClass('cursor-none');
      $("#resourceInfoId").val(resourceInfoId);
      $("#actionOn").val('view');
      $("#resourceInfoForm").submit();
    }
  }

  function markAsCompleted() {
    $('#resourceMarkAsCompletedForm').submit();
  }

  function hideDisplayMessage() {
    $('#alertMsg').hide();
  }

  var sucMsg = '${sucMsg}';
  if (sucMsg.length > 0) {
    showSucMsg(sucMsg);
  }

	function showSucMsg(message) {
	  $("#alertMsg").removeClass('e-box').addClass('s-box').text(message);
	  $('#alertMsg').show('5000');
	  if('${param.buttonText}' == 'completed'){
		    window.setTimeout(function(){
		        window.location.href = "/studybuilder/adminStudies/viewStudyNotificationList.do?_S=${param._S}";
		    }, 5000);
	  }else{
	  	setTimeout(hideDisplayMessage, 5000);
	  }
	}
</script>