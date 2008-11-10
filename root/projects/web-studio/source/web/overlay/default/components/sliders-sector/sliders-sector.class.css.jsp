<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

div#AlfrescoSlidersSector {
	border:1px solid #99bbe8;
}

.ASSSliderHeader{
	border-top:1px solid #FFFFFF;
	border-bottom:1px solid #99bbe8;
	background-color:#d9e7f8;
	font-family:tahoma,arial,verdana,sans-serif;
	font-size:11px;
	font-size-adjust:none;
	font-style:normal;
	font-variant:normal;
	line-height:15px;
	padding:4px;
	cursor:default;
}

.ASSToggleImage {
	background-image:url(<%=rootPath%>/images/ApplicationSlidersSectorToggle.gif);
	background-position:right;
	background-repeat:no-repeat;
	float:right;
	width:15px;
	height:15px;
	cursor:pointer;
	font-size:1px;
}

.ASSToggleImage.Selected1 {
	background-image:url(<%=rootPath%>/images/ApplicationSlidersSectorToggleRoll.gif);
	background-position:right;
	background-repeat:no-repeat;
	float:right;
	width:15px;
	height:15px;
	font-size:1px;
}

.ASSToggleImage.Show {
	background-image:url(<%=rootPath%>/images/ApplicationSlidersSectorToggleShow.gif);
	background-position:right;
	background-repeat:no-repeat;
	float:right;
	width:15px;
	height:15px;
	font-size:1px;
}

.ASSToggleImage.Show.Selected1 {
	background-image:url(<%=rootPath%>/images/ApplicationSlidersSectorToggleShowRoll.gif);
	background-position:right;
	background-repeat:no-repeat;
	float:right;
	width:15px;
	height:15px;
	font-size:1px;
}

.ASSSliderData {
	overflow:hidden;
	background:#FFFFFF;
	font-family:arial,tahoma,helvetica,sans-serif;
	font-size:11px;
	color:black;
	text-decoration:none;
}

.ASSSliderData a {
	text-decoration:none;
	color:black;
	font-size:11px;
	padding-top:1px;
	padding-left:25px;
	height:16px;
}

.ASSSliderDropper {
	height: 1px;
	border-top:1px dashed #0066FF;
	font-size:1px;
	background-color: #0099FF;
	overflow:hidden;
	font-size:1px;
	display:none;
}