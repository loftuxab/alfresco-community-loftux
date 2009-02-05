<#if item.value?exists>
<#assign isTrue=item.value>
<#else>
<#assign isTrue=false>
</#if>

<label>${item.label?html}:</label>

<#if form.mode == "view">
<span class="field"><#if isTrue>Yes<#else>No</#if></span>
<#else>
<input id="${item.id}" type="checkbox" name="${item.name}" <#if item.description?exists>title="${item.description}"</#if>
       <#if isTrue> value="true" checked="checked"</#if> <#if item.protectedField>disabled="true"</#if> />
</#if>