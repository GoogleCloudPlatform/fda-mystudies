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
      action="/studybuilder/adminStudies/saveOrUpdateSettingAndAdmins.do?_S=${param._S}"
      data-toggle="validator" role="form" id="appPropertiesFormId" method="post"
      autocomplete="off">
    	
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

                         <div class="row mt-xlg">
                            <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">Feedback email  <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.feedbackEmailAddress}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Contact Us email <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.contactEmailAddress}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                             <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">App support email  <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appSupportEmailAddress}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">App 'Terms' URL<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appTermsUrl}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">App Privacy policy URL <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appPrivacyUrl}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Organization name<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.organizationName}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">App Store URL <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.appStoreUrl}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Play Store URL<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value="${appBo.playStoreUrl}"/>
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
	  <c:if test="${not empty permission}">
	     $('#appPropertiesFormId input').prop('disabled', true);
	     </c:if>
	});
</script>