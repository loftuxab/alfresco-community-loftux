<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.module.org_alfresco_module_cloud_share.*" %>
<%@ page import="org.springframework.extensions.surf.*" %>
<%@ page import="org.springframework.extensions.surf.site.*" %>
<%@ page import="org.springframework.extensions.surf.util.*" %>
<%@ page import="java.util.*" %>
<%
    // retrieve user name from the session
   String userid = (String)session.getAttribute(SlingshotUserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
   
   // Check to see if a tenant has been explicitly requested...
   String tenant = (String) request.getAttribute(TenantUtil.TENANT_NAME_REQUEST_ATTRIBUTE);
   if (tenant == null || tenant.equals("") || tenant.equals(TenantUtil.DEFAULT_TENANT_NAME))
   {
       // ...if not, grab the users home tenant
       TenantUser user = (TenantUser)session.getAttribute(SlingshotUserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
       tenant = user.getDefaultTenant();
       
       // Set the tenant as a request attribute. This is required because without a tenant it will
       // not be possible to find a dashboard for the user. This is because the CloudRemoteStore
       // accesses the /a WebScript service path that requires a tenant...
       request.setAttribute(TenantUtil.TENANT_NAME_REQUEST_ATTRIBUTE, tenant);
   }
   
   // test user dashboard page exists?
   RequestContext context = (RequestContext)request.getAttribute(RequestContext.ATTR_REQUEST_CONTEXT);
   if (context.getObjectService().getPage("user/" + userid + "/dashboard") == null)
   {
      // no user dashboard page found! create initial dashboard for this user...
      Map<String, String> tokens = new HashMap<String, String>();
      tokens.put("userid", userid);
      FrameworkUtil.getServiceRegistry().getPresetsManager().constructPreset("user-dashboard", tokens);
   }
   
   // redirect to site or user dashboard as appropriate
   String siteName = request.getParameter("site");
   if (siteName == null || siteName.length() == 0)
   {
      // forward to user specific dashboard page
      response.sendRedirect(request.getContextPath() + "/" + tenant + "/page/user/" + URLEncoder.encode(userid) + "/dashboard");
   }
   else
   {
      // forward to site specific dashboard page
      response.sendRedirect(request.getContextPath() + "/" + tenant + "/page/site/" + URLEncoder.encode(siteName) + "/dashboard");
   }
%>