<import resource="/org/alfresco/web/site/include/json.js">
<import resource="/org/alfresco/web/site/include/utils.js">

var avmStoreId = args["avmStoreId"];

// inputs
var nodeId = args["nodeId"];

// return
var json = new Array();

if(nodeId != null)
{
	var ctr = 0;
	
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
		json[ctr]["nodeId"] = "components";
		json[ctr]["text"] = "Components";
		json[ctr]["leaf"] = false;
		ctr++;
	}
	
	// if the node is "component instances"
	if("components" == nodeId)
	{
		var components = site.getComponents();
		for(var i = 0; i < components.length; i++)
		{
			var component = components[i];
			
			json[ctr] = { };
			json[ctr]["draggable"] = true;
			json[ctr]["nodeId"] = component.getProperty("id");
			json[ctr]["text"] = component.getProperty("name");
			json[ctr]["leaf"] = false;
			json[ctr]["iconCls"] = "tree-icon-componenttree-component";
			json[i]["alfType"] = "component";
			ctr++;
		}
	}

	if("component-types" == nodeId)
	{
		var componentTypes = site.getComponentTypes();
		for(var i = 0; i < componentTypes.length; i++)
		{
			var componentType = componentTypes[i];
			
			json[ctr] = { };
			json[ctr]["draggable"] = true;
			json[ctr]["nodeId"] = componentType.getProperty("id");
			json[ctr]["text"] = componentType.getProperty("name");
			json[ctr]["leaf"] = true;
			json[ctr]["iconCls"] = "tree-icon-componenttree-componenttype";
			json[i]["alfType"] = "componentType";
			ctr++;
		}
	}

	// if it is a component instance
	if(nodeId.substring(0,3) == "cm-")
	{
	}

	// if it is a component type
	if(nodeId.substring(0,3) == "ct-")
	{
	}
}

var outputString = json.toJSONString();
model.json = outputString;
