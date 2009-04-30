<#include "common/picker.inc.ftl" />

<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<#assign controlId = args.htmlid + "-" + field.id>
<label for="${controlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

<script type="text/javascript">//<![CDATA[
(function()
{
   <@renderPickerJS "picker" />
   picker.setOptions(
   {
      itemType: "cm:category", /* "${field.dataType}" */
      multiSelectMode: true,
      parentNodeRef: "alfresco://category/root",
      itemFamily: "category"
   });
})();
//]]></script>

<div id="${controlId}" class="object-finder">
   
   <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
   <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
   <input type="hidden" id="${controlId}-current" name="-" value="${field.value}" />
   <div id="${controlId}-currentValue" class="current-values"></div>
   <div class="show-picker">
      <button id="${controlId}-showPicker-button">${msg("button.select")}</button>
   </div>

   <@renderPickerHTML controlId />
   
</div>
</#if>
