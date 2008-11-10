<import resource="/include/support.js">

var avmStoreId = args["alfStoreId"];

// inputs
var nodeId = args["nodeId"];

// return
var json = new Array();
if(nodeId != null)
{
	var ctr = 0;
	
	/*
	// if the node is the root node
	if("root" == nodeId)
	{
		json[ctr] = { };
		json[ctr]["draggable"] = false;
		json[ctr]["nodeId"] = "component-types";
		json[ctr]["text"] = "Component Types";
		json[ctr]["leaf"] = false;
		ctr++;

		json[ctr] = { };
		json[ctr]["draggable"] = false;
		json[ctr]["nodeId"] = "web-script-components";
		json[ctr]["text"] = "Web Script Components";
		json[ctr]["leaf"] = false;
		ctr++;
	}
	
	if("component-types" == nodeId)
	{
		var componentTypes = sitedata.getComponentTypes();
		for(var i = 0; i < componentTypes.length; i++)
		{
			var componentType = componentTypes[i];
			
			json[ctr] = { };
			json[ctr]["draggable"] = true;
			json[ctr]["nodeId"] = componentType.id;
			json[ctr]["text"] = componentType.title;
			json[ctr]["leaf"] = true;
			json[ctr]["alfType"] = "componentType";
			
			ctr++;
		}
	}

	if("web-script-components" == nodeId)
	{
		var webscripts = sitedata.findWebScripts("component");
		
		for(var i = 0; i < webscripts.length; i++)
		{
			var webscript = webscripts[i];
			
			var shortName = webscript.shortName;
			var description = webscript.description;

			var uris = webscript.getURIs();
			if(uris !== null && uris.length > 0 && webscript.shortName !== null)
			{			
				json[ctr] = { };
				json[ctr]["draggable"] = true;
				json[ctr]["nodeId"] = uris[0];
				json[ctr]["text"] = webscript.getShortName();
				json[ctr]["leaf"] = true;
				json[ctr]["alfType"] = "webscriptComponent";
				
				ctr++;
			}
		}
	}
	*/
	
	if("root" == nodeId)
	{
		var webscripts = sitedata.findWebScripts("component");
		
		for(var i = 0; i < webscripts.length; i++)
		{
			var webscript = webscripts[i];
			
			var shortName = webscript.shortName;
			var description = webscript.description;

			var uris = webscript.getURIs();
			if(uris !== null && uris.length > 0 && webscript.shortName !== null)
			{			
				json[ctr] = { };
				json[ctr]["draggable"] = true;
				json[ctr]["nodeId"] = uris[0];
				json[ctr]["text"] = webscript.getShortName();
				json[ctr]["leaf"] = true;
				json[ctr]["alfType"] = "webscriptComponent";
				
				ctr++;
			}
		}
	}
	
}

var outputString = json.toJSONString();
model.json = outputString;
