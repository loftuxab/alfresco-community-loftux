<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.AlfrescoContentView #ACVMenu .AMHorizontalHolder 
{
	width:100%;
	height:25px;
	cursor:default !important;
	border-top:1px solid #F0F5FA;
	border-bottom:1px solid #A9BFD3;
	background-image:url(<%=rootPath%>/images/menubg.gif);
}

.AlfrescoContentView #ACVMenu .AMRoot
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
	overflow:visible;
	margin-top:1px;
	margin-left:3px;
}

.AlfrescoContentView #ACVMenu .AMRoot.RolloverItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);	
}

.AlfrescoContentView #ACVMenu .AMRoot.RolloverItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

.AlfrescoContentView #ACVMenu .AMRoot.RolloverItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);	
}

.AlfrescoContentView #ACVMenu .AMRoot.PushItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);	
}

.AlfrescoContentView #ACVMenu .AMRoot.PushItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

.AlfrescoContentView #ACVMenu .AMRoot.PushItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);	
}

.AlfrescoContentView
{
	height: 100%;
	margin: 0;
	padding: 0;	
}

.AlfrescoContentView .scrollBody 
{
    margin: 0 auto;
    position: relative;
	overflow: hidden;
}

.AlfrescoContentView .scrollContainer 
{
	position: relative;
}

.AlfrescoContentView .scrollContainer div.panel 
{
    width: 100%;
    height: 74px;
    margin: 0px;
    padding: 0px; 
}

.AlfrescoContentView .scrollContainer div.panel-over
{
    width: 100%;
    background-color: #e9f2fd;
    height: 74px;
    margin: 0px;
    padding: 0px; 
}

.AlfrescoContentView .scrollContainer div.panel-selected
{
    width: 100%;
    background-color: #d9e8fb;
    height: 74px;
    margin: 0px;
    padding: 0px; 
}

.AlfrescoContentView .inside {
	border: 1px solid #CCC;
	margin: 0px;
	padding: 0px;
	width: 100%;
	height: 73px;
}

.AlfrescoContentView .insideImage {
	display: block;
	margin-right: 5px;
	width: 64px;
}

.AlfrescoContentView .insideHeader {
	font-weight: normal;
	font-size: 12px;
	color: #333;
	margin: 5px;	
}

.AlfrescoContentView .insideParagraph {
	font-size: 10px;
	margin-top: 4px;
	margin-left: 2px;
	color: #777;
}

.AlfrescoContentView .top-shadow {
	position: absolute;
	top: 1;
	left: 1;
	height: 12px;
	background: url(<%=rootPath%>/images/shadows/topshadow.png) repeat-x;
	float: left;
	width: 100%;
	display: none;
}

.AlfrescoContentView .bottom-shadow {
	position: absolute;
	top: 68;
	left: 1;
	height: 12px;
	background: url(<%=rootPath%>/images/shadows/bottomshadow.png) repeat-x;
	float: left;
	width: 100%;
	display: none;
}

.AlfrescoContentView .hide 
{
    display: none;
}