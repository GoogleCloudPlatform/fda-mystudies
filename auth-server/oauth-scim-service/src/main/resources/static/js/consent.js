/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

window.onload = function() {
  window.setTimeout(function() {
    document.consentForm.submit();
  }, 500);
  
  window.setTimeout(function() {
  document.consentForm.submit();
  hideLoader();
  }, 60000);
};

var hideLoader = function () {
  $(".loading_div").fadeOut("slow");
  $("#errorMsg").show();
};