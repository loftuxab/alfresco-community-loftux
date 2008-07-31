<div class="wrap">
	<a href="${url.context}"><img src="${path}/logo.gif" class="logo" title="Alfresco Network - Community Beta"/></a>

<#if user.id == "guest">	
	<ul id="top-links">
		<li>
			<img src="${url.context}/images/extranet/login_16.gif"/>
			<a href="?pt=login">Sign In</a>
		</li>
		<li>
			<img src="${url.context}/images/extranet/register_16.gif"/>
			<a href="?pt=register">Register</a>
		</li>
		<li>
			<img src="${url.context}/images/extranet/contact_us_16.gif"/>
			<a href="mailto:support@alfresco.com">Contact Us</a>
		</li>
	</ul>	
	
<#else>
	<p style="padding: 0.300em 0 0.224em; float:right;font: 12px/14px 'Trebuchet MS', Arial, sans-serif;">
	You are logged in as 
	
	<img src="${url.context}/images/extranet/user_16.gif"/>
	&nbsp;<b>${user.fullName}</b> ( <b>${user.id}</b> )
	
	</p>
	<br/>
	
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

	<br/>
	<br/>
	<br/>
	<p><i><font color="gray" size="2">Beta</font></i></p>

</#if>

</div>