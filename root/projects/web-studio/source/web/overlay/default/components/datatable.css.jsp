<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String proxyPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio");
%>

/*
Copyright (c) 2008, Yahoo! Inc. All rights reserved.
Code licensed under the BSD License:
http://developer.yahoo.net/yui/license.txt
version: 2.5.2
*/
/*
.yui-dt{border-bottom:1px solid transparent;}
.yui-dt-noop{border-bottom:none;}
.yui-dt-hd{display:none;}
.yui-dt-scrollable .yui-dt-hd{display:block;}
.yui-dt-scrollable .yui-dt-bd thead tr,.yui-dt-scrollable .yui-dt-bd thead th{position:absolute;left:-1500px;}
.yui-dt-scrollable tbody{-moz-outline:none;}
.yui-dt-draggable{cursor:move;}
.yui-dt-coltarget{position:absolute;z-index:999;}
.yui-dt-hd{zoom:1;}th.yui-dt-resizeable .yui-dt-liner{position:relative;}
.yui-dt-resizer{position:absolute;right:0;bottom:0;height:100%;cursor:e-resize;cursor:col-resize;background:url(transparent.gif);}
.yui-dt-resizerproxy{visibility:hidden;position:absolute;z-index:9000;background:url(transparent.gif);}
th.yui-dt-hidden .yui-dt-liner,td.yui-dt-hidden .yui-dt-liner{margin:0;padding:0;overflow:hidden;white-space:nowrap;}
.yui-dt-scrollable .yui-dt-bd{overflow:auto;}
.yui-dt-scrollable .yui-dt-hd{overflow:hidden;position:relative;}
.yui-dt-editor{position:absolute;z-index:9000;}
.yui-dt table{margin:0;padding:0;font-family:arial;font-size:inherit;border-collapse:separate;*border-collapse:collapse;border-spacing:0;}
.yui-dt thead{border-spacing:0;}
.yui-dt caption{padding-bottom:1em;text-align:left;}
.yui-dt-hd table{border-left:1px solid #7F7F7F;border-top:1px solid #7F7F7F;border-right:1px solid #7F7F7F;}
.yui-dt-bd table{border:1px solid #7F7F7F;}
.yui-dt th{background:#D8D8DA url(<%=proxyPath%>/yui/assets/skins/sam/sprite.png) repeat-x 0 0;}
.yui-dt th,.yui-dt th a{font-weight:normal;text-decoration:none;color:#000;vertical-align:bottom;}
.yui-dt th{margin:0;padding:0;border:none;border-right:1px solid #CBCBCB;}
.yui-dt th .yui-dt-liner{white-space:nowrap;}
.yui-dt-liner{margin:0;padding:0;padding:4px 10px 4px 10px;}
.yui-dt-coltarget{width:5px;background-color:red;}
.yui-dt td{margin:0;padding:0;border:none;border-right:1px solid #CBCBCB;text-align:left;}
.yui-dt-list td{border-right:none;}
.yui-dt-resizer{width:6px;}
tbody.yui-dt-msg td{border:none;}
.yui-dt-loading{background-color:#FFF;}
.yui-dt-empty{background-color:#FFF;}
.yui-dt-error{background-color:#FFF;}
.yui-dt-scrollable .yui-dt-hd table{border:0px;}
.yui-dt-scrollable .yui-dt-bd table{border:0px;}
.yui-dt-scrollable .yui-dt-hd{border-left:1px solid #7F7F7F;border-top:1px solid #7F7F7F;border-right:1px solid #7F7F7F;}
.yui-dt-scrollable .yui-dt-bd{border-left:1px solid #7F7F7F;border-bottom:1px solid #7F7F7F;border-right:1px solid #7F7F7F;background-color:#FFF;}
thead .yui-dt-sortable{cursor:pointer;}
th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc{background:url(<%=proxyPath%>/yui/assets/skins/sam/sprite.png) repeat-x 0 -100px;}  th.yui-dt-sortable .yui-dt-label{margin-right:10px;}  th.yui-dt-asc .yui-dt-liner{background:url(<%=rootPath%>/images/dt-arrow-up.png) no-repeat right;}  th.yui-dt-desc .yui-dt-liner{background:url(<%=rootPath%>images/dt-arrow-dn.png) no-repeat right;}.yui-dt-editable{cursor:pointer;}.yui-dt-editor{text-align:left;background-color:#F2F2F2;border:1px solid #808080;padding:6px;}.yui-dt-editor label{padding-left:4px;padding-right:6px;}.yui-dt-editor .yui-dt-button{padding-top:6px;text-align:right;}.yui-dt-editor .yui-dt-button button{background:url(<%=proxyPath%>/yui/assets/skins/sam/sprite.png) repeat-x 0 0;border:1px solid #999;width:4em;height:1.8em;margin-left:6px;}.yui-dt-editor .yui-dt-button button.yui-dt-default{background:url(<%=proxyPath%>/yui/assets/skins/sam/sprite.png) repeat-x 0 -1400px;background-color:#5584E0;border:1px solid #304369;color:#FFF}.yui-dt-editor .yui-dt-button button:hover{background:url(<%=proxyPath%>/yui/assets/skins/sam/sprite.png) repeat-x 0 -1300px;color:#000;}.yui-dt-editor .yui-dt-button button:active{background:url(<%=proxyPath%>/yui/assets/skins/sam/sprite.png) repeat-x 0 -1700px;color:#000;}  tr.yui-dt-even{background-color:#FFF;}  tr.yui-dt-odd{background-color:#EDF5FF;}  tr.yui-dt-even td.yui-dt-asc,  tr.yui-dt-even td.yui-dt-desc{background-color:#EDF5FF;}  tr.yui-dt-odd td.yui-dt-asc,  tr.yui-dt-odd td.yui-dt-desc{background-color:#DBEAFF;}  .yui-dt-list tr.yui-dt-even{background-color:#FFF;}  .yui-dt-list tr.yui-dt-odd{background-color:#FFF;}  .yui-dt-list tr.yui-dt-even td.yui-dt-asc,  .yui-dt-list tr.yui-dt-even td.yui-dt-desc{background-color:#EDF5FF;}  .yui-dt-list tr.yui-dt-odd td.yui-dt-asc,  .yui-dt-list tr.yui-dt-odd td.yui-dt-desc{background-color:#EDF5FF;}  th.yui-dt-highlighted,  th.yui-dt-highlighted a{background-color:#B2D2FF;}  tr.yui-dt-highlighted,  tr.yui-dt-highlighted td.yui-dt-asc,  tr.yui-dt-highlighted td.yui-dt-desc,  tr.yui-dt-even td.yui-dt-highlighted,  tr.yui-dt-odd td.yui-dt-highlighted{cursor:pointer;background-color:#B2D2FF;}  .yui-dt-list th.yui-dt-highlighted,  .yui-dt-list th.yui-dt-highlighted a{background-color:#B2D2FF;}  .yui-dt-list tr.yui-dt-highlighted,  .yui-dt-list tr.yui-dt-highlighted td.yui-dt-asc,  .yui-dt-list tr.yui-dt-highlighted td.yui-dt-desc,  .yui-dt-list tr.yui-dt-even td.yui-dt-highlighted,  .yui-dt-list tr.yui-dt-odd td.yui-dt-highlighted{cursor:pointer;background-color:#B2D2FF;}  th.yui-dt-selected,  th.yui-dt-selected a{background-color:#446CD7;}  tr.yui-dt-selected td,  tr.yui-dt-selected td.yui-dt-asc,  tr.yui-dt-selected td.yui-dt-desc{background-color:#426FD9;color:#FFF;}  tr.yui-dt-even td.yui-dt-selected,  tr.yui-dt-odd td.yui-dt-selected{background-color:#446CD7;color:#FFF;}  .yui-dt-list th.yui-dt-selected,  .yui-dt-list th.yui-dt-selected a{background-color:#446CD7;}  .yui-dt-list tr.yui-dt-selected td,  .yui-dt-list tr.yui-dt-selected td.yui-dt-asc,  .yui-dt-list tr.yui-dt-selected td.yui-dt-desc{background-color:#426FD9;color:#FFF;}  .yui-dt-list tr.yui-dt-even td.yui-dt-selected,  .yui-dt-list tr.yui-dt-odd td.yui-dt-selected{background-color:#446CD7;color:#FFF;}  .yui-pg-container,  .yui-dt-paginator{display:block;margin:6px 0;white-space:nowrap;}  .yui-pg-first,  .yui-pg-last,  .yui-pg-current-page,  .yui-dt-paginator .yui-dt-first,  .yui-dt-paginator .yui-dt-last,  .yui-dt-paginator .yui-dt-selected{padding:2px 6px;}  a.yui-pg-first,  a.yui-pg-previous,  a.yui-pg-next,  a.yui-pg-last,  a.yui-pg-page,  .yui-dt-paginator a.yui-dt-first,  .yui-dt-paginator a.yui-dt-last{text-decoration:none;}  .yui-dt-paginator .yui-dt-previous,  .yui-dt-paginator .yui-dt-next{display:none;}  a.yui-pg-page,  a.yui-dt-page{border:1px solid #CBCBCB;padding:2px 6px;text-decoration:none;background-color:#fff}  .yui-pg-current-page,  .yui-dt-paginator .yui-dt-selected{border:1px solid #fff;background-color:#fff;}  .yui-pg-pages{margin-left:1ex;margin-right:1ex;}  .yui-pg-page{margin-right:1px;margin-left:1px;}  .yui-pg-first,  .yui-pg-previous{margin-right:3px;}  .yui-pg-next,  .yui-pg-last{margin-left:3px;}  .yui-pg-current,  .yui-pg-rpp-options{margin-right:1em;margin-left:1em;}
*/