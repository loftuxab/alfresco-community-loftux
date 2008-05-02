<h2>Post Result</h2>

<ul>
<#list formdata.fields as field>
<li>${field.name} = ${field.value}</li>
</#list>
</ul>

<div style='padding-top: 20px;'><a href="javascript:history.back();">Back to form</a></div>

