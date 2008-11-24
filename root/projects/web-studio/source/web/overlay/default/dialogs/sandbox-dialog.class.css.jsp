<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String imagesPath = rootPath + "/images";
%>

.sandbox-window
{
	position:absolute;
	width:500px;
	height:300px;
	left:300px;
	top: 200px;
	cursor:default;
}

.sandbox-panel
{
   position:absolute;
   background-color: #B4B4B4;
   text-align: left;   
   width: 502px;
   height: 289px;
   background: transparent url("<%=imagesPath%>/login/loginbg.png") no-repeat;
   font-family: Arial,Helvetica,sans-serif;
   font-size: 12px;
   font-weight: bold;
   color: #515354;
}

.sandbox-input
{
   border: 1px solid #C5D6E2;
   padding: 2px;
   font-weight: normal;
}

.sandbox-fieldset
{
   border: 0px;
   padding: 10px;
}

.sandbox-table
{
   border: 1px;
   padding: 10px;
}

.sandbox-table TD
{
	border-spacing:10px;
}

.sandbox-button
{
   background-color: #f0f0f0;
}

.sandbox-copyright
{
   font-family: Arial,Helvetica,sans-serif;
   font-size: 11px;
   font-weight: normal;
   color: #959a9b;
}

.sandbox-header
{
   font-family: Arial,Helvetica,sans-serif;
   font-size: 14px;
   font-weight: normal;

   position: absolute;
   top: 16px;
   left: 20px;
   height: 64px;
   width: 450px;
}

.sandbox-info
{
	font-weight: normal;
}

.sandbox-row-even
{
   background-color: #F1F7FD;
}

.sandbox-row-odd
{
}