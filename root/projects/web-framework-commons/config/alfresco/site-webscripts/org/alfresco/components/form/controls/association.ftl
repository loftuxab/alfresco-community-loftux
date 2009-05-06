<#include "common/picker.inc.ftl" />

<#if form.mode == "view">
<div class="viewmode-field">
   <#if field.endpointMandatory && field.value == "">
      <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.incomplete.field")}" /><span>
   </#if>
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<#assign controlId = args.htmlid + "-" + field.id>
<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

<script type="text/javascript">//<![CDATA[
(function()
{
   <@renderPickerJS field "picker" />
   picker.setOptions(
   {
      itemType: "${field.endpointType}",
      multipleSelectMode: ${field.endpointMany?string},
      parentNodeRef: "alfresco://company/home",
      itemFamily: "node"
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