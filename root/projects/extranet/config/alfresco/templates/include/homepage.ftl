<#macro body>

<#if alfEnterprise == true>
	<@enterprisebody/>
<#else>
	<@communitybody/>
</#if>

</#macro>

<#macro enterprisebody>

<!-- wide column -->
<div id="yui-main">
	<div class="yui-b">
	
		<@region id="enterprise-welcome" scope="global"/>
		<br/>
		<@region id="enterprise-product-updates" scope="global"/>
		<br/>		
		<@region id="enterprise-product-news" scope="global"/>
		
	</div>
</div>

<!-- narrow column -->
<div class="yui-b">

	<@region id="networknews" scope="global"/>
	<br/>
	<@region id="alfresco-events" scope="global"/>

</div>

</#macro>

<#macro communitybody>

<!-- wide column -->
<div id="yui-main">
	<div class="yui-b">
		
		<!-- Community Welcome -->
		<@region id="community-welcome" scope="global"/>
				
		<!-- Community News -->
		<@region id="community-news" scope="global"/>
		
		<!-- Product News -->
		<@region id="enterprise-product-news" scope="global"/>

		<!-- Activity Tracking -->
		<@region id="activity-tracking" scope="global"/>

	</div>
</div>

<!-- narrow column -->
<div class="yui-b">

	<!-- Download Button -->
	<div>

<a href="http://wiki.alfresco.com/wiki/Installing_Labs_3"><img src="images/download-button-labs.png" alt="Download Alfresco Labs" width="300" height="36" style="padding-bottom:10px;"/></a>

	</div>
	
	<!-- Community Downloads -->
	<div class="roundbox gradient">
		<div class="inner1">
			<div class="inner2">
				<div class="inner3">
					<div class="inner4">
						<div class="txt-box">
						
							<!-- Community Welcome -->
							<@region id="community-stats" scope="global"/>

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<@region id="alfresco-communities" scope="global"/>
	<br/>
	<@region id="alfresco-events" scope="global"/>
	<br/>
	<@region id="alfresco-top-contributors" scope="global"/>
	<br/>
	<@region id="alfresco-popular-extensions" scope="global"/>

</div>

</#macro>