<p align="center">
<br/>
<br/>
Please enter your credentials to log in:
<br/>
<br/>

<form method="POST" action="${url.context}/login">
<table align="center">
	<tr>
		<td>User name:</td>
		<td><input type="text" name="username"/></td>
	</tr>
	<tr>
		<td>Password:</td>
		<td><input type="password" name="password"/></td>
	</tr>
	<tr>
		<td></td>
		<td><input type="submit" value="Login"/></td>
	</tr>
</table>
<input type="hidden" name="success" value="${successUrl}"/>
<input type="hidden" name="failure" value="<@link pageType='login'/>"/>
</form>

<br/>
<br/>

</p>