<import resource="/include/support.js">

var path = args["path"];
if(path == null || path == "")
{
	path = "/";
}

path = path.replace(/ /g, "%20");

// json
var json = new Array();

// remote call
var connector = remote.connect("alfresco");
var responseString = connector.call("/webframework/content/metadata?path="+path);
if(responseString != null)
{
	var response = eval('(' + responseString.toString() + ')');
	
	//
	// walk the json tree and convert to the tree structure that the UI expects
	//	
	for(var i = 0; i < response.children.length; i++)
	{
		var node = response.children[i];

		json[i] = { };
		json[i]["text"] = node["name"];
		json[i]["draggable"] = true;
		json[i]["leaf"] = true;
		json[i]["alfType"] = "dmFile";
		if(node.isContainer)
		{
			json[i]["alfType"] = "dmSpace";		
			json[i]["leaf"] = false;
		}
		else
		{
			json[i]["mimetype"] = node["mimetype"];
		}

		json[i]["url"] = node.url;
		json[i]["nodeId"] = node.id;
		json[i]["nodeRef"] = node.nodeRef;

		// copy in some alfresco metadata
		json[i]["cmType"] = node.type;
		
		// path
		json[i]["path"] = node.displayPath + "/" + node.name;
	}
}

var outputString = json.toJSONString();
model.json = outputString;
