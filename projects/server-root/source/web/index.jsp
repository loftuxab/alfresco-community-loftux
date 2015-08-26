<%@page session="true" import="javax.servlet.ServletContext, javax.servlet.RequestDispatcher"
%><%
if(request.getMethod().equals("PROPFIND") || request.getMethod().equals("OPTIONS"))
{
    ServletContext alfrescoContext = application.getContext("/alfresco");
    if( (alfrescoContext != null) && !alfrescoContext.equals(getServletConfig().getServletContext()) )
    {
        RequestDispatcher rd = alfrescoContext.getRequestDispatcher("/AosResponder_ServerRoot");
        if(rd != null)
        {
            rd.forward(request, response);
            return;
        }
    }
}
%>
<html><body>Welcome to Alfresco!</body></html>