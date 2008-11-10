<import resource="/include/support.js">

// arguments
var regionScopeId = args["regionScopeId"];
var regionSourceId = args["regionSourceId"];

// set up the json
var json = { };
json["regionScopeId"] = regionScopeId;
json["regionSourceId"] = regionSourceId;
json["regions"] = { };

// load the binding object
var modelObject = null;
if(regionScopeId == "page")
{
	modelObject = sitedata.getPage(regionSourceId);
}
if(regionScopeId == "template")
{
	modelObject = sitedata.getTemplate(regionSourceId);
}
if(modelObject != null)
{
	json["object"] = { };
	json["object"]["id"] = modelObject.id;
	json["object"]["objectTypeId"] = modelObject.getModelObject().getTypeId();
	for(var key in modelObject.properties)
	{
		var value = modelObject.properties[key];
		if(value != null)
		{		
			json["object"][key] = value;
		}
	}
}

// is there a component bound here?
var component = sitedata.getComponent(regionScopeId, regionId, regionSourceId)
if(component != null)
{
	json["component"] = { };
	json["component"]["id"] = component.id;
	json["component"]["objectTypeId"] = component.getModelObject().getTypeId();
	for(var key in component.properties)
	{
		var value = component.properties[key];
		if(value != null)
		{		
			json["component"][key] = value;
		}
	}

}

// format the json	
var outputString = json.toJSONString();
var callback = args["callback"];
if(callback != null)
	outputString = callback + "(" + outputString + ");";
model.json = outputString;
