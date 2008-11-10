<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

/* CSS Document */

.alf-region {
	position: absolute;
	width: 300px;
	height: 150px;
	left: 0px;
	top: 0px;
}

.alf-region .region-header {
	background: transparent url( <%=rootPath%>/themes/default/window/top-bottom.png );
	color: #15428B;
	font-family: tahoma, arial, verdana, sans-serif;
	font-size: 11px;
	font-size-adjust: none;
	font-style: normal;
	font-variant: normal;
	font-weight: bold;
	line-height: normal;
	left: 6px;
	position: absolute;
	width: 288px;
	height: 28px;
}

.alf-region .region-bottom {
	background: transparent url( <%=rootPath%>/themes/default/window/top-bottom.png ) bottom repeat;
	left: 4px;
	top: 146px;
	position: absolute;
	width: 292px;
	height: 4px;
}

.alf-region .region-tl {
	background: transparent url( <%=rootPath%>/themes/default/window/left-corners.png ) no-repeat scroll left 0pt;
	font-size: 1px;
	left: 0px;
	top: 0px;
	position: absolute;
	width: 6px;
	height: 28px;
}

.alf-region .region-tr {
	background: transparent url( <%=rootPath%>/themes/default/window/right-corners.png ) no-repeat scroll right 0pt;
	font-size: 1px;
	left: 294px;
	top: 0px;
	position: absolute;
	width: 6px;
	height: 28px;
}

.alf-region-ml {
	background: transparent url( <%=rootPath%>/themes/default/window/left-right.png ) scroll left 0pt;
	font-size: 1px;
	left: 0px;
	top: 28px;
	position: absolute;
	width: 4px;
	height: 118px;
}

.alf-region-mr {
	background: transparent url( <%=rootPath%>/themes/default/window/left-right.png ) scroll right 0pt;
	font-size: 1px;
	left: 296px;
	top: 28px;
	position: absolute;
	width: 4px;
	height: 118px;
}

.alf-region .region-bl {
	left: 0px;
	top: 146px;
	position: absolute;
	width: 4px;
	height: 4px;
	background: transparent url( <%=rootPath%>/themes/default/window/left-corners.png ) no-repeat scroll 0pt bottom;
}

.alf-region .region-br {
	background: transparent url( <%=rootPath%>/themes/default/window/right-corners.png ) no-repeat scroll right bottom;
	left: 296px;
	top: 146px;
	position: absolute;
	width: 4px;
	height: 4px;
}

.alf-region .region-body {
	overflow: auto;
	width: 292px;
	height: 118px;
	position: absolute;
	top: 28px;
	left: 4px;
}

.alf-region .button-divider {
	background-image: url( <%=rootPath%>/images/regionToolbar/grid-blue-split.gif );
	background-position: center;
	background-repeat: no-repeat;
	border: 0pt none;
	display: block;
	font-size: 1px;
	height: 16px;
	margin: 0pt 2px;
	overflow: hidden;
	width: 4px;
}

.alf-region .button-add {
	background-image: url( <%=rootPath%>/images/regionToolbar/add_new_component.gif );
	background-position: center;
	background-repeat: no-repeat;
	border: 0pt none;
	display: block;
	font-size: 1px;
	height: 16px;
	overflow: hidden;
	width: 16px;
}

.alf-region .button-delete {
	background-image: url( <%=rootPath%>/images/regionToolbar/delete_component.gif );
	background-position: center;
	background-repeat: no-repeat;
	border: 0pt none;
	display: block;
	font-size: 1px;
	height: 16px;
	overflow: hidden;
	width: 16px;
}

.alf-region .button-edit {
	background-image: url( <%=rootPath%>/images/regionToolbar/edit_component.gif );
	background-position: center;
	background-repeat: no-repeat;
	border: 0pt none;
	display: block;
	font-size: 1px;
	height: 16px;
	overflow: hidden;
	width: 16px;
}

.btn {
	cursor: pointer;
	width: auto;
	height: 21px;
}

.btn_left_selected {
	background: transparent url( <%=rootPath%>/themes/builder/images/default/icons/regionToolbar/btn_over_left.PNG ) no-repeat scroll;
	vertical-align: middle;
}

.btn_center_selected {
	background: transparent url( <%=rootPath%>/themes/builder/images/default/icons/regionToolbar/btn_over_center.PNG ) repeat-x scroll;
	vertical-align: middle;
}

.btn_right_selected {
	background: transparent url( <%=rootPath%>/themes/builder/images/default/icons/regionToolbar/btn_over_right.PNG ) no-repeat scroll;
	vertical-align: middle;

}

.i {
	display: block;
	font-size: 1px;
	line-height: 1px;
	overflow: hidden;
	width: 3px;
}
