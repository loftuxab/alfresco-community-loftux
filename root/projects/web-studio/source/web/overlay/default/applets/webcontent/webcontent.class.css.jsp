<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.icon-web-content-root { 
	display:block;
	height:16px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/contenttree/webapproot.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-web-content-folder { 
	display:block;
	height:16px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/contenttree/folder.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-web-content-file { 
	display:block;
	height:16px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/contenttree/file.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}
