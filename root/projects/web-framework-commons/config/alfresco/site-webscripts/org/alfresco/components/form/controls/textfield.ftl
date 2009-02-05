<label>${item.label?html}:</label>

<#if form.mode == "view">
<span class="field">${item.value?html}</span>
<#else>
<input id="${item.id}" type="text" name="${item.name}" value="${item.value}" 
       <#if item.description?exists>title="${item.description}"</#if>
       <#if item.control.params.maxLength?exists>maxlength="${item.control.params.maxLength}"</#if>
       <#if item.control.params.width?exists>style="width: ${item.control.params.width};"</#if> 
       <#if item.control.params.size?exists>size="${item.control.params.size}"</#if> 
       <#if item.protectedField>disabled="true"</#if> />
</#if>