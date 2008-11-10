<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

table#AlfrescoTwoPanelsResizer {
	
}

table#AlfrescoTwoPanelsResizer #ATPLeft {
	border:0;
	overflow:hidden;
	font-size:1px;
}

table#AlfrescoTwoPanelsResizer #ATPLeftDiv {
	border:1px solid #99bbe8;
	background-color:#FFFFFF;
	overflow:hidden;
	overflow-x:hidden;
	overflow-y:hidden;
}

table#AlfrescoTwoPanelsResizer #ATPResizer {
	width:5px;
	background:#dfe8f6;
	border:0px solid black;
	font-size:1px;
}

table#AlfrescoTwoPanelsResizer #ATPRight {
	border: 0;
	overflow:hidden;
	padding: 0;
}

table#AlfrescoTwoPanelsResizer #ATPRightDiv {
	border: 1px solid #99bbe8;
	overflow:hidden;
	background:url(<%=rootPath%>/images/AlfrescoFadedBG.png) no-repeat;
	background-color:#FFFFFF;
}

.ATPHeader {
	height:20px;
	background-image:url(<%=rootPath%>/images/ApplicationPanelsHeaderBg.gif);
	border-top:1px solid #FFFFFF;
	color:#15428B;
	font-family:tahoma,arial,verdana,sans-serif;
	font-size:11px;
	font-size-adjust:none;
	font-style:normal;
	font-variant:normal;
	font-weight:bold;
	line-height:15px;
	padding-left:10px;
	padding-top:3px;
}

table#AlfrescoTwoPanelsResizer .PanelBg {
	background:#ece9d8;
}
/*----------ResizerMenu-------------*/

div#AlfrescoPanelResizerMenu.AMVerticalHolder {
	position:absolute;
	width:100px;
	left:0px;
	top:0px;
	cursor:default;
	border:1px solid #aca899;
	background-color:#ffffff;
	overflow:visible;
	padding:2px;
}

div#AlfrescoPanelResizerMenu .AMRoot {
	cursor:default;
	font-size:12px;
	font-family:Arial, Helvetica, sans-serif;
	color:#000000;
	height:16px;
	text-align:center;
	background-color:#ffffff;
	overflow:visible;
}
div#AlfrescoPanelResizerMenu .AMRoot.Selected {
	background-color:#edeae4;
	color:#000000;
}

div#AlfrescoPanelResizerMenu .AMRoot.RolloverItemRoot {
	background-color:#316ac5;
	color:#FFFFFF;
}

div#AlfrescoPanelResizerMenu .AMRoot.PushItemRoot {
	background-color:#CC3300;
	color:#FFFFFF;
}