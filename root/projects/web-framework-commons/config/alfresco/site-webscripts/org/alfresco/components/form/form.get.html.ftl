<#if error?exists>
   <div class="error">${error}</div>
<#elseif form?exists>

   <#assign formId=args.htmlid + "-form">
   
   <#if form.mode != "view">
      <script type="text/javascript">//<![CDATA[
         new Alfresco.FormUI("${formId}").setOptions(
         {
            mode: "${form.mode}",
            enctype: "${form.enctype}",
            fieldConstraints: 
            [
               <#list form.constraints as constraint>
               {
                  fieldId : "${args.htmlid}_${constraint.fieldId}", 
                  handler : ${constraint.validationHandler}, 
                  params : ${constraint.params}, 
                  event : "${constraint.event}",
                  message : <#if constraint.message?exists>"${constraint.message}"<#else>null</#if>
               }
               <#if constraint_has_next>,</#if>
               </#list>
            ]
         }).setMessages(
            ${messages}
         );
      //]]></script>
   </#if>
   
   <div id="${formId}-container" class="form-container">
      
      <#if form.showCaption?exists && form.showCaption>
         <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
      </#if>
         
      <#if form.mode != "view">
         <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
      </#if>
      
      <div id="${formId}-fields" class="form-fields">
         <#list form.items as item>
            <#if item.kind == "set">
               <@renderSet set=item />
            <#else>
               <@renderField field=item />
            </#if>
         </#list>
      </div>
         
      <#if form.mode != "view">
         <div id="${formId}-buttons" class="form-buttons">
            <input id="${formId}-submit" type="submit" value="${msg("form.button.submit.label")}" />
            <#if form.showResetButton?exists && form.showResetButton>
               &nbsp;<input id="${formId}-reset" type="reset" value="${msg("form.button.reset.label")}" />
            </#if>
            <#if form.showCancelButton?exists && form.showCancelButton>
               &nbsp;<input id="${formId}-cancel" type="button" value="${msg("form.button.cancel.label")}" />
            </#if>
         </div>
         </form>
      </#if>

   </div>
</#if>

<#macro renderField field>
   <#if field.control.template?exists>
      <#include "${field.control.template}" />
   </#if>
</#macro>

<#macro renderSet set>
   <#if set.appearance?exists>
      <fieldset><legend>${set.label}</legend>
   </#if>
   
   <#list set.children as item>
      <#if item.kind == "set">
         <@renderSet set=item />
      <#else>
         <@renderField field=item />
      </#if>
   </#list>
   
   <#if set.appearance?exists>
      </fieldset>
   </#if>
</#macro>

