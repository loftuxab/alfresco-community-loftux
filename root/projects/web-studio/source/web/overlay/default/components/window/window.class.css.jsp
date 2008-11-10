<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

/* CSS Document */

.alf-window{
	position:absolute;
	width:500px;
	height:300px;
	left:300px;
	top: 200px;
	cursor:default;
}

.alf-window #AWIcon {
	cursor:default;
	left:3px;
	top:3px;
	position:absolute;
	width:18px;
	height:18px;
	background-repeat:no-repeat;
}

.alf-window #AWIcon.AWIDefaulth {
	background-image:url(<%=rootPath%>/images/AlfrescoLogo16.gif);
}

.alf-window #AWIcon.AWIRollover {
	background-image:url(<%=rootPath%>/images/AlfrescoLogo16Roll.gif);
}

.alf-window #aw-title-div {
	cursor:default;
	left:24px;
	top:3px;
	position:absolute;
	width:410px;
	height:16px;
	overflow:hidden;
	border:0px solid black;
}

.alf-window #aw-button-close {
	cursor:default;
	left:480px;
	top:4px;
	position:absolute;
	width:16px;
	height:16px;
}

.AWBDefault_cl {
	background-image:url(<%=rootPath%>/themes/Philka/buttonCloseDefault.gif);
}

.AWBRollover_cl {
	background-image:url(<%=rootPath%>/themes/Philka/buttonCloseRoll.gif);
}

.AWBPush_cl {
	background-image:url(<%=rootPath%>/themes/Philka/buttonClosePush.gif);
}

.alf-window #aw-button-minimize {
	cursor:default;
	left:440px;
	top:4px;
	position:absolute;
	width:16px;
	height:16px;
}

.AWBDefault_min {
	background-image:url(<%=rootPath%>/themes/Philka/buttonMinimizeDefault.gif);
}

.AWBRollover_min {
	background-image:url(<%=rootPath%>/themes/Philka/buttonMinimizeRoll.gif);
}

.AWBPush_min {
	background-image:url(<%=rootPath%>/themes/Philka/buttonMinimizePush.gif);
}


.alf-window #aw-button-maximize {
	cursor:default;
	left:460px;
	top:4px;
	position:absolute;
	width:16px;
	height:16px;
}

.AWBDefault_max {
	background-image:url(<%=rootPath%>/themes/Philka/buttonMaximizeDefault.gif);
}

.AWBRollover_max {
	background-image:url(<%=rootPath%>/themes/Philka/buttonMaximizeRoll.gif);
}

.AWBPush_max {
	background-image:url(<%=rootPath%>/themes/Philka/buttonMaximizePush.gif);
}

.alf-window #aw-body {
	cursor:default;
	left:4px;
	top:23px;
	position:absolute;
	width:492px;
	height:273px;
  	background-color:#DFE8F6;
	background-repeat:no-repeat;
	background-position:left top;
	overflow:hidden;
}


.alf-window #aw-body-content {
	cursor:default;
	background-color:#C7D6E9;
    width:472px;
    height:253px;
    border-top:0 none;
    overflow-x:hidden;
	overflow-y:auto;
}

.alf-window #aw-body #AWGeneralMenu {
	font-size:1px;
	height:16px;
}

.alf-window .window-header {
	background: transparent url( <%=rootPath%>/themes/default/window/top-bottom.png);
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
	width:488px;
	height:23px;
}

.alf-window .window-bottom {
	background: transparent url( <%=rootPath%>/themes/default/window/top-bottom.png) bottom repeat;
	cursor:default;
	left:4px;
	top:296px;
	position:absolute;
	width:492px;
	height:4px;
}

.alf-window .window-body {
	width:492px;
	/*height:273px;*/
	background-color:#C7D6E9;

}

.alf-window .window-tl {
	background: transparent url( <%=rootPath%>/themes/default/window/left-corners.png) no-repeat scroll left 0pt;
	cursor: nw-resize;
	font-size: 1px;
	left:0px;
	top:0px;
	position:absolute;
	width:6px;
	height:23px;
}

.alf-window .window-tr {
	background: transparent url( <%=rootPath%>/themes/default/window/right-corners.png) no-repeat scroll right 0pt;
	cursor: ne-resize;
	font-size: 1px;
	left:494px;
	top:0px;
	position:absolute;
	width:6px;
	height:23px;
}

.alf-window-ml {
	background: transparent url(<%=rootPath%>/themes/default/window/left-right.png) scroll left 0pt;
	font-size: 1px;
	left:0px;
	top:23px;
	position:absolute;
	width:4px;
	height:273px;
}

.alf-window-mr {
	background: transparent url(<%=rootPath%>/themes/default/window/left-right.png) scroll right 0pt;
	font-size: 1px;
	left:496px;
	top:23px;
	position:absolute;
	width:4px;
	height:273px;
}

.alf-window .header-text {
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

.alf-window .window-bl {
	cursor:default;
	left:0px;
	top:296px;
	position:absolute;
	width:4px;
	height:4px;
	background:transparent url( <%=rootPath%>/themes/default/window/left-corners.png) no-repeat scroll 0pt bottom;
}

.alf-window .window-br {
	background:transparent url(<%=rootPath%>/themes/default/window/right-corners.png) no-repeat scroll right bottom;
	cursor:default;
	left:496px;
	top:296px;
	position:absolute;
	width:4px;
	height:4px;
}









