<#-- Renders a hidden form field for edit and create modes only -->
<#if field.control.params.contextProperty?? && context.properties[field.control.params.contextProperty]??>
   <#assign fieldValue = context.properties[field.control.params.contextProperty]>
<#elseif context.properties[field.name]??>
   <#assign fieldValue = context.properties[field.name]>
<#else>
   <#assign fieldValue = field.value>
</#if>

<#if form.mode == "edit" || form.mode == "create">
   <input type="hidden" name="${field.name}" 
          <#if field.value?is_number>value="${fieldValue?c}"<#else>value="${fieldValue?html}"</#if> />
</#if>