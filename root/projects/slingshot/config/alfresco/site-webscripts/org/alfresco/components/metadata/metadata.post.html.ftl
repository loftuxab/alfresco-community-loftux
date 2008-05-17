<#if formdata?exists>
<h2>Post Result (multipart/form-data)</h2>

<ul>
<#list formdata.fields as field>
<li>${field.name} = ${field.value}</li>
</#list>
</ul>

<#elseif json?exists>
${json}

<#else>
<h2>Post Result (application/x-www-form-urlencoded)</h2>

<ul>
<#list argsM?keys as arg>
<#list argsM[arg] as val>
<li>${arg} = ${val}</li>
</#list>
</#list>
</ul>

</#if>

<#if !json?exists>
<div style='padding-top: 20px;'><a href="javascript:history.back();">Back to form</a></div>
</#if>
