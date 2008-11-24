<%@ page import="org.alfresco.web.framework.*" %>
<%@ page import="org.alfresco.web.site.RequestContext" %>
<%@ page import="org.alfresco.web.site.RequestUtil" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	RequestContext context = RequestUtil.getRequestContext(request);
%>


//
// Surf Namespace
//
function Surf() {} 


//
// RequestContext object
//
Surf.RequestContext = function()
{
	var nullCheck = function(o)
	{
		if(o == "null") { o = null; }
		return o;	
	};
	
	return {
	
		id : nullCheck("<%=context.getId()%>")
		,
		websiteTitle : nullCheck("<%=context.getWebsiteTitle()%>")
		,
		pageTitle : nullCheck("<%=context.getPageTitle()%>")
		,
		uri : nullCheck("<%=context.getUri()%>")
		,
		pageId : nullCheck("<%=context.getPageId()%>")
		,
		templateId : nullCheck("<%=context.getTemplateId()%>")
		,
		objectId : nullCheck("<%=context.getCurrentObjectId()%>")
		,
		formatId : nullCheck("<%=context.getFormatId()%>")
		,
		themeId : nullCheck("<%=context.getThemeId()%>")
		,
		getRootPageId : function() {
			<%
				String rootPageId = null;
				if(context.getRootPage() != null)
				{
					rootPageId = context.getRootPage().getId();
				}		
			%>
			return nullCheck("<%=rootPageId%>");		
		}
		,
		getRootPageTitle : function() {
			<%
				String rootPageTitle = null;
				if(context.getRootPage() != null)
				{
					rootPageTitle = context.getRootPage().getTitle();
				}		
			%>
			return nullCheck("<%=rootPageTitle%>");		
		}		
		,
		getStoreId : function() {
			<%
				String storeId = (String) context.getModel().getObjectManager().getContext().getValue(ModelPersistenceContext.REPO_STOREID);
				if(storeId == null) { %>return null;<% }
				if(storeId != null) { %>return "<%=storeId%>";<% }
			%>
		}
		,
		getWebappId : function() {
			<%
				String webappId = (String) context.getModel().getObjectManager().getContext().getValue(ModelPersistenceContext.REPO_WEBAPPID);
				if(webappId == null) { %>return null;<% }
				if(webappId != null) { %>return "<%=webappId%>";<% }
			%>
		}
		,
		getCurrentPageId : function() {
			return this.pageId;
		}
		,
		getCurrentTemplateId : function() {
			return this.templateId;
		}
	};	
};



//
// Helper Functions
//
Surf.wait = function(msecs)
{
	var start = new Date().getTime();
	var cur = start
	while(cur - start < msecs)
	{
		cur = new Date().getTime();
	}
}



//
// Additional empowerments
//
String.prototype.startsWith = function(s) { return this.indexOf(s)==0; }
String.prototype.endsWith = function(str) { return (this.match(str+"$")==str); }



//
// Instances
//
Surf.context = new Surf.RequestContext();
