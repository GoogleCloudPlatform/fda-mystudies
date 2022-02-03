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
	var mfaEnabled = $('#mfaEnabled').val();
	  
	$.ajax({
	    url: serverContextPath + "/isGCIUser",
	    type: "POST",
	    dataType: 'json',
	    data: {
	          email: email
	        },
	    success: function getResponse(data) {
	        var isGCIUser = data.isGciUser;
	        var phoneNumber = data.phoneNumber;
	
	        if(isGCIUser == 'true') {
	
			  firebase.auth().onAuthStateChanged(function(user) {
		   	    if (user) {
		   	    console.log("success  " + email);
		   	    } else {
		   	    console.log("No user signed in " + email);
		   	    }
		   	  });
		   	  
		   	  firebase.auth().signInWithEmailAndPassword(email, password)
		   	  	.then(function(firebaseUser) {
	   	  		  if(mfaEnabled == 'true'){
			   	    $('#recaptcha-container').show();
			   	   	multiFactorAuth(email, password, phoneNumber);
			   	  } else {
		        	errorDiv.innerHTML = '';
				    errorDiv.style.display = "none";
		        	$("#loginForm").unbind();
	 				$("#loginForm").submit();
			   	  }
								   	  
		      	}).catch(function(error) {
		      	
		      	if (error.code == 'auth/too-many-requests') {
		      	  errorDiv.innerHTML = "Access to this account has been temporarily disabled due to many failed login attempts. Please contact your IT admin to immediately restore it by resetting your password or you can try again later.";
			      errorDiv.style.display = "block";
			      return false;
		      	} else {
			      errorDiv.innerHTML = error;
			      errorDiv.style.display = "block";
			      return false;
		        }
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


function multiFactorAuth(email, password, phoneNumber) {
			
  var errorDiv = document.getElementById("password_error");
  this.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptcha-container');

  window.setTimeout(function() {
   	var provider = new firebase.auth.PhoneAuthProvider();
	return provider.verifyPhoneNumber(phoneNumber, recaptchaVerifier)
	    .then(function(verificationId) {
	     $('#recaptcha-container').hide();
	     // Ask user for the verification code.
	     var form = $('<form><div class="bootbox-font">Please enter the verification code that was sent to your mobile device.</div><div class="float-left mt-lg mb-lg"><input name="verificationCode" autocomplete="off"/></div></form>');

	    bootbox.confirm({
	      closeButton: false,
	      message: form,
	      buttons: {
	        'cancel': {
	          label: 'Cancel',
	        },
	        'confirm': {
	          label: 'OK',
	        },
	      },
	      callback: function (result) {
	        if (result) {
		 	  var verificationCode = form.find('input[name=verificationCode]').val();
	      	  //verificationCode
			  var cred = firebase.auth.PhoneAuthProvider.credential(verificationId,
	          verificationCode);
	          // sign in the user with the credential
	          return firebase.auth().signInWithCredential(cred)
			  .then((cred) => {
			    $('#recaptcha-container').hide();
	        	errorDiv.innerHTML = '';
			    errorDiv.style.display = "none";
	        	$("#loginForm").unbind();
 				$("#loginForm").submit();
			  })
			  .catch(function(error) {
   	          errorDiv.innerHTML = error;
  			  errorDiv.style.display = "block";
    		  return false;
           });
		
	          }
	        }
	      })			
			
          }).catch(function (error) {
           $('#recaptcha-container').hide();
           recaptchaVerifier.reset();
           if (error.code == 'auth/invalid-verification-code') {
	          errorDiv.innerHTML = "The SMS verification code used to create the phone auth credential is invalid."
						+ "Please login again and use the correct verification code sent to your registered phone number.";
  			  errorDiv.style.display = "block";
    		  return false;
           } else if(error.code == 'auth/argument-error') {
	          errorDiv.innerHTML = error;
  			  errorDiv.style.display = "block";
    		  return false;
           } else {
              errorDiv.innerHTML = error;
  			  errorDiv.style.display = "block";
    		  return false;
           }
        });

  }, 3000);
  
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
