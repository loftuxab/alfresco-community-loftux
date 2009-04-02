<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<label for="${args.htmlid}-${field.id}">${field.label?html}:<#if field.endpointMandatory><span class="mandatory-indicator">*</span></#if></label>
<input id="${args.htmlid}-${field.id}" type="text" value="${field.value}" disabled="true" 
       title="${msg("form.field.not.editable")}" style="width: 300px;" />       
</#if>