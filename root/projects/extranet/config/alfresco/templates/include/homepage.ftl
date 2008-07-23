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
	<@region id="alfresco-communities" scope="global"/>
	<br/>
	<@region id="alfresco-events" scope="global"/>
	<br/>
	<@region id="alfresco-top-contributors" scope="global"/>
	<br/>
	<@region id="alfresco-popular-extensions" scope="global"/>

</div>

</#macro>

<#macro communitybody>

<!-- wide column -->
<div id="yui-main">
	<div class="yui-b">
	
		<!-- Guided Section -->
		<ol class="path">
			<li><a href="#">Download</a></li>
			<li><a href="#">Install</a></li>
			<li><a href="#">Make it Your Own</a></li>
			<li class="last"><a href="#">Participate</a></li>
		</ol>
		
		<!-- Community Welcome -->
		<h1>Welcome to the Alfresco Community!</h1>
		<p class="bigger">Whether you are looking to use Alfresco out-of-the-box or you are a hard-core programmer planning to develop a custom content management application, you've come to the right place. Visit the <a href="#">Alfresco Discovery Center</a> to get information about what you can do with Alfresco or if you're ready to get started, click on one of the links below.</p>
		
		<!-- Community News -->
		<@region id="community-news" scope="global"/>
		
		<!--
		<h2>Community News</h2>
		<div class="yui-g">
		
			<div id="doclist">
			    <@region id="community-news" scope="global"/>
			</div>
		
		</div>
		-->
		
		<@region id="enterprise-product-news" scope="global"/>

		<h2>Activity Feed</h2>
		<ul>
			<li><a href="#">Mike F added 'DM Roadway' 9 AM today.</a></li>
			<li><a href="#">Dave C added 'New API Handbook' 6 AM, 12 May</a></li>
			<li><a href="#">John N updated 'Roadmap' 2 PM, 10 May</a></li>
		</ul>

		<h2>Additional Community Resources</h2>
		<ul>
			<li><a href="#">Source Code</a></li>
			<li><a href="#">Roadmap</a></li>
			<li><a href="#">Training</a></li>
		</ul>

		<div class="rightalign">
			<a href="#"><img src="images/add.gif" alt="" /></a>
		</div>
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
							Community Stats
							<ul>
								<li>&gt; 1.3 million downloads</li>
								<li>&gt; 50,000 registered members</li>
							</ul>
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