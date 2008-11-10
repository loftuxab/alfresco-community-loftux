<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

.alf-root-menu-itm-holder {
   position: absolute;
   cursor: default;
   border: 1px solid #718BB7;
   background-color: #F0F0F0;
   overflow: visible;
   color: #000000;
   padding: 2px;
   width: 190px;
   z-index:99000;
}

.alf-root-menu-itm {
   font-family: tahoma, arial, sans-serif;
   font-size: 11px;
   height: 20px;
   cursor:pointer;
}

.alf-root-menu-itm-caption {
   font-family: tahoma, arial, sans-serif;
   font-size: 11px;
   position: relative;
   white-space: nowrap;
   margin-top:3px;
}

.alf-root-menu-itm-sep {
   cursor: default;
   color: #000000;
   height: 1px;
   width: 100%;
   text-align: left;
   background-color: #F0F0F0;
   border-bottom: 1px solid #E2E3E3;
   border-top: 1px solid #FFFFFF;
   font-size: 1px;
}

.alf-menu-template {
   position: absolute;
   height: 21px;
   top: 0px;
   left: 0px;
   width: 100%;
   border-top: 1px solid #F0F5FA;
   border-bottom: 1px solid #A9BFD3;
   background-image: url( <%=rootPath%>/images/menubg.gif );
}

.alf-menu-itm-caption {
   font-family: tahoma, arial, sans-serif;
   font-size: 11px;
   white-space: nowrap;
}

.alf-root-menu-itm.selected #center-sel {
   background-image: url( <%=rootPath%>/images/menuBgCenter.gif );
}

.alf-root-menu-itm.selected #left-sel {
   background-image: url( <%=rootPath%>/images/menuBgLeft.gif );
   background-repeat: no-repeat;
}

.alf-root-menu-itm.selected #right-sel {
   background-image: url( <%=rootPath%>/images/menuBgRight.gif );
   background-repeat: no-repeat;
}

.alf-menu-itm {
	border: 1px solid #F0F0F0;
	zoom: 1;
	width: 100%;
	cursor: pointer;
	width:188px;
}

.alf-menu-itm.selected-i {
   background-image: url( <%=rootPath%>/images/subMenuselect.gif );
   border: 1px solid #AACCF6;
}

.alf-menu-itm.disable-i {
   filter: alpha( opacity = 50 );
   opacity: 0.40;
   -moz-opacity: 0.40;
}

.alf-itm-link {
   text-decoration:none;
   color:black;
}