
// Get available components of family/type dashlet 
var dashlets = sitedata.findWebScripts("dashlet");
if(!dashlets || dashlets.length === 0)
{
   dashlets = [];
}
model.dashlets = dashlets;

// The following is test data until proper service exists
var columns = [];
columns[0] = [{id: "10", shortName: "My Profile 1", description: "Description..."}, {id: "11", shortName: "My Sites 1", description: "Description..."}, {id: "12", shortName: "My Calendar 1", description: "Description..."}];
columns[1] = [{id: "13", shortName: "My Profile 2", description: "Description..."}, {id: "14", shortName: "My Sites 2", description: "Description..."}, {id: "15", shortName: "My Calendar 2", description: "Description..."}];
columns[2] = [{id: "16", shortName: "My Profile 3", description: "Description..."}, {id: "17", shortName: "My Sites 3", description: "Description..."}, {id: "18", shortName: "My Calendar 3", description: "Description..."}];
columns[3] = [{id: "17", shortName: "My Profile 4", description: "Description..."}, {id: "18", shortName: "My Sites 4", description: "Description..."}, {id: "19", shortName: "My Calendar 4", description: "Description..."}];
model.columns = columns;
