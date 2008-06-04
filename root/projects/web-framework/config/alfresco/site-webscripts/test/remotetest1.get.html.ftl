<div>This is a test of the remote client, results:</div>
<pre>
Response Code: ${result.status.code}
<#if result.status.code != 200>
Response Error Message: ${result.status.message}
<#else>
${result.response?html}
</#if>
</pre>
