<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	String componentId = (String) request.getAttribute("error-componentId");
%>
<div width="100%">
<font color="#cc0000">
<b>
A problem has occurred.
<br/>
This component could not be rendered:
<br/>
<%=componentId%>
<br/>
Please notify your system administrator.
</b>
</font>

<br/>
<br/>
<jsp:include page="error-viewer.jsp"/>

</div>