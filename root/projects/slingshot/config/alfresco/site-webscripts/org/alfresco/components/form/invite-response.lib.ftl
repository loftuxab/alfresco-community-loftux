<#macro renderInviteResponse outcome formUI formId>
   <#if form.mode =="edit" && formUI == "true">
      <@formLib.renderFormsRuntime formId=formId />
   </#if>
      
   <div class="form-container">
      
      <#if form.mode =="edit">
         <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
      </#if>
      
      <div id="${formId}-fields" class="form-fields">
         <div class="yui-gc">
            <div class="yui-u first">
               <div class="invite-task-title">
                  <img src="${url.context}/components/images/site-24.png" />
                  <span>${msg("workflow.task.invite." + outcome, form.data["prop_inwf_inviteeFirstName"], form.data["prop_inwf_inviteeLastName"], form.data["prop_inwf_resourceTitle"])?html}</span>
               </div>
            </div>
            <div class="yui-u">
               <div class="invite-task-priority">
                  <img src="${url.context}/components/images/priority-${form.data["prop_bpm_priority"]?c}-16.png" />
               </div>
            </div>
         </div>
         
         <#if form.mode =="edit">
            <div class="invite-task-subtitle">
               <@formLib.renderField field=form.fields["prop_transitions"] />
            </div>
         </#if>
      </div>
      
      <#if form.mode =="edit">
         <@formLib.renderFormButtons formId=formId />
         </form>
      </#if>
   </div>
</#macro>