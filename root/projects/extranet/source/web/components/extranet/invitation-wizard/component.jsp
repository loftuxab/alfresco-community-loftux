<%
	// base path
	String path = "/components/extranet/invitation-wizard/";
	
	// place to which we want to dispatch	
	String dispatchTo = (String) request.getParameter("dispatchTo");
	if(dispatchTo == null)
	{
		path += "user-invitation.jsp";
	}
	else
	{
		path += dispatchTo;	
	}
	
	// do the dispatch
	request.getRequestDispatcher(path).include(request, response);
%>