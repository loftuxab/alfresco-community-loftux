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
		json[ctr]["nodeId"] = "web-script-components";
		json[ctr]["text"] = "Web Components";
		json[ctr]["leaf"] = false;
		ctr++;
	}
	
	if("component-types" == nodeId)
	{
		var componentTypes = site.getComponentTypes();
		for(var i = 0; i < componentTypes.length; i++)
		{
			var componentType = componentTypes[i];
			
			json[ctr] = { };
			json[ctr]["draggable"] = true;
			json[ctr]["nodeId"] = componentType.getId();
			json[ctr]["text"] = componentType.getTitle();
			json[ctr]["leaf"] = true;
			json[ctr]["iconCls"] = "tree-icon-componenttree-componenttype";
			json[ctr]["alfType"] = "componentType";
			ctr++;
		}
	}

	if("web-script-components" == nodeId)
	{
		var dirs = site.getModelFileSystem().getFiles("/site-webscripts");
		if(dirs != null)
		{
			for(var x = 0; x < dirs.length; x++)
			{
				if(dirs[x].isDirectory())
				{
					var files = site.getModelFileSystem().getFiles("/site-webscripts/" + dirs[x].getName());
					if(files != null)
					{
						for(var i = 0; i < files.length; i++)
						{
							var file = files[i];
							var fileName = file.getName();
							if(fileName.endsWith("desc.xml"))
							{
								var filePath = file.getPath();

								// get the contents of the file (using e4x)
								var xmlString = file.readContents();
								var xml = new XML(xmlString);

								var shortName = xml.shortname.toString();
								var uri = xml.url.toString();

								json[ctr] = { };
								json[ctr]["draggable"] = true;
								json[ctr]["nodeId"] = filePath;
								json[ctr]["text"] = shortName;
								json[ctr]["leaf"] = false;
								json[ctr]["iconCls"] = "tree-icon-componenttree-component";
								json[ctr]["alfType"] = "webscriptComponent";

								json[ctr]["uri"] = uri;
								ctr++;
							}
						}
					}
				}
			}
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
