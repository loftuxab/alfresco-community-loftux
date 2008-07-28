<#if success == false>
	<script>
		window.location.href="${url.context}?f=default&pt=changepassword&message=${message}";
	</script>
<#else>
	User password was successfully updated.
	<br/>
	<br/>
	<a href="/extranet/?pt=viewprofile">Return to Alfresco Network</a>
</#if>