<import resource="/org/alfresco/web/site/include/ads-support.js">



// pull some values off of the JSON request

var regionId = wizard.request("regionId");
if(regionId == null)
	regionId = "";

var regionScopeId = wizard.request("regionScopeId");
if(regionScopeId == null)
	regionScopeId = "";

var regionSourceId = wizard.request("regionSourceId");
if(regionSourceId == null)
	regionSourceId = "";




//
// create the component
//
var component = site.newComponent();
component.setProperty("name", componentName);
component.setProperty("description", componentDescription);
component.setProperty("component-type-id", componentTypeId);
save(component);



//
// associate the component to the region
//
var componentId = component.getId();
site.associateComponent(componentId, regionScopeId, regionSourceId, regionId);




// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Successfully added new component!");
wizard.setBrowserReload(true);

