<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.icon-sites-root { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/sitestree/site.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-sites-folder { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/sitestree/folder.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-sites-item { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/sitestree/file.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}
