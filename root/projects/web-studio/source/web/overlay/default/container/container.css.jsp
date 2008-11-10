<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

/* Common styles */
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
