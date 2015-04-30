<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Process Engines GET method
 */
Admin.initModel(
   "Alfresco:Name=WorkflowInformation",
   ["ActivitiEngineEnabled","NumberOfActivitiWorkflowInstances","NumberOfActivitiWorkflowDefinitionsDeployed",
    "ActivitiWorkflowDefinitionsVisible","NumberOfActivitiTaskInstances","JBPMEngineEnabled",
    "NumberOfJBPMWorkflowInstances","NumberOfJBPMWorkflowDefinitionsDeployed",
    "JBPMWorkflowDefinitionsVisible","NumberOfJBPMTaskInstances"],
   "admin-processengines"
);