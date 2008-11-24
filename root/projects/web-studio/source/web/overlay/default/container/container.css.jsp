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
	font-style:normal;font-weight:normal;
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
div#AlfrescoApplication.Default #AAStartEditControl {
	position:absolute;
	border:1px solid #808080;
	width:100px;
	background-color:#FFFFFF;
}

div#AlfrescoApplication.Default #AAStartEditControl .AAContentManagedIcon {
	width:88px;
	height:32px;
	background-image:url(<%=rootPath%>/images/AlfrescoContentManaged.gif);
	background-repeat:no-repeat;
	cursor: pointer;
    position:relative;
    top:0px;
 }

div#AlfrescoApplication.Default #AAStartEditControl .AAContentEditIcon {
	width:16px;
	height:16px;
	background-image:url(<%=rootPath%>/images/AlfrescoContentEdit.gif);
	background-repeat:no-repeat;
	float:left;
	cursor:pointer;
}

div#AlfrescoApplication.Default #AAStartEditControl .AAContentEditCaption {
	font-family:Tahoma,Arial,Helvetica,sans-serif;
	font-size:12px;
	padding-left:5px;
	cursor:pointer;
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
	cursor:pointer;
	float:left;
	font-size:11px;
	font-family:tahoma,verdana,helvetica;
	color:black;
	height:16px;
	text-align:center;
	background-image:url(<%=rootPath%>/images/menubg.gif);
	margin-top:1px;
	margin-left:3px;
}

.MountSelectorLeft
{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);
	padding: 0px;
	margin: 0px;	
	float:left
}

.MountSelectorCenter
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	color: #000000;
	padding: 0px;
	margin: 0px;
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);
	float:left;	
}

.MountSelectorCenterSelected
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-weight: bold;	
	color: #000000;
	padding: 0px;
	margin: 0px;
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);
	float:left;
}

.MountSelectorRight
{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);	
	padding: 0px;
	margin: 0px;
	float: left;
}

.MountSelectorCenter DIV:hover { 
	cursor: pointer;
}
