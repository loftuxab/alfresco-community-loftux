<import resource="/include/support.js">

// arguments
var regionScopeId = args["regionScopeId"];
var regionSourceId = args["regionSourceId"];
var regionId = args["regionId"];

// or we can take in a regionElementId
var regionElementId = args["regionElementId"];
if(regionElementId != null)
{
	var x = regionElementId.indexOf("_x002e_");
	if(x > -1)
	{
		regionScopeId = id.substring(0, x);		
		
		if("global" == regionScopeId)
		{
			regionSourceId = regionScopeId;
			regionId = id.substring(x+7, id.length());
		}
		else
		{
			var cdr = id.substring(x+7, id.length());
			int y = cdr.indexOf("_x002e_");
			if(y > -1)
			{
				regionId = cdr.substring(0, y);
				regionSourceId = cdr.substring(y+7, cdr.length());
			}
		}
	}
}

// set up the json
var json = { };
json["regionScopeId"] = regionScopeId;
json["regionSourceId"] = regionSourceId;
json["regionId"] = regionId;

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
