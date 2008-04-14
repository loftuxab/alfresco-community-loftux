<%@ page import="org.alfresco.website.ads.*" %>
<%@ page import="org.alfresco.website.ads.types.*" %>
<%@ page import="org.alfresco.website.ads.config.*" %>
<%@ page import="org.json.simple.*" %>
<%@ taglib uri="/WEB-INF/tlds/ads.tld" prefix="ads" %>
<%@ page buffer="0kb" contentType="text/html;charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%
	// get the request context
	RequestContext context = RequestUtil.getRequestContext(request);
	
	// get the configuration
	Configuration configuration = (Configuration) request.getAttribute("layout-configuration");
	
	// get the layout json
	String layoutJson = (String) configuration.get("panelConfig-json");	
	if(layoutJson == null || "".equals(layoutJson))
	{
		JSONObject json = new JSONObject();
		json.put("layout", "table");
		
		JSONObject layoutConfig = new JSONObject();
		layoutConfig.put("columns", new Integer(2));
		json.put("layoutConfig", layoutConfig);
		
		// left region
		JSONObject leftRegion = new JSONObject();
		leftRegion.put("regionId", "leftRegion");
		leftRegion.put("regionScopeId", "page");
		leftRegion.put("width", 200);
		leftRegion.put("height", 300);
		
		// right region
		JSONObject rightRegion = new JSONObject();
		rightRegion.put("regionId", "rightRegion");
		rightRegion.put("regionScopeId", "page");
		rightRegion.put("width", 200);
		rightRegion.put("height", 300);		
				
		JSONArray items = new JSONArray();
		items.add(leftRegion);
		items.add(rightRegion);
		
		json.put("items", items);
		
		layoutJson = json.toString();
	}
	
	// build the JSON object
	JSONObject json = (JSONObject) JSONValue.parse(layoutJson);
		
	String layout = (String) json.get("layout");
	JSONObject layoutConfig = (JSONObject) json.get("layoutConfig");
	JSONArray items = (JSONArray) json.get("items");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title><ads:pageTitle/></title>
    <ads:imports/>
    
    <link rel="stylesheet" type="text/css" href="/build/misc/portal.css"></link>
    <script type="text/javascript" src="/build/misc/Portal.js"></script>
    <script type="text/javascript" src="/build/misc/PortalColumn.js"></script>
    <script type="text/javascript" src="/build/misc/Portlet.js"></script>

</head>
<body>

<%
	if("table".equalsIgnoreCase(layout))
	{
%>
	<table>
		<tr>
<%	
		int columns = ((Number) layoutConfig.get("columns")).intValue();
		for(int i = 0; i < columns; i++)
		{
%>
			<td>
<%
				JSONObject region = (JSONObject) items.get(i);
				int width = ((Number) region.get("width")).intValue();
				int height = ((Number) region.get("height")).intValue();
				String regionId = (String) region.get("regionId");
				String regionScopeId = (String) region.get("regionScopeId");
				
				RenderUtil.renderRegion(context, request, response, regionId, regionScopeId);
%>
			</td>
<%
		}
%>
		</tr>
	</table>
<%
	}
%>


<script language="Javascript">

    var dynamicJson = <%=layoutJson%>;

    // create some portlet tools using built in Ext tool ids
    var tools = [{
        id:'gear',
        handler: function(){
            Ext.Msg.alert('Message', 'The Settings tool was clicked.');
        }
    },{
        id:'close',
        handler: function(e, target, panel){
            panel.ownerCt.remove(panel, true);
        }
    }];
    
    var dynamic_viewport = new Ext.Viewport({
    	id: 'dynamic_viewport',
        layout:'border',
        border: false,
        items:[{
            xtype:'portal',
            region:'center',
            margins:'1 1 1 1',
            items:[{
                columnWidth:.50,
                style:'padding:5px 0 5px 5px',
                items:[{
                    title: 'rightRegion',
                    layout:'fit',
                    tools: tools,
                    contentEl: 'rightRegion'
                }]
            },{
                columnWidth:.50,
                style:'padding:5px',
                items:[{
                    title: 'leftRegion',
                    tools: tools,
                    contentEl: 'leftRegion'
                }]
            }]
        }]
    });
    
    Ext.ComponentMgr.register(dynamic_viewport);

</script>

<ads:floatingmenu/>
</body>
</html>

