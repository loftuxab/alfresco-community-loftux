<#if formUI == "true">
   <@formLib.renderFormsRuntime formId=formId />
</#if>
    
<div id="${formId}-container" class="form-container">
 
   <#if form.showCaption?exists && form.showCaption>
      <div id="${formId}-caption" class="caption"><span class="mandatory-indicator">*</span>${msg("form.required.fields")}</div>
   </#if>
    
   <#if form.mode != "view">
      <form id="${formId}" method="${form.method}" accept-charset="utf-8" enctype="${form.enctype}" action="${form.submissionUrl}">
   </#if>
   
   <div id="${formId}-fields" class="form-fields">
      <div class="form-panel">
         <div class="form-panel-heading">${msg("label.set.idStatus")}</div>
         <div class="form-panel-body">
            <@formLib.renderField field=form.fields["prop_cm_name"] />
            <@formLib.renderField field=form.fields["prop_rma_identifier"] />
            <@formLib.renderField field=form.fields["prop_cm_title"] />
            <@formLib.renderField field=form.fields["prop_cm_description"] />
            <#if form.fields["prop_cm_owner"]?? && form.mode == "view">
               <@formLib.renderField field=form.fields["prop_cm_owner"] />
            </#if>
            <#if form.fields["prop_cm_author"]??>
               <@formLib.renderField field=form.fields["prop_cm_author"] />
            </#if>
         </div>
      </div>
      <div class="form-panel">
         <div class="form-panel-heading">${msg("label.set.general")}</div>
         <div class="form-panel-body">
            <#if form.mode == "view">
                <@formLib.renderField field=form.fields["prop_cm_creator"] />
                <@formLib.renderField field=form.fields["prop_cm_created"] />
                <@formLib.renderField field=form.fields["prop_cm_modifier"] />
                <@formLib.renderField field=form.fields["prop_cm_modified"] />
                <@formLib.renderField field=form.fields["prop_size"] />
            </#if>
            <@formLib.renderField field=form.fields["prop_mimetype"] />
         </div>
      </div>
      <div class="form-panel">
         <div class="form-panel-heading">${msg("label.set.record")}</div>
         <div class="form-panel-body">
            <@formLib.renderField field=form.fields["prop_rma_originator"] />
            <@formLib.renderField field=form.fields["prop_rma_originatingOrganization"] />
            <#if form.mode == "view">
                <@formLib.renderField field=form.fields["prop_rma_dateFiled"] />
            </#if>
            <@formLib.renderField field=form.fields["prop_rma_publicationDate"] />
            <@formLib.renderField field=form.fields["prop_rma_location"] />
            <@formLib.renderField field=form.fields["prop_rma_mediaType"] />
            <@formLib.renderField field=form.fields["prop_rma_format"] />
         </div>
      </div>
      <!-- Scanned Record Fields -->
      <!-- PDF Record Fields -->
      <!-- Photo Record Fields -->
      <!-- Web Record Fields -->
      <div class="form-panel">
         <div class="form-panel-heading">${msg("label.set.correspondence")}</div>
         <div class="form-panel-body">
            <@formLib.renderField field=form.fields["prop_rma_dateReceived"] />
            <@formLib.renderField field=form.fields["prop_rma_address"] />
            <@formLib.renderField field=form.fields["prop_rma_otherAddress"] />
         </div>
      </div>
      <div class="form-panel">
         <div class="form-panel-heading">${msg("label.set.security")}</div>
         <div class="form-panel-body">
            <@formLib.renderField field=form.fields["prop_rma_supplementalMarkingList"] />
         </div>
      </div>
      
      <#if form.fields["prop_rma_vitalRecordIndicator"]?? || form.fields["prop_rma_reviewPeriod"]?? ||
           (form.fields["prop_rma_reviewAsOf"]?? && form.mode == "view")>
         <div class="form-panel">
            <div class="form-panel-heading">${msg("label.set.vitalRecord")}</div>
            <div class="form-panel-body">
               <#if form.fields["prop_rma_vitalRecordIndicator"]??>
                  <@formLib.renderField field=form.fields["prop_rma_vitalRecordIndicator"] />
               </#if>
               <#if form.fields["prop_rma_reviewPeriod"]??>
                  <@formLib.renderField field=form.fields["prop_rma_reviewPeriod"] />
               </#if>
               <#if form.fields["prop_rma_reviewAsOf"]?? && form.mode == "view">
                  <@formLib.renderField field=form.fields["prop_rma_reviewAsOf"] />
               </#if>
            </div>
         </div>
      </#if>
      
      <#if form.fields["prop_rma_cutOffDate"]?? && form.mode == "view">
         <div class="form-panel">
            <div class="form-panel-heading">${msg("label.set.disposition")}</div>
            <div class="form-panel-body">
               <@formLib.renderField field=form.fields["prop_rma_cutOffDate"] />
            </div>
         </div>
      </#if>
      
      <#list form.structure as item>
         <#if item.kind == "set" && item.id == "rm-custom">
            <@formLib.renderSet set=item />
            <#break>
         </#if>
      </#list>
   </div>
    
   <#if form.mode != "view">
      <@formLib.renderFormButtons formId=formId />
      </form>
   </#if>
   
</div>