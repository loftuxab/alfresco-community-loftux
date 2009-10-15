<%
	String rootPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
%>

<table width="100%" height="100%" id="alfrescoPanelTbl" cellpadding="0" cellspacing="0" style="position:relative;">
	<tr>
		<td id="alfrescoPanelHolder"></td>
	</tr>
</table>

<div id="templateContainer" style="display:none">

	<!-- Splitter Template -->
	<div id="AlfSplitterTemplate" style="position : relative; height : 100%; width : 100%; top : 0px;">
		<div id="AlfSplitterContainer" style="width : 100%; height : 100%; overflow : hidden;">
			<div id="AlfSplitterPanel" style="position : absolute; top : 0px; overflow : hidden; height : 100%"> </div>
			<div id="AlfSplitterDivider" style="position : absolute; top : 0px; overflow : hidden; height : 100%; cursor : w-resize; width : 4px; background-color:#D6E3F2"> </div>
			<div id="AlfSplitterCover" style="position : absolute; overflow : hidden; cursor : w-resize; z-index : 999999; top : 20px;"> </div>
		</div>
	</div>

	<!-- Menu Template -->
	<div id="AlfMenuTemplate" class="alf-menu-template" style="visibility:hidden">
		<span id="AlfRootMenuHolder" class="alf-root-menu-itm" style="float:left">
			<table cellpadding="0px" cellspacing="0px" height="100%" border="0px">
				<tr>
					<td id="left-sel" style="font-size:1px;width:3px"></td>
					<td id="center-sel" valign="middle">
						<span style="float:left;" id="AlfRootMenuCaptionImgholder">
							<img id="AlfRootMenuCaptionImg"/>
						</span>
						<span id="AlfRootMenuCaptionHolder" style="float:left;" class="alf-root-menu-itm-caption"></span>
						<span style="float:left;height:21px">
							<img src="<%=rootPath%>/images/arrow.gif" vspace="3px;" style="margin-top:6px;">
						</span>
					</td>
					<td id="right-sel" style="font-size:1px;width:3px"></td>
				</tr>
			</table>
			<div id="AlfRootMenuItmHolder" class="alf-root-menu-itm-holder" style="display:none">
				<div id="AlfRootMenuItm" class="alf-menu-itm">
					<table class="alf-root-menu-itm" cellpadding="0px" cellspacing="0px" width="100%">
						<tr>
							<td style="width:20px">
								<img id="AlfRootMenuItmIcon"/>
							</td>
							<td>
								<div id="AlfRootMenuItmCaption" class="alf-menu-itm-caption"></div>
							</td>
						</tr>
					</table>
				</div>
				<div id="AlfRootMenuItmRadio" class="alf-menu-itm">
					<table class="alf-root-menu-itm" cellpadding="0px" cellspacing="0px" width="100%">
						<tr>
							<td style="width:20px">
								<input type="radio" id="AlfMenuItmRadio"/>
							</td>
			       				<td>
				  				<div id="AlfMenuItmRadioCaption" class="alf-menu-itm-caption"></div>
							</td>
						</tr>
					</table>
				</div>
				<div id="AlfRootMenuItmLink" class="alf-menu-itm">
					<table class="alf-root-menu-itm" cellpadding="0px" cellspacing="0px" width="100%">
						<tr>
							<td style="width:20px">
								<img id="AlfMenuItmIcon"/>
							</td>
							<td>
								<div id="AlfMenuItmLinkCaption" class="alf-menu-itm-caption">
									<a id="AlfMenuItmLink" href="" class="alf-itm-link" target="_blank"></a>
								</div>
							</td>
						</tr>
					</table>
				</div>
				<div id="AlfRootMenuItmCheckBox" class="alf-menu-itm">
					<table class="alf-root-menu-itm" cellpadding="0px" cellspacing="0px" width="100%">
						<tr>
							<td style="width:20px" valign="middle" align="center">
								<input type="checkbox" id="AlfMenuItmCheckBox"/>
							</td>
							<td>
								<div id="AlfMenuItmCheckBoxCaption" class="alf-menu-itm-caption"></div>
							</td>
						</tr>
					</table>
				</div>
				<div id="AlfRootMenuItmSep" class="alf-root-menu-itm-sep"></div>
			</div>
		</span>
	</div>

	<!-- Drop Down Template -->
	<div id="AlfDropDownTempalte" class="alf-drop-down-tempalte" style="display:none">
		<span id="AlfDropDownHolder" style="float:left;height:20px">
			<div>
				<span style="float:left;" class="alf-drop-down-root-itm">
					<span id="AlfDropDownCaptionHolder" style="float:left;width:140px" class="alf-drop-down-root-itm-caption">&nbsp;</span>
				</span>
				<span style="float:left;height:20px;width:16px" class="alf-drop-down-root-itm-trigger-img">
					<!--<img src="<%=rootPath%>/images/triggerUp.gif" style="height:20px"> -->
				</span>
			</div>
			<div id="AlfDropDownItmHolder" class="alf-drop-down-itm-holder" style="display:none">
				<div id="AlfDropDownItm" class="alf-drop-down-itm">
					<table cellpadding="0px" cellspacing="0px" width="100%" height="100%">
						<tr>
							<td style="width:20px"></td>
							<td>
								<div id="AlfDropDownItmCaption" class="alf-drop-down-itm-caption"></div>
							</td>
						</tr>
					</table>
				</div>
			</div>
		</span>
	</div>

	<!-- Application Template -->
	<div id="AlfrescoApplicationTemplate" class="Default" style="overflow:hidden;position:absolute;top:0px;left:0px;">
	
		<div id="AlfrescoMessageBoxTmplate" class="alf-message-box yui-skin-sam">
	   
			<!--Borders&HeaderBg-->
			<div id="aw-top-left-corner" class="window-tl">&nbsp;</div>
			<div id="aw-top-right-corner" class="window-tr">&nbsp;</div>
			<div id="aw-header-bg" class="window-header">&nbsp;</div>
			<div id="aw-bottom-left-corner" class="window-bl">&nbsp;</div>
			<div id="aw-bottom-right-corner" class="window-br">&nbsp;</div>
			<div id="aw-left-border" class="alf-window-ml">&nbsp;</div>
			<div id="aw-right-border" class="alf-window-mr">&nbsp;</div>
			<div id="aw-bottom-border" class="window-bottom">&nbsp;</div>
		
			<!--Header-->
			<div id="aw-icon" class="AWIDefaulth">&nbsp;</div>
			<div id="aw-title-div">
				<table border="0" cellpadding="0" cellspacing="0" width="100%">
					<tr>
						<td nowrap="nowrap" id="aw-title" class="header-text"></td>
					</tr>
				</table>
			</div>
			<div id="aw-button-close" class="AWBDefault_cl">&nbsp;</div>
			<div id="aw-body" style="overflow : auto;">
				<table style="width:100%; height: 100px" cellpadding="0" cellspacing="0">
					<tr>
						<td id="AWMessageContainer" align="center" class="window-body">
							<div id="aw-body-content" style="overflow : hidden;"></div>
						</td>
					</tr>
				</table>
			    <table id="AWButtonContainer" width="100%">
					<tr>
						<td align="center"><input id="AWButtonOk" type="button" value="ok"/></td>
					</tr>
				</table>
			</div>
		</div>
	
		
		<!--  Message Box Progress Bar -->
		<div id="AlfrescoMessageBoxProgressBar" class="alf-window-progress yui-skin-sam">
		
			<!--Borders&HeaderBg-->
			<div id="aw-top-left-corner" class="window-tl">&nbsp;</div>
			<div id="aw-top-right-corner" class="window-tr">&nbsp;</div>
			<div id="aw-header-bg" class="window-header">&nbsp;</div>
			<div id="aw-bottom-left-corner" class="window-bl">&nbsp;</div>
			<div id="aw-bottom-right-corner" class="window-br">&nbsp;</div>
			<div id="aw-left-border" class="alf-window-ml">&nbsp;</div>
			<div id="aw-right-border" class="alf-window-mr">&nbsp;</div>
			<div id="aw-bottom-border" class="window-bottom">&nbsp;</div>
	
			<div id="aw-body" style="overflow : auto;">
				<table style="width:100%; height:80px" cellpadding="0" cellspacing="0">
					<tr>
						<td id="AWMessageContainer" align="center" class="window-body">
							<div id="alf-mess-box-pb-content" style="overflow : hidden;"></div>
						</td>
					</tr>
				</table>
				<table id="AWButtonContainer" width="100%">
					<tr>
						<td align="center">
							<div class="place-for-progress-bar">&nbsp;</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
	
	
		<!-- Alfresco Form -->
		<div id="AlfrescoForm" class="alf-form yui-skin-sam" height="600px">
			<div class="alf-form-caption"></div>
			<div class="alf-form-body">
				<table border="0" cellpadding="0" cellspacing="0">
					<tr>
						<td align="left" valign="top" class="alf-form-body-content" nowrap>
							<table id="AlfFormFieldText" width="100%" cellpadding="0" cellspacing="0" align="left">
								<tbody>
									<tr class="alf-form-row" align="left">
										<td class="alf-form-label" valign="top" align="left"></td>
										<td class="alf-form-field-container" align="left">
											<input type="text" class="alf-form-field"/>
										</td>
									</tr>
								</tbody>
	 						</table>
							<table id="AlfFormFieldTextArea" width="100%" cellpadding="0" cellspacing="0" align="left">
								<tbody>
									<tr class="alf-form-row" align="left">
										<td class="alf-form-label" valign="top" align="left"></td>
										<td class="alf-form-field-container" align="left">
											<textarea class="alf-form-field"></textarea>
										</td>
									</tr>
								</tbody>
							</table>
							<table id="AlfFormCombo" cellpadding="0" cellspacing="0" align="left">
								<tbody>
									<tr class="alf-form-row" align="left">
										<td class="alf-form-label" valign="top" align="left" nowrap></td>
										<td class="alf-form-field-container" align="left" nowrap></td>
									</tr>
								</tbody>
							</table>
							<table id="AlfFormRadio" width="100%" cellpadding="0" cellspacing="0" align="left">
								<tbody>
									<tr class="alf-form-row" align="left">
										<td class="alf-form-label" valign="top" align="left"></td>
										<td class="alf-form-field-container" align="left"></td>
									</tr>
								</tbody>
							</table>
						</td>
					</tr>
				</table>
			</div>
			<div class="form-footer">
				<table border="0" cellpadding="0" cellspacing="0" style="width:100%;height:100%">
					<tr>
						<td align="center" valign="middle">
							<table style='text-align:center;height:100%'>
								<tr class="alf-footer-content"></tr>
							</table>
						</td>
					</tr>
				</table>
			</div>
		</div>
	
		<div id="AATwoPanels"></div>

		<div id="FloatingMenuIcon">&nbsp;</div>
		
		<div id="FloatingMenuControl">
			<table width="100%" height="100%" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td style="border-bottom: 1px white solid; border-top: 1px white solid; border-left: 1px white solid">
						<img id="FloatingMenuIconSpacer" src="<%=rootPath%>/images/spacer.gif" width="48px" />
					</td>
					<td width="100%" valign="top" style="padding-left: 3px; padding-top: 1px; padding-right: 1px; border-right: 1px white solid; border-bottom: 1px white solid; border-top: 1px white solid" valign="top" align="left">					
						<table width="100%" cellpadding="1" cellspacing="0">
							<tr>
								<td width="100%" style="border-bottom: 1px gray solid; padding-bottom: 1px" nowrap>
									<span class="FloatingMenuTitle"></span>
								</td>
							</tr>						
							<tr>
								<td width="100%" style="border-bottom: 1px gray solid; padding-bottom: 1px" nowrap>
									<span class="FloatingMenuSandboxId"></span>
								</td>
							</tr>
							<tr>
								<td width="100%" style="padding-bottom: 1px" nowrap>
									<span class="FloatingMenuInfo"></span>
								</td>
							</tr>
						</table>						
					</td>
				</tr>
			</table>
		</div>
		
	</div>

	<div id="AlfrescoRegionTemplate" class="Active alf-region" style="position : absolute;">
	
		<!--Borders&HeaderBg-->
		<div id="ar-top-left-corner" class="region-tl"> </div>
		<div id="ar-top-right-corner" class="region-tr"> </div>
		<div id="ar-header-bg" class="region-header">
			<table cellspacing="2" cellpadding="2">
				<tr>
					<td nowrap>
						<span id="region-overlay-title"></span>
					</td>
					<td width="100%"></td>
					<td nowrap>
						<table id="button-delete" cellpadding="0" cellspacing="0" class="btn"><tr><td><div class="i"> </div></td><td><div class="button-delete"> </div></td><td><div class="i"> </div></td></tr></table>
					</td>
				</tr>
			</table>
		</div>
		<div id="ar-bottom-left-corner" class="region-bl"> </div>
		<div id="ar-bottom-right-corner" class="region-br"> </div>
		<div id="ar-left-border" class="alf-region-ml"> </div>
		<div id="ar-right-border" class="alf-region-mr"> </div>
		<div id="ar-bottom-border" class="region-bottom"> </div>		
		<div id="ar-body" class="region-body"> </div>
	</div>


	<!-- Page Editor Template -->
	<div id="AlfrescoPageEditorTemplate">
		<div id="PageEditorTab" style="position:absolute;"></div>
	</div>


	<!-- Panel Template -->
	<div id="AlfrescoPanelTemplate">
		<table id="AlfrescoTwoPanelsResizer" width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td id="ATPTop"></td>
			</tr>
			<tr>
				<td id="ATP">
					<div id="ATPLeftDiv" style="float:left; display: none;">
						<div class="ATPHeader">Site Builder</div>
					</div>
					<div id="ATPResizer" style="display:none;width:10px;height : 100%; float: left;">&nbsp;</div>
					<div id="ATPRightDiv"></div>
				</td>
			</tr>
		</table>
	</div>


	<!-- Panel Resizer Menu Template -->
	<div id="AlfrescoPanelResizerMenuTemplate" class="AMVerticalHolder">
		<div class="AMRoot">
			<div id="SMCaption"><strong>20% / 80%</strong></div>
		</div>
		<div class="AMRoot">
			<div id="SMCaption">30% / 70%</div>
		</div>
		<div class="AMRoot">
			<div id="SMCaption">40% / 60%</div>
		</div>
		<div class="AMRoot">
			<div id="SMCaption">50% / 50%</div>
		</div>
	</div>

	<!-- Sliders Sector Template -->
	<div id="AlfrescoSlidersSectorTemplate"></div>
	
	<!-- Application Panels -->
	<div id="ApplicationSplitterPanelHolder" style="width : 100%; height : 100%; overflow : hidden;">
		<div id="SurfSiteApplication_SplitterPanel" style="position : absolute; top : 0px; overflow : hidden; height : 100%"> </div>
		<div id="SurfAssembleApplication_SplitterPanel" style="position : absolute; top : 0px; overflow : hidden; height : 100%"> </div>
		<div id="ContentApplication_SplitterPanel" style="position : absolute; top : 0px; overflow : hidden; height : 100%"> </div>
	</div>

	<!-- Application: Surf Site -->
	<div id="SurfSiteApplication_SlidersSectorTemplate">

		<!-- Applet: Navigation -->
		<div id="NavigationApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
					<div class="ASSToggleImage">&nbsp;</div>
					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData">
					<div class="ASSNavigation"></div>
				</div>
			</div>
		</div>
		
		<!-- Applet: Templates -->
		<div id="TemplatesApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>		
		
		<!-- Applet: Components -->
		<div id="ComponentsApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>		

		<!-- Applet: Content Associations -->
		<div id="ContentAssociationsApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>
		
		<!-- Applet: Sandbox -->
		<div id="SandboxApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>		
		
		<!-- Applet: Search -->
		<!--
		<div id="SearchApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>
		-->		
		
	</div>
	
	<!-- Application: Surf Assemble Application -->
	<div id="SurfAssembleApplication_SlidersSectorTemplate">

		<!-- Applet: Web Content -->
		<div id="WebContentApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>
		
		<!-- Applet: Images -->
		<div id="ImagesApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>

		<!-- Applet: Videos -->
		<div id="VideosApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>
				
		<!-- Applet: Spaces -->
		<div id="SpacesApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>

		<!-- Applet: Sites -->
		<div id="SitesApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>		
		
		<!-- Applet: Sandbox -->
		<div id="SandboxApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>				
		
		<!-- Applet: Search -->
		<!--
		<div id="SearchApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>
		-->		
		
	</div>
	
	<!-- Application: Content -->
	<div id="ContentApplication_SlidersSectorTemplate">
		
		<!-- Applet: Web Forms -->
		<div id="WebFormsApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>		
		
		<!-- Applet: Images -->
		<div id="ImagesApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>

		<!-- Applet: Videos -->
		<div id="VideosApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>
				
		<!-- Applet: Sandbox -->
		<div id="SandboxApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>				
		
		<!-- Applet: Search -->
		<!--
		<div id="SearchApplet_Slider" class="ASSSlider">
			<div class="ASSSliderDropper">&nbsp;</div>
			<div class="ASSSliderContent">
				<div class="ASSSliderHeader">
	  					<div class="ASSToggleImage">&nbsp;</div>
	  					<div class="ASSHeaderTitle"></div>
				</div>
				<div class="ASSSliderData"></div>
			</div>
		</div>
		-->			

	</div>
	
	<!-- Window Template -->
	<div id="AlfrescoWindowTemplate" class="Active alf-window">

		<!--Borders&HeaderBg-->
		<div id="aw-top-left-corner" class="window-tl"> </div>
		<div id="aw-top-right-corner" class="window-tr"> </div>
		<div id="aw-header-bg" class="window-header"> </div>
		<div id="aw-bottom-left-corner" class="window-bl"> </div>
		<div id="aw-bottom-right-corner" class="window-br"> </div>
		<div id="aw-left-border" class="alf-window-ml"> </div>
		<div id="aw-right-border" class="alf-window-mr"> </div>
		<div id="aw-bottom-border" class="window-bottom"> </div>
		
		<!--Header-->
		<div id="aw-icon" class="AWIDefaulth">&nbsp;</div>
		<div id="aw-title-div">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td nowrap="nowrap" id="aw-title" class="header-text"></td>
				</tr>
			</table>
		</div>
		<div id="aw-button-close" class="AWBDefault_cl">&nbsp;</div>
		<div id="aw-button-minimize" class="AWBDefault_min">&nbsp;</div>
		<div id="aw-button-maximize" class="AWBDefault_max">&nbsp;</div>
		<div id="aw-body" style="overflow : auto;">
			<table style="width:100%; height : 100%" cellpadding="0" cellspacing="0">
				<tr>
					<td id="aw-body-cell" style="vertical-align: middle;" align="center" class="window-body">
						<div id="aw-body-content" style="overflow : hidden;"></div>
					</td>
				</tr>
			</table>
		</div>
	</div>


	<!-- Instantiated Controls are bound here -->
	<div id="ControlInstances"></div>
	
	
	<!-- Reusable Menu -->
	<div id="ALVMenuTemplate" class="AMHorizontalHolder">
		<div class="AMRoot">
			<table height="21" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td width="1px;" class="AMItemLeft">&nbsp;</td>
					<td class="AMItemCenter" valign="left">
					   <img src="<%=rootPath%>/images/buttons/add.gif" style="padding-right: 2px">
					</td>
					<td class="AMItemCenter" valign="middle" style="font-size:10px">Add</td>
					<td width="1px;" class="AMItemRight">&nbsp;</td>
			    </tr>
			</table>
		</div>
		<div class="AMRoot">
			<table height="21" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td width="1px;" class="AMItemLeft">&nbsp;</td>
					<td class="AMItemCenter" valign="left">
						<img src="<%=rootPath%>/images/buttons/edit.png" style="padding-right: 2px">
					</td>
					<td class="AMItemCenter" valign="middle" style="font-size:10px">Edit</td>
					<td width="1px;" class="AMItemRight">&nbsp;</td>
				</tr>
			</table>
		</div>
		<div class="AMRoot">
			<table height="21" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td width="1px;" class="AMItemLeft">&nbsp;</td>
					<td class="AMItemCenter" valign="left">
						<img src="<%=rootPath%>/images/buttons/copy.gif" style="padding-right: 2px">
					</td>
					<td class="AMItemCenter" valign="middle" style="font-size:10px">Copy</td>
					<td width="1px;" class="AMItemRight">&nbsp;</td>
				</tr>
			</table>
		</div>
		<div class="AMRoot">
			<table height="21" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td width="1px;" class="AMItemLeft">&nbsp;</td>
					<td class="AMItemCenter" valign="left">
						<img src="<%=rootPath%>/images/buttons/delete.gif" style="padding-right: 2px">
					</td>
					<td class="AMItemCenter" valign="middle" style="font-size:10px">Remove</td>
					<td width="1px;" class="AMItemRight">&nbsp;</td>
				</tr>
			</table>
		</div>
	</div>
	
	
	<!-- Control Templates -->
	<div id="AlfrescoTemplatesViewTemplate" class="AlfrescoTemplatesView">
        <div id="ALVMenu"></div>
		<div id="ALVInstances"></div>				
	</div>
	<div id="AlfrescoSearchViewTemplate">
		<div id="ASVBody"></div>
	</div>
	<div id="AlfrescoTreeViewNavigationTemplate" class="AlfrescoTreeView">
		<div id="ATVMenuNavigation">
			<div id="ATVMenuTemplateNavigation" class="AMHorizontalHolder">
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
							   <img src="<%=rootPath%>/images/buttons/add.gif" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Add</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
					    </tr>
					</table>
				</div>
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
								<img src="<%=rootPath%>/images/buttons/edit.png" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Edit</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
						</tr>
					</table>
				</div>
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
								<img src="<%=rootPath%>/images/buttons/copy.gif" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Copy</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
						</tr>
					</table>
				</div>
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
								<img src="<%=rootPath%>/images/buttons/delete.gif" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Remove</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div id="ATVTreeNavigation"></div>
	</div>	
	<div id="AlfrescoTreeViewWebComponentsTemplate" class="AlfrescoTreeView">
		<div id="ATVMenuWebComponents">
			<div id="ATVMenuTemplateWebComponents" class="AMHorizontalHolder">
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
								<img src="<%=rootPath%>/images/buttons/findmore.gif" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Find More</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div id="ATVTreeWebComponents"></div>
	</div>	
	<div id="AlfrescoTreeViewWebContentTemplate" class="AlfrescoTreeView">
		<div id="ATVMenuWebContent">
			<div id="ATVMenuTemplateWebContent" class="AMHorizontalHolder">
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
							   <img src="<%=rootPath%>/images/buttons/add.gif" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Upload</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
					    </tr>
					</table>
				</div>
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
								<img src="<%=rootPath%>/images/folder_large.png" width="16px" height="16px" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Folder</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
						</tr>
					</table>
				</div>
				<div class="AMRoot">
					<table height="21" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td width="1px;" class="AMItemLeft">&nbsp;</td>
							<td class="AMItemCenter" valign="middle">
								<img src="<%=rootPath%>/images/buttons/delete.gif" style="padding-right: 2px">
							</td>
							<td class="AMItemCenter" valign="middle" style="font-size:10px">Remove</td>
							<td width="1px;" class="AMItemRight">&nbsp;</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
		<div id="ATVTreeWebContent"></div>
	</div>
	<div id="AlfrescoTreeViewSitesTemplate" class="AlfrescoTreeView">
		<div id="ATVTreeSites"></div>
	</div>
	<div id="AlfrescoTreeViewSpacesTemplate" class="AlfrescoTreeView">
		<div id="ATVTreeSpaces"></div>
	</div>
	<div id="AlfrescoContentViewTemplate" class="AlfrescoContentView">
		<div id="ACVMenu"></div>
		<div id="ACVMenuTemplate" class="AMHorizontalHolder">
			<div class="AMRoot">
				<table height="21" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td width="1px;" class="AMItemLeft">&nbsp;</td>
						<td class="AMItemCenter" valign="middle">
						   <img src="<%=rootPath%>/images/buttons/options.gif" style="padding-right: 2px">
						</td>
						<td class="AMItemCenter" valign="middle" style="font-size:10px">Preview</td>
						<td width="1px;" class="AMItemRight">&nbsp;</td>
				    </tr>
				</table>
			</div>
		</div>
		<div id="ACVBody" class="scrollBody">
			<div class="scrollContainer"></div>
		</div>				
	</div>

		
	<!--  Alfresco Template Designer Template -->
	<div id="_AlfrescoTemplateDesigner">
		<div id="_AlfrescoTemplateDesignerFrame" class="AlfrescoTemplateDesignerFrame" />
		<div id="_AlfrescoTemplateDesignerEditor" class="AlfrescoTemplateDesignerEditor" />
	</div>
	<div id="_TemplateRegionTemplate" class="AlfrescoTemplateRegion"/>	

	<!--  Alfresco Dashboards -->
	<div id="_AlfrescoDashboard">
		<div id="_AlfrescoDashboardFrame" class="AlfrescoDashboardFrame" />
		<div id="_AlfrescoDashboardEditor" class="AlfrescoDashboardEditor" />
	</div>
	<div id="_DashletTemplate" class="AlfrescoDashlet"/>	
	
	<!--  Site Administrator Template -->
	<div id="AlfrescoSiteAdministratorViewTemplate">
		<div id="ASABody"></div>
	</div>

	<!--  Page Blocker -->
	<div id="_AlfrescoPageBlocker">
		<div id="_AlfrescoPageBlockerFrame" class="AlfrescoPageBlockerFrame"></div>
		<div id="_AlfrescoPageBlockerEditor" class="AlfrescoPageBlockerEditor"></div>
	</div>

	
	
	<!--  LOGIN DIALOG -->
	<div id="AlfrescoWebStudioLoginTemplate" class="login-window">
		<div id="AlfrescoWebStudioLoginTemplatePanel" class="login-panel">
			<div class="login-logo"></div>
			<form accept-charset="UTF-8">
				<fieldset class="login-fieldset">
					<div style="padding-top:96px">
						<span id="txt-username">Login Name</span>
					</div>
					<div style="padding-top:4px">
						<input type="text" id="AlfrescoWebStudioLoginTemplateUsername" maxlength="256" style="width:200px" class="login-input"/>
					</div>
					<div style="padding-top:12px">
						<span id="txt-password">Password</span>
					</div>
					<div style="padding-top:4px">
						<input type="password" id="AlfrescoWebStudioLoginTemplatePassword" maxlength="256" style="width:200px" class="login-input"/>
					</div>
					<div style="padding-top:16px">
						<input type="button" value="Login" id="AlfrescoWebStudioLoginTemplateLogin" class="login-button" />
					</div>
					<div style="padding-top:32px">
						<span class="login-copyright">
							&copy; 2005-2009 Alfresco Software Inc. All rights reserved.
						</span>
					</div>
				</fieldset>
			</form>
		</div>
	</div>


	<!--  SANDBOX DIALOG -->
	<div id="AlfrescoWebStudioSandboxTemplate" class="sandbox-window">
		<div id="AlfrescoWebStudioSandboxTemplatePanel" class="sandbox-panel">
			<div class="sandbox-header">
				<table width="100%" cellpadding="0" cellspacing="0">
					<tr>
						<td>
							<img src="<%=rootPath%>/images/dialogs/sandbox-dialog-large.gif"/>
						</td>
						<td valign="center" width="100%" style="padding-left: 5px">
							Web Site Wizard
						</td>
					</tr>
				</table>
			</div>
			<form accept-charset="UTF-8">
				<fieldset class="sandbox-fieldset">
					<div style="padding-top:44px" class="sandbox-info">
					</div>
					<div id="create-web-site">
						<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<img id="create-web-site-img" src="<%=rootPath%>/images/arrows/arrow_closed.gif"/>
							</td>
							<td valign="center" width="100%" style="padding-left: 5px">
								Create a New Web Site
							</td>
						</tr>		
						</table>				
					</div>
					<div id="create-web-site-body" style="display: none">
						<table width="100%" class="sandbox-table" cellpadding="0" cellspacing="0">
							<tr>
								<td style="padding-right: 1px black solid" valign="top">
								
									<table width="100%" style="padding-top: 5px; padding-left: 10px">
									
										<tr>
											<td colspan="2" align="left">
												Web Site Name
											</td>
										</tr>
										<tr>
											<td colspan="2" align="right" style="padding-right: 20px">
												<input type="text" id="AlfrescoWebStudioSandboxTemplate_WebSiteName" maxlength="256" style="width:200px" class="sandbox-input"/>
											</td>
										</tr>
										<tr>
											<td colspan="2" align="left">
												Based on the following:
											</td>
										</tr>
										<tr>
											<td id="AlfrescoWebStudioSandboxTemplate_BasedOn" colspan="2" align="right" style="padding-right: 20px">
											</td>
										</tr>
										<tr><td colspan="2"><br/></td></tr>
										<tr>
											<td colspan="2" align="right" style="padding-right: 20px">
												<input type="button" value="Create" id="AlfrescoWebStudioSandboxTemplate_Create" class="sandbox-button" />
											</td>
										</tr>
									</table>
								</td>
								<td width="160px" height="140px" align="center" valign="middle">
									<img id="AlfrescoWebStudioSandboxTemplate_Image" src=""/>
								</td>
							</tr>
						</table>
					</div>					
					<div id="load-web-site">
						<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<img id="load-web-site-img" src="<%=rootPath%>/images/arrows/arrow_closed.gif"/>
							</td>
							<td valign="center" width="100%" style="padding-left: 5px;">
								Load an Existing Web Site
							</td>
						</tr>		
						</table>				
					</div>
					<div id="load-web-site-body" style="display: none">
						<div id="load-web-site-search-results" style="height: 132px; overflow: auto; border: 1px gray solid"></div>
						<input type="button" value="Load" id="AlfrescoWebStudioSandboxTemplate_Load" class="sandbox-button" style="margin-top: 8px"/>
						<input id="load-web-site-selected" type="hidden"/>
					</div>
					<div style="padding-top:4px">
						<span class="sandbox-copyright">
							&copy; 2005-2009 Alfresco Software Inc. All rights reserved.
						</span>
					</div>
				</fieldset>
			</form>
		</div>
	</div>
	
	<!-- Content Type Associations Dialog -->
	<div id="ContentTypeAssociationsDialog">
		<div id="ContentTypeAssociationsPanel" height="300px">
			<div class="hd">Content Type Associations</div>
			<div class="bd" height="100%">
				<div id="ContentTypeAssociationsPanelDataTable"></div>
				<div>
					<br/>
		            <input class="yui-button" id="newassociationbutton" type="button" value="New Association" />
		            <!--
		            <input class="yui-button" id="editassociationbutton" type="button" value="Edit Association" />
		            -->
		            <input class="yui-button" id="removeassociationbutton" type="button" value="Remove Association" />
				</div>
			</div>
			<div class="ft"></div>
		</div>
	</div>
	
	<!-- Page Template Associations Dialog -->
	<div id="PageTemplateAssociationsDialog">
		<div id="PageTemplateAssociationsPanel" height="300px">
			<div class="hd">Template Associations</div>
			<div class="bd" height="100%">
				<div id="PageTemplateAssociationsPanelDataTable"></div>
				<div>
					<br/>
		            <input class="yui-button" id="pta_newbutton" type="button" value="Associate a Template" />
		            <!--
		            <input class="yui-button" id="pta_editbutton" type="button" value="Edit this Association" />
		            -->
		            <input class="yui-button" id="pta_removebutton" type="button" value="Remove this Association" />
				</div>
			</div>
			<div class="ft"></div>
		</div>
	</div>	
</div>