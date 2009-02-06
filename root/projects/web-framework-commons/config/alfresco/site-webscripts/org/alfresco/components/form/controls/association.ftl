<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<label for="${args.htmlid}_${field.id}">${field.label?html}:</label>
<input id="${args.htmlid}_${field.id}" type="text" value="${field.value}" disabled="true" 
       title="${msg("form.field.not.editable")}" style="width: 300px;" />
</#if>