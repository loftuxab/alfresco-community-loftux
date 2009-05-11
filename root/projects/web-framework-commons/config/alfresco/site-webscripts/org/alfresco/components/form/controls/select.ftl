<#if form.mode == "view">
<div class="viewmode-field">
   <#if field.mandatory && field.value == "">
      <span class="incomplete-warning"><img src="${url.context}/components/form/images/warning-16.png" title="${msg("form.incomplete.field")}" /><span>
   </#if>
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<label for="${fieldHtmlId}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">${msg("form.required.fields.marker")}</span></#if></label>
<select id="${fieldHtmlId}" name="${field.name}" 
       <#if field.description?exists>title="${field.description}"</#if>
       <#if field.control.params.size?exists>size="${field.control.params.size}"</#if> 
       <#if field.control.params.styleClass?exists>class="${field.control.params.styleClass}"</#if>
       <#if field.disabled>disabled="true"</#if>>
   <#if field.control.params.options?exists>  
      <#list field.control.params.options?split(",") as nameValue>
         <#if nameValue?index_of("|") == -1>
            <option value="${nameValue}"<#if nameValue == field.value> selected="selected"</#if>>${nameValue}</option>
         <#else>
            <#assign choice=nameValue?split("|")>
            <option value="${choice[0]}"<#if choice[0] == field.value> selected="selected"</#if>>${choice[1]}</option>
         </#if>
      </#list>
   </#if>
</select>
</#if>