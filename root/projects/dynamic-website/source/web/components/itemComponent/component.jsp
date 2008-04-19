<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page import="org.alfresco.web.site.remote.*" %>
<%@ page import="org.alfresco.web.site.config.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="org.dom4j.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	RuntimeConfig configuration = (RuntimeConfig) request.getAttribute("component-configuration");	

	// settings	
	String theItemType = (String) configuration.get("itemType");
	String theItemPath = (String) configuration.get("itemPath");	
	String howToRender = (String) configuration.get("howToRender");
	String renderData = (String) configuration.get("renderData");
	String endpointId = (String) configuration.get("endpointId");
	

	// find this endpoint
	Endpoint endpoint = ModelUtil.getEndpoint(context, endpointId);
	if(endpoint == null)
	{
		out.println("No endpoint specified");
		return;
	}
	
	// if we're showing the current item, grab the path
	if("current".equalsIgnoreCase(theItemType))
		theItemPath = context.getCurrentObjectId();
		
		
	// nothing configured yet
	if(theItemPath == null)
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = URLUtil.browser(context, "/ui/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Item Component'/>";	
		out.println(renderString);
		return;
	}
	




	// special case
	if("direct".equalsIgnoreCase(howToRender))
	{
		RequestUtil.include(request, response, theItemPath);
		return;
	}



	/////////////////////
	// rendering
	/////////////////////

	// TEMPORARY
	theItemPath = "/ROOT" + theItemPath;

	// build the web script connector
	//String webscriptUri = "/ads/search/lucene/ads--admin";
	String webscriptUri = "/ads/search/lucene/" + context.getStoreId();
	WebscriptConnector connector = RemoteFactory.newWebscriptConnector(context, endpoint, webscriptUri);
	HashMap params = new HashMap();
	params.put("p", theItemPath);
	params.put("format", "xml");
	
	// fire the script and grab the results from our search filter
	String xml = connector.connect(params, null);
	Document xmlDocument = XMLUtil.parse(xml);

	// now handle the results
        if("templateTitle".equals(howToRender))
        {
        	// for each item, grab the template rendition with this name
        	// and include it
        	
        	List itemList = XMLUtil.getChildren(xmlDocument.getRootElement(), "item");
        	for(int i = 0; i < itemList.size(); i++)
        	{
        		Element item = (Element) itemList.get(i);
        		
        		// path to the xml item
        		String itemId = (String) XMLUtil.getChildValue(item, "id");
        		String itemPath = (String) XMLUtil.getChildValue(item, "path");
        		String itemNodeRef = (String) XMLUtil.getChildValue(item, "nodeRef");
        		
        		// get each rendition
        		List renditionList = XMLUtil.getChildren(item, "rendition");
        		for(int j = 0; j < renditionList.size(); j++)
        		{
        			Element rendition = (Element) renditionList.get(j);
        			String _templateTitle = XMLUtil.getChildValue(rendition, "templateTitle");
        			if(_templateTitle != null && _templateTitle.equalsIgnoreCase(renderData))
        			{
        				String renditionPath = (String) XMLUtil.getChildValue(rendition, "path");
%>        				
	<!-- content -->
		<%
			//out.println(renditionPath);
			// /sample/articles/arz1-list.html
			
			String originalPath = itemPath;
			RequestUtil.includeRendition(request, response, renditionPath, originalPath);
		%>
		</div>
	<!-- end of content -->
<%        				
        			}
        		}
        	}
        }
%>
<!--
<%=xml%>
-->
