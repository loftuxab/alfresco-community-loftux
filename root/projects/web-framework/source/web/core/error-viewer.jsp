<%@ page import="org.alfresco.web.framework.model.*"%>
<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="java.io.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	RequestContext context = RequestUtil.getRequestContext(request);
	String objectId = (String) request.getAttribute("error-objectId");
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
<img id="i-<%=objectId%>" src="<%=collapsedImage%>" onclick="toggle('<%=objectId%>');"/>
<font face="Verdana" size="-1">Details...</font>
<br/>
<div id="e-<%=objectId%>" style="display: none">
<table width="100%">
<tr>
<td width="100%" align="left">
<pre>
<font face="Courier" size="-2">
<%
	
	PrintWriter pw = new PrintWriter(out);
	
	Throwable ex = t;
	ex.fillInStackTrace();
	while(ex != null)
	{
		ex.printStackTrace(pw);
		ex = ex.getCause();
		if(ex != null)
		{
			pw.print("\r\nCaused By...\r\n");
		}
	}
%>
</font>
</pre>
</td>
</tr>
</table>
</div>
<!-- end of error viewer -->

<%
	}
%>
