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
      data-toggle="validator" role="form" id="settingfoFormId" method="post"
      autocomplete="off">
    <input type="hidden" name="buttonText" id="buttonText">
    <input type="hidden" id="settingsstudyId" name="id"
           value="${studyBo.id}">
    <input type="hidden" id="userIds" name="userIds">
    <input type="hidden" id="permissions" name="permissions">
    <input type="hidden" id="projectLead" name="projectLead">
    <input type="hidden" id="modifiedBy" name="modifiedBy"  value="${studyBo.modifiedBy}">
    
	
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          DEVELOPER CONFIGURATIONS
          <c:set var="isLive">${_S}isLive</c:set>
         </div>

        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut"
                  id="cancelId">Cancel
          </button>
        </div>
        <c:if
            test="${(empty permission) && (sessionObject.role ne 'Org-level Admin')}">
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
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Contact Us email <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                             <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">App support email  <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">App 'Terms' URL<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">App Privacy policy URL <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Organization name<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">App Store URL <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Play Store URL<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
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


<!-- Modal -->
<div class="modal fade" id="myModal" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">

      <div class="modal-header cust-hdr pt-lg">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
        <h4 class="modal-title pl-lg">
          <strong>Platform and Feature Support</strong>
        </h4>
      </div>
      <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
        <div>
          <div>
            <ul class="no-disc">
              <li><strong>1. Platform support: </strong><br/>
                <ul class="no-disc">
                  <li>Note that once the study is launched, platform support
                    cannot be revoked. However, adding support for a platform not
                    previously selected will still be possible.
                  </li>
                </ul>
              </li>
              <li>&nbsp;</li>
              <li><strong>2. Feature support on iOS and Android:</strong><br/>

                <ul class="no-disc">
                  <li>Given below is a list of features currently NOT
					available for Android as compared to iOS. Please note the same
                    in your creation of study content:
                  </li>
                  <li>i. Active tasks: Tower of hanoi, Spatial span memory
                  </li>
                </ul>
              </li>

            </ul>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<script>
  $(document).ready(function () {

	}
</script>