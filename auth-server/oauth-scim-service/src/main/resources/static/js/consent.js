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
  }, 660000);
  
  window.setTimeout(function() {
  hideLoader();
  }, 2000);
};

var hideLoader = function () {
  $(".loading_div").fadeOut("slow");
  $("#errorMsg").show();
  $("#errorImg").show();
};
