// todo replace by call to future REST API and filter out by using workflow list in share-config.xml
var workflowDefinitions = [
   { title: "Review & Approve", startTaskId: "jbpm$wf:review" },
   { title: "Adhoc Task", startTaskId: "jbpm$wf:adhoc" }
];
model.workflowDefinitions = workflowDefinitions;
