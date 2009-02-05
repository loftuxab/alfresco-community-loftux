<label>${item.label?html}:</label>

<#if form.mode == "view">
<span class="field">${item.value?html}</span>
<#else>
<input type="text" value="${item.value}" disabled="true" title="${msg("form.field.not.editable")}" style="width: 300px;" />
</#if>