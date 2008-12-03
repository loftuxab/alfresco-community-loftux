<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

/*
Copyright (c) 2008, Yahoo! Inc. All rights reserved.
Code licensed under the BSD License:
http://developer.yahoo.net/yui/license.txt
version: 2.5.1
*/

.AlfrescoTreeView .AMHorizontalHolder {
	width:100%;
	height:25px;
	cursor:default !important;
	border-top:1px solid #F0F5FA;
	border-bottom:1px solid #A9BFD3;
	background-image:url(<%=rootPath%>/images/menubg.gif);
}

.AlfrescoTreeView .AMRoot {
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
	margin-left:1px;
}

.AlfrescoTreeView .AMRoot.RolloverItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);	
}

.AlfrescoTreeView .AMRoot.RolloverItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

.AlfrescoTreeView .AMRoot.RolloverItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);	
}

.AlfrescoTreeView .AMRoot.PushItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);	
}

.AlfrescoTreeView .AMRoot.PushItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

.AlfrescoTreeView .AMRoot.PushItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);
}

.AlfrescoTreeView-tip {
	color: #fff;
	z-index: 13000000;
}
 
.AlfrescoTreeView-title {
	font-weight: bold;
	font-size: 11px;
	margin: 0;
	color: #333333;
	padding: 6px;
	border:1px solid #0099FF;
	background: url(<%=rootPath%>/images/ApplicationPanelsHeaderBg.gif);
}

.AlfrescoTreeView-text {
	font-weight: bold;
	font-size: 12px;
	margin: 0;
	color: #003399;
	padding: 3px;
	border:1px solid #0099FF;
	background: url(<%=rootPath%>/images/ApplicationPanelsHeaderBg.gif);
}

.AlfrescoTreeView .ygtvitem table {
	width: 10000px;
}

.AlfrescoTreeView .ygtvitem table.Active {
	background-color:#d9e8fb;
}

.AlfrescoTreeView .ygtvitem table.Selected {
	background-color:#eeeeee;
}

/* first or middle sibling, no children */
.ygtvtn {
	width:18px; height:22px; 
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -5600px no-repeat;
}

/* first or middle sibling, collapsable */
.ygtvtm {
	width:18px; height:22px; 
	cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -4000px no-repeat;
}

/* first or middle sibling, collapsable, hover */
.ygtvtmh {
	width:18px; height:22px; 
	cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -4800px no-repeat;
}

/* first or middle sibling, expandable */
.ygtvtp {
	width:18px; height:22px; 
	cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -6400px no-repeat;
}

/* first or middle sibling, expandable, hover */
.ygtvtph {
	width:18px; height:22px; 
	cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -7200px no-repeat;
}

/* last sibling, no children */
.ygtvln {
	width:18px; height:22px; 
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -1600px no-repeat;
}

/* Last sibling, collapsable */
.ygtvlm {
	width:18px; height:22px; 
	cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 0px no-repeat;
}

/* Last sibling, collapsable, hover */
.ygtvlmh {
	width:18px; height:22px; 
	cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -800px no-repeat;
}

/* Last sibling, expandable */
.ygtvlp { 
	width:18px; height:22px; 
	cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -2400px no-repeat;
}

/* Last sibling, expandable, hover */
.ygtvlph { 
	width:18px; height:22px; cursor:pointer ;
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -3200px no-repeat;
}

/* Loading icon */
.ygtvloading { 
	width:18px; height:22px; 
	background: url(<%=rootPath%>/images/treeview-loading.gif) 0 0 no-repeat; 
}

/* the style for the empty cells that are used for rendering the depth 
 * of the node */
.ygtvdepthcell { 
	width:18px; height:22px; 
	background: url(<%=rootPath%>/images/treeview-sprite.gif) 0 -8000px no-repeat;
}

.ygtvblankdepthcell { width:18px; height:22px; }

/* the style of the div around each node */
.ygtvitem { }  

/* the style of the div around each node's collection of children */
.ygtvchildren {  
    *zoom: 1;
}

/* the style of the text label in ygTextNode */
.ygtvlabel, .ygtvlabel:link, .ygtvlabel:visited, .ygtvlabel:hover { 
	margin-left:2px;
	text-decoration: none;
    background-color: white;
}

.ygtvspacer { height: 22px; width: 12px; }

.DragClone {
	color:black;
	font-size:11px;
	height:16px;
	padding-left:25px;
	padding-top:1px;
	text-decoration:none;
	font-family:arial,tahoma,helvetica,sans-serif;
	background-color:#f6f6f6;
	border-right:1px solid #bdbdbd;
	border-bottom:1px solid #bdbdbd;
	border-left:1px solid #e2e2e2;
	border-top:1px solid #e2e2e2;
	white-space:nowrap;
}
