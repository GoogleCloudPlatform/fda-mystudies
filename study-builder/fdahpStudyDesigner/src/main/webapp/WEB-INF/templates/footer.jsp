<div class="clearfix"></div>

<div class="md-container ft_con">
  <jsp:include page="copyright.jsp">
    <jsp:param name="footerClass" value="foot" />
  </jsp:include>
</div>
<!-- Modal -->
<div class="modal fade" id="termsModal" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">

      <div class="modal-header cust-hdr">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
        <div>
          <div class="mt-md mb-md"><u><b>Terms</b></u></div>
          <span>${sessionObject.termsText}</span>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="privacyModal" role="dialog">
  <div class="modal-dialog modal-lg">
    <!-- Modal content-->
    <div class="modal-content">

      <div class="modal-header cust-hdr">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
      </div>
      <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
        <div>
          <div class="mt-md mb-md"><u><b>Privacy Policy</b></u></div>
          <span>${sessionObject.privacyPolicyText}</span>
        </div>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
  $(document).ready(function (e) {
    $('#termsId').on('click', function () {
      $('#termsModal').modal('show');
    });

    $('#privacyId').on('click', function () {
      $('#privacyModal').modal('show');
    });
  });
</script>