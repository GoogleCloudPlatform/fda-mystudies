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
      data-toggle="validator" role="form" id="developerConfigFormId" method="post"
      autocomplete="off">
    
	
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
                                <div class="gray-xs-f mb-xs mt-md">Android Bundle ID <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value= "${appBo.androidBundleId}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-md">Android Server Key <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value= "${appBo.androidServerKey}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                             <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs mt-md">iOS Bundle ID <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value= "${appBo.iosBundleId}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs mt-md">iOS Server Key<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value= "${appBo.iosServerKey}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            <div class="blue-md-f mb-md mt-md text-uppercase"> APP VERSION INFORMATION </div>
                <div class="clearfix"></div>
                            
                              <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">Latest XCode app version (for iOS app) <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value= "${appBo.iosXCodeAppVersion}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">Lastest app build version (for iOS app)<span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value= "${appBo.androidAppBuildVersion}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                            
                              <div class="col-md-12 pl-none">
                                <div class="gray-xs-f mb-xs mt-md">Force upgrade for iOS users? <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                               <div class="mt-md mb-md">
                         <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="inlineRadio1" value="1" name="radioInline1" <c:if test="${appBo.iosForceUpgrade eq 1}">checked</c:if>>
                            <label for="inlineRadio1">Yes</label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" id="inlineRadio2" value="0" name="radioInline1" <c:if test="${appBo.iosForceUpgrade eq 0}">checked</c:if>>
                            <label for="inlineRadio2">NO</label>
                        </span>
                    </div>
                            </div>
                             <div class="clearfix"></div>

                           <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs"> Latest app version code (for Android app) <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control" value= "${appBo.androidBundleId}"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                             <div class="clearfix"></div>
                            
                              <div class="col-md-12 pl-none">
                                <div class="gray-xs-f mb-xs mt-md">Force upgrade for Android users? <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                               <div class="mt-md mb-md">
                         <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="inlineRadio3" value="1" name="radioInline1" <c:if test="${appBo.androidForceUpgrade eq 1}">checked</c:if>>
                            <label for="inlineRadio3">Yes</label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" id="inlineRadio4" value="0" name="radioInline1" <c:if test="${appBo.androidForceUpgrade eq 0}">checked</c:if>>
                            <label for="inlineRadio4">NO</label>
                        </span>
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
	  <c:if test="${not empty permission}">
	     $('#developerConfigFormId input').prop('disabled', true);
	  </c:if>
	});
</script>