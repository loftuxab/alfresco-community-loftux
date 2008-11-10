<import resource="/include/support.js">

var path = args["path"];
if(path == null)
{
	path = "";
}

var storeId = args["alfStoreId"];
var webappId = "ROOT";

// call over to Alfresco for this store id and retrieve a list of content
var connector = remote.connect("alfresco");
var feed = connector.get("/webframework/avm/metadata/"+storeId+"/"+webappId+"/"+path);
var response = eval('(' + feed.toString() + ')');

var json = new Array();

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
	json[i]["alfType"] = "file";
	if(node.isContainer)
	{
		json[i]["alfType"] = "directory";		
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

var outputString = json.toJSONString();
model.json = outputString;
