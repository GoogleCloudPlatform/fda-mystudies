/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

var fieldErrors = {
	"email": {
		"required": "Enter an email",
		"invalid": "Enter a valid email"
	},
	"password": {
		"required": "Enter a password",
		"invalid": "Enter a valid password"
	}
}
function validateField(elementId) {
	var element = document.getElementById(elementId);
	var isValid = element.checkValidity();
	var errorDiv = document.getElementById(elementId + "_error");

	if (element.value === '') {
		errorDiv.innerHTML = fieldErrors[elementId].required;
		errorDiv.style.display = "block";
	} else if (!isValid) {
		errorDiv.innerHTML = fieldErrors[elementId].invalid;
		errorDiv.style.display = "block";
	} else {
		errorDiv.innerHTML = '';
		errorDiv.style.display = "none";
	}

	return isValid;
}

function validateform() {
	return validateField("email") && validateField("password");
}