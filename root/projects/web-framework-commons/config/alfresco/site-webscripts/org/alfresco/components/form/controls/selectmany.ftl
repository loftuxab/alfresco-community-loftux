<#include "/org/alfresco/components/form/controls/common/utils.inc.ftl" />
<#if field.control.params.size?exists><#assign size=field.control.params.size><#else><#assign size=5></#if>

<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && !(field.value?is_number) && field.value?string == "">
            <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <span class="viewmode-value">${field.value?html}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}-entry">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}" type="hidden" name="${field.name}" value="${field.value?string}" />
      <#if field.control.params.options?exists && field.control.params.options != "">
         <select id="${fieldHtmlId}-entry" name="-" multiple="multiple" size="${size}" tabindex="0"
               onchange="javascript:Alfresco.util.updateMultiSelectListValue('${fieldHtmlId}-entry', '${fieldHtmlId}', <#if field.mandatory>true<#else>false</#if>);"
               <#if field.description?exists>title="${field.description}"</#if> 
               <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if>
               <#if field.disabled>disabled="true"</#if>>
               <#list field.control.params.options?split(",") as nameValue>
                  <#if nameValue?index_of("|") == -1>
                     <option value="${nameValue?html}"<#if (field.value?string?index_of(nameValue) != -1)> selected="selected"</#if>>${nameValue?html}</option>
                  <#else>
                     <#assign choice=nameValue?split("|")>
                     <option value="${choice[0]?html}"<#if (field.value?string?index_of(choice[0]) != -1)> selected="selected"</#if>>${msgValue(choice[1])?html}</option>
                  </#if>
               </#list>
         </select>
         <@formLib.renderFieldHelp field=field />
      <#else>
         <div id="${fieldHtmlId}" class="missing-options">${msg("form.control.selectone.missing-options")}</div>
      </#if>
   </#if>
</div>