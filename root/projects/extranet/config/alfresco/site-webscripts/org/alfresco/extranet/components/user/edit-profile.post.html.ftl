<#if success == false>
<script>
	${message}
	<br/>
	<br/>
	<a href="${url.full}">Go back</a>
</script>
<#else>
	Your profile was successfully updated.
	<br/>
	<br/>
	<a href="/extranet/?pt=viewprofile">Return to Alfresco Network</a>
</#if>