<%@ page import="org.alfresco.web.site.remote.*" %>
<%@ page import="org.alfresco.tools.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="javax.xml.parsers.DocumentBuilder" %>
<%@ page import="javax.xml.parsers.DocumentBuilderFactory" %>
<%@ page import="org.w3c.dom.*" %>
<%@ page import="org.alfresco.web.site.*" %>
<%@ page import="org.alfresco.web.site.model.*" %>
<%@ page import="org.alfresco.web.site.config.*" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	RuntimeConfig configuration = (RuntimeConfig) request.getAttribute("component-configuration");	

	// settings	
	String queryString = (String) configuration.get("queryString");
	String queryType = (String) configuration.get("queryType");
	String howToRender = (String) configuration.get("howToRender");
	String renderData = (String) configuration.get("renderData");
	String endpointId = (String) configuration.get("endpointId");
	

	// find this endpoint
	Endpoint endpoint = context.getManager().getEndpoint(context, endpointId);
	if(endpoint == null)
	{
		out.println("No endpoint specified");
		return;
	}
	
	if(queryString == null)
	{
		String currentThemeId = ThemeUtil.getCurrentThemeId(context);
		String unconfiguredImageUrl = RenderUtil.toBrowserUrl("/ui/themes/builder/images/" + currentThemeId + "/icons/unconfigured_component_large.gif");
		String renderString = "<img src='" + unconfiguredImageUrl + "' border='0' alt='Unconfigured Query Component'/>";	
		out.println(renderString);
		return;
	}
	
	
	// build the web script connector
	//String webscriptUri = "/ads/search/lucene/ads--admin";
	String webscriptUri = "/ads/search/lucene/" + context.getStoreId();
	WebscriptConnector connector = RemoteFactory.newWebscriptConnector(context, endpoint, webscriptUri);
		
	// fetch xml
	HashMap params = new HashMap();
	params.put("q", queryString);
	params.put("format", "xml");
	String xml = connector.connect(params, null);
	
	// parse the xml
	Document xmlDocument = XMLUtil.parse(xml);

        if("templateTitle".equals(howToRender))
        {
        	// for each item, grab the template rendition with this name
        	// and include it
        	
        	List itemList = XMLUtil.getChildren(xmlDocument.getDocumentElement(), "item");
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
