<#if user?exists>

	<#assign userId = user.properties["id"]>
	
	<#if userId == "guest">
		<p>
			<@anchor pageType="login">LOG IN</@anchor> to access premium content or to contribute.
			<br>
			Not registered?
			<@anchor pageType="register">Register Now</@anchor>
		</p>
	<#else>
		<p>
			Welcome, ${userId}, you are logged in!
			<br/>
			<@anchor pageType="manage-account">Manage my account</@anchor>
			<br/>
			<@anchor pageType="logout">Log out...</@anchor>
		</p>
	</#if>
</#if>
