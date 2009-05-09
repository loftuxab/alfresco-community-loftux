<#include "common/picker.inc.ftl" />

<#assign controlId = args.htmlid + "-" + field.id + "-cntrl">

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

<#if form.mode == "view">
<div id="${controlId}" class="viewmode-field">
   <#if field.endpointMandatory && field.value == "">
      <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.incomplete.field")}" /><span>
   </#if>
   <span class="viewmode-label">${field.label?html}:</span>
   <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
</div>
<#else>

<label for="${controlId}">${field.label?html}:<#if field.endpointMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

<div id="${controlId}" class="object-finder">
   
   <div id="${controlId}-currentValueDisplay" class="current-values"></div>
   
   <#if form.mode != "view" && field.disabled == false>
   <input type="hidden" id="${args.htmlid}_${field.id}" name="-" value="${field.value}" />
   <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
   <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
   <div class="show-picker">
      <button id="${controlId}-showPicker-button">${msg("button.select")}</button>
   </div>

   <@renderPickerHTML controlId />
   </#if>
</div>
</#if>