<%
	String hostPort = org.alfresco.web.studio.OverlayUtil.getOriginalContextPath(request);
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	
	String imagesPath = overlayPath + "/images";
	
	String iconsPath = imagesPath + "/icons";
	String treesPath = imagesPath + "/trees";
%>
body.studio 
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:12px;
	color:#333;
	background-color: white;
	padding: 0px;
	margin: 0px;
}

#no_webproject_selected
{
	color: gray;
	_font-style: italic;
	_font-weight: bold;
	text-decoration: underline;
}
#no_webproject_selected DIV:hover { cursor: pointer; }

#no_sandbox_selected
{
	color: gray;
	_font-style: italic;
	_font-weight: bold;
	text-decoration: underline;
}
#no_sandbox_selected DIV:hover { cursor: pointer; }

.MountSelectorCenter DIV:hover { 
	cursor: pointer;
}



.regionFrameButton {
	width: 24px
}
.iframeStyle1 { border-color: #FF0000 #00FF00 #0000FF #000000; padding-top: 10px; padding-right: 10px; padding-bottom: 20px; padding-left: 20px; border-style: groove; border-top-width: 5px; border-right-width: 5px; border-bottom-width: 10px; border-left-width: 5px}

.floatingMenu
{
	border-left: 1px gray solid;
	border-top: 1px gray solid;
	border-right: 1px black solid;
	border-bottom: 1px black solid;
	padding: 2px;
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size: 12px;
}
.floatingMenuHeader
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size: 12px;
	background-color: #ffffff;
	border-bottom: 1px gray solid;
}
.floatingMenuOptions
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size: 12px;
	background-color: #eeeeee;
}
A.floatingMenuOptions:link { color: #000000; text-decoration: none; }
A.floatingMenuOptions:visited { color: #000000; text-decoration: none; }
A.floatingMenuOptions:hover { color: #000000; text-decoration: none; }
A.floatingMenuOptions:active { color: #000000; text-decoration: none; }
	
.wizardFormPanel
{
	background-color: #aa0000;
}









.spacestree-icon-companyhome
{
	background-image:url(<%=treesPath%>/spacestree/companyhome.gif) !important;
}



.tree-icon-webapplicationroot
{
    background-image:url(<%=treesPath%>/contenttree/webapproot.gif) !important;
}

.tree-icon-webapplication-folder
{
    background-image:url(<%=treesPath%>/contenttree/folder.gif) !important;
}

.tree-icon-webapplication-file
{
    background-image:url(<%=treesPath%>/contenttree/file.gif) !important;
}

.tree-icon-webapplication-imagefile
{
    background-image:url(<%=imagesPath%>/filetypes/image.gif) !important;
}

.tree-icon-webapplication-textfile
{
    background-image:url(<%=imagesPath%>/filetypes/txt.gif) !important;
}

.tree-icon-webapplication-xmlfile
{
    background-image:url(<%=imagesPath%>/filetypes/xml.gif) !important;
}

.tree-icon-webapplication-htmlfile
{
    background-image:url(<%=imagesPath%>/filetypes/html.gif) !important;
}

.tree-icon-webapplication-pdffile
{
    background-image:url(<%=imagesPath%>/filetypes/pdf.gif) !important;
}





.tree-icon-rootnode
{
    background-image:url(<%=treesPath%>/navtree/rootnode.gif) !important;
}

.tree-icon-node
{
    background-image:url(<%=treesPath%>/navtree/rootnode.gif) !important;
}




.tree-icon-componenttree-root
{
    background-image:url(<%=treesPath%>/comptree/site.gif) !important;
}

.tree-icon-componenttree-component
{
    background-image:url(<%=treesPath%>/comptree/component.gif) !important;
}

.tree-icon-componenttree-componenttype
{
    background-image:url(<%=treesPath%>/comptree/component-type.gif) !important;
}





.maintoolbar-icon-getting-started
{
    background-image:url(<%=iconsPath%>/info_icon.gif);
}
.maintoolbar-icon-view-sandbox
{
    background-image:url(<%=treesPath%>/toolbar/view_sandbox.gif) !important;
}
.maintoolbar-icon-view-dashboard
{
    background-image:url(<%=iconsPath%>/dashboard.gif) !important;
}
.maintoolbar-icon-watch-tutorials
{
    background-image:url(<%=iconsPath%>/info_icon.gif);
}
.maintoolbar-icon-learn-about-alfresco
{
    background-image:url(<%=hostPort%>/images/logo/AlfrescoLogo32.gif);
}
.maintoolbar-icon-configure-web-site
{
    background-image:url(<%=treesPath%>/toolbar/configure_website.gif);
}
.maintoolbar-icon-configure-endpoints
{
    background-image:url(<%=treesPath%>/toolbar/configure_endpoints.png) !important;
}
.maintoolbar-icon-manage-site-templates
{
    background-image:url(<%=treesPath%>/toolbar/manage_site_templates.gif) !important;
}
.maintoolbar-icon-manage-content-presentation
{
    background-image:url(<%=treesPath%>/toolbar/manage_content_presentation.gif) !important;
}
.maintoolbar-icon-manage-content-presentation
{
    background-image:url(<%=treesPath%>/toolbar/manage_content_presentation.gif) !important;
}
.maintoolbar-icon-manage-site-layouts
{
    background-image:url(<%=treesPath%>/toolbar/manage_site_layouts.gif) !important;
}
.maintoolbar-icon-copy-page
{
    background-image:url(<%=treesPath%>/toolbar/copy_page.gif) !important;
}
.maintoolbar-icon-paste-page
{
    background-image:url(<%=treesPath%>/toolbar/paste_page.gif) !important;
}
.maintoolbar-icon-associate-templates
{
    background-image:url(<%=treesPath%>/toolbar/associate_templates.gif) !important;
}
.maintoolbar-icon-open-template
{
    background-image:url(<%=treesPath%>/toolbar/open_template.gif) !important;
}
.maintoolbar-icon-refresh-cache
{
    background-image:url(<%=treesPath%>/toolbar/refresh_cache.gif) !important;
}
.maintoolbar-icon-add-web-content
{
    background-image:url(<%=treesPath%>/toolbar/add_web_content.gif) !important;
}
.maintoolbar-icon-start-workflow
{
    background-image:url(<%=treesPath%>/toolbar/start_workflow.gif) !important;
}
.maintoolbar-icon-left
{
    background-image:url(<%=hostPort%>/images/logo/AlfrescoLogo32.gif) !important;
}











.tree-icon-navtree-addbutton
{
    background-image:url(<%=treesPath%>/buttons/add.gif) !important;
}
.tree-icon-navtree-editbutton
{
    background-image:url(<%=treesPath%>/buttons/edit.png) !important;
}
.tree-icon-navtree-deletebutton
{
    background-image:url(<%=treesPath%>/buttons/delete.gif) !important;
}






.dialog-grid-icon-addbutton
{
    background-image:url(<%=treesPath%>/buttons/add.gif) !important;
}
.dialog-grid-icon-editbutton
{
    background-image:url(<%=treesPath%>/buttons/edit.png) !important;
}
.dialog-grid-icon-deletebutton
{
    background-image:url(<%=treesPath%>/buttons/delete.gif) !important;
}
.dialog-grid-icon-optionsbutton
{
    background-image:url(<%=treesPath%>/buttons/options.gif) !important;
}




.regiontoolbar-icon-add-new-component
{
    background-image:url(<%=treesPath%>/regionToolbar/add_new_component.gif) !important;
}
.regiontoolbar-icon-add-existing-component
{
    background-image:url(<%=treesPath%>/regionToolbar/add_existing_component.gif) !important;
}
.regiontoolbar-icon-edit-component
{
    background-image:url(<%=treesPath%>/regionToolbar/edit_component.gif) !important;
}
.regiontoolbar-icon-delete-component
{
    background-image:url(<%=treesPath%>/regionToolbar/delete_component.gif) !important;
}



.region-window-body-configured-global-region 
{
    background-color: #CCCCFF;
    color: black;
    font-face: Verdana;
    font-size: 10px;
    padding-top:0px;
    padding-left:1px;
    
    border-left: 1px white solid;
    border-top: 1px white solid;
    border-bottom: 1px black solid;
    border-right: 1px black solid;;
}
.region-window-body-configured-template-region 
{
    background-color: #CCCCFF;
    color: black;
    font-face: Verdana;
    font-size: 10px;
    padding-top:0px;
    padding-left:1px;
    padding-right:1px;
    padding-bottom:1px;
    
    border-left: 1px white solid;
    border-top: 1px white solid;
    border-bottom: 1px black solid;
    border-right: 1px black solid;;
}
.region-window-body-configured-page-region 
{
    background-color: #CCCCFF;
    color: black;
    font-face: Verdana;
    font-size: 10px;
    padding-top:0px;
    padding-left:1px;
    
    border-left: 1px white solid;
    border-top: 1px white solid;
    border-bottom: 1px black solid;
    border-right: 1px black solid;;
}
.region-window-body-unconfigured-region 
{
    background-color: #CCCCFF;
    color: black;
    font-face: Verdana;
    font-size: 11px;
    padding-top:0px;
    padding-left:1px;    
}



.MountSelector
{
	width:100%;
	height:25px;
	cursor:default !important;
	border-top:1px solid #F0F5FA;
	border-bottom:1px solid #A9BFD3;
	background-image:url(<%=overlayPath%>/images/menubg.gif);
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
	background-image:url(<%=overlayPath%>/images/menubg.gif);
	overflow:visible;
	margin-top:1px;
	margin-left:3px;
}


.MountSelectorLeft
{
	background-image:url(<%=overlayPath%>/images/menuBgLeft.gif);	
}

.MountSelectorCenter
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	color: #000000;
	padding: 0px;
	margin: 0px;
	background-image:url(<%=overlayPath%>/images/menuBgCenter.gif);	
}

.MountSelectorCenterSelected
{
	font-family: Tahoma,Arial,Helvetica,sans-serif;
	font-size:11px;
	font-weight: bold;	
	color: #000000;
	padding: 0px;
	margin: 0px;
	background-image:url(<%=overlayPath%>/images/menuBgCenter.gif);	
}

.MountSelectorRight
{
	background-image:url(<%=overlayPath%>/images/menuBgRight.gif);	
}

.OverlaySearch
{
	padding-top: 0px;
	margin-top: 0px;
	height: 18px;
	font-size:11px;
	font-family:tahoma,verdana,helvetica;	
	float:left;
	vertical-align:top;
}







.icon-filetype-html-16 { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=overlayPath%>/images/filetypes/html.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-filetype-image-16 { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=overlayPath%>/images/filetypes/image.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-filetype-pdf-16 { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=overlayPath%>/images/filetypes/pdf.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-filetype-txt-16 { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=overlayPath%>/images/filetypes/txt.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}

.icon-filetype-xml-16 { 
	display:block;
	height:22px;
	padding-left:20px;
	background-image:url(<%=overlayPath%>/images/filetypes/xml.gif);
	background-repeat:no-repeat;
	background-position:2px 0px;
}





.AlfrescoTemplateDesignerFrame
{
	display: none;
	position: absolute;
	top: 0px;
	left: 0px;
	background-color: gray;
}

.AlfrescoTemplateDesignerEditor
{
	display: none;
	position: absolute;
	border: 1px black dashed;
	overflow: hidden;
	background-color: #ffffea;
}

.AlfrescoTemplateRegion
{
	display: none;
	position: absolute;
	border: 1px black dashed;
	overflow: hidden;
	background-color: #ffffca;
}

.AlfrescoDashboardFrame
{
	display: none;
	position: absolute;
	top: 0px;
	left: 0px;
	background-color: gray;
}

.AlfrescoDashboardEditor
{
	display: none;
	position: absolute;
	border: 1px black dashed;
	overflow: hidden;
	background-color: #ffffea;
}

.AlfrescoDashlet
{
	display: none;
	position: absolute;
	border: 1px black dashed;
	overflow: hidden;
	background-color: #ffffca;
}










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


.AlfrescoComponentEditor
{
   font-family: Verdana,Tahoma,Arial,Helvetica,sans-serif;
   font-size:11px;
   font-style:normal;
   font-weight:normal;
   width: 100%;
   margin: 5px 5px 5px 5px;  
}
.AlfrescoComponentEditor .label
{
   color: #515D6B;
}
.AlfrescoComponentEditor INPUT
{
   border: 1px solid #C5D6E2;
   padding: 2px;
   font-weight: normal;
   color: #515D6B;
}
.AlfrescoComponentEditor TEXTAREA
{
   border: 1px solid #C5D6E2;
   padding: 2px;
   font-weight: normal;
   color: #515D6B;
}
.AlfrescoComponentEditor SELECT
{
   border: 1px solid #C5D6E2;
   padding: 2px;
   font-weight: normal;
   color: #515D6B;
}
.AlfrescoComponentEditor TD
{
	padding: 2px;
}
