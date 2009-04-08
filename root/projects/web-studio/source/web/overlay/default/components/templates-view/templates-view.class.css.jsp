<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.AlfrescoTemplatesView #ALVMenu .AMHorizontalHolder {
	width:100%;
	height:25px;
	cursor:default !important;
	border-top:1px solid #F0F5FA;
	border-bottom:1px solid #A9BFD3;
	background-image:url(<%=rootPath%>/images/menubg.gif);
}

.AlfrescoTemplatesView #ALVMenu .AMRoot {
	padding-top:1px;
	cursor:pointer;
	float:left;
	font-size:11px;
	font-family:tahoma,verdana,helvetica;
	color:black;
	height:16px;
	text-align:center;
	background-image:url(<%=rootPath%>/images/menubg.gif);
	overflow:visible;
	margin-top:1px;
	margin-left:3px;

}

.AlfrescoTemplatesView #ALVMenu .AMRoot.RolloverItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);	
}

.AlfrescoTemplatesView #ALVMenu .AMRoot.RolloverItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

.AlfrescoTemplatesView #ALVMenu .AMRoot.RolloverItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);	
}

.AlfrescoTemplatesView #ALVMenu .AMRoot.PushItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);	
}

.AlfrescoTemplatesView #ALVMenu .AMRoot.PushItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

.AlfrescoTemplatesView #ALVMenu .AMRoot.PushItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);	
}

.AlfrescoTemplatesView
{
	height: 100%;
}

.TemplateRow
{
}

.TemplateRowCell
{
	cursor: pointer;
	
	font-family:tahoma,arial,helvetica,sans-serif;
	font-size:11px;
	font-size-adjust:none;
	font-style:normal;
	font-variant:normal;
	font-weight:normal;
	
	border-bottom: 1px solid #CCC; 
}

.TemplateRowOver
{
	background-color: #e9f2fd;
}

.TemplateRowSelected
{
	background-color: #d9e8fb;
}

