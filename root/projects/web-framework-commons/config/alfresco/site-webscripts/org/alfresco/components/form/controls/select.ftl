<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<label for="${args.htmlid}_${field.id}">${field.label?html}:<#if field.mandatory><span class="mandatory-indicator">*</span></#if></label>
<select id="${args.htmlid}_${field.id}" name="${field.name}" 
       <#if field.description?exists>title="${field.description}"</#if>
       <#if field.control.params.size?exists>size="${field.control.params.size}"</#if> 
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