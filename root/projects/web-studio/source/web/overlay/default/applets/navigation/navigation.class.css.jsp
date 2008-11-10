<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.icon-navigation-root 
{ 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/navtree/rootnode.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-navigation-node 
{ 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/navtree/node.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}
