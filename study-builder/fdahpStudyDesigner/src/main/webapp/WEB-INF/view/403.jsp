<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date"/>
<c:set var="tz" value="America/Los_Angeles"/>
<!DOCTYPE html>
<html lang="en">
  <head>
    <!-- Basic -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>Study Builder</title>

    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">

    <!-- Favicon -->
    <link rel="shortcut icon" href="/studybuilder/images/icons/FAV_Icon.png" type="image/x-icon"/>
    <link rel="apple-touch-icon" href="/studybuilder/images/icons/FAV_Icon.png">

    <!-- Mobile Metas -->
    <meta name="viewport"
          content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">

    <!-- Web Fonts  -->
    <link href="https://fonts.googleapis.com/css?family=Roboto:300,400" rel="stylesheet">

    <!-- Vendor CSS -->
    <link rel="stylesheet" href="/studybuilder/vendor/boostrap/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css">
 
 
    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/studybuilder/css/layout.css">

    <!-- Theme CSS -->
    <link rel="stylesheet" href="/studybuilder/css/theme.css">
    <link rel="stylesheet" href="/studybuilder/css/style.css">

    <!-- Vendor -->
    <script src="/studybuilder/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/studybuilder/vendor/summernote/popper.min.js"></script>
    <script src="/studybuilder/vendor/boostrap/bootstrap.min.js"></script>
  
   
<style>
 header.header {
	 width: 100%;
	 height: 60px;
	 background-color: #fff;
	 user-select: none;
}
 header.header .navigation {
	 width: 100%;
	 height: 100%;
}
 header.header .navigation .header {
	 width: 200px;
	 height: 100%;
	 float: left;
	 display: inline-block;
}

.page-wrap {
  min-height: 100vh;
}

@media only screen and (max-width: 2560px) and (min-width: 1020px) {
  .custom__font_error {
    font-size: 140px !important;
  }
}

@media only screen and (max-width: 1019px) and (min-width: 760px) {
  .custom__font_error {
    font-size: 140px !important;
  }
}

@media only screen and (max-width: 760px) and (min-width: 320px) {
  .custom__font_error {
    font-size: 140px !important;
  }
}

.custom__font_error {
  color: #b3bcc6 !important;
  margin-top: -20px;
}

.custom_sub_font {
  color: #b3bcc6 !important;
  margin-top: -20px;
  font-size: 40px;
}

 .container, .row {
  height: 100%;
  min-height: 100%;
}

html, body {
  height: 100%;
}
</style>
  </head>
  <body>
   
    <!-- content start  -->
<header class="header">
    <div class="navigation">
        <div class="header pt-2">
           <a href="/studybuilder/login.do" class="navbar-brand">
          <img
            class="mr-sm width__auto"
           src="/studybuilder/images/logo/logo_innerScreens.png"
            alt="main logo" style="margin-top: 7px; margin-left: 25px;"
          />
        </a>
        </div>
    </div>
</header>


<div class="d-flex align-items-center justify-content-center" style="height: 100vh">
      <div class="col-md-12 text-center">
        <span class="display-1 d-block">
          <img src="/studybuilder/images/icons/ErrorIcon.svg" alt="Page not found here" />
        </span>
        <div class="custom__font_error">404</div>
        <div class="mb-4 custom_sub_font">
		<div class="custom__font_error">Oops 403!</div>
    	<span class="subTxt">Access is denied <br> You do not have permission to access this page!</span>
        </div>
      </div>
</div>
 
  </body>
</html>



   

