<html>
<body>
<B>Web Project Wizard</B>

<br/>
<br/>
<#if report?exists>
	<font color="#0000bb">${report}</font>
	<br/>
	<br/>
</#if>

Select a Web Project that you would like to prepare for Alfresco Dynamic Website:
<br/>
<br/>

<form action="/alfresco/service/ads/webprojectwizard" method="GET">

<#list webProjects as webProject>
<input type="radio" name="webProjectId" value="${webProject.id}">${webProject.name}</input>
<br/>
</#list>

<br/>
<input type="submit" name="submit" value="Prepare"/>

</form>
</body>
</html>