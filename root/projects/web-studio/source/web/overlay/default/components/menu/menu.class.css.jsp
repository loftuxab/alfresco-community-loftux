<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

/* CSS Document */

div#AlfrescoApplicationRootMenuTemplate.AMHorizontalHolder {
	height:1px;
}
div#AlfrescoApplicationRootMenuDefault.AMHorizontalHolder {
	position:absolute;
	width:100%;
	height:25px;
	left:0px;
	top:0px;
	cursor:default !important;
	border-top:1px solid #F0F5FA;
	border-bottom:1px solid #A9BFD3;
	background-image:url(<%=rootPath%>/images/menubg.gif);
	overflow:visible;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot {
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
}

div#AlfrescoApplicationRootMenuDefault .AMRootChanger {
	border:1px solid #B5B8C8;
	padding-top:3px;
	cursor:pointer;
	float:right;
	font-size:11px;
	font-family:arial,tahoma,helvetica,sans-serif;
	color:black;
	height:15px;
	width:151px;
	text-align:center;
	background-color:white;
	margin-top:1px;
}

div#AlfrescoApplicationRootMenuDefault .AMRootChangerArrow {	
	border-bottom: 1px solid #B5B8C8;
	padding-top:4px;
	cursor:pointer;
	float:right;
	font-size:1px;
	height:15px;
	width:17px;
	background-image:url(<%=rootPath%>/images/triggerDefault.gif);
	margin-top:1px;
	margin-right:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRootChangerText {	
	padding-top:4px;
	cursor:text;
	float:right;
	font-size:11px;
	color:#333333;
	font-family:tahoma,arial,helvetica,sans-serif;
	height:15px;
	width:70px;
	text-align:center;
	overflow:visible;
	margin-top:1px;
	margin-right:1px;
}

div#AlfrescoApplicationRootMenuDefault .AMItem.Disabled  {
    opacity:0.40;
    -moz-opacity:0.40;
	 filter:alpha(opacity=50);
}

div#AlfrescoApplicationRootMenuDefault .AMItem.Disabled .AMSubmenuStyle .AMItemIcon {
    cursor:default;
}

div#AlfrescoApplicationRootMenuDefault .AMItem.Disabled .AMSubmenuStyle .AMItemCaption {    
    cursor:default;
}

div#AlfrescoApplicationRootMenuDefault .AMRootChangerArrow.RolloverItem2 {
	background-image:url(<%=rootPath%>/images/triggerRollover.gif);
	color:#233D6D;
}

div#AlfrescoApplicationRootMenuDefault .AMRootChangerArrow.PushItem2 {
	background-image:url(<%=rootPath%>/images/triggerPress.gif);
	color:#233D6D;
}

div#AlfrescoApplicationRootMenuDefault .AMRootChangerArrow.RolloverUpItem2 {
	background-image:url(<%=rootPath%>/images/triggerUp.gif);
	color:#233D6D;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot.RolloverItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);
   background-repeat:no-repeat;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot.RolloverItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

div#AlfrescoApplicationRootMenuDefault .AMRoot.RolloverItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);
   background-repeat:no-repeat;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot.PushItemRoot .AMItemLeft{
	background-image:url(<%=rootPath%>/images/menuBgLeft.gif);
   background-repeat:no-repeat;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot.PushItemRoot .AMItemCenter{
	background-image:url(<%=rootPath%>/images/menuBgCenter.gif);	
}

div#AlfrescoApplicationRootMenuDefault .AMRoot.PushItemRoot .AMItemRight{
	background-image:url(<%=rootPath%>/images/menuBgRight.gif);
   background-repeat:no-repeat;
}

div#AlfrescoApplicationRootMenuDefault .AMItem.RolloverItem .AMSubmenuStyle {
	background-image:url(<%=rootPath%>/images/subMenuselect.gif);
	border-bottom:1px solid #AACCF6;
	border-top:1px solid #AACCF6;
	border-right:1px solid #AACCF6;
	color:#233D6D;
}

div#AlfrescoApplicationRootMenuDefault .AMItem .AMSubmenuStyle {
	border-bottom:1px solid #F0F0F0;
	border-top:1px solid #F0F0F0;
	border-right:1px solid #F0F0F0;
	color:#233D6D;
}

div#AlfrescoApplicationRootMenuDefault .AMItem.PushItem .AMSubmenuStyle {
	background-image:url(<%=rootPath%>/images/subMenuselect.gif);	
	border-bottom:1px solid red;
	border-top:1px solid #AACCF6;
	border-left:1px solid #AACCF6;
	color:#233D6D;
}



div#AlfrescoApplicationRootMenuDefault .AMItem.PushItem .AMSubmenuStyle .AMItemCaptionEdit{
	background-color: #DFE8F6;
	color:#233D6D;
}

div#AlfrescoApplicationRootMenuDefault .AMImg{
	vertical-align:middle;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderLogo {
	position:absolute;
	width:126px;
	height:117px;
	left:3px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderConfiguration {
	position:absolute;
	width:210px;
	height:125px;
	left:40px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderEditor {
	position:absolute;
	width:172px;
	height:97px;
	left:151px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderEditorThird {
	position:absolute;
	width:126px;
	height:72px;
	left:151px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderWorkflow {
	position:absolute;
	width:202px;
	height:50px;
	left:232px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderWorkflowSecond {
	position:absolute;
	width:195px;
	height:70px;
	left:241px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderPreferences {
	position:absolute;
	width:154px;
	height:244px;
	left:313px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderPreferencesPageEditor {
	position:absolute;
	width:153px;
	height:240px;
	left:302px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}


div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderPreferencesPageEditor .AMItemCaption {

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	width:111px;
	text-align:left;
	overflow:visible;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderPreferencesPageEditor .AMItemIcon {
	padding-left:8px;
	padding-top:2px;
	padding-right:5px;
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	height:20px;
	text-align:left;
	overflow:visible;
	border:1px solid #F0F0F0;
	border-right:1px solid #E2E3E3;
}

div#AlfrescoApplicationRootMenuDefault .AMRoot .AMVerticalHolderPreferencesThird {
	position:absolute;
	width:153px;
	height:250px;
	left:237px;
	top:23px;
	cursor:default;
	border:1px solid #718BB7;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
	padding:2px;
}

div#AlfrescoApplicationRootMenuDefault .AMRootChanger .AMVerticalHolderEditingMode {
	position:absolute;
	width:168px;
	height:88px;
	top:21px;
	right:2px;
	cursor:default;
	border:1px solid #98C0F4;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
}

div#AlfrescoApplicationRootMenuDefault .AMRootChangerArrow .AMVerticalHolderEditingMode {
	position:absolute;
	width:168px;
	height:88px;
	top:21px;
	right:2px;
	cursor:default;
	border:1px solid #98C0F4;
	background-color:#F0F0F0;
	overflow:visible;
	color:#000000;
}

div#AlfrescoApplicationRootMenuDefault .AMItemIcon {
	padding-left:8px;
	padding-top:3px;
	padding-right:5px;
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	height:18px;
	text-align:left;
	overflow:visible;
	border:1px solid #F0F0F0;
	border-right:1px solid #E2E3E3;
}



div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderConfiguration .AMItemIcon {	
	height:19px;
}


div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderLogo .AMItemCaption {	
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	width:85px;
	text-align:left;
	overflow:visible;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderConfiguration .AMItemCaption {	

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	width:168px;
	text-align:left;
	overflow:visible;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderEditor .AMItemCaption {	

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	width:130px;
	text-align:left;
	overflow:visible;

}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderEditorThird .AMItemCaption {	

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;

	width:100px;
	text-align:left;
	overflow:visible;

}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderWorkflow .AMItemCaption {	

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	width:160px;
	text-align:left;
	overflow:visible;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderWorkflowSecond .AMItemCaption {	

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;

	width:153px;
	text-align:left;
	overflow:visible;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderPreferencesThird .AMItemCaption {

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	width:111px;
	text-align:left;
	overflow:visible;

}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderEditingMode .AMItemCaptionEdit {	
	padding-top:2px;
	padding-left:2px;
	cursor:pointer;
	font-size:12px;
	font-family:tahoma,arial,helvetica,sans-serif;
	color:#333333;
	height:18px;
	width:166px;
	text-align:left;
	background-color:#F0F0F0;
	overflow:visible;
	border:1px solid #F0F0F0;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderEditor .AMItemIcon {	
	padding-left:8px;
	padding-top:2px;
	padding-right:5px;
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	height:20px;
	text-align:left;
	overflow:visible;
	border:1px solid #F0F0F0;
	border-right:1px solid #E2E3E3;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderEditorThird .AMItemIcon {	
	padding-left:8px;
	padding-top:2px;
	padding-right:5px;
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	height:20px;
	text-align:left;

	overflow:visible;
	border:1px solid #F0F0F0;
	border-right:1px solid #E2E3E3;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderWorkflow .AMItemIcon {
	padding-left:8px;
	padding-top:3px;
	padding-right:5px;
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	height:20px;
	text-align:left;

	overflow:visible;
	border:1px solid #F0F0F0;
	border-right:1px solid #E2E3E3;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderPreferences .AMItemCaption {

	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	width:111px;
	text-align:left;
	overflow:visible;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderPreferences .AMItemIcon {
	padding-left:8px;
	padding-top:2px;
	padding-right:5px;
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	height:20px;
	text-align:left;
	overflow:visible;
	border:1px solid #F0F0F0;
	border-right:1px solid #E2E3E3;	
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderPreferencesThird .AMItemIcon {
	padding-left:8px;
	padding-top:1px;
	padding-right:5px;
	cursor:pointer;
	font-size:11px;
	font-family:tahoma,arial,sans-serif;
	color:#000000;
	height:22px;
	text-align:left;
	overflow:visible;
	border:1px solid #F0F0F0;
	border-right:1px solid #E2E3E3;	
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderLogo .AMSeparator {	
	padding-left:8px;
	cursor:default;
	color:#000000;
	height:1px;
	width:115px;
	text-align:left;
	background-color:#F0F0F0;
	overflow:visible;
    border-bottom:1px solid #E2E3E3;
	border-top:1px solid #FFFFFF;
    font-size:1px;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderConfiguration .AMSeparator {	
	padding-left:8px;
	cursor:default;
	color:#000000;
	height:1px;
	width:200px;
	text-align:left;
	background-color:#F0F0F0;
	overflow:visible;
    border-bottom:1px solid #E2E3E3;
	border-top:1px solid #FFFFFF;
    font-size:1px;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderWorkflow .AMSeparator {	
	padding-left:8px;
	cursor:default;
	color:#000000;
	height:1px;
	width:180px;
	text-align:left;
	background-color:#F0F0F0;
	overflow:visible;
    border-bottom:1px solid #E2E3E3;
	border-top:1px solid #FFFFFF;
    font-size:1px;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderPreferences .AMSeparator {	
	padding-left:8px;
	cursor:default;
	color:#000000;
	height:1px;
	width:145px;
	text-align:left;
	background-color:#F0F0F0;
	overflow:visible;
    border-bottom:1px solid #E2E3E3;
	border-top:1px solid #FFFFFF;
    font-size:1px;
}

div#AlfrescoApplicationRootMenuDefault .AMVerticalHolderEditor .AMSeparator {	
	padding-left:8px;
	cursor:default;
	color:#000000;
	height:1px;
	width:160px;
	text-align:left;
	background-color:#F0F0F0;
	overflow:visible;
    border-bottom:1px solid #E2E3E3;
	border-top:1px solid #FFFFFF;
    font-size:1px;
}

div#AlfrescoApplicationRootMenuDefault .AMEditingMode {	
	cursor:text;
	float:left;
	font-size:11px;
	font-family:tahoma,arial,helvetica,sans-serif;
	color:#333333;
	height:16px;
	text-align:center;
	overflow:visible;
	margin-top:5px;
}