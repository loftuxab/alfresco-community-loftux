<#if field.value?exists><#assign isTrue=field.value><#else><#assign isTrue=false></#if>

<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value"><#if isTrue>Yes<#else>No</#if></span>
</div>
<#else>
<label for="${args.htmlid}_${field.id}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
<input id="${args.htmlid}_${field.id}" type="checkbox" name="${field.name}" <#if field.description?exists>title="${field.description}"</#if>
       <#if isTrue> value="true" checked="checked"</#if> <#if field.disabled>disabled="true"</#if> 
       <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if> />
</#if>