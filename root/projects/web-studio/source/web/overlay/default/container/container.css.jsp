<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

/* Common styles */
/*
html,body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,p,blockquote,th,td
{
	margin:0;
	padding:0;
}
img,body,html
{
	border:0;
}
address,caption,cite,code,dfn,em,strong,th,var
{
	font-style:normal;
	font-weight:normal;
}
ol,ul{
	list-style:none;
}
caption,th{
	text-align:left;
}
h1,h2,h3,h4,h5,h6{
	font-size:100%;
}
q:before,q:after
{
	content:'';
}
*/


div#AlfrescoApplication.Default {
	overflow:hidden;
	top:0px;
	width:100%;
}

.MountSelector
{
	height:25px;
	cursor:default !important;
	border-top:1px solid #F0F5FA;
	border-bottom:1px solid #A9BFD3;
	background-image:url(<%=rootPath%>/images/menubg.gif);
}

.MountSelectorRoot
{
	padding-top:1px;
	float:left;
	font-size:11px;
	font-family:tahoma,verdana,helvetica;
	color:black;
	background-image:url(<%=rootPath%>/images/menubg.gif);
	margin-top:1px;
	margin-left:3px;
	cursor:pointer;
}

.MountSelectorTable
{ 
}

.MountSelectorLeft
{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);
	padding: 0px;
	margin: 0px;
	border: 0px;
}

.MountSelectorCenter
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	color: #000000;
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);
	padding: 0px;
	margin: 0px;
	border: 0px;	
}

.MountSelectorCenterSelected
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-weight: bold;	
	color: #000000;
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);
	padding: 0px;
	margin: 0px;
	border: 0px;
}

.MountSelectorRight
{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);	
	padding: 0px;
	margin: 0px;	
}

.MountSelectorCenter DIV:hover { 
	cursor: pointer;
}

#FloatingMenuControl
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-style:normal;
	position:absolute;
	border-left:1px solid #000000;
	border-top:1px solid #000000;
	border-right:1px solid #000000;
	border-bottom:1px solid #000000;
	height:48px;
	background-image:url(<%=rootPath%>/images/floatingmenu/background.png);
}

.FloatingMenuTitle
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-style:normal;
}
.FloatingMenuTitle DIV:hover { 
	text-decoration: underline;
}

.FloatingMenuSandboxId
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-style:normal;
}

.FloatingMenuOptions
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-style:normal;
	background-color: transparent;
}

#FloatingMenuIcon
{
    position: absolute;
    top: 0px;
    left: 0px;
    display: none;
    width: 64px;
    height: 64px;
}

#FloatingMenuSelector
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-style:normal;
	position:absolute;
	display: none;
	width: 300px;
	z-index: 999999;
}