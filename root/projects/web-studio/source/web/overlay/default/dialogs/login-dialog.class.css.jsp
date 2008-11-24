<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String imagesPath = rootPath + "/images";
%>

.login-window
{
	position:absolute;
	width:500px;
	height:300px;
	left:300px;
	top: 200px;
	cursor:default;
}

.login-panel
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

.login-input
{
   border: 1px solid #C5D6E2;
   padding: 2px;
   font-weight: normal;
}

.login-fieldset
{
   border: 0px;
   padding: 10px;
}

.login-button
{
   background-color: #f0f0f0;
}

.login-copyright
{
   font-family: Arial,Helvetica,sans-serif;
   font-size: 11px;
   font-weight: normal;
   color: #959a9b;
}

.login-logo
{
   position: absolute;
   top: 16px;
   left: 20px;
   height: 64px;
   width: 450px;
   background: transparent url("<%=imagesPath%>/login/logo.png") no-repeat;
}
