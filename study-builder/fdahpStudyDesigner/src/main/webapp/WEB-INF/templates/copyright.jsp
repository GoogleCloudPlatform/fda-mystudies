<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("application");
String releaseVersion=resource.getString("release.version");
%>

<div class="${param.footerClass}" style="padding-left: 2%;">
  <span>Copyright</span>
	<span><a href="/studybuilder/terms.do" id="" target="_blank">Terms</a></span>
	<span><a href="/studybuilder/privacyPolicy.do" id="" target="_blank">Privacy Policy</a></span>
    <span>v<%=releaseVersion %></span> 
</div>

