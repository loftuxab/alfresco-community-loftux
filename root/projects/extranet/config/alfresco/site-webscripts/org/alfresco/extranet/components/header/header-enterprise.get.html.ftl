<div class="wrap">
	<a href="${url.context}"><img src="${path}/logo.gif" class="logo2" alt=""/></a>

<#if user.id == "guest">	
	<ul id="top-links">
		<li><a href="?pt=login">Sign In</a></li>
		<li><a href="?pt=register">Register</a></li>
		<li><a href="mailto:support@alfresco.com">Contact Us</a></li>
	</ul>
<#else>
	<span id="top-user">
		You are logged in as 	
		<img src="${url.context}/images/extranet/user_16.gif"/>
		&nbsp;<b>${user.fullName}</b> ( <b>${user.id}</b> )	
	</span>
	<br/><br/>
	<ul id="top-links2">
		<li>
			<img src="${url.context}/images/extranet/logout_16.gif"/>
			<a href="?pt=logout">Log out</a>
		</li>
		<li>
			<img src="${url.context}/images/extranet/my_account_16.gif"/>
			<a href="?pt=viewprofile">My Account</a>
		</li>
		<li>
			<img src="${url.context}/images/extranet/contact_us_16.gif"/>
			<a href="mailto:support@alfresco.com">Contact Us</a>
		</li>
	</ul>

</#if>

</div>
