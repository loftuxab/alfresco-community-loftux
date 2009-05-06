<#include "common/picker.inc.ftl" />

<#if form.mode == "view">
<div class="viewmode-field">
   <#if field.mandatory && field.value == "">
      <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.incomplete.field")}" /><span>
   </#if>
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<#assign controlId = args.htmlid + "-" + field.id>
<label for="${controlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>

<script type="text/javascript">//<![CDATA[
(function()
{
   <@renderPickerJS field "picker" />
   picker.setOptions(
   {
      itemType: "cm:category",
      multipleSelectMode: true,
      parentNodeRef: "alfresco://category/root",
      itemFamily: "category",
      maintainAddedRemovedItems: false,
      params: "${field.control.params.params!""}"
   });
})();
//]]></script>

<div id="${controlId}" class="object-finder">
   
   <input type="hidden" id="${controlId}-current" name="${field.name}" value="${field.value}" />
   <div id="${controlId}-currentValue" class="current-values"></div>
   <div class="show-picker">
      <button id="${controlId}-showPicker-button">${msg("button.select")}</button>
   </div>

   <@renderPickerHTML controlId />
   
</div>
</#if>
