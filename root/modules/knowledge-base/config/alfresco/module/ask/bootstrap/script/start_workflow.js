<!-- Start workflow -->

  if (logger.isLoggingEnabled()) {
		logger.log("Person: " + args.workflowAssignee);
	}


var personarg = args.workflowAssignee;
	
var workflowAssignee = people.getPerson(personarg);
//var workflowAssignee = "admin";


var theDoc = search.findNode("workspace://SpacesStore/" + args.nodeid);

var workflow = actions.create("start-workflow");
workflow.parameters.workflowName = "jbpm$wf:articleapproval";
workflow.parameters["bpm:workflowDescription"] = theDoc.name;
workflow.parameters["bpm:assignee"] = workflowAssignee;
var futureDate = new Date();
futureDate.setDate(futureDate.getDate() + 7);
workflow.parameters["bpm:workflowDueDate"] = futureDate; 
workflow.execute(theDoc);

result = args.returnid + "| Workflow Started";
result;
