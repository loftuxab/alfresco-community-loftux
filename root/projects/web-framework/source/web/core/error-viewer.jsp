<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	RequestContext context = RequestUtil.getRequestContext(request);
	String pageId = (String) request.getAttribute("error-pageId");
	Throwable t = (Throwable) request.getAttribute("error");
	if(t != null)
	{
%>

<!-- allows the error to be expanded and viewed -->
<%
	String collapsedImage = org.alfresco.web.site.URLUtil.browser(context, "/images/misc/collapsed.gif");
	String expandedImage = org.alfresco.web.site.URLUtil.browser(context, "/images/misc/expanded.gif");
%>	
<script language="Javascript">
var display = false;
function toggle(id)
{
	var imageDiv = document.getElementById("i-" + id);
	var errorDiv = document.getElementById("e-" + id);
	
	if(display == false)
	{
		imageDiv.src = '<%=expandedImage%>';
		errorDiv.style.display = "block";
		display = true;
	}
	else
	{
		imageDiv.src = '<%=collapsedImage%>';
		errorDiv.style.display = "none";
		display = false;
	}
}
</script>
<img id="i-<%=pageId%>" src="<%=collapsedImage%>" onclick="toggle('<%=pageId%>');"/>
<font face="Verdana" size="-1">Details...</font>
<br/>
<div id="e-<%=pageId%>" style="display: none">
<font face="Verdana" size="-1">
<%
	
	PrintWriter pw = new PrintWriter(out);
	t.printStackTrace(pw);
%>
</font>
</div>
<!-- end of error viewer -->

<%
	}
%>
