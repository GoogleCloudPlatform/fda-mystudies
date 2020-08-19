/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

function validateField(elementId) {
  var element = document.getElementById(elementId);
  var isValid = element.checkValidity();
  var errorDiv = document.getElementById(elementId + "_error");

  if (isValid) {
    errorDiv.style.display = "none";
  } else {
    errorDiv.style.display = "block";
  }

  return isValid;
}

function validateform() {
  return validateField("email") && validateField("password");
}