<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	String templateId = (String) request.getAttribute("error-templateId");
	String regionId = (String) request.getAttribute("error-regionId");
	String regionSourceId = (String) request.getAttribute("error-regionSourceId");
%>
<div width="100%" id="<%=regionId%>">
<font color="#cc0000">
<b>
A problem has occurred.
<br/>
This region could not be rendered:
<br/>
templateId (<%=templateId%>)
<br/>
regionId (<%=regionId%>)
<br/>
regionSourceId (<%=regionSourceId%>)
<br/>
Please notify your system administrator.
</b>
</font>

<br/>
<br/>
<jsp:include page="error-viewer.jsp"/>

</div>