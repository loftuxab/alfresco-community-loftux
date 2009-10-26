<div class="form-field">
   <#if form.mode == "view">
      <div class="viewmode-field">
         <#if field.mandatory && !(field.value?is_number) && field.value == "">
            <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.field.incomplete")}" /><span>
         </#if>
         <span class="viewmode-label">${field.label?html}:</span>
         <#if field.control.params.activateLinks?? && field.control.params.activateLinks == "true">
            <#assign fieldValue=field.value?html?replace("((http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?\\^=%&:\\/~\\+#]*[\\w\\-\\@?\\^=%&\\/~\\+#])?)", "<a href=\"$1\" target=\"_blank\">$1</a>", "r")>
         <#else>
            <#assign fieldValue=field.value?html>
         </#if>
         <span class="viewmode-value">${fieldValue}</span>
      </div>
   <#else>
      <label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
      <input id="${fieldHtmlId}" type="text" name="${field.name}" 
             <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if>
             <#if field.value?is_number>value="${field.value?c}"<#else>value="${field.value?html}"</#if>
             <#if field.description?exists>title="${field.description}"</#if>
             <#if field.control.params.maxLength?exists>maxlength="${field.control.params.maxLength}"</#if> 
             <#if field.control.params.size?exists>size="${field.control.params.size}"</#if> 
             <#if field.disabled>disabled="true"</#if> />
   </#if>
</div>