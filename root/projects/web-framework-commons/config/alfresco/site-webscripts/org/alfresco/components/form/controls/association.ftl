<#include "common/picker.inc.ftl" />

<#assign controlId = fieldHtmlId + "-cntrl">
<#assign fieldValue = "">
<#if field.control.params.selectedItemsContextProperty??>
   <#if context.properties[field.control.params.selectedItemsContextProperty]??>
      <#assign fieldValue = context.properties[field.control.params.selectedItemsContextProperty]>
   <#elseif args[field.control.params.selectedItemsContextProperty]??>
      <#assign fieldValue = args[field.control.params.selectedItemsContextProperty]>
   </#if>
<#elseif context.properties[field.name]??>
   <#assign fieldValue = context.properties[field.name]>
<#else>
   <#assign fieldValue = field.value>
</#if>

<script type="text/javascript">//<![CDATA[
(function()
{
   <@renderPickerJS field "picker" />
   picker.setOptions(
   {
   <#if field.control.params.showTargetLink??>
      showLinkToTarget: ${field.control.params.showTargetLink},
      targetLinkTemplate: "${url.context}/page/site/${page.url.templateArgs.site!""}/document-details?nodeRef={nodeRef}",
   </#if>
      <#if fieldValue != "">currentValue: "${fieldValue}",</#if>
      itemType: "${field.endpointType}",
      multipleSelectMode: ${field.endpointMany?string},
      parentNodeRef: "alfresco://company/home",
      itemFamily: "node",
      displayMode: "${field.control.params.displayMode!"items"}"
   });
})();
//]]></script>

<div class="form-field">
   <#if form.mode == "view">
      <div id="${controlId}" class="viewmode-field">
         <#if (field.endpointMandatory!false || field.mandatory!false) && fieldValue == "">
            <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <span id="${controlId}-currentValueDisplay" class="viewmode-value current-values"></span>
      </div>
   <#else>
      <label for="${controlId}">${field.label?html}:<#if field.endpointMandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      
      <div id="${controlId}" class="object-finder">
         
         <div id="${controlId}-currentValueDisplay" class="current-values"></div>
         
         <#if form.mode != "view" && field.disabled == false>
            <input type="hidden" id="${fieldHtmlId}" name="-" value="${fieldValue?html}" />
            <input type="hidden" id="${controlId}-added" name="${field.name}_added" />
            <input type="hidden" id="${controlId}-removed" name="${field.name}_removed" />
            <div class="show-picker">
               <span id="${controlId}-showPicker-button" class="yui-button yui-push-button">
                  <span class="first-child">
                     <button>${msg("button.select")}</button>
                  </span>
               </span>
            </div>
         
            <@renderPickerHTML controlId />
         </#if>
      </div>
   </#if>
</div>
