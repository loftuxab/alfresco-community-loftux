<import resource="/include/support.js">

var endpoint = url.templateArgs["endpoint"];

var storeType = url.templateArgs["storeType"];
var storeId = url.templateArgs["storeId"];
var nodeId = url.templateArgs["nodeId"];

var nodeRef = storeType + "://" + storeId + "/" + nodeId;

// load the content from the alfresco spaces endpoint
var connector = remote.connect(endpoint);
var responseString = connector.call("/webframework/content/metadata?id="+nodeRef);
if(responseString != null)
{
	var response = eval('(' + responseString.toString() + ')');
	
	// convert the response to the structure we expect
	var json = new Array();	
	for(var i = 0; i < response.children.length; i++)
	{
		var node = response.children[i];

		json[i] = { };
		json[i]["title"] = node["name"];
		json[i]["description"] = node["description"];
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
		json[i]["type"] = node.type;
		
		// path
		json[i]["path"] = node.displayPath + "/" + node.name;
	}
}

var outputString = json.toJSONString();
model.json = outputString;
