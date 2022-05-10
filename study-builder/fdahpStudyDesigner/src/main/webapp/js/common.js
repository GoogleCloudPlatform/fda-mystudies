 /*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

/*

Name: 			common.js
Version: 		1.0

 */

var isShift = false;

/**
 * Check the given form is valid or not
 *
 * @param param ,
 *            form id
 * @returns {Boolean}
 */
function isFromValid(param) {
  $(param).validator('validate');
  if ($(param).find(".has-danger").length > 0) {
    return false;
  } else {
    return true;
  }
}

/**
 * reset validation js from the form
 *
 * @param param ,
 *            form id,
 * @returns void
 */
function resetValidation(param) {
  $(param).validator('destroy').validator();
}

function checkboxValidate(name) {
  var min = 1 // minumum number of boxes to be checked for this form-group
  if ($('input[name="' + name + '"]:checked').length < min) {
    $('input[name="' + name + '"]').prop('required', true);
  } else {
    $('input[name="' + name + '"]').prop('required', false);
  }
}

$(document)
    .ready(
        function () {

          // remove the arrow key event from date picker
          $(document).on('dp.show', function (e) {
            var $el = $(e.target);
            if ($el.data("DateTimePicker")) {
              $el.data("DateTimePicker").keyBinds({
                down: function (widget) {
                  return false;
                }
              });
            }
          });

          $("select[multiple='multiple']").on('change', function (e) {
            if (($(this).val()).length) {
              $(this).prop('required', false);
            } else {
              $(this).prop('required', true);
            }
          });
          $("button[type = submit]").on(
              'click',
              function (e) {
                if ($(this).hasClass("disabled")) {
                  e.preventDefault();
                  $(this).parents('form')
                      .validator('destroy').validator();
                  isFromValid($(this).parents('form'));
                }
              });
          $(document).on('change', 'input[type = text] , textarea',
              function (e) {
                $(this).val($.trim($(this).val()));
              });

          document.addEventListener("keydown", keyDownTextField,
              false);

          function keyDownTextField(e) {
            var evt = (e) ? e : window.event;
            var charCode = (evt.which) ? evt.which : evt.keyCode;
            if (charCode == 16)
              isShift = true;
          }

          $('input[type = text] , textarea')
              .keyup(
                  function (e) {
                    var wrappedString = $(this).val()
                        .toLowerCase();
                    if (wrappedString.indexOf('<script>') !== -1
                        || wrappedString
                            .indexOf('</script>') !== -1) {
                      e.preventDefault();
                      $(this).val('');
                      $(this).parent().addClass(
                          "has-danger").addClass(
                          "has-error");
                      $(this)
                          .parent()
                          .find(".help-block")
                          .empty()
                          .append($("<ul><li> </li></ul>")
                          .attr("class","list-unstyled")
                          .text("Special characters such as #^}{ are not allowed"));
                    } else {
                      $(this).parent()
                          .find(".help-block").empty();
                    }

                  });
          $('input[type = text][custAttType = customValidate]')
              .on(
                  'keyup',
                  function (e) {
                    var evt = (e) ? e : window.event;
                    var charCode = (evt.which) ? evt.which
                        : evt.keyCode;
                    if (charCode == 16)
                      isShift = false;
                    if (!isShift && $(this).val()) {
                      var regularExpression = /^[a-z0-9_-]*$/;
                      if (!regularExpression.test($(this)
                          .val())) {
                        var newVal = $(this)
                            .val()
                            .replace(
                                /[^a-z0-9_-]/g,
                                '');
                        e.preventDefault();
                        $(this).val(newVal);
                        $(this).parent().addClass(
                            "has-danger has-error");
                        $(this)
                            .parent()
                            .find(".help-block")
                            .empty()
                            .append($("<ul><li> </li></ul>")
                            .attr("class","list-unstyled")
                            .attr("style","white-space:nowrap")
                            .text("Please use allowed characters only: lowercase alphabets (a-z), digits (0-9), _ (underscore) and -(minus)"));
                      }
                    }
                  });       
          $('input[type = text][custAttType != cust]')
              .on(
                  'keyup',
                  function (e) {
                    var evt = (e) ? e : window.event;
                    var charCode = (evt.which) ? evt.which
                        : evt.keyCode;
                    if (charCode == 16)
                      isShift = false;
                    if (!isShift && $(this).val()) {
                      var regularExpression = /^[ A-Za-z0-9!\$&\*\(\)_+|:"?,.\;'\[\]=\-@]*$/;
                      if (!regularExpression.test($(this)
                          .val())) {
                        var newVal = $(this)
                            .val()
                            .replace(
                                /[^ A-Za-z0-9!\$&\*\(\)_+|:"?,.\;'\[\]=\-@]/g,
                                '');
                        e.preventDefault();
                        $(this).val(newVal);
                        $(this).parent().addClass(
                            "has-danger has-error");
                        $(this)
                            .parent()
                            .find(".help-block")
                            .empty()
                            .append($("<ul><li> </li></ul>")
                            .attr("class","list-unstyled")
                            .text("Special characters such as #^><}{ are not allowed"));
                      }
                    }
                  });
          $('input').on('drop', function () {
            return false;
          });
          $('input[type = text][custAttType = cust]')
              .on(
                  'keyup',
                  function (e) {
                    var evt = (e) ? e : window.event;
                    var charCode = (evt.which) ? evt.which
                        : evt.keyCode;
                    if (charCode == 16)
                      isShift = false;
                    if (!isShift && $(this).val()) {
                      var regularExpression = /^[A-Za-z0-9*()_+|:.-]*$/;
                      if (!regularExpression.test($(this)
                          .val())) {
                        var newVal = $(this)
                            .val()
                            .replace(
                                /[^A-Za-z0-9\*\(\)_+|:.\-]/g,
                                '');
                        e.preventDefault();
                        $(this).val(newVal);
                        $(this).parent().addClass(
                            "has-danger has-error");
                        $(this)
                            .parent()
                            .find(".help-block")
                            .empty()
                            .append($("<ul><li> </li></ul>")
                            .attr("class","list-unstyled")
                            .text("Please use characters from the following set only: A-Z a-z 0-9 *()_+|:.-"));
                      }
                    }
                  });

          $('div[role = textbox]')
              .keyup(
                  function (e) {
                    var wrappedString = $(this).text();
                    if (wrappedString.indexOf('<script>') !== -1
                        || wrappedString
                            .indexOf('</script>') !== -1) {
                      e.preventDefault();
                      $(this).empty();
                      $(this).parents().eq(2).find("textarea").val("");
                      $(this).parents().eq(2).addClass(
                          "has-danger").addClass(
                          "has-error");
                      $(this)
                         .parents().eq(2)
                          .find(".help-block")
                          .empty()
                          .append($("<ul><li> </li></ul>")
                          .attr("class","list-unstyled")
                          .text("Special characters such as #^}{ are not allowed"));
                    } else {
                      $(this).parents().eq(2)
                          .find(".help-block").empty();
                    }

                  });
          checkboxValidate($('.form-group input:checkbox').attr(
              'name'));
          $('.form-group').on("click load", 'input:checkbox',
              function () {
                checkboxValidate($(this).attr('name'));
              });
          $('.phoneMask').mask('000-000-0000');

          $(".phoneMask").keypress(function (event) {
            if ($(this).val() === "000-000-000") {
              event = (event || window.event);
              if (event.keyCode === 48) {
                $(this).val("");
              }
            }
          });

          $(".validateUserEmail")
              .blur(
                  function () {
                    var email = $(this).val();
                    var oldEmail = $(this).attr('oldVal');
                    var isEmail;
                    var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
                    isEmail = regEX.test(email);

                    if (isEmail
                        && ('' === oldEmail || ('' !== oldEmail && oldEmail !== email))) {
                      var csrfDetcsrfParamName = $(
                          '#csrfDet').attr(
                          'csrfParamName');
                      var csrfToken = $('#csrfDet').attr(
                          'csrfToken');
                      var thisAttr = this;
                      $(thisAttr)
                          .parent()
                          .find(".help-block")
                          .empty()
                          .append($("<ul><li> </li></ul>")
                          .attr("class","list-unstyled"));
                      if (email !== '') {
                        $
                            .ajax({
                              url: "/studybuilder/isEmailValid.do?"
                                  + csrfDetcsrfParamName
                                  + "="
                                  + csrfToken,
                              type: "POST",
                              datatype: "json",
                              data: {
                                email: email,
                              },
                              success: function getResponse(
                                  data) {
                                var message = data.message;
                                if ('SUCCESS' !== message) {
                                  $(thisAttr)
                                      .validator(
                                          'validate');
                                  $(thisAttr)
                                      .parent()
                                      .removeClass(
                                          "has-danger")
                                      .removeClass(
                                          "has-error");
                                  $(thisAttr)
                                      .parent()
                                      .find(
                                          ".help-block")
                                      .empty();
                                } else {
                                  $(thisAttr)
                                      .val(
                                          '');
                                  $(thisAttr)
                                      .parent()
                                      .addClass(
                                          "has-danger")
                                      .addClass(
                                          "has-error");
                                  $(thisAttr)
                                      .parent()
                                      .find(
                                          ".help-block")
                                      .empty();
                                  $(thisAttr)
                                      .parent()
                                      .find(
                                          ".help-block")
                                      .append($("<ul><li> </li></ul>")
                                      .attr("class","list-unstyled").text(email
                                          + " already exists"));
                                }
                              }
                            });
                      }
                    }
                  });

          document.onkeypress = function (event) {
            event = (event || window.event);
            if (event.keyCode == 123) {
              alert("This action is disabled.")
              return false;
            }
          }

          document.onmousedown = function (event) {
            event = (event || window.event);
            if (event.keyCode == 123) {
              alert("This action is disabled.")
              return false;
            }
          }

          document.onkeydown = function (e) {
            if (e.keyCode == 123) {
              alert("This action is disabled.");
              return false;
            }
            if (e.ctrlKey && e.shiftKey
                && e.keyCode == 'I'.charCodeAt(0)) {
              alert("This action is disabled.");
              return false;
            }
            if (e.ctrlKey && e.shiftKey
                && e.keyCode == 'J'.charCodeAt(0)) {
              alert("This action is disabled.");
              return false;
            }
            if (e.ctrlKey && e.keyCode == 'U'.charCodeAt(0)) {
              alert("This action is disabled.");
              return false;
            }
            if (e.ctrlKey && e.shiftKey
                && e.keyCode == 'C'.charCodeAt(0)) {
              alert("This action is disabled.");
              return false;
            }
          }

        });

/**
 * param String
 *
 * This method is used to replace the special characters (single and double
 * quotes with HTML number)
 */
function replaceSpecialCharacters(inputFormat) {
  var replacedString = "";
  if (inputFormat !== null && inputFormat !== '' && inputFormat !== undefined) {
    while (inputFormat.includes('"') || inputFormat.includes("'")) {
      inputFormat = inputFormat.toString().replace("'", '&#39;'); // replce
      // single
      // quote
      inputFormat = inputFormat.toString().replace('"', '&#34;'); // replce
      // double
      // quote
    }
    replacedString = inputFormat;
  }
  return replacedString;
}

/**
 *
 *
 * This method is used for dropdown animation
 */

$(document)
    .ready(
        function () {

          /* common script for dropdown animation */
          $('.navbar .dropdown').click(
              function () {
                $(this).find('.dropdown-menu').first()(true)
                    .slideToggle(100);
              },
              function () {
                $(this).find('.dropdown-menu').first()(true)
                    .slideToggle(200)
              });
          /* common script for dropdown animation ends */
          $('#signPasswordBut')
              .click(
                  function () {
                    $("#signUpForm").validator('validate');
                    if ($("#signUpForm")
                        .find(".has-danger").length > 0) {
                      isValidLoginForm = false;
                    } else {
                      isValidLoginForm = true;
                    }
                    if (isValidLoginForm) {
                      $("#signUpForm").validator(
                          'destroy');
                      $('#password')
                          .val(
                              $('#password')
                                  .val()
                              + $(
                              '#csrfDet')
                                  .attr(
                                      'csrfToken'));
                      $('#hidePass').val(
                          $('#password').val());
                      $('#password').val('');
                      $('#password').unbind().attr(
                          "type", "text").css(
                          '-webkit-text-security',
                          'disc');
                      $('#password').attr("pattern", "");
                      $('#password').attr(
                          "data-minlength", "");
                      var passwordLength = "";
                      var i;
                      for (i = 0; i < $('#password').val().length; i++) {
                        passwordLength += "*";
                      }
                      $('#password')
                          .val(passwordLength);
                      $('#cfnPassword')
                          .unbind()
                          .attr("type", "text")
                          .css(
                              '-webkit-text-security',
                              'disc')
                          .val(passwordLength);
                      $('#hideOldPass')
                          .val(
                              $('#oldPassword')
                                  .val()
                              + $(
                              '#csrfDet')
                                  .attr(
                                      'csrfToken'));
                      $('#oldPassword')
                          .unbind()
                          .attr("type", "text")
                          .css(
                              '-webkit-text-security',
                              'disc')
                          .val(passwordLength);
                      $('#signUpForm').submit();
                    }

                  });

          $('#signUpForm')
              .keypress(
                  function (e) {
                    if (e.which == 13) {
                      $("#signUpForm").validator(
                          'validate');
                      if ($("#signUpForm").find(
                          ".has-danger").length > 0) {
                        isValidLoginForm = false;
                      } else {
                        isValidLoginForm = true;
                      }
                      if (isValidLoginForm) {
                        $("#signUpForm").validator(
                            'destroy');
                        $('#password')
                            .val(
                                $('#password')
                                    .val()
                                + $(
                                '#csrfDet')
                                    .attr(
                                        'csrfToken'));
                        $('#hidePass').val(
                            $('#password').val());
                        $('#password').val('');
                        $('#password')
                            .unbind()
                            .attr("type", "text")
                            .css(
                                '-webkit-text-security',
                                'disc');
                        $('#password').attr("pattern",
                            "");
                        $('#password').attr(
                            "data-minlength", "");
                        var passwordLength = "";
                        var i;
                        for (i = 0; i < $('#password').val().length; i++) {
                          passwordLength += "*";
                        }
                        $('#password')
                            .val(passwordLength);
                        $('#cfnPassword')
                            .unbind()
                            .attr("type", "text")
                            .css(
                                '-webkit-text-security',
                                'disc')
                            .val(passwordLength);
                        $('#hideOldPass')
                            .val(
                                $(
                                    '#oldPassword')
                                    .val()
                                + $(
                                '#csrfDet')
                                    .attr(
                                        'csrfToken'));
                        $('#oldPassword')
                            .unbind()
                            .attr("type", "text")
                            .css(
                                '-webkit-text-security',
                                'disc')
                            .val(passwordLength);
                        $('#signUpForm').submit();
                      }
                    }
                  });

          $('#resetPasswordBut')
              .click(
                  function () {
                    $("#passwordResetForm").validator(
                        'validate');
                    if ($("#passwordResetForm").find(
                        ".has-danger").length > 0) {
                      isValidLoginForm = false;
                    } else {
                      isValidLoginForm = true;
                    }
                    if (isValidLoginForm) {
                      $("#passwordResetForm").validator(
                          'destroy');
                      $('#password')
                          .val(
                              $('#password')
                                  .val()
                              + $(
                              '#csrfDet')
                                  .attr(
                                      'csrfToken'));
                      $('#hidePass').val(
                          $('#password').val());
                      $('#password').val('');
                      $('#password').unbind().attr(
                          "type", "text").css(
                          '-webkit-text-security',
                          'disc');
                      $('#password').attr("pattern", "");
                      $('#password').attr(
                          "data-minlength", "");
                      var passwordLength = "";
                      var i;
                      for (i = 0; i < $('#password').val().length; i++) {
                        passwordLength += "*";
                      }
                      $('#password')
                          .val(passwordLength);
                      $('#cfnPassword')
                          .unbind()
                          .attr("type", "text")
                          .css(
                              '-webkit-text-security',
                              'disc')
                          .val(passwordLength);
                      $('#hideOldPass')
                          .val(
                              $('#oldPassword')
                                  .val()
                              + $(
                              '#csrfDet')
                                  .attr(
                                      'csrfToken'));
                      $('#oldPassword')
                          .unbind()
                          .attr("type", "text")
                          .css(
                              '-webkit-text-security',
                              'disc')
                          .val(passwordLength);
                      $('#passwordResetForm').submit();
                    }

                  });

          $('#passwordResetForm')
              .keypress(
                  function (e) {
                    if (e.which === 13) {
                      $("#passwordResetForm").validator(
                          'validate');
                      if ($("#passwordResetForm").find(
                          ".has-danger").length > 0) {
                        isValidLoginForm = false;
                      } else {
                        isValidLoginForm = true;
                      }
                      if (isValidLoginForm) {
                        $("#passwordResetForm")
                            .validator('destroy');
                        $('#password')
                            .val(
                                $('#password')
                                    .val()
                                + $(
                                '#csrfDet')
                                    .attr(
                                        'csrfToken'));
                        $('#hidePass').val(
                            $('#password').val());
                        $('#password').val('');
                        $('#password')
                            .unbind()
                            .attr("type", "text")
                            .css(
                                '-webkit-text-security',
                                'disc');
                        $('#password').attr("pattern",
                            "");
                        $('#password').attr(
                            "data-minlength", "");
                        var passwordLength = "";
                        var i;
                        for (i = 0; i < $('#password').val().length; i++) {
                        	passwordLength += "*";
                        }
                        $('#password')
                            .val(passwordLength);
                        $('#cfnPassword')
                            .unbind()
                            .attr("type", "text")
                            .css(
                                '-webkit-text-security',
                                'disc')
                            .val(passwordLength);
                        $('#hideOldPass')
                            .val(
                                $(
                                    '#oldPassword')
                                    .val()
                                + $(
                                '#csrfDet')
                                    .attr(
                                        'csrfToken'));
                        $('#oldPassword')
                            .unbind()
                            .attr("type", "text")
                            .css(
                                '-webkit-text-security',
                                'disc')
                            .val(passwordLength);
                        $('#passwordResetForm')
                            .submit();
                      }
                    }
                  });

			function multiFactorAuth(fdaLink, email, password, passwordLength, userPhoneNumber) {
			
			 this.recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptcha-container');
			 
			  document.getElementById("recaptcha-container").style.display = "inline-block";
			 setTimeout(function() {
	           	var provider = new firebase.auth.PhoneAuthProvider();
				return provider.verifyPhoneNumber(userPhoneNumber, recaptchaVerifier)
				    .then(function(verificationId) {
				     $('#recaptcha-container').hide();
				     // Ask user for the verification code.
				     
				     var form = $('<form><div class="bootbox-font">Please enter the verification code that was sent to your mobile device.</div><div class="float-left mb-xs mt-md"><input name="verificationCode" class="popup_input" autocomplete="off"/></div></form>');

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
						    $("body").addClass("loading");
						    $('#recaptcha-container').hide();
						     viewDashBoard(fdaLink, email, password, passwordLength);
						  })
 						  .catch(function(error) {
			   	            $('#password')
	                            .val('');
	                        $(
	                            ".askSignInCls")
	                            .addClass(
	                                'hide');
	                        $("#errMsg")
	                            .text(
	                                error.message);
	                        $("#errMsg")
	                            .show(
	                                "fast");
	                        setTimeout(
	                            hideDisplayMessage,
	                            5000);
	                        $('#password')
	                            .attr(
	                                "type",
	                                "password");
	                        $('#email')
	                            .val(
	                                email);
	                        $("body")
	                            .removeClass(
	                                "loading");
	                        return false;
                       });
					
				          }
				        }
				      })			
						
			          }).catch(function (error) {
			           $('#recaptcha-container').hide();
			          
			           if (error.code == 'auth/invalid-verification-code') {
			           
				            $('#password')
				                .val('');
				            $(
				                ".askSignInCls")
				                .addClass(
				                    'hide');
				            $("#errMsg")
				                .text(
				                    "The SMS verification code used to create the phone auth credential is invalid."
									+ "Please login again and use the correct verification code sent to your registered phone number.");
				            $("#errMsg")
				                .show(
				                    "fast");
				            setTimeout(
				                hideDisplayMessage,
				                5000);
				            $('#password')
				                .attr(
				                    "type",
				                    "password");
				            $('#email')
				                .val(
				                    email);
				            $("body")
				                .removeClass(
				                    "loading");
				            return false;
			           } else if(error.code == 'auth/argument-error') {
			              $('#password')
				                .val('');
				            $(
				                ".askSignInCls")
				                .addClass(
				                    'hide');
				            $("#errMsg")
				                .text(
				                    "");
				            $("#errMsg")
				                .show(
				                    "fast");
				            setTimeout(
				                hideDisplayMessage,
				                1000);
				            $('#password')
				                .attr(
				                    "type",
				                    "password");
				            $('#email')
				                .val(
				                    email);
				            $("body")
				                .removeClass(
				                    "loading");
				            return false;
			           } else {
			           
			   	           $('#password')
				                .val('');
				            $(
				                ".askSignInCls")
				                .addClass(
				                    'hide');
				            $("#errMsg")
				                .text(
				                    error.message);
				            $("#errMsg")
				                .show(
				                    "fast");
				            setTimeout(
				                hideDisplayMessage,
				                5000);
				            $('#password')
				                .attr(
				                    "type",
				                    "password");
				            $('#email')
				                .val(
				                    email);
				            $("body")
				                .removeClass(
				                    "loading");
				            return false;
			            
			            }
		
		            });

			  }, 1000);
  
			  
			}
  
			function viewDashBoard(fdaLink, email, password, passwordLength) {

		         $.ajax({
                    url: fdaLink,
                    type: "POST",
                    datatype: "json",
                    data: {
                      username: email,
                      password: password,
                    },
                    success: function (data) {
                      var jsonobject = data;
                      var message = jsonobject.message;
                      if (message == "SUCCESS") {
                        $('#email')
                            .val(email);
                        $('#password')
                            .val(passwordLength);
                        $('#landingId')
                            .submit();
                        var a = document
                            .createElement('a');
                        a.href = "/studybuilder/adminDashboard/viewDashBoard.do?action=landing";
                        document.body
                            .appendChild(
                                a)
                            .click();
                      } else {
                        $('#password')
                            .val('');
                        $(
                            ".askSignInCls")
                            .addClass(
                                'hide');
                        $("#errMsg")
                            .text(
                                message);
                        $("#errMsg")
                            .show(
                                "fast");
                        setTimeout(
                            hideDisplayMessage,
                            5000);
                        $('#password')
                            .attr(
                                "type",
                                "password");
                        $('#email')
                            .val(
                                email);
                        $("body")
                            .removeClass(
                                "loading");
                      }
                    },
                    global: false
                  })
				}

          $('#loginBtnId')
              .click(
                  function () {
                    $("#loginForm").validator('validate');
                    if ($("#loginForm").find(".has-danger").length > 0) {
                      isValidLoginForm = false;
                    } else {
                      isValidLoginForm = true;
                    }
                    if (isValidLoginForm) {
                      var email = $('#email').val();
                      var password = $('#password').val();
                      var passwordLength = "";
                      var i;
                      for (i = 0; i < $('#password').val().length; i++) {
                        passwordLength += "*";
                      }
                      $('#password')
                          .val(passwordLength);
                      $('#password')
                          .attr("type", "text")
                          .css(
                              '-webkit-text-security',
                              'disc');
                      var csrfDetcsrfParamName = $(
                          '#csrfDet').attr(
                          'csrfParamName');
                      var csrfToken = $('#csrfDet').attr(
                          'csrfToken');
                      var isGCIUser = false;
                      var fdaLink = $('#fdaLink').val();
                      var gciEnabled = $('#gci').val();
                      var mfaEnabled = $('#mfa').val();
                      if(gciEnabled == 'true'){
                       		$.ajax({
                              url: "/studybuilder/getGCIUserData.do?"
                                  + csrfDetcsrfParamName
                                  + "="
                                  + csrfToken,
                              type: "POST",
                              datatype: "json",
                              data: {
                                email: email,
                              },
                              success: function getResponse(
                                  data) {
                                var isGCIUser = data.gciUser;
                                var userPhoneNumber = data.userPhoneNumber;
			                     if(isGCIUser) {
				   	   			  firebase.auth().onAuthStateChanged(function(user) {
							   	    if (user) {
							   	//    alert("success  " + email);
							   	    } else {
							   	 //   alert("No user signed in " + email);
							   	    }
							   	  });
						
							   	  firebase.auth().signInWithEmailAndPassword(email, password)
							   	  .then(function(firebaseUser) {
								   	  if(mfaEnabled == 'true'){
								   	    $('#recaptcha-container').show();
								   	   	multiFactorAuth(fdaLink, email, password, passwordLength, userPhoneNumber);
								   	  } else {
								   	    $("body").addClass("loading");
								   	    viewDashBoard(fdaLink, email, password, passwordLength); 
								   	  }
								   })
								   .catch(function(error) {
							   	   if(error.code == 'auth/argument-error') {
						              $('#password')
							                .val('');
							            $(
							                ".askSignInCls")
							                .addClass(
							                    'hide');
							            $("#errMsg")
							                .text(
							                    "");
							            $("#errMsg")
							                .show(
							                    "fast");
							            setTimeout(
							                hideDisplayMessage,
							                1000);
							            $('#password')
							                .attr(
							                    "type",
							                    "password");
							            $('#email')
							                .val(
							                    email);
							            $("body")
							                .removeClass(
							                    "loading");
							            return false;
						           } else if (error.code == 'auth/too-many-requests') {
						           		$('#password')
							                .val('');
							            $(
							                ".askSignInCls")
							                .addClass(
							                    'hide');
							            $("#errMsg")
							                .text(
							                    "Access to this account has been temporarily disabled due to many failed login attempts. Please contact your IT admin to immediately restore it by resetting your password or you can try again later.");
							            $("#errMsg")
							                .show(
							                    "fast");
							            setTimeout(
							                hideDisplayMessage,
							                8000);
							            $('#password')
							                .attr(
							                    "type",
							                    "password");
							            $('#email')
							                .val(
							                    email);
							            $("body")
							                .removeClass(
							                    "loading");
							            return false;
						           } else {
						   	           $('#password')
				                            .val('');
				                        $(
				                            ".askSignInCls")
				                            .addClass(
				                                'hide');
				                        $("#errMsg")
				                            .text(
				                                error.message);
				                        $("#errMsg")
				                            .show(
				                                "fast");
				                        setTimeout(
				                            hideDisplayMessage,
				                            5000);
				                        $('#password')
				                            .attr(
				                                "type",
				                                "password");
				                        $('#email')
				                            .val(
				                                email);
				                        $("body")
				                            .removeClass(
				                                "loading");
				                        return false;
				                     }  
			                       });
				   	  		} else {
				   	  		   $("body").addClass("loading");
				   	  	       viewDashBoard(fdaLink, email, password, passwordLength);
				   	  		}
				                 },
				                  global: false
                            });
                            } else {
                               $("body").addClass("loading");
				   	  	       viewDashBoard(fdaLink, email, password, passwordLength);
				   	  		}
				   	  		
	                    }
	                  }); 
        });
