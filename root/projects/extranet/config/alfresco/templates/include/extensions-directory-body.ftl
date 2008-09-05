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

<!-- TODO -->
			
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