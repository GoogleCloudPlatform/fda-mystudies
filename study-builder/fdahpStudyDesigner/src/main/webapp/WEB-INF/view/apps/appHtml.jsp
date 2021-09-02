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
        Manage Apps
      </div>
      <div class="dis-line pull-right ml-md">
          <div class="form-group mb-none mt-xs">
            <button type="button" class="btn btn-primary blue-btn applistDetailsToEdit"
                    actionType="add">
             Create New
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
      <table id="app_Wide_table_list" class="table wid100 tbl">
        <thead>
         <tr>
                <th class="linkDis">APP ID <span class="sort"></span></th>
                <th class="linkDis">APP NAME <span class="sort"></span></th>
                <th class="linkDis">TYPE <span class="sort"></span></th>
                <th class="linkDis">STATUS<span class="sort"></span></th>
                <th id="" class="linkDis text-right" style="padding-right: 3% !important; "  >Actions</th>
              </tr>
        </thead>
        <tbody>
          <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
              
 <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>       
              <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
			   <tr>
                <td>WAP6789E3</td>
                <td>Lorem ipsum</td>
                <td>Gateway</td>
                <td>Active</td>
                <td class="text-right" style="padding-right: 2% !important; ">
                    <span class="sprites_icon preview-g mr-lg"></span>
                    <span class="sprites_icon edit-g mr-lg"></span>
                    <span class="sprites_icon copy mr-lg"></span>
                    
                  </td>        
              </tr>
        </tbody>
      </table>
    </div>
  </div>
</div>


<!--  applist page  -->

   
<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mt-lg">
    <div class="white-bg">
        
        <div class="row lc-gray-bg">
        
        <!-- Start left Content here -->
        <div class="col-sm-2 col-lc p-none">
            <div class="left-content-container wid100">
                <ul>
                    <li>Create Study</li>
                    <li class="active">APP INFORMATION </li>
                    <li>APP SETTINGS</li>
                    <li>APP PROPERTIES</li>
                    <li>DEVELOPER CONFIGURATIONS</li>
                    <li>ACTIONS</li>            
                </ul>
            </div>
        </div>
        <!-- End left Content here -->
        
         <!-- Start right Content here -->
        <div class="col-sm-10 col-rc white-bg p-none">
            
            <!--  Start top tab section-->
            <div class="right-content-head">        
                <div class="text-right">
                    <div class="black-md-f dis-line pull-left line34">
                        APP INFORMATION
                    </div>
                    
                    <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn">Cancel</button>
                     </div>
                    
                     <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn">Save</button>
                     </div>

                     <div class="dis-line form-group mb-none">
                         <button type="button" class="btn btn-primary blue-btn">Mark as Completed</button>
                     </div>
                 </div>
            </div>
            <!--  End  top tab section-->
            
            <!--  Start body tab section -->
            <div class="right-content-body pt-none pl-none pr-none">
                <div class="tab-content pl-xlg pr-xlg">
                    
                    <!-- Step-level Attributes--> 
                    <div class="tab-pane fade in active mt-xlg">
                        
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
                        
                        
                          <div class="row mt-xlg">
                            <div class="col-md-12 pl-none">
                                <div class="gray-xs-f mb-xs">App Type  <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                               <div>
                                <span class="radio radio-info radio-inline p-45">
                                    <input type="radio" id="inlineRadio4" value="option2" name="radioInline3">
                                    <label for="inlineRadio4">Gateway</label>
                                </span>
                                <span class="radio radio-inline">
                                    <input type="radio" id="inlineRadio3" value="option2" name="radioInline3">
                                    <label for="inlineRadio3">Standalone</label>
                                </span>
                                <div class="help-block with-errors red-txt"></div>
                            </div>
                            </div>
							 <div class="clearfix"></div>
                           
                        </div>
                        
                        <div class="row mt-xlg">
                            <div class="col-md-12 pl-none">
                                <div class="gray-xs-f mb-xs">Platform(s) supported  <span class="requiredStar"> *</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The Tooltip plugin is small pop-up box that appears when the user moves."></span></div>
                               <div>
                                 <span class="checkbox checkbox-inline p-45"><input type="checkbox" id="number_of_moves_tower_stat_id" name="taskAttributeValueBos[1].useForStatistic" value="false">
          <label for="number_of_moves_tower_stat_id">ios</label>
        </span>
                               <span class="checkbox checkbox-inline p-45"><input type="checkbox" id="number_of_moves_tower_stat_id" name="taskAttributeValueBos[1].useForStatistic" value="false">
          <label for="number_of_moves_tower_stat_id">Android</label>
        </span>
                                <div class="help-block with-errors red-txt"></div>
                            </div>
                            </div>
							 <div class="clearfix"></div>
                            
                        </div>
                       
                        
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
                        
                        
                    </div>
                    
                 
                  
                    
                </div>
                
            </div>
            
        </div>
        <!-- End right Content here -->
            
    </div>
        
    </div>
</div>

<div class="clearfix"></div>



<script>
  $(document).ready(function () {
	  $('#rowId').parent().removeClass('white-bg');
	  
    $('#app_Wide_table_list').DataTable({
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