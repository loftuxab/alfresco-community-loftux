<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.util.URLEncoder" %>
<%@ page import="java.util.*" %>
<%
   // retrieve user name from the session
   String userid = (String)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
   
   // test user dashboard page exists?
   RequestContext context = (RequestContext)request.getAttribute(RequestUtil.ATTR_REQUEST_CONTEXT);
   if (context.getModel().getPage("user/" + userid + "/dashboard") == null)
   {
      // no site found! create initial dashboard for this user...
      Map<String, String> tokens = new HashMap<String, String>();
      tokens.put("userid", userid);
      FrameworkHelper.getPresetsManager().constructPreset(context.getModel(), "user-dashboard", tokens);
   }
   
   // forward to user specific dashboard page
   response.sendRedirect(request.getContextPath() + "/page/user/" + URLEncoder.encode(userid) + "/dashboard");
%>