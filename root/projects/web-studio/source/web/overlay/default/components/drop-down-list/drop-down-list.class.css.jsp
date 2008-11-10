<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.alf-drop-down-tempalte{
	position:absolute;
	height:21px;
	top:0px;
	right:0px;
	width:170px;
	border-top:1px solid #F0F5FA;
	border-bottom:1px solid #A9BFD3;
	z-index:800;
}

.alf-drop-down-root-itm{
	font-family:tahoma,arial,sans-serif;
	font-size:11px;
	position:relative;
	height:19px;
	background-color:#fff;
	border:1px solid #A9BFD3;
}
.alf-drop-down-root-itm-caption{
	font-family:tahoma,arial,sans-serif;
	font-size:11px;
	position:relative;
	white-space: nowrap;
	top:3px;
	left:3px;
	cursor:pointer;
}
.alf-drop-down-itm{
    border:1px solid #F0F0F0;
	cursor:pointer;
	height:20px;
}
.alf-drop-down-itm.selected-i{
    background-image:url(<%=rootPath%>/images/subMenuselect.gif);
	border:1px solid #AACCF6;
}
.alf-drop-down-itm-caption{
	font-family:tahoma,arial,sans-serif;
	font-size:11px;
	white-space:nowrap;
}
.alf-drop-down-itm-holder{
	position:absolute;
    cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
	width:150px;
	z-index:99000;
}
.alf-drop-down-root-itm-trigger-img{
	background-image:url(<%=rootPath%>/images/triggerDefault.gif);
	cursor:pointer;
}

.selected .alf-drop-down-root-itm-trigger-img{
	background-image:url(<%=rootPath%>/images/triggerRollover.gif);
	cursor:pointer;
}