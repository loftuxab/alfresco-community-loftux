<#import "alfresco-template.ftl" as template />
<@template.header />
<@template.body>
<div id="alflogin">
   <form accept-charset="UTF-8" method="POST" action="${url.context}/login">
   <table align="center">
   	<tr>
   		<td>Username:</td>
   		<td><input type="text" id="username" name="username" maxlength="256" style="width:150px" /></td>
   	</tr>
   	<tr>
   		<td>Password:</td>
   		<td><input type="password" id="password" name="password" maxlength="256" style="width:150px" /></td>
   	</tr>
   	<tr>
   		<td></td>
   		<td><input type="submit" value="Login"/></td>
   	</tr>
   </table>
   <input type="hidden" name="success" value="<#if alfRedirectUrl?exists>${alfRedirectUrl}<#else>${url.context}</#if>"/>
   <input type="hidden" name="failure" value="<@link pageType='login'/>"/>
   </form>
</div>
</@>