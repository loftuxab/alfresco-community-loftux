<#if error?exists>
   <div class="error">${error}</div>
<#elseif form?exists>

   <#assign formId=args.htmlid + "-form">
   <#assign formVar=formId?replace("-", "_")>
   
   <#-- Do we really need the client side JS component, can we just manually include the required files? --> 
   
   <script type="text/javascript">//<![CDATA[
      new Alfresco.FormUI("${formId}");
   //]]></script>
   
   <div id="${formId}-container" class="form-container">
      
      <form id="${formId}" method="POST" action="${url.context}/proxy/alfresco${form.submissionUrl}" accept-charset="utf-8"
            <#if page.url.args.submitMode == "multipart">enctype="multipart/form-data"</#if> >
         <#list form.items as item>
            <#if item.control.template?exists>
               <#assign field=item>
               <#include "${field.control.template}" />
            </#if>
         </#list>
         <hr/>
         <#if form.mode != "view"><input id="${formId}-submit" type="submit" value="Submit" /></#if>
      </form>
      
   </div>
   
   <#if form.mode != "view">      
      <script type="text/javascript">//<![CDATA[
         function onJsonPostSuccess(response)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: response.serverResponse.responseText
            });
         }
         
         function onJsonPostFailure(response)
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: "ERROR: Failed to submit JSON data, see logs for details."
            });
         }
         
         var ${formVar} = new Alfresco.forms.Form("${formId}");
         ${formVar}.setShowSubmitStateDynamically(true, false);
         
         <#if page.url.args.submitMode == "json">
         ${formVar}.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: onJsonPostSuccess            
            },
            failureCallback:
            {
               fn: onJsonPostFailure
            }
         });
         ${formVar}.setSubmitAsJSON(true);
         </#if>
         
         <#list form.constraints as constraint>
         ${formVar}.addValidation("${args.htmlid}_${constraint.fieldId}", ${constraint.validationHandler}, ${constraint.params}, "${constraint.event}");
         </#list>
         
         ${formVar}.init();
      //]]></script>
   </#if>
</#if>