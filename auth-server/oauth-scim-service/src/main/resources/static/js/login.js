/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

$(document).ready(function () {

  var gciEnabled = $('#gciEnabled').val();
  var gciApiKey = $('#gciApiKey').val();
  var gciAuthDomain = $('#gciAuthDomain').val();

  if(gciEnabled) {
  	var config = {
      apiKey: gciApiKey,
      authDomain: gciAuthDomain,
  	};
  	firebase.initializeApp(config);
  
	$("#loginForm").submit(function (event) {
		//stop submit the form, we will post it manually.
		event.preventDefault();
	    validateLoginForm();
	});
  } else {
    $("#loginForm").submit();
  }
});


function validateLoginForm() {

    var email = $(document.getElementById("email")).val();
   	var password = $(document.getElementById("password")).val();
	var errorDiv = document.getElementById("password_error");
	var serverContextPath = $('#serverContextPath').val(); 

    $.ajax({
        url: serverContextPath + "/isGCIUser",
        type: "POST",
        dataType: 'json',
	    data: {
	          email: email
	        },
        success: function getResponse(data) {
	        var isGCIUser = data.message;

	        if(isGCIUser) {

			  firebase.auth().onAuthStateChanged(function(user) {
		   	    if (user) {
		   	    console.log("success  " + email);
		   	    } else {
		   	    console.log("No user signed in " + email);
		   	    }
		   	  });
		   	  
		   	  firebase.auth().signInWithEmailAndPassword(email, password)
		   	  	.then(function(firebaseUser) {

	        	errorDiv.innerHTML = '';
			    errorDiv.style.display = "none";
	        	$("#loginForm").unbind();
 				$("#loginForm").submit();
		      	}).catch(function(error) {
		      	errorDiv.innerHTML = error;
		      	errorDiv.style.display = "block";
		        return false;
		      });
			} else {
			  errorDiv.innerHTML = '';
			  errorDiv.style.display = "none";
			  $("#loginForm").unbind();
	  	      $("#loginForm").submit();
	  		}

        },
        error: function (e) {
            console.log("ERROR : ", e);
        }
    });

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
