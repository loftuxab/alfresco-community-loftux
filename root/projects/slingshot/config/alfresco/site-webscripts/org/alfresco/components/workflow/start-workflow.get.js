// todo replace by call to future REST API and filter out by using workflow list in share-config.xml
var workflowDefinitions = [
   { title: "Review & Approve", startTaskId: "wf:submitReviewTask" },
   { title: "Adhoc Task", startTaskId: "wf:submitAdhocTask" }
];
model.workflowDefinitions = workflowDefinitions;
