<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

/* CSS Document */

.alf-window-progress{
	position:absolute;
	width:400px;
	height:120px;
	left:0px;
	top: 0px;
	cursor:default;
}

.alf-window-progress #aw-icon {
	cursor:default;
	left:3px;
	top:3px;
	position:absolute;
	width:18px;
	height:18px;
	background-repeat:no-repeat;
}

.alf-window-progress #aw-icon.AWIDefaulth {
	background-image:url(<%=rootPath%>/images/AlfrescoLogo16.gif);
}

.alf-window-progress #aw-icon.AWIRollover {
	background-image:url(<%=rootPath%>/images/AlfrescoLogo16Roll.gif);
}

.alf-window-progress #aw-title-div {
	cursor:default;
	left:24px;
	top:3px;
	position:absolute;
	width:410px;
	height:16px;
	overflow:hidden;
	border:0px solid black;
}

.alf-window-progress #aw-button-close {
	cursor:default;
	left:380px;
	top:4px;
	position:absolute;
	width:16px;
	height:16px;
}

.alf-window-progress #aw-button-close.AWBDefault {
	background-image:url(<%=rootPath%>/themes/Philka/buttonCloseDefault.gif);
}

.alf-window-progress #aw-button-close.AWBRollover {
	background-image:url(<%=rootPath%>/themes/Philka/buttonCloseRoll.gif);
}

.alf-window-progress #aw-button-close.AWBPush {
	background-image:url(<%=rootPath%>/themes/Philka/buttonClosePush.gif);
}



.alf-window-progress #aw-body {
	cursor:default;
	left:4px;
	top:3px;
	position:absolute;
	width:392px;
	height:137px;
  	background-color:#C7D6E9;
	background-repeat:no-repeat;
	background-position:left top;
	overflow:visible;
}


.alf-window-progress #aw-body-content {
	cursor:default;
    width:472px;
    height:253px;
    border-top:0 none;
    overflow-x:hidden;
	overflow-y:auto;
}

.alf-window-progress .window-header {
	background: transparent url(<%=rootPath%>/themes/default/window/top-bottom.png);
	color: #15428B;
	font-family: tahoma, arial, verdana, sans-serif;
	font-size: 11px;
	font-size-adjust: none;
	font-style: normal;
	font-variant: normal;
	font-weight: bold;
	line-height: normal;
	cursor:default;
	left:6px;
	position:absolute;
	width:388px;
	height:3px;
}

.alf-window-progress .window-bottom {
	background: transparent url(<%=rootPath%>/themes/default/window/top-bottom.png) bottom repeat;
	cursor:default;
	left:4px;
	top:140px;
	position:absolute;
	width:392px;
	height:4px;
}

.alf-window-progress .window-body {
	width:392px;
	height:50px;
	background-color:#C7D6E9;

}

.alf-window-progress .window-tl {
	background: transparent url(<%=rootPath%>/themes/default/window/left-corners.png) no-repeat scroll left 0pt;
	cursor: nw-resize;
	font-size: 1px;
	left:0px;
	top:0px;
	position:absolute;
	width:6px;
	height:23px;
}

.alf-window-progress .window-tr {
	background: transparent url(<%=rootPath%>/themes/default/window/right-corners.png) no-repeat scroll right 0pt;
	cursor: ne-resize;
	font-size: 1px;
	left:394px;
	top:0px;
	position:absolute;
	width:6px;
	height:23px;
}

.alf-window-progress-ml {
	background: transparent url(<%=rootPath%>/themes/default/window/left-right.png) scroll left 0pt;
	font-size: 1px;
	left:0px;
	top:23px;
	position:absolute;
	width:4px;
	height:173px;
}

.alf-window-progress-mr {
	background: transparent url(<%=rootPath%>/themes/default/window/left-right.png) scroll right 0pt;
	font-size: 1px;
	left:496px;
	top:23px;
	position:absolute;
	width:4px;
	height:173px;
}

.alf-window-progress .header-text {
	cursor: default;
	height: 16px;
	color: #15428B;
	font-family: tahoma, arial, verdana, sans-serif;
	font-size: 11px;
	font-size-adjust: none;
	font-stretch: normal;
	font-style: normal;
	font-variant: normal;
	font-weight: bold;
	line-height: normal;
	padding: 3px 0pt 4px;
}

.alf-window-progress .window-bl {
	cursor:default;
	left:0px;
	top:140px;
	position:absolute;
	width:4px;
	height:4px;
	background:transparent url(<%=rootPath%>/themes/default/window/left-corners.png) no-repeat scroll 0pt bottom;
}

.alf-window-progress .window-br {
	background:transparent url(<%=rootPath%>/themes/default/window/right-corners.png) no-repeat scroll right bottom;
	cursor:default;
	left:396px;
	top:140px;
	position:absolute;
	width:4px;
	height:4px;
}

.alf-window-progress .alf-window-ml {
	background: transparent url(<%=rootPath%>/themes/default/window/left-right.png) scroll left 0pt;
	font-size: 1px;
	left:0px;
	top:23px;
	position:absolute;
	width:4px;
	height:120px;
}

.alf-window-progress .alf-window-mr {
	background: transparent url(<%=rootPath%>/themes/default/window/left-right.png) scroll right 0pt;
	font-size: 1px;
	left:396px;
	top:23px;
	position:absolute;
	width:4px;
	height:120px;
}

.alf-window-progress .place-for-progress-bar {
    background-image:url("<%=rootPath%>/images/progress_bar.gif");
    background-repeat:no-repeat;
    background-position:center;
}