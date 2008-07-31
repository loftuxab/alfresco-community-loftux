<#macro renderItem item>
<h4><a href="${item.link}">${item.title}</a></h4>
<div>${item.description}</div>
<#if item.attachment??>
<div><img src="${url.context}/images/components/generic-file-32.png"/><a href="${item.attachment.url}">${item.attachment.name}</a></div>
</#if>
<br />
</#macro>