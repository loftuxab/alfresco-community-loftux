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
		<!--
		<ol class="path">
			<li><a href="#">Download</a></li>
			<li><a href="#">Install</a></li>
			<li><a href="#">Make it Your Own</a></li>
			<li class="last"><a href="#">Participate</a></li>
		</ol>
		-->
		<h1>Welcome to the Alfresco Enterprise Network!</h1>
		<p class="bigger">Whether you are looking to use Alfresco out-of-the-box or you are a hard-core programmer planning to develop a custom content management application, you've come to the right place. Visit the <a href="#">Alfresco Discovery Center</a> to get information about what you can do with Alfresco or if you're ready to get started, click on one of the links below.</p>
		<h2>Enterprise News</h2>
		<div class="yui-g">
			<div class="yui-u first">
				<div class="box">
					<img src="images/pic1.jpg" alt="" />
					<h3>Contributor of the Month</h3>
					We are pleased to recognize Romain Guinot as our May Contributor of the Month for his tremendous value on the French language forums.
					<a href="#">Read more</a>
				</div>
				<div class="box">
					<img src="images/pic3.jpg" alt="" />
					<h3>The Chumby Awards</h3>
					Make yourself useful and win a Chumby.
					<a href="#">Learn how</a>
				</div>
			</div>
			<div class="yui-u">
				<div class="box">
					<img src="images/pic2.jpg" alt="" />
					<h3>Web Script Challenge</h3>
					Doing some cool things with web scripts? All valid entries get an Alfresco T-Shirt and the winner an engraved 16GB Apple iPod Touch!
					<a href="#">Enter now</a>
				</div>
				<div class="box">
					<img src="images/pic4.jpg" alt="" />
					<h3>Featured Contribution</h3>
					An Alfresco plugin to BlueXML Developer Studio allows you to generate Alfresco applications very quickly.
					<a href="#">Learn how</a>
				</div>
			</div>
		</div>
		<h2>Product News</h2>
		<ul>
			<li><a href="#">Alfresco 3.0 Wiki Pages to help get you up-to-speed on our next major release</a> (15 May)</li>
			<li><a href="#">Alfresco 2.2.0 Enterprise Now Available</a> (18 Apr)</li>
			<li><a href="#">Alfresco Community 2.9B Labs release available for download</a> (9 Dec 2007)</li>
		</ul>
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
	<!--
	<div class="roundbox">
		<div class="inner1">
			<div class="inner2">
				<div class="inner3">
					<div class="inner4">
						<form class="login-form" action="#">
							<strong>New to the Community?</strong> Join Now for free access to Documentation, Webinars, White Papers, and more.
							<ul class="form-holder">
								<li><label>User Name:</label> <input type="text" class="text" /></li>
								<li><label>Password:</label> <input type="password" class="text" /></li>
								<li><input type="button" class="button" value="Login" /> <a href="#">Lost your password?</a></li>
							</ul>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
	-->
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
	<h3>Other Alfresco Communities</h3>
	<div class="box">
		<img src="images/facebook.gif" alt="" />
		<div class="txt">
			<a href="#">Join our Facebook Community</a>
		</div>
	</div>
	<h3>Related Events &amp; Webinars</h3>
	<div class="box">
		<img src="images/img1.gif" alt="" />
		<div class="txt">
			<a href="#">JavaOne Conference</a><br />
			6-9 May 2008<br />
			San Francisco, CA
		</div>
	</div>
	<div class="box">
		<img src="images/img2.gif" alt="" />
		<div class="txt">
			<a href="#">Alfresco iPhone Integration Webinar</a><br />
			3 April 2008<br />
			12pm EDT
		</div>
	</div>
	<h3>Top Forum Members</h3>
	<div class="box">
		<img src="images/photo1.jpg" alt="" />
		<div class="txt">
			<a href="#">Nancy Garrity</a>
		</div>
	</div>
	<div class="box">
		<img src="images/photo2.jpg" alt="" />
		<div class="txt">
			<a href="#">Ian Howells</a>
		</div>
	</div>
	<div class="box">
		<img src="images/photo3.jpg" alt="" />
		<div class="txt">
			<a href="#">David Sadowski</a>
		</div>
	</div>
	<h3>Popular Extensions</h3>
	<div class="box">
		<img src="images/img3.gif" alt="" />
		<div class="txt">
			<ul>
				<li><a href="#">Email User Password</a></li>
				<li><a href="#">Alfresco Rights Viewer</a></li>
				<li><a href="#">Google Mashup</a></li>
			</ul>
		</div>
	</div>
</div>

</#macro>

<#macro communitybody>

<!-- wide column -->
<div id="yui-main">
	<div class="yui-b">
		<ol class="path">
			<li><a href="#">Download</a></li>
			<li><a href="#">Install</a></li>
			<li><a href="#">Make it Your Own</a></li>
			<li class="last"><a href="#">Participate</a></li>
		</ol>
		<h1>Welcome to the Alfresco Community!</h1>
		<p class="bigger">Whether you are looking to use Alfresco out-of-the-box or you are a hard-core programmer planning to develop a custom content management application, you've come to the right place. Visit the <a href="#">Alfresco Discovery Center</a> to get information about what you can do with Alfresco or if you're ready to get started, click on one of the links below.</p>
		<h2>Community News</h2>
		<div class="yui-g">
			<div class="yui-u first">
				<div class="box">
					<img src="images/pic1.jpg" alt="" />
					<h3>Contributor of the Month</h3>
					We are pleased to recognize Romain Guinot as our May Contributor of the Month for his tremendous value on the French language forums.
					<a href="#">Read more</a>
				</div>
				<div class="box">
					<img src="images/pic3.jpg" alt="" />
					<h3>The Chumby Awards</h3>
					Make yourself useful and win a Chumby.
					<a href="#">Learn how</a>
				</div>
			</div>
			<div class="yui-u">
				<div class="box">
					<img src="images/pic2.jpg" alt="" />
					<h3>Web Script Challenge</h3>
					Doing some cool things with web scripts? All valid entries get an Alfresco T-Shirt and the winner an engraved 16GB Apple iPod Touch!
					<a href="#">Enter now</a>
				</div>
				<div class="box">
					<img src="images/pic4.jpg" alt="" />
					<h3>Featured Contribution</h3>
					An Alfresco plugin to BlueXML Developer Studio allows you to generate Alfresco applications very quickly.
					<a href="#">Learn how</a>
				</div>
			</div>
		</div>
		<h2>Product News</h2>
		<ul>
			<li><a href="#">Alfresco 3.0 Wiki Pages to help get you up-to-speed on our next major release</a> (15 May)</li>
			<li><a href="#">Alfresco 2.2.0 Enterprise Now Available</a> (18 Apr)</li>
			<li><a href="#">Alfresco Community 2.9B Labs release available for download</a> (9 Dec 2007)</li>
		</ul>
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
	<div class="roundbox">
		<div class="inner1">
			<div class="inner2">
				<div class="inner3">
					<div class="inner4">
						<form class="login-form" action="#">
							<strong>New to the Community?</strong> Join Now for free access to Documentation, Webinars, White Papers, and more.
							<ul class="form-holder">
								<li><label>User Name:</label> <input type="text" class="text" /></li>
								<li><label>Password:</label> <input type="password" class="text" /></li>
								<li><input type="button" class="button" value="Login" /> <a href="#">Lost your password?</a></li>
							</ul>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
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
	<h3>Other Alfresco Communities</h3>
	<div class="box">
		<img src="images/facebook.gif" alt="" />
		<div class="txt">
			<a href="#">Join our Facebook Community</a>
		</div>
	</div>
	<h3>Related Events &amp; Webinars</h3>
	<div class="box">
		<img src="images/img1.gif" alt="" />
		<div class="txt">
			<a href="#">JavaOne Conference</a><br />
			6-9 May 2008<br />
			San Francisco, CA
		</div>
	</div>
	<div class="box">
		<img src="images/img2.gif" alt="" />
		<div class="txt">
			<a href="#">Alfresco iPhone Integration Webinar</a><br />
			3 April 2008<br />
			12pm EDT
		</div>
	</div>
	<h3>Top Forum Members</h3>
	<div class="box">
		<img src="images/photo1.jpg" alt="" />
		<div class="txt">
			<a href="#">Nancy Garrity</a>
		</div>
	</div>
	<div class="box">
		<img src="images/photo2.jpg" alt="" />
		<div class="txt">
			<a href="#">Ian Howells</a>
		</div>
	</div>
	<div class="box">
		<img src="images/photo3.jpg" alt="" />
		<div class="txt">
			<a href="#">David Sadowski</a>
		</div>
	</div>
	<h3>Popular Extensions</h3>
	<div class="box">
		<img src="images/img3.gif" alt="" />
		<div class="txt">
			<ul>
				<li><a href="#">Email User Password</a></li>
				<li><a href="#">Alfresco Rights Viewer</a></li>
				<li><a href="#">Google Mashup</a></li>
			</ul>
		</div>
	</div>
</div>

</#macro>