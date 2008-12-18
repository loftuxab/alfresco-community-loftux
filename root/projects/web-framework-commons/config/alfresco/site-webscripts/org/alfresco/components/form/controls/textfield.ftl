<label>${item.label?html}:</label>

<#if form.mode == "view">
&nbsp;${form.data["${item.name}"]}
<#else>
<input type="text" size="20" maxlength="1024" name="${item.name}" value="${form.data["${item.name}"]}" />
</#if>