<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	String pageId = (String) request.getAttribute("error-pageId");
%>
<div width="100%">
<font color="#cc0000" face="Verdana" size="-1">
<b>
A problem has occurred.
<br/>
This page could not be rendered:
<br/>
<%=pageId%>
<br/>
Please notify your system administrator.
</b>
</font>

<br/>
<br/>
<jsp:include page="error-viewer.jsp"/>

</div>