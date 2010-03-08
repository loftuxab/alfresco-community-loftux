<#include "common/editorparams.inc.ftl" />

<#if field.control.params.rows??><#assign rows=field.control.params.rows><#else><#assign rows=8></#if>
<#if field.control.params.columns??><#assign columns=field.control.params.columns><#else><#assign columns=60></#if>

<#if form.capabilities?? && form.capabilities.javascript?? && form.capabilities.javascript == false><#assign jsDisabled=true><#else><#assign jsDisabled=false></#if>

<#-- NOTE: content properties are not shown at all in view mode -->

<#if form.mode != "view">
<div class="form-field" id="${fieldHtmlId}-field">
   <#if jsDisabled == false>
   <script type="text/javascript">//<![CDATA[
   (function()
   {
      new Alfresco.ContentControl("${fieldHtmlId}").setOptions(
      {
         <#if field.disabled>disabled: true,</#if>
         currentValue: "${field.value}",
         mandatory: ${field.mandatory?string},
         formMode: "${form.mode}",
         nodeRef: "${context.properties.nodeRef!""}",
         mimeType: "${context.properties.mimeType!""}",
         <#if field.control.params.plainMimeTypes??>plainMimeTypes: "${field.control.params.plainMimeTypes}",</#if>
         <#if field.control.params.richMimeTypes??>richMimeTypes: "${field.control.params.richMimeTypes}",</#if>
         <#if field.control.params.imageMimeTypes??>imageMimeTypes: "${field.control.params.imageMimeTypes}",</#if>
         <#if field.control.params.forceEditor??>forceEditor: ${field.control.params.forceEditor},</#if>
         <@editorParameters field />
      }).setMessages(
         ${messages}
      );
   })();
   //]]></script>
   </#if>
   
   <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
   <textarea id="${fieldHtmlId}" name="${field.name}" rows="${rows}" columns="${columns}" tabindex="0"
             <#if field.description??>title="${field.description}"</#if>
             <#if field.control.params.styleClass??>class="${field.control.params.styleClass}"</#if>
             <#if field.disabled>disabled="true"</#if>><#if jsDisabled>${field.content?html}</#if></textarea>
</div>
</#if>