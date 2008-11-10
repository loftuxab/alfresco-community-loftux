<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.icon-web-components-root { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/comptree/site.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-web-components-folder { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/folder.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-web-components-item { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/comptree/component.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}
