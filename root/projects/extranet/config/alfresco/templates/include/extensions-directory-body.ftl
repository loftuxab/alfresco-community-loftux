<#macro body>

<#if alfEnterprise == true>
<!-- Default to Community for now -->
	<@communitybody/>
<#else>
	<@communitybody/>
</#if>

</#macro>

<#macro enterprisebody>

<!-- Placeholder for eventual Enterprise-only features -->

</#macro>

<#macro communitybody>

<!-- narrow column -->
<div class="yui-b">

Lorem ipsum odio mazim vulputate sed et, qui ei kasd malis quaerendum. Quidam vidisse eos in, ei vis adhuc honestatis, has te ridens probatus consequuntur. Habeo vocibus sapientem at eos, sed amet justo gloriatur ut, veri volumus delicatissimi ut mel. An ius nibh elit euripidis. Quod dicam ut qui. Mundi dolores sed et, copiosae platonem mel ei, pri ei sint altera consequuntur.
<br/>
Eos amet mollis inciderint ea, meis oportere democritum eos ad. Ex quem decore consequat quo, sed labores rationibus dissentiet ex. Ad per dolorem ancillae. Duo ut legere vituperata disputando, agam eruditi appareat sea eu.

			
</div>



<!-- wide column -->
<div id="yui-main">
	<div class="yui-b">
	
		<@region id="alfresco-extensions" scope="global"/>
	
	</div>
</div>
</#macro>

<#macro renderCategoryBrowser>

</#macro>

<#macro renderExtensionSummary>

</#macro>

<#macro renderExtensionDetails>

</#macro>