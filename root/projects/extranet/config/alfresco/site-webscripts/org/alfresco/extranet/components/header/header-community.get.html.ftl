<div class="wrap">
	<a href="${url.context}"><img src="${path}/logo.gif" class="logo" alt=""/></a>

<#if user.id == "guest">	
	<ul id="top-links">
		<li><a href="?pt=login">Sign In</a></li>
		<li><a href="?pt=register">Register</a></li>
		<li><a href="#">Contact Us</a></li>
	</ul>
<#else>
	<ul id="top-links">
		<li align="right" style="font: 11px/14px 'Trebuchet MS', Arial, sans-serif">Welcome, <b>${user.fullName}</b></li>
		<li><a href="?pt=logout">Log out</a></li>
		<li><a href="?pt=viewprofile">My Account</a></li>
		<li><a href="#">Contact Us</a></li>
	</ul>
</#if>

	<form action="#" class="search">
		Search
		<select>
			<option>alfresco.com</option>
		</select>
		for
		<input type="text" class="text" />
		<input type="submit" class="button" value="Go" />
	</form>
</div>
