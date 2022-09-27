/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

function preventDoubleClick() {
	var form = document.getElementById('loginForm');
	var submitButton = document.getElementById('signInID');

	form.addEventListener('submit', function() {
		submitButton.disabled = true;
		setTimeout(()=>{
	submitButton.disabled = false;
		}, 5000)
	}, false);
}

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

  preventDoubleClick();
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


$(".toggle-password").click(function () {

	    $(this).toggleClass("fa-eye-slash fa-eye");
	    
	    var input = $("#password");
	    input.attr('type') === 'password' ? input.attr('type','text') : input.attr('type','password')
	});
	
