<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<style>
  <!--
  .sorting, .sorting_asc, .sorting_desc {
    background: none !important;
  }

  -->
  #app_Wide_Notification_list tr td {
    padding-left: 20px !important;
    }
    #app_Wide_Notification_list tr th {
    padding-left: 20px !important;
}
</style>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mb-md">
  <div>
    <!-- widgets section-->
    <div class="col-sm-12 col-md-12 col-lg-12 p-none">
      <div class="black-lg-f">
        Send notifications to app users
      </div>
      <div class="dis-line pull-right ml-md">
          <div class="form-group mb-none mt-xs">
            <button type="button" class="btn btn-primary blue-btn notificationDetailsToEdit"
                    actionType="add">
              Create notification
            </button>
          </div>
      </div>
    </div>
  </div>
  <div class="clearfix"></div>
</div>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
  <div class="white-bg">
    <div>
      <table id="app_Wide_Notification_list" class="table wid100 tbl">
        <thead>
          <tr>
            <th id="">Notification</th>
            <th id="" class="linkDis">Status</th>
            <th id="" class="linkDis text-right" style="padding-right: 3% !important;">ACTIONS</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${notificationList}" var="notification" varStatus="status">
            <tr>
              <td>
                <div class="dis-ellipsis lg-ellipsis">${fn:escapeXml(notification.notificationText)}</div>
              </td>
              <td>${notification.checkNotificationSendingStatus}</td>
              <td class="text-right" style="padding-right: 3% !important;">
                <span class="sprites_icon preview-g mr-lg notificationDetailsToView"
                      actionType="view"
                      notificationId="${notification.notificationId}"
                      data-toggle="tooltip"
                      data-placement="top" title="View"></span>
                
                  <c:if test="${notification.notificationSent}">
                    <span
                        class="sprites-icons-2 send mr-lg notificationDetailsToEdit
                        <c:if test="${not notification.appPermission}"> cursor-none </c:if> "
                        actionType="resend"
                        notificationId="${notification.notificationId}"
                        data-toggle="tooltip" data-placement="top"
                        title="Resend"></span>
                       
                  </c:if>
                  <c:if test="${not notification.notificationSent}">
                    <span
                        class="sprites_icon edit-g mr-lg notificationDetailsToEdit  
                        <c:if test="${not notification.appPermission}"> cursor-none </c:if> "
                        actionType="edit"
                        notificationId="${notification.notificationId}"
                        data-toggle="tooltip"
                        data-placement="top" title="Edit"></span>
                  </c:if>
                  <span class="sprites_icon copy notificationDetailsToEdit" actionType="add"
                        notificationText="${fn:escapeXml(notification.notificationText)}"
                        data-toggle="tooltip" data-placement="top" title="Copy-into-new"></span>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</div>
<form:form action="/studybuilder/adminNotificationEdit/getNotificationToEdit.do"
           id="getNotificationEditPage"
           name="getNotificationEditPage" method="post">
  <input type="hidden" id="notificationId" name="notificationId">
  <input type="hidden" id="notificationText" name="notificationText">
  <input type="hidden" id="actionType" name="actionType">
  <input type="hidden" name="chkRefreshflag" value="y">
</form:form>
<form:form action="/studybuilder/adminNotificationView/getNotificationToView.do"
           id="getNotificationViewPage"
           name="getNotificationViewPage" method="post">
  <input type="hidden" id="notificationIdToView" name="notificationId">
  <input type="hidden" id="actionTypeToView" name="actionType">
  <input type="hidden" name="chkRefreshflag" value="y">
</form:form>

<script>
  $(document).ready(function () {
    $('#rowId').parent().removeClass('white-bg');

    $('#notification').addClass('active');

    $('[data-toggle="tooltip"]').tooltip();

    $('.notificationDetailsToEdit').on('click', function () {
      $('.notificationDetailsToEdit').prop('disabled', true);
      $('#notificationId').val($(this).attr('notificationId'));
      $('#notificationText').val($(this).attr('notificationText'));
      $('#actionType').val($(this).attr('actionType'));
      $('#getNotificationEditPage').submit();
    });

    $('.notificationDetailsToView').on('click', function () {
      $('.notificationDetailsToView').prop('disabled', true);
      $('#notificationIdToView').val($(this).attr('notificationId'));
      $('#actionTypeToView').val($(this).attr('actionType'));
      $('#getNotificationViewPage').submit();
    });

    $('#app_Wide_Notification_list').DataTable({
      "paging": true,
      "order": [],
      "columnDefs": [{orderable: false, orderable: false, targets: [0]}],
      "info": false,
      "lengthChange": false,
      language: {
        "zeroRecords": "No content created yet",
      },
      "searching": false,
      "pageLength": 15,
    });

  });
</script>
