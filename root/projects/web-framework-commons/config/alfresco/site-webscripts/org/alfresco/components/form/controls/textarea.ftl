<#if field.control.params.rows?exists><#assign rows=field.control.params.rows><#else><#assign rows=3></#if>
<#if field.control.params.columns?exists><#assign columns=field.control.params.columns><#else><#assign columns=32></#if>

<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<label for="${args.htmlid}_${field.id}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">*</span></#if></label>
<textarea id="${args.htmlid}_${field.id}" name="${field.name}" rows="${rows}" columns="${columns}"
          <#if field.description?exists>title="${field.description}"</#if>
          <#if field.control.params.width?exists>style="width: ${field.control.params.width};"</#if>
          <#if field.disabled>disabled="true"</#if>>${field.value}</textarea>
</#if>