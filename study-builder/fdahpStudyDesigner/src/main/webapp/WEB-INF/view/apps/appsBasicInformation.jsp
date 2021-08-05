<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<style>
.uploadImg{
margin-top:16px !important;
}
</style>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->

      
<div class="col-sm-10 col-rc white-bg p-none">
  <form:form
      action="/studybuilder/adminStudies/saveOrUpdateBasicInfo.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}"
      data-toggle="validator" role="form" id="basicInfoFormId" method="post"
      autocomplete="off" enctype="multipart/form-data">
    <!-- Start top tab section-->
    <div class="right-content-head">
      <div class="text-right">
        <div class="black-md-f text-uppercase dis-line pull-left line34">
          APP INFORMATION
          <c:set var="isLive">${_S}isLive</c:set>
            ${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}
        </div>


        <div class="dis-line form-group mb-none mr-sm">
          <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
        </div>
        <c:if test="${empty permission}">
          <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn actBut"
                    id="saveId"
                    <c:if test="${not studyBo.viewPermission }">disabled</c:if>>Save
            </button>
          </div>

          <div class="dis-line form-group mb-none">
            <button type="button" class="btn btn-primary blue-btn actBut"
                    id="completedId"
                    <c:if test="${not studyBo.viewPermission }">disabled</c:if>>Mark
              as completed
            </button>
          </div>
        </c:if>
      </div>
    </div>
    <!-- End top tab section-->
    <input type="hidden" id="sId" value="${studyBo.id}" name="id"/>
    <input type="hidden" value="" id="buttonText" name="buttonText">
    <!-- Start body tab section -->
    <div class="right-content-body col-xs-12">

                        <div class="row">
                            <div class="col-md-6 pl-none">
                                <div class="gray-xs-f mb-xs">App ID  <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>

                           <div class="col-md-6">
                                <div class="gray-xs-f mb-xs">App Name <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                                <div class="form-group mb-none">
                                    <input type="text" class="form-control"/>
                                    <div class="help-block with-errors red-txt"></div>
                                </div>
                            </div>
                            
                            <div class="clearfix"></div>
                           
                        </div>
 
    <!-- End body tab section -->
  </form:form>
</div>
<!-- End right Content here -->

<script>
</script>
