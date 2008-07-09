<import resource="/org/alfresco/web/site/include/utils.js">
<import resource="/org/alfresco/web/site/include/json.js">
<import resource="/org/alfresco/web/site/include/ads-support.js">

function pushComponentSettings(component)
{
	for(var argName in args)
	{
		if(argName.substring(0,1) == "_")
		{		
			var settingName = argName.substring(1, argName.length);			
			var argValue = args[argName];
			if(argValue != null)
			{
				component.setProperty(settingName, argValue);
			}
		}
	}
}

// fundamental things
var componentTypeId = args["componentType"];
var componentId = args["component"];
var regionId = args["regionId"];
var regionSourceId = args["regionSourceId"];
var regionScopeId = args["regionScopeId"];

var json = { };
if(componentTypeId != null)
	json["componentTypeId"] = componentTypeId;
if(componentId != null)
	json["componentId"] = componentId;
json["regionId"] = regionId;
json["regionSourceId"] = regionSourceId;
json["regionScopeId"] = regionScopeId;

// we have to have values for all of these
var proceed = true;
if(componentTypeId == null && componentId == null)
	proceed = false;
if(regionId == null)
	proceed = false;
if(regionSourceId == null)
	proceed = false;
if(regionScopeId == null)
	proceed = false;

// should we proceed?
if(proceed)
{
	var component = null;
	if(componentId == null && componentTypeId != null)
	{	
		// build the component
		component = sitedata.newComponent(componentTypeId);
	
		// push any arguments for prepopulation	
		pushComponentSettings(component);

		// bind the component
		sitedata.bindComponent(component, regionScopeId, regionId, regionSourceId);

		// assign component id onto json return
		componentId = component.getId();
		json["componentId"] = componentId;
		json["componentTypeId"] = componentTypeId;
	}	
}

// format the json	
var outputString = json.toJSONString();
var callback = args["callback"];
if(callback != null)
	outputString = callback + "(" + outputString + ");";
model.json = outputString;
