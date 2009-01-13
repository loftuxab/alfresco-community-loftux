<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.icon-spaces-root { 
	display:block;
	height:16px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/spacestree/companyhome.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-spaces-folder { 
	display:block;
	height:16px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/spacestree/folder.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-spaces-item { 
	display:block;
	height:16px;
	padding-left:20px;
	background-image:url(<%=rootPath%>/images/trees/spacestree/file.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}
