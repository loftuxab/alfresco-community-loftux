<#if field.control.params.rows?exists><#assign rows=field.control.params.rows><#else><#assign rows=2></#if>
<#if field.control.params.columns?exists><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value">${field.value?html}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" columns="${columns}"
                <#if field.description?exists>title="${field.description}"</#if>
                <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if>
                <#if field.disabled>disabled="true"</#if>>${field.value}</textarea>
   </#if>
</div>