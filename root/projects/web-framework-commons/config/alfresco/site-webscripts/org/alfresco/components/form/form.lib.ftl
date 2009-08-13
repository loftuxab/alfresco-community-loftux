<#macro renderFormsRuntime formId>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.FormUI("${formId}", "${args.htmlid}").setOptions(
      {
         mode: "${form.mode}",
         <#if form.mode == "view">
         arguments:
         {
            itemKind: "${form.arguments.itemKind!""}",
            itemId: "${form.arguments.itemId!""}",
            formId: "${form.arguments.formId!""}"
         }
         <#else>
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
         </#if>
      }).setMessages(
         ${messages}
      );
   //]]></script>
</#macro> 
         
<#macro renderFormButtons formId>         
   <div id="${formId}-buttons" class="form-buttons">
      <input id="${formId}-submit" type="submit" value="${msg("form.button.submit.label")}" />
      <#if form.showResetButton?exists && form.showResetButton>
         &nbsp;<input id="${formId}-reset" type="reset" value="${msg("form.button.reset.label")}" />
      </#if>
      <#if form.showCancelButton?exists && form.showCancelButton>
         &nbsp;<input id="${formId}-cancel" type="button" value="${msg("form.button.cancel.label")}" />
      </#if>
   </div>
</#macro>   

<#macro renderField field>
   <#if field.control.template?exists>
      <#assign fieldHtmlId=args.htmlid + "_" + field.id >
      <#include "${field.control.template}" />
   </#if>
</#macro>

<#macro renderSet set>
   <#if set.appearance?exists>
      <#if set.appearance == "fieldset">
         <fieldset><legend>${set.label}</legend>
      <#elseif set.appearance == "panel">
         <div class="form-panel">
            <div class="form-panel-heading">${set.label}</div>
            <div class="form-panel-body">
      </#if>
   </#if>
   
   <#list set.children as item>
      <#if item.kind == "set">
         <@renderSet set=item />
      <#else>
         <@renderField field=form.fields[item.id] />
      </#if>
   </#list>
   
   <#if set.appearance?exists>
      <#if set.appearance == "fieldset">
         </fieldset>
      <#elseif set.appearance == "panel">
            </div>
         </div>
      </#if>
   </#if>
</#macro>

