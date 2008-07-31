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

	<!-- Login Component -->
	<div class="roundbox">
		<div class="inner1">
			<div class="inner2">
				<div class="inner3">
					<div class="inner4">
						<form class="login-form" action="/extranet/login">
							<strong>New to the Community?</strong> Join Now for free access to Documentation, Webinars, White Papers, and more.
							<ul class="form-holder">
								<li><label>User Name:</label> <input type="text" class="text" name="username" /></li>
								<li><label>Password:</label> <input type="password" class="text" name="password" /></li>
								<li><input type="submit" class="button" value="Login" /> <a href="?pt=lostpassword">Lost your password?</a></li>
							</ul>
						</form>
					</div>
				</div>
			</div>
		</div>
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