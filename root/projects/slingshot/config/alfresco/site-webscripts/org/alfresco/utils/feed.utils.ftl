<#macro renderItem item>
<p>
<h4><a href="${item.link}">${item.title}</a></h4>
${item.description}
</p>
<#if item.attachment??>
<div><img src="${url.context}/images/filetypes32/${item.attachment.type}.gif"/><a href="${item.attachment.url}">${item.attachment.name}</a></div>
</#if>
</#macro>