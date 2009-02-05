<#if item.control.params.rows?exists><#assign rows=item.control.params.rows><#else><#assign rows=3></#if>
<#if item.control.params.columns?exists><#assign columns=item.control.params.columns><#else><#assign columns=32></#if>

<label>${item.label?html}:</label>

<#if form.mode == "view">
<span class="field">${item.value?html}</span>
<#else>
<textarea id="${item.id}" name="${item.name}" rows="${rows}" columns="${columns}"
          <#if item.description?exists>title="${item.description}"</#if>
          <#if item.control.params.width?exists>style="width: ${item.control.params.width};"</#if>
          <#if item.protectedField>disabled="true"</#if>>
${item.value}
</textarea>
</#if>