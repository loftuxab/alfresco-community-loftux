<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	String templateId = (String) request.getAttribute("error-templateId");
%>
<div width="100%">
<font color="#cc0000">
<b>
A problem has occurred.
<br/>
This template could not be rendered:
<br/>
<%=templateId%>
<br/>
Please notify your system administrator.
</b>
</font>

<br/>
<br/>
<jsp:include page="error-viewer.jsp"/>

</div>