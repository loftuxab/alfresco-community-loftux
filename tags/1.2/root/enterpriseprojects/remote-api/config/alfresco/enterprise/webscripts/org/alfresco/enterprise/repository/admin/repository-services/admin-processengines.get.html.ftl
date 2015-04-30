<#include "../admin-template.ftl" />

<@page title=msg("processengines.title")>

   <div class="column-full">
      <p class="intro">${msg("processengines.intro-text")?html}</p>
      <@section label=msg("processengines.activiti-workflow-engine") />
      <p class="info">${msg("processengines.activiti-workflow-engine.description")?html}</p>
   </div>

   <div class="column-left">
      <@attrcheckbox attribute=attributes["ActivitiEngineEnabled"] label=msg("processengines.activiti-workflow-engine.ActivitiEngineEnabled") description=msg("processengines.activiti-workflow-engine.ActivitiEngineEnabled.description") />
      <@attrfield attribute=attributes["NumberOfActivitiWorkflowInstances"] label=msg("processengines.activiti-workflow-engine.NumberOfActivitiWorkflowInstances") description=msg("processengines.activiti-workflow-engine.NumberOfActivitiWorkflowInstances.description") />
      <@attrfield attribute=attributes["NumberOfActivitiWorkflowDefinitionsDeployed"] label=msg("processengines.activiti-workflow-engine.NumberOfActivitiWorkflowDefinitionsDeployed") description=msg("processengines.activiti-workflow-engine.NumberOfActivitiWorkflowDefinitionsDeployed.description") />
   </div>
   <div class="column-right">
      <@attrcheckbox attribute=attributes["ActivitiWorkflowDefinitionsVisible"] label=msg("processengines.activiti-workflow-engine.ActivitiWorkflowDefinitionsVisible") description=msg("processengines.activiti-workflow-engine.ActivitiWorkflowDefinitionsVisible.description") />
      <@attrfield attribute=attributes["NumberOfActivitiTaskInstances"] label=msg("processengines.activiti-workflow-engine.NumberOfActivitiTaskInstances") description=msg("processengines.activiti-workflow-engine.NumberOfActivitiTaskInstances.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("processengines.jbpm-workflow-engine") />
      <p class="info">${msg("processengines.jbpm-workflow-engine.description")?html}</p>
   </div>
   <div class="column-left">
      <@attrcheckbox attribute=attributes["JBPMEngineEnabled"] label=msg("processengines.jbpm-workflow-engine.JBPMEngineEnabled") description=msg("processengines.jbpm-workflow-engine.JBPMEngineEnabled.description") />
      <#-- <@attrfield attribute=attributes["NumberOfJBPMWorkflowInstances"] label=msg("processengines.jbpm-workflow-engine.NumberOfJBPMWorkflowInstances") description=msg("processengines.jbpm-workflow-engine.NumberOfJBPMWorkflowInstances.description") /> -->
      <@attrfield attribute=attributes["NumberOfJBPMWorkflowDefinitionsDeployed"] label=msg("processengines.jbpm-workflow-engine.NumberOfJBPMWorkflowDefinitionsDeployed") description=msg("processengines.jbpm-workflow-engine.NumberOfJBPMWorkflowDefinitionsDeployed.description") />
   </div>
   <div class="column-right">
      <@attrcheckbox attribute=attributes["JBPMWorkflowDefinitionsVisible"] label=msg("processengines.jbpm-workflow-engine.JBPMWorkflowDefinitionsVisible") description=msg("processengines.jbpm-workflow-engine.JBPMWorkflowDefinitionsVisible.description") />
      <@attrfield attribute=attributes["NumberOfJBPMTaskInstances"] label=msg("processengines.jbpm-workflow-engine.NumberOfJBPMTaskInstances") description=msg("processengines.jbpm-workflow-engine.NumberOfJBPMTaskInstances.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("processengines.activiti-tools") />
      <a href="${url.context}/activiti-admin">${msg("processengines.activiti-console")?html}</a>
   </div>
</@page>